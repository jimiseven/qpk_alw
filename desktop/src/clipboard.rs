#[cfg(target_os = "windows")]
pub fn set_clipboard_text(text: &str) -> bool {
    use std::ffi::c_void;
    use std::ptr;

    type HWND = *mut c_void;
    type HANDLE = *mut c_void;
    type HGLOBAL = *mut c_void;
    type BOOL = i32;
    type UINT = u32;

    const CF_UNICODETEXT: UINT = 13;
    const GMEM_MOVEABLE: UINT = 0x0002;

    #[link(name = "user32")]
    unsafe extern "system" {
        fn OpenClipboard(hwnd: HWND) -> BOOL;
        fn CloseClipboard() -> BOOL;
        fn EmptyClipboard() -> BOOL;
        fn SetClipboardData(uFormat: UINT, hMem: HANDLE) -> HANDLE;
    }

    #[link(name = "kernel32")]
    unsafe extern "system" {
        fn GlobalAlloc(uFlags: UINT, dwBytes: usize) -> HGLOBAL;
        fn GlobalLock(hMem: HGLOBAL) -> *mut c_void;
        fn GlobalUnlock(hMem: HGLOBAL) -> BOOL;
    }

    let wide: Vec<u16> = text.encode_utf16().chain(std::iter::once(0)).collect();
    let bytes_len = wide.len() * std::mem::size_of::<u16>();

    unsafe {
        if OpenClipboard(ptr::null_mut()) == 0 {
            return false;
        }
        EmptyClipboard();

        let h_mem = GlobalAlloc(GMEM_MOVEABLE, bytes_len);
        if h_mem.is_null() {
            CloseClipboard();
            return false;
        }

        let locked = GlobalLock(h_mem);
        if locked.is_null() {
            CloseClipboard();
            return false;
        }

        ptr::copy_nonoverlapping(wide.as_ptr() as *const u8, locked as *mut u8, bytes_len);
        GlobalUnlock(h_mem);

        if SetClipboardData(CF_UNICODETEXT, h_mem).is_null() {
            CloseClipboard();
            return false;
        }

        CloseClipboard();
        true
    }
}

#[cfg(not(target_os = "windows"))]
pub fn set_clipboard_text(_text: &str) -> bool {
    false
}

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_set_clipboard_text() {
        let ok = set_clipboard_text("Hola desde Gestor Cuentas Desktop! 🔥");
        assert!(ok);
    }
}

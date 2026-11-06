#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

mod models;
mod storage;
mod backup;
mod clipboard;
mod ui;

use std::borrow::Cow;
use std::sync::{Arc, Mutex};
use tao::{
    dpi::LogicalSize,
    event::{Event, StartCause, WindowEvent},
    event_loop::{ControlFlow, EventLoop},
    window::WindowBuilder,
};
use wry::{
    http::{Request, Response},
    WebViewBuilder,
};

use crate::models::{Cuenta, Venta};
use crate::storage::StorageManager;
use crate::backup::BackupManager;
use crate::ui::INDEX_HTML;

fn json_response(json: String) -> Response<Cow<'static, [u8]>> {
    Response::builder()
        .header("Content-Type", "application/json; charset=utf-8")
        .header("Access-Control-Allow-Origin", "*")
        .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
        .header("Access-Control-Allow-Headers", "*")
        .body(Cow::Owned(json.into_bytes()))
        .unwrap()
}

fn ok_response() -> Response<Cow<'static, [u8]>> {
    json_response("{\"success\":true}".to_string())
}

#[allow(non_snake_case)]
#[repr(C)]
struct OPENFILENAMEW {
    lStructSize: u32,
    hwndOwner: *mut std::ffi::c_void,
    hInstance: *mut std::ffi::c_void,
    lpstrFilter: *const u16,
    lpstrCustomFilter: *mut u16,
    nMaxCustFilter: u32,
    nFilterIndex: u32,
    lpstrFile: *mut u16,
    nMaxFile: u32,
    lpstrFileTitle: *mut u16,
    nMaxFileTitle: u32,
    lpstrInitialDir: *const u16,
    lpstrTitle: *const u16,
    Flags: u32,
    nFileOffset: u16,
    nFileExtension: u16,
    lpstrDefExt: *const u16,
    lCustData: usize,
    lpfnHook: *mut std::ffi::c_void,
    lpTemplateName: *const u16,
    pvReserved: *mut std::ffi::c_void,
    dwReserved: u32,
    FlagsEx: u32,
}

#[link(name = "comdlg32")]
unsafe extern "system" {
    fn GetSaveFileNameW(lpofn: *mut OPENFILENAMEW) -> i32;
}

fn prompt_save_file_path(default_name: &str) -> Option<std::path::PathBuf> {
    use std::ffi::{OsStr, OsString};
    use std::os::windows::ffi::{OsStrExt, OsStringExt};
    use std::path::PathBuf;

    let mut file_buf = [0u16; 1024];
    let def_name_w: Vec<u16> = OsStr::new(default_name).encode_wide().collect();
    for (i, &ch) in def_name_w.iter().take(file_buf.len() - 1).enumerate() {
        file_buf[i] = ch;
    }

    let filter_text = "Archivos de Respaldo JSON (*.json)\0*.json\0Todos los archivos (*.*)\0*.*\0\0";
    let filter_w: Vec<u16> = filter_text.encode_utf16().collect();
    let title_text = "Seleccione la ubicación donde desea guardar la copia de seguridad\0";
    let title_w: Vec<u16> = title_text.encode_utf16().collect();
    let def_ext: Vec<u16> = "json\0".encode_utf16().collect();

    let mut ofn: OPENFILENAMEW = unsafe { std::mem::zeroed() };
    ofn.lStructSize = std::mem::size_of::<OPENFILENAMEW>() as u32;
    ofn.lpstrFilter = filter_w.as_ptr();
    ofn.nFilterIndex = 1;
    ofn.lpstrFile = file_buf.as_mut_ptr();
    ofn.nMaxFile = file_buf.len() as u32;
    ofn.lpstrTitle = title_w.as_ptr();
    ofn.lpstrDefExt = def_ext.as_ptr();
    // OFN_OVERWRITEPROMPT (0x02) | OFN_PATHMUSTEXIST (0x800) | OFN_NOCHANGEDIR (0x08)
    ofn.Flags = 0x00000002 | 0x00000800 | 0x00000008;

    let res = unsafe { GetSaveFileNameW(&mut ofn) };
    if res != 0 {
        let len = file_buf.iter().position(|&c| c == 0).unwrap_or(file_buf.len());
        let os_str = OsString::from_wide(&file_buf[..len]);
        Some(PathBuf::from(os_str))
    } else {
        None
    }
}

fn main() {
    let storage = StorageManager::new();
    let initial_state = storage.load_state();
    let state = Arc::new(Mutex::new(initial_state));

    let event_loop = EventLoop::new();
    let window = WindowBuilder::new()
        .with_title("Gestor de Cuentas y Perfiles - Desktop (100% Offline)")
        .with_inner_size(LogicalSize::new(1220.0, 800.0))
        .with_min_inner_size(LogicalSize::new(950.0, 620.0))
        .build(&event_loop)
        .expect("No se pudo crear la ventana de Windows");

    let state_clone = Arc::clone(&state);

    let _webview = WebViewBuilder::new()
        .with_custom_protocol("gestor".into(), move |_webview_id, request: Request<Vec<u8>>| {
            if request.method() == "OPTIONS" {
                return Response::builder()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS")
                    .header("Access-Control-Allow-Headers", "*")
                    .body(Cow::Borrowed(b"".as_ref()))
                    .unwrap();
            }

            let path = request.uri().path().trim_end_matches('/');
            let body_bytes = request.body();
            let body_str = String::from_utf8_lossy(body_bytes);

            let mut current_state = state_clone.lock().unwrap();

            match path {
                "/api/state" => {
                    let json = serde_json::to_string(&*current_state).unwrap_or_else(|_| "{}".to_string());
                    json_response(json)
                }

                "/api/add_cuenta" => {
                    if let Ok(mut cuenta) = serde_json::from_str::<Cuenta>(&body_str) {
                        cuenta.id = StorageManager::next_cuenta_id(&current_state.cuentas);
                        current_state.cuentas.push(cuenta);
                        let _ = storage.save_state(&current_state);
                    }
                    ok_response()
                }

                "/api/update_cuenta" => {
                    if let Ok(cuenta) = serde_json::from_str::<Cuenta>(&body_str) {
                        if let Some(pos) = current_state.cuentas.iter().position(|c| c.id == cuenta.id) {
                            current_state.cuentas[pos] = cuenta;
                            let _ = storage.save_state(&current_state);
                        }
                    }
                    ok_response()
                }

                "/api/delete_cuenta" => {
                    #[derive(serde::Deserialize)]
                    struct DelReq { id: i64 }
                    if let Ok(req) = serde_json::from_str::<DelReq>(&body_str) {
                        current_state.cuentas.retain(|c| c.id != req.id);
                        current_state.ventas.retain(|v| v.cuenta_id != req.id);
                        let _ = storage.save_state(&current_state);
                    }
                    ok_response()
                }

                "/api/toggle_cuenta_estado" => {
                    #[derive(serde::Deserialize)]
                    struct ToggleReq { id: i64 }
                    if let Ok(req) = serde_json::from_str::<ToggleReq>(&body_str) {
                        if let Some(c) = current_state.cuentas.iter_mut().find(|c| c.id == req.id) {
                            c.estado = if c.estado.to_lowercase() == "inhabilitada" {
                                "activa".to_string()
                            } else {
                                "inhabilitada".to_string()
                            };
                            let _ = storage.save_state(&current_state);
                        }
                    }
                    ok_response()
                }

                "/api/clear_db" => {
                    current_state.cuentas.clear();
                    current_state.ventas.clear();
                    let _ = storage.save_state(&current_state);
                    ok_response()
                }

                "/api/add_venta" => {
                    if let Ok(mut venta) = serde_json::from_str::<Venta>(&body_str) {
                        venta.id = StorageManager::next_venta_id(&current_state.ventas);
                        current_state.ventas.push(venta);
                        let _ = storage.save_state(&current_state);
                    }
                    ok_response()
                }

                "/api/update_venta" => {
                    if let Ok(venta) = serde_json::from_str::<Venta>(&body_str) {
                        if let Some(pos) = current_state.ventas.iter().position(|v| v.id == venta.id) {
                            current_state.ventas[pos] = venta;
                            let _ = storage.save_state(&current_state);
                        }
                    }
                    ok_response()
                }

                "/api/delete_venta" => {
                    #[derive(serde::Deserialize)]
                    struct DelReq { id: i64 }
                    if let Ok(req) = serde_json::from_str::<DelReq>(&body_str) {
                        current_state.ventas.retain(|v| v.id != req.id);
                        let _ = storage.save_state(&current_state);
                    }
                    ok_response()
                }

                "/api/save_template" => {
                    current_state.mensaje_template = Some(body_str.to_string());
                    let _ = storage.save_state(&current_state);
                    ok_response()
                }

                "/api/clipboard" => {
                    #[derive(serde::Deserialize)]
                    struct ClipReq { text: String }
                    if let Ok(req) = serde_json::from_str::<ClipReq>(&body_str) {
                        let ok = crate::clipboard::set_clipboard_text(&req.text);
                        let resp = format!("{{\"success\":{}}}", ok);
                        json_response(resp)
                    } else {
                        json_response("{\"success\":false,\"error\":\"JSON invalido\"}".to_string())
                    }
                }

                "/api/clipboard/paste" => {
                    let text = crate::clipboard::get_clipboard_text().unwrap_or_default();
                    #[derive(serde::Serialize)]
                    struct ClipResp { success: bool, text: String }
                    let resp = serde_json::to_string(&ClipResp { success: true, text }).unwrap_or_default();
                    json_response(resp)
                }

                "/api/import_backup" => {
                    match BackupManager::parsear_backup(&body_str) {
                        Ok((cuentas, ventas)) => {
                            let total_c = cuentas.len();
                            let total_v = ventas.len();
                            current_state.cuentas = cuentas;
                            current_state.ventas = ventas;
                            let _ = storage.save_state(&current_state);

                            let resp = format!(
                                "{{\"success\":true,\"cuentas\":{},\"ventas\":{}}}",
                                total_c, total_v
                            );
                            json_response(resp)
                        }
                        Err(err) => {
                            let resp = format!(
                                "{{\"success\":false,\"error\":\"{}\"}}",
                                err.replace('"', "\\\"")
                            );
                            json_response(resp)
                        }
                    }
                }

                "/api/export_backup" => {
                    let json = BackupManager::exportar_backup(&current_state.cuentas, &current_state.ventas)
                        .unwrap_or_else(|_| "{}".to_string());

                    let now = chrono::Local::now();
                    let default_filename = format!("gestor_backup_{}.json", now.format("%Y%m%d_%H%M%S"));

                    #[derive(serde::Serialize)]
                    struct ExportResp {
                        success: bool,
                        cancelled: bool,
                        filename: String,
                        saved_path: String,
                    }

                    if let Some(target_path) = prompt_save_file_path(&default_filename) {
                        let filename = target_path
                            .file_name()
                            .map(|n| n.to_string_lossy().to_string())
                            .unwrap_or(default_filename);
                        let saved_path = target_path.to_string_lossy().to_string();

                        if let Ok(_) = std::fs::write(&target_path, &json) {
                            let resp = ExportResp {
                                success: true,
                                cancelled: false,
                                filename,
                                saved_path,
                            };
                            json_response(serde_json::to_string(&resp).unwrap_or_default())
                        } else {
                            let resp = ExportResp {
                                success: false,
                                cancelled: false,
                                filename: String::new(),
                                saved_path: String::new(),
                            };
                            json_response(serde_json::to_string(&resp).unwrap_or_default())
                        }
                    } else {
                        let resp = ExportResp {
                            success: false,
                            cancelled: true,
                            filename: String::new(),
                            saved_path: String::new(),
                        };
                        json_response(serde_json::to_string(&resp).unwrap_or_default())
                    }
                }

                "/api/renovar_venta" => {
                    #[derive(serde::Deserialize)]
                    struct RenovarVentaReq {
                        venta_id: i64,
                        dias: i32,
                        monto_cobrado: f64,
                    }
                    if let Ok(req) = serde_json::from_str::<RenovarVentaReq>(&body_str) {
                        let hoy = chrono::Local::now().naive_local().date();
                        let mut nueva_fecha_str = String::new();
                        let mut servicio = String::new();
                        let mut correo = String::new();
                        let mut mensaje = String::new();
                        let mut encontrada = false;

                        if let Some(v) = current_state.ventas.iter_mut().find(|v| v.id == req.venta_id) {
                            encontrada = true;
                            let fecha_base = chrono::NaiveDate::parse_from_str(&v.fecha_fin, "%Y-%m-%d")
                                .unwrap_or(hoy);
                            let fecha_calculo = if fecha_base >= hoy { fecha_base } else { hoy };
                            let nueva_fecha = fecha_calculo + chrono::Duration::days(req.dias as i64);
                            nueva_fecha_str = nueva_fecha.format("%Y-%m-%d").to_string();

                            v.duracion_dias = req.dias; // SOLO el último periodo contratado!
                            v.fecha_inicio = fecha_calculo.format("%Y-%m-%d").to_string();
                            v.fecha_fin = nueva_fecha_str.clone();
                            v.precio_venta_por_perfil += req.monto_cobrado;
                            v.renovaciones_count += 1;
                            v.ultima_renovacion = hoy.format("%Y-%m-%d").to_string();

                            let cuenta_id = v.cuenta_id;
                            let mut contrasena = String::new();
                            if let Some(c) = current_state.cuentas.iter().find(|c| c.id == cuenta_id) {
                                servicio = c.tipo_cuenta.clone();
                                correo = c.correo.clone();
                                contrasena = c.contrasena_plataforma.clone();
                            }

                            let default_template = "Datos para ingresar a la cuenta de {servicio}\n\nCuenta {servicio} ({dias} dias)\nCorreo: {correo}\nContrasena: {contrasena}\n\nFecha ini: {fecha_inicio}\nFecha end: {fecha_fin}\n\nReglas para el uso de la cuenta:\n\n- No modificar ningun dato de la cuenta, en caso de modificar algun dato de la cuenta, retiro la cuenta del grupo de trabajo y te quitare el acceso, no cubrire la garantia y el tiempo de servicio.\n- Evita salirte de la cuenta.\n- Referentemente, usa la aplicacion movil en el celular y en computadora navegador Google Chrome NO PESTAÑA INCOGNITO\n- Link para pc preferentemente la pagina oficial en tu navegador\n\nIngresa ahora por favor y te paso los codigos de activacion";
                            let template = current_state.mensaje_template.as_deref().unwrap_or(default_template);
                            let texto_periodo = if req.dias == 30 { "1 mes" } else { &format!("{} días", req.dias) };

                            let mensaje_formateado = template
                                .replace("{servicio}", &servicio)
                                .replace("{periodo}", texto_periodo)
                                .replace("{dias}", &req.dias.to_string())
                                .replace("{correo}", &correo)
                                .replace("{cuenta}", &correo)
                                .replace("{contrasena}", &contrasena)
                                .replace("{clave}", &contrasena)
                                .replace("{fecha_inicio}", &fecha_calculo.format("%Y-%m-%d").to_string())
                                .replace("{fecha_fin}", &nueva_fecha_str)
                                .replace("{perfil}", "Perfil 1")
                                .replace("{pin}", "0000");

                            mensaje = mensaje_formateado;
                        }

                        if encontrada {
                            let _ = storage.save_state(&current_state);
                            crate::clipboard::set_clipboard_text(&mensaje);

                            #[derive(serde::Serialize)]
                            struct RenovarVentaResp {
                                success: bool,
                                nueva_fecha_fin: String,
                                mensaje: String,
                            }
                            let resp = serde_json::to_string(&RenovarVentaResp {
                                success: true,
                                nueva_fecha_fin: nueva_fecha_str,
                                mensaje,
                            }).unwrap_or_default();
                            json_response(resp)
                        } else {
                            json_response("{\"success\":false,\"error\":\"Venta no encontrada\"}".to_string())
                        }
                    } else {
                        json_response("{\"success\":false,\"error\":\"JSON invalido\"}".to_string())
                    }
                }

                "/api/renovar_cuenta" => {
                    #[derive(serde::Deserialize)]
                    struct RenovarCuentaReq {
                        cuenta_id: i64,
                        dias: i32,
                        costo_renovacion: f64,
                    }
                    if let Ok(req) = serde_json::from_str::<RenovarCuentaReq>(&body_str) {
                        let hoy = chrono::Local::now().naive_local().date();
                        let mut nueva_fecha_str = String::new();
                        let mut encontrada = false;

                        if let Some(c) = current_state.cuentas.iter_mut().find(|c| c.id == req.cuenta_id) {
                            encontrada = true;
                            let fecha_base = chrono::NaiveDate::parse_from_str(&c.fecha_fin, "%Y-%m-%d")
                                .unwrap_or(hoy);
                            let fecha_calculo = if fecha_base >= hoy { fecha_base } else { hoy };
                            let nueva_fecha = fecha_calculo + chrono::Duration::days(req.dias as i64);
                            nueva_fecha_str = nueva_fecha.format("%Y-%m-%d").to_string();

                            c.fecha_fin = nueva_fecha_str.clone();
                            c.costo_compra += req.costo_renovacion;
                            c.renovaciones_count += 1;
                            c.ultima_renovacion = hoy.format("%Y-%m-%d").to_string();
                            c.estado = "activa".to_string();
                        }

                        if encontrada {
                            let _ = storage.save_state(&current_state);

                            #[derive(serde::Serialize)]
                            struct RenovarCuentaResp {
                                success: bool,
                                nueva_fecha_fin: String,
                            }
                            let resp = serde_json::to_string(&RenovarCuentaResp {
                                success: true,
                                nueva_fecha_fin: nueva_fecha_str,
                            }).unwrap_or_default();
                            json_response(resp)
                        } else {
                            json_response("{\"success\":false,\"error\":\"Cuenta no encontrada\"}".to_string())
                        }
                    } else {
                        json_response("{\"success\":false,\"error\":\"JSON invalido\"}".to_string())
                    }
                }

                _ => {
                    // Servir la interfaz gráfica HTML por defecto
                    Response::builder()
                        .header("Content-Type", "text/html; charset=utf-8")
                        .header("Access-Control-Allow-Origin", "*")
                        .body(Cow::Borrowed(INDEX_HTML.as_bytes()))
                        .unwrap()
                }
            }
        })
        .with_url("gestor://app/index.html")
        .build(&window)
        .expect("No se pudo inicializar WebView2 en Windows");

    event_loop.run(move |event, _, control_flow| {
        *control_flow = ControlFlow::Wait;

        match event {
            Event::NewEvents(StartCause::Init) => {
                println!("Gestor de Cuentas Desktop iniciado con éxito.");
            }
            Event::WindowEvent {
                event: WindowEvent::CloseRequested,
                ..
            } => {
                *control_flow = ControlFlow::Exit;
            }
            _ => (),
        }
    });
}

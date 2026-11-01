fn main() {
    println!("cargo:rustc-link-arg=app.res");
    println!("cargo:rerun-if-changed=app.rc");
    println!("cargo:rerun-if-changed=app.ico");
}

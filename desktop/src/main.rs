#![cfg_attr(not(debug_assertions), windows_subsystem = "windows")]

mod models;
mod storage;
mod backup;
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
                    json_response(json)
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

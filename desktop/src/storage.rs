use std::fs;
use std::path::PathBuf;
use chrono::{Local, NaiveDate};
use crate::models::{AppState, Cuenta, FinancialSummary, Venta};

pub struct StorageManager {
    file_path: PathBuf,
}

impl StorageManager {
    pub fn new() -> Self {
        let base_dir = dirs::data_local_dir()
            .unwrap_or_else(|| PathBuf::from("."))
            .join("GestorCuentas");

        if !base_dir.exists() {
            let _ = fs::create_dir_all(&base_dir);
        }

        let file_path = base_dir.join("db.json");
        Self { file_path }
    }

    pub fn load_state(&self) -> AppState {
        if self.file_path.exists() {
            if let Ok(content) = fs::read_to_string(&self.file_path) {
                if let Ok(state) = serde_json::from_str::<AppState>(&content) {
                    return state;
                }
            }
        }
        AppState::default()
    }

    pub fn save_state(&self, state: &AppState) -> Result<(), String> {
        let json = serde_json::to_string_pretty(state)
            .map_err(|e| format!("Error serializando estado: {}", e))?;

        let temp_path = self.file_path.with_extension("tmp");
        fs::write(&temp_path, json)
            .map_err(|e| format!("Error escribiendo archivo temporal: {}", e))?;

        fs::rename(&temp_path, &self.file_path)
            .map_err(|e| format!("Error guardando archivo final: {}", e))?;

        Ok(())
    }

    pub fn next_cuenta_id(cuentas: &[Cuenta]) -> i64 {
        cuentas.iter().map(|c| c.id).max().unwrap_or(0) + 1
    }

    pub fn next_venta_id(ventas: &[Venta]) -> i64 {
        ventas.iter().map(|v| v.id).max().unwrap_or(0) + 1
    }

    #[allow(dead_code)]
    pub fn calcular_resumen(state: &AppState) -> FinancialSummary {
        let total_inversion: f64 = state.cuentas.iter().map(|c| c.costo_compra).sum();
        let total_ingresos: f64 = state
            .ventas
            .iter()
            .map(|v| v.precio_venta_por_perfil * (v.cantidad_perfiles as f64))
            .sum();

        let ganancia_neta = total_ingresos - total_inversion;
        let roi_porcentaje = if total_inversion > 0.0 {
            (ganancia_neta / total_inversion) * 100.0
        } else {
            0.0
        };

        let hoy_str = Local::now().format("%Y-%m-%d").to_string();
        let hoy_date = NaiveDate::parse_from_str(&hoy_str, "%Y-%m-%d")
            .unwrap_or_else(|_| Local::now().date_naive());

        let cuentas_activas = state
            .cuentas
            .iter()
            .filter(|c| c.estado.to_lowercase() == "activa")
            .count();

        let mut ventas_activas = 0;
        let mut ventas_vencen_hoy = 0;

        for v in &state.ventas {
            if let Ok(fin_date) = NaiveDate::parse_from_str(&v.fecha_fin, "%Y-%m-%d") {
                let diff_dias = (fin_date - hoy_date).num_days();
                if diff_dias >= 0 {
                    ventas_activas += 1;
                }
                if diff_dias == 0 {
                    ventas_vencen_hoy += 1;
                }
            }
        }

        FinancialSummary {
            total_inversion,
            total_ingresos,
            ganancia_neta,
            roi_porcentaje,
            cuentas_activas,
            ventas_activas,
            ventas_vencen_hoy,
        }
    }
}

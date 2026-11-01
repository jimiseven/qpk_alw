use chrono::Local;
use crate::models::{BackupData, Cuenta, Venta};

pub struct BackupManager;

impl BackupManager {
    pub fn parsear_backup(json_content: &str) -> Result<(Vec<Cuenta>, Vec<Venta>), String> {
        let clean = json_content.trim().trim_start_matches('\u{feff}').trim();
        if clean.is_empty() {
            return Err("El archivo de respaldo está vacío (0 bytes).".to_string());
        }

        let parsed: BackupData = serde_json::from_str(clean)
            .map_err(|e| format!("Formato JSON no válido o corrupto: {}", e))?;

        if parsed.cuentas.is_empty() && parsed.ventas.is_empty() {
            return Err("El respaldo no contiene registros de cuentas ni de ventas.".to_string());
        }

        Ok((parsed.cuentas, parsed.ventas))
    }

    pub fn exportar_backup(cuentas: &[Cuenta], ventas: &[Venta]) -> Result<String, String> {
        let hoy = Local::now().format("%Y-%m-%d").to_string();

        let backup = BackupData {
            version: 2,
            fecha_exportacion: hoy,
            total_cuentas: cuentas.len(),
            total_ventas: ventas.len(),
            cuentas: cuentas.to_vec(),
            ventas: ventas.to_vec(),
        };

        serde_json::to_string_pretty(&backup)
            .map_err(|e| format!("Error generando archivo JSON de exportación: {}", e))
    }
}

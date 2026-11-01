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
            .map_err(|e| format!("Formato JSON no compatible o corrupto: {}", e))?;

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

#[cfg(test)]
mod tests {
    use super::*;

    #[test]
    fn test_parsear_backup_valido() {
        let sample_json = r#"{
            "version": 2,
            "fecha_exportacion": "2026-11-01",
            "total_cuentas": 1,
            "total_ventas": 1,
            "cuentas": [
                {
                    "id": 1,
                    "correo": "test@netflix.com",
                    "contrasena_correo": "123",
                    "contrasena_plataforma": "456",
                    "tipo_cuenta": "Netflix",
                    "costo_compra": 50.0,
                    "fecha_creacion": "2026-10-01",
                    "fecha_fin": "2026-11-01",
                    "estado": "activa"
                }
            ],
            "ventas": [
                {
                    "id": 1,
                    "cuenta_id": 1,
                    "numero_celular_cliente": "76992490",
                    "cantidad_perfiles": 1,
                    "precio_venta_por_perfil": 15.0,
                    "duracion_dias": 30,
                    "fecha_inicio": "2026-10-05",
                    "fecha_fin": "2026-11-04"
                }
            ]
        }"#;

        let res = BackupManager::parsear_backup(sample_json);
        assert!(res.is_ok());
        let (cuentas, ventas) = res.unwrap();
        assert_eq!(cuentas.len(), 1);
        assert_eq!(ventas.len(), 1);
        assert_eq!(cuentas[0].correo, "test@netflix.com");
        assert_eq!(ventas[0].numero_celular_cliente, "76992490");
    }
}

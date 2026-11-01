use serde::{Deserialize, Serialize};

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct Cuenta {
    #[serde(default)]
    pub id: i64,
    #[serde(default)]
    pub correo: String,
    #[serde(default)]
    pub contrasena_correo: String,
    #[serde(default)]
    pub contrasena_plataforma: String,
    #[serde(default = "default_tipo_cuenta")]
    pub tipo_cuenta: String,
    #[serde(default)]
    pub costo_compra: f64,
    #[serde(default)]
    pub fecha_creacion: String,
    #[serde(default)]
    pub fecha_fin: String,
    #[serde(default = "default_estado")]
    pub estado: String,
}

fn default_tipo_cuenta() -> String {
    "Otros".to_string()
}

fn default_estado() -> String {
    "activa".to_string()
}

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct Venta {
    #[serde(default)]
    pub id: i64,
    #[serde(default)]
    pub cuenta_id: i64,
    #[serde(default)]
    pub numero_celular_cliente: String,
    #[serde(default = "default_perfiles")]
    pub cantidad_perfiles: i32,
    #[serde(default)]
    pub precio_venta_por_perfil: f64,
    #[serde(default = "default_dias")]
    pub duracion_dias: i32,
    #[serde(default)]
    pub fecha_inicio: String,
    #[serde(default)]
    pub fecha_fin: String,
}

fn default_perfiles() -> i32 {
    1
}

fn default_dias() -> i32 {
    30
}

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct BackupData {
    #[serde(default = "default_version")]
    pub version: i32,
    #[serde(default)]
    pub fecha_exportacion: String,
    #[serde(default)]
    pub total_cuentas: usize,
    #[serde(default)]
    pub total_ventas: usize,
    #[serde(default)]
    pub cuentas: Vec<Cuenta>,
    #[serde(default)]
    pub ventas: Vec<Venta>,
}

fn default_version() -> i32 {
    2
}

#[derive(Debug, Clone, Serialize, Deserialize, Default)]
pub struct AppState {
    #[serde(default)]
    pub cuentas: Vec<Cuenta>,
    #[serde(default)]
    pub ventas: Vec<Venta>,
    #[serde(default)]
    pub mensaje_template: Option<String>,
}

#[allow(dead_code)]
#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct FinancialSummary {
    pub total_inversion: f64,
    pub total_ingresos: f64,
    pub ganancia_neta: f64,
    pub roi_porcentaje: f64,
    pub cuentas_activas: usize,
    pub ventas_activas: usize,
    pub ventas_vencen_hoy: usize,
}

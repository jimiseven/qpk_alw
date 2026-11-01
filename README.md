# 📱💻 Gestor de Cuentas y Perfiles Digitales (Offline Micro-CRM)

[![Android](https://img.shields.io/badge/Platform-Android%2013%2B-3DDC84.svg?style=flat&logo=android)](https://www.android.com/)
[![Windows](https://img.shields.io/badge/Platform-Windows%2010%20%2F%2011-0078D6.svg?style=flat&logo=windows)](https://www.microsoft.com/windows/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Rust](https://img.shields.io/badge/Rust-2024%20Edition-black.svg?style=flat&logo=rust)](https://www.rust-lang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Database-Room%20SQLite%20(Offline)-003B57.svg?style=flat)](https://developer.android.com/training/data-storage/room)
[![APK Size](https://img.shields.io/badge/Android%20APK-1.96%20MB-success.svg?style=flat)]()
[![EXE Size](https://img.shields.io/badge/Windows%20EXE-892%20KB-success.svg?style=flat)]()
[![Privacy](https://img.shields.io/badge/Privacy-100%25%20Offline%20(No%20Internet)-brightgreen.svg?style=flat)]()

**Gestor de Cuentas** es una suite multiplataforma (**Android + Windows**) diseñada específicamente para revendedores y administradores de suscripciones digitales (Netflix, Spotify, ChatGPT, Gemini, YouTube, Disney+, etc.). Opera como un **micro-CRM 100% offline**, garantizando privacidad absoluta, velocidad instantánea, cero consumo de datos móviles y sincronización bidireccional mediante copias de seguridad portables en formato JSON.

---

## 🎯 Objetivo y Filosofía del Ecosistema

1. **100% Offline & Privacidad Absoluta**: Sin servidores remotos, sin APIs de terceros ni telemetría. Todos tus datos y credenciales residen exclusivamente en tu dispositivo (SQLite en Android y `%LOCALAPPDATA%` en Windows).
2. **Rendimiento Extremo**:
   * **App Móvil (Android):** Pesa **1.96 MB** (optimizada para Honor con Android 13 / MagicOS).
   * **App de Escritorio (Windows):** Pesa **892 KB** (desarrollada nativamente en **Rust**, consume solo ~25 MB de RAM y arranca en 0.1s).
3. **Ergonomía Dual**:
   * En móvil: Diseño *"Thumb Zone"* (*Bottom-Up*) para uso fluido a una mano.
   * En PC: Pantalla completa, tablas amplias, atajos de teclado y rapidez para trabajo administrativo intensivo.

---

## 🔄 Flujo de Sincronización Bidireccional (Móvil ↔ PC)

```
┌──────────────────────────┐                   ┌──────────────────────────┐
│   📱 Celular (Android)   │                   │    💻 PC (Windows Rust)  │
│  Base de Datos SQLite    │                   │   Persistencia Local     │
└────────────┬─────────────┘                   └────────────▲─────────────┘
             │ 1. Exportar Backup JSON                      │ 2. Importar JSON
             ▼                                              │
      ╔═════════════════════════════════════════════════════╧════════╗
      ║          Archivo: gestor_backup_YYYYMMDD_HHMMSS.json         ║
      ╚═════════════════════════════════════════════════════╤════════╝
             ▲                                              │
             │ 4. Restaurar en Celular                      │ 3. Descargar JSON
┌────────────┴─────────────┐                   ┌────────────▼─────────────┐
│   📱 Celular (Android)   │                   │    💻 PC (Windows Rust)  │
│   PRAGMA integrity_check │                   │  Edición rápida en PC    │
└──────────────────────────┘                   └──────────────────────────┘
```

1. **Del Celular a la PC:** En el celular pulsas *"Exportar Backup"* o tomas el respaldo automático de las 21:00 (`Android/data/com.gestorcuentas.app/files/backups/`). En la PC abres `GestorCuentas.exe`, pulsas *"📥 Importar Backup de Celular"* y listo: **los datos se guardan permanentemente en tu disco duro**. No necesitas volver a cargar el archivo cada vez que abras la PC.
2. **De la PC al Celular:** Tras registrar nuevas ventas o cuentas en tu PC, pulsas *"📤 Descargar Backup para Celular"*. Pasas el archivo a tu teléfono (por cable USB o WhatsApp Web) y tocas *"Restaurar"* en la app móvil. Room SQLite verificará la consistencia con `PRAGMA integrity_check` y se actualizará al instante.

---

## ⚡ Características Principales

### 💼 1. Gestión de Inventario y Ventas
* **Cuentas Maestras:** Registro de cuentas compradas a proveedores (correo, contraseña, pin, costo de compra, fecha y estado).
* **Venta de Perfiles:** Asignación de perfiles individuales a clientes por número de WhatsApp, definiendo precio por perfil y duración en días.
* **Cálculo Automático de Fechas:** Cálculo exacto del vencimiento (`fecha_fin`) y días restantes en tiempo real.
* **Relación en Cascada:** Al eliminar una cuenta maestra, se gestionan o eliminan sus ventas de forma íntegra sin dejar datos huérfanos.

### 📊 2. Métricas Financieras y Estadísticas en Vivo
* **Indicadores Clave (KPIs):** Resumen visual de Total Invertido, Ingresos Totales, Ganancia Neta y Porcentaje de Retorno de Inversión (**ROI %**).
* **Filtros Temporales:** Consulta de métricas por mes o por rango de fechas personalizado con selector de calendario.
* **Semáforo de Rentabilidad:** Cada cuenta maestra indica si ya recuperó la inversión y cuánta ganancia neta ha producido según los perfiles vendidos.

### 🛡️ 3. Sistema Robusto de Respaldo y Mantenimiento SQLite
* **Respaldo Automático Diario (21:00):** Tarea programada en Android mediante `WorkManager`.
* **Salvavidas In-App:** Si el teléfono estaba apagado a las 21:00, la app detecta al abrirse que falta el respaldo del día y lo genera en segundo plano sin congelar la interfaz.
* **Rotación de 14 Días:** Limpieza automática que conserva únicamente los últimos 14 respaldos (~200 KB).
* **Directorio Accesible por USB/PC:** Ubicado en `Android/data/com.gestorcuentas.app/files/backups/`, accesible directamente conectando el celular a una PC por cable USB sin root.
* **Mantenimiento Preventivo:** Ejecución de `PRAGMA optimize` y `VACUUM` tras cada respaldo para desfragmentar índices y acelerar las consultas de SQLite.
* **Verificación de Integridad:** Ejecución previa de `PRAGMA integrity_check` antes de restaurar cualquier copia para evitar bases de datos corruptas.

### 🔔 4. Control de Vencimientos y Búsqueda Inteligente
* **Alerta In-App de Vencimientos de Hoy:** Banner visual destacado en el Dashboard (*"¡Hoy vencen X perfiles! Toca aquí para ver y renovar"*). Sin notificaciones invasivas del sistema operativo ni consumo de batería en segundo plano.
* **Filtros Rápidos:** Pestañas para segmentar ventas por *Vencidas*, *Activas*, *Vencen Hoy* o *Todas*.
* **Búsqueda Inteligente por Teléfono:** Limpieza automática de prefijos internacionales (por ejemplo, al pegar `+591 76992490`, busca directamente `76992490`).

### 💬 5. Mensaje de Entrega Personalizable
* Plantilla de mensaje para WhatsApp totalmente editable desde el panel de Configuración tanto en Android como en Windows.
* Soporta variables dinámicas como:
  * `{cliente}`: Nombre o número del cliente.
  * `{servicio}`: Nombre de la plataforma (ej. Netflix).
  * `{cuenta}`: Correo de la cuenta.
  * `{clave}`: Contraseña.
  * `{perfil}`: Nombre del perfil.
  * `{pin}`: PIN de acceso.
  * `{fecha_fin}`: Fecha de expiración.
* Botón de copiado con 1 toque al portapapeles listo para enviar por chat.

---

## 📂 Organización del Repositorio (Monorepo)

```text
qpk_alw/
├── .gitignore                     # Filtra temporales pesados de Android (.gradle/) y Rust (target/)
├── README.md                      # Documentación completa del proyecto
├── GestorCuentas.apk              # 📱 Instalador compilado para Android (1.96 MB)
├── GestorCuentas.exe              # 💻 Ejecutable compilado para Windows (892 KB)
│
├── app/                           # 📱 PROYECTO ANDROID (Kotlin / Jetpack Compose)
│   ├── src/main/java/             # Código fuente de Android
│   └── build.gradle.kts           # Configuración de compilación Android
│
└── desktop/                       # 💻 PROYECTO WINDOWS (Rust / Cargo)
    ├── Cargo.toml                 # Dependencias y optimización Release (LTO + Strip)
    └── src/
        ├── main.rs                # Inicialización nativa de Windows (wry + tao + IPC)
        ├── models.rs              # Estructuras Cuenta y Venta (serde_json)
        ├── storage.rs             # Almacén local en %LOCALAPPDATA%/GestorCuentas
        ├── backup.rs              # Motor de validación y generación de backups JSON
        └── ui.rs                  # Interfaz gráfica adaptada para escritorio (HTML/CSS/JS)
```

---

## 📦 Descarga Directa de Binarios Compilados

Ambos ejecutables finales listos para usar se encuentran en la raíz del repositorio:

* 📱 **[Descargar GestorCuentas.apk](GestorCuentas.apk)** *(Android 8.0 a 14+ — 1.96 MB)*
* 💻 **[Descargar GestorCuentas_Windows.zip](GestorCuentas_Windows.zip)** *(Windows 10 / 11 64-bit portable — 495 KB, incluye `GestorCuentas.exe` y `WebView2Loader.dll`)*
* 💻 **[GestorCuentas.exe](GestorCuentas.exe)** *(Requiere tener `WebView2Loader.dll` en la misma carpeta)*

---

## 🛠️ Compilación desde el Código Fuente

### 📱 Compilar Versión Android:
```bash
./gradlew assembleRelease
# El APK se genera en: app/build/outputs/apk/release/app-release.apk
```

### 💻 Compilar Versión Windows (Rust):
```bash
cd desktop
cargo build --release
# El ejecutable se genera en: desktop/target/release/gestor-cuentas-desktop.exe
```

---

## 🚀 Sincronización con Git

Para enviar todo tu proyecto actualizado (Android + Windows) a GitHub en un solo paso:

```bash
# 1. Comprobar que el árbol esté limpio
git status

# 2. Agregar cambios y comitear
git add .
git commit -m "feat: Version completa multiplataforma Android (APK) y Windows (Rust EXE)"

# 3. Subir a GitHub
git push origin main
```

---

## 📄 Licencia

Este proyecto es privado e independiente, desarrollado a medida para uso personal y comercial en la administración de micro-servicios digitales. Todos los derechos reservados.

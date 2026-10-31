# 📱 Gestor de Cuentas y Perfiles Digitales (Offline Micro-CRM)

[![Android](https://img.shields.io/badge/Platform-Android%2013%2B-3DDC84.svg?style=flat&logo=android)](https://www.android.com/)
[![Kotlin](https://img.shields.io/badge/Kotlin-2.0-7F52FF.svg?style=flat&logo=kotlin)](https://kotlinlang.org/)
[![Compose](https://img.shields.io/badge/UI-Jetpack%20Compose%20M3-4285F4.svg?style=flat&logo=jetpackcompose)](https://developer.android.com/jetpack/compose)
[![Room](https://img.shields.io/badge/Database-Room%20SQLite%20(Offline)-003B57.svg?style=flat)](https://developer.android.com/training/data-storage/room)
[![APK Size](https://img.shields.io/badge/APK%20Size-1.96%20MB-success.svg?style=flat)]()
[![Privacy](https://img.shields.io/badge/Privacy-100%25%20Offline%20(No%20Internet)-brightgreen.svg?style=flat)]()

**Gestor de Cuentas** es una aplicación móvil nativa para Android diseñada específicamente para revendedores y administradores de suscripciones digitales (Netflix, Spotify, ChatGPT, Gemini, YouTube, Disney+, etc.). Opera como un **micro-CRM 100% offline**, garantizando privacidad absoluta, velocidad instantánea, cero consumo de datos móviles y una experiencia ergonómica adaptada al uso continuo con una sola mano.

---

## 🎯 Objetivo y Filosofía del Proyecto

1. **100% Offline & Privacidad Absoluta**: Sin servidores remotos, sin APIs de terceros ni telemetría. Todos tus datos y credenciales residen exclusivamente en la base de datos local SQLite de tu teléfono.
2. **Rendimiento Extremo**: Optimizada para teléfonos de gama media y alta (probada y adaptada para dispositivos **Honor con Android 13 / MagicOS**). Pesa tan solo **1.96 MB** gracias a la optimización de código y ofuscación R8/ProGuard.
3. **Diseño Ergonómico "Thumb Zone"**: Construida desde abajo hacia arriba (*Bottom-Up Design*). Los botones de acción, menús de navegación y formularios están ubicados en la zona natural de alcance del pulgar, eliminando la incomodidad de alcanzar la parte superior de la pantalla.

---

## ⚡ Características Principales

### 💼 1. Gestión de Inventario y Ventas
* **Cuentas Maestras:** Registro de cuentas compradas a proveedores (correo, contraseña, pin, costo de compra, fecha y estado de activación).
* **Venta de Perfiles:** Asignación de perfiles individuales a clientes por número de WhatsApp, definiendo precio por perfil y duración en días.
* **Cálculo Automático de Fechas:** Cálculo exacto del vencimiento (`fecha_fin`) y días restantes en tiempo real.
* **Relación en Cascada:** Al eliminar una cuenta maestra, se gestionan sus ventas de forma íntegra sin dejar datos huérfanos.

### 📊 2. Métricas Financieras y Estadísticas en Vivo
* **Indicadores Clave (KPIs):** Resumen visual de Total Invertido, Ingresos Totales, Ganancia Neta y Porcentaje de Retorno de Inversión (**ROI %**).
* **Filtros Temporales:** Consulta de métricas por mes o por rango de fechas personalizado con selector de calendario nativo Material 3.
* **Semáforo de Rentabilidad:** Cada cuenta maestra indica si ya recuperó la inversión y cuánta ganancia neta ha producido según los perfiles vendidos.

### 🛡️ 3. Sistema Robusto de Respaldo y Mantenimiento SQLite
* **Respaldo Automático Diario (21:00):** Tarea programada mediante `WorkManager` que genera una copia de seguridad JSON al final del día.
* **Salvavidas In-App:** Si el teléfono estaba apagado a las 21:00, la app detecta al abrirse que falta el respaldo del día y lo genera en segundo plano sin congelar la interfaz.
* **Rotación de 14 Días:** Limpieza automática que conserva únicamente los últimos 14 respaldos (~200 KB), evitando saturar el almacenamiento.
* **Directorio Accesible por USB/PC:** Ubicado en `Android/data/com.gestorcuentas.app/files/backups/`, accesible directamente conectando el celular a una PC por cable USB (sin requerir permisos especiales ni root).
* **Compartir y Restaurar con 1 Toque:** Exportación e importación directa, además de envío inmediato a WhatsApp, Google Drive, Telegram o Gmail mediante el selector nativo de Android.
* **Mantenimiento Preventivo:** Ejecución de `PRAGMA optimize` y `VACUUM` tras cada respaldo para desfragmentar índices y acelerar las consultas de SQLite.
* **Verificación de Integridad:** Ejecución previa de `PRAGMA integrity_check` antes de restaurar cualquier copia para evitar bases de datos corruptas.

### 🔔 4. Control de Vencimientos y Búsqueda Inteligente
* **Alerta In-App Matutina:** Banner visual destacado en el Dashboard al abrir la app (*"¡Hoy vencen X perfiles! Toca aquí para ver y renovar"*). Sin notificaciones invasivas del sistema operativo ni consumo de batería en segundo plano.
* **Filtros en Historial:** Pestañas para segmentar ventas por *Vencidas*, *Activas* o *Todas*.
* **Búsqueda Inteligente por Teléfono:** Limpieza automática de prefijos internacionales (por ejemplo, al pegar `+591 76992490`, busca directamente `76992490`). Incluye botón para limpiar el texto con 1 toque.

### 💬 5. Mensaje de Entrega Personalizable
* Plantilla de mensaje para WhatsApp totalmente editable desde el panel de Configuración.
* Soporta variables dinámicas como:
  * `{cliente}`: Nombre o número del cliente.
  * `{servicio}`: Nombre de la plataforma (ej. Netflix).
  * `{cuenta}`: Correo de la cuenta.
  * `{clave}`: Contraseña.
  * `{perfil}`: Nombre del perfil.
  * `{pin}`: PIN de acceso.
  * `{fecha_fin}`: Fecha de expiración.
* Botón de copiado con 1 toque al portapapeles listo para enviar por chat.

### 🔒 6. Seguridad y Apariencia
* **Protección por PIN:** Bloqueo opcional de 4 dígitos solicitado únicamente al iniciar la app.
* **Tema Visual Unificado:** Selector de Modo Oscuro, Modo Claro o Automático del Sistema con diseño Material 3 de alto contraste.
* **Iconografía Simétrica:** Interfaz calibrada para mantener proporciones uniformes y estética moderna.

---

## 🏗️ Arquitectura y Tecnologías

```
┌────────────────────────────────────────────────────────┐
│             UI Layer (Jetpack Compose M3)             │
│   DashboardScreen  •  CuentasScreen  •  HistorialScreen │
│   ModalBottomSheets (Nueva Venta, Nueva Cuenta, Backup) │
└───────────────────────────▲────────────────────────────┘
                            │ (StateFlow / UI Events)
┌───────────────────────────┴────────────────────────────┐
│         ViewModel Layer (InventarioViewModel)          │
│   Cálculos Financieros  •  Filtros  •  Validaciones    │
└───────────────────────────▲────────────────────────────┘
                            │ (Coroutines / Flow)
┌───────────────────────────┴────────────────────────────┐
│                   Repository Layer                     │
│               InventarioRepository                     │
└───────────────────────────▲────────────────────────────┘
                            │
┌───────────────────────────┴────────────────────────────┐
│      Data & Storage (Room SQLite + SharedPreferences)  │
│   AppDatabase  •  InventarioDao  •  AutoBackupManager  │
│   WorkManager (DailyBackupWorker @ 21:00)              │
└────────────────────────────────────────────────────────┘
```

| Componente | Tecnología / Librería | Versión |
| :--- | :--- | :--- |
| **Lenguaje** | Kotlin | 2.0.0 |
| **UI Toolkit** | Jetpack Compose (BOM) | 2024.06.00 |
| **Diseño** | Material Design 3 | 1.2.1 |
| **Persistencia** | Room Database (SQLite) | 2.6.1 |
| **KSP Compiler** | Google KSP | 2.0.0-1.0.21 |
| **Background Jobs**| AndroidX WorkManager | 2.9.0 |
| **Navegación** | Navigation Compose | 2.7.7 |
| **Mínimo SDK** | Android 8.0 (Oreo) | API 26 |
| **Target SDK** | Android 13 (Tiramisu) | API 33 / 34 |

---

## 📂 Estructura del Proyecto

```text
app/src/main/java/com/gestorcuentas/app/
├── CuentasApp.kt                       # Inicializador de la aplicación y agendamiento WorkManager
├── MainActivity.kt                     # Entrada única, chequeo de PIN y rescate de backup pasivo
├── data/
│   ├── dao/
│   │   └── InventarioDao.kt            # Consultas SQL optimizadas e indexadas con Flow
│   ├── db/
│   │   └── AppDatabase.kt              # Singleton Room, PRAGMA optimize, VACUUM e integrity_check
│   └── entity/
│       ├── Cuenta.kt                   # Entidad 'cuentas' (inventario de cuentas maestras)
│       ├── Venta.kt                    # Entidad 'ventas' (perfiles asignados a clientes)
│       └── Relaciones.kt               # Modelo relacional CuentaConVentas y VentaConCuenta
├── repository/
│   └── InventarioRepository.kt         # Fuente única de verdad para datos y mantenimiento
├── ui/
│   ├── components/
│   │   └── CampoFechaPicker.kt         # Componente selector de fecha con DatePickerDialog M3
│   ├── navigation/
│   │   └── Screen.kt                   # Rutas de navegación de la aplicación
│   ├── screens/
│   │   ├── DashboardScreen.kt          # KPIs, resumen financiero y alerta in-app de vencimientos
│   │   ├── CuentasScreen.kt            # Lista de cuentas maestras con indicador de ganancia
│   │   ├── CuentaDetalleScreen.kt      # Detalle de cuenta maestra y sus perfiles vendidos
│   │   ├── HistorialClienteScreen.kt   # Historial de compras, búsqueda y filtros de vencimiento
│   │   ├── NuevaCuentaBottomSheet.kt   # Formulario inferior para registrar cuenta maestra
│   │   ├── NuevaVentaBottomSheet.kt    # Formulario inferior para registrar venta de perfil
│   │   ├── EditarCuentaDialog.kt       # Diálogo modal de edición de cuenta
│   │   ├── EditarVentaDialog.kt        # Diálogo modal de edición de venta
│   │   ├── CopiaSeguridadBottomSheet.kt# Gestor de respaldos locales, compartir y restaurar
│   │   ├── EstadisticasDialog.kt       # Reporte detallado por mes o rango de fechas
│   │   ├── ConfiguracionSeguridadDialog.kt # Ajuste de plantilla de mensaje, PIN y tema
│   │   └── LockScreen.kt               # Pantalla de bloqueo numérico al abrir la app
│   └── theme/
│       ├── Color.kt                    # Paleta de colores Dark & Light
│       ├── Theme.kt                    # Configuración de MaterialTheme M3
│       └── Type.kt                     # Tipografía Material 3
├── utils/
│   ├── AppPreferences.kt               # Preferencias del usuario en SharedPreferences
│   ├── AutoBackupManager.kt            # Lógica de respaldo a las 21:00, rotación 14 días y exportación
│   ├── BackupUtils.kt                  # Serialización y parseo de estructura JSON
│   ├── DateUtils.kt                    # Utilidades de fechas (días restantes, formato dd/MM/yyyy)
│   ├── MensajeEntregaUtils.kt          # Procesador de plantillas dinámicas para WhatsApp
│   └── PhoneUtils.kt                   # Normalizador y limpiador de números de teléfono
├── viewmodel/
│   └── InventarioViewModel.kt          # Estado reactivo, KPIs, validación y gestión de Room
└── workers/
    └── DailyBackupWorker.kt            # CoroutineWorker para el respaldo diario nocturno
```

---

## 🗄️ Modelo de Base de Datos (SQLite)

```sql
-- Tabla: cuentas (Cuentas Maestras Compradas)
CREATE TABLE cuentas (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    servicio TEXT NOT NULL,
    correo TEXT NOT NULL,
    password TEXT NOT NULL,
    pin_cuenta TEXT,
    costo REAL NOT NULL,
    fecha_compra INTEGER NOT NULL,
    perfiles_totales INTEGER NOT NULL,
    activa INTEGER NOT NULL DEFAULT 1
);

-- Tabla: ventas (Perfiles Vendidos a Clientes)
CREATE TABLE ventas (
    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
    cuenta_id INTEGER NOT NULL,
    numero_celular_cliente TEXT NOT NULL,
    nombre_cliente TEXT,
    nombre_perfil TEXT,
    pin_perfil TEXT,
    precio_venta REAL NOT NULL,
    dias_duracion INTEGER NOT NULL,
    fecha_inicio INTEGER NOT NULL,
    fecha_fin INTEGER NOT NULL,
    FOREIGN KEY(cuenta_id) REFERENCES cuentas(id) ON DELETE CASCADE
);

-- Índices de Rendimiento
CREATE INDEX index_ventas_cuenta_id ON ventas(cuenta_id);
CREATE INDEX index_ventas_numero_celular_cliente ON ventas(numero_celular_cliente);
```

---

## 📦 Instalación y Descarga Directa

El archivo compilado listo para instalar en tu dispositivo Android se encuentra en la raíz del repositorio:

* 📥 **[Descargar GestorCuentas.apk](GestorCuentas.apk)** *(Versión Release Optimizada — 1.96 MB)*

### Pasos para instalar en el teléfono:
1. Copia el archivo `GestorCuentas.apk` a tu teléfono (o descárgalo directamente desde GitHub en tu navegador).
2. Toca el archivo `.apk` desde el explorador de archivos de tu teléfono.
3. Si el sistema te lo solicita, activa *"Permitir instalar aplicaciones de fuentes desconocidas"*.
4. Abre la aplicación y configura tu PIN si deseas proteger tus datos.

---

## 🛠️ Compilación desde el Código Fuente

Si deseas compilar el APK tú mismo desde la terminal:

### Requisitos:
* JDK 17 o JDK 21 configurado en tu entorno (`JAVA_HOME`).
* Android SDK con Build-Tools `34.0.0` o superior y Plataforma Android 33/34.

### Comandos de Compilación:
```bash
# Compilar versión Release optimizada (con R8 y ProGuard)
./gradlew assembleRelease

# El APK generado se ubicará en:
# app/build/outputs/apk/release/app-release.apk
```

---

## 🚀 Sincronización con Git

Para enviar esta versión estable a tu repositorio remoto de GitHub:

```bash
# 1. Verificar estado actual (rama limpia y commit listo)
git status

# 2. Subir commits al repositorio remoto en GitHub
git push origin main
```

---

## 📄 Licencia

Este proyecto es privado e independiente, desarrollado a medida para uso personal y comercial en la administración de micro-servicios digitales. Todos los derechos reservados.

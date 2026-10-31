# Reglas de optimización ProGuard / R8 para Gestor de Cuentas

# 1. Base de datos Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.paging.**

# 2. Entidades de datos y relaciones de inventario
-keep class com.gestorcuentas.app.data.entity.** { *; }
-keepclassmembers class com.gestorcuentas.app.data.entity.** { *; }

# 3. Modelos de preferencias y temas
-keep class com.gestorcuentas.app.utils.AppThemeMode { *; }

# 4. Corrutinas de Kotlin
-dontwarn kotlinx.coroutines.**
-keepattributes *Annotation*, InnerClasses, EnclosingMethod

# 5. WorkManager y Workers
-keep class * extends androidx.work.ListenableWorker { *; }
-keep class * extends androidx.work.Worker { *; }

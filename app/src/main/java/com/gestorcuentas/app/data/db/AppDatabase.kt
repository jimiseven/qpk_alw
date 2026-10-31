package com.gestorcuentas.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.gestorcuentas.app.data.dao.InventarioDao
import com.gestorcuentas.app.data.entity.Cuenta
import com.gestorcuentas.app.data.entity.Venta

/**
 * Base de datos Room principal de la aplicación.
 * 100% Offline, utiliza SQLite nativo para máxima velocidad y fiabilidad en Android.
 */
@Database(
    entities = [
        Cuenta::class,
        Venta::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun inventarioDao(): InventarioDao

    /**
     * Mantenimiento de optimización de SQLite (analiza estadísticas de índices y desfragmenta).
     */
    fun optimizeDatabase() {
        try {
            openHelper.writableDatabase.execSQL("PRAGMA optimize")
        } catch (e: Exception) {
            // Ignorar en caso de bloqueo concurrente temporal
        }
    }

    /**
     * Desfragmenta y compacta la base de datos eliminando espacio no utilizado en disco.
     */
    fun vacuumDatabase() {
        try {
            openHelper.writableDatabase.execSQL("VACUUM")
        } catch (e: Exception) {
            // Ignorar si hay transacción concurrente
        }
    }

    /**
     * Verifica la integridad física del archivo SQLite de Room.
     * Retorna true si SQLite responde "ok".
     */
    fun verifyIntegrity(): Boolean {
        return try {
            val cursor = openHelper.writableDatabase.query("PRAGMA integrity_check")
            var isValid = false
            if (cursor.moveToFirst()) {
                val result = cursor.getString(0)
                isValid = result.equals("ok", ignoreCase = true)
            }
            cursor.close()
            isValid
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cuentas ADD COLUMN fecha_fin TEXT NOT NULL DEFAULT ''")
            }
        }

        private val MIGRATION_2_3 = object : androidx.room.migration.Migration(2, 3) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("CREATE INDEX IF NOT EXISTS index_ventas_fecha_fin ON ventas(fecha_fin)")
                db.execSQL("CREATE INDEX IF NOT EXISTS index_cuentas_estado ON cuentas(estado)")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventario_cuentas_db"
                )
                .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}

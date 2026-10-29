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
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun inventarioDao(): InventarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE cuentas ADD COLUMN fecha_fin TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventario_cuentas_db"
                )
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}

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
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun inventarioDao(): InventarioDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "inventario_cuentas_db"
                )
                // En caso de cambio de versión durante desarrollo
                .fallbackToDestructiveMigration()
                .build()

                INSTANCE = instance
                instance
            }
        }
    }
}

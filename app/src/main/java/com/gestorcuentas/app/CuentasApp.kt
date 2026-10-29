package com.gestorcuentas.app

import android.app.Application
import com.gestorcuentas.app.data.db.AppDatabase
import com.gestorcuentas.app.repository.InventarioRepository

/**
 * Clase Application para inicialización perezosa (lazy) de la base de datos Room y repositorio.
 */
class CuentasApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
    val repository: InventarioRepository by lazy { InventarioRepository(database.inventarioDao()) }
}

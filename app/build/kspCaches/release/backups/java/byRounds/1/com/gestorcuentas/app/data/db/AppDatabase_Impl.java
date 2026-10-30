package com.gestorcuentas.app.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.gestorcuentas.app.data.dao.InventarioDao;
import com.gestorcuentas.app.data.dao.InventarioDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile InventarioDao _inventarioDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(3) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `cuentas` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `correo` TEXT NOT NULL, `contrasena_correo` TEXT NOT NULL, `contrasena_plataforma` TEXT NOT NULL, `tipo_cuenta` TEXT NOT NULL, `costo_compra` REAL NOT NULL, `fecha_creacion` TEXT NOT NULL, `fecha_fin` TEXT NOT NULL DEFAULT '', `estado` TEXT NOT NULL)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_cuentas_estado` ON `cuentas` (`estado`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `ventas` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `cuenta_id` INTEGER NOT NULL, `numero_celular_cliente` TEXT NOT NULL, `cantidad_perfiles` INTEGER NOT NULL, `precio_venta_por_perfil` REAL NOT NULL, `duracion_dias` INTEGER NOT NULL, `fecha_inicio` TEXT NOT NULL, `fecha_fin` TEXT NOT NULL, FOREIGN KEY(`cuenta_id`) REFERENCES `cuentas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_ventas_cuenta_id` ON `ventas` (`cuenta_id`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_ventas_numero_celular_cliente` ON `ventas` (`numero_celular_cliente`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_ventas_fecha_fin` ON `ventas` (`fecha_fin`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0bb39335c6fb8f27f9a6d2bddd19d66e')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `cuentas`");
        db.execSQL("DROP TABLE IF EXISTS `ventas`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        db.execSQL("PRAGMA foreign_keys = ON");
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsCuentas = new HashMap<String, TableInfo.Column>(9);
        _columnsCuentas.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCuentas.put("correo", new TableInfo.Column("correo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCuentas.put("contrasena_correo", new TableInfo.Column("contrasena_correo", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCuentas.put("contrasena_plataforma", new TableInfo.Column("contrasena_plataforma", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCuentas.put("tipo_cuenta", new TableInfo.Column("tipo_cuenta", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCuentas.put("costo_compra", new TableInfo.Column("costo_compra", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCuentas.put("fecha_creacion", new TableInfo.Column("fecha_creacion", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsCuentas.put("fecha_fin", new TableInfo.Column("fecha_fin", "TEXT", true, 0, "''", TableInfo.CREATED_FROM_ENTITY));
        _columnsCuentas.put("estado", new TableInfo.Column("estado", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysCuentas = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesCuentas = new HashSet<TableInfo.Index>(1);
        _indicesCuentas.add(new TableInfo.Index("index_cuentas_estado", false, Arrays.asList("estado"), Arrays.asList("ASC")));
        final TableInfo _infoCuentas = new TableInfo("cuentas", _columnsCuentas, _foreignKeysCuentas, _indicesCuentas);
        final TableInfo _existingCuentas = TableInfo.read(db, "cuentas");
        if (!_infoCuentas.equals(_existingCuentas)) {
          return new RoomOpenHelper.ValidationResult(false, "cuentas(com.gestorcuentas.app.data.entity.Cuenta).\n"
                  + " Expected:\n" + _infoCuentas + "\n"
                  + " Found:\n" + _existingCuentas);
        }
        final HashMap<String, TableInfo.Column> _columnsVentas = new HashMap<String, TableInfo.Column>(8);
        _columnsVentas.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVentas.put("cuenta_id", new TableInfo.Column("cuenta_id", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVentas.put("numero_celular_cliente", new TableInfo.Column("numero_celular_cliente", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVentas.put("cantidad_perfiles", new TableInfo.Column("cantidad_perfiles", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVentas.put("precio_venta_por_perfil", new TableInfo.Column("precio_venta_por_perfil", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVentas.put("duracion_dias", new TableInfo.Column("duracion_dias", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVentas.put("fecha_inicio", new TableInfo.Column("fecha_inicio", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsVentas.put("fecha_fin", new TableInfo.Column("fecha_fin", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysVentas = new HashSet<TableInfo.ForeignKey>(1);
        _foreignKeysVentas.add(new TableInfo.ForeignKey("cuentas", "CASCADE", "NO ACTION", Arrays.asList("cuenta_id"), Arrays.asList("id")));
        final HashSet<TableInfo.Index> _indicesVentas = new HashSet<TableInfo.Index>(3);
        _indicesVentas.add(new TableInfo.Index("index_ventas_cuenta_id", false, Arrays.asList("cuenta_id"), Arrays.asList("ASC")));
        _indicesVentas.add(new TableInfo.Index("index_ventas_numero_celular_cliente", false, Arrays.asList("numero_celular_cliente"), Arrays.asList("ASC")));
        _indicesVentas.add(new TableInfo.Index("index_ventas_fecha_fin", false, Arrays.asList("fecha_fin"), Arrays.asList("ASC")));
        final TableInfo _infoVentas = new TableInfo("ventas", _columnsVentas, _foreignKeysVentas, _indicesVentas);
        final TableInfo _existingVentas = TableInfo.read(db, "ventas");
        if (!_infoVentas.equals(_existingVentas)) {
          return new RoomOpenHelper.ValidationResult(false, "ventas(com.gestorcuentas.app.data.entity.Venta).\n"
                  + " Expected:\n" + _infoVentas + "\n"
                  + " Found:\n" + _existingVentas);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0bb39335c6fb8f27f9a6d2bddd19d66e", "e64faa52fa9b239dbd4118ede9ab1c2d");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "cuentas","ventas");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `cuentas`");
      _db.execSQL("DELETE FROM `ventas`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(InventarioDao.class, InventarioDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public InventarioDao inventarioDao() {
    if (_inventarioDao != null) {
      return _inventarioDao;
    } else {
      synchronized(this) {
        if(_inventarioDao == null) {
          _inventarioDao = new InventarioDao_Impl(this);
        }
        return _inventarioDao;
      }
    }
  }
}

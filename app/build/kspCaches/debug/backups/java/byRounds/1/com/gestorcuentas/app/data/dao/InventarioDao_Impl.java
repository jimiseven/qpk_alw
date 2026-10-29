package com.gestorcuentas.app.data.dao;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.collection.LongSparseArray;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.RelationUtil;
import androidx.room.util.StringUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.gestorcuentas.app.data.entity.Cuenta;
import com.gestorcuentas.app.data.entity.CuentaConVentas;
import com.gestorcuentas.app.data.entity.Venta;
import com.gestorcuentas.app.data.entity.VentaConCuenta;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.StringBuilder;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class InventarioDao_Impl implements InventarioDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<Cuenta> __insertionAdapterOfCuenta;

  private final EntityInsertionAdapter<Venta> __insertionAdapterOfVenta;

  private final EntityDeletionOrUpdateAdapter<Cuenta> __deletionAdapterOfCuenta;

  private final EntityDeletionOrUpdateAdapter<Venta> __deletionAdapterOfVenta;

  private final EntityDeletionOrUpdateAdapter<Cuenta> __updateAdapterOfCuenta;

  private final EntityDeletionOrUpdateAdapter<Venta> __updateAdapterOfVenta;

  public InventarioDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfCuenta = new EntityInsertionAdapter<Cuenta>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `cuentas` (`id`,`correo`,`contrasena_correo`,`contrasena_plataforma`,`tipo_cuenta`,`costo_compra`,`fecha_creacion`,`estado`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Cuenta entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCorreo());
        statement.bindString(3, entity.getContrasenaCorreo());
        statement.bindString(4, entity.getContrasenaPlataforma());
        statement.bindString(5, entity.getTipoCuenta());
        statement.bindDouble(6, entity.getCostoCompra());
        statement.bindString(7, entity.getFechaCreacion());
        statement.bindString(8, entity.getEstado());
      }
    };
    this.__insertionAdapterOfVenta = new EntityInsertionAdapter<Venta>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `ventas` (`id`,`cuenta_id`,`numero_celular_cliente`,`cantidad_perfiles`,`precio_venta_por_perfil`,`duracion_dias`,`fecha_inicio`,`fecha_fin`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Venta entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCuentaId());
        statement.bindString(3, entity.getNumeroCelularCliente());
        statement.bindLong(4, entity.getCantidadPerfiles());
        statement.bindDouble(5, entity.getPrecioVentaPorPerfil());
        statement.bindLong(6, entity.getDuracionDias());
        statement.bindString(7, entity.getFechaInicio());
        statement.bindString(8, entity.getFechaFin());
      }
    };
    this.__deletionAdapterOfCuenta = new EntityDeletionOrUpdateAdapter<Cuenta>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `cuentas` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Cuenta entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__deletionAdapterOfVenta = new EntityDeletionOrUpdateAdapter<Venta>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `ventas` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Venta entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfCuenta = new EntityDeletionOrUpdateAdapter<Cuenta>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `cuentas` SET `id` = ?,`correo` = ?,`contrasena_correo` = ?,`contrasena_plataforma` = ?,`tipo_cuenta` = ?,`costo_compra` = ?,`fecha_creacion` = ?,`estado` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Cuenta entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getCorreo());
        statement.bindString(3, entity.getContrasenaCorreo());
        statement.bindString(4, entity.getContrasenaPlataforma());
        statement.bindString(5, entity.getTipoCuenta());
        statement.bindDouble(6, entity.getCostoCompra());
        statement.bindString(7, entity.getFechaCreacion());
        statement.bindString(8, entity.getEstado());
        statement.bindLong(9, entity.getId());
      }
    };
    this.__updateAdapterOfVenta = new EntityDeletionOrUpdateAdapter<Venta>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `ventas` SET `id` = ?,`cuenta_id` = ?,`numero_celular_cliente` = ?,`cantidad_perfiles` = ?,`precio_venta_por_perfil` = ?,`duracion_dias` = ?,`fecha_inicio` = ?,`fecha_fin` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final Venta entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getCuentaId());
        statement.bindString(3, entity.getNumeroCelularCliente());
        statement.bindLong(4, entity.getCantidadPerfiles());
        statement.bindDouble(5, entity.getPrecioVentaPorPerfil());
        statement.bindLong(6, entity.getDuracionDias());
        statement.bindString(7, entity.getFechaInicio());
        statement.bindString(8, entity.getFechaFin());
        statement.bindLong(9, entity.getId());
      }
    };
  }

  @Override
  public Object insertCuenta(final Cuenta cuenta, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfCuenta.insertAndReturnId(cuenta);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object insertVenta(final Venta venta, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfVenta.insertAndReturnId(venta);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteCuenta(final Cuenta cuenta, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfCuenta.handle(cuenta);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteVenta(final Venta venta, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfVenta.handle(venta);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateCuenta(final Cuenta cuenta, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfCuenta.handle(cuenta);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateVenta(final Venta venta, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfVenta.handle(venta);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<Cuenta>> getAllCuentas() {
    final String _sql = "SELECT * FROM cuentas ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cuentas"}, new Callable<List<Cuenta>>() {
      @Override
      @NonNull
      public List<Cuenta> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCorreo = CursorUtil.getColumnIndexOrThrow(_cursor, "correo");
          final int _cursorIndexOfContrasenaCorreo = CursorUtil.getColumnIndexOrThrow(_cursor, "contrasena_correo");
          final int _cursorIndexOfContrasenaPlataforma = CursorUtil.getColumnIndexOrThrow(_cursor, "contrasena_plataforma");
          final int _cursorIndexOfTipoCuenta = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo_cuenta");
          final int _cursorIndexOfCostoCompra = CursorUtil.getColumnIndexOrThrow(_cursor, "costo_compra");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_creacion");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final List<Cuenta> _result = new ArrayList<Cuenta>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Cuenta _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpCorreo;
            _tmpCorreo = _cursor.getString(_cursorIndexOfCorreo);
            final String _tmpContrasenaCorreo;
            _tmpContrasenaCorreo = _cursor.getString(_cursorIndexOfContrasenaCorreo);
            final String _tmpContrasenaPlataforma;
            _tmpContrasenaPlataforma = _cursor.getString(_cursorIndexOfContrasenaPlataforma);
            final String _tmpTipoCuenta;
            _tmpTipoCuenta = _cursor.getString(_cursorIndexOfTipoCuenta);
            final double _tmpCostoCompra;
            _tmpCostoCompra = _cursor.getDouble(_cursorIndexOfCostoCompra);
            final String _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getString(_cursorIndexOfFechaCreacion);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            _item = new Cuenta(_tmpId,_tmpCorreo,_tmpContrasenaCorreo,_tmpContrasenaPlataforma,_tmpTipoCuenta,_tmpCostoCompra,_tmpFechaCreacion,_tmpEstado);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Cuenta> getCuentaById(final int id) {
    final String _sql = "SELECT * FROM cuentas WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cuentas"}, new Callable<Cuenta>() {
      @Override
      @Nullable
      public Cuenta call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCorreo = CursorUtil.getColumnIndexOrThrow(_cursor, "correo");
          final int _cursorIndexOfContrasenaCorreo = CursorUtil.getColumnIndexOrThrow(_cursor, "contrasena_correo");
          final int _cursorIndexOfContrasenaPlataforma = CursorUtil.getColumnIndexOrThrow(_cursor, "contrasena_plataforma");
          final int _cursorIndexOfTipoCuenta = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo_cuenta");
          final int _cursorIndexOfCostoCompra = CursorUtil.getColumnIndexOrThrow(_cursor, "costo_compra");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_creacion");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final Cuenta _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpCorreo;
            _tmpCorreo = _cursor.getString(_cursorIndexOfCorreo);
            final String _tmpContrasenaCorreo;
            _tmpContrasenaCorreo = _cursor.getString(_cursorIndexOfContrasenaCorreo);
            final String _tmpContrasenaPlataforma;
            _tmpContrasenaPlataforma = _cursor.getString(_cursorIndexOfContrasenaPlataforma);
            final String _tmpTipoCuenta;
            _tmpTipoCuenta = _cursor.getString(_cursorIndexOfTipoCuenta);
            final double _tmpCostoCompra;
            _tmpCostoCompra = _cursor.getDouble(_cursorIndexOfCostoCompra);
            final String _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getString(_cursorIndexOfFechaCreacion);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            _result = new Cuenta(_tmpId,_tmpCorreo,_tmpContrasenaCorreo,_tmpContrasenaPlataforma,_tmpTipoCuenta,_tmpCostoCompra,_tmpFechaCreacion,_tmpEstado);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Cuenta>> getCuentasActivas() {
    final String _sql = "SELECT * FROM cuentas WHERE estado = 'activa' ORDER BY tipo_cuenta ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"cuentas"}, new Callable<List<Cuenta>>() {
      @Override
      @NonNull
      public List<Cuenta> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCorreo = CursorUtil.getColumnIndexOrThrow(_cursor, "correo");
          final int _cursorIndexOfContrasenaCorreo = CursorUtil.getColumnIndexOrThrow(_cursor, "contrasena_correo");
          final int _cursorIndexOfContrasenaPlataforma = CursorUtil.getColumnIndexOrThrow(_cursor, "contrasena_plataforma");
          final int _cursorIndexOfTipoCuenta = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo_cuenta");
          final int _cursorIndexOfCostoCompra = CursorUtil.getColumnIndexOrThrow(_cursor, "costo_compra");
          final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_creacion");
          final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
          final List<Cuenta> _result = new ArrayList<Cuenta>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Cuenta _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpCorreo;
            _tmpCorreo = _cursor.getString(_cursorIndexOfCorreo);
            final String _tmpContrasenaCorreo;
            _tmpContrasenaCorreo = _cursor.getString(_cursorIndexOfContrasenaCorreo);
            final String _tmpContrasenaPlataforma;
            _tmpContrasenaPlataforma = _cursor.getString(_cursorIndexOfContrasenaPlataforma);
            final String _tmpTipoCuenta;
            _tmpTipoCuenta = _cursor.getString(_cursorIndexOfTipoCuenta);
            final double _tmpCostoCompra;
            _tmpCostoCompra = _cursor.getDouble(_cursorIndexOfCostoCompra);
            final String _tmpFechaCreacion;
            _tmpFechaCreacion = _cursor.getString(_cursorIndexOfFechaCreacion);
            final String _tmpEstado;
            _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
            _item = new Cuenta(_tmpId,_tmpCorreo,_tmpContrasenaCorreo,_tmpContrasenaPlataforma,_tmpTipoCuenta,_tmpCostoCompra,_tmpFechaCreacion,_tmpEstado);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Venta>> getAllVentas() {
    final String _sql = "SELECT * FROM ventas ORDER BY fecha_fin ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ventas"}, new Callable<List<Venta>>() {
      @Override
      @NonNull
      public List<Venta> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCuentaId = CursorUtil.getColumnIndexOrThrow(_cursor, "cuenta_id");
          final int _cursorIndexOfNumeroCelularCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "numero_celular_cliente");
          final int _cursorIndexOfCantidadPerfiles = CursorUtil.getColumnIndexOrThrow(_cursor, "cantidad_perfiles");
          final int _cursorIndexOfPrecioVentaPorPerfil = CursorUtil.getColumnIndexOrThrow(_cursor, "precio_venta_por_perfil");
          final int _cursorIndexOfDuracionDias = CursorUtil.getColumnIndexOrThrow(_cursor, "duracion_dias");
          final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_inicio");
          final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_fin");
          final List<Venta> _result = new ArrayList<Venta>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Venta _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCuentaId;
            _tmpCuentaId = _cursor.getInt(_cursorIndexOfCuentaId);
            final String _tmpNumeroCelularCliente;
            _tmpNumeroCelularCliente = _cursor.getString(_cursorIndexOfNumeroCelularCliente);
            final int _tmpCantidadPerfiles;
            _tmpCantidadPerfiles = _cursor.getInt(_cursorIndexOfCantidadPerfiles);
            final double _tmpPrecioVentaPorPerfil;
            _tmpPrecioVentaPorPerfil = _cursor.getDouble(_cursorIndexOfPrecioVentaPorPerfil);
            final int _tmpDuracionDias;
            _tmpDuracionDias = _cursor.getInt(_cursorIndexOfDuracionDias);
            final String _tmpFechaInicio;
            _tmpFechaInicio = _cursor.getString(_cursorIndexOfFechaInicio);
            final String _tmpFechaFin;
            _tmpFechaFin = _cursor.getString(_cursorIndexOfFechaFin);
            _item = new Venta(_tmpId,_tmpCuentaId,_tmpNumeroCelularCliente,_tmpCantidadPerfiles,_tmpPrecioVentaPorPerfil,_tmpDuracionDias,_tmpFechaInicio,_tmpFechaFin);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<Venta> getVentaById(final int id) {
    final String _sql = "SELECT * FROM ventas WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ventas"}, new Callable<Venta>() {
      @Override
      @Nullable
      public Venta call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCuentaId = CursorUtil.getColumnIndexOrThrow(_cursor, "cuenta_id");
          final int _cursorIndexOfNumeroCelularCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "numero_celular_cliente");
          final int _cursorIndexOfCantidadPerfiles = CursorUtil.getColumnIndexOrThrow(_cursor, "cantidad_perfiles");
          final int _cursorIndexOfPrecioVentaPorPerfil = CursorUtil.getColumnIndexOrThrow(_cursor, "precio_venta_por_perfil");
          final int _cursorIndexOfDuracionDias = CursorUtil.getColumnIndexOrThrow(_cursor, "duracion_dias");
          final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_inicio");
          final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_fin");
          final Venta _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCuentaId;
            _tmpCuentaId = _cursor.getInt(_cursorIndexOfCuentaId);
            final String _tmpNumeroCelularCliente;
            _tmpNumeroCelularCliente = _cursor.getString(_cursorIndexOfNumeroCelularCliente);
            final int _tmpCantidadPerfiles;
            _tmpCantidadPerfiles = _cursor.getInt(_cursorIndexOfCantidadPerfiles);
            final double _tmpPrecioVentaPorPerfil;
            _tmpPrecioVentaPorPerfil = _cursor.getDouble(_cursorIndexOfPrecioVentaPorPerfil);
            final int _tmpDuracionDias;
            _tmpDuracionDias = _cursor.getInt(_cursorIndexOfDuracionDias);
            final String _tmpFechaInicio;
            _tmpFechaInicio = _cursor.getString(_cursorIndexOfFechaInicio);
            final String _tmpFechaFin;
            _tmpFechaFin = _cursor.getString(_cursorIndexOfFechaFin);
            _result = new Venta(_tmpId,_tmpCuentaId,_tmpNumeroCelularCliente,_tmpCantidadPerfiles,_tmpPrecioVentaPorPerfil,_tmpDuracionDias,_tmpFechaInicio,_tmpFechaFin);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Venta>> getVentasPorCuenta(final int cuentaId) {
    final String _sql = "SELECT * FROM ventas WHERE cuenta_id = ? ORDER BY fecha_inicio DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cuentaId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ventas"}, new Callable<List<Venta>>() {
      @Override
      @NonNull
      public List<Venta> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCuentaId = CursorUtil.getColumnIndexOrThrow(_cursor, "cuenta_id");
          final int _cursorIndexOfNumeroCelularCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "numero_celular_cliente");
          final int _cursorIndexOfCantidadPerfiles = CursorUtil.getColumnIndexOrThrow(_cursor, "cantidad_perfiles");
          final int _cursorIndexOfPrecioVentaPorPerfil = CursorUtil.getColumnIndexOrThrow(_cursor, "precio_venta_por_perfil");
          final int _cursorIndexOfDuracionDias = CursorUtil.getColumnIndexOrThrow(_cursor, "duracion_dias");
          final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_inicio");
          final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_fin");
          final List<Venta> _result = new ArrayList<Venta>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Venta _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCuentaId;
            _tmpCuentaId = _cursor.getInt(_cursorIndexOfCuentaId);
            final String _tmpNumeroCelularCliente;
            _tmpNumeroCelularCliente = _cursor.getString(_cursorIndexOfNumeroCelularCliente);
            final int _tmpCantidadPerfiles;
            _tmpCantidadPerfiles = _cursor.getInt(_cursorIndexOfCantidadPerfiles);
            final double _tmpPrecioVentaPorPerfil;
            _tmpPrecioVentaPorPerfil = _cursor.getDouble(_cursorIndexOfPrecioVentaPorPerfil);
            final int _tmpDuracionDias;
            _tmpDuracionDias = _cursor.getInt(_cursorIndexOfDuracionDias);
            final String _tmpFechaInicio;
            _tmpFechaInicio = _cursor.getString(_cursorIndexOfFechaInicio);
            final String _tmpFechaFin;
            _tmpFechaFin = _cursor.getString(_cursorIndexOfFechaFin);
            _item = new Venta(_tmpId,_tmpCuentaId,_tmpNumeroCelularCliente,_tmpCantidadPerfiles,_tmpPrecioVentaPorPerfil,_tmpDuracionDias,_tmpFechaInicio,_tmpFechaFin);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Venta>> getVentasPorCliente(final String numeroCelular) {
    final String _sql = "SELECT * FROM ventas WHERE numero_celular_cliente LIKE '%' || ? || '%' ORDER BY fecha_inicio DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, numeroCelular);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ventas"}, new Callable<List<Venta>>() {
      @Override
      @NonNull
      public List<Venta> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCuentaId = CursorUtil.getColumnIndexOrThrow(_cursor, "cuenta_id");
          final int _cursorIndexOfNumeroCelularCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "numero_celular_cliente");
          final int _cursorIndexOfCantidadPerfiles = CursorUtil.getColumnIndexOrThrow(_cursor, "cantidad_perfiles");
          final int _cursorIndexOfPrecioVentaPorPerfil = CursorUtil.getColumnIndexOrThrow(_cursor, "precio_venta_por_perfil");
          final int _cursorIndexOfDuracionDias = CursorUtil.getColumnIndexOrThrow(_cursor, "duracion_dias");
          final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_inicio");
          final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_fin");
          final List<Venta> _result = new ArrayList<Venta>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Venta _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCuentaId;
            _tmpCuentaId = _cursor.getInt(_cursorIndexOfCuentaId);
            final String _tmpNumeroCelularCliente;
            _tmpNumeroCelularCliente = _cursor.getString(_cursorIndexOfNumeroCelularCliente);
            final int _tmpCantidadPerfiles;
            _tmpCantidadPerfiles = _cursor.getInt(_cursorIndexOfCantidadPerfiles);
            final double _tmpPrecioVentaPorPerfil;
            _tmpPrecioVentaPorPerfil = _cursor.getDouble(_cursorIndexOfPrecioVentaPorPerfil);
            final int _tmpDuracionDias;
            _tmpDuracionDias = _cursor.getInt(_cursorIndexOfDuracionDias);
            final String _tmpFechaInicio;
            _tmpFechaInicio = _cursor.getString(_cursorIndexOfFechaInicio);
            final String _tmpFechaFin;
            _tmpFechaFin = _cursor.getString(_cursorIndexOfFechaFin);
            _item = new Venta(_tmpId,_tmpCuentaId,_tmpNumeroCelularCliente,_tmpCantidadPerfiles,_tmpPrecioVentaPorPerfil,_tmpDuracionDias,_tmpFechaInicio,_tmpFechaFin);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<Venta>> getVentasPorVencer(final String fechaHoy, final String fechaLimite) {
    final String _sql = "SELECT * FROM ventas WHERE fecha_fin >= ? AND fecha_fin <= ? ORDER BY fecha_fin ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fechaHoy);
    _argIndex = 2;
    _statement.bindString(_argIndex, fechaLimite);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"ventas"}, new Callable<List<Venta>>() {
      @Override
      @NonNull
      public List<Venta> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCuentaId = CursorUtil.getColumnIndexOrThrow(_cursor, "cuenta_id");
          final int _cursorIndexOfNumeroCelularCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "numero_celular_cliente");
          final int _cursorIndexOfCantidadPerfiles = CursorUtil.getColumnIndexOrThrow(_cursor, "cantidad_perfiles");
          final int _cursorIndexOfPrecioVentaPorPerfil = CursorUtil.getColumnIndexOrThrow(_cursor, "precio_venta_por_perfil");
          final int _cursorIndexOfDuracionDias = CursorUtil.getColumnIndexOrThrow(_cursor, "duracion_dias");
          final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_inicio");
          final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_fin");
          final List<Venta> _result = new ArrayList<Venta>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Venta _item;
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final int _tmpCuentaId;
            _tmpCuentaId = _cursor.getInt(_cursorIndexOfCuentaId);
            final String _tmpNumeroCelularCliente;
            _tmpNumeroCelularCliente = _cursor.getString(_cursorIndexOfNumeroCelularCliente);
            final int _tmpCantidadPerfiles;
            _tmpCantidadPerfiles = _cursor.getInt(_cursorIndexOfCantidadPerfiles);
            final double _tmpPrecioVentaPorPerfil;
            _tmpPrecioVentaPorPerfil = _cursor.getDouble(_cursorIndexOfPrecioVentaPorPerfil);
            final int _tmpDuracionDias;
            _tmpDuracionDias = _cursor.getInt(_cursorIndexOfDuracionDias);
            final String _tmpFechaInicio;
            _tmpFechaInicio = _cursor.getString(_cursorIndexOfFechaInicio);
            final String _tmpFechaFin;
            _tmpFechaFin = _cursor.getString(_cursorIndexOfFechaFin);
            _item = new Venta(_tmpId,_tmpCuentaId,_tmpNumeroCelularCliente,_tmpCantidadPerfiles,_tmpPrecioVentaPorPerfil,_tmpDuracionDias,_tmpFechaInicio,_tmpFechaFin);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<CuentaConVentas>> getCuentasConVentas() {
    final String _sql = "SELECT * FROM cuentas ORDER BY id DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"ventas",
        "cuentas"}, new Callable<List<CuentaConVentas>>() {
      @Override
      @NonNull
      public List<CuentaConVentas> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfCorreo = CursorUtil.getColumnIndexOrThrow(_cursor, "correo");
            final int _cursorIndexOfContrasenaCorreo = CursorUtil.getColumnIndexOrThrow(_cursor, "contrasena_correo");
            final int _cursorIndexOfContrasenaPlataforma = CursorUtil.getColumnIndexOrThrow(_cursor, "contrasena_plataforma");
            final int _cursorIndexOfTipoCuenta = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo_cuenta");
            final int _cursorIndexOfCostoCompra = CursorUtil.getColumnIndexOrThrow(_cursor, "costo_compra");
            final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_creacion");
            final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
            final LongSparseArray<ArrayList<Venta>> _collectionVentas = new LongSparseArray<ArrayList<Venta>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionVentas.containsKey(_tmpKey)) {
                _collectionVentas.put(_tmpKey, new ArrayList<Venta>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipventasAscomGestorcuentasAppDataEntityVenta(_collectionVentas);
            final List<CuentaConVentas> _result = new ArrayList<CuentaConVentas>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final CuentaConVentas _item;
              final Cuenta _tmpCuenta;
              final int _tmpId;
              _tmpId = _cursor.getInt(_cursorIndexOfId);
              final String _tmpCorreo;
              _tmpCorreo = _cursor.getString(_cursorIndexOfCorreo);
              final String _tmpContrasenaCorreo;
              _tmpContrasenaCorreo = _cursor.getString(_cursorIndexOfContrasenaCorreo);
              final String _tmpContrasenaPlataforma;
              _tmpContrasenaPlataforma = _cursor.getString(_cursorIndexOfContrasenaPlataforma);
              final String _tmpTipoCuenta;
              _tmpTipoCuenta = _cursor.getString(_cursorIndexOfTipoCuenta);
              final double _tmpCostoCompra;
              _tmpCostoCompra = _cursor.getDouble(_cursorIndexOfCostoCompra);
              final String _tmpFechaCreacion;
              _tmpFechaCreacion = _cursor.getString(_cursorIndexOfFechaCreacion);
              final String _tmpEstado;
              _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
              _tmpCuenta = new Cuenta(_tmpId,_tmpCorreo,_tmpContrasenaCorreo,_tmpContrasenaPlataforma,_tmpTipoCuenta,_tmpCostoCompra,_tmpFechaCreacion,_tmpEstado);
              final ArrayList<Venta> _tmpVentasCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpVentasCollection = _collectionVentas.get(_tmpKey_1);
              _item = new CuentaConVentas(_tmpCuenta,_tmpVentasCollection);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<CuentaConVentas> getCuentaConVentas(final int cuentaId) {
    final String _sql = "SELECT * FROM cuentas WHERE id = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, cuentaId);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"ventas",
        "cuentas"}, new Callable<CuentaConVentas>() {
      @Override
      @Nullable
      public CuentaConVentas call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfCorreo = CursorUtil.getColumnIndexOrThrow(_cursor, "correo");
            final int _cursorIndexOfContrasenaCorreo = CursorUtil.getColumnIndexOrThrow(_cursor, "contrasena_correo");
            final int _cursorIndexOfContrasenaPlataforma = CursorUtil.getColumnIndexOrThrow(_cursor, "contrasena_plataforma");
            final int _cursorIndexOfTipoCuenta = CursorUtil.getColumnIndexOrThrow(_cursor, "tipo_cuenta");
            final int _cursorIndexOfCostoCompra = CursorUtil.getColumnIndexOrThrow(_cursor, "costo_compra");
            final int _cursorIndexOfFechaCreacion = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_creacion");
            final int _cursorIndexOfEstado = CursorUtil.getColumnIndexOrThrow(_cursor, "estado");
            final LongSparseArray<ArrayList<Venta>> _collectionVentas = new LongSparseArray<ArrayList<Venta>>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfId);
              if (!_collectionVentas.containsKey(_tmpKey)) {
                _collectionVentas.put(_tmpKey, new ArrayList<Venta>());
              }
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipventasAscomGestorcuentasAppDataEntityVenta(_collectionVentas);
            final CuentaConVentas _result;
            if (_cursor.moveToFirst()) {
              final Cuenta _tmpCuenta;
              final int _tmpId;
              _tmpId = _cursor.getInt(_cursorIndexOfId);
              final String _tmpCorreo;
              _tmpCorreo = _cursor.getString(_cursorIndexOfCorreo);
              final String _tmpContrasenaCorreo;
              _tmpContrasenaCorreo = _cursor.getString(_cursorIndexOfContrasenaCorreo);
              final String _tmpContrasenaPlataforma;
              _tmpContrasenaPlataforma = _cursor.getString(_cursorIndexOfContrasenaPlataforma);
              final String _tmpTipoCuenta;
              _tmpTipoCuenta = _cursor.getString(_cursorIndexOfTipoCuenta);
              final double _tmpCostoCompra;
              _tmpCostoCompra = _cursor.getDouble(_cursorIndexOfCostoCompra);
              final String _tmpFechaCreacion;
              _tmpFechaCreacion = _cursor.getString(_cursorIndexOfFechaCreacion);
              final String _tmpEstado;
              _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
              _tmpCuenta = new Cuenta(_tmpId,_tmpCorreo,_tmpContrasenaCorreo,_tmpContrasenaPlataforma,_tmpTipoCuenta,_tmpCostoCompra,_tmpFechaCreacion,_tmpEstado);
              final ArrayList<Venta> _tmpVentasCollection;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfId);
              _tmpVentasCollection = _collectionVentas.get(_tmpKey_1);
              _result = new CuentaConVentas(_tmpCuenta,_tmpVentasCollection);
            } else {
              _result = null;
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<VentaConCuenta>> getAllVentasConCuenta() {
    final String _sql = "SELECT * FROM ventas ORDER BY fecha_fin ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"cuentas",
        "ventas"}, new Callable<List<VentaConCuenta>>() {
      @Override
      @NonNull
      public List<VentaConCuenta> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfCuentaId = CursorUtil.getColumnIndexOrThrow(_cursor, "cuenta_id");
            final int _cursorIndexOfNumeroCelularCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "numero_celular_cliente");
            final int _cursorIndexOfCantidadPerfiles = CursorUtil.getColumnIndexOrThrow(_cursor, "cantidad_perfiles");
            final int _cursorIndexOfPrecioVentaPorPerfil = CursorUtil.getColumnIndexOrThrow(_cursor, "precio_venta_por_perfil");
            final int _cursorIndexOfDuracionDias = CursorUtil.getColumnIndexOrThrow(_cursor, "duracion_dias");
            final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_inicio");
            final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_fin");
            final LongSparseArray<Cuenta> _collectionCuenta = new LongSparseArray<Cuenta>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfCuentaId);
              _collectionCuenta.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcuentasAscomGestorcuentasAppDataEntityCuenta(_collectionCuenta);
            final List<VentaConCuenta> _result = new ArrayList<VentaConCuenta>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final VentaConCuenta _item;
              final Venta _tmpVenta;
              final int _tmpId;
              _tmpId = _cursor.getInt(_cursorIndexOfId);
              final int _tmpCuentaId;
              _tmpCuentaId = _cursor.getInt(_cursorIndexOfCuentaId);
              final String _tmpNumeroCelularCliente;
              _tmpNumeroCelularCliente = _cursor.getString(_cursorIndexOfNumeroCelularCliente);
              final int _tmpCantidadPerfiles;
              _tmpCantidadPerfiles = _cursor.getInt(_cursorIndexOfCantidadPerfiles);
              final double _tmpPrecioVentaPorPerfil;
              _tmpPrecioVentaPorPerfil = _cursor.getDouble(_cursorIndexOfPrecioVentaPorPerfil);
              final int _tmpDuracionDias;
              _tmpDuracionDias = _cursor.getInt(_cursorIndexOfDuracionDias);
              final String _tmpFechaInicio;
              _tmpFechaInicio = _cursor.getString(_cursorIndexOfFechaInicio);
              final String _tmpFechaFin;
              _tmpFechaFin = _cursor.getString(_cursorIndexOfFechaFin);
              _tmpVenta = new Venta(_tmpId,_tmpCuentaId,_tmpNumeroCelularCliente,_tmpCantidadPerfiles,_tmpPrecioVentaPorPerfil,_tmpDuracionDias,_tmpFechaInicio,_tmpFechaFin);
              final Cuenta _tmpCuenta;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfCuentaId);
              _tmpCuenta = _collectionCuenta.get(_tmpKey_1);
              _item = new VentaConCuenta(_tmpVenta,_tmpCuenta);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<VentaConCuenta>> getVentasConCuentaPorVencer(final String fechaHoy,
      final String fechaLimite) {
    final String _sql = "SELECT * FROM ventas WHERE fecha_fin >= ? AND fecha_fin <= ? ORDER BY fecha_fin ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 2);
    int _argIndex = 1;
    _statement.bindString(_argIndex, fechaHoy);
    _argIndex = 2;
    _statement.bindString(_argIndex, fechaLimite);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"cuentas",
        "ventas"}, new Callable<List<VentaConCuenta>>() {
      @Override
      @NonNull
      public List<VentaConCuenta> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfCuentaId = CursorUtil.getColumnIndexOrThrow(_cursor, "cuenta_id");
            final int _cursorIndexOfNumeroCelularCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "numero_celular_cliente");
            final int _cursorIndexOfCantidadPerfiles = CursorUtil.getColumnIndexOrThrow(_cursor, "cantidad_perfiles");
            final int _cursorIndexOfPrecioVentaPorPerfil = CursorUtil.getColumnIndexOrThrow(_cursor, "precio_venta_por_perfil");
            final int _cursorIndexOfDuracionDias = CursorUtil.getColumnIndexOrThrow(_cursor, "duracion_dias");
            final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_inicio");
            final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_fin");
            final LongSparseArray<Cuenta> _collectionCuenta = new LongSparseArray<Cuenta>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfCuentaId);
              _collectionCuenta.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcuentasAscomGestorcuentasAppDataEntityCuenta(_collectionCuenta);
            final List<VentaConCuenta> _result = new ArrayList<VentaConCuenta>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final VentaConCuenta _item;
              final Venta _tmpVenta;
              final int _tmpId;
              _tmpId = _cursor.getInt(_cursorIndexOfId);
              final int _tmpCuentaId;
              _tmpCuentaId = _cursor.getInt(_cursorIndexOfCuentaId);
              final String _tmpNumeroCelularCliente;
              _tmpNumeroCelularCliente = _cursor.getString(_cursorIndexOfNumeroCelularCliente);
              final int _tmpCantidadPerfiles;
              _tmpCantidadPerfiles = _cursor.getInt(_cursorIndexOfCantidadPerfiles);
              final double _tmpPrecioVentaPorPerfil;
              _tmpPrecioVentaPorPerfil = _cursor.getDouble(_cursorIndexOfPrecioVentaPorPerfil);
              final int _tmpDuracionDias;
              _tmpDuracionDias = _cursor.getInt(_cursorIndexOfDuracionDias);
              final String _tmpFechaInicio;
              _tmpFechaInicio = _cursor.getString(_cursorIndexOfFechaInicio);
              final String _tmpFechaFin;
              _tmpFechaFin = _cursor.getString(_cursorIndexOfFechaFin);
              _tmpVenta = new Venta(_tmpId,_tmpCuentaId,_tmpNumeroCelularCliente,_tmpCantidadPerfiles,_tmpPrecioVentaPorPerfil,_tmpDuracionDias,_tmpFechaInicio,_tmpFechaFin);
              final Cuenta _tmpCuenta;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfCuentaId);
              _tmpCuenta = _collectionCuenta.get(_tmpKey_1);
              _item = new VentaConCuenta(_tmpVenta,_tmpCuenta);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Flow<List<VentaConCuenta>> getVentasConCuentaPorCliente(final String numeroCelular) {
    final String _sql = "SELECT * FROM ventas WHERE numero_celular_cliente LIKE '%' || ? || '%' ORDER BY fecha_inicio DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, numeroCelular);
    return CoroutinesRoom.createFlow(__db, true, new String[] {"cuentas",
        "ventas"}, new Callable<List<VentaConCuenta>>() {
      @Override
      @NonNull
      public List<VentaConCuenta> call() throws Exception {
        __db.beginTransaction();
        try {
          final Cursor _cursor = DBUtil.query(__db, _statement, true, null);
          try {
            final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
            final int _cursorIndexOfCuentaId = CursorUtil.getColumnIndexOrThrow(_cursor, "cuenta_id");
            final int _cursorIndexOfNumeroCelularCliente = CursorUtil.getColumnIndexOrThrow(_cursor, "numero_celular_cliente");
            final int _cursorIndexOfCantidadPerfiles = CursorUtil.getColumnIndexOrThrow(_cursor, "cantidad_perfiles");
            final int _cursorIndexOfPrecioVentaPorPerfil = CursorUtil.getColumnIndexOrThrow(_cursor, "precio_venta_por_perfil");
            final int _cursorIndexOfDuracionDias = CursorUtil.getColumnIndexOrThrow(_cursor, "duracion_dias");
            final int _cursorIndexOfFechaInicio = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_inicio");
            final int _cursorIndexOfFechaFin = CursorUtil.getColumnIndexOrThrow(_cursor, "fecha_fin");
            final LongSparseArray<Cuenta> _collectionCuenta = new LongSparseArray<Cuenta>();
            while (_cursor.moveToNext()) {
              final long _tmpKey;
              _tmpKey = _cursor.getLong(_cursorIndexOfCuentaId);
              _collectionCuenta.put(_tmpKey, null);
            }
            _cursor.moveToPosition(-1);
            __fetchRelationshipcuentasAscomGestorcuentasAppDataEntityCuenta(_collectionCuenta);
            final List<VentaConCuenta> _result = new ArrayList<VentaConCuenta>(_cursor.getCount());
            while (_cursor.moveToNext()) {
              final VentaConCuenta _item;
              final Venta _tmpVenta;
              final int _tmpId;
              _tmpId = _cursor.getInt(_cursorIndexOfId);
              final int _tmpCuentaId;
              _tmpCuentaId = _cursor.getInt(_cursorIndexOfCuentaId);
              final String _tmpNumeroCelularCliente;
              _tmpNumeroCelularCliente = _cursor.getString(_cursorIndexOfNumeroCelularCliente);
              final int _tmpCantidadPerfiles;
              _tmpCantidadPerfiles = _cursor.getInt(_cursorIndexOfCantidadPerfiles);
              final double _tmpPrecioVentaPorPerfil;
              _tmpPrecioVentaPorPerfil = _cursor.getDouble(_cursorIndexOfPrecioVentaPorPerfil);
              final int _tmpDuracionDias;
              _tmpDuracionDias = _cursor.getInt(_cursorIndexOfDuracionDias);
              final String _tmpFechaInicio;
              _tmpFechaInicio = _cursor.getString(_cursorIndexOfFechaInicio);
              final String _tmpFechaFin;
              _tmpFechaFin = _cursor.getString(_cursorIndexOfFechaFin);
              _tmpVenta = new Venta(_tmpId,_tmpCuentaId,_tmpNumeroCelularCliente,_tmpCantidadPerfiles,_tmpPrecioVentaPorPerfil,_tmpDuracionDias,_tmpFechaInicio,_tmpFechaFin);
              final Cuenta _tmpCuenta;
              final long _tmpKey_1;
              _tmpKey_1 = _cursor.getLong(_cursorIndexOfCuentaId);
              _tmpCuenta = _collectionCuenta.get(_tmpKey_1);
              _item = new VentaConCuenta(_tmpVenta,_tmpCuenta);
              _result.add(_item);
            }
            __db.setTransactionSuccessful();
            return _result;
          } finally {
            _cursor.close();
          }
        } finally {
          __db.endTransaction();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private void __fetchRelationshipventasAscomGestorcuentasAppDataEntityVenta(
      @NonNull final LongSparseArray<ArrayList<Venta>> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, true, (map) -> {
        __fetchRelationshipventasAscomGestorcuentasAppDataEntityVenta(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`cuenta_id`,`numero_celular_cliente`,`cantidad_perfiles`,`precio_venta_por_perfil`,`duracion_dias`,`fecha_inicio`,`fecha_fin` FROM `ventas` WHERE `cuenta_id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "cuenta_id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfCuentaId = 1;
      final int _cursorIndexOfNumeroCelularCliente = 2;
      final int _cursorIndexOfCantidadPerfiles = 3;
      final int _cursorIndexOfPrecioVentaPorPerfil = 4;
      final int _cursorIndexOfDuracionDias = 5;
      final int _cursorIndexOfFechaInicio = 6;
      final int _cursorIndexOfFechaFin = 7;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        final ArrayList<Venta> _tmpRelation = _map.get(_tmpKey);
        if (_tmpRelation != null) {
          final Venta _item_1;
          final int _tmpId;
          _tmpId = _cursor.getInt(_cursorIndexOfId);
          final int _tmpCuentaId;
          _tmpCuentaId = _cursor.getInt(_cursorIndexOfCuentaId);
          final String _tmpNumeroCelularCliente;
          _tmpNumeroCelularCliente = _cursor.getString(_cursorIndexOfNumeroCelularCliente);
          final int _tmpCantidadPerfiles;
          _tmpCantidadPerfiles = _cursor.getInt(_cursorIndexOfCantidadPerfiles);
          final double _tmpPrecioVentaPorPerfil;
          _tmpPrecioVentaPorPerfil = _cursor.getDouble(_cursorIndexOfPrecioVentaPorPerfil);
          final int _tmpDuracionDias;
          _tmpDuracionDias = _cursor.getInt(_cursorIndexOfDuracionDias);
          final String _tmpFechaInicio;
          _tmpFechaInicio = _cursor.getString(_cursorIndexOfFechaInicio);
          final String _tmpFechaFin;
          _tmpFechaFin = _cursor.getString(_cursorIndexOfFechaFin);
          _item_1 = new Venta(_tmpId,_tmpCuentaId,_tmpNumeroCelularCliente,_tmpCantidadPerfiles,_tmpPrecioVentaPorPerfil,_tmpDuracionDias,_tmpFechaInicio,_tmpFechaFin);
          _tmpRelation.add(_item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }

  private void __fetchRelationshipcuentasAscomGestorcuentasAppDataEntityCuenta(
      @NonNull final LongSparseArray<Cuenta> _map) {
    if (_map.isEmpty()) {
      return;
    }
    if (_map.size() > RoomDatabase.MAX_BIND_PARAMETER_CNT) {
      RelationUtil.recursiveFetchLongSparseArray(_map, false, (map) -> {
        __fetchRelationshipcuentasAscomGestorcuentasAppDataEntityCuenta(map);
        return Unit.INSTANCE;
      });
      return;
    }
    final StringBuilder _stringBuilder = StringUtil.newStringBuilder();
    _stringBuilder.append("SELECT `id`,`correo`,`contrasena_correo`,`contrasena_plataforma`,`tipo_cuenta`,`costo_compra`,`fecha_creacion`,`estado` FROM `cuentas` WHERE `id` IN (");
    final int _inputSize = _map.size();
    StringUtil.appendPlaceholders(_stringBuilder, _inputSize);
    _stringBuilder.append(")");
    final String _sql = _stringBuilder.toString();
    final int _argCount = 0 + _inputSize;
    final RoomSQLiteQuery _stmt = RoomSQLiteQuery.acquire(_sql, _argCount);
    int _argIndex = 1;
    for (int i = 0; i < _map.size(); i++) {
      final long _item = _map.keyAt(i);
      _stmt.bindLong(_argIndex, _item);
      _argIndex++;
    }
    final Cursor _cursor = DBUtil.query(__db, _stmt, false, null);
    try {
      final int _itemKeyIndex = CursorUtil.getColumnIndex(_cursor, "id");
      if (_itemKeyIndex == -1) {
        return;
      }
      final int _cursorIndexOfId = 0;
      final int _cursorIndexOfCorreo = 1;
      final int _cursorIndexOfContrasenaCorreo = 2;
      final int _cursorIndexOfContrasenaPlataforma = 3;
      final int _cursorIndexOfTipoCuenta = 4;
      final int _cursorIndexOfCostoCompra = 5;
      final int _cursorIndexOfFechaCreacion = 6;
      final int _cursorIndexOfEstado = 7;
      while (_cursor.moveToNext()) {
        final long _tmpKey;
        _tmpKey = _cursor.getLong(_itemKeyIndex);
        if (_map.containsKey(_tmpKey)) {
          final Cuenta _item_1;
          final int _tmpId;
          _tmpId = _cursor.getInt(_cursorIndexOfId);
          final String _tmpCorreo;
          _tmpCorreo = _cursor.getString(_cursorIndexOfCorreo);
          final String _tmpContrasenaCorreo;
          _tmpContrasenaCorreo = _cursor.getString(_cursorIndexOfContrasenaCorreo);
          final String _tmpContrasenaPlataforma;
          _tmpContrasenaPlataforma = _cursor.getString(_cursorIndexOfContrasenaPlataforma);
          final String _tmpTipoCuenta;
          _tmpTipoCuenta = _cursor.getString(_cursorIndexOfTipoCuenta);
          final double _tmpCostoCompra;
          _tmpCostoCompra = _cursor.getDouble(_cursorIndexOfCostoCompra);
          final String _tmpFechaCreacion;
          _tmpFechaCreacion = _cursor.getString(_cursorIndexOfFechaCreacion);
          final String _tmpEstado;
          _tmpEstado = _cursor.getString(_cursorIndexOfEstado);
          _item_1 = new Cuenta(_tmpId,_tmpCorreo,_tmpContrasenaCorreo,_tmpContrasenaPlataforma,_tmpTipoCuenta,_tmpCostoCompra,_tmpFechaCreacion,_tmpEstado);
          _map.put(_tmpKey, _item_1);
        }
      }
    } finally {
      _cursor.close();
    }
  }
}

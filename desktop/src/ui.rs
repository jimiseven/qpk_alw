pub const INDEX_HTML: &str = r#"<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Gestor de Cuentas y Perfiles</title>
  <style>
    :root {
      --bg: #121214;
      --surface: #1e1e24;
      --surface-light: #282832;
      --primary: #7c4dff;
      --primary-hover: #651fff;
      --accent: #00e5ff;
      --success: #00e676;
      --warning: #ffab00;
      --danger: #ff5252;
      --text: #f0f0f5;
      --text-muted: #9e9ea7;
      --border: #32323f;
      --radius: 10px;
    }

    * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }
    body { background-color: var(--bg); color: var(--text); overflow-x: hidden; height: 100vh; display: flex; flex-direction: column; }

    /* Header */
    header {
      background-color: var(--surface);
      border-bottom: 1px solid var(--border);
      padding: 12px 24px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      user-select: none;
    }
    .brand { display: flex; align-items: center; gap: 12px; }
    .brand svg { width: 32px; height: 32px; fill: var(--primary); }
    .brand h1 { font-size: 1.15rem; font-weight: 700; letter-spacing: 0.5px; }
    .brand span { font-size: 0.75rem; color: var(--success); background: rgba(0,230,118,0.1); padding: 3px 8px; border-radius: 12px; border: 1px solid rgba(0,230,118,0.3); }

    .header-actions { display: flex; gap: 10px; align-items: center; }
    button {
      background: var(--surface-light);
      color: var(--text);
      border: 1px solid var(--border);
      padding: 8px 16px;
      border-radius: var(--radius);
      cursor: pointer;
      font-size: 0.85rem;
      font-weight: 600;
      transition: all 0.2s;
      display: inline-flex;
      align-items: center;
      gap: 6px;
    }
    button:hover { background: var(--border); }
    button.btn-primary { background: var(--primary); border-color: var(--primary); color: #fff; }
    button.btn-primary:hover { background: var(--primary-hover); }
    button.btn-success { background: var(--success); border-color: var(--success); color: #000; }
    button.btn-danger { background: rgba(255,82,82,0.1); border-color: var(--danger); color: var(--danger); }
    button.btn-danger:hover { background: var(--danger); color: #fff; }
    button.btn-sm { padding: 4px 10px; font-size: 0.78rem; }

    /* Navigation Bar */
    nav {
      background-color: var(--surface);
      border-bottom: 1px solid var(--border);
      padding: 0 24px;
      display: flex;
      gap: 20px;
    }
    .nav-tab {
      padding: 14px 4px;
      color: var(--text-muted);
      cursor: pointer;
      font-weight: 600;
      font-size: 0.9rem;
      border-bottom: 3px solid transparent;
      transition: all 0.2s;
      display: flex;
      align-items: center;
      gap: 8px;
    }
    .nav-tab:hover { color: var(--text); }
    .nav-tab.active { color: var(--accent); border-bottom-color: var(--accent); }

    /* Main Content */
    main {
      flex: 1;
      padding: 24px;
      overflow-y: auto;
    }
    .tab-pane { display: none; }
    .tab-pane.active { display: block; animation: fadeIn 0.15s ease-in-out; }

    @keyframes fadeIn { from { opacity: 0; transform: translateY(4px); } to { opacity: 1; transform: translateY(0); } }

    /* Dashboard Cards */
    .kpi-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
      gap: 16px;
      margin-bottom: 24px;
    }
    .kpi-card {
      background: var(--surface);
      border: 1px solid var(--border);
      padding: 20px;
      border-radius: var(--radius);
      box-shadow: 0 4px 12px rgba(0,0,0,0.15);
    }
    .kpi-card .label { font-size: 0.82rem; color: var(--text-muted); text-transform: uppercase; letter-spacing: 0.5px; margin-bottom: 6px; }
    .kpi-card .value { font-size: 1.8rem; font-weight: 700; color: #fff; }
    .kpi-card.gain .value { color: var(--success); }
    .kpi-card.cost .value { color: var(--danger); }
    .kpi-card.roi .value { color: var(--accent); }

    /* Alert Banner */
    .alert-banner {
      background: rgba(255, 171, 0, 0.12);
      border: 1px solid var(--warning);
      color: #ffca28;
      padding: 14px 20px;
      border-radius: var(--radius);
      margin-bottom: 24px;
      display: flex;
      align-items: center;
      justify-content: space-between;
      font-weight: 600;
    }

    /* Tables */
    .table-container {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      overflow: hidden;
      box-shadow: 0 4px 16px rgba(0,0,0,0.2);
    }
    .table-toolbar {
      padding: 16px 20px;
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: 16px;
      border-bottom: 1px solid var(--border);
    }
    .search-box {
      position: relative;
      flex: 1;
      max-width: 360px;
    }
    .search-box input {
      width: 100%;
      background: var(--bg);
      border: 1px solid var(--border);
      color: var(--text);
      padding: 8px 36px 8px 12px;
      border-radius: var(--radius);
      font-size: 0.88rem;
      outline: none;
    }
    .search-box input:focus { border-color: var(--primary); }
    .search-box .clear-btn {
      position: absolute;
      right: 8px;
      top: 50%;
      transform: translateY(-50%);
      cursor: pointer;
      color: var(--text-muted);
      background: none;
      border: none;
      padding: 4px;
    }

    table { width: 100%; border-collapse: collapse; text-align: left; font-size: 0.88rem; }
    th {
      background: var(--surface-light);
      color: var(--text-muted);
      padding: 12px 16px;
      font-weight: 600;
      border-bottom: 1px solid var(--border);
    }
    td { padding: 12px 16px; border-bottom: 1px solid var(--border); vertical-align: middle; }
    tr:last-child td { border-bottom: none; }
    tr:hover td { background: rgba(255,255,255,0.02); }

    .badge {
      display: inline-block;
      padding: 3px 8px;
      border-radius: 6px;
      font-size: 0.75rem;
      font-weight: 700;
      letter-spacing: 0.3px;
    }
    .badge-success { background: rgba(0,230,118,0.15); color: var(--success); }
    .badge-warning { background: rgba(255,171,0,0.15); color: var(--warning); }
    .badge-danger { background: rgba(255,82,82,0.15); color: var(--danger); }
    .badge-info { background: rgba(0,229,255,0.15); color: var(--accent); }

    /* Modal */
    .modal-overlay {
      display: none;
      position: fixed;
      top: 0; left: 0; width: 100%; height: 100%;
      background: rgba(0,0,0,0.7);
      backdrop-filter: blur(4px);
      z-index: 1000;
      align-items: center;
      justify-content: center;
    }
    .modal-overlay.active { display: flex; }
    .modal-card {
      background: var(--surface);
      border: 1px solid var(--border);
      width: 100%;
      max-width: 500px;
      border-radius: var(--radius);
      overflow: hidden;
      box-shadow: 0 10px 30px rgba(0,0,0,0.5);
    }
    .modal-header {
      padding: 16px 20px;
      border-bottom: 1px solid var(--border);
      display: flex;
      justify-content: space-between;
      align-items: center;
    }
    .modal-body { padding: 20px; max-height: 75vh; overflow-y: auto; }
    .form-group { margin-bottom: 14px; }
    .form-group label { display: block; font-size: 0.8rem; color: var(--text-muted); margin-bottom: 6px; font-weight: 600; }
    .form-group input, .form-group select, .form-group textarea {
      width: 100%;
      background: var(--bg);
      border: 1px solid var(--border);
      color: var(--text);
      padding: 9px 12px;
      border-radius: var(--radius);
      font-size: 0.9rem;
      outline: none;
    }
    .form-group input:focus, .form-group select:focus, .form-group textarea:focus { border-color: var(--primary); }
    .modal-footer {
      padding: 14px 20px;
      border-top: 1px solid var(--border);
      display: flex;
      justify-content: flex-end;
      gap: 10px;
      background: var(--surface-light);
    }

    /* Template WhatsApp */
    .template-box {
      background: var(--surface);
      border: 1px solid var(--border);
      border-radius: var(--radius);
      padding: 24px;
      max-width: 700px;
    }
    .template-box textarea {
      width: 100%;
      height: 180px;
      background: var(--bg);
      border: 1px solid var(--border);
      color: var(--text);
      padding: 12px;
      border-radius: var(--radius);
      font-family: monospace;
      font-size: 0.9rem;
      margin-bottom: 16px;
    }
  </style>
</head>
<body>

  <!-- Header -->
  <header>
    <div class="brand">
      <svg viewBox="0 0 24 24"><path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm-5-9h10v2H7z"/></svg>
      <div>
        <h1>Gestor de Cuentas</h1>
        <small style="color:var(--text-muted)">Versión Desktop (Windows Nativo)</small>
      </div>
      <span>100% Offline • Persistente</span>
    </div>
    <div class="header-actions">
      <input type="file" id="fileInput" accept=".json" style="display:none" onchange="handleFileSelect(event)">
      <button onclick="document.getElementById('fileInput').click()">📥 Importar Backup de Celular</button>
      <button class="btn-success" onclick="exportBackup()">📤 Descargar Backup para Celular</button>
    </div>
  </header>

  <!-- Navigation -->
  <nav>
    <div class="nav-tab active" onclick="switchTab('dashboard')">📊 Dashboard</div>
    <div class="nav-tab" onclick="switchTab('cuentas')">💼 Cuentas Maestras</div>
    <div class="nav-tab" onclick="switchTab('ventas')">🏷️ Ventas y Perfiles</div>
    <div class="nav-tab" onclick="switchTab('clientes')">👥 Clientes</div>
    <div class="nav-tab" onclick="switchTab('plantilla')">💬 Plantilla WhatsApp</div>
  </nav>

  <!-- Main Content -->
  <main>
    <!-- TAB: DASHBOARD -->
    <div id="tab-dashboard" class="tab-pane active">
      <div id="alertBanner" class="alert-banner" style="display:none">
        <span id="alertText">⚠️ ¡Atención! Cuentas o perfiles vencen hoy.</span>
        <button class="btn-sm btn-primary" onclick="switchTab('ventas'); filterVentas('hoy')">Ver Vencimientos</button>
      </div>

      <div class="kpi-grid">
        <div class="kpi-card cost">
          <div class="label">Inversión en Cuentas</div>
          <div class="value" id="kpiInversion">Bs. 0.00</div>
        </div>
        <div class="kpi-card gain">
          <div class="label">Ingresos por Ventas</div>
          <div class="value" id="kpiIngresos">Bs. 0.00</div>
        </div>
        <div class="kpi-card gain">
          <div class="label">Ganancia Neta</div>
          <div class="value" id="kpiGanancia">Bs. 0.00</div>
        </div>
        <div class="kpi-card roi">
          <div class="label">Retorno de Inversión (ROI)</div>
          <div class="value" id="kpiRoi">0.0%</div>
        </div>
      </div>

      <div class="table-container">
        <div class="table-toolbar">
          <h3>⚡ Resumen Rápido de Inventario</h3>
          <button class="btn-primary" onclick="openNuevaVentaModal()">+ Nueva Venta</button>
        </div>
        <table>
          <thead>
            <tr>
              <th>Total Cuentas Registradas</th>
              <th>Cuentas Activas</th>
              <th>Total Ventas Realizadas</th>
              <th>Perfiles Activos Actualmente</th>
            </tr>
          </thead>
          <tbody>
            <tr>
              <td id="dashTotalCuentas" style="font-weight:700">0</td>
              <td id="dashCuentasActivas" style="color:var(--success);font-weight:700">0</td>
              <td id="dashTotalVentas" style="font-weight:700">0</td>
              <td id="dashVentasActivas" style="color:var(--accent);font-weight:700">0</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- TAB: CUENTAS MAESTRAS -->
    <div id="tab-cuentas" class="tab-pane">
      <div class="table-container">
        <div class="table-toolbar">
          <div class="search-box">
            <input type="text" id="searchCuentas" placeholder="Buscar cuenta por correo o servicio..." oninput="renderCuentas()">
            <button class="clear-btn" onclick="document.getElementById('searchCuentas').value=''; renderCuentas()">✕</button>
          </div>
          <button class="btn-primary" onclick="openNuevaCuentaModal()">+ Nueva Cuenta Maestra</button>
        </div>
        <table>
          <thead>
            <tr>
              <th>ID</th>
              <th>Servicio</th>
              <th>Correo</th>
              <th>Clave Plataforma</th>
              <th>Costo</th>
              <th>Ganancia Generada</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody id="cuentasTableBody"></tbody>
        </table>
      </div>
    </div>

    <!-- TAB: VENTAS -->
    <div id="tab-ventas" class="tab-pane">
      <div class="table-container">
        <div class="table-toolbar">
          <div style="display:flex; gap:10px;">
            <button id="filterTodas" class="btn-sm btn-primary" onclick="filterVentas('todas')">Todas</button>
            <button id="filterActivas" class="btn-sm" onclick="filterVentas('activas')">Activas</button>
            <button id="filterVencidas" class="btn-sm" onclick="filterVentas('vencidas')">Vencidas</button>
            <button id="filterHoy" class="btn-sm" onclick="filterVentas('hoy')">Vencen Hoy</button>
          </div>
          <div class="search-box">
            <input type="text" id="searchVentas" placeholder="Buscar por celular de cliente..." oninput="renderVentas()">
            <button class="clear-btn" onclick="document.getElementById('searchVentas').value=''; renderVentas()">✕</button>
          </div>
          <button class="btn-primary" onclick="openNuevaVentaModal()">+ Nueva Venta</button>
        </div>
        <table>
          <thead>
            <tr>
              <th>Cliente</th>
              <th>Servicio</th>
              <th>Perfiles</th>
              <th>Precio Unit.</th>
              <th>Total</th>
              <th>Inicio</th>
              <th>Vencimiento</th>
              <th>Días Restantes</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody id="ventasTableBody"></tbody>
        </table>
      </div>
    </div>

    <!-- TAB: CLIENTES -->
    <div id="tab-clientes" class="tab-pane">
      <div class="table-container">
        <div class="table-toolbar">
          <div class="search-box" style="max-width:450px">
            <input type="text" id="searchClienteHistorial" placeholder="Ingresa número (ej: +591 76992490)..." oninput="renderClientes()">
            <button class="clear-btn" onclick="document.getElementById('searchClienteHistorial').value=''; renderClientes()">✕</button>
          </div>
          <small style="color:var(--text-muted)">Limpia prefijos internacionales automáticamente</small>
        </div>
        <table>
          <thead>
            <tr>
              <th>Celular Cliente</th>
              <th>Total Compras</th>
              <th>Total Gastado</th>
              <th>Servicios Comprados</th>
              <th>Acciones</th>
            </tr>
          </thead>
          <tbody id="clientesTableBody"></tbody>
        </table>
      </div>
    </div>

    <!-- TAB: PLANTILLA -->
    <div id="tab-plantilla" class="tab-pane">
      <div class="template-box">
        <h3>💬 Plantilla de Mensaje para WhatsApp</h3>
        <p style="color:var(--text-muted); font-size:0.85rem; margin:8px 0 16px;">
          Esta plantilla se utilizará cuando pulses el botón "Copiar Mensaje" en una venta. Puedes personalizarla con las siguientes etiquetas:
          <br><code>{servicio}</code>, <code>{cuenta}</code>, <code>{clave}</code>, <code>{perfil}</code>, <code>{pin}</code>, <code>{fecha_fin}</code>
        </p>
        <textarea id="templateText"></textarea>
        <button class="btn-primary" onclick="saveTemplate()">Guardar Plantilla</button>
      </div>
    </div>
  </main>

  <!-- MODAL: NUEVA CUENTA -->
  <div id="modalCuenta" class="modal-overlay">
    <div class="modal-card">
      <div class="modal-header">
        <h3 id="modalCuentaTitle">Nueva Cuenta Maestra</h3>
        <button onclick="closeModal('modalCuenta')">✕</button>
      </div>
      <div class="modal-body">
        <input type="hidden" id="cuentaId">
        <div class="form-group">
          <label>Servicio / Plataforma</label>
          <input type="text" id="cuentaServicio" placeholder="Ej: Netflix, Spotify, ChatGPT, Gemini">
        </div>
        <div class="form-group">
          <label>Correo Electrónico</label>
          <input type="email" id="cuentaCorreo" placeholder="correo@ejemplo.com">
        </div>
        <div class="form-group">
          <label>Contraseña de la Plataforma</label>
          <input type="text" id="cuentaClavePlataforma" placeholder="Contraseña de acceso">
        </div>
        <div class="form-group">
          <label>Contraseña del Correo (Opcional)</label>
          <input type="text" id="cuentaClaveCorreo" placeholder="Contraseña del correo original">
        </div>
        <div class="form-group">
          <label>Costo de Compra (Bs.)</label>
          <input type="number" step="0.5" id="cuentaCosto" placeholder="50.0">
        </div>
        <div class="form-group">
          <label>Fecha de Compra</label>
          <input type="date" id="cuentaFecha">
        </div>
      </div>
      <div class="modal-footer">
        <button onclick="closeModal('modalCuenta')">Cancelar</button>
        <button class="btn-primary" onclick="saveCuenta()">Guardar Cuenta</button>
      </div>
    </div>
  </div>

  <!-- MODAL: NUEVA VENTA -->
  <div id="modalVenta" class="modal-overlay">
    <div class="modal-card">
      <div class="modal-header">
        <h3 id="modalVentaTitle">Registrar Venta de Perfil</h3>
        <button onclick="closeModal('modalVenta')">✕</button>
      </div>
      <div class="modal-body">
        <input type="hidden" id="ventaId">
        <div class="form-group">
          <label>Celular del Cliente</label>
          <input type="text" id="ventaCelular" placeholder="Ej: 76992490">
        </div>
        <div class="form-group">
          <label>Seleccionar Cuenta Maestra</label>
          <select id="ventaCuentaId"></select>
        </div>
        <div class="form-group">
          <label>Cantidad de Perfiles</label>
          <input type="number" id="ventaCantidad" value="1" min="1">
        </div>
        <div class="form-group">
          <label>Precio por Perfil (Bs.)</label>
          <input type="number" step="0.5" id="ventaPrecio" placeholder="15.0">
        </div>
        <div class="form-group">
          <label>Duración (Días)</label>
          <input type="number" id="ventaDias" value="30">
        </div>
        <div class="form-group">
          <label>Fecha de Inicio</label>
          <input type="date" id="ventaFechaInicio">
        </div>
      </div>
      <div class="modal-footer">
        <button onclick="closeModal('modalVenta')">Cancelar</button>
        <button class="btn-primary" onclick="saveVenta()">Guardar Venta</button>
      </div>
    </div>
  </div>

  <script>
    let appState = { cuentas: [], ventas: [], mensaje_template: null };
    let currentVentaFilter = 'todas';

    const DEFAULT_TEMPLATE = `👋 ¡Hola! Aquí tienes los accesos para tu cuenta de *{servicio}*:

📧 *Correo:* {cuenta}
🔑 *Contraseña:* {clave}
👤 *Perfil:* {perfil}
🔒 *PIN:* {pin}
📅 *Vencimiento:* {fecha_fin}

¡Gracias por tu preferencia! 🙌`;

    async function fetchState() {
      try {
        const res = await fetch('/api/state');
        appState = await res.json();
        renderAll();
      } catch (e) {
        console.error("Error cargando estado:", e);
      }
    }

    function renderAll() {
      renderDashboard();
      renderCuentas();
      renderVentas();
      renderClientes();
      renderTemplate();
    }

    function switchTab(tabId) {
      document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));
      document.querySelectorAll('.tab-pane').forEach(p => p.classList.remove('active'));
      event.currentTarget.classList.add('active');
      document.getElementById('tab-' + tabId).classList.add('active');
    }

    function renderDashboard() {
      let totalInv = 0;
      let totalIng = 0;
      let cuentasActivas = 0;
      let ventasActivas = 0;
      let vencenHoy = 0;
      const hoyStr = new Date().toISOString().split('T')[0];

      appState.cuentas.forEach(c => {
        totalInv += (c.costo_compra || 0);
        if ((c.estado || 'activa').toLowerCase() === 'activa') cuentasActivas++;
      });

      appState.ventas.forEach(v => {
        const total = (v.precio_venta_por_perfil || 0) * (v.cantidad_perfiles || 1);
        totalIng += total;
        const dias = calcularDiasRestantes(v.fecha_fin);
        if (dias >= 0) ventasActivas++;
        if (dias === 0) vencenHoy++;
      });

      const ganancia = totalIng - totalInv;
      const roi = totalInv > 0 ? ((ganancia / totalInv) * 100).toFixed(1) : '0.0';

      document.getElementById('kpiInversion').textContent = 'Bs. ' + totalInv.toFixed(2);
      document.getElementById('kpiIngresos').textContent = 'Bs. ' + totalIng.toFixed(2);
      document.getElementById('kpiGanancia').textContent = 'Bs. ' + ganancia.toFixed(2);
      document.getElementById('kpiRoi').textContent = roi + '%';

      document.getElementById('dashTotalCuentas').textContent = appState.cuentas.len ? appState.cuentas.length : appState.cuentas.length;
      document.getElementById('dashCuentasActivas').textContent = cuentasActivas;
      document.getElementById('dashTotalVentas').textContent = appState.ventas.length;
      document.getElementById('dashVentasActivas').textContent = ventasActivas;

      const alertBanner = document.getElementById('alertBanner');
      if (vencenHoy > 0) {
        alertBanner.style.display = 'flex';
        document.getElementById('alertText').textContent = `⚠️ ¡Atención! ${vencenHoy} perfil(es) vencen hoy. Toca para ver y renovar.`;
      } else {
        alertBanner.style.display = 'none';
      }
    }

    function renderCuentas() {
      const q = (document.getElementById('searchCuentas').value || '').toLowerCase();
      const tbody = document.getElementById('cuentasTableBody');
      tbody.innerHTML = '';

      const filtradas = appState.cuentas.filter(c =>
        (c.correo || '').toLowerCase().includes(q) || (c.tipo_cuenta || '').toLowerCase().includes(q)
      );

      if (filtradas.length === 0) {
        tbody.innerHTML = '<tr><td colspan="8" style="text-align:center;color:var(--text-muted);padding:30px;">No hay cuentas registradas.</td></tr>';
        return;
      }

      filtradas.forEach(c => {
        // Calcular ingresos de esta cuenta
        const ingresosCuenta = appState.ventas
          .filter(v => v.cuenta_id === c.id)
          .reduce((sum, v) => sum + (v.precio_venta_por_perfil * v.cantidad_perfiles), 0);
        const gananciaCuenta = ingresosCuenta - c.costo_compra;
        const recuperada = gananciaCuenta >= 0;

        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td>#${c.id}</td>
          <td><strong>${c.tipo_cuenta || 'Otro'}</strong></td>
          <td>${c.correo}</td>
          <td><code>${c.contrasena_plataforma}</code></td>
          <td>Bs. ${c.costo_compra.toFixed(2)}</td>
          <td>
            <span class="badge ${recuperada ? 'badge-success' : 'badge-warning'}">
              Bs. ${gananciaCuenta >= 0 ? '+' : ''}${gananciaCuenta.toFixed(2)} (${recuperada ? 'Ganancia' : 'Recuperando'})
            </span>
          </td>
          <td><span class="badge ${c.estado === 'activa' ? 'badge-info' : 'badge-danger'}">${c.estado}</span></td>
          <td>
            <button class="btn-sm" onclick="editCuenta(${c.id})">✏️</button>
            <button class="btn-sm btn-danger" onclick="deleteCuenta(${c.id})">🗑️</button>
          </td>
        `;
        tbody.appendChild(tr);
      });
    }

    function filterVentas(tipo) {
      currentVentaFilter = tipo;
      document.querySelectorAll('#tab-ventas .table-toolbar button').forEach(b => b.classList.remove('btn-primary'));
      if (tipo === 'todas') document.getElementById('filterTodas').classList.add('btn-primary');
      if (tipo === 'activas') document.getElementById('filterActivas').classList.add('btn-primary');
      if (tipo === 'vencidas') document.getElementById('filterVencidas').classList.add('btn-primary');
      if (tipo === 'hoy') document.getElementById('filterHoy').classList.add('btn-primary');
      renderVentas();
    }

    function renderVentas() {
      const q = (document.getElementById('searchVentas').value || '').toLowerCase();
      const tbody = document.getElementById('ventasTableBody');
      tbody.innerHTML = '';

      let list = appState.ventas.filter(v => (v.numero_celular_cliente || '').toLowerCase().includes(q));

      if (currentVentaFilter === 'activas') {
        list = list.filter(v => calcularDiasRestantes(v.fecha_fin) >= 0);
      } else if (currentVentaFilter === 'vencidas') {
        list = list.filter(v => calcularDiasRestantes(v.fecha_fin) < 0);
      } else if (currentVentaFilter === 'hoy') {
        list = list.filter(v => calcularDiasRestantes(v.fecha_fin) === 0);
      }

      if (list.length === 0) {
        tbody.innerHTML = '<tr><td colspan="9" style="text-align:center;color:var(--text-muted);padding:30px;">No se encontraron ventas para este filtro.</td></tr>';
        return;
      }

      list.forEach(v => {
        const cuenta = appState.cuentas.find(c => c.id === v.cuenta_id) || {};
        const dias = calcularDiasRestantes(v.fecha_fin);
        let badgeClass = 'badge-success';
        let diasText = `${dias} días`;

        if (dias < 0) {
          badgeClass = 'badge-danger';
          diasText = `Venció hace ${Math.abs(dias)} d`;
        } else if (dias === 0) {
          badgeClass = 'badge-warning';
          diasText = '¡Vence Hoy!';
        } else if (dias <= 3) {
          badgeClass = 'badge-warning';
          diasText = `${dias} días (Próximo)`;
        }

        const total = (v.precio_venta_por_perfil * v.cantidad_perfiles).toFixed(2);

        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td><strong>${v.numero_celular_cliente}</strong></td>
          <td>${cuenta.tipo_cuenta || 'Cuenta Eliminada'}</td>
          <td>${v.cantidad_perfiles}</td>
          <td>Bs. ${v.precio_venta_por_perfil.toFixed(2)}</td>
          <td><strong>Bs. ${total}</strong></td>
          <td>${v.fecha_inicio}</td>
          <td>${v.fecha_fin}</td>
          <td><span class="badge ${badgeClass}">${diasText}</span></td>
          <td>
            <button class="btn-sm btn-primary" title="Copiar mensaje WhatsApp" onclick="copyWhatsAppMsg(${v.id})">💬 Copiar</button>
            <button class="btn-sm" onclick="editVenta(${v.id})">✏️</button>
            <button class="btn-sm btn-danger" onclick="deleteVenta(${v.id})">🗑️</button>
          </td>
        `;
        tbody.appendChild(tr);
      });
    }

    function renderClientes() {
      let rawQ = document.getElementById('searchClienteHistorial').value || '';
      // Limpiar prefijo internacional como +591
      let q = rawQ.replace(/[^0-9]/g, '');
      if (q.startsWith('591') && q.length > 8) q = q.substring(3);

      const tbody = document.getElementById('clientesTableBody');
      tbody.innerHTML = '';

      // Agrupar ventas por celular
      const mapaClientes = {};
      appState.ventas.forEach(v => {
        const cel = (v.numero_celular_cliente || '').trim();
        if (!mapaClientes[cel]) mapaClientes[cel] = [];
        mapaClientes[cel].push(v);
      });

      let cels = Object.keys(mapaClientes);
      if (q.length > 0) {
        cels = cels.filter(c => c.replace(/[^0-9]/g, '').includes(q));
      }

      if (cels.length === 0) {
        tbody.innerHTML = '<tr><td colspan="5" style="text-align:center;color:var(--text-muted);padding:30px;">No se encontraron clientes con este número.</td></tr>';
        return;
      }

      cels.forEach(cel => {
        const ventasCliente = mapaClientes[cel];
        const totalGastado = ventasCliente.reduce((sum, v) => sum + (v.precio_venta_por_perfil * v.cantidad_perfiles), 0);
        const servicios = Array.from(new Set(ventasCliente.map(v => {
          const c = appState.cuentas.find(acc => acc.id === v.cuenta_id);
          return c ? c.tipo_cuenta : 'Servicio';
        }))).join(', ');

        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td><strong>${cel}</strong></td>
          <td>${ventasCliente.length} compra(s)</td>
          <td><strong style="color:var(--success)">Bs. ${totalGastado.toFixed(2)}</strong></td>
          <td>${servicios}</td>
          <td>
            <button class="btn-sm btn-primary" onclick="verHistorialCliente('${cel}')">Ver Compras</button>
          </td>
        `;
        tbody.appendChild(tr);
      });
    }

    function verHistorialCliente(cel) {
      document.getElementById('searchVentas').value = cel;
      switchTab('ventas');
      filterVentas('todas');
    }

    function renderTemplate() {
      const template = appState.mensaje_template || DEFAULT_TEMPLATE;
      document.getElementById('templateText').value = template;
    }

    async function saveTemplate() {
      const t = document.getElementById('templateText').value;
      await fetch('/api/save_template', { method: 'POST', body: t });
      alert('¡Plantilla de WhatsApp guardada con éxito!');
    }

    function copyWhatsAppMsg(ventaId) {
      const v = appState.ventas.find(x => x.id === ventaId);
      if (!v) return;
      const c = appState.cuentas.find(x => x.id === v.cuenta_id) || {};
      let t = appState.mensaje_template || DEFAULT_TEMPLATE;

      t = t.replace(/{servicio}/g, c.tipo_cuenta || 'Servicio')
           .replace(/{cuenta}/g, c.correo || '')
           .replace(/{clave}/g, c.contrasena_plataforma || '')
           .replace(/{perfil}/g, 'Perfil 1')
           .replace(/{pin}/g, '0000')
           .replace(/{fecha_fin}/g, v.fecha_fin || '');

      navigator.clipboard.writeText(t).then(() => {
        alert('✅ Mensaje de WhatsApp copiado al portapapeles listo para enviar al cliente.');
      });
    }

    function calcularDiasRestantes(fechaFinStr) {
      if (!fechaFinStr) return 0;
      const [y, m, d] = fechaFinStr.split('-').map(Number);
      const fin = new Date(y, m - 1, d);
      const hoy = new Date();
      hoy.setHours(0,0,0,0);
      const diffTime = fin - hoy;
      return Math.round(diffTime / (1000 * 60 * 60 * 24));
    }

    /* Modales */
    function openNuevaCuentaModal() {
      document.getElementById('modalCuentaTitle').textContent = 'Nueva Cuenta Maestra';
      document.getElementById('cuentaId').value = '';
      document.getElementById('cuentaServicio').value = '';
      document.getElementById('cuentaCorreo').value = '';
      document.getElementById('cuentaClavePlataforma').value = '';
      document.getElementById('cuentaClaveCorreo').value = '';
      document.getElementById('cuentaCosto').value = '';
      document.getElementById('cuentaFecha').value = new Date().toISOString().split('T')[0];
      document.getElementById('modalCuenta').classList.add('active');
    }

    function editCuenta(id) {
      const c = appState.cuentas.find(x => x.id === id);
      if (!c) return;
      document.getElementById('modalCuentaTitle').textContent = 'Editar Cuenta Maestra #' + id;
      document.getElementById('cuentaId').value = c.id;
      document.getElementById('cuentaServicio').value = c.tipo_cuenta;
      document.getElementById('cuentaCorreo').value = c.correo;
      document.getElementById('cuentaClavePlataforma').value = c.contrasena_plataforma;
      document.getElementById('cuentaClaveCorreo').value = c.contrasena_correo;
      document.getElementById('cuentaCosto').value = c.costo_compra;
      document.getElementById('cuentaFecha').value = c.fecha_creacion;
      document.getElementById('modalCuenta').classList.add('active');
    }

    async function saveCuenta() {
      const idVal = document.getElementById('cuentaId').value;
      const cuenta = {
        id: idVal ? parseInt(idVal) : 0,
        tipo_cuenta: document.getElementById('cuentaServicio').value.trim() || 'Otro',
        correo: document.getElementById('cuentaCorreo').value.trim(),
        contrasena_plataforma: document.getElementById('cuentaClavePlataforma').value.trim(),
        contrasena_correo: document.getElementById('cuentaClaveCorreo').value.trim(),
        costo_compra: parseFloat(document.getElementById('cuentaCosto').value) || 0.0,
        fecha_creacion: document.getElementById('cuentaFecha').value || new Date().toISOString().split('T')[0],
        fecha_fin: '',
        estado: 'activa'
      };

      if (!cuenta.correo) {
        alert('Por favor ingresa el correo de la cuenta.');
        return;
      }

      const endpoint = idVal ? 'update_cuenta' : 'add_cuenta';
      await fetch(`/api/${endpoint}`, { method: 'POST', body: JSON.stringify(cuenta) });
      closeModal('modalCuenta');
      fetchState();
    }

    async function deleteCuenta(id) {
      if (confirm(`¿Eliminar la cuenta #${id}? Las ventas vinculadas también se borrarán en cascada.`)) {
        await fetch('/api/delete_cuenta', { method: 'POST', body: JSON.stringify({ id }) });
        fetchState();
      }
    }

    function openNuevaVentaModal() {
      if (appState.cuentas.length === 0) {
        alert('Primero debes registrar al menos una Cuenta Maestra.');
        return;
      }
      document.getElementById('modalVentaTitle').textContent = 'Registrar Venta de Perfil';
      document.getElementById('ventaId').value = '';
      document.getElementById('ventaCelular').value = '';
      document.getElementById('ventaCantidad').value = '1';
      document.getElementById('ventaPrecio').value = '';
      document.getElementById('ventaDias').value = '30';
      document.getElementById('ventaFechaInicio').value = new Date().toISOString().split('T')[0];

      const select = document.getElementById('ventaCuentaId');
      select.innerHTML = '';
      appState.cuentas.forEach(c => {
        const opt = document.createElement('option');
        opt.value = c.id;
        opt.textContent = `${c.tipo_cuenta} - ${c.correo}`;
        select.appendChild(opt);
      });

      document.getElementById('modalVenta').classList.add('active');
    }

    function editVenta(id) {
      const v = appState.ventas.find(x => x.id === id);
      if (!v) return;
      document.getElementById('modalVentaTitle').textContent = 'Editar Venta #' + id;
      document.getElementById('ventaId').value = v.id;
      document.getElementById('ventaCelular').value = v.numero_celular_cliente;
      document.getElementById('ventaCantidad').value = v.cantidad_perfiles;
      document.getElementById('ventaPrecio').value = v.precio_venta_por_perfil;
      document.getElementById('ventaDias').value = v.duracion_dias;
      document.getElementById('ventaFechaInicio').value = v.fecha_inicio;

      const select = document.getElementById('ventaCuentaId');
      select.innerHTML = '';
      appState.cuentas.forEach(c => {
        const opt = document.createElement('option');
        opt.value = c.id;
        opt.textContent = `${c.tipo_cuenta} - ${c.correo}`;
        if (c.id === v.cuenta_id) opt.selected = true;
        select.appendChild(opt);
      });

      document.getElementById('modalVenta').classList.add('active');
    }

    async function saveVenta() {
      const idVal = document.getElementById('ventaId').value;
      const cel = document.getElementById('ventaCelular').value.trim();
      if (!cel) {
        alert('Por favor ingresa el número celular del cliente.');
        return;
      }

      const cuentaId = parseInt(document.getElementById('ventaCuentaId').value);
      const cantidad = parseInt(document.getElementById('ventaCantidad').value) || 1;
      const precio = parseFloat(document.getElementById('ventaPrecio').value) || 0.0;
      const dias = parseInt(document.getElementById('ventaDias').value) || 30;
      const inicio = document.getElementById('ventaFechaInicio').value || new Date().toISOString().split('T')[0];

      // Calcular fecha fin
      const [y, m, d] = inicio.split('-').map(Number);
      const finDate = new Date(y, m - 1, d + dias);
      const finStr = finDate.toISOString().split('T')[0];

      const venta = {
        id: idVal ? parseInt(idVal) : 0,
        cuenta_id: cuentaId,
        numero_celular_cliente: cel,
        cantidad_perfiles: cantidad,
        precio_venta_por_perfil: precio,
        duracion_dias: dias,
        fecha_inicio: inicio,
        fecha_fin: finStr
      };

      const endpoint = idVal ? 'update_venta' : 'add_venta';
      await fetch(`/api/${endpoint}`, { method: 'POST', body: JSON.stringify(venta) });
      closeModal('modalVenta');
      fetchState();
    }

    async function deleteVenta(id) {
      if (confirm(`¿Eliminar la venta #${id}?`)) {
        await fetch('/api/delete_venta', { method: 'POST', body: JSON.stringify({ id }) });
        fetchState();
      }
    }

    function closeModal(modalId) {
      document.getElementById(modalId).classList.remove('active');
    }

    /* Importar y Exportar Backup */
    async function handleFileSelect(event) {
      const file = event.target.files[0];
      if (!file) return;

      const reader = new FileReader();
      reader.onload = async function(e) {
        const jsonContent = e.target.result;
        try {
          const res = await fetch('/api/import_backup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: jsonContent
          });
          const result = await res.json();
          if (result.success) {
            alert(`✅ ¡Copia de seguridad importada con éxito!\n\nCuentas cargadas: ${result.cuentas}\nVentas cargadas: ${result.ventas}`);
            fetchState();
          } else {
            alert('❌ Error al importar respaldo: ' + result.error);
          }
        } catch (err) {
          alert('Error procesando el archivo: ' + err);
        }
      };
      reader.readAsText(file);
      event.target.value = '';
    }

    async function exportBackup() {
      try {
        const res = await fetch('/api/export_backup');
        const jsonStr = await res.text();
        const blob = new Blob([jsonStr], { type: 'application/json' });
        const now = new Date();
        const dateStr = now.toISOString().replace(/[-:T]/g, '').slice(0, 14);
        const fileName = `gestor_backup_${dateStr}.json`;

        const a = document.createElement('a');
        a.href = URL.createObjectURL(blob);
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        alert(`✅ Archivo descargado como "${fileName}".\nPásalo a tu celular y restáuralo en la app móvil.`);
      } catch (e) {
        alert('Error al exportar: ' + e);
      }
    }

    // Inicializar al cargar
    fetchState();
  </script>
</body>
</html>
"#;

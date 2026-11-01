pub const INDEX_HTML: &str = r##"<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Gestor de Cuentas y Perfiles</title>
  <link rel="icon" type="image/svg+xml" href="data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 108 108'%3E%3Ccircle cx='54' cy='54' r='48' fill='%230F172A' stroke='%232563EB' stroke-width='3'/%3E%3Cpath fill='%23F8FAFC' d='M76.94,46.42 C76.64,46.07 75.46,44.99 74.83,44.70 C69.53,41.23 62.87,44.18 61.81,47.97 C67.74,44.63 71.64,45.90 76.94,46.42 L76.94,46.42 Z M30.83,46.42 C36.13,45.92 40.31,44.81 45.80,47.97 C44.45,42.81 35.27,41.13 30.83,46.42 L30.83,46.42 Z M49.79,46.83 C49.59,47.03 49.20,47.32 48.83,47.59 C47.68,48.54 46.08,49.62 45.66,51.06 C45.58,52.00 46.13,54.03 46.47,54.88 C46.97,53.73 47.40,51.58 47.40,51.58 C47.40,51.58 48.83,50.57 50.39,49.47 C50.78,49.14 51.19,48.83 51.63,48.33 C52.92,46.66 52.18,43.15 51.19,41.45 C51.07,41.90 50.21,46.65 49.79,46.83 L49.79,46.83 Z M27.23,39.79 C26.92,40.68 27.27,41.84 27.87,42.35 C28.46,42.88 29.07,42.80 29.05,42.82 C29.07,42.81 29.04,42.27 29.16,41.80 C29.16,41.76 29.63,40.43 29.64,40.42 C29.96,39.47 29.94,37.64 29.89,37.56 C29.87,37.48 27.73,37.88 27.23,39.79 L27.23,39.79 Z M78.69,37.56 C78.63,37.64 78.27,39.35 78.56,40.37 C78.68,40.92 78.94,41.39 79.14,41.86 C79.35,42.32 79.52,42.82 79.52,42.82 C79.52,42.82 80.07,42.72 80.52,42.15 C80.96,41.61 81.22,40.66 81.01,39.86 C80.69,38.14 78.74,37.47 78.69,37.56 L78.69,37.56 Z M57.40,49.47 C58.96,50.57 60.39,51.58 60.39,51.58 C60.39,51.58 60.81,53.73 61.32,54.88 C62.81,51.19 61.99,50.41 61.20,49.53 C60.37,48.63 58.18,47.01 58.01,46.83 C57.60,46.66 56.72,41.90 56.60,41.45 C55.66,43.06 54.88,46.67 56.16,48.33 C56.60,48.83 57.01,49.14 57.40,49.47 L57.40,49.47 Z M30.85,46.45 C30.88,46.43 30.90,46.41 30.93,46.39 C30.90,46.40 30.86,46.41 30.83,46.42 L30.85,46.45 Z M42.95,45.60 C43.52,45.18 45.69,43.52 45.44,42.86 C42.88,36.00 33.29,37.14 32.08,43.36 L33.61,45.18 L42.95,45.60 Z M72.84,45.18 L74.37,43.36 C73.64,39.58 69.59,37.40 65.76,38.44 C62.07,39.46 60.54,42.95 61.24,43.62 C61.90,44.25 62.76,45.05 63.50,45.60 L72.84,45.18 Z M59.52,36.23 C62.67,34.85 65.30,32.82 67.64,30.38 C69.54,28.38 71.29,28.33 73.64,29.41 C74.53,29.82 79.12,32.12 79.92,32.31 C81.44,32.67 80.67,30.27 79.32,29.52 C75.00,27.11 70.07,24.47 65.67,27.07 C63.77,28.20 59.67,31.78 57.58,31.92 C56.52,31.99 56.13,32.38 56.10,33.41 C56.19,33.89 56.24,34.39 56.37,34.87 C56.86,36.66 57.76,37.01 59.52,36.23 L59.52,36.23 Z M28.59,31.99 C28.59,31.99 32.64,29.99 34.01,29.39 C36.37,28.37 38.06,28.39 39.94,30.33 C42.77,33.25 44.71,34.73 48.68,36.51 C49.68,36.96 50.46,36.66 50.90,35.64 C51.19,34.95 51.41,34.21 51.49,33.47 C51.60,32.42 51.16,32.09 50.11,31.90 C47.82,31.47 45.15,29.16 43.06,27.70 C40.28,25.76 36.76,25.67 33.59,26.81 C32.22,27.31 28.27,29.30 27.31,30.57 C27.02,30.96 26.81,31.75 27.03,32.09 C27.42,32.70 28.07,32.12 28.59,31.99 L28.59,31.99 Z M67.49,71.71 C65.93,71.38 62.56,71.00 61.10,71.51 C56.72,73.02 52.33,73.09 47.87,71.92 C44.73,71.10 43.88,71.07 40.12,71.71 C46.85,81.93 61.71,80.91 67.49,71.71 L67.49,71.71 Z M84.22,56.21 C83.73,55.69 79.12,50.73 78.50,50.40 C78.76,53.01 79.56,52.18 79.27,55.86 C78.91,60.66 75.38,60.35 71.18,60.36 C67.34,60.38 66.83,58.65 63.81,57.45 C61.63,56.58 59.02,57.02 57.69,59.08 C57.12,59.95 56.20,60.44 55.05,60.37 C54.28,60.33 53.52,60.33 52.75,60.37 C51.45,60.44 50.53,59.94 49.82,58.90 C48.89,57.52 47.38,56.96 45.84,57.01 C44.17,57.06 42.65,57.82 41.27,58.81 C38.42,60.89 36.52,60.22 33.48,60.37 C30.38,60.52 28.33,58.19 28.31,55.42 C28.30,52.56 28.79,52.56 29.11,50.37 C27.14,51.60 24.50,54.13 22.91,57.22 C21.83,59.32 21.84,61.57 22.27,63.82 C23.54,70.42 32.29,70.84 38.95,70.23 C40.67,70.07 42.39,69.77 44.10,69.49 C45.81,69.20 47.50,69.08 49.16,69.71 C50.01,70.03 50.87,70.45 51.75,70.55 C53.73,70.76 55.78,70.95 57.64,70.07 C59.53,69.18 61.48,68.99 63.44,69.49 C68.76,70.85 79.54,71.44 83.39,67.42 C84.46,66.29 85.15,65.03 85.41,63.50 C85.84,60.92 85.60,58.45 84.22,56.21 L84.22,56.21 Z M30.83,46.42 C30.80,46.48 30.78,46.54 30.75,46.60 C30.78,46.55 30.82,46.50 30.85,46.45 L30.83,46.42 Z M76.78,46.43 C76.84,46.47 76.88,46.50 76.94,46.53 C76.88,46.50 76.82,46.48 76.76,46.46 L76.78,46.43 Z'/%3E%3C/svg%3E">
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
    .brand { display: flex; align-items: center; gap: 14px; }
    .brand-logo-container {
      display: flex;
      align-items: center;
      justify-content: center;
      width: 42px;
      height: 42px;
      border-radius: 50%;
      box-shadow: 0 0 14px rgba(37,99,235,0.45);
      transition: transform 0.2s cubic-bezier(0.4, 0, 0.2, 1);
      flex-shrink: 0;
    }
    .brand-logo-container:hover { transform: scale(1.08); }
    .brand-logo { width: 42px; height: 42px; border-radius: 50%; display: block; }
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
    button.btn-warning { background: rgba(255,171,0,0.15); border-color: var(--warning); color: var(--warning); }
    button.btn-warning:hover { background: var(--warning); color: #000; }
    button.btn-sm { padding: 4px 10px; font-size: 0.78rem; }

    /* Inputs with Action (Paste) & Chips */
    .input-with-action { display: flex; gap: 8px; width: 100%; }
    .input-with-action input { flex: 1; min-width: 0; }
    .btn-paste {
      background: var(--surface-light);
      border: 1px solid var(--border);
      color: var(--accent);
      padding: 6px 14px;
      font-size: 0.8rem;
      font-weight: 700;
      border-radius: var(--radius);
      cursor: pointer;
      white-space: nowrap;
      transition: all 0.2s;
      display: inline-flex;
      align-items: center;
      gap: 4px;
    }
    .btn-paste:hover {
      background: rgba(0, 229, 255, 0.18);
      border-color: var(--accent);
      color: #fff;
    }
    .service-chips { display: flex; gap: 8px; width: 100%; }
    .chip-btn {
      flex: 1;
      background: var(--surface-light);
      border: 1px solid var(--border);
      color: var(--text-muted);
      padding: 8px 12px;
      border-radius: 8px;
      font-weight: 600;
      font-size: 0.85rem;
      cursor: pointer;
      text-align: center;
      transition: all 0.2s;
    }
    .chip-btn:hover { color: var(--text); border-color: var(--primary); }
    .chip-btn.active {
      background: rgba(124, 77, 255, 0.25);
      border-color: var(--primary);
      color: #fff;
      box-shadow: 0 0 10px rgba(124, 77, 255, 0.3);
    }
    .duration-chips { display: flex; gap: 8px; margin-top: 6px; }
    .duration-chip {
      background: var(--surface-light);
      border: 1px solid var(--border);
      color: var(--text-muted);
      padding: 4px 12px;
      border-radius: 6px;
      font-size: 0.78rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.2s;
    }
    .duration-chip:hover { border-color: var(--accent); color: var(--accent); }
    .duration-chip.active { background: rgba(0, 229, 255, 0.15); border-color: var(--accent); color: var(--accent); }

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
      <div class="brand-logo-container" title="Logo Oficial Gestor de Cuentas">
        <svg class="brand-logo" viewBox="0 0 108 108" xmlns="http://www.w3.org/2000/svg">
          <circle cx="54" cy="54" r="48" fill="#0F172A" stroke="#2563EB" stroke-width="2.5" />
          <path fill="#F8FAFC" d="M76.94,46.42 C76.64,46.07 75.46,44.99 74.83,44.70 C69.53,41.23 62.87,44.18 61.81,47.97 C67.74,44.63 71.64,45.90 76.94,46.42 L76.94,46.42 Z M30.83,46.42 C36.13,45.92 40.31,44.81 45.80,47.97 C44.45,42.81 35.27,41.13 30.83,46.42 L30.83,46.42 Z M49.79,46.83 C49.59,47.03 49.20,47.32 48.83,47.59 C47.68,48.54 46.08,49.62 45.66,51.06 C45.58,52.00 46.13,54.03 46.47,54.88 C46.97,53.73 47.40,51.58 47.40,51.58 C47.40,51.58 48.83,50.57 50.39,49.47 C50.78,49.14 51.19,48.83 51.63,48.33 C52.92,46.66 52.18,43.15 51.19,41.45 C51.07,41.90 50.21,46.65 49.79,46.83 L49.79,46.83 Z M27.23,39.79 C26.92,40.68 27.27,41.84 27.87,42.35 C28.46,42.88 29.07,42.80 29.05,42.82 C29.07,42.81 29.04,42.27 29.16,41.80 C29.16,41.76 29.63,40.43 29.64,40.42 C29.96,39.47 29.94,37.64 29.89,37.56 C29.87,37.48 27.73,37.88 27.23,39.79 L27.23,39.79 Z M78.69,37.56 C78.63,37.64 78.27,39.35 78.56,40.37 C78.68,40.92 78.94,41.39 79.14,41.86 C79.35,42.32 79.52,42.82 79.52,42.82 C79.52,42.82 80.07,42.72 80.52,42.15 C80.96,41.61 81.22,40.66 81.01,39.86 C80.69,38.14 78.74,37.47 78.69,37.56 L78.69,37.56 Z M57.40,49.47 C58.96,50.57 60.39,51.58 60.39,51.58 C60.39,51.58 60.81,53.73 61.32,54.88 C62.81,51.19 61.99,50.41 61.20,49.53 C60.37,48.63 58.18,47.01 58.01,46.83 C57.60,46.66 56.72,41.90 56.60,41.45 C55.66,43.06 54.88,46.67 56.16,48.33 C56.60,48.83 57.01,49.14 57.40,49.47 L57.40,49.47 Z M30.85,46.45 C30.88,46.43 30.90,46.41 30.93,46.39 C30.90,46.40 30.86,46.41 30.83,46.42 L30.85,46.45 Z M42.95,45.60 C43.52,45.18 45.69,43.52 45.44,42.86 C42.88,36.00 33.29,37.14 32.08,43.36 L33.61,45.18 L42.95,45.60 Z M72.84,45.18 L74.37,43.36 C73.64,39.58 69.59,37.40 65.76,38.44 C62.07,39.46 60.54,42.95 61.24,43.62 C61.90,44.25 62.76,45.05 63.50,45.60 L72.84,45.18 Z M59.52,36.23 C62.67,34.85 65.30,32.82 67.64,30.38 C69.54,28.38 71.29,28.33 73.64,29.41 C74.53,29.82 79.12,32.12 79.92,32.31 C81.44,32.67 80.67,30.27 79.32,29.52 C75.00,27.11 70.07,24.47 65.67,27.07 C63.77,28.20 59.67,31.78 57.58,31.92 C56.52,31.99 56.13,32.38 56.10,33.41 C56.19,33.89 56.24,34.39 56.37,34.87 C56.86,36.66 57.76,37.01 59.52,36.23 L59.52,36.23 Z M28.59,31.99 C28.59,31.99 32.64,29.99 34.01,29.39 C36.37,28.37 38.06,28.39 39.94,30.33 C42.77,33.25 44.71,34.73 48.68,36.51 C49.68,36.96 50.46,36.66 50.90,35.64 C51.19,34.95 51.41,34.21 51.49,33.47 C51.60,32.42 51.16,32.09 50.11,31.90 C47.82,31.47 45.15,29.16 43.06,27.70 C40.28,25.76 36.76,25.67 33.59,26.81 C32.22,27.31 28.27,29.30 27.31,30.57 C27.02,30.96 26.81,31.75 27.03,32.09 C27.42,32.70 28.07,32.12 28.59,31.99 L28.59,31.99 Z M67.49,71.71 C65.93,71.38 62.56,71.00 61.10,71.51 C56.72,73.02 52.33,73.09 47.87,71.92 C44.73,71.10 43.88,71.07 40.12,71.71 C46.85,81.93 61.71,80.91 67.49,71.71 L67.49,71.71 Z M84.22,56.21 C83.73,55.69 79.12,50.73 78.50,50.40 C78.76,53.01 79.56,52.18 79.27,55.86 C78.91,60.66 75.38,60.35 71.18,60.36 C67.34,60.38 66.83,58.65 63.81,57.45 C61.63,56.58 59.02,57.02 57.69,59.08 C57.12,59.95 56.20,60.44 55.05,60.37 C54.28,60.33 53.52,60.33 52.75,60.37 C51.45,60.44 50.53,59.94 49.82,58.90 C48.89,57.52 47.38,56.96 45.84,57.01 C44.17,57.06 42.65,57.82 41.27,58.81 C38.42,60.89 36.52,60.22 33.48,60.37 C30.38,60.52 28.33,58.19 28.31,55.42 C28.30,52.56 28.79,52.56 29.11,50.37 C27.14,51.60 24.50,54.13 22.91,57.22 C21.83,59.32 21.84,61.57 22.27,63.82 C23.54,70.42 32.29,70.84 38.95,70.23 C40.67,70.07 42.39,69.77 44.10,69.49 C45.81,69.20 47.50,69.08 49.16,69.71 C50.01,70.03 50.87,70.45 51.75,70.55 C53.73,70.76 55.78,70.95 57.64,70.07 C59.53,69.18 61.48,68.99 63.44,69.49 C68.76,70.85 79.54,71.44 83.39,67.42 C84.46,66.29 85.15,65.03 85.41,63.50 C85.84,60.92 85.60,58.45 84.22,56.21 L84.22,56.21 Z M30.83,46.42 C30.80,46.48 30.78,46.54 30.75,46.60 C30.78,46.55 30.82,46.50 30.85,46.45 L30.83,46.42 Z M76.78,46.43 C76.84,46.47 76.88,46.50 76.94,46.53 C76.88,46.50 76.82,46.48 76.76,46.46 L76.78,46.43 Z" />
        </svg>
      </div>
      <div>
        <h1>Gestor de Cuentas</h1>
        <small style="color:var(--text-muted)">Versión Desktop (Windows Nativo)</small>
      </div>
      <span>100% Offline • Persistente</span>
    </div>
    <div class="header-actions">
      <input type="file" id="fileInput" accept=".json" style="display:none" onchange="handleFileSelect(event)">
      <button onclick="document.getElementById('fileInput').click()">📥 Importar Backup</button>
      <button class="btn-success" onclick="exportBackup()">📤 Exportar Backup</button>
      <button class="btn-danger" onclick="confirmarLimpiarBD()" title="Elimina todos los datos locales para reiniciar o cambiar de base de datos">🗑️ Borrar Base de Datos</button>
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
              <th>Vigencia Proveedor</th>
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
        <h3>💬 Plantilla de Mensaje para WhatsApp / Entrega</h3>
        <p style="color:var(--text-muted); font-size:0.85rem; margin:8px 0 16px;">
          Esta plantilla es compatible con la aplicación del celular. Se utilizará automáticamente al hacer clic en <strong>"💬 Copiar"</strong> en cualquier venta. Puedes personalizarla con las siguientes variables:
          <br><code>{servicio}</code>, <code>{dias}</code>, <code>{correo}</code> (o <code>{cuenta}</code>), <code>{contrasena}</code> (o <code>{clave}</code>), <code>{fecha_inicio}</code>, <code>{fecha_fin}</code>, <code>{perfil}</code>, <code>{pin}</code>
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
          <label>Tipo de Servicio</label>
          <div class="service-chips">
            <button type="button" class="chip-btn active" id="chipServicioChatGPT" onclick="selectServiceOption('Chat GPT')">Chat GPT</button>
            <button type="button" class="chip-btn" id="chipServicioGemini" onclick="selectServiceOption('Gemini')">Gemini</button>
            <button type="button" class="chip-btn" id="chipServicioOtros" onclick="selectServiceOption('Otros')">Otros</button>
          </div>
          <input type="text" id="cuentaServicioOtro" placeholder="Especificar otro servicio (ej: Netflix, Spotify, Canva...)" style="display:none; margin-top:8px;">
        </div>
        <div class="form-group">
          <label>Correo Electrónico</label>
          <div class="input-with-action">
            <input type="email" id="cuentaCorreo" placeholder="ejemplo@correo.com">
            <button type="button" class="btn-paste" onclick="pasteInto('cuentaCorreo')" title="Pegar desde el portapapeles">📋 Pegar</button>
          </div>
        </div>
        <div class="form-group">
          <label id="lblClavePlataforma">Contraseña Plataforma (Chat GPT)</label>
          <div class="input-with-action">
            <input type="text" id="cuentaClavePlataforma" placeholder="Contraseña de acceso a la plataforma">
            <button type="button" class="btn-paste" onclick="pasteInto('cuentaClavePlataforma')" title="Pegar desde el portapapeles">📋 Pegar</button>
          </div>
        </div>
        <div class="form-group">
          <label>Contraseña del Correo (Opcional)</label>
          <div class="input-with-action">
            <input type="text" id="cuentaClaveCorreo" placeholder="Contraseña del correo original">
            <button type="button" class="btn-paste" onclick="pasteInto('cuentaClaveCorreo')" title="Pegar desde el portapapeles">📋 Pegar</button>
          </div>
        </div>
        <div class="form-group">
          <label>Costo de Compra (Bs.)</label>
          <input type="number" step="0.5" id="cuentaCosto" placeholder="0.00">
        </div>
        <div style="display:flex; gap:10px;">
          <div class="form-group" style="flex:1;">
            <label>Fecha Inicio (Registro)</label>
            <input type="date" id="cuentaFechaCreacion" onchange="onFechaCreacionChange()">
          </div>
          <div class="form-group" style="flex:1;">
            <label>Fecha Fin (Proveedor)</label>
            <input type="date" id="cuentaFechaFin">
          </div>
        </div>
        <div class="form-group" id="grpCuentaEstado" style="display:none;">
          <label>Estado de la Cuenta</label>
          <select id="cuentaEstado">
            <option value="activa">ACTIVA (Disponible para ventas)</option>
            <option value="inhabilitada">INHABILITADA (No disponible para ventas)</option>
          </select>
        </div>
      </div>
      <div class="modal-footer">
        <button onclick="closeModal('modalCuenta')">Cancelar</button>
        <button class="btn-primary" onclick="saveCuenta()">Guardar Cuenta Maestra</button>
      </div>
    </div>
  </div>

  <!-- MODAL: NUEVA VENTA -->
  <div id="modalVenta" class="modal-overlay">
    <div class="modal-card">
      <div class="modal-header">
        <h3 id="modalVentaTitle">Registrar Nueva Venta</h3>
        <button onclick="closeModal('modalVenta')">✕</button>
      </div>
      <div class="modal-body">
        <input type="hidden" id="ventaId">
        <div class="form-group">
          <label>1. Seleccionar Cuenta del Inventario</label>
          <select id="ventaCuentaId"></select>
        </div>
        <div class="form-group">
          <label>2. Celular del Cliente</label>
          <div class="input-with-action">
            <input type="text" id="ventaCelular" placeholder="Pega o escribe (+591 76992490)">
            <button type="button" class="btn-paste" onclick="pasteInto('ventaCelular')" title="Pegar desde el portapapeles">📋 Pegar</button>
          </div>
        </div>
        <div class="form-group">
          <label>3. Precio de Venta (Bs.)</label>
          <input type="number" step="0.5" id="ventaPrecio" placeholder="15.00">
        </div>
        <div class="form-group">
          <label>4. Duración (Días)</label>
          <input type="number" id="ventaDias" value="30" oninput="onVentaDiasChange()">
          <div class="duration-chips">
            <button type="button" class="duration-chip active" id="chipDias30" onclick="setVentaDuracion(30)">30 días</button>
            <button type="button" class="duration-chip" id="chipDias60" onclick="setVentaDuracion(60)">60 días</button>
            <button type="button" class="duration-chip" id="chipDias90" onclick="setVentaDuracion(90)">90 días</button>
          </div>
        </div>
        <div style="display:flex; gap:10px;">
          <div class="form-group" style="flex:1;">
            <label>Fecha de Inicio</label>
            <input type="date" id="ventaFechaInicio" onchange="onVentaFechaInicioChange()">
          </div>
          <div class="form-group" style="flex:1;">
            <label>Fecha Fin (Vencimiento)</label>
            <input type="date" id="ventaFechaFin">
          </div>
        </div>
      </div>
      <div class="modal-footer">
        <button onclick="closeModal('modalVenta')">Cancelar</button>
        <button class="btn-primary" onclick="saveVenta()">Guardar y Copiar Mensaje</button>
      </div>
    </div>
  </div>

  <script>
    let appState = { cuentas: [], ventas: [], mensaje_template: null };
    let currentVentaFilter = 'todas';

    const DEFAULT_TEMPLATE = `Datos para ingresar a la cuenta de {servicio}

Cuenta {servicio} ({dias} dias)
Correo: {correo}
Contrasena: {contrasena}

Fecha ini: {fecha_inicio}
Fecha end: {fecha_fin}

Reglas para el uso de la cuenta:

- No modificar ningun dato de la cuenta, en caso de modificar algun dato de la cuenta, retiro la cuenta del grupo de trabajo y te quitare el acceso, no cubrire la garantia y el tiempo de servicio.
- Evita salirte de la cuenta.
- Referentemente, usa la aplicacion movil en el celular y en computadora navegador Google Chrome NO PESTAÑA INCOGNITO 
- Link para pc preferentemente la pagina oficial en tu navegador

Ingresa ahora por favor y te paso los codigos de activacion`;

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
        const esActiva = (c.estado || 'activa').toLowerCase() === 'activa';
        const vigenciaStr = c.fecha_fin ? `${c.fecha_creacion || ''} → ${c.fecha_fin}` : (c.fecha_creacion || 'Sin fecha');

        const tr = document.createElement('tr');
        tr.innerHTML = `
          <td>#${c.id}</td>
          <td><strong>${c.tipo_cuenta || 'Otro'}</strong></td>
          <td>${c.correo}</td>
          <td><code>${c.contrasena_plataforma}</code></td>
          <td>Bs. ${c.costo_compra.toFixed(2)}</td>
          <td><small style="color:var(--text-muted)">${vigenciaStr}</small></td>
          <td>
            <span class="badge ${recuperada ? 'badge-success' : 'badge-warning'}">
              Bs. ${gananciaCuenta >= 0 ? '+' : ''}${gananciaCuenta.toFixed(2)} (${recuperada ? 'Ganancia' : 'Recuperando'})
            </span>
          </td>
          <td><span class="badge ${esActiva ? 'badge-success' : 'badge-danger'}">${esActiva ? 'ACTIVA' : 'INHABILITADA'}</span></td>
          <td>
            <button class="btn-sm" onclick="editCuenta(${c.id})" title="Editar">✏️</button>
            <button class="btn-sm ${esActiva ? 'btn-warning' : 'btn-success'}" onclick="toggleCuentaEstado(${c.id})" title="${esActiva ? 'Desactivar / Inhabilitar cuenta' : 'Activar cuenta'}">
              ${esActiva ? '⏸️ Desactivar' : '▶️ Activar'}
            </button>
            <button class="btn-sm btn-danger" onclick="deleteCuenta(${c.id})" title="Eliminar cuenta">🗑️</button>
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

    async function copyWhatsAppMsg(ventaId) {
      const v = appState.ventas.find(x => x.id === ventaId);
      if (!v) return;
      const c = appState.cuentas.find(x => x.id === v.cuenta_id) || {};
      let t = appState.mensaje_template || DEFAULT_TEMPLATE;

      // Calcular duracion en dias si no está explícita
      let dias = v.duracion_dias || 30;
      if (v.fecha_inicio && v.fecha_fin) {
        const dIni = new Date(v.fecha_inicio + 'T00:00:00');
        const dFin = new Date(v.fecha_fin + 'T00:00:00');
        const diff = Math.round((dFin - dIni) / (1000 * 60 * 60 * 24));
        if (diff > 0) dias = diff;
      }

      t = t.replace(/{servicio}/gi, c.tipo_cuenta || 'Servicio')
           .replace(/{dias}/gi, dias.toString())
           .replace(/{correo}/gi, c.correo || '')
           .replace(/{cuenta}/gi, c.correo || '')
           .replace(/{contrasena}/gi, c.contrasena_plataforma || '')
           .replace(/{clave}/gi, c.contrasena_plataforma || '')
           .replace(/{fecha_inicio}/gi, v.fecha_inicio || '')
           .replace(/{fecha_fin}/gi, v.fecha_fin || '')
           .replace(/{perfil}/gi, 'Perfil 1')
           .replace(/{pin}/gi, '0000');

      let copiado = false;

      // 1. Enviar al backend nativo en Rust (Win32 API pura de Windows)
      try {
        const res = await fetch('/api/clipboard', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ text: t })
        });
        const resData = await res.json();
        if (resData.success) {
          copiado = true;
        }
      } catch (err) {
        console.warn('Fallo en clipboard backend:', err);
      }

      // 2. Fallback de navegador (execCommand / Clipboard API)
      fallbackCopyText(t);

      showToast('✅ ¡Mensaje de entrega copiado al portapapeles!');
    }

    function fallbackCopyText(text) {
      try {
        const textArea = document.createElement('textarea');
        textArea.value = text;
        textArea.style.position = 'fixed';
        textArea.style.left = '-999999px';
        textArea.style.top = '-999999px';
        document.body.appendChild(textArea);
        textArea.focus();
        textArea.select();
        document.execCommand('copy');
        document.body.removeChild(textArea);
      } catch (e) {
        console.warn('execCommand copy fallback error:', e);
      }

      if (navigator.clipboard && navigator.clipboard.writeText) {
        navigator.clipboard.writeText(text).catch(() => {});
      }
    }

    function showToast(msg) {
      let toast = document.getElementById('toastNotification');
      if (!toast) {
        toast = document.createElement('div');
        toast.id = 'toastNotification';
        toast.style.position = 'fixed';
        toast.style.bottom = '28px';
        toast.style.right = '28px';
        toast.style.backgroundColor = '#0f172a';
        toast.style.color = '#38bdf8';
        toast.style.border = '1px solid #2563eb';
        toast.style.padding = '14px 24px';
        toast.style.borderRadius = '10px';
        toast.style.boxShadow = '0 8px 30px rgba(0,0,0,0.6)';
        toast.style.zIndex = '999999';
        toast.style.fontWeight = '600';
        toast.style.fontSize = '0.95rem';
        toast.style.display = 'flex';
        toast.style.alignItems = 'center';
        toast.style.gap = '10px';
        toast.style.transition = 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)';
        document.body.appendChild(toast);
      }
      toast.textContent = msg;
      toast.style.opacity = '1';
      toast.style.transform = 'translateY(0) scale(1)';

      clearTimeout(toast._timer);
      toast._timer = setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(12px) scale(0.95)';
      }, 3500);
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

    /* Selector de Servicios (Chips) */
    let currentServicioOpcion = 'Chat GPT';

    function selectServiceOption(opcion) {
      currentServicioOpcion = opcion;
      const chipGpt = document.getElementById('chipServicioChatGPT');
      const chipGem = document.getElementById('chipServicioGemini');
      const chipOtro = document.getElementById('chipServicioOtros');
      if (chipGpt) chipGpt.classList.toggle('active', opcion === 'Chat GPT');
      if (chipGem) chipGem.classList.toggle('active', opcion === 'Gemini');
      if (chipOtro) chipOtro.classList.toggle('active', opcion === 'Otros');

      const otroInput = document.getElementById('cuentaServicioOtro');
      if (otroInput) {
        if (opcion === 'Otros') {
          otroInput.style.display = 'block';
          otroInput.focus();
        } else {
          otroInput.style.display = 'none';
        }
      }
      const lbl = document.getElementById('lblClavePlataforma');
      if (lbl) {
        lbl.textContent = `Contraseña Plataforma (${opcion === 'Otros' ? (otroInput.value.trim() || 'Servicio') : opcion})`;
      }
    }

    function getServicioSeleccionado() {
      if (currentServicioOpcion === 'Otros') {
        const val = document.getElementById('cuentaServicioOtro').value.trim();
        return val || 'Otros';
      }
      return currentServicioOpcion;
    }

    function onFechaCreacionChange() {
      const fIni = document.getElementById('cuentaFechaCreacion').value;
      if (fIni) {
        const d = new Date(fIni + 'T00:00:00');
        const dFin = new Date(d.getTime() + 30 * 24 * 60 * 60 * 1000);
        document.getElementById('cuentaFechaFin').value = dFin.toISOString().split('T')[0];
      }
    }

    /* Pegado desde Portapapeles */
    async function pasteInto(elemId) {
      let text = '';
      // 1. Intentar mediante la API nativa de Rust en Windows (Win32 pura sin restricciones)
      try {
        const res = await fetch('/api/clipboard/paste');
        const data = await res.json();
        if (data.success && data.text) {
          text = data.text;
        }
      } catch (err) {}

      // 2. Si no hubo texto en backend, intentar navigator.clipboard
      if (!text && navigator.clipboard && navigator.clipboard.readText) {
        try {
          text = await navigator.clipboard.readText();
        } catch (err) {}
      }

      if (text) {
        const el = document.getElementById(elemId);
        if (el) {
          el.value = text.trim();
          showToast('📋 Pegado desde portapapeles');
        }
      } else {
        showToast('⚠️ No se encontró texto en el portapapeles');
      }
    }

    /* Modales Cuenta */
    function openNuevaCuentaModal() {
      document.getElementById('modalCuentaTitle').textContent = 'Registrar Cuenta Maestra';
      document.getElementById('cuentaId').value = '';
      selectServiceOption('Chat GPT');
      document.getElementById('cuentaServicioOtro').value = '';
      document.getElementById('cuentaCorreo').value = '';
      document.getElementById('cuentaClavePlataforma').value = '';
      document.getElementById('cuentaClaveCorreo').value = '';
      document.getElementById('cuentaCosto').value = '';

      const hoy = new Date();
      const hoyStr = hoy.toISOString().split('T')[0];
      const fin = new Date(hoy.getTime() + 30 * 24 * 60 * 60 * 1000);
      const finStr = fin.toISOString().split('T')[0];

      document.getElementById('cuentaFechaCreacion').value = hoyStr;
      document.getElementById('cuentaFechaFin').value = finStr;
      document.getElementById('grpCuentaEstado').style.display = 'none';
      document.getElementById('modalCuenta').classList.add('active');
    }

    function editCuenta(id) {
      const c = appState.cuentas.find(x => x.id === id);
      if (!c) return;
      document.getElementById('modalCuentaTitle').textContent = 'Editar Cuenta Maestra #' + id;
      document.getElementById('cuentaId').value = c.id;

      if (c.tipo_cuenta === 'Chat GPT' || c.tipo_cuenta === 'Gemini') {
        selectServiceOption(c.tipo_cuenta);
      } else {
        selectServiceOption('Otros');
        document.getElementById('cuentaServicioOtro').value = c.tipo_cuenta || '';
      }

      document.getElementById('cuentaCorreo').value = c.correo || '';
      document.getElementById('cuentaClavePlataforma').value = c.contrasena_plataforma || '';
      document.getElementById('cuentaClaveCorreo').value = c.contrasena_correo || '';
      document.getElementById('cuentaCosto').value = c.costo_compra || 0;
      document.getElementById('cuentaFechaCreacion').value = c.fecha_creacion || new Date().toISOString().split('T')[0];
      document.getElementById('cuentaFechaFin').value = c.fecha_fin || '';

      document.getElementById('grpCuentaEstado').style.display = 'block';
      document.getElementById('cuentaEstado').value = (c.estado || 'activa').toLowerCase();

      document.getElementById('modalCuenta').classList.add('active');
    }

    async function saveCuenta() {
      const idVal = document.getElementById('cuentaId').value;
      const servicio = getServicioSeleccionado();
      const correo = document.getElementById('cuentaCorreo').value.trim();
      const clavePlat = document.getElementById('cuentaClavePlataforma').value.trim();

      if (!correo) {
        alert('Por favor ingresa el correo de la cuenta.');
        return;
      }
      if (!clavePlat) {
        alert('Por favor ingresa la contraseña de la plataforma.');
        return;
      }

      const fIni = document.getElementById('cuentaFechaCreacion').value || new Date().toISOString().split('T')[0];
      const fFin = document.getElementById('cuentaFechaFin').value || '';
      const estadoVal = document.getElementById('cuentaEstado').value || 'activa';

      const cuenta = {
        id: idVal ? parseInt(idVal) : 0,
        tipo_cuenta: servicio,
        correo: correo,
        contrasena_plataforma: clavePlat,
        contrasena_correo: document.getElementById('cuentaClaveCorreo').value.trim(),
        costo_compra: parseFloat(document.getElementById('cuentaCosto').value) || 0.0,
        fecha_creacion: fIni,
        fecha_fin: fFin,
        estado: idVal ? estadoVal : 'activa'
      };

      const endpoint = idVal ? 'update_cuenta' : 'add_cuenta';
      await fetch(`/api/${endpoint}`, { method: 'POST', body: JSON.stringify(cuenta) });
      closeModal('modalCuenta');
      fetchState();
      showToast('✅ Cuenta maestra guardada');
    }

    async function toggleCuentaEstado(id) {
      await fetch('/api/toggle_cuenta_estado', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ id })
      });
      fetchState();
      showToast('🔄 Estado de la cuenta actualizado');
    }

    async function deleteCuenta(id) {
      if (confirm(`¿Eliminar la cuenta #${id}? Las ventas vinculadas también se borrarán en cascada.`)) {
        await fetch('/api/delete_cuenta', { method: 'POST', body: JSON.stringify({ id }) });
        fetchState();
        showToast('🗑️ Cuenta eliminada');
      }
    }

    /* Duracion de Venta Chips y Fechas */
    function setVentaDuracion(dias) {
      document.getElementById('ventaDias').value = dias;
      const c30 = document.getElementById('chipDias30');
      const c60 = document.getElementById('chipDias60');
      const c90 = document.getElementById('chipDias90');
      if (c30) c30.classList.toggle('active', dias === 30);
      if (c60) c60.classList.toggle('active', dias === 60);
      if (c90) c90.classList.toggle('active', dias === 90);
      recalcularVentaFechaFin();
    }

    function onVentaDiasChange() {
      const dias = parseInt(document.getElementById('ventaDias').value) || 30;
      const c30 = document.getElementById('chipDias30');
      const c60 = document.getElementById('chipDias60');
      const c90 = document.getElementById('chipDias90');
      if (c30) c30.classList.toggle('active', dias === 30);
      if (c60) c60.classList.toggle('active', dias === 60);
      if (c90) c90.classList.toggle('active', dias === 90);
      recalcularVentaFechaFin();
    }

    function onVentaFechaInicioChange() {
      recalcularVentaFechaFin();
    }

    function recalcularVentaFechaFin() {
      const inicio = document.getElementById('ventaFechaInicio').value;
      const dias = parseInt(document.getElementById('ventaDias').value) || 30;
      if (inicio) {
        const d = new Date(inicio + 'T00:00:00');
        const finDate = new Date(d.getTime() + dias * 24 * 60 * 60 * 1000);
        document.getElementById('ventaFechaFin').value = finDate.toISOString().split('T')[0];
      }
    }

    /* Modales Venta */
    function openNuevaVentaModal() {
      if (appState.cuentas.length === 0) {
        alert('Primero debes registrar al menos una Cuenta Maestra.');
        return;
      }
      document.getElementById('modalVentaTitle').textContent = 'Registrar Nueva Venta';
      document.getElementById('ventaId').value = '';
      document.getElementById('ventaCelular').value = '';
      document.getElementById('ventaPrecio').value = '';

      const hoy = new Date().toISOString().split('T')[0];
      document.getElementById('ventaFechaInicio').value = hoy;
      setVentaDuracion(30);

      const select = document.getElementById('ventaCuentaId');
      select.innerHTML = '';

      // Mostrar cuentas con el contador de ventas de cada una
      appState.cuentas.forEach(c => {
        const ventasCuenta = appState.ventas.filter(v => v.cuenta_id === c.id);
        const esInactiva = (c.estado || 'activa').toLowerCase() === 'inhabilitada';

        const opt = document.createElement('option');
        opt.value = c.id;
        opt.textContent = `${c.tipo_cuenta} - ${c.correo} • (${ventasCuenta.length} ventas)${esInactiva ? ' [INHABILITADA]' : ''}`;
        if (esInactiva) {
          opt.disabled = true;
        }
        select.appendChild(opt);
      });

      document.getElementById('modalVenta').classList.add('active');
    }

    function editVenta(id) {
      const v = appState.ventas.find(x => x.id === id);
      if (!v) return;
      document.getElementById('modalVentaTitle').textContent = 'Editar Venta #' + id;
      document.getElementById('ventaId').value = v.id;
      document.getElementById('ventaCelular').value = v.numero_celular_cliente || '';
      document.getElementById('ventaPrecio').value = v.precio_venta_por_perfil || '';
      document.getElementById('ventaDias').value = v.duracion_dias || 30;
      document.getElementById('ventaFechaInicio').value = v.fecha_inicio || '';
      document.getElementById('ventaFechaFin').value = v.fecha_fin || '';

      const select = document.getElementById('ventaCuentaId');
      select.innerHTML = '';
      appState.cuentas.forEach(c => {
        const ventasCuenta = appState.ventas.filter(x => x.cuenta_id === c.id);
        const opt = document.createElement('option');
        opt.value = c.id;
        opt.textContent = `${c.tipo_cuenta} - ${c.correo} • (${ventasCuenta.length} ventas)`;
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
      if (!cuentaId) {
        alert('Por favor selecciona una cuenta maestra válida.');
        return;
      }

      const precio = parseFloat(document.getElementById('ventaPrecio').value) || 0.0;
      const dias = parseInt(document.getElementById('ventaDias').value) || 30;
      const inicio = document.getElementById('ventaFechaInicio').value || new Date().toISOString().split('T')[0];
      const finStr = document.getElementById('ventaFechaFin').value;

      const venta = {
        id: idVal ? parseInt(idVal) : 0,
        cuenta_id: cuentaId,
        numero_celular_cliente: cel,
        cantidad_perfiles: 1, // Siempre 1 perfil por venta como en el celular
        precio_venta_por_perfil: precio,
        duracion_dias: dias,
        fecha_inicio: inicio,
        fecha_fin: finStr
      };

      const endpoint = idVal ? 'update_venta' : 'add_venta';
      await fetch(`/api/${endpoint}`, { method: 'POST', body: JSON.stringify(venta) });
      closeModal('modalVenta');

      // Copiar automáticamente el mensaje al portapapeles al guardar
      const c = appState.cuentas.find(x => x.id === cuentaId) || {};
      let t = appState.mensaje_template || DEFAULT_TEMPLATE;
      t = t.replace(/{servicio}/gi, c.tipo_cuenta || 'Servicio')
           .replace(/{dias}/gi, dias.toString())
           .replace(/{correo}/gi, c.correo || '')
           .replace(/{cuenta}/gi, c.correo || '')
           .replace(/{contrasena}/gi, c.contrasena_plataforma || '')
           .replace(/{clave}/gi, c.contrasena_plataforma || '')
           .replace(/{fecha_inicio}/gi, inicio)
           .replace(/{fecha_fin}/gi, finStr)
           .replace(/{perfil}/gi, 'Perfil 1')
           .replace(/{pin}/gi, '0000');

      try {
        await fetch('/api/clipboard', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ text: t })
        });
      } catch (e) {}
      fallbackCopyText(t);

      fetchState();
      showToast('✅ ¡Venta guardada y mensaje copiado al portapapeles!');
    }

    async function deleteVenta(id) {
      if (confirm(`¿Eliminar la venta #${id}?`)) {
        await fetch('/api/delete_venta', { method: 'POST', body: JSON.stringify({ id }) });
        fetchState();
        showToast('🗑️ Venta eliminada');
      }
    }

    function closeModal(modalId) {
      document.getElementById(modalId).classList.remove('active');
    }

    /* Limpiar Base de Datos */
    async function confirmarLimpiarBD() {
      const confirmacion = confirm(
        '⚠️ ¿Estás seguro de que deseas ELIMINAR TODA la base de datos local en esta PC?\n\n' +
        '• Se borrarán todas las cuentas maestras y todas las ventas registradas.\n' +
        '• Esto te permitirá importar una nueva base de datos completamente limpia.\n\n' +
        '¿Deseas continuar?'
      );
      if (confirmacion) {
        await fetch('/api/clear_db', { method: 'POST' });
        fetchState();
        showToast('🗑️ Base de datos eliminada. Todo limpio.');
      }
    }

    /* Importar y Exportar Backup */
    async function handleFileSelect(event) {
      const file = event.target.files[0];
      if (!file) return;

      const continuar = confirm(
        'ℹ️ NOTA DE IMPORTACIÓN:\n\n' +
        'Al importar este archivo de respaldo del celular, los datos de esta PC se sincronizarán y actualizarán con el contenido del backup.\n\n' +
        '¿Deseas continuar con la importación?'
      );

      if (!continuar) {
        event.target.value = '';
        return;
      }

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
        event.target.value = '';
      };
      reader.readAsText(file);
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
"##;

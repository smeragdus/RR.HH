import { Component, useEffect, useMemo, useState } from 'react'
import PropTypes from 'prop-types'
import {
  BriefcaseBusiness,
  CalendarCheck,
  ClipboardList,
  DoorOpen,
  Download,
  FileText,
  FileUp,
  Fingerprint,
  Search,
  RotateCcw,
  ShieldCheck,
  Upload,
  UserPlus,
  Users,
} from 'lucide-react'
import './App.css'

const API = import.meta.env.VITE_API_URL || '/api'
const roleLabels = { ADMIN: 'Administrador', RRHH: 'RR.HH.', JEFE: 'Jefe', EMPLEADO: 'Empleado' }
const initialEmployee = { firstName: '', lastName: '', dni: '', phone: '', email: '', position: '', area: '', location: '', employmentStatus: 'ACTIVO' }
const rowShape = PropTypes.object
const apiShape = PropTypes.shape({
  audit: PropTypes.func.isRequired,
  approveRequest: PropTypes.func.isRequired,
  deactivateEmployee: PropTypes.func.isRequired,
  downloadDocument: PropTypes.func.isRequired,
  downloadReport: PropTypes.func.isRequired,
  get: PropTypes.func.isRequired,
  importAttendance: PropTypes.func.isRequired,
  post: PropTypes.func.isRequired,
  reinstateEmployee: PropTypes.func.isRequired,
  rejectRequest: PropTypes.func.isRequired,
  report: PropTypes.func.isRequired,
  requestDocuments: PropTypes.func.isRequired,
  updateEmployee: PropTypes.func.isRequired,
  uploadContractDocument: PropTypes.func.isRequired,
  uploadRequestDocument: PropTypes.func.isRequired,
})
const userShape = PropTypes.shape({
  email: PropTypes.string,
  employeeName: PropTypes.string,
  role: PropTypes.string.isRequired,
})

function App() {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)
  const [view, setView] = useState('dashboard')
  const [error, setError] = useState('')

  const api = useMemo(() => createApi(setError), [])

  useEffect(() => {
    api.get('/auth/me').then((data) => {
      if (data) setUser(data)
      setLoading(false)
    }).catch(() => setLoading(false))
  }, [])

  function onLogin(response) {
    setUser(response.user)
    setView('dashboard')
  }

  async function logout() {
    await api.post('/auth/logout')
    setUser(null)
  }

  if (loading) return null
  if (!user) return <Login onLogin={onLogin} api={api} error={error} />

  const nav = navigationFor(user.role)

  return (
    <div className="shell">
      <aside className="sidebar">
        <div className="brand">
          <img src="/logo-mendoza.svg" alt="Grupo Mendoza Hnos. S.A.C." />
          <div>
            <strong>Grupo Mendoza</strong>
            <span>Recursos humanos</span>
          </div>
        </div>
        <nav>
          {nav.map((item) => (
            <button className={view === item.id ? 'active' : ''} key={item.id} onClick={() => setView(item.id)}>
              <item.icon size={18} />
              {item.label}
            </button>
          ))}
        </nav>
        <button className="logout" onClick={logout}>
          <DoorOpen size={18} />
          Cerrar sesión
        </button>
      </aside>
      <main>
        <header className="topbar">
          <div>
            <p>{roleLabels[user.role]}</p>
            <h1>{titleFor(view)}</h1>
          </div>
          <div className="identity">
            <span>{user.employeeName || user.email}</span>
            <small>{user.email}</small>
          </div>
        </header>
        {error && <div className="alert">{error}</div>}
        <ModuleBoundary resetKey={view}>
          {view === 'dashboard' && <Dashboard api={api} user={user} />}
          {view === 'employees' && <Employees api={api} />}
          {view === 'attendance' && <Attendance api={api} user={user} />}
          {view === 'requests' && <Requests api={api} user={user} />}
          {view === 'contracts' && <Contracts api={api} />}
          {view === 'reports' && <Reports api={api} />}
          {view === 'users' && <UsersPanel api={api} />}
          {view === 'audit' && <Audit api={api} />}
        </ModuleBoundary>
      </main>
    </div>
  )
}

class ModuleBoundary extends Component {
  static propTypes = {
    children: PropTypes.node,
    resetKey: PropTypes.string.isRequired,
  }

  constructor(props) {
    super(props)
    this.state = { error: null }
  }

  static getDerivedStateFromError(error) {
    return { error }
  }

  componentDidUpdate(previousProps) {
    if (previousProps.resetKey !== this.props.resetKey && this.state.error) {
      this.setState({ error: null })
    }
  }

  render() {
    if (this.state.error) {
      return (
        <div className="panel error-panel">
          <h2>No se pudo abrir este módulo</h2>
          <p>Intenta recargar la vista.</p>
        </div>
      )
    }
    return this.props.children
  }
}

function Login({ onLogin, api, error }) {
  const [email, setEmail] = useState('admin@sistemahr.local')
  const [password, setPassword] = useState('')

  async function submit(event) {
    event.preventDefault()
    const response = await api.post('/auth/login', { email, password })
    if (response?.user) onLogin(response)
  }

  return (
    <div className="login-page">
      <section className="login-copy">
        <img className="login-logo" src="/logo-mendoza.svg" alt="Grupo Mendoza Hnos. S.A.C." />
        <h1>Gestión laboral integrada</h1>
        <p>Personal, asistencia, contratos, solicitudes y auditoría para Grupo Mendoza Hnos. S.A.C.</p>
      </section>
      <form className="login-card" onSubmit={submit}>
        <h2>Acceso seguro</h2>
        <label>Correo<input value={email} onChange={(e) => setEmail(e.target.value)} /></label>
        <label>Contraseña<input type="password" value={password} onChange={(e) => setPassword(e.target.value)} /></label>
        {error && <div className="alert">{error}</div>}
        <button className="primary">Ingresar</button>
      </form>
    </div>
  )
}

Login.propTypes = {
  api: apiShape.isRequired,
  error: PropTypes.string,
  onLogin: PropTypes.func.isRequired,
}

function Dashboard({ api, user }) {
  const [notifications, setNotifications] = useState([])
  useEffect(() => { api.get('/notifications').then((data) => setNotifications(Array.isArray(data) ? data : [])) }, [])
  const cards = [
    ['Rol activo', roleLabels[user.role], ShieldCheck],
    ['Empleado vinculado', user.employeeName || 'Sin vinculo', Users],
    ['Notificaciones', notifications.length, ClipboardList],
  ]
  return (
    <section className="grid cards">
      {cards.map(([label, value, Icon]) => <article key={label}><Icon /><span>{label}</span><strong>{value}</strong></article>)}
      <div className="panel wide">
        <h2>Notificaciones internas</h2>
        <Table rows={notifications} columns={['message', 'createdAt']} empty="No hay notificaciones." />
      </div>
    </section>
  )
}

Dashboard.propTypes = {
  api: apiShape.isRequired,
  user: userShape.isRequired,
}

function Employees({ api }) {
  const [rows, setRows] = useState([])
  const [form, setForm] = useState(initialEmployee)
  const [editing, setEditing] = useState(null)
  const load = () => api.get('/employees').then((data) => setRows(Array.isArray(data) ? data : []))
  useEffect(() => { load() }, [])

  async function submit(event) {
    event.preventDefault()
    if (!/^\d{8}$/.test(form.dni || '')) {
      alert('El DNI debe tener exactamente 8 dígitos.')
      return
    }
    if (form.phone && !/^9\d{8}$/.test(form.phone)) {
      alert('El teléfono debe empezar por 9 y tener 9 dígitos.')
      return
    }
    const saved = editing ? await api.updateEmployee(editing, form) : await api.post('/employees', form)
    if (saved) {
      setForm(initialEmployee)
      setEditing(null)
      load()
    }
  }

  function edit(row) {
    setEditing(row.id)
    setForm({ ...initialEmployee, ...row })
  }

  function renderActions(row) {
    return (
      <>
        <button onClick={() => edit(row)}>Editar</button>
        {row.employmentStatus === 'ACTIVO'
          ? <button onClick={() => api.deactivateEmployee(row.id).then(load)}>Baja</button>
          : <button onClick={() => api.reinstateEmployee(row.id).then(load)}><RotateCcw size={16} /> Reincorporar</button>}
      </>
    )
  }

  return (
    <section className="split">
      <form className="panel form-grid" onSubmit={submit}>
        <h2>{editing ? 'Editar empleado' : 'Registrar empleado'}</h2>
        <label>{label('firstName')}<input value={form.firstName || ''} onChange={(e) => setForm({ ...form, firstName: e.target.value })} /></label>
        <label>{label('lastName')}<input value={form.lastName || ''} onChange={(e) => setForm({ ...form, lastName: e.target.value })} /></label>
        <label>{label('dni')}<input inputMode="numeric" maxLength={8} pattern="\d{8}" value={form.dni || ''} onChange={(e) => setForm({ ...form, dni: digits(e.target.value).slice(0, 8) })} /></label>
        <label>{label('phone')}<input inputMode="numeric" maxLength={9} pattern="9\d{8}" value={form.phone || ''} onChange={(e) => setForm({ ...form, phone: digits(e.target.value).slice(0, 9) })} /></label>
        {['email', 'position', 'area', 'location'].map((key) => (
          <label key={key}>{label(key)}<input value={form[key] || ''} onChange={(e) => setForm({ ...form, [key]: e.target.value })} /></label>
        ))}
        <button className="primary"><UserPlus size={17} /> Guardar</button>
      </form>
      <div className="panel">
        <h2>Empleados</h2>
        <Table rows={rows} columns={['firstName', 'lastName', 'dni', 'position', 'area', 'employmentStatus']} actions={renderActions} />
      </div>
    </section>
  )
}

Employees.propTypes = {
  api: apiShape.isRequired,
}

function Attendance({ api, user }) {
  const [rows, setRows] = useState([])
  const [file, setFile] = useState(null)
  const [workDate, setWorkDate] = useState(new Date().toISOString().slice(0, 10))
  const load = () => api.get('/attendance').then((data) => setRows(Array.isArray(data) ? data : []))
  useEffect(() => { load() }, [])
  async function uploadExcel(event) {
    event.preventDefault()
    if (!file) return
    const imported = await api.importAttendance(workDate, file)
    if (Array.isArray(imported)) setRows(imported)
    setFile(null)
  }
  return (
    <section className="grid module-grid">
      {user.role === 'EMPLEADO' && <div className="toolbar">
        <button className="primary" onClick={() => api.post('/attendance/check-in').then(load)}><Fingerprint size={17} /> Marcar entrada</button>
        <button onClick={() => api.post('/attendance/check-out').then(load)}><CalendarCheck size={17} /> Marcar salida</button>
      </div>}
      {['ADMIN', 'RRHH'].includes(user.role) && <form className="panel upload-strip" onSubmit={uploadExcel}>
        <div>
          <h2>Cargar Excel del huellero</h2>
          <p>Formato esperado: nombre, DNI, cargo, horas trabajadas y tardanza.</p>
        </div>
        <label>Fecha<input type="date" value={workDate} onChange={(e) => setWorkDate(e.target.value)} /></label>
        <label>Excel<input type="file" accept=".xlsx,.xls" onChange={(e) => setFile(e.target.files?.[0] || null)} /></label>
        <button className="primary"><Upload size={17} /> Importar</button>
      </form>}
      <div className="panel">
        <Table rows={rows} columns={['employeeName', 'dni', 'importedPosition', 'workDate', 'hoursWorked', 'checkIn', 'checkOut', 'status', 'late']} empty="Sin registros de asistencia." />
      </div>
    </section>
  )
}

Attendance.propTypes = {
  api: apiShape.isRequired,
  user: userShape.isRequired,
}

function Requests({ api, user }) {
  const [rows, setRows] = useState([])
  const [documents, setDocuments] = useState({})
  const [form, setForm] = useState({ type: 'PERMISO', startDate: '', endDate: '', reason: '' })
  const [file, setFile] = useState(null)

  async function loadRequestDocuments(nextRows) {
    const entries = await Promise.all(nextRows.map(async (row) => [row.id, await api.requestDocuments(row.id)]))
    setDocuments(Object.fromEntries(entries.map(([id, docs]) => [id, Array.isArray(docs) ? docs : []])))
  }

  const load = () => api.get('/requests').then((data) => {
    const nextRows = Array.isArray(data) ? data : []
    setRows(nextRows)
    loadRequestDocuments(nextRows)
  })
  useEffect(() => { load() }, [])

  async function submit(event) {
    event.preventDefault()
    const saved = await api.post('/requests', form)
    if (saved?.id && file) {
      await api.uploadRequestDocument(saved.id, file)
    }
    setForm({ type: 'PERMISO', startDate: '', endDate: '', reason: '' })
    setFile(null)
    load()
  }

  async function uploadFor(row, selectedFile) {
    if (!selectedFile) return
    await api.uploadRequestDocument(row.id, selectedFile)
    load()
  }

  return (
    <section className="split">
      {user.role !== 'JEFE' && <form className="panel form-grid" onSubmit={submit}>
        <h2>Nueva solicitud</h2>
        <label>Tipo<select value={form.type} onChange={(e) => setForm({ ...form, type: e.target.value })}>{['PERMISO', 'LICENCIA', 'VACACIONES', 'JUSTIFICACION'].map(x => <option key={x}>{x}</option>)}</select></label>
        <label>Inicio<input type="date" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} /></label>
        <label>Fin<input type="date" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} /></label>
        <label className="full">Motivo<textarea value={form.reason} onChange={(e) => setForm({ ...form, reason: e.target.value })} /></label>
        <label className="full">Comprobante PDF o imagen<input type="file" accept=".pdf,image/*" onChange={(e) => setFile(e.target.files?.[0] || null)} /></label>
        <button className="primary">Enviar</button>
      </form>}
      <div className="panel">
        <h2>Solicitudes</h2>
        <RequestTable rows={rows} documents={documents} api={api} user={user} load={load} uploadFor={uploadFor} />
      </div>
    </section>
  )
}

Requests.propTypes = {
  api: apiShape.isRequired,
  user: userShape.isRequired,
}

function Contracts({ api }) {
  const [employees, setEmployees] = useState([])
  const [rows, setRows] = useState([])
  const [form, setForm] = useState({ employeeId: '', contractType: '', startDate: '', endDate: '', status: 'VIGENTE' })
  const [file, setFile] = useState(null)
  const load = () => api.get('/contracts').then((data) => setRows(Array.isArray(data) ? data : []))
  useEffect(() => {
    api.get('/employees').then((data) => setEmployees(Array.isArray(data) ? data : []))
    load()
  }, [])
  async function submit(event) {
    event.preventDefault()
    const saved = await api.post('/contracts', { ...form, employeeId: Number(form.employeeId) })
    if (saved?.id && file) {
      await api.uploadContractDocument(saved.id, file)
    }
    setForm({ employeeId: '', contractType: '', startDate: '', endDate: '', status: 'VIGENTE' })
    setFile(null)
    load()
  }
  async function uploadFor(row, selectedFile) {
    if (!selectedFile) return
    await api.uploadContractDocument(row.id, selectedFile)
    load()
  }

  function renderActions(row) {
    return (
      <label className="inline-upload"><FileUp size={16} /> Archivo<input type="file" accept=".pdf,.doc,.docx" onChange={(e) => uploadFor(row, e.target.files?.[0])} /></label>
    )
  }

  return (
    <section className="split">
      <form className="panel form-grid" onSubmit={submit}>
        <h2>Contrato laboral</h2>
        <label>Empleado<select value={form.employeeId} onChange={(e) => setForm({ ...form, employeeId: e.target.value })}><option value="">Seleccione</option>{employees.map(e => <option value={e.id} key={e.id}>{e.firstName} {e.lastName}</option>)}</select></label>
        <label>Tipo<input value={form.contractType} onChange={(e) => setForm({ ...form, contractType: e.target.value })} /></label>
        <label>Inicio<input type="date" value={form.startDate} onChange={(e) => setForm({ ...form, startDate: e.target.value })} /></label>
        <label>Fin<input type="date" value={form.endDate} onChange={(e) => setForm({ ...form, endDate: e.target.value })} /></label>
        <label className="full">Contrato PDF o Word<input type="file" accept=".pdf,.doc,.docx,application/pdf,application/msword,application/vnd.openxmlformats-officedocument.wordprocessingml.document" onChange={(e) => setFile(e.target.files?.[0] || null)} /></label>
        <button className="primary">Registrar</button>
      </form>
      <div className="panel"><h2>Historial</h2><Table rows={rows} columns={['employeeName', 'contractType', 'startDate', 'endDate', 'status']} actions={renderActions} /></div>
    </section>
  )
}

Contracts.propTypes = {
  api: apiShape.isRequired,
}

function Reports({ api }) {
  const [type, setType] = useState('active-employees')
  const [rows, setRows] = useState([])
  const load = () => api.report(type).then((data) => setRows(Array.isArray(data) ? data : []))
  useEffect(() => { load() }, [type])
  const columns = reportColumns(type)
  return (
    <section className="panel">
      <div className="toolbar">
        <select value={type} onChange={(e) => setType(e.target.value)}>
          <option value="active-employees">Empleados activos</option>
          <option value="attendance">Asistencia</option>
          <option value="vacations">Vacaciones</option>
        </select>
        <button onClick={() => api.downloadReport(type, 'pdf')}><Download size={17} /> PDF</button>
        <button onClick={() => api.downloadReport(type, 'excel')}><Download size={17} /> Excel</button>
      </div>
      <Table rows={rows} columns={columns} />
    </section>
  )
}

Reports.propTypes = {
  api: apiShape.isRequired,
}

function UsersPanel({ api }) {
  const [rows, setRows] = useState([])
  const [employees, setEmployees] = useState([])
  const [form, setForm] = useState({ email: '', password: '', role: 'EMPLEADO', status: 'ACTIVO', employeeId: '' })
  const load = () => api.get('/users').then((data) => setRows(Array.isArray(data) ? data : []))
  useEffect(() => {
    load()
    api.get('/employees').then((data) => setEmployees(Array.isArray(data) ? data : []))
  }, [])
  async function submit(event) {
    event.preventDefault()
    await api.post('/users', { ...form, employeeId: form.employeeId ? Number(form.employeeId) : null })
    setForm({ email: '', password: '', role: 'EMPLEADO', status: 'ACTIVO', employeeId: '' })
    load()
  }
  return (
    <section className="split">
      <form className="panel form-grid" onSubmit={submit}>
        <h2>Usuario</h2>
        <label>Correo<input value={form.email} onChange={(e) => setForm({ ...form, email: e.target.value })} /></label>
        <label>Clave<input type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} /></label>
        <label>Rol<select value={form.role} onChange={(e) => setForm({ ...form, role: e.target.value })}>{Object.keys(roleLabels).map(r => <option key={r}>{r}</option>)}</select></label>
        <label>Empleado<select value={form.employeeId} onChange={(e) => setForm({ ...form, employeeId: e.target.value })}><option value="">Sin vinculo</option>{employees.map(e => <option value={e.id} key={e.id}>{e.firstName} {e.lastName}</option>)}</select></label>
        <button className="primary">Crear usuario</button>
      </form>
      <div className="panel"><h2>Usuarios</h2><Table rows={rows} columns={['email', 'role', 'status', 'employeeName']} /></div>
    </section>
  )
}

UsersPanel.propTypes = {
  api: apiShape.isRequired,
}

function Audit({ api }) {
  const [rows, setRows] = useState([])
  const [filter, setFilter] = useState('')
  const load = () => api.audit(filter).then((data) => setRows(Array.isArray(data) ? data : []))
  useEffect(() => { load() }, [])
  return (
    <section className="panel">
      <form className="toolbar audit-filter" onSubmit={(event) => { event.preventDefault(); load() }}>
        <label>Filtrar por usuario, DNI o cargo<input value={filter} onChange={(e) => setFilter(e.target.value)} placeholder="Ej. Rosa, 00000002, RRHH" /></label>
        <button className="primary"><Search size={17} /> Buscar</button>
        <button type="button" onClick={() => { setFilter(''); api.audit('').then((data) => setRows(Array.isArray(data) ? data : [])) }}>Limpiar</button>
      </form>
      <Table rows={rows} columns={['actorName', 'actorDni', 'actorPosition', 'action', 'module', 'occurredAt', 'description']} />
    </section>
  )
}

Audit.propTypes = {
  api: apiShape.isRequired,
}

function RequestTable({ rows = [], documents = {}, api, user, load, uploadFor }) {
  if (!rows.length) return <div className="empty">Sin solicitudes.</div>
  return (
    <div className="table-wrap">
      <table>
        <thead><tr>{['Empleado', 'Tipo', 'Inicio', 'Fin', 'Estado', 'Motivo rechazo', 'Comprobantes', 'Acciones'].map(h => <th key={h}>{h}</th>)}</tr></thead>
        <tbody>{rows.map((row) => (
          <tr key={row.id}>
            <td>{row.employeeName}</td>
            <td>{row.type}</td>
            <td>{row.startDate}</td>
            <td>{row.endDate}</td>
            <td>{row.status}</td>
            <td>{row.rejectionReason || ''}</td>
            <td>
              <div className="document-list">
                {(documents[row.id] || []).length ? documents[row.id].map((doc) => (
                  <button key={doc.id} type="button" onClick={() => api.downloadDocument(doc.id, doc.originalName)}>
                    <FileText size={15} /> {doc.originalName}
                  </button>
                )) : <span className="muted">Sin archivo</span>}
              </div>
            </td>
            <td className="actions">
              {user.role !== 'EMPLEADO' && row.status === 'PENDIENTE' && <>
                <button onClick={() => api.approveRequest(row.id).then(load)}>Aprobar</button>
                <button onClick={() => {
                  const reason = globalThis.prompt('Motivo de rechazo')
                  if (reason) api.rejectRequest(row.id, reason).then(load)
                }}>Rechazar</button>
              </>}
              {user.role !== 'EMPLEADO' && <label className="inline-upload"><FileUp size={16} /> Adjuntar<input type="file" accept=".pdf,image/*" onChange={(e) => uploadFor(row, e.target.files?.[0])} /></label>}
            </td>
          </tr>
        ))}</tbody>
      </table>
    </div>
  )
}

RequestTable.propTypes = {
  api: apiShape.isRequired,
  documents: PropTypes.objectOf(PropTypes.arrayOf(rowShape)),
  load: PropTypes.func.isRequired,
  rows: PropTypes.arrayOf(rowShape),
  uploadFor: PropTypes.func.isRequired,
  user: userShape.isRequired,
}

function Table({ rows = [], columns, actions, empty = 'Sin datos.' }) {
  if (!rows?.length) return <div className="empty">{empty}</div>
  return (
    <div className="table-wrap">
      <table>
        <thead><tr>{columns.map(c => <th key={c}>{label(c)}</th>)}{actions && <th>Acciones</th>}</tr></thead>
        <tbody>{rows.map((row) => <tr key={row.id || JSON.stringify(row)}>{columns.map(c => <td key={c}>{String(row[c] ?? '')}</td>)}{actions && <td className="actions">{actions(row)}</td>}</tr>)}</tbody>
      </table>
    </div>
  )
}

Table.propTypes = {
  actions: PropTypes.func,
  columns: PropTypes.arrayOf(PropTypes.string).isRequired,
  empty: PropTypes.string,
  rows: PropTypes.arrayOf(rowShape),
}

function createApi(setError) {
  async function request(method, path, body) {
    setError('')
    const headers = { 'Content-Type': 'application/json' }
    const csrf = await csrfToken(method)
    if (csrf) headers['X-XSRF-TOKEN'] = csrf
    let response
    try {
      response = await fetch(`${API}${path}`, { method, headers, credentials: 'include', body: body ? JSON.stringify(body) : undefined })
    } catch {
      setError('No se pudo conectar con el backend.')
      return null
    }
    if (!response.ok) {
      const data = await response.json().catch(() => ({}))
      setError(data.message || 'No se pudo completar la operación')
      return null
    }
    if (response.status === 204) return null
    return response.json().catch(() => null)
  }
  return {
    get: (path) => request('GET', path),
    post: (path, body) => request('POST', path, body),
    put: (path, body) => request('PUT', path, body),
    patch: (path, body) => request('PATCH', path, body),
    updateEmployee: (id, body) => request('PUT', employeePath(id), body),
    deactivateEmployee: (id) => request('PATCH', `${employeePath(id)}/deactivate`),
    reinstateEmployee: (id) => request('PATCH', `${employeePath(id)}/reinstate`),
    importAttendance: (workDate, file) => uploadFile(attendanceImportPath(workDate), file),
    report: (type) => request('GET', reportPath(type)),
    downloadReport: (type, format) => {
      const report = reportType(type)
      const output = reportFormat(format)
      return downloadFile(reportExportPath(report, output), `reporte-${report}.${output === 'excel' ? 'xlsx' : 'pdf'}`)
    },
    audit: (filter) => request('GET', auditPath(filter)),
    requestDocuments: (id) => request('GET', requestDocumentsPath(id)),
    approveRequest: (id) => request('PATCH', requestActionPath(id, 'approve')),
    rejectRequest: (id, reason) => request('PATCH', requestActionPath(id, 'reject'), { reason }),
    uploadRequestDocument: (id, file) => uploadFile(requestDocumentsPath(id), file),
    uploadContractDocument: (id, file) => uploadFile(contractDocumentsPath(id), file),
    downloadDocument: (id, filename) => downloadFile(documentDownloadPath(id), filename),
  }

  async function uploadFile(path, file) {
      setError('')
      const formData = new FormData()
      formData.append('file', file)
      const headers = {}
      const csrf = await csrfToken('POST')
      if (csrf) headers['X-XSRF-TOKEN'] = csrf
      let response
      try {
        response = await fetch(`${API}${path}`, { method: 'POST', headers, credentials: 'include', body: formData })
      } catch {
        setError('No se pudo cargar el archivo')
        return null
      }
      if (!response.ok) {
        const data = await response.json().catch(() => ({}))
        setError(data.message || 'No se pudo cargar el archivo')
        return null
      }
      return response.json().catch(() => null)
  }

  async function downloadFile(path, filename) {
      setError('')
      const response = await fetch(`${API}${path}`, { credentials: 'include' })
      if (!response.ok) {
        setError('No se pudo descargar el reporte')
        return
      }
      const blob = await response.blob()
      const url = globalThis.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = filename
      link.click()
      globalThis.URL.revokeObjectURL(url)
  }
}

function numericId(value) {
  const id = Number(value)
  if (!Number.isSafeInteger(id) || id <= 0) {
    throw new Error('Identificador invalido')
  }
  return id
}

function employeePath(id) {
  return `/employees/${numericId(id)}`
}

function attendanceImportPath(workDate) {
  return `/attendance/import?workDate=${dateValue(workDate)}`
}

function reportPath(type) {
  return `/reports/${reportType(type)}`
}

function reportExportPath(report, output) {
  return `/reports/${reportType(report)}/export/${reportFormat(output)}`
}

function requestDocumentsPath(id) {
  return `/requests/${numericId(id)}/documents`
}

function requestActionPath(id, action) {
  if (action !== 'approve' && action !== 'reject') {
    throw new Error('Accion invalida')
  }
  return `/requests/${numericId(id)}/${action}`
}

function contractDocumentsPath(id) {
  return `/contracts/${numericId(id)}/documents`
}

function documentDownloadPath(id) {
  return `/documents/${numericId(id)}/download`
}

function dateValue(value) {
  const date = String(value || '')
  if (!/^\d{4}-\d{2}-\d{2}$/.test(date)) {
    throw new Error('Fecha invalida')
  }
  return date
}

function reportType(value) {
  const allowed = {
    'active-employees': 'employees',
    employees: 'employees',
    attendance: 'attendance',
    vacations: 'vacations',
  }
  const report = allowed[value]
  if (!report) {
    throw new Error('Reporte invalido')
  }
  return report
}

function reportFormat(value) {
  if (value !== 'pdf' && value !== 'excel') {
    throw new Error('Formato invalido')
  }
  return value
}

function reportColumns(type) {
  if (type === 'attendance') return ['employeeName', 'workDate', 'checkIn', 'checkOut', 'status']
  if (type === 'vacations') return ['employeeName', 'startDate', 'endDate', 'status']
  return ['firstName', 'lastName', 'position', 'area', 'location']
}

function auditPath(filter) {
  const query = String(filter || '').trim()
  if (!query) return '/audit'
  return `/audit?${new URLSearchParams({ user: query }).toString()}`
}

async function csrfToken(method) {
  if (method === 'GET') return ''
  if (cachedCsrfToken) return cachedCsrfToken
  const response = await fetch(`${API}/auth/csrf`, { credentials: 'include' })
  if (!response.ok) return ''
  const data = await response.json().catch(() => ({}))
  cachedCsrfToken = data.token || ''
  return cachedCsrfToken
}

let cachedCsrfToken = ''

function navigationFor(role) {
  return [
    { id: 'dashboard', label: 'Panel', icon: ShieldCheck },
    ...(['ADMIN', 'RRHH', 'JEFE'].includes(role) ? [{ id: 'employees', label: 'Empleados', icon: Users }] : []),
    { id: 'attendance', label: 'Asistencia', icon: Fingerprint },
    { id: 'requests', label: 'Solicitudes', icon: ClipboardList },
    ...(['ADMIN', 'RRHH'].includes(role) ? [{ id: 'contracts', label: 'Contratos', icon: BriefcaseBusiness }, { id: 'reports', label: 'Reportes', icon: FileText }] : []),
    ...(role === 'ADMIN' ? [{ id: 'users', label: 'Usuarios', icon: UserPlus }, { id: 'audit', label: 'Auditoria', icon: ShieldCheck }] : []),
  ]
}

function titleFor(view) {
  return { dashboard: 'Panel operativo', employees: 'Gestión de empleados', attendance: 'Control de asistencia', requests: 'Solicitudes y permisos', contracts: 'Contratos laborales', reports: 'Reportes', users: 'Usuarios y roles', audit: 'Auditoría' }[view]
}

function label(key) {
  return {
    firstName: 'Nombres', lastName: 'Apellidos', dni: 'DNI', phone: 'Teléfono', email: 'Correo', position: 'Cargo', area: 'Área', location: 'Sede', employmentStatus: 'Estado', employeeName: 'Empleado', importedPosition: 'Cargo huellero', hoursWorked: 'Horas trabajadas', workDate: 'Fecha', checkIn: 'Entrada', checkOut: 'Salida', status: 'Estado', late: 'Tardanza', type: 'Tipo', startDate: 'Inicio', endDate: 'Fin', rejectionReason: 'Motivo rechazo', contractType: 'Contrato', role: 'Rol', actorEmail: 'Usuario', actorName: 'Nombre', actorDni: 'DNI', actorPosition: 'Cargo', action: 'Acción', module: 'Módulo', occurredAt: 'Hora', message: 'Mensaje', createdAt: 'Fecha', description: 'Detalle'
  }[key] || key
}

function digits(value) {
  return String(value || '').replace(/\D/g, '')
}

export default App

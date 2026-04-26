import { NavLink } from 'react-router-dom'
import { useAuth } from '../../context/AuthContext'

const menuItems = [
  { path: '/', label: 'Dashboard', icon: '📊' },
  { path: '/employees', label: 'Empleados', icon: '👥', roles: ['ADMIN', 'RRHH'] },
  { path: '/attendance', label: 'Asistencia', icon: '📅' },
  { path: '/requests', label: 'Solicitudes', icon: '📋' },
  { path: '/contracts', label: 'Contratos', icon: '📄', roles: ['ADMIN', 'RRHH'] },
  { path: '/reports', label: 'Reportes', icon: '📈', roles: ['ADMIN', 'RRHH'] },
]

export default function Sidebar() {
  const { user } = useAuth()

  const filteredItems = menuItems.filter(
    (item) => !item.roles || (user?.rol && item.roles.includes(user.rol))
  )

  return (
    <aside className="w-64 bg-slate-800 text-white">
      <div className="p-4">
        <h1 className="text-xl font-bold text-white">RR.HH.</h1>
        <p className="text-xs text-gray-400">Sistema de Gestión</p>
      </div>
      <nav className="mt-4">
        {filteredItems.map((item) => (
          <NavLink
            key={item.path}
            to={item.path}
            className={({ isActive }) =>
              `flex items-center px-4 py-3 text-sm ${
                isActive
                  ? 'bg-slate-700 border-l-4 border-blue-500'
                  : 'hover:bg-slate-700 border-l-4 border-transparent'
              }`
            }
          >
            <span className="mr-3 text-lg">{item.icon}</span>
            {item.label}
          </NavLink>
        ))}
      </nav>
    </aside>
  )
}

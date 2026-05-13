import { useEffect, useState } from 'react'
import { useAuth } from '../context/AuthContext'
import { attendanceApi } from '../api/attendanceApi'
import { requestApi } from '../api/requestApi'
import { employeeApi } from '../api/employeeApi'
import { Attendance, Page } from '../types'
import Loading from '../components/common/Loading'

export default function Dashboard() {
  const { user } = useAuth()
  const [todayAttendance, setTodayAttendance] = useState<Attendance | null>(null)
  const [pendingRequests, setPendingRequests] = useState<number>(0)
  const [stats, setStats] = useState({ employees: 0, attendance: 0 })
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadDashboardData()
  }, [])

  const loadDashboardData = async () => {
    try {
      let attendanceData: Attendance | null = null
      let requestsData = 0
      let employeesCount = 0

      if (user?.empleadoId) {
        try {
          const attendanceRes = await attendanceApi.getMyToday()
          if (attendanceRes.success && attendanceRes.data) {
            attendanceData = attendanceRes.data
          }
        } catch (e) {
          // ignore
        }
      }

      try {
        const requestsRes = await requestApi.getPending(0, 10)
        if (requestsRes.success && requestsRes.data) {
          requestsData = (requestsRes.data as Page<any>).totalElements || 0
        }
      } catch (e) {
        // ignore
      }

      try {
        const employeesRes = await employeeApi.getAll(0, 1)
        if (employeesRes.success && employeesRes.data) {
          employeesCount = (employeesRes.data as Page<any>).totalElements || 0
        }
      } catch (e) {
        // ignore
      }

      setTodayAttendance(attendanceData)
      setPendingRequests(requestsData)
      setStats({ employees: employeesCount, attendance: 0 })
    } catch (error) {
      console.error('Error loading dashboard:', error)
    } finally {
      setLoading(false)
    }
  }

  if (loading) return <Loading />

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold font-display text-gray-900">Dashboard</h1>
          <p className="text-gray-500 text-sm mt-1">Resumen de tu actividad y métricas clave</p>
        </div>
        <div className="hidden sm:flex items-center gap-2 px-4 py-2 bg-white rounded-xl border border-gray-100 shadow-sm">
          <svg className="w-5 h-5 text-brand-500" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
          </svg>
          <span className="text-sm font-medium text-gray-600">{new Date().toLocaleDateString('es-ES')}</span>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-5">
        <div className="card card-hover p-6 animate-stagger">
          <div className="flex items-start justify-between">
            <div>
              <div className="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-gradient-to-br from-brand-100 to-brand-200 mb-4">
                <svg className="w-6 h-6 text-brand-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
                </svg>
              </div>
              <div className="text-3xl font-bold font-display text-gray-900">{stats.employees}</div>
              <div className="text-sm text-gray-500 mt-1">Total Empleados</div>
            </div>
            <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-brand-100 text-brand-700">+0 hoy</span>
          </div>
        </div>

        <div className="card card-hover p-6 animate-stagger">
          <div className="flex items-start justify-between">
            <div>
              <div className="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-gradient-to-br from-amber-100 to-amber-200 mb-4">
                <svg className="w-6 h-6 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
                </svg>
              </div>
              <div className="text-3xl font-bold font-display text-gray-900">{pendingRequests}</div>
              <div className="text-sm text-gray-500 mt-1">Solicitudes Pendientes</div>
            </div>
            <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-amber-100 text-amber-700">Revision</span>
          </div>
        </div>

        <div className="card card-hover p-6 animate-stagger">
          <div className="flex items-start justify-between">
            <div>
              <div className="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-gradient-to-br from-emerald-100 to-emerald-200 mb-4">
                <svg className="w-6 h-6 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                </svg>
              </div>
              <div className="text-3xl font-bold font-display text-gray-900">
                {todayAttendance ? 'Presente' : 'Ausente'}
              </div>
              <div className="text-sm text-gray-500 mt-1">Asistencia Hoy</div>
            </div>
            <span className={`inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium ${todayAttendance ? 'bg-emerald-100 text-emerald-700' : 'bg-gray-100 text-gray-600'}`}>
              {todayAttendance ? 'OK' : '—'}
            </span>
          </div>
        </div>

        <div className="card card-hover p-6 animate-stagger">
          <div className="flex items-start justify-between">
            <div>
              <div className="inline-flex items-center justify-center w-12 h-12 rounded-xl bg-gradient-to-br from-violet-100 to-violet-200 mb-4">
                <svg className="w-6 h-6 text-violet-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                </svg>
              </div>
              <div className="text-lg font-bold font-display text-gray-900">{user?.rol}</div>
              <div className="text-sm text-gray-500 mt-1">Tu Rol</div>
            </div>
            <span className="inline-flex items-center px-2.5 py-1 rounded-full text-xs font-medium bg-violet-100 text-violet-700">Activo</span>
          </div>
        </div>
      </div>

      {todayAttendance && (
        <div className="card card-hover p-6">
          <div className="flex items-center gap-3 mb-5">
            <div className="inline-flex items-center justify-center w-10 h-10 rounded-lg bg-gradient-to-br from-brand-500 to-brand-600">
              <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <div>
              <h2 className="text-lg font-semibold font-display text-gray-900">Mi Asistencia de Hoy</h2>
              <p className="text-sm text-gray-500">{todayAttendance.fecha}</p>
            </div>
          </div>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div className="p-4 bg-gray-50/50 rounded-xl border border-gray-100">
              <div className="text-xs font-medium text-gray-500 uppercase tracking-wide mb-1">Fecha</div>
              <div className="font-semibold text-gray-900">{todayAttendance.fecha}</div>
            </div>
            <div className="p-4 bg-gray-50/50 rounded-xl border border-gray-100">
              <div className="text-xs font-medium text-gray-500 uppercase tracking-wide mb-1">Entrada</div>
              <div className="font-semibold text-gray-900">{todayAttendance.horaEntrada || '—'}</div>
            </div>
            <div className="p-4 bg-gray-50/50 rounded-xl border border-gray-100">
              <div className="text-xs font-medium text-gray-500 uppercase tracking-wide mb-1">Salida</div>
              <div className="font-semibold text-gray-900">{todayAttendance.horaSalida || '—'}</div>
            </div>
            <div className="p-4 bg-gray-50/50 rounded-xl border border-gray-100">
              <div className="text-xs font-medium text-gray-500 uppercase tracking-wide mb-1">Horas</div>
              <div className="font-semibold text-gray-900">{todayAttendance.horasTrabajadas || '—'}</div>
            </div>
          </div>
        </div>
      )}

      <div className="card card-hover p-6">
        <div className="flex items-center gap-3 mb-4">
          <div className="inline-flex items-center justify-center w-10 h-10 rounded-lg bg-gradient-to-br from-brand-500 to-brand-600">
            <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h2 className="text-lg font-semibold font-display text-gray-900">Bienvenido al Sistema</h2>
            <p className="text-sm text-gray-500">Resumen de funciones</p>
          </div>
        </div>
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          <div className="p-4 bg-gradient-to-br from-brand-50 to-brand-100/30 rounded-xl border border-brand-200/50">
            <div className="flex items-center gap-2 mb-2">
              <svg className="w-5 h-5 text-brand-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4.354a4 4 0 110 5.292M15 21H3v-1a6 6 0 0112 0v1zm0 0h6v-1a6 6 0 00-9-5.197M13 7a4 4 0 11-8 0 4 4 0 018 0z" />
              </svg>
              <span className="font-semibold text-gray-900">Empleados</span>
            </div>
            <p className="text-sm text-gray-600">Gestiona el directorio de empleados con información detallada y estados.</p>
          </div>
          <div className="p-4 bg-gradient-to-br from-emerald-50 to-emerald-100/30 rounded-xl border border-emerald-200/50">
            <div className="flex items-center gap-2 mb-2">
              <svg className="w-5 h-5 text-emerald-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
              <span className="font-semibold text-gray-900">Asistencia</span>
            </div>
            <p className="text-sm text-gray-600">Controla y registra la asistencia diaria de todos los empleados.</p>
          </div>
          <div className="p-4 bg-gradient-to-br from-amber-50 to-amber-100/30 rounded-xl border border-amber-200/50">
            <div className="flex items-center gap-2 mb-2">
              <svg className="w-5 h-5 text-amber-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
              </svg>
              <span className="font-semibold text-gray-900">Solicitudes</span>
            </div>
            <p className="text-sm text-gray-600">Procesa solicitudes de vacaciones, permisos y demás.</p>
          </div>
        </div>
      </div>
    </div>
  )
}
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
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Dashboard</h1>

      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6 mb-8">
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-3xl font-bold text-blue-500">{stats.employees}</div>
          <div className="text-gray-500">Total Empleados</div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-3xl font-bold text-green-500">{pendingRequests}</div>
          <div className="text-gray-500">Solicitudes Pendientes</div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-3xl font-bold text-purple-500">
            {todayAttendance ? '✓' : '○'}
          </div>
          <div className="text-gray-500">Asistencia Hoy</div>
        </div>
        <div className="bg-white rounded-lg shadow p-6">
          <div className="text-3xl font-bold text-orange-500">{user?.rol}</div>
          <div className="text-gray-500">Tu Rol</div>
        </div>
      </div>

      {todayAttendance && (
        <div className="bg-white rounded-lg shadow p-6 mb-6">
          <h2 className="text-lg font-semibold mb-4">Mi Asistencia de Hoy</h2>
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div>
              <div className="text-sm text-gray-500">Fecha</div>
              <div className="font-medium">{todayAttendance.fecha}</div>
            </div>
            <div>
              <div className="text-sm text-gray-500">Entrada</div>
              <div className="font-medium">{todayAttendance.horaEntrada || '—'}</div>
            </div>
            <div>
              <div className="text-sm text-gray-500">Salida</div>
              <div className="font-medium">{todayAttendance.horaSalida || '—'}</div>
            </div>
            <div>
              <div className="text-sm text-gray-500">Horas</div>
              <div className="font-medium">{todayAttendance.horasTrabajadas || '—'}</div>
            </div>
          </div>
        </div>
      )}

      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold mb-4">Bienvenido al Sistema de RR.HH.</h2>
        <p className="text-gray-600">
          Este sistema te permite gestionar empleados, controlar asistencia, procesar solicitudes
          y generar reportes. Utiliza el menú lateral para navegar entre las diferentes secciones.
        </p>
      </div>
    </div>
  )
}

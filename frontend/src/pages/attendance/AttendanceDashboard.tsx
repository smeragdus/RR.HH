import { useEffect, useState } from 'react'
import { attendanceApi } from '../../api/attendanceApi'
import { useAuth } from '../../context/AuthContext'
import { Attendance } from '../../types'
import Table from '../../components/common/Table'
import Button from '../../components/common/Button'
import Loading from '../../components/common/Loading'

export default function AttendanceDashboard() {
  const { user } = useAuth()
  const [myAttendance, setMyAttendance] = useState<Attendance | null>(null)
  const [todayAttendance, setTodayAttendance] = useState<Attendance[]>([])
  const [loading, setLoading] = useState(true)
  const [actionLoading, setActionLoading] = useState(false)

  useEffect(() => {
    loadData()
  }, [])

  const loadData = async () => {
    setLoading(true)
    try {
      if (user?.empleadoId) {
        try {
          const myRes = await attendanceApi.getMyToday()
          if (myRes.success && myRes.data) {
            setMyAttendance(myRes.data)
          }
        } catch (e) {
          // ignore
        }
      }

      const todayRes = await attendanceApi.getToday()
      if (todayRes.success && todayRes.data) {
        setTodayAttendance(Array.isArray(todayRes.data) ? todayRes.data : [])
      }
    } catch (error) {
      console.error('Error loading attendance:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleCheckIn = async () => {
    setActionLoading(true)
    try {
      await attendanceApi.checkIn()
      await loadData()
    } catch (error) {
      console.error('Error checking in:', error)
    } finally {
      setActionLoading(false)
    }
  }

  const handleCheckOut = async () => {
    setActionLoading(true)
    try {
      await attendanceApi.checkOut()
      await loadData()
    } catch (error) {
      console.error('Error checking out:', error)
    } finally {
      setActionLoading(false)
    }
  }

  const columns = [
    { key: 'numeroEmpleado', label: 'No. Empleado' },
    { key: 'nombreEmpleado', label: 'Nombre' },
    { key: 'horaEntrada', label: 'Entrada' },
    { key: 'horaSalida', label: 'Salida' },
    { key: 'horasTrabajadas', label: 'Horas' },
    { key: 'tipoJornada', label: 'Tipo' },
  ]

  if (loading) return <Loading />

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold font-display text-gray-900">Control de Asistencia</h1>
        <p className="text-sm text-gray-500 mt-1">Registro y control de asistencia diaria</p>
      </div>

      <div className="card p-6">
        <div className="flex items-center gap-3 mb-5">
          <div className="inline-flex items-center justify-center w-10 h-10 rounded-lg bg-gradient-to-br from-brand-500 to-brand-600">
            <svg className="w-5 h-5 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z" />
            </svg>
          </div>
          <div>
            <h2 className="text-lg font-semibold font-display text-gray-900">Mi Asistencia de Hoy</h2>
            <p className="text-sm text-gray-500">{new Date().toLocaleDateString('es-ES', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</p>
          </div>
        </div>

        <div className="flex flex-wrap gap-3 mb-6">
          <Button
            onClick={handleCheckIn}
            variant="success"
            disabled={actionLoading || !!myAttendance?.horaEntrada}
          >
            {myAttendance?.horaEntrada ? (
              <span className="flex items-center gap-2">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                </svg>
                Entrada Registrada - {myAttendance.horaEntrada}
              </span>
            ) : 'Registrar Entrada'}
          </Button>
          <Button
            onClick={handleCheckOut}
            variant="danger"
            disabled={actionLoading || !myAttendance?.horaEntrada || !!myAttendance?.horaSalida}
          >
            {myAttendance?.horaSalida ? (
              <span className="flex items-center gap-2">
                <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
                </svg>
                Salida Registrada - {myAttendance.horaSalida}
              </span>
            ) : 'Registrar Salida'}
          </Button>
        </div>

        {myAttendance && (
          <div className="grid grid-cols-2 md:grid-cols-4 gap-4 p-4 bg-gray-50/50 rounded-xl border border-gray-100">
            <div className="p-3 bg-white rounded-lg border border-gray-100">
              <div className="text-xs font-medium text-gray-500 uppercase tracking-wide mb-1">Fecha</div>
              <div className="font-semibold text-gray-900">{myAttendance.fecha}</div>
            </div>
            <div className="p-3 bg-white rounded-lg border border-gray-100">
              <div className="text-xs font-medium text-gray-500 uppercase tracking-wide mb-1">Entrada</div>
              <div className="font-semibold text-gray-900">{myAttendance.horaEntrada || '—'}</div>
            </div>
            <div className="p-3 bg-white rounded-lg border border-gray-100">
              <div className="text-xs font-medium text-gray-500 uppercase tracking-wide mb-1">Salida</div>
              <div className="font-semibold text-gray-900">{myAttendance.horaSalida || '—'}</div>
            </div>
            <div className="p-3 bg-white rounded-lg border border-gray-100">
              <div className="text-xs font-medium text-gray-500 uppercase tracking-wide mb-1">Horas</div>
              <div className="font-semibold text-gray-900">{myAttendance.horasTrabajadas || '—'}</div>
            </div>
          </div>
        )}
      </div>

      {(user?.rol === 'ADMIN' || user?.rol === 'RRHH') && (
        <div className="card">
          <div className="px-6 py-4 border-b border-gray-100">
            <div className="flex items-center gap-3">
              <div className="inline-flex items-center justify-center w-8 h-8 rounded-lg bg-gradient-to-br from-brand-500 to-brand-600">
                <svg className="w-4 h-4 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z" />
                </svg>
              </div>
              <h2 className="text-lg font-semibold font-display text-gray-900">Asistencia de Hoy - Todos</h2>
            </div>
          </div>
          <Table data={todayAttendance} columns={columns} />
        </div>
      )}
    </div>
  )
}
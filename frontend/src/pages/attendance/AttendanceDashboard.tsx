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
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Control de Asistencia</h1>

      <div className="bg-white rounded-lg shadow p-6 mb-6">
        <h2 className="text-lg font-semibold mb-4">Mi Asistencia de Hoy</h2>
        <div className="flex gap-4">
          <Button
            onClick={handleCheckIn}
            variant="success"
            disabled={actionLoading || !!myAttendance?.horaEntrada}
          >
            {myAttendance?.horaEntrada ? '✓ Entrada Registrada' : 'Registrar Entrada'}
          </Button>
          <Button
            onClick={handleCheckOut}
            variant="danger"
            disabled={actionLoading || !myAttendance?.horaEntrada || !!myAttendance?.horaSalida}
          >
            {myAttendance?.horaSalida ? '✓ Salida Registrada' : 'Registrar Salida'}
          </Button>
        </div>

        {myAttendance && (
          <div className="mt-4 p-4 bg-gray-50 rounded">
            <div className="grid grid-cols-4 gap-4 text-sm">
              <div>
                <span className="text-gray-500">Fecha:</span>{' '}
                <span className="font-medium">{myAttendance.fecha}</span>
              </div>
              <div>
                <span className="text-gray-500">Entrada:</span>{' '}
                <span className="font-medium">{myAttendance.horaEntrada || '—'}</span>
              </div>
              <div>
                <span className="text-gray-500">Salida:</span>{' '}
                <span className="font-medium">{myAttendance.horaSalida || '—'}</span>
              </div>
              <div>
                <span className="text-gray-500">Horas:</span>{' '}
                <span className="font-medium">{myAttendance.horasTrabajadas || '—'}</span>
              </div>
            </div>
          </div>
        )}
      </div>

      {(user?.rol === 'ADMIN' || user?.rol === 'RRHH') && (
        <div className="bg-white rounded-lg shadow">
          <div className="p-4 border-b">
            <h2 className="text-lg font-semibold">Asistencia de Hoy - Todos</h2>
          </div>
          <Table data={todayAttendance} columns={columns} />
        </div>
      )}
    </div>
  )
}

import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { requestApi } from '../../api/requestApi'
import { useAuth } from '../../context/AuthContext'
import { Request, Page, RequestStatus } from '../../types'
import Table from '../../components/common/Table'
import Pagination from '../../components/common/Pagination'
import Button from '../../components/common/Button'
import Modal from '../../components/common/Modal'
import Loading from '../../components/common/Loading'

export default function RequestList() {
  const { user } = useAuth()
  const [requests, setRequests] = useState<Request[]>([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)
  const [actionModal, setActionModal] = useState<{ request: Request; action: 'approve' | 'reject' } | null>(null)
  const [comentarios, setComentarios] = useState('')
  const [actionLoading, setActionLoading] = useState(false)

  const isAdmin = user?.rol === 'ADMIN' || user?.rol === 'RRHH'

  useEffect(() => {
    loadRequests()
  }, [page])

  const loadRequests = async () => {
    setLoading(true)
    try {
      const response = isAdmin
        ? await requestApi.getAll(page, 20)
        : await requestApi.getMyRequests(page, 20)
      if (response.success && response.data) {
        const data = response.data as Page<Request>
        setRequests(data.content)
        setTotalPages(data.totalPages)
      }
    } catch (error) {
      console.error('Error loading requests:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleAction = async () => {
    if (!actionModal) return
    setActionLoading(true)
    try {
      if (actionModal.action === 'approve') {
        await requestApi.approve(actionModal.request.id, comentarios)
      } else {
        await requestApi.reject(actionModal.request.id, comentarios)
      }
      setActionModal(null)
      setComentarios('')
      loadRequests()
    } catch (error) {
      console.error('Error performing action:', error)
    } finally {
      setActionLoading(false)
    }
  }

  const handleCancel = async (id: number) => {
    try {
      await requestApi.cancel(id)
      loadRequests()
    } catch (error) {
      console.error('Error canceling request:', error)
    }
  }

  const columns = [
    { key: 'numeroEmpleado', label: 'No. Empleado' },
    { key: 'nombreEmpleado', label: 'Empleado' },
    { key: 'tipo', label: 'Tipo' },
    { key: 'fechaInicio', label: 'Inicio' },
    { key: 'fechaFin', label: 'Fin' },
    { key: 'diasSolicitados', label: 'Días' },
    {
      key: 'estado',
      label: 'Estado',
      render: (req: Request) => {
        const colors: Record<RequestStatus, string> = {
          PENDIENTE: 'bg-yellow-100 text-yellow-800',
          APROBADA: 'bg-green-100 text-green-800',
          RECHAZADA: 'bg-red-100 text-red-800',
          CANCELADA: 'bg-gray-100 text-gray-800',
        }
        return (
          <span className={`px-2 py-1 rounded text-xs ${colors[req.estado]}`}>
            {req.estado}
          </span>
        )
      },
    },
    {
      key: 'actions',
      label: 'Acciones',
      render: (req: Request) => (
        <div className="flex gap-2">
          {isAdmin && req.estado === 'PENDIENTE' && (
            <>
              <Button
                variant="success"
                className="text-xs px-2 py-1"
                onClick={(e) => {
                  e.stopPropagation()
                  setActionModal({ request: req, action: 'approve' })
                }}
              >
                Aprobar
              </Button>
              <Button
                variant="danger"
                className="text-xs px-2 py-1"
                onClick={(e) => {
                  e.stopPropagation()
                  setActionModal({ request: req, action: 'reject' })
                }}
              >
                Rechazar
              </Button>
            </>
          )}
          {!isAdmin && req.estado === 'PENDIENTE' && (
            <Button
              variant="secondary"
              className="text-xs px-2 py-1"
              onClick={(e) => {
                e.stopPropagation()
                handleCancel(req.id)
              }}
            >
              Cancelar
            </Button>
          )}
        </div>
      ),
    },
  ]

  if (loading) return <Loading />

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold text-gray-800">Solicitudes</h1>
        <Link to="/requests/new">
          <Button>Nueva Solicitud</Button>
        </Link>
      </div>

      <div className="bg-white rounded-lg shadow">
        <Table data={requests} columns={columns} />
        <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
      </div>

      {actionModal && (
        <Modal
          isOpen={true}
          onClose={() => setActionModal(null)}
          title={actionModal.action === 'approve' ? 'Aprobar Solicitud' : 'Rechazar Solicitud'}
        >
          <p className="mb-4">
            ¿Está seguro de {actionModal.action === 'approve' ? 'aprobar' : 'rechazar'} la
            solicitud de {actionModal.request.nombreEmpleado}?
          </p>
          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Comentarios</label>
            <textarea
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
              rows={3}
              value={comentarios}
              onChange={(e) => setComentarios(e.target.value)}
            />
          </div>
          <div className="flex justify-end gap-4">
            <Button variant="secondary" onClick={() => setActionModal(null)}>
              Cancelar
            </Button>
            <Button variant={actionModal.action === 'approve' ? 'success' : 'danger'} onClick={handleAction}>
              {actionLoading ? 'Procesando...' : actionModal.action === 'approve' ? 'Aprobar' : 'Rechazar'}
            </Button>
          </div>
        </Modal>
      )}
    </div>
  )
}

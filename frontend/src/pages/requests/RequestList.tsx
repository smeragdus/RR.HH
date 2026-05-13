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

  const statusColors: Record<RequestStatus, { bg: string; text: string; dot: string }> = {
    PENDIENTE: { bg: 'bg-amber-100', text: 'text-amber-700', dot: 'bg-amber-500' },
    APROBADA: { bg: 'bg-emerald-100', text: 'text-emerald-700', dot: 'bg-emerald-500' },
    RECHAZADA: { bg: 'bg-red-100', text: 'text-red-700', dot: 'bg-red-500' },
    CANCELADA: { bg: 'bg-gray-100', text: 'text-gray-600', dot: 'bg-gray-400' },
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
      render: (req: Request) => (
        <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-xs font-medium ${statusColors[req.estado].bg} ${statusColors[req.estado].text}`}>
          <span className={`w-1.5 h-1.5 rounded-full ${statusColors[req.estado].dot}`} />
          {req.estado}
        </span>
      ),
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
                className="text-xs px-3 py-1.5"
                onClick={(e) => {
                  e.stopPropagation()
                  setActionModal({ request: req, action: 'approve' })
                }}
              >
                Aprobar
              </Button>
              <Button
                variant="danger"
                className="text-xs px-3 py-1.5"
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
              className="text-xs px-3 py-1.5"
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
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold font-display text-gray-900">Solicitudes</h1>
          <p className="text-sm text-gray-500 mt-1">Gestión de solicitudes y permisos</p>
        </div>
        <Link to="/requests/new">
          <Button>Nueva Solicitud</Button>
        </Link>
      </div>

      <div className="card">
        <Table data={requests} columns={columns} />
        <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
      </div>

      {actionModal && (
        <Modal
          isOpen={true}
          onClose={() => setActionModal(null)}
          title={actionModal.action === 'approve' ? 'Aprobar Solicitud' : 'Rechazar Solicitud'}
        >
          <p className="text-gray-600 mb-4">
            ¿Está seguro de {actionModal.action === 'approve' ? 'aprobar' : 'rechazar'} la solicitud de <span className="font-semibold">{actionModal.request.nombreEmpleado}</span>?
          </p>
          <div className="mb-4">
            <label className="label">Comentarios</label>
            <textarea
              className="input-field"
              rows={3}
              value={comentarios}
              onChange={(e) => setComentarios(e.target.value)}
              placeholder="Agregue un comentario (opcional)"
            />
          </div>
          <div className="flex justify-end gap-3">
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
import { useEffect, useState } from 'react'
import { contractApi } from '../../api/contractApi'
import { Contract, Page } from '../../types'
import Table from '../../components/common/Table'
import Pagination from '../../components/common/Pagination'
import Loading from '../../components/common/Loading'

export default function ContractList() {
  const [contracts, setContracts] = useState<Contract[]>([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    loadContracts()
  }, [page])

  const loadContracts = async () => {
    setLoading(true)
    try {
      const response = await contractApi.getAll(page, 20)
      if (response.success && response.data) {
        const data = response.data as Page<Contract>
        setContracts(data.content)
        setTotalPages(data.totalPages)
      }
    } catch (error) {
      console.error('Error loading contracts:', error)
    } finally {
      setLoading(false)
    }
  }

  const columns = [
    { key: 'numeroContrato', label: 'No. Contrato' },
    { key: 'nombreEmpleado', label: 'Empleado' },
    { key: 'tipo', label: 'Tipo' },
    { key: 'fechaInicio', label: 'Fecha Inicio' },
    { key: 'fechaFin', label: 'Fecha Fin' },
    { key: 'salario', label: 'Salario' },
    {
      key: 'activo',
      label: 'Estado',
      render: (c: Contract) => (
        <span
          className={`px-2 py-1 rounded text-xs font-medium ${
            c.activo ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'
          }`}
        >
          {c.activo ? 'Activo' : 'Inactivo'}
        </span>
      ),
    },
  ]

  if (loading) return <Loading />

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold font-display text-gray-900">Contratos</h1>
        <p className="text-sm text-gray-500 mt-1">Gestión de contratos laborales</p>
      </div>

      <div className="card">
        <Table data={contracts} columns={columns} />
        <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
      </div>
    </div>
  )
}
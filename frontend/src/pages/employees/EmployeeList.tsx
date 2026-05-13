import { useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { employeeApi } from '../../api/employeeApi'
import { Employee, Page } from '../../types'
import Table from '../../components/common/Table'
import Pagination from '../../components/common/Pagination'
import Button from '../../components/common/Button'
import Input from '../../components/common/Input'
import Loading from '../../components/common/Loading'

export default function EmployeeList() {
  const [employees, setEmployees] = useState<Employee[]>([])
  const [page, setPage] = useState(0)
  const [totalPages, setTotalPages] = useState(0)
  const [search, setSearch] = useState('')
  const [loading, setLoading] = useState(true)
  const navigate = useNavigate()

  useEffect(() => {
    loadEmployees()
  }, [page, search])

  const loadEmployees = async () => {
    setLoading(true)
    try {
      const response = await employeeApi.getAll(page, 20, search)
      if (response.success && response.data) {
        const data = response.data as Page<Employee>
        setEmployees(data.content)
        setTotalPages(data.totalPages)
      }
    } catch (error) {
      console.error('Error loading employees:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault()
    setPage(0)
    loadEmployees()
  }

  const handleEdit = (employee: Employee) => {
    navigate(`/employees/${employee.id}/edit`)
  }

  const columns = [
    { key: 'numeroEmpleado', label: 'No. Empleado' },
    { key: 'nombreCompleto', label: 'Nombre' },
    { key: 'departamento', label: 'Departamento' },
    { key: 'puesto', label: 'Puesto' },
    { key: 'emailPersonal', label: 'Email' },
    {
      key: 'activo',
      label: 'Estado',
      render: (emp: Employee) => (
        <span
          className={`px-2 py-1 rounded text-xs font-medium ${
            emp.activo ? 'bg-emerald-100 text-emerald-700' : 'bg-red-100 text-red-700'
          }`}
        >
          {emp.activo ? 'Activo' : 'Inactivo'}
        </span>
      ),
    },
    {
      key: 'actions',
      label: 'Acciones',
      render: (emp: Employee) => (
        <Button
          variant="secondary"
          onClick={(e) => {
            e.stopPropagation()
            handleEdit(emp)
          }}
          className="text-xs px-3 py-1"
        >
          Editar
        </Button>
      ),
    },
  ]

  if (loading) return <Loading />

  return (
    <div>
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold font-display text-gray-900">Empleados</h1>
          <p className="text-sm text-gray-500 mt-1">Gestiona la información de los empleados</p>
        </div>
        <Link to="/employees/new">
          <Button>Nuevo Empleado</Button>
        </Link>
      </div>

      <div className="card mb-6 p-4">
        <form onSubmit={handleSearch} className="flex gap-4">
          <Input
            placeholder="Buscar por nombre, número de empleado..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
            className="flex-1"
          />
          <Button type="submit">Buscar</Button>
        </form>
      </div>

      <div className="card">
        <Table data={employees} columns={columns} onRowClick={handleEdit} />
        <Pagination page={page} totalPages={totalPages} onPageChange={setPage} />
      </div>
    </div>
  )
}
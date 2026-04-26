import { useState, useEffect } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { employeeApi } from '../../api/employeeApi'
import { Employee } from '../../types'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Button from '../../components/common/Button'
import Loading from '../../components/common/Loading'

export default function EmployeeForm() {
  const { id } = useParams()
  const navigate = useNavigate()
  const isEditing = !!id

  const [loading, setLoading] = useState(isEditing)
  const [saving, setSaving] = useState(false)
  const [form, setForm] = useState({
    nombres: '',
    apellidoPaterno: '',
    apellidoMaterno: '',
    fechaNacimiento: '',
    curp: '',
    rfc: '',
    nss: '',
    genero: '',
    estadoCivil: '',
    direccion: '',
    telefono: '',
    emailPersonal: '',
    departamento: '',
    puesto: '',
    tipoContrato: '',
    salario: '',
    fechaIngreso: '',
  })

  useEffect(() => {
    if (isEditing && id) {
      loadEmployee(parseInt(id))
    }
  }, [id])

  const loadEmployee = async (employeeId: number) => {
    try {
      const response = await employeeApi.getById(employeeId)
      if (!response.success || !response.data) return
      const emp: Employee = response.data
      setForm({
        nombres: emp.nombres || '',
        apellidoPaterno: emp.apellidoPaterno || '',
        apellidoMaterno: emp.apellidoMaterno || '',
        fechaNacimiento: emp.fechaNacimiento || '',
        curp: emp.curp || '',
        rfc: emp.rfc || '',
        nss: emp.nss || '',
        genero: emp.genero || '',
        estadoCivil: emp.estadoCivil || '',
        direccion: emp.direccion || '',
        telefono: emp.telefono || '',
        emailPersonal: emp.emailPersonal || '',
        departamento: emp.departamento || '',
        puesto: emp.puesto || '',
        tipoContrato: emp.tipoContrato || '',
        salario: emp.salario?.toString() || '',
        fechaIngreso: emp.fechaIngreso || '',
      })
    } catch (error) {
      console.error('Error loading employee:', error)
    } finally {
      setLoading(false)
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    setSaving(true)
    try {
      const data = {
        ...form,
        salario: form.salario ? parseFloat(form.salario) : undefined,
      }
      if (isEditing && id) {
        await employeeApi.update(parseInt(id), data)
      } else {
        await employeeApi.create(data)
      }
      navigate('/employees')
    } catch (error) {
      console.error('Error saving employee:', error)
    } finally {
      setSaving(false)
    }
  }

  if (loading) return <Loading />

  return (
    <div className="max-w-2xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">
        {isEditing ? 'Editar Empleado' : 'Nuevo Empleado'}
      </h1>

      <div className="bg-white rounded-lg shadow p-6">
        <form onSubmit={handleSubmit}>
          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Nombres"
              name="nombres"
              value={form.nombres}
              onChange={handleChange}
              required
            />
            <Input
              label="Apellido Paterno"
              name="apellidoPaterno"
              value={form.apellidoPaterno}
              onChange={handleChange}
              required
            />
            <Input
              label="Apellido Materno"
              name="apellidoMaterno"
              value={form.apellidoMaterno}
              onChange={handleChange}
            />
            <Input
              label="Fecha de Nacimiento"
              name="fechaNacimiento"
              type="date"
              value={form.fechaNacimiento}
              onChange={handleChange}
            />
            <Input
              label="CURP"
              name="curp"
              value={form.curp}
              onChange={handleChange}
              maxLength={18}
            />
            <Input
              label="RFC"
              name="rfc"
              value={form.rfc}
              onChange={handleChange}
              maxLength={13}
            />
            <Input
              label="NSS"
              name="nss"
              value={form.nss}
              onChange={handleChange}
              maxLength={15}
            />
            <Select
              label="Género"
              name="genero"
              value={form.genero}
              onChange={handleChange}
              options={[
                { value: 'M', label: 'Masculino' },
                { value: 'F', label: 'Femenino' },
              ]}
            />
            <Select
              label="Estado Civil"
              name="estadoCivil"
              value={form.estadoCivil}
              onChange={handleChange}
              options={[
                { value: 'SOLTERO', label: 'Soltero' },
                { value: 'CASADO', label: 'Casado' },
                { value: 'DIVORCIADO', label: 'Divorciado' },
                { value: 'VIUDO', label: 'Viudo' },
              ]}
            />
            <Input
              label="Teléfono"
              name="telefono"
              value={form.telefono}
              onChange={handleChange}
            />
            <Input
              label="Email Personal"
              name="emailPersonal"
              type="email"
              value={form.emailPersonal}
              onChange={handleChange}
            />
            <Input
              label="Dirección"
              name="direccion"
              value={form.direccion}
              onChange={handleChange}
            />
            <Input
              label="Departamento"
              name="departamento"
              value={form.departamento}
              onChange={handleChange}
            />
            <Input
              label="Puesto"
              name="puesto"
              value={form.puesto}
              onChange={handleChange}
            />
            <Select
              label="Tipo de Contrato"
              name="tipoContrato"
              value={form.tipoContrato}
              onChange={handleChange}
              options={[
                { value: 'TEMPORAL', label: 'Temporal' },
                { value: 'INDETERMINADO', label: 'Indeterminado' },
                { value: 'POR_HORA', label: 'Por Hora' },
              ]}
            />
            <Input
              label="Salario"
              name="salario"
              type="number"
              step="0.01"
              value={form.salario}
              onChange={handleChange}
            />
            <Input
              label="Fecha de Ingreso"
              name="fechaIngreso"
              type="date"
              value={form.fechaIngreso}
              onChange={handleChange}
            />
          </div>

          <div className="flex justify-end gap-4 mt-6">
            <Button type="button" variant="secondary" onClick={() => navigate('/employees')}>
              Cancelar
            </Button>
            <Button type="submit" disabled={saving}>
              {saving ? 'Guardando...' : 'Guardar'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  )
}

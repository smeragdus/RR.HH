import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { requestApi } from '../../api/requestApi'
import { RequestType } from '../../types'
import Input from '../../components/common/Input'
import Select from '../../components/common/Select'
import Button from '../../components/common/Button'

export default function RequestForm() {
  const navigate = useNavigate()
  const [saving, setSaving] = useState(false)
  const [form, setForm] = useState({
    tipo: '' as RequestType | '',
    fechaInicio: '',
    fechaFin: '',
    motivo: '',
  })

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    setForm({ ...form, [e.target.name]: e.target.value })
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!form.tipo || !form.fechaInicio || !form.fechaFin) {
      alert('Por favor complete todos los campos requeridos')
      return
    }
    setSaving(true)
    try {
      await requestApi.create({
        tipo: form.tipo as RequestType,
        fechaInicio: form.fechaInicio,
        fechaFin: form.fechaFin,
        motivo: form.motivo,
      })
      navigate('/requests')
    } catch (error) {
      console.error('Error creating request:', error)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="max-w-xl mx-auto">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Nueva Solicitud</h1>

      <div className="bg-white rounded-lg shadow p-6">
        <form onSubmit={handleSubmit}>
          <Select
            label="Tipo de Solicitud"
            name="tipo"
            value={form.tipo}
            onChange={handleChange}
            options={[
              { value: 'VACACIONES', label: 'Vacaciones' },
              { value: 'ENFERMEDAD', label: 'Enfermedad' },
              { value: 'MATRIMONIO', label: 'Matrimonio' },
              { value: 'PATERNIDAD', label: 'Paternidad' },
              { value: 'DEFUNCION', label: 'Defunción' },
              { value: 'TRASLADO', label: 'Traslado' },
              { value: 'PERMISO', label: 'Permiso' },
            ]}
          />

          <div className="grid grid-cols-2 gap-4">
            <Input
              label="Fecha de Inicio"
              name="fechaInicio"
              type="date"
              value={form.fechaInicio}
              onChange={handleChange}
              required
            />
            <Input
              label="Fecha de Fin"
              name="fechaFin"
              type="date"
              value={form.fechaFin}
              onChange={handleChange}
              required
            />
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium text-gray-700 mb-1">Motivo</label>
            <textarea
              name="motivo"
              rows={4}
              className="w-full px-3 py-2 border border-gray-300 rounded-md"
              value={form.motivo}
              onChange={handleChange}
            />
          </div>

          <div className="flex justify-end gap-4">
            <Button type="button" variant="secondary" onClick={() => navigate('/requests')}>
              Cancelar
            </Button>
            <Button type="submit" disabled={saving}>
              {saving ? 'Enviando...' : 'Enviar Solicitud'}
            </Button>
          </div>
        </form>
      </div>
    </div>
  )
}

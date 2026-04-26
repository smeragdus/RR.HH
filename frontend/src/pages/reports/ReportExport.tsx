import { useState } from 'react'
import { useAuth } from '../../context/AuthContext'
import Button from '../../components/common/Button'
import Input from '../../components/common/Input'

export default function ReportExport() {
  const { user } = useAuth()
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  const [exporting, setExporting] = useState(false)

  const handleExportExcel = async (type: 'attendance' | 'employees') => {
    setExporting(true)
    try {
      let url = `/api/reports/export/excel/${type}`
      if (type === 'attendance' && startDate && endDate) {
        url += `?startDate=${startDate}&endDate=${endDate}`
      }

      const token = localStorage.getItem('token')
      const response = await fetch(url, {
        headers: { Authorization: `Bearer ${token}` },
      })

      const blob = await response.blob()
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = `reporte_${type}_${new Date().toISOString().split('T')[0]}.xlsx`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    } catch (error) {
      console.error('Error exporting:', error)
    } finally {
      setExporting(false)
    }
  }

  const handleExportPdf = async (type: 'attendance' | 'employees') => {
    setExporting(true)
    try {
      let url = `/api/reports/export/pdf/${type}`
      if (type === 'attendance' && startDate && endDate) {
        url += `?startDate=${startDate}&endDate=${endDate}`
      }

      const token = localStorage.getItem('token')
      const response = await fetch(url, {
        headers: { Authorization: `Bearer ${token}` },
      })

      const blob = await response.blob()
      const downloadUrl = window.URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = downloadUrl
      link.download = `reporte_${type}_${new Date().toISOString().split('T')[0]}.pdf`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
    } catch (error) {
      console.error('Error exporting:', error)
    } finally {
      setExporting(false)
    }
  }

  if (user?.rol !== 'ADMIN' && user?.rol !== 'RRHH') {
    return (
      <div className="bg-white rounded-lg shadow p-6">
        <p className="text-gray-600">No tiene permisos para acceder a esta sección.</p>
      </div>
    )
  }

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-800 mb-6">Reportes y Exportación</h1>

      <div className="bg-white rounded-lg shadow p-6 mb-6">
        <h2 className="text-lg font-semibold mb-4">Reporte de Asistencia</h2>
        <div className="grid grid-cols-2 gap-4 mb-4">
          <Input
            label="Fecha Inicio"
            type="date"
            value={startDate}
            onChange={(e) => setStartDate(e.target.value)}
          />
          <Input
            label="Fecha Fin"
            type="date"
            value={endDate}
            onChange={(e) => setEndDate(e.target.value)}
          />
        </div>
        <div className="flex gap-4">
          <Button onClick={() => handleExportExcel('attendance')} disabled={exporting}>
            Exportar Excel
          </Button>
          <Button variant="secondary" onClick={() => handleExportPdf('attendance')} disabled={exporting}>
            Exportar PDF
          </Button>
        </div>
      </div>

      <div className="bg-white rounded-lg shadow p-6">
        <h2 className="text-lg font-semibold mb-4">Reporte de Empleados</h2>
        <div className="flex gap-4">
          <Button onClick={() => handleExportExcel('employees')} disabled={exporting}>
            Exportar Excel
          </Button>
          <Button variant="secondary" onClick={() => handleExportPdf('employees')} disabled={exporting}>
            Exportar PDF
          </Button>
        </div>
      </div>
    </div>
  )
}

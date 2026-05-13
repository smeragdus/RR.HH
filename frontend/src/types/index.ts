export type Role = 'ADMIN' | 'RRHH' | 'JEFE' | 'EMPLEADO'

export interface User {
  id: number
  username: string
  email: string
  rol: Role
  activo?: boolean
  empleadoId?: number
}

export interface JwtResponse {
  token: string
  type: string
  id: number
  username: string
  email: string
  rol: Role
  empleadoId?: number
  nombreCompleto?: string
}

export interface Employee {
  id: number
  numeroEmpleado: string
  nombres: string
  apellidoPaterno: string
  apellidoMaterno?: string
  nombreCompleto: string
  fechaNacimiento?: string
  genero?: string
  direccion?: string
  telefono?: string
  emailPersonal?: string
  fotoUrl?: string
  departamento?: string
  puesto?: string
  tipoContrato?: string
  salario?: number
  fechaIngreso?: string
  usuarioId?: number
  supervisorId?: number
  supervisorNombre?: string
  activo: boolean
}

export interface Attendance {
  id: number
  empleadoId: number
  numeroEmpleado: string
  nombreEmpleado: string
  fecha: string
  horaEntrada?: string
  horaSalida?: string
  horasTrabajadas?: number
  tipoJornada: string
  observaciones?: string
}

export type RequestType = 'VACACIONES' | 'ENFERMEDAD' | 'MATRIMONIO' | 'PATERNIDAD' | 'DEFUNCION' | 'TRASLADO' | 'PERMISO'
export type RequestStatus = 'PENDIENTE' | 'APROBADA' | 'RECHAZADA' | 'CANCELADA'

export interface Request {
  id: number
  empleadoId: number
  numeroEmpleado: string
  nombreEmpleado: string
  tipo: RequestType
  fechaInicio: string
  fechaFin: string
  diasSolicitados: number
  motivo?: string
  estado: RequestStatus
  aprobadoPorId?: number
  aprobadoPorNombre?: string
  fechaAprobacion?: string
  comentarios?: string
}

export interface Contract {
  id: number
  empleadoId: number
  numeroEmpleado: string
  nombreEmpleado: string
  numeroContrato: string
  tipo: string
  fechaInicio: string
  fechaFin?: string
  salario: number
  puesto?: string
  departamento?: string
  jornada?: string
  salarioDiario?: number
  sbcImss?: number
  prestaciones?: string
  observaciones?: string
  activo: boolean
}

export interface ApiResponse<T> {
  success: boolean
  message?: string
  data: T
  timestamp: string
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
}

export interface ChangePasswordRequest {
  currentPassword: string
  newPassword: string
}

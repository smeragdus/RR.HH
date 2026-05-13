-- =============================================
-- Sistema de Gestión de Recursos Humanos
-- Script de inicialización de base de datos
-- =============================================

-- USUARIO (User accounts)
CREATE TABLE IF NOT EXISTS usuario (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'RRHH', 'JEFE', 'EMPLEADO')),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultimo_acceso TIMESTAMP
);

-- EMPLEADO (Employee profiles)
CREATE TABLE IF NOT EXISTS empleado (
    id BIGSERIAL PRIMARY KEY,
    numero_empleado VARCHAR(20) UNIQUE NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(50) NOT NULL,
    apellido_materno VARCHAR(50),
    fecha_nacimiento DATE,
    genero VARCHAR(10),
    direccion TEXT,
    telefono VARCHAR(20),
    email_personal VARCHAR(100),
    foto_url VARCHAR(500),
    departamento VARCHAR(100),
    puesto VARCHAR(100),
    tipo_contrato VARCHAR(50),
    salario DECIMAL(12,2),
    fecha_ingreso DATE,
    usuario_id BIGINT REFERENCES usuario(id),
    supervisor_id BIGINT REFERENCES empleado(id),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ASISTENCIA (Attendance records)
CREATE TABLE IF NOT EXISTS asistencia (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL REFERENCES empleado(id),
    fecha DATE NOT NULL,
    hora_entrada TIME,
    hora_salida TIME,
    horas_trabajadas DECIMAL(4,2),
    tipo_jornada VARCHAR(20) DEFAULT 'ORDINARIA',
    observaciones TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(empleado_id, fecha)
);

-- SOLICITUD (Requests/Permits)
CREATE TABLE IF NOT EXISTS solicitud (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL REFERENCES empleado(id),
    tipo VARCHAR(30) NOT NULL CHECK (tipo IN ('VACACIONES', 'ENFERMEDAD', 'MATRIMONIO', 'PATERNIDAD', 'DEFUNCION', 'TRASLADO', 'PERMISO')),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    dias_solicitados INTEGER NOT NULL,
    motivo TEXT,
    estado VARCHAR(20) DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'APROBADA', 'RECHAZADA', 'CANCELADA')),
    aprobado_por BIGINT REFERENCES empleado(id),
    fecha_aprobacion TIMESTAMP,
    comentarios TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- CONTRATO (Labor contracts)
CREATE TABLE IF NOT EXISTS contrato (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL REFERENCES empleado(id),
    numero_contrato VARCHAR(30) UNIQUE NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE,
    salario DECIMAL(12,2) NOT NULL,
    puesto VARCHAR(100),
    departamento VARCHAR(100),
    jornada VARCHAR(30),
    salario_diario DECIMAL(12,2),
    sbc_imss DECIMAL(12,2),
    prestaciones TEXT,
    observaciones TEXT,
    documento_url VARCHAR(500),
    activo BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- AUDITORIA (Audit log)
CREATE TABLE IF NOT EXISTS auditoria (
    id BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL REFERENCES usuario(id),
    accion VARCHAR(50) NOT NULL,
    entidad VARCHAR(50) NOT NULL,
    entidad_id BIGINT,
    datos_anteriores JSONB,
    datos_nuevos JSONB,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- =============================================
-- ÍNDICES PARA RENDIMIENTO
-- =============================================

CREATE INDEX IF NOT EXISTS idx_empleado_supervisor ON empleado(supervisor_id);
CREATE INDEX IF NOT EXISTS idx_empleado_usuario ON empleado(usuario_id);
CREATE INDEX IF NOT EXISTS idx_empleado_departamento ON empleado(departamento);
CREATE INDEX IF NOT EXISTS idx_asistencia_fecha ON asistencia(fecha);
CREATE INDEX IF NOT EXISTS idx_asistencia_empleado_fecha ON asistencia(empleado_id, fecha);
CREATE INDEX IF NOT EXISTS idx_solicitud_empleado ON solicitud(empleado_id);
CREATE INDEX IF NOT EXISTS idx_solicitud_estado ON solicitud(estado);
CREATE INDEX IF NOT EXISTS idx_contrato_empleado ON contrato(empleado_id);
CREATE INDEX IF NOT EXISTS idx_contrato_activo ON contrato(activo);
CREATE INDEX IF NOT EXISTS idx_auditoria_usuario ON auditoria(usuario_id);
CREATE INDEX IF NOT EXISTS idx_auditoria_fecha ON auditoria(created_at);
CREATE INDEX IF NOT EXISTS idx_auditoria_entidad ON auditoria(entidad, entidad_id);

-- =============================================
-- DATOS INICIALES (Seed Data)
-- =============================================

-- Usuario Admin inicial (password: admin123 - BCrypt)
INSERT INTO usuario (username, password, email, rol, activo)
VALUES (
    'admin',
    '$2b$10$6u.PouheUcDh.ZpbMmBMuu0ab92rnVfDtn0UxNdHcIiLBJBPmO6K2',
    'admin@rrhh.com',
    'ADMIN',
    true
) ON CONFLICT (username) DO NOTHING;

-- Usuario RRHH inicial (password: rrhh123)
INSERT INTO usuario (username, password, email, rol, activo)
VALUES (
    'rrhh',
    '$2b$10$6u.PouheUcDh.ZpbMmBMuu0ab92rnVfDtn0UxNdHcIiLBJBPmO6K2',
    'rrhh@rrhh.com',
    'RRHH',
    true
) ON CONFLICT (username) DO NOTHING;

-- =============================================
-- SECUENCIAS PARA NÚMEROS DE EMPLEADO/CONTRATO
-- =============================================

CREATE SEQUENCE IF NOT EXISTS seq_numero_empleado START 1000;
CREATE SEQUENCE IF NOT EXISTS seq_numero_contrato START 1;

COMMENT ON TABLE usuario IS 'Cuentas de usuario del sistema';
COMMENT ON TABLE empleado IS 'Perfiles de empleados';
COMMENT ON TABLE asistencia IS 'Registros de asistencia diaria';
COMMENT ON TABLE solicitud IS 'Solicitudes y permisos';
COMMENT ON TABLE contrato IS 'Contratos laborales';
COMMENT ON TABLE auditoria IS 'Log de auditoría del sistema';

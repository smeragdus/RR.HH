# Sistema de Gestión de Recursos Humanos

Sistema web para la gestión integral de recursos humanos con autenticación JWT.

## Tech Stack

- **Backend**: Spring Boot 3.2.4, Java 17, Spring Security, JPA
- **Frontend**: React 18, TypeScript, Vite, TailwindCSS, React Router
- **Base de datos**: PostgreSQL 16
- **Contenedores**: Docker Compose

## Estructura del Proyecto

```
RR.HH/
├── backend/          # API Spring Boot
├── frontend/        # Aplicación React
├── docker-compose.yml
└── init.sql         # Esquema de base de datos
```

## Setup

1. **Clonar y configurar variables de entorno**
   ```bash
   cp .env.example .env  # Configurar JWT_SECRET
   ```

2. **Iniciar con Docker Compose**
   ```bash
   docker-compose up -d
   ```

3. **Acceso**
   - Frontend: http://localhost
   - API: http://localhost:8080

## Desarrollo Local

### Backend
```bash
cd backend
./mvnw spring-boot:run
```

### Frontend
```bash
cd frontend
npm install
npm run dev
```

## Funcionalidades

- Autenticación con JWT
- Gestión de empleados
- Control de asistencia
- Gestión de solicitudes
- Gestión de contratos
- Exportación de reportes en Excel

## API Endpoints

| Método | Ruta | Descripción |
|--------|------|-------------|
| POST | /api/auth/login | Iniciar sesión |
| GET/POST | /api/employees | Listar/Crear empleados |
| GET/PUT/DELETE | /api/employees/{id} | Gestionar empleado |
| GET/POST | /api/requests | Listar/Crear solicitudes |
| GET/PUT | /api/requests/{id} | Actualizar solicitud |
| GET/POST | /api/contracts | Listar/Crear contratos |
| GET | /api/attendance | Registro de asistencia |
| GET | /api/reports/export | Exportar Excel |
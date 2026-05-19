# SistemaHR

Sistema web MVP para gestion de recursos humanos, asistencia, solicitudes, contratos, reportes y auditoria.

## Requisitos

- Java 21+
- Maven 3.9+
- Node.js 20+
- Docker Desktop para PostgreSQL local

## Arranque local

1. Iniciar PostgreSQL:

```powershell
docker compose up -d
```

2. Iniciar backend:

```powershell
cd backend
mvn spring-boot:run
```

3. Iniciar frontend:

```powershell
cd frontend
npm install
npm run dev
```

Frontend: `http://localhost:5173`
Backend: `http://localhost:8080`

## Usuarios semilla

Todos usan la clave `Password123!`.

- `admin@sistemahr.local` / `ADMIN`
- `rrhh@sistemahr.local` / `RRHH`
- `jefe@sistemahr.local` / `JEFE`
- `empleado@sistemahr.local` / `EMPLEADO`

## Variables utiles

- `DB_URL`, recomendado para Docker local `jdbc:postgresql://127.0.0.1:55432/sistemahr`
- `DB_USERNAME`, por defecto `postgres`
- `DB_PASSWORD`, por defecto `postgres`
- `JWT_SECRET`
- `UPLOAD_DIR`, por defecto `uploads`
- `ATTENDANCE_START_TIME`, por defecto `09:00`
- `ATTENDANCE_LATE_TOLERANCE_MINUTES`, por defecto `10`

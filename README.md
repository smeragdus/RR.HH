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
- `CORS_ALLOWED_ORIGINS`, por defecto `http://localhost:5173,http://127.0.0.1:5173`
- `ATTENDANCE_START_TIME`, por defecto `09:00`
- `ATTENDANCE_LATE_TOLERANCE_MINUTES`, por defecto `10`

## Despliegue en Railway

Railway es una buena opcion para este proyecto porque permite desplegar el backend Spring Boot y una base PostgreSQL en el mismo proyecto. El `Dockerfile` de la raiz compila el frontend Vite con `VITE_API_URL=/api`, copia el `dist` dentro del jar de Spring Boot y ejecuta todo como un solo servicio.

1. Sube el repositorio a GitHub.
2. En Railway, crea un proyecto nuevo desde ese repositorio.
3. Agrega una base de datos PostgreSQL al proyecto.
4. En el servicio de la app, configura estas variables:

```text
PGHOST=${{Postgres.PGHOST}}
PGPORT=${{Postgres.PGPORT}}
PGDATABASE=${{Postgres.PGDATABASE}}
PGUSER=${{Postgres.PGUSER}}
PGPASSWORD=${{Postgres.PGPASSWORD}}
JWT_SECRET=pon-aqui-un-secreto-largo-y-aleatorio-de-al-menos-32-caracteres
```

Railway define `PORT` automaticamente y la aplicacion lo respeta. Si prefieres usar una sola variable para la base de datos, tambien puedes configurar `DB_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}`, `DB_USERNAME=${{Postgres.PGUSER}}` y `DB_PASSWORD=${{Postgres.PGPASSWORD}}`.

Si despliegas el frontend separado en Vercel, Netlify o Render Static, configura en ese frontend `VITE_API_URL=https://tu-backend.railway.app/api` y en el backend `CORS_ALLOWED_ORIGINS=https://tu-frontend.vercel.app`.

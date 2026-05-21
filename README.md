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
4. En el servicio de la app, no en el servicio de Postgres, configura estas variables:

```text
DB_URL=jdbc:postgresql://${{Postgres.PGHOST}}:${{Postgres.PGPORT}}/${{Postgres.PGDATABASE}}
DB_USERNAME=${{Postgres.PGUSER}}
DB_PASSWORD=${{Postgres.PGPASSWORD}}
JWT_SECRET=pon-aqui-un-secreto-largo-y-aleatorio-de-al-menos-32-caracteres
```

Si tu servicio de base de datos no se llama `Postgres`, cambia ese nombre en las referencias. Por ejemplo, si Railway lo llama `PostgreSQL`, usa `${{PostgreSQL.PGHOST}}`, `${{PostgreSQL.PGPORT}}`, etc.

Si el log muestra `jdbc:postgresql://:/`, Railway no resolvio esas referencias. En ese caso, abre el servicio PostgreSQL, copia los valores reales de `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER` y `PGPASSWORD`, y en el servicio de la app escribe valores directos:

```text
DB_URL=jdbc:postgresql://valor-real-de-PGHOST:valor-real-de-PGPORT/valor-real-de-PGDATABASE
DB_USERNAME=valor-real-de-PGUSER
DB_PASSWORD=valor-real-de-PGPASSWORD
JWT_SECRET=pon-aqui-un-secreto-largo-y-aleatorio-de-al-menos-32-caracteres
```

No uses `DB_URL=jdbc:postgresql://${PGHOST}:${PGPORT}/${PGDATABASE}` en Railway; esa sintaxis queda vacia si esas variables no existen en el servicio de la app.

Railway define `PORT` automaticamente y la aplicacion lo respeta. Tambien puedes usar directamente las variables `PGHOST`, `PGPORT`, `PGDATABASE`, `PGUSER` y `PGPASSWORD` si las referencian hacia el servicio PostgreSQL, pero `DB_URL`, `DB_USERNAME` y `DB_PASSWORD` dejan mas claro que son variables de la aplicacion.

Si despliegas el frontend separado en Vercel, Netlify o Render Static, configura en ese frontend `VITE_API_URL=https://tu-backend.railway.app/api` y en el backend `CORS_ALLOWED_ORIGINS=https://tu-frontend.vercel.app`.

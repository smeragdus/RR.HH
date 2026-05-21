import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.jsx'

try {
  createRoot(document.getElementById('root')).render(
    <StrictMode>
      <App />
    </StrictMode>,
  )
} catch (error) {
  document.getElementById('root').innerHTML = `
    <main class="startup-error">
      <h1>No se pudo cargar SistemaHR</h1>
      <p>${error?.message || 'Error inesperado al iniciar la interfaz.'}</p>
    </main>
  `
}

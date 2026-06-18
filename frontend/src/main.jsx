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
  console.error('No se pudo iniciar SistemaHR', error)
  const root = document.getElementById('root')
  const main = document.createElement('main')
  const title = document.createElement('h1')
  const message = document.createElement('p')
  main.className = 'startup-error'
  title.textContent = 'No se pudo cargar SistemaHR'
  message.textContent = 'Error inesperado al iniciar la interfaz.'
  main.append(title, message)
  root.replaceChildren(main)
}

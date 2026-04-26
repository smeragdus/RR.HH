import { useToast } from '../../hooks/useToast'

export default function Toast() {
  const { toasts, removeToast } = useToast()

  const typeClasses = {
    success: 'bg-green-500',
    error: 'bg-red-500',
    info: 'bg-blue-500',
  }

  return (
    <div className="fixed bottom-4 right-4 space-y-2 z-50">
      {toasts.map((toast) => (
        <div
          key={toast.id}
          className={`${typeClasses[toast.type]} text-white px-6 py-3 rounded shadow-lg flex items-center justify-between min-w-[300px]`}
        >
          <span>{toast.message}</span>
          <button onClick={() => removeToast(toast.id)} className="ml-4 hover:opacity-75">
            ✕
          </button>
        </div>
      ))}
    </div>
  )
}

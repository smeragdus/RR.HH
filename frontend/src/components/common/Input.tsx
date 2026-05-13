import { InputHTMLAttributes, forwardRef } from 'react'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string
  error?: string
}

const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, className = '', ...props }, ref) => {
    return (
      <div className="mb-4">
        {label && (
          <label className="label">
            {label}
          </label>
        )}
        <input
          ref={ref}
          className={`input-field ${error ? 'border-red-500 focus:ring-red-400/50 focus:border-red-500' : ''} ${className}`}
          {...props}
        />
        {error && <p className="mt-1.5 text-sm text-red-500 flex items-center gap-1">{error}</p>}
      </div>
    )
  }
)

Input.displayName = 'Input'

export default Input
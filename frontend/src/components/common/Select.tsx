import { SelectHTMLAttributes, forwardRef } from 'react'

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string
  error?: string
  options: { value: string; label: string }[]
}

const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, error, options, className = '', ...props }, ref) => {
    return (
      <div className="mb-4">
        {label && (
          <label className="label">{label}</label>
        )}
        <select
          ref={ref}
          className={`input-field ${error ? 'border-red-500 focus:ring-red-400/50 focus:border-red-500' : ''} ${className}`}
          {...props}
        >
          <option value="">Seleccionar...</option>
          {options.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
        {error && <p className="mt-1.5 text-sm text-red-500 flex items-center gap-1">{error}</p>}
      </div>
    )
  }
)

Select.displayName = 'Select'

export default Select
import { createContext, useContext, useState, useCallback } from 'react';

const ToastContext = createContext(null);

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) throw new Error("useToast must be used within a ToastProvider");
  return context;
};

export const ToastProvider = ({ children }) => {
  const [toasts, setToasts] = useState([]);

  const removeToast = useCallback((id) => {
    setToasts((prev) => prev.filter((toast) => toast.id !== id));
  }, []);

  const addToast = useCallback((message, type = 'info') => {
    const id = Date.now();
    setToasts((prev) => [...prev, { id, message, type }]);
    setTimeout(() => {
      removeToast(id);
    }, 5000);
  }, [removeToast]);

  const success = (message) => addToast(message, 'success');
  const error = (message) => addToast(message, 'error');
  const warning = (message) => addToast(message, 'warning');
  const info = (message) => addToast(message, 'info');

  const getToastIcon = (type) => {
    switch (type) {
      case 'success': return <span className="material-symbols-outlined text-emerald-600">check_circle</span>;
      case 'error': return <span className="material-symbols-outlined text-error">error</span>;
      case 'warning': return <span className="material-symbols-outlined text-amber-600">warning</span>;
      default: return <span className="material-symbols-outlined text-primary">info</span>;
    }
  };

  const getToastStyle = (type) => {
    switch (type) {
      case 'success': return 'bg-emerald-50 border-emerald-200 text-emerald-900';
      case 'error': return 'bg-error-container border-error/20 text-on-error-container';
      case 'warning': return 'bg-amber-50 border-amber-200 text-amber-900';
      default: return 'bg-primary-container/20 border-primary/20 text-on-surface';
    }
  };

  return (
    <ToastContext.Provider value={{ success, error, warning, info }}>
      {children}
      <div className="fixed bottom-lg right-lg z-50 flex flex-col gap-sm pointer-events-none">
        {toasts.map((toast) => (
          <div 
            key={toast.id} 
            className={`pointer-events-auto flex items-start gap-sm p-md rounded-xl border shadow-lg min-w-[300px] max-w-md transition-all animate-in slide-in-from-bottom-5 fade-in duration-300 ${getToastStyle(toast.type)}`}
          >
            {getToastIcon(toast.type)}
            <div className="flex-1 font-body-sm text-body-sm pt-0.5">{toast.message}</div>
            <button 
              onClick={() => removeToast(toast.id)} 
              className="text-on-surface-variant hover:text-on-surface transition-colors"
            >
              <span className="material-symbols-outlined text-[18px]">close</span>
            </button>
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
};

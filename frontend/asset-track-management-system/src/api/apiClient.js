const API_BASE = '/api/v1';

export const apiClient = {
  get: async (url, options = {}) => request(url, { ...options, method: 'GET' }),
  post: async (url, body, options = {}) => request(url, { ...options, method: 'POST', body: JSON.stringify(body) }),
  put: async (url, body, options = {}) => request(url, { ...options, method: 'PUT', body: JSON.stringify(body) }),
  delete: async (url, options = {}) => request(url, { ...options, method: 'DELETE' }),
};

async function request(url, options) {
  const token = localStorage.getItem('token');
  const headers = {
    'Content-Type': 'application/json',
    ...options.headers,
  };

  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }

  const response = await fetch(`${API_BASE}${url}`, {
    ...options,
    headers,
  });

  if (!response.ok) {
    let errorMsg = 'An error occurred';
    try {
      const errorData = await response.json();
      errorMsg = errorData.error || errorData.message || errorMsg;
    } catch (e) {
      // Ignored
    }
    
    // Auto logout on 401 (Unauthorized) only. 403 (Forbidden) means they lack permissions, not that their token is invalid.
    if (response.status === 401 && window.location.pathname !== '/login') {
       localStorage.removeItem('token');
       localStorage.removeItem('user');
       window.location.href = '/login';
    }
    
    throw new Error(errorMsg);
  }

  // Handle empty responses (like 204 No Content)
  const text = await response.text();
  return text ? JSON.parse(text) : {};
}

import { createContext, useState, useEffect, useContext } from 'react';
import { jwtDecode } from 'jwt-decode';

const AuthContext = createContext(null);

export const AuthProvider = ({ children }) => {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(null);
  const [loading, setLoading] = useState(true);

  const logout = () => {
    localStorage.removeItem('token');
    setToken(null);
    setUser(null);
  };

  useEffect(() => {
    // Check if user is logged in on initial load
    const storedToken = localStorage.getItem('token');
    if (storedToken) {
      try {
        const decodedUser = jwtDecode(storedToken);
        // Check if token is expired
        const currentTime = Date.now() / 1000;
        if (decodedUser.exp < currentTime) {
          // eslint-disable-next-line react-hooks/set-state-in-effect
          logout();
        } else {
          setToken(storedToken);
          setUser(decodedUser);
        }
      } catch (error) {
        console.error("Invalid token found in storage:", error);
        logout();
      }
    }
    setLoading(false);
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  const login = (newToken) => {
    try {
      const decodedUser = jwtDecode(newToken);
      localStorage.setItem('token', newToken);
      setToken(newToken);
      setUser(decodedUser);
      return true;
    } catch (error) {
      console.error("Failed to decode token during login:", error);
      return false;
    }
  };

  const hasRole = (role) => {
    return user?.role === role;
  };

  const value = {
    user,
    token,
    login,
    logout,
    hasRole,
    isAuthenticated: !!user,
    loading
  };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};

// eslint-disable-next-line react-refresh/only-export-components
export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within an AuthProvider');
  }
  return context;
};

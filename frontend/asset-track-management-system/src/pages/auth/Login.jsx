import React, { useState } from 'react';
import { useNavigate, Link, useLocation } from 'react-router-dom';
import { Mail, Lock, Eye, EyeOff, AlertCircle, Box, ArrowRight, CheckCircle2 } from 'lucide-react';
import { useAuth } from '../../context/AuthContext';
// import apiClient from '../../api/apiClient'; // For real API integration

const Login = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(false);
  
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const from = location.state?.from?.pathname || "/dashboard";

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setIsLoading(true);

    // Validation
    if (!email || !password) {
      setError('Please enter both email and password.');
      setIsLoading(false);
      return;
    }
    
    if (password.length < 8) {
      setError('Password must be at least 8 characters long.');
      setIsLoading(false);
      return;
    }

    try {
      // ** MOCK API CALL **
      // For real integration: const response = await apiClient.post('/auth/login', { email, password });
      // const token = response.data.token;
      
      // Simulate network request
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // Mock validation (accept anything for demonstration, or hardcode valid credentials)
      if (email === 'manager@assettrack.com' && password === 'password') {
        // Create a mock JWT for manager
        // Header: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9 (HS256)
        // Payload: {"sub":"123","email":"manager@assettrack.com","role":"Manager","exp":9999999999} 
        // Base64 encoded payload: eyJzdWIiOiIxMjMiLCJlbWFpbCI6Im1hbmFnZXJAYXNzZXR0cmFjay5jb20iLCJyb2xlIjoiTWFuYWdlciIsImV4cCI6OTk5OTk5OTk5OX0
        const mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjMiLCJlbWFpbCI6Im1hbmFnZXJAYXNzZXR0cmFjay5jb20iLCJyb2xlIjoiTWFuYWdlciIsImV4cCI6OTk5OTk5OTk5OX0.signature";
        
        login(mockToken);
        navigate(from, { replace: true });
      } else if (email === 'admin@assettrack.com' && password === 'password') {
         // Payload: {"sub":"124","email":"admin@assettrack.com","role":"Admin","exp":9999999999}
         const mockToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjQiLCJlbWFpbCI6ImFkbWluQGFzc2V0dHJhY2suY29tIiwicm9sZSI6IkFkbWluIiwiZXhwIjo5OTk5OTk5OTk5fQ.signature";
         login(mockToken);
         navigate(from, { replace: true });
      }
      else {
        setError('Authentication Failed. The email or password you entered is incorrect. Please try again.');
      }
    } catch (err) {
      setError('An error occurred during login. Please try again.');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex min-h-screen font-body-lg text-on-surface bg-neutral">
      {/* Left Panel - Branding */}
      <div className="hidden lg:flex w-1/2 bg-primary p-12 flex-col justify-between relative overflow-hidden">
        {/* Abstract background overlay */}
        <div className="absolute inset-0 bg-gradient-to-br from-primary/80 to-primary mix-blend-multiply opacity-50 z-0"></div>
        <div className="absolute inset-0 bg-[url('https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?q=80&w=2070&auto=format&fit=crop')] bg-cover bg-center opacity-20 mix-blend-overlay z-0"></div>
        
        <div className="relative z-10">
          <div className="flex items-center gap-3 text-white mb-16">
            <Box className="w-8 h-8" />
            <span className="text-2xl font-bold tracking-tight">AssetTrack</span>
          </div>
          
          <h1 className="text-4xl lg:text-5xl font-bold text-white leading-tight mb-6 max-w-lg">
            Streamline your inventory management with precision.
          </h1>
          <p className="text-primary-fixed-dim text-lg max-w-md">
            Access real-time tracking, detailed analytics, and comprehensive asset lifecycles in one secure platform.
          </p>
        </div>

        <div className="relative z-10 flex gap-6 mt-12">
          <div className="bg-white/10 backdrop-blur-md border border-white/20 rounded-lg p-5 flex-1 max-w-[200px]">
            <p className="text-primary-fixed-dim text-xs font-bold tracking-wider uppercase mb-1">Active Assets</p>
            <p className="text-white text-3xl font-bold">12,842</p>
          </div>
          <div className="bg-white/10 backdrop-blur-md border border-white/20 rounded-lg p-5 flex-1 max-w-[200px]">
            <p className="text-primary-fixed-dim text-xs font-bold tracking-wider uppercase mb-1">System Status</p>
            <div className="flex items-center gap-2 mt-1">
              <div className="w-2.5 h-2.5 rounded-full bg-green-400"></div>
              <p className="text-white font-medium">Operational</p>
            </div>
          </div>
        </div>
      </div>

      {/* Right Panel - Login Form */}
      <div className="w-full lg:w-1/2 flex items-center justify-center p-8 sm:p-12 relative">
        <div className="w-full max-w-md">
          <div className="mb-8">
            <h2 className="text-3xl font-bold text-on-surface mb-2 tracking-tight">Welcome Back</h2>
            <p className="text-secondary">Please enter your credentials to access the Management Console.</p>
          </div>

          {error && (
            <div className="mb-6 p-4 rounded-lg bg-error-container border border-error/20 flex gap-3 text-error items-start">
              <AlertCircle className="w-5 h-5 flex-shrink-0 mt-0.5" />
              <div className="text-sm">
                <p className="font-semibold mb-0.5">Authentication Failed</p>
                <p className="opacity-90">{error}</p>
              </div>
            </div>
          )}

          <form onSubmit={handleSubmit} className="space-y-5">
            <div>
              <label className="block text-sm font-semibold text-on-surface mb-1.5" htmlFor="email">
                Email Address
              </label>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Mail className="h-5 w-5 text-outline" />
                </div>
                <input
                  id="email"
                  type="email"
                  className="block w-full pl-10 pr-3 py-2.5 border border-outline-variant rounded-lg focus:ring-2 focus:ring-primary focus:border-primary bg-white text-on-surface transition-colors"
                  placeholder="manager@assettrack.com"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </div>
            </div>

            <div>
              <div className="flex justify-between items-center mb-1.5">
                <label className="block text-sm font-semibold text-on-surface" htmlFor="password">
                  Password
                </label>
                <a href="#" className="text-sm font-medium text-primary hover:text-primary-container transition-colors">
                  Forgot password?
                </a>
              </div>
              <div className="relative">
                <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                  <Lock className="h-5 w-5 text-outline" />
                </div>
                <input
                  id="password"
                  type={showPassword ? "text" : "password"}
                  className="block w-full pl-10 pr-10 py-2.5 border border-outline-variant rounded-lg focus:ring-2 focus:ring-primary focus:border-primary bg-white text-on-surface transition-colors"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                />
                <button
                  type="button"
                  className="absolute inset-y-0 right-0 pr-3 flex items-center"
                  onClick={() => setShowPassword(!showPassword)}
                >
                  {showPassword ? (
                    <EyeOff className="h-5 w-5 text-outline hover:text-on-surface transition-colors" />
                  ) : (
                    <Eye className="h-5 w-5 text-outline hover:text-on-surface transition-colors" />
                  )}
                </button>
              </div>
            </div>

            <div className="flex items-center">
              <input
                id="remember-me"
                name="remember-me"
                type="checkbox"
                className="h-4 w-4 text-primary focus:ring-primary border-outline rounded"
              />
              <label htmlFor="remember-me" className="ml-2 block text-sm text-secondary">
                Remember this device for 30 days
              </label>
            </div>

            <button
              type="submit"
              disabled={isLoading}
              className="w-full flex items-center justify-center py-2.5 px-4 border border-transparent rounded-lg shadow-sm text-sm font-semibold text-white bg-primary hover:bg-primary/90 focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-primary transition-colors disabled:opacity-70"
            >
              {isLoading ? (
                <div className="w-5 h-5 border-2 border-white border-t-transparent rounded-full animate-spin"></div>
              ) : (
                <>
                  Sign In to Console
                  <ArrowRight className="ml-2 h-4 w-4" />
                </>
              )}
            </button>
          </form>

          <div className="mt-8">
            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-outline-variant" />
              </div>
              <div className="relative flex justify-center text-xs uppercase">
                <span className="bg-neutral px-2 text-outline font-semibold tracking-wider">Or continue with</span>
              </div>
            </div>

            <div className="mt-6 grid grid-cols-2 gap-3">
              <button className="flex items-center justify-center gap-2 w-full px-4 py-2 border border-outline-variant rounded-lg shadow-sm bg-white text-sm font-medium text-on-surface hover:bg-surface-container-low transition-colors">
                <svg className="h-5 w-5" viewBox="0 0 24 24">
                  <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4" />
                  <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853" />
                  <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05" />
                  <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335" />
                </svg>
                Google
              </button>
              <button className="flex items-center justify-center gap-2 w-full px-4 py-2 border border-outline-variant rounded-lg shadow-sm bg-white text-sm font-medium text-on-surface hover:bg-surface-container-low transition-colors">
                <svg className="h-5 w-5" viewBox="0 0 21 21">
                    <path fill="#f25022" d="M1 1h9v9H1z"/>
                    <path fill="#00a4ef" d="M1 11h9v9H1z"/>
                    <path fill="#7fba00" d="M11 1h9v9h-9z"/>
                    <path fill="#ffb900" d="M11 11h9v9h-9z"/>
                </svg>
                Azure AD
              </button>
            </div>
          </div>

          <div className="mt-8 text-center text-sm text-secondary">
            New to the platform?{' '}
            <Link to="/signup" className="font-semibold text-primary hover:text-primary-container transition-colors">
              Create an Organization Account
            </Link>
          </div>

          <div className="absolute bottom-8 left-0 right-0 flex justify-between px-8 sm:px-12 text-xs text-outline">
            <span>© 2024 AssetTrack Inc.</span>
            <div className="flex gap-4">
              <a href="#" className="hover:text-on-surface transition-colors">Privacy</a>
              <a href="#" className="hover:text-on-surface transition-colors">Security</a>
              <a href="#" className="hover:text-on-surface transition-colors">Support</a>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;

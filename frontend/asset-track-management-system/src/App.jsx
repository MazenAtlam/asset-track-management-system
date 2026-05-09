import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layout/Layout';
import InventoryDashboard from './pages/dashboard/InventoryDashboard';
import AssetDirectory from './pages/assets/AssetDirectory';
import AssetForm from './pages/assets/AssetForm';
import Login from './pages/auth/Login';
import SignUp from './pages/auth/SignUp';
import UserManagement from './pages/admin/UserManagement';
import AssetDetails from './pages/assets/AssetDetails';
import { ToastProvider } from './components/ui/ToastContext';
import { AuthProvider } from './context/AuthContext';
import ProtectedRoute from './components/auth/ProtectedRoute';
import './App.css';

function App() {
  return (
    <ToastProvider>
      <AuthProvider>
        <BrowserRouter>
          <Routes>
            {/* Public Routes */}
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<SignUp />} />

            {/* Protected Routes */}
            <Route element={<ProtectedRoute />}>
              <Route path="/" element={<Layout />}>
                <Route index element={<Navigate to="/dashboard" replace />} />
                <Route path="dashboard" element={<InventoryDashboard />} />
                <Route path="assets" element={<AssetDirectory />} />
                <Route path="assets/new" element={<AssetForm />} />
                <Route path="assets/:id" element={<AssetDetails />} />

                
                {/* Admin Only Routes */}
                <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
                  <Route path="admin/users" element={<UserManagement />} />
                </Route>
              </Route>
            </Route>
            
            {/* Catch-all redirect */}
            <Route path="*" element={<Navigate to="/dashboard" replace />} />
          </Routes>
        </BrowserRouter>
      </AuthProvider>
    </ToastProvider>
  );
}

export default App;

import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layout/Layout';
import InventoryDashboard from './pages/dashboard/InventoryDashboard';
import AssetDirectory from './pages/assets/AssetDirectory';
import AssetForm from './pages/assets/AssetForm';
import Login from './pages/auth/Login';
import SignUp from './pages/auth/SignUp';
import UserManagement from './pages/admin/UserManagement';
import ReportManagement from './pages/admin/ReportManagement';
import AssetDetails from './pages/assets/AssetDetails';
import Profile from './pages/profile/Profile';
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
                <Route path="assets/:id" element={<AssetDetails />} />
                <Route path="profile" element={<Profile />} />

                {/* Admin & Manager Routes */}
                <Route element={<ProtectedRoute allowedRoles={['ADMIN', 'MANAGER']} />}>
                  <Route path="admin/reports" element={<ReportManagement />} />
                </Route>
                
                {/* Admin Only Routes */}
                <Route element={<ProtectedRoute allowedRoles={['ADMIN']} />}>
                  <Route path="assets/new" element={<AssetForm />} />
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

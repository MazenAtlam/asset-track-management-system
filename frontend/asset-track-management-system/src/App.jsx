import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Layout from './components/layout/Layout';
import InventoryDashboard from './pages/dashboard/InventoryDashboard';
import AssetDirectory from './pages/assets/AssetDirectory';
import AssetForm from './pages/assets/AssetForm';
import { ToastProvider } from './components/ui/ToastContext';
import './App.css';

function App() {
  return (
    <ToastProvider>
      <BrowserRouter>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Navigate to="/dashboard" replace />} />
            <Route path="dashboard" element={<InventoryDashboard />} />
            <Route path="assets" element={<AssetDirectory />} />
            <Route path="assets/new" element={<AssetForm />} />
          </Route>
        </Routes>
      </BrowserRouter>
    </ToastProvider>
  );
}

export default App;

import React from 'react';
import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Navbar from './Navbar';

const Layout = () => {
  return (
    <div className="bg-background text-on-surface font-body-md overflow-hidden flex h-screen">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0">
        <Navbar />
        <main className="flex-1 overflow-y-auto p-margin">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default Layout;

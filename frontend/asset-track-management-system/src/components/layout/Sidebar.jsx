import React from 'react';
import { Link, useLocation } from 'react-router-dom';

const Sidebar = () => {
  const location = useLocation();
  const path = location.pathname;

  return (
    <aside className="hidden md:flex flex-col h-screen py-md px-sm gap-xs bg-surface-container-low border-r border-outline-variant w-64 shrink-0">
      <div className="px-md mb-xl flex items-center gap-sm mt-sm">
        <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
          <span className="material-symbols-outlined text-on-primary" style={{ fontVariationSettings: "'FILL' 1" }}>
            inventory_2
          </span>
        </div>
        <div>
          <h2 className="font-h3 text-h3 font-black text-primary">AssetTrack</h2>
          <p className="font-body-sm text-body-sm text-on-surface-variant">Management Console</p>
        </div>
      </div>
      
      <nav className="flex-1 flex flex-col gap-xs">
        <Link 
          to="/dashboard" 
          className={`flex items-center gap-md px-md py-sm rounded-lg transition-all ${path === '/dashboard' ? 'bg-secondary-container text-on-secondary-container font-bold scale-[0.98]' : 'text-on-surface-variant hover:bg-surface-container-high'}`}
        >
          <span className="material-symbols-outlined" style={path === '/dashboard' ? { fontVariationSettings: "'FILL' 1" } : {}}>dashboard</span>
          <span className="font-label-bold text-label-bold">Dashboard</span>
        </Link>
        <Link 
          to="/assets" 
          className={`flex items-center gap-md px-md py-sm rounded-lg transition-all ${path === '/assets' ? 'bg-secondary-container text-on-secondary-container font-bold scale-[0.98]' : 'text-on-surface-variant hover:bg-surface-container-high'}`}
        >
          <span className="material-symbols-outlined" style={path === '/assets' ? { fontVariationSettings: "'FILL' 1" } : {}}>inventory_2</span>
          <span className="font-label-bold text-label-bold">Assets</span>
        </Link>
        <Link 
          to="/assets/new" 
          className={`flex items-center gap-md px-md py-sm rounded-lg transition-all ${path === '/assets/new' ? 'bg-secondary-container text-on-secondary-container font-bold scale-[0.98]' : 'text-on-surface-variant hover:bg-surface-container-high'}`}
        >
          <span className="material-symbols-outlined" style={path === '/assets/new' ? { fontVariationSettings: "'FILL' 1" } : {}}>add_box</span>
          <span className="font-label-bold text-label-bold">Add Asset</span>
        </Link>
      </nav>

      <div className="mt-auto flex flex-col gap-xs border-t border-outline-variant pt-md">
        <Link 
          to="/assets/new"
          className="bg-primary text-on-primary font-label-bold py-md px-lg rounded-lg mb-md flex items-center justify-center gap-sm hover:opacity-90 transition-opacity"
        >
          <span className="material-symbols-outlined">add</span>
          New Asset
        </Link>
        <a className="flex items-center gap-md px-md py-sm text-on-surface-variant hover:bg-surface-container-high transition-all rounded-lg" href="#">
          <span className="material-symbols-outlined">settings</span>
          <span className="font-label-bold text-label-bold">Settings</span>
        </a>
      </div>
    </aside>
  );
};

export default Sidebar;

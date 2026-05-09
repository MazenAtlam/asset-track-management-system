import React from 'react';
import { useLocation } from 'react-router-dom';

const Navbar = () => {
  const location = useLocation();
  
  const getBreadcrumbs = () => {
    const path = location.pathname;
    if (path === '/dashboard') return ['Dashboard'];
    if (path === '/assets') return ['Inventory', 'Asset Directory'];
    if (path === '/assets/new') return ['Inventory', 'Register Asset'];
    return ['AssetTrack'];
  };

  const breadcrumbs = getBreadcrumbs();

  return (
    <header className="flex justify-between items-center w-full px-lg h-16 bg-surface border-b border-outline-variant shadow-sm shrink-0">
      <div className="flex items-center gap-lg flex-1">
        <nav className="hidden lg:flex items-center gap-md">
          {breadcrumbs.map((crumb, index) => (
            <React.Fragment key={index}>
              <span className={`font-label-bold text-label-bold py-2 ${index === breadcrumbs.length - 1 ? 'text-primary border-b-2 border-primary' : 'text-on-surface-variant'}`}>
                {crumb}
              </span>
              {index < breadcrumbs.length - 1 && (
                <span className="material-symbols-outlined text-outline">chevron_right</span>
              )}
            </React.Fragment>
          ))}
        </nav>
      </div>

      <div className="flex items-center gap-md">
        <div className="flex items-center gap-sm">
          <button className="w-10 h-10 flex items-center justify-center rounded-full text-on-surface-variant hover:bg-surface-container transition-colors">
            <span className="material-symbols-outlined">notifications</span>
          </button>
          <button className="w-10 h-10 flex items-center justify-center rounded-full text-on-surface-variant hover:bg-surface-container transition-colors">
            <span className="material-symbols-outlined">help_outline</span>
          </button>
          <button className="w-10 h-10 flex items-center justify-center rounded-full text-on-surface-variant hover:bg-surface-container transition-colors">
            <span className="material-symbols-outlined">settings</span>
          </button>
        </div>
        
        <div className="h-8 w-[1px] bg-outline-variant mx-sm"></div>
        
        <div className="flex items-center gap-md">
          <div className="text-right hidden sm:block">
            <p className="font-label-bold text-label-bold text-on-surface">Member Two</p>
            <p className="text-[10px] text-on-surface-variant uppercase tracking-wider">Frontend Engineer</p>
          </div>
          <div className="w-10 h-10 rounded-full bg-primary text-on-primary flex items-center justify-center font-bold">
            M2
          </div>
        </div>
      </div>
    </header>
  );
};

export default Navbar;

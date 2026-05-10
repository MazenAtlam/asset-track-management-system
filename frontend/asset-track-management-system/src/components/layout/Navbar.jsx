import { useState, useEffect, useRef } from 'react';
import { useLocation, Link } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { apiClient } from '../../api/apiClient';

const Navbar = () => {
  const location = useLocation();
  const { user, logout } = useAuth();
  
  const [alerts, setAlerts] = useState([]);
  const [showNotifications, setShowNotifications] = useState(false);
  const [showProfileMenu, setShowProfileMenu] = useState(false);
  const notifRef = useRef(null);
  const profileRef = useRef(null);
  
  useEffect(() => {
    const fetchAlerts = async () => {
      try {
        const data = await apiClient.get('/alerts');
        setAlerts(data.alerts || []);
      } catch (error) {
        console.error('Failed to fetch alerts:', error);
      }
    };
    if (user) {
      fetchAlerts();
    }
  }, [user]);

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (notifRef.current && !notifRef.current.contains(event.target)) {
        setShowNotifications(false);
      }
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        setShowProfileMenu(false);
      }
    };
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const getBreadcrumbs = () => {
    const path = location.pathname;
    if (path.startsWith('/dashboard')) return ['Dashboard'];
    if (path.startsWith('/assets/new')) return ['Inventory', 'Register Asset'];
    if (path.startsWith('/assets')) return ['Inventory', 'Asset Directory'];
    if (path.startsWith('/admin/users')) return ['Admin', 'User Management'];
    if (path.startsWith('/admin/reports')) return ['Admin', 'Condition Reports'];
    return ['AssetTrack'];
  };

  const breadcrumbs = getBreadcrumbs();

  return (
    <header className="flex justify-between items-center w-full px-lg h-16 bg-surface border-b border-outline-variant shadow-sm shrink-0">
      <div className="flex items-center gap-lg flex-1">
        <nav className="hidden lg:flex items-center gap-md">
          {breadcrumbs.map((crumb, index) => (
            <span key={index} className="flex items-center gap-md">
              <span className={`font-label-bold text-label-bold py-2 ${index === breadcrumbs.length - 1 ? 'text-primary border-b-2 border-primary' : 'text-on-surface-variant'}`}>
                {crumb}
              </span>
              {index < breadcrumbs.length - 1 && (
                <span className="material-symbols-outlined text-outline">chevron_right</span>
              )}
            </span>
          ))}
        </nav>
      </div>

      <div className="flex items-center gap-md">
        <div className="flex items-center gap-sm">
          {/* Notifications Dropdown */}
          <div className="relative" ref={notifRef}>
            <button 
              onClick={() => setShowNotifications(!showNotifications)}
              className="w-10 h-10 flex items-center justify-center rounded-full text-on-surface-variant hover:bg-surface-container transition-colors relative"
            >
              <span className="material-symbols-outlined">notifications</span>
              {alerts.length > 0 && (
                <span className="absolute top-2 right-2 w-2 h-2 bg-error rounded-full animate-pulse"></span>
              )}
            </button>
            
            {showNotifications && (
              <div className="absolute right-0 mt-2 w-80 bg-surface rounded-xl shadow-lg border border-outline-variant overflow-hidden z-50 animate-in fade-in slide-in-from-top-2">
                <div className="p-4 border-b border-outline-variant flex justify-between items-center bg-surface-container-lowest">
                  <h3 className="font-h3 text-h3 text-on-surface">Notifications</h3>
                  <span className="bg-primary-container text-on-primary-container px-2 py-0.5 rounded-full text-xs font-bold">
                    {alerts.length} New
                  </span>
                </div>
                <div className="max-h-96 overflow-y-auto">
                  {alerts.length === 0 ? (
                    <div className="p-6 text-center text-on-surface-variant text-sm">
                      No new alerts.
                    </div>
                  ) : (
                    <div className="flex flex-col divide-y divide-outline-variant">
                      {alerts.map((alert) => (
                        <div key={alert.alertId} className={`p-4 hover:bg-surface-container-lowest transition-colors flex gap-3 ${alert.type === 'WARRANTY_EXPIRING' ? 'border-l-4 border-l-error' : 'border-l-4 border-l-secondary'}`}>
                          <span className={`material-symbols-outlined ${alert.type === 'WARRANTY_EXPIRING' ? 'text-error' : 'text-secondary'}`}>
                            {alert.type === 'WARRANTY_EXPIRING' ? 'warning' : 'inventory_2'}
                          </span>
                          <div>
                            <p className="text-sm font-semibold text-on-surface">{alert.type.replace('_', ' ')}</p>
                            <p className="text-xs text-on-surface-variant mt-1">{alert.message}</p>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
                <div className="p-3 border-t border-outline-variant bg-surface-container-lowest text-center">
                  <button className="text-sm text-primary font-semibold hover:underline">Mark all as read</button>
                </div>
              </div>
            )}
          </div>

          <button className="w-10 h-10 flex items-center justify-center rounded-full text-on-surface-variant hover:bg-surface-container transition-colors">
            <span className="material-symbols-outlined">help_outline</span>
          </button>
          <button className="w-10 h-10 flex items-center justify-center rounded-full text-on-surface-variant hover:bg-surface-container transition-colors">
            <span className="material-symbols-outlined">settings</span>
          </button>
        </div>
        
        <div className="h-8 w-[1px] bg-outline-variant mx-sm"></div>
        
        {/* Profile Dropdown */}
        <div className="relative" ref={profileRef}>
          <div 
            className="flex items-center gap-md cursor-pointer hover:bg-surface-container p-1 rounded-lg transition-colors"
            onClick={() => setShowProfileMenu(!showProfileMenu)}
          >
            <div className="text-right hidden sm:block">
              <p className="font-label-bold text-label-bold text-on-surface">
                {user ? `${user.firstName || 'User'} ${user.lastName || ''}` : 'Loading...'}
              </p>
              <p className="text-[10px] text-on-surface-variant uppercase tracking-wider">{user?.role || 'User'}</p>
            </div>
            <div className="w-10 h-10 rounded-full bg-primary text-on-primary flex items-center justify-center font-bold">
              {user ? (user.firstName ? user.firstName[0] : 'U') : 'U'}
            </div>
          </div>
          
          {showProfileMenu && (
            <div className="absolute right-0 mt-2 w-48 bg-surface rounded-xl shadow-lg border border-outline-variant overflow-hidden z-50 animate-in fade-in slide-in-from-top-2">
              <div className="p-4 border-b border-outline-variant flex flex-col gap-1">
                <p className="font-semibold text-sm text-on-surface truncate">{user?.email}</p>
              </div>
              <div className="p-2 flex flex-col">
                <Link 
                  to="/profile"
                  onClick={() => setShowProfileMenu(false)}
                  className="flex items-center gap-2 px-3 py-2 text-sm text-on-surface hover:bg-surface-container rounded-md w-full text-left transition-colors"
                >
                  <span className="material-symbols-outlined text-[18px]">person</span> Profile
                </Link>
                <button 
                  onClick={() => { setShowProfileMenu(false); logout(); }}
                  className="flex items-center gap-2 px-3 py-2 text-sm text-error hover:bg-error/10 rounded-md w-full text-left transition-colors mt-1"
                >
                  <span className="material-symbols-outlined text-[18px]">logout</span> Log out
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};

export default Navbar;

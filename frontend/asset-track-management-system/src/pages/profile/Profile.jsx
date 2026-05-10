import React from 'react';
import { useAuth } from '../../context/AuthContext';
import { Link } from 'react-router-dom';

const Profile = () => {
  const { user } = useAuth();

  return (
    <div className="p-lg max-w-5xl mx-auto w-full animate-in fade-in slide-in-from-bottom-4">
      <div className="mb-xl">
        <h1 className="font-h1 text-h1 text-on-surface">My Profile</h1>
        <p className="text-body-lg text-on-surface-variant mt-2">Manage your account details and view your assigned assets.</p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-lg">
        {/* Profile Details Card */}
        <div className="md:col-span-1 flex flex-col gap-lg">
          <div className="bg-surface rounded-2xl shadow-sm border border-outline-variant p-lg flex flex-col items-center text-center relative overflow-hidden">
            <div className="absolute top-0 left-0 right-0 h-24 bg-surface-container-high z-0"></div>
            <div className="w-24 h-24 rounded-full bg-primary-container text-on-primary-container flex items-center justify-center font-display text-4xl mb-md z-10 border-4 border-surface shadow-sm">
              {user?.firstName ? user.firstName[0] : (user?.sub ? user.sub[0].toUpperCase() : 'U')}
            </div>
            <h2 className="font-h3 text-h3 text-on-surface mt-2 z-10">
              {user?.firstName || 'AssetTrack'} {user?.lastName || 'User'}
            </h2>
            <p className="text-on-surface-variant text-sm mb-md z-10">{user?.sub || user?.email || 'user@example.com'}</p>
            <span className="bg-secondary-container text-on-secondary-container px-sm py-xs rounded-lg text-xs font-bold uppercase tracking-wider z-10">
              {user?.role || 'User Role'}
            </span>
          </div>
        </div>

        {/* Account Details & Settings Placeholder */}
        <div className="md:col-span-2 flex flex-col gap-lg">
          <div className="bg-surface rounded-2xl shadow-sm border border-outline-variant p-lg">
            <h3 className="font-h3 text-h3 text-on-surface mb-md pb-sm border-b border-outline-variant flex items-center gap-sm">
              <span className="material-symbols-outlined text-primary">badge</span>
              Account Information
            </h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 gap-md">
              <div>
                <p className="text-xs text-on-surface-variant font-semibold uppercase tracking-wider mb-1">First Name</p>
                <p className="text-on-surface font-medium">{user?.firstName || 'AssetTrack'}</p>
              </div>
              <div>
                <p className="text-xs text-on-surface-variant font-semibold uppercase tracking-wider mb-1">Last Name</p>
                <p className="text-on-surface font-medium">{user?.lastName || 'User'}</p>
              </div>
              <div>
                <p className="text-xs text-on-surface-variant font-semibold uppercase tracking-wider mb-1">Email Address</p>
                <p className="text-on-surface font-medium">{user?.sub || user?.email || 'user@example.com'}</p>
              </div>
              <div>
                <p className="text-xs text-on-surface-variant font-semibold uppercase tracking-wider mb-1">Role</p>
                <p className="text-on-surface font-medium capitalize">{(user?.role || 'User').toLowerCase()}</p>
              </div>
            </div>
          </div>

          <div className="bg-surface rounded-2xl shadow-sm border border-outline-variant p-lg">
            <h3 className="font-h3 text-h3 text-on-surface mb-md pb-sm border-b border-outline-variant flex items-center gap-sm">
              <span className="material-symbols-outlined text-primary">devices</span>
              My Assigned Assets
            </h3>
            <div className="flex flex-col items-center justify-center py-xl text-center bg-surface-container-lowest rounded-xl border border-dashed border-outline-variant">
              <span className="material-symbols-outlined text-outline text-5xl mb-sm opacity-50">inventory_2</span>
              <p className="text-on-surface-variant font-medium">You currently have no assets assigned to you.</p>
              <Link to="/assets" className="mt-md text-primary font-label-bold hover:underline">
                Browse Asset Directory
              </Link>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile;

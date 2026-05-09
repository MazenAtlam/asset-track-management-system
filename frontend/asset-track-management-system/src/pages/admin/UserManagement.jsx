import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { Shield, UserPlus, Search, Edit2, Trash2 } from 'lucide-react';

const UserManagement = () => {
  const { user } = useAuth();
  
  // Mock data for users
  const [users, setUsers] = useState([
    { id: 1, name: 'Alice Admin', email: 'admin@assettrack.com', role: 'ADMIN', status: 'Active' },
    { id: 2, name: 'Bob Manager', email: 'manager@assettrack.com', role: 'MANAGER', status: 'Active' },
    { id: 3, name: 'Charlie Dev', email: 'dev@assettrack.com', role: 'DEVELOPER', status: 'Inactive' },
  ]);

  return (
    <div className="p-8">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-on-surface flex items-center gap-2">
            <Shield className="w-8 h-8 text-primary" />
            User & Role Management
          </h1>
          <p className="text-secondary mt-1">Manage system access and assign roles to team members.</p>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-outline-variant overflow-hidden">
        <div className="p-4 border-b border-outline-variant flex justify-between items-center bg-surface-container-low">
          <div className="text-sm text-secondary">
            Showing <span className="font-medium text-on-surface">{users.length}</span> users
          </div>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-surface-container-low text-secondary text-sm uppercase tracking-wider">
                <th className="px-6 py-4 font-semibold">User</th>
                <th className="px-6 py-4 font-semibold">Role</th>
                <th className="px-6 py-4 font-semibold">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant">
              {users.map((u) => (
                <tr key={u.id} className="hover:bg-surface-container-lowest transition-colors">
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-primary-container text-white flex items-center justify-center font-bold text-sm">
                        {u.name.charAt(0)}
                      </div>
                      <div>
                        <div className="font-semibold text-on-surface">{u.name}</div>
                        <div className="text-sm text-secondary">{u.email}</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      u.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' :
                      u.role === 'MANAGER' ? 'bg-blue-100 text-blue-800' :
                      'bg-gray-100 text-gray-800'
                    }`}>
                      {u.role}
                    </span>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`inline-flex items-center gap-1.5 ${u.status === 'Active' ? 'text-green-600' : 'text-secondary'}`}>
                      <div className={`w-2 h-2 rounded-full ${u.status === 'Active' ? 'bg-green-500' : 'bg-outline'}`}></div>
                      <span className="text-sm font-medium">{u.status}</span>
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};

export default UserManagement;

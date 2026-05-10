import { useState, useEffect } from 'react';
import { Shield, Search } from 'lucide-react';
import { apiClient } from '../../api/apiClient';
import { useToast } from '../../components/ui/ToastContext';

const UserManagement = () => {
  const [data, setData] = useState({ users: [], totalItems: 0, totalPages: 1, currentPage: 1 });
  const [loading, setLoading] = useState(true);
  
  const [searchTerm, setSearchTerm] = useState('');
  const [searchInput, setSearchInput] = useState('');
  const [roleFilter, setRoleFilter] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);
  
  const toast = useToast();

  useEffect(() => {
    const handler = setTimeout(() => {
      setSearchTerm(searchInput);
      setCurrentPage(1);
    }, 300);
    return () => clearTimeout(handler);
  }, [searchInput]);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const queryParams = new URLSearchParams();
      queryParams.append('page', currentPage);
      queryParams.append('size', itemsPerPage);
      if (searchTerm) queryParams.append('search', searchTerm);
      if (roleFilter) queryParams.append('role', roleFilter);
      
      const response = await apiClient.get(`/users?${queryParams.toString()}`);
      setData(response);
    } catch (error) {
      toast.error(error.message || 'Failed to fetch users');
      setData({ users: [], totalItems: 0, totalPages: 1, currentPage: 1 });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchUsers();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, searchTerm, roleFilter]);

  const handleRoleChange = async (userId, newRole) => {
    try {
      await apiClient.put(`/users/${userId}/role`, { role: newRole });
      toast.success('User role updated successfully');
      fetchUsers();
    } catch (error) {
      toast.error(error.message || 'Failed to update user role');
    }
  };

  return (
    <div className="p-8 flex flex-col gap-xl">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-on-surface flex items-center gap-2">
            <Shield className="w-8 h-8 text-primary" />
            User & Role Management
          </h1>
          <p className="text-secondary mt-1">Manage system access and assign roles to team members.</p>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-outline-variant overflow-hidden flex flex-col">
        <div className="p-4 border-b border-outline-variant flex flex-col sm:flex-row justify-between items-center bg-surface-container-low gap-4">
          <div className="flex items-center gap-4 w-full sm:w-auto">
            <div className="relative flex-1 sm:w-64">
              <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                <Search className="h-4 w-4 text-outline" />
              </div>
              <input
                type="text"
                className="block w-full pl-10 pr-3 py-2 border border-outline-variant rounded-lg focus:ring-1 focus:ring-primary focus:border-primary text-sm"
                placeholder="Search users..."
                value={searchInput}
                onChange={(e) => setSearchInput(e.target.value)}
              />
            </div>
            <select
              className="px-3 py-2 border border-outline-variant rounded-lg text-sm focus:ring-1 focus:ring-primary focus:border-primary outline-none"
              value={roleFilter}
              onChange={(e) => { setRoleFilter(e.target.value); setCurrentPage(1); }}
            >
              <option value="">All Roles</option>
              <option value="ADMIN">Admin</option>
              <option value="MANAGER">Manager</option>
              <option value="DEVELOPER">Developer</option>
            </select>
          </div>
          <div className="text-sm text-secondary whitespace-nowrap">
            Showing <span className="font-medium text-on-surface">{data.users.length}</span> of <span className="font-medium text-on-surface">{data.totalItems}</span> users
          </div>
        </div>

        <div className="overflow-x-auto flex-1">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-surface-container-low text-secondary text-sm uppercase tracking-wider">
                <th className="px-6 py-4 font-semibold">User</th>
                <th className="px-6 py-4 font-semibold">Role</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant">
              {loading ? (
                <tr>
                  <td colSpan="2" className="px-6 py-8 text-center text-secondary">
                    <span className="material-symbols-outlined animate-spin text-primary text-3xl">sync</span>
                    <p className="mt-2">Loading users...</p>
                  </td>
                </tr>
              ) : data.users.length === 0 ? (
                <tr>
                  <td colSpan="2" className="px-6 py-8 text-center text-secondary">
                    No users found matching your criteria.
                  </td>
                </tr>
              ) : data.users.map((u) => (
                <tr key={u.id} className="hover:bg-surface-container-lowest transition-colors">
                  <td className="px-6 py-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 rounded-full bg-primary-container text-white flex items-center justify-center font-bold text-sm">
                        {(u.firstName || u.email).charAt(0).toUpperCase()}
                      </div>
                      <div>
                        <div className="font-semibold text-on-surface">{u.firstName} {u.lastName}</div>
                        <div className="text-sm text-secondary">{u.email}</div>
                      </div>
                    </div>
                  </td>
                  <td className="px-6 py-4">
                    <select
                      className={`inline-flex items-center pl-3 pr-8 py-1 rounded-full text-xs font-medium border border-transparent cursor-pointer hover:opacity-80 transition-opacity focus:ring-2 focus:ring-offset-1 focus:ring-primary outline-none ${
                        u.role === 'ADMIN' ? 'bg-purple-100 text-purple-800' :
                        u.role === 'MANAGER' ? 'bg-blue-100 text-blue-800' :
                        'bg-gray-100 text-gray-800'
                      }`}
                      value={u.role}
                      onChange={(e) => handleRoleChange(u.id, e.target.value)}
                    >
                      <option value="ADMIN">ADMIN</option>
                      <option value="MANAGER">MANAGER</option>
                      <option value="DEVELOPER">DEVELOPER</option>
                    </select>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="p-4 border-t border-outline-variant flex items-center justify-between bg-surface">
          <div className="text-sm text-secondary">
            Page {currentPage} of {data.totalPages || 1}
          </div>
          <div className="flex gap-2">
            <button
              className="px-3 py-1 border border-outline-variant rounded hover:bg-surface-container disabled:opacity-50 transition-colors"
              disabled={currentPage === 1 || loading}
              onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
            >
              Previous
            </button>
            <button
              className="px-3 py-1 border border-outline-variant rounded hover:bg-surface-container disabled:opacity-50 transition-colors"
              disabled={currentPage >= (data.totalPages || 1) || loading}
              onClick={() => setCurrentPage(p => Math.min(data.totalPages || 1, p + 1))}
            >
              Next
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default UserManagement;

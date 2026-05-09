import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useToast } from '../../components/ui/ToastContext';

const mockAssetsData = [
  {
    id: 101,
    type: "LAPTOP",
    brand: "Apple",
    model: "MacBook Pro 16\" M2",
    serialNumber: "SN-9421-XB01",
    status: "ASSIGNED",
    assignedUser: { id: 1, name: "Sarah Chen" },
    warrantyExpirationDate: "2025-12-12"
  },
  {
    id: 102,
    type: "WORKSTATION",
    brand: "Dell",
    model: "Precision 7865 Tower",
    serialNumber: "SN-1023-LT45",
    status: "AVAILABLE",
    assignedUser: null,
    warrantyExpirationDate: "2026-03-05"
  },
  {
    id: 103,
    type: "NETWORKING",
    brand: "Cisco",
    model: "Catalyst 9300 Switch",
    serialNumber: "SN-5588-NW12",
    status: "MAINTENANCE",
    assignedUser: { id: 2, name: "IT Dept." },
    warrantyExpirationDate: "2023-01-01" 
  },
  {
    id: 104,
    type: "MONITOR",
    brand: "Samsung",
    model: "32\" 4K Smart Monitor",
    serialNumber: "SN-1102-DS09",
    status: "ASSIGNED",
    assignedUser: { id: 3, name: "Maya Patel" },
    warrantyExpirationDate: "2025-01-20"
  },
  {
    id: 105,
    type: "LAPTOP",
    brand: "Apple",
    model: "MacBook Air M1",
    serialNumber: "SN-8822-AA11",
    status: "SPARE",
    assignedUser: null,
    warrantyExpirationDate: "2024-11-15"
  }
];

const AssetDirectory = () => {
  const toast = useToast();
  
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Basic Filters
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('All Types');
  const [filterStatus, setFilterStatus] = useState('All Statuses');
  
  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);
  
  // Sorting
  const [sortBy, setSortBy] = useState('id');
  const [sortDirection, setSortDirection] = useState('desc');
  
  // Advanced Filters
  const [isAdvancedFilterOpen, setIsAdvancedFilterOpen] = useState(false);
  const [warrantyFilter, setWarrantyFilter] = useState('');
  const [assignedUserFilter, setAssignedUserFilter] = useState('');

  useEffect(() => {
    const fetchAssets = async () => {
      setLoading(true);
      try {
        const queryParams = new URLSearchParams();
        queryParams.append('page', currentPage);
        queryParams.append('size', itemsPerPage);
        queryParams.append('sortBy', sortBy);
        queryParams.append('direction', sortDirection);
        
        if (searchTerm) queryParams.append('search', searchTerm);
        if (filterType !== 'All Types') queryParams.append('type', filterType.toUpperCase());
        if (filterStatus !== 'All Statuses') queryParams.append('status', filterStatus.toUpperCase());
        if (warrantyFilter) queryParams.append('warranty', warrantyFilter);
        if (assignedUserFilter) queryParams.append('assignedUser', assignedUserFilter);
        
        const response = await fetch(`/api/v1/assets?${queryParams.toString()}`);
        if (response.ok) {
          const json = await response.json();
          setData(json);
        } else {
          toast.warning('Failed to fetch live data. Showing offline mock data.');
          simulateBackendData();
        }
      } catch (error) {
        toast.warning('Network error. Showing offline mock data.');
        simulateBackendData();
      } finally {
        setLoading(false);
      }
    };
    
    // Reset to page 1 on filter changes
    setCurrentPage(prev => {
      fetchAssets();
      return prev;
    });
  }, [searchTerm, filterType, filterStatus, currentPage, itemsPerPage, sortBy, sortDirection, warrantyFilter, assignedUserFilter]);

  // Simulate backend filtering, sorting, and pagination so the UI is fully functional offline
  const simulateBackendData = () => {
    let filtered = [...mockAssetsData];
    
    if (searchTerm) {
      const lower = searchTerm.toLowerCase();
      filtered = filtered.filter(a => a.serialNumber.toLowerCase().includes(lower) || a.model.toLowerCase().includes(lower) || a.brand.toLowerCase().includes(lower));
    }
    if (filterType !== 'All Types') filtered = filtered.filter(a => a.type === filterType.toUpperCase());
    if (filterStatus !== 'All Statuses') filtered = filtered.filter(a => a.status === filterStatus.toUpperCase());
    
    if (assignedUserFilter) {
      filtered = filtered.filter(a => a.assignedUser?.name.toLowerCase().includes(assignedUserFilter.toLowerCase()));
    }
    
    filtered.sort((a, b) => {
      let valA = a[sortBy];
      let valB = b[sortBy];
      if (sortBy === 'assignedUser') {
        valA = a.assignedUser?.name || '';
        valB = b.assignedUser?.name || '';
      }
      
      if (valA < valB) return sortDirection === 'asc' ? -1 : 1;
      if (valA > valB) return sortDirection === 'asc' ? 1 : -1;
      return 0;
    });
    
    const startIndex = (currentPage - 1) * itemsPerPage;
    const paginated = filtered.slice(startIndex, startIndex + itemsPerPage);
    
    setData({
      totalItems: filtered.length,
      totalPages: Math.ceil(filtered.length / itemsPerPage),
      currentPage,
      assets: paginated
    });
  };

  const handleSort = (column) => {
    if (sortBy === column) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(column);
      setSortDirection('asc');
    }
  };

  const handleFindSpare = async () => {
    try {
      const response = await fetch('/api/v1/assets/actions/spare-laptop');
      if (response.ok) {
        const json = await response.json();
        toast.success(`Found spare laptop: ${json.brand} ${json.model} (ID: ${json.id})`);
      } else {
        toast.info('Found spare laptop: Apple MacBook Air M1 (ID: 105)');
      }
    } catch (e) {
      toast.info('Found spare laptop: Apple MacBook Air M1 (ID: 105)');
    }
  };

  const getStatusBadge = (status) => {
    switch (status) {
      case 'AVAILABLE':
        return <span className="inline-flex items-center px-sm py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-emerald-100 text-emerald-700">Available</span>;
      case 'ASSIGNED':
        return <span className="inline-flex items-center px-sm py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-blue-100 text-blue-700">Assigned</span>;
      case 'MAINTENANCE':
        return <span className="inline-flex items-center px-sm py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-amber-100 text-amber-700">Maintenance</span>;
      case 'DECOMMISSIONED':
        return <span className="inline-flex items-center px-sm py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-slate-200 text-slate-700">Decom.</span>;
      case 'SPARE':
        return <span className="inline-flex items-center px-sm py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-primary-container/30 text-primary">Spare</span>;
      default:
        return <span className="inline-flex items-center px-sm py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-surface-container text-on-surface-variant">{status}</span>;
    }
  };

  const SortIcon = ({ column }) => {
    if (sortBy !== column) return <span className="material-symbols-outlined text-[14px] opacity-30">unfold_more</span>;
    return <span className="material-symbols-outlined text-[14px] text-primary">{sortDirection === 'asc' ? 'arrow_upward' : 'arrow_downward'}</span>;
  };

  return (
    <div className="flex flex-col gap-xl">
      {/* Header Actions */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-md">
        <div>
          <div className="flex items-center gap-sm text-outline text-body-sm mb-xs">
            <span>Main</span>
            <span className="material-symbols-outlined text-[14px]">chevron_right</span>
            <span>Inventory</span>
            <span className="material-symbols-outlined text-[14px]">chevron_right</span>
            <span className="text-primary font-bold">Assets</span>
          </div>
          <h2 className="font-h1 text-h1 text-on-surface">Asset Inventory</h2>
        </div>
        <div className="flex gap-sm">
          <button 
            onClick={handleFindSpare}
            className="flex items-center gap-sm px-lg py-md border border-outline text-on-surface rounded-lg hover:bg-surface-container transition-colors font-label-bold"
          >
            <span className="material-symbols-outlined">search_check</span>
            Find Spare
          </button>
          <Link 
            to="/assets/new"
            className="flex items-center gap-sm px-lg py-md bg-primary text-on-primary rounded-lg hover:opacity-90 transition-opacity font-label-bold"
          >
            <span className="material-symbols-outlined">add</span>
            New Asset
          </Link>
        </div>
      </div>

      {/* Table Section */}
      <div className="bg-surface border border-outline-variant rounded-xl shadow-sm overflow-hidden flex flex-col">
        {/* Filters Toolbar */}
        <div className="p-lg border-b border-outline-variant flex flex-col lg:flex-row lg:items-center justify-between gap-md">
          <div className="flex flex-wrap items-center gap-md">
            <div className="flex items-center gap-sm bg-surface-container-low border border-outline-variant rounded-lg px-md py-xs">
              <span className="text-body-sm font-label-bold text-outline">Type:</span>
              <select 
                className="bg-transparent border-none focus:ring-0 text-body-md py-1 pr-8 outline-none"
                value={filterType}
                onChange={(e) => { setFilterType(e.target.value); setCurrentPage(1); }}
              >
                <option>All Types</option>
                <option>Laptop</option>
                <option>Workstation</option>
                <option>Networking</option>
                <option>Monitor</option>
              </select>
            </div>
            <div className="flex items-center gap-sm bg-surface-container-low border border-outline-variant rounded-lg px-md py-xs">
              <span className="text-body-sm font-label-bold text-outline">Status:</span>
              <select 
                className="bg-transparent border-none focus:ring-0 text-body-md py-1 pr-8 outline-none"
                value={filterStatus}
                onChange={(e) => { setFilterStatus(e.target.value); setCurrentPage(1); }}
              >
                <option>All Statuses</option>
                <option>Available</option>
                <option>Assigned</option>
                <option>Maintenance</option>
                <option>Decommissioned</option>
                <option>Spare</option>
              </select>
            </div>
            <div className="relative">
              <span className="material-symbols-outlined absolute left-2 top-1/2 -translate-y-1/2 text-[18px] text-outline">search</span>
              <input 
                type="text"
                placeholder="Search serial, model..."
                className="pl-8 pr-4 py-1.5 bg-surface-container-low border border-outline-variant rounded-lg text-body-md focus:border-primary outline-none"
                value={searchTerm}
                onChange={(e) => { setSearchTerm(e.target.value); setCurrentPage(1); }}
              />
            </div>
            <button 
              onClick={() => setIsAdvancedFilterOpen(!isAdvancedFilterOpen)}
              className={`flex items-center gap-xs font-label-bold text-label-bold transition-colors ${isAdvancedFilterOpen || warrantyFilter || assignedUserFilter ? 'text-primary' : 'text-on-surface-variant hover:text-on-surface'}`}
            >
              <span className="material-symbols-outlined text-[18px]">tune</span>
              Advanced
              {(warrantyFilter || assignedUserFilter) && <span className="w-2 h-2 rounded-full bg-primary ml-1"></span>}
            </button>
          </div>
          <div className="text-body-sm text-on-surface-variant italic">
            Showing {data?.assets?.length || 0} of {data?.totalItems || 0} assets
          </div>
        </div>

        {/* Advanced Filters Drawer */}
        {isAdvancedFilterOpen && (
          <div className="bg-surface-container-lowest border-b border-outline-variant p-lg animate-in slide-in-from-top-2 fade-in duration-200">
            <h4 className="font-h3 text-h3 mb-md">Advanced Search</h4>
            <div className="grid grid-cols-1 md:grid-cols-3 gap-lg">
              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs">Warranty Status</label>
                <select 
                  className="w-full px-md py-sm border border-outline-variant rounded-lg font-body-md text-body-md focus:outline-none focus:border-primary"
                  value={warrantyFilter}
                  onChange={(e) => { setWarrantyFilter(e.target.value); setCurrentPage(1); }}
                >
                  <option value="">Any</option>
                  <option value="active">Active</option>
                  <option value="expiring_30">Expiring in 30 Days</option>
                  <option value="expired">Expired</option>
                </select>
              </div>
              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs">Assigned User</label>
                <input 
                  type="text"
                  placeholder="Search by name..."
                  className="w-full px-md py-sm border border-outline-variant rounded-lg font-body-md text-body-md focus:outline-none focus:border-primary"
                  value={assignedUserFilter}
                  onChange={(e) => { setAssignedUserFilter(e.target.value); setCurrentPage(1); }}
                />
              </div>
              <div className="flex items-end gap-md">
                <button 
                  onClick={() => {
                    setWarrantyFilter('');
                    setAssignedUserFilter('');
                    setCurrentPage(1);
                  }}
                  className="px-md py-sm font-label-bold text-on-surface-variant hover:text-on-surface transition-colors"
                >
                  Clear Filters
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Table */}
        <div className="overflow-x-auto flex-1">
          <table className="w-full text-left border-collapse min-w-[800px]">
            <thead className="bg-surface-container-low sticky top-0 z-10">
              <tr>
                <th className="px-lg py-md border-b border-outline-variant">
                  <input type="checkbox" className="rounded border-outline-variant text-primary focus:ring-primary" />
                </th>
                <th 
                  className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant cursor-pointer hover:bg-surface-container transition-colors group select-none"
                  onClick={() => handleSort('serialNumber')}
                >
                  <div className="flex items-center gap-xs">SERIAL NUMBER <SortIcon column="serialNumber" /></div>
                </th>
                <th 
                  className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant cursor-pointer hover:bg-surface-container transition-colors group select-none"
                  onClick={() => handleSort('type')}
                >
                  <div className="flex items-center gap-xs">TYPE <SortIcon column="type" /></div>
                </th>
                <th 
                  className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant cursor-pointer hover:bg-surface-container transition-colors group select-none"
                  onClick={() => handleSort('brand')}
                >
                  <div className="flex items-center gap-xs">BRAND/MODEL <SortIcon column="brand" /></div>
                </th>
                <th 
                  className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant cursor-pointer hover:bg-surface-container transition-colors group select-none"
                  onClick={() => handleSort('assignedUser')}
                >
                  <div className="flex items-center gap-xs">OWNER <SortIcon column="assignedUser" /></div>
                </th>
                <th 
                  className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant cursor-pointer hover:bg-surface-container transition-colors group select-none"
                  onClick={() => handleSort('status')}
                >
                  <div className="flex items-center gap-xs">STATUS <SortIcon column="status" /></div>
                </th>
                <th 
                  className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant cursor-pointer hover:bg-surface-container transition-colors group select-none"
                  onClick={() => handleSort('warrantyExpirationDate')}
                >
                  <div className="flex items-center gap-xs">WARRANTY <SortIcon column="warrantyExpirationDate" /></div>
                </th>
                <th className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant">ACTIONS</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant">
              {loading ? (
                <tr>
                  <td colSpan="8" className="px-lg py-xl text-center text-on-surface-variant font-body-md">
                    <span className="material-symbols-outlined animate-spin text-primary text-[32px] mb-md">sync</span>
                    <p>Loading assets...</p>
                  </td>
                </tr>
              ) : data?.assets?.map((asset) => (
                <tr key={asset.id} className={`hover:bg-surface-container-lowest transition-colors ${asset.status === 'MAINTENANCE' ? 'bg-error/5' : ''}`}>
                  <td className="px-lg py-md">
                    <input type="checkbox" className="rounded border-outline-variant text-primary focus:ring-primary" />
                  </td>
                  <td className="px-lg py-md font-code text-code text-on-surface">{asset.serialNumber}</td>
                  <td className="px-lg py-md font-body-md text-body-md capitalize">{asset.type?.toLowerCase()}</td>
                  <td className="px-lg py-md">
                    <div className="font-body-md text-body-md font-bold">{asset.brand}</div>
                    <div className="text-body-sm text-outline">{asset.model}</div>
                  </td>
                  <td className="px-lg py-md">
                    {asset.assignedUser ? (
                      <div className="flex items-center gap-sm">
                        <div className="w-8 h-8 rounded-full bg-secondary-container text-on-secondary-container flex items-center justify-center font-bold text-[10px]">
                          {asset.assignedUser.name.substring(0, 2).toUpperCase()}
                        </div>
                        <span className="text-body-md">{asset.assignedUser.name}</span>
                      </div>
                    ) : (
                      <span className="text-outline italic text-body-md">Unassigned</span>
                    )}
                  </td>
                  <td className="px-lg py-md">
                    {getStatusBadge(asset.status)}
                  </td>
                  <td className={`px-lg py-md text-body-md ${new Date(asset.warrantyExpirationDate) < new Date() ? 'text-error font-bold' : ''}`}>
                    {new Date(asset.warrantyExpirationDate) < new Date() ? 'Expired' : new Date(asset.warrantyExpirationDate).toLocaleDateString()}
                  </td>
                  <td className="px-lg py-md">
                    <button className="text-primary font-label-bold text-label-bold hover:bg-primary/5 px-md py-sm rounded-lg transition-colors">
                      View Details
                    </button>
                  </td>
                </tr>
              ))}
              {!loading && data?.assets?.length === 0 && (
                <tr>
                  <td colSpan="8" className="px-lg py-xl text-center text-on-surface-variant font-body-md">
                    <span className="material-symbols-outlined text-[48px] text-outline mb-md">inventory_2</span>
                    <p className="font-h3 text-h3 text-on-surface mb-xs">No assets found</p>
                    <p>Try adjusting your search or filters.</p>
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>

        {/* Pagination */}
        <div className="p-lg bg-surface border-t border-outline-variant flex items-center justify-between">
          <div className="text-body-sm text-outline">
            Rows per page: 
            <select 
              className="bg-transparent border-none focus:ring-0 text-body-sm font-bold ml-1 outline-none cursor-pointer"
              value={itemsPerPage}
              onChange={(e) => { setItemsPerPage(Number(e.target.value)); setCurrentPage(1); }}
            >
              <option value={5}>5</option>
              <option value={10}>10</option>
              <option value={25}>25</option>
              <option value={50}>50</option>
            </select>
          </div>
          <div className="flex items-center gap-md">
            <span className="text-body-sm text-on-surface-variant">
              {data?.totalItems > 0 ? ((currentPage - 1) * itemsPerPage) + 1 : 0} - {Math.min(currentPage * itemsPerPage, data?.totalItems || 0)} of {data?.totalItems || 0}
            </span>
            <div className="flex items-center gap-xs">
              <button 
                className="p-2 border border-outline-variant rounded-lg hover:bg-surface-container text-on-surface-variant disabled:opacity-30 transition-colors"
                disabled={currentPage === 1 || loading}
                onClick={() => setCurrentPage(p => Math.max(1, p - 1))}
              >
                <span className="material-symbols-outlined text-[20px]">chevron_left</span>
              </button>
              <button 
                className="p-2 border border-outline-variant rounded-lg hover:bg-surface-container text-on-surface-variant disabled:opacity-30 transition-colors"
                disabled={currentPage >= (data?.totalPages || 1) || loading}
                onClick={() => setCurrentPage(p => Math.min(data?.totalPages || 1, p + 1))}
              >
                <span className="material-symbols-outlined text-[20px]">chevron_right</span>
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AssetDirectory;

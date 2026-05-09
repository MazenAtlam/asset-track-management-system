import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useToast } from '../../components/ui/ToastContext';
import { apiClient } from '../../api/apiClient';

const AssetDirectory = () => {
  const toast = useToast();
  
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Basic Filters
  const [searchInputValue, setSearchInputValue] = useState('');
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('All Types');
  const [filterStatus, setFilterStatus] = useState('All Statuses');
  
  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage, setItemsPerPage] = useState(10);

  // Debounce search input
  useEffect(() => {
    const handler = setTimeout(() => {
      if (searchTerm !== searchInputValue) {
        setSearchTerm(searchInputValue);
        setCurrentPage(1);
      }
    }, 300);

    return () => {
      clearTimeout(handler);
    };
  }, [searchInputValue, searchTerm]);
  
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
        
        const responseData = await apiClient.get(`/assets?${queryParams.toString()}`);
        setData(responseData);
      } catch (error) {
        toast.error(error.message || 'Failed to fetch assets.');
        setData({ assets: [], totalItems: 0, totalPages: 1, currentPage: 1 });
      } finally {
        setLoading(false);
      }
    };
    
    // Reset to page 1 on filter changes
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setCurrentPage(prev => {
      fetchAssets();
      return prev;
    });
  }, [searchTerm, filterType, filterStatus, currentPage, itemsPerPage, sortBy, sortDirection, warrantyFilter, assignedUserFilter, toast]);

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
      const json = await apiClient.get('/assets/actions/spare-laptop');
      toast.success(`Found spare laptop: ${json.brand} ${json.model} (ID: ${json.id})`);
    } catch (e) {
      toast.error(e.message || 'No spare laptops available at the moment.');
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

  const renderSortIcon = (column) => {
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
                value={searchInputValue}
                onChange={(e) => setSearchInputValue(e.target.value)}
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
                  <div className="flex items-center gap-xs">SERIAL NUMBER {renderSortIcon("serialNumber")}</div>
                </th>
                <th 
                  className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant cursor-pointer hover:bg-surface-container transition-colors group select-none"
                  onClick={() => handleSort('type')}
                >
                  <div className="flex items-center gap-xs">TYPE {renderSortIcon("type")}</div>
                </th>
                <th 
                  className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant cursor-pointer hover:bg-surface-container transition-colors group select-none"
                  onClick={() => handleSort('brand')}
                >
                  <div className="flex items-center gap-xs">BRAND/MODEL {renderSortIcon("brand")}</div>
                </th>
                <th 
                  className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant cursor-pointer hover:bg-surface-container transition-colors group select-none"
                  onClick={() => handleSort('assignedUser')}
                >
                  <div className="flex items-center gap-xs">OWNER {renderSortIcon("assignedUser")}</div>
                </th>
                <th 
                  className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant cursor-pointer hover:bg-surface-container transition-colors group select-none"
                  onClick={() => handleSort('status')}
                >
                  <div className="flex items-center gap-xs">STATUS {renderSortIcon("status")}</div>
                </th>
                <th 
                  className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant cursor-pointer hover:bg-surface-container transition-colors group select-none"
                  onClick={() => handleSort('warrantyExpirationDate')}
                >
                  <div className="flex items-center gap-xs">WARRANTY {renderSortIcon("warrantyExpirationDate")}</div>
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
                    <Link 
                      to={`/assets/${asset.id}`}
                      className="text-primary font-label-bold text-label-bold hover:bg-primary/5 px-md py-sm rounded-lg transition-colors inline-block"
                    >
                      View Details
                    </Link>
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

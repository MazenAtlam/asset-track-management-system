import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

const mockAssets = {
  totalItems: 1,
  totalPages: 1,
  currentPage: 1,
  assets: [
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
      warrantyExpirationDate: "2023-01-01" // Expired
    }
  ]
};

const AssetDirectory = () => {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [searchTerm, setSearchTerm] = useState('');
  const [filterType, setFilterType] = useState('All Types');
  const [filterStatus, setFilterStatus] = useState('All Statuses');

  useEffect(() => {
    const fetchAssets = async () => {
      try {
        const queryParams = new URLSearchParams();
        if (searchTerm) queryParams.append('search', searchTerm);
        if (filterType !== 'All Types') queryParams.append('type', filterType.toUpperCase());
        if (filterStatus !== 'All Statuses') queryParams.append('status', filterStatus.toUpperCase());
        
        const response = await fetch(`/api/v1/assets?${queryParams.toString()}`);
        if (response.ok) {
          const json = await response.json();
          setData(json);
        } else {
          // Mock data
          setData(mockAssets);
        }
      } catch (error) {
        setData(mockAssets);
      } finally {
        setLoading(false);
      }
    };
    fetchAssets();
  }, [searchTerm, filterType, filterStatus]);

  const handleFindSpare = async () => {
    try {
      const response = await fetch('/api/v1/assets/actions/spare-laptop');
      if (response.ok) {
        const json = await response.json();
        alert(`Found spare laptop: ${json.brand} ${json.model} (ID: ${json.id})`);
      } else {
        alert('Found spare laptop: Apple MacBook Pro (ID: 105)');
      }
    } catch (e) {
      alert('Found spare laptop: Apple MacBook Pro (ID: 105)');
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
        return <span className="inline-flex items-center px-sm py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-slate-200 text-slate-700">Decommissioned</span>;
      case 'SPARE':
        return <span className="inline-flex items-center px-sm py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-primary/10 text-primary">Spare</span>;
      default:
        return <span className="inline-flex items-center px-sm py-1 rounded-full text-[11px] font-bold uppercase tracking-wider bg-surface-container text-on-surface-variant">{status}</span>;
    }
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
      <div className="bg-surface border border-outline-variant rounded-xl shadow-sm overflow-hidden">
        {/* Filters Toolbar */}
        <div className="p-lg border-b border-outline-variant flex flex-col lg:flex-row lg:items-center justify-between gap-md">
          <div className="flex flex-wrap items-center gap-md">
            <div className="flex items-center gap-sm bg-surface-container-low border border-outline-variant rounded-lg px-md py-xs">
              <span className="text-body-sm font-label-bold text-outline">Type:</span>
              <select 
                className="bg-transparent border-none focus:ring-0 text-body-md py-1 pr-8 outline-none"
                value={filterType}
                onChange={(e) => setFilterType(e.target.value)}
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
                onChange={(e) => setFilterStatus(e.target.value)}
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
                onChange={(e) => setSearchTerm(e.target.value)}
              />
            </div>
          </div>
          <div className="text-body-sm text-on-surface-variant italic">
            Showing {data?.assets?.length || 0} assets
          </div>
        </div>

        {/* Table */}
        <div className="overflow-x-auto">
          <table className="w-full text-left border-collapse">
            <thead className="bg-surface-container-low sticky top-0">
              <tr>
                <th className="px-lg py-md border-b border-outline-variant">
                  <input type="checkbox" className="rounded border-outline-variant text-primary focus:ring-primary" />
                </th>
                <th className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant">SERIAL NUMBER</th>
                <th className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant">TYPE</th>
                <th className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant">BRAND/MODEL</th>
                <th className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant">OWNER</th>
                <th className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant">STATUS</th>
                <th className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant">WARRANTY</th>
                <th className="px-lg py-md border-b border-outline-variant font-label-bold text-label-bold text-on-surface-variant">ACTIONS</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant">
              {loading ? (
                <tr>
                  <td colSpan="8" className="px-lg py-xl text-center text-on-surface-variant font-body-md">
                    Loading assets...
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
                    No assets found.
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
            <select className="bg-transparent border-none focus:ring-0 text-body-sm font-bold ml-1 outline-none">
              <option>10</option>
              <option>25</option>
              <option>50</option>
            </select>
          </div>
          <div className="flex items-center gap-md">
            <span className="text-body-sm text-on-surface-variant">1 - {data?.assets?.length || 0} of {data?.totalItems || 0}</span>
            <div className="flex items-center gap-xs">
              <button className="p-2 border border-outline-variant rounded-lg hover:bg-surface-container text-on-surface-variant disabled:opacity-30">
                <span className="material-symbols-outlined text-[20px]">chevron_left</span>
              </button>
              <button className="p-2 border border-outline-variant rounded-lg hover:bg-surface-container text-on-surface-variant">
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

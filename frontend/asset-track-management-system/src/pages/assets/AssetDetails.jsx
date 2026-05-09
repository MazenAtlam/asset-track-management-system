import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { useToast } from '../../components/ui/ToastContext';

const AssetDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  
  const [asset, setAsset] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Status Update Form State
  const [newStatus, setNewStatus] = useState('');
  const [notes, setNotes] = useState('');
  const [updating, setUpdating] = useState(false);

  // Delete State
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    const fetchAsset = async () => {
      try {
        const data = await apiClient.get(`/assets/${id}`);
        setAsset(data);
        setNewStatus(data.status);
      } catch (error) {
        toast.error(error.message || 'Failed to load asset details.');
        navigate('/assets');
      } finally {
        setLoading(false);
      }
    };
    fetchAsset();
  }, [id, navigate, toast]);

  const handleStatusUpdate = async (e) => {
    e.preventDefault();
    setUpdating(true);
    try {
      const updatedAsset = await apiClient.put(`/assets/${id}`, {
        status: newStatus,
        notes: notes
      });
      setAsset(prev => ({ ...prev, status: updatedAsset.status || newStatus }));
      setNotes('');
      toast.success('Asset status updated successfully');
    } catch (error) {
      toast.error(error.message || 'Failed to update asset status.');
    } finally {
      setUpdating(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Are you sure you want to permanently delete this asset? This action cannot be undone.')) {
      return;
    }
    
    setDeleting(true);
    try {
      await apiClient.delete(`/assets/${id}`);
      toast.success('Asset deleted successfully');
      navigate('/assets');
    } catch (error) {
      toast.error(error.message || 'Failed to delete asset. Ensure it is not currently assigned.');
      setDeleting(false);
    }
  };

  if (loading) {
    return (
      <div className="flex flex-col items-center justify-center p-xl gap-md">
        <span className="material-symbols-outlined animate-spin text-primary text-[48px]">sync</span>
        <p className="font-body-md text-on-surface-variant">Loading asset details...</p>
      </div>
    );
  }

  if (!asset) return null;

  return (
    <div className="flex flex-col gap-xl max-w-5xl mx-auto w-full">
      {/* Header Actions */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-md">
        <div>
          <div className="flex items-center gap-sm text-outline text-body-sm mb-xs">
            <span>Main</span>
            <span className="material-symbols-outlined text-[14px]">chevron_right</span>
            <Link to="/assets" className="hover:underline">Inventory</Link>
            <span className="material-symbols-outlined text-[14px]">chevron_right</span>
            <span className="text-primary font-bold">Details</span>
          </div>
          <h2 className="font-h1 text-h1 text-on-surface flex items-center gap-sm">
            {asset.brand} {asset.model}
            <span className="px-sm py-1 bg-surface-container-low border border-outline-variant rounded text-body-sm font-code text-on-surface-variant">
              {asset.serialNumber}
            </span>
          </h2>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-lg">
        {/* Left Column: Details */}
        <div className="lg:col-span-2 flex flex-col gap-lg">
          <div className="bg-surface border border-outline-variant rounded-xl shadow-sm p-lg">
            <h3 className="font-h3 text-h3 text-on-surface border-b border-outline-variant pb-xs mb-lg">Asset Information</h3>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-y-md gap-x-lg">
              <div>
                <p className="font-label-bold text-label-bold text-outline uppercase tracking-wider mb-xs">Asset ID</p>
                <p className="font-body-md text-on-surface">{asset.id}</p>
              </div>
              <div>
                <p className="font-label-bold text-label-bold text-outline uppercase tracking-wider mb-xs">Type</p>
                <p className="font-body-md text-on-surface capitalize">{asset.type?.toLowerCase()}</p>
              </div>
              <div>
                <p className="font-label-bold text-label-bold text-outline uppercase tracking-wider mb-xs">Purchase Date</p>
                <p className="font-body-md text-on-surface">
                  {asset.purchaseDate ? new Date(asset.purchaseDate).toLocaleDateString() : 'N/A'}
                </p>
              </div>
              <div>
                <p className="font-label-bold text-label-bold text-outline uppercase tracking-wider mb-xs">Warranty Expiration</p>
                <p className={`font-body-md ${new Date(asset.warrantyExpirationDate) < new Date() ? 'text-error font-bold' : 'text-on-surface'}`}>
                  {asset.warrantyExpirationDate ? new Date(asset.warrantyExpirationDate).toLocaleDateString() : 'N/A'}
                  {new Date(asset.warrantyExpirationDate) < new Date() && ' (Expired)'}
                </p>
              </div>
            </div>
          </div>

          <div className="bg-surface border border-outline-variant rounded-xl shadow-sm p-lg">
            <h3 className="font-h3 text-h3 text-on-surface border-b border-outline-variant pb-xs mb-lg">Current Owner</h3>
            {asset.assignedUser ? (
              <div className="flex items-center gap-md">
                <div className="w-12 h-12 rounded-full bg-secondary-container text-on-secondary-container flex items-center justify-center font-h3">
                  {asset.assignedUser.name?.substring(0, 2).toUpperCase()}
                </div>
                <div>
                  <p className="font-label-bold text-on-surface text-lg">{asset.assignedUser.name}</p>
                  <p className="font-body-sm text-outline">User ID: {asset.assignedUser.id}</p>
                </div>
              </div>
            ) : (
              <div className="flex items-center gap-sm p-md bg-surface-container-lowest border border-outline-variant rounded-lg border-dashed">
                <span className="material-symbols-outlined text-outline">person_off</span>
                <p className="font-body-md text-on-surface-variant italic">This asset is currently unassigned.</p>
              </div>
            )}
          </div>
        </div>

        {/* Right Column: Actions */}
        <div className="flex flex-col gap-lg">
          {/* Status Update */}
          <div className="bg-surface border border-outline-variant rounded-xl shadow-sm p-lg">
            <h3 className="font-h3 text-h3 text-on-surface border-b border-outline-variant pb-xs mb-lg flex items-center gap-2">
              <span className="material-symbols-outlined text-[20px]">manage_history</span>
              Update Status
            </h3>
            <form onSubmit={handleStatusUpdate} className="flex flex-col gap-md">
              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs" htmlFor="status">Current Status</label>
                <select 
                  id="status"
                  className="w-full px-md py-sm bg-surface-container-lowest border border-outline-variant rounded-lg font-body-md text-body-md focus:outline-none focus:border-primary"
                  value={newStatus}
                  onChange={(e) => setNewStatus(e.target.value)}
                  required
                >
                  <option value="AVAILABLE">Available</option>
                  <option value="ASSIGNED">Assigned</option>
                  <option value="MAINTENANCE">Maintenance</option>
                  <option value="DECOMMISSIONED">Decommissioned</option>
                  <option value="SPARE">Spare</option>
                </select>
              </div>
              
              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs" htmlFor="notes">Notes (Optional)</label>
                <textarea 
                  id="notes"
                  rows="3"
                  className="w-full px-md py-sm bg-surface-container-lowest border border-outline-variant rounded-lg font-body-md text-body-md focus:outline-none focus:border-primary resize-none"
                  placeholder="e.g. Battery replaced, returned by user..."
                  value={notes}
                  onChange={(e) => setNotes(e.target.value)}
                ></textarea>
              </div>
              
              <button 
                type="submit"
                disabled={updating || (newStatus === asset.status && !notes)}
                className="w-full bg-primary text-on-primary py-sm rounded-lg font-label-bold text-label-bold hover:opacity-90 transition-opacity disabled:opacity-50 flex justify-center items-center gap-2"
              >
                {updating ? <span className="material-symbols-outlined animate-spin text-[18px]">sync</span> : 'Update Asset'}
              </button>
            </form>
          </div>

          {/* Danger Zone */}
          <div className="bg-surface border border-error/30 rounded-xl shadow-sm p-lg">
            <h3 className="font-h3 text-h3 text-error border-b border-error/20 pb-xs mb-lg flex items-center gap-2">
              <span className="material-symbols-outlined text-[20px]">warning</span>
              Danger Zone
            </h3>
            <p className="font-body-sm text-on-surface-variant mb-md">
              Permanently remove this asset from the inventory. It must be returned (unassigned) before deletion.
            </p>
            <button 
              onClick={handleDelete}
              disabled={deleting}
              className="w-full bg-error text-on-error py-sm rounded-lg font-label-bold text-label-bold hover:bg-error/90 transition-colors disabled:opacity-50 flex justify-center items-center gap-2"
            >
              {deleting ? <span className="material-symbols-outlined animate-spin text-[18px]">sync</span> : 'Delete Asset'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AssetDetails;

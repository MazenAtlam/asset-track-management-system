import { useState, useEffect } from 'react';
import { useParams, useNavigate, Link } from 'react-router-dom';
import { apiClient } from '../../api/apiClient';
import { useToast } from '../../components/ui/ToastContext';
import { useAuth } from '../../context/AuthContext';

const AssetDetails = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const toast = useToast();
  const { hasRole } = useAuth();
  
  const [asset, setAsset] = useState(null);
  const [loading, setLoading] = useState(true);
  
  // Status Update Form State
  const [newStatus, setNewStatus] = useState('');
  const [notes, setNotes] = useState('');
  const [updating, setUpdating] = useState(false);

  // Delete State
  const [deleting, setDeleting] = useState(false);

  // Allocation State
  const [allocationUserId, setAllocationUserId] = useState('');
  const [allocationDate, setAllocationDate] = useState(new Date().toISOString().split('T')[0]);
  const [allocating, setAllocating] = useState(false);

  // Return State
  const [returnDate, setReturnDate] = useState(new Date().toISOString().split('T')[0]);
  const [returnCondition, setReturnCondition] = useState('GOOD');
  const [returnStatus, setReturnStatus] = useState('SPARE');
  const [returning, setReturning] = useState(false);

  // History State
  const [history, setHistory] = useState({ data: [], totalPages: 1, currentPage: 1, loading: true });

  // Reporting State
  const [showReportModal, setShowReportModal] = useState(false);
  const [reportIssueType, setReportIssueType] = useState('HARDWARE');
  const [reportDescription, setReportDescription] = useState('');
  const [reportCondition, setReportCondition] = useState('POOR');
  const [reporting, setReporting] = useState(false);

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

  const fetchHistory = async (page = 1) => {
    setHistory(prev => ({ ...prev, loading: true }));
    try {
      const data = await apiClient.get(`/assets/${id}/history?page=${page}&size=5`);
      setHistory({
        data: data.history || [],
        totalPages: data.totalPages || 1,
        currentPage: data.currentPage || 1,
        loading: false
      });
    } catch (error) {
      console.error(error);
      setHistory(prev => ({ ...prev, loading: false }));
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchAsset();
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchHistory();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

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
      fetchHistory(history.currentPage);
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

  const handleAllocate = async (e) => {
    e.preventDefault();
    if (!allocationUserId) return toast.error("User ID is required");
    setAllocating(true);
    try {
      await apiClient.post(`/assets/${id}/allocate`, {
        userId: parseInt(allocationUserId, 10),
        allocationDate
      });
      toast.success('Asset allocated successfully');
      fetchAsset();
      fetchHistory(1);
    } catch (error) {
      toast.error(error.message || 'Failed to allocate asset');
    } finally {
      setAllocating(false);
    }
  };

  const handleReturn = async (e) => {
    e.preventDefault();
    setReturning(true);
    try {
      await apiClient.post(`/assets/${id}/return`, {
        returnDate,
        conditionUponReturn: returnCondition,
        newStatus: returnStatus
      });
      toast.success('Asset returned successfully');
      fetchAsset();
      fetchHistory(1);
    } catch (error) {
      toast.error(error.message || 'Failed to return asset');
    } finally {
      setReturning(false);
    }
  };

  const submitReport = async (e) => {
    e.preventDefault();
    setReporting(true);
    try {
      await apiClient.post(`/assets/${id}/reports`, {
        issueType: reportIssueType,
        description: reportDescription,
        condition: reportCondition
      });
      toast.success('Condition report submitted successfully');
      setShowReportModal(false);
      setReportDescription('');
    } catch (error) {
      toast.error(error.message || 'Failed to submit report');
    } finally {
      setReporting(false);
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
    <div className="flex flex-col gap-xl max-w-5xl mx-auto w-full relative">
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
        <div className="flex gap-sm">
          <button 
            onClick={() => setShowReportModal(true)}
            className="flex items-center gap-sm px-lg py-md border border-outline text-on-surface rounded-lg hover:bg-surface-container transition-colors font-label-bold"
          >
            <span className="material-symbols-outlined text-[18px]">report_problem</span>
            Report Issue
          </button>
        </div>
      </div>

      {showReportModal && (
        <div className="fixed inset-0 bg-black/50 z-50 flex items-center justify-center p-4">
          <div className="bg-surface rounded-xl shadow-xl w-full max-w-md overflow-hidden animate-in fade-in zoom-in-95 duration-200">
            <div className="p-lg border-b border-outline-variant flex justify-between items-center bg-surface-container-low">
              <h3 className="font-h3 text-h3 text-on-surface flex items-center gap-2">
                <span className="material-symbols-outlined text-error">report</span>
                Report Asset Issue
              </h3>
              <button onClick={() => setShowReportModal(false)} className="text-on-surface-variant hover:text-on-surface">
                <span className="material-symbols-outlined">close</span>
              </button>
            </div>
            <form onSubmit={submitReport} className="p-lg flex flex-col gap-md">
              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs">Issue Type</label>
                <select 
                  className="w-full px-md py-sm bg-surface-container-lowest border border-outline-variant rounded-lg font-body-md focus:outline-none focus:border-primary"
                  value={reportIssueType}
                  onChange={(e) => setReportIssueType(e.target.value)}
                >
                  <option value="HARDWARE">Hardware Failure</option>
                  <option value="SOFTWARE">Software/OS Issue</option>
                  <option value="DAMAGE">Physical Damage</option>
                  <option value="LOSS">Lost or Stolen</option>
                </select>
              </div>
              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs">Current Condition</label>
                <select 
                  className="w-full px-md py-sm bg-surface-container-lowest border border-outline-variant rounded-lg font-body-md focus:outline-none focus:border-primary"
                  value={reportCondition}
                  onChange={(e) => setReportCondition(e.target.value)}
                >
                  <option value="GOOD">Good - Functional but impaired</option>
                  <option value="FAIR">Fair - Needs attention</option>
                  <option value="POOR">Poor - Unusable</option>
                </select>
              </div>
              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs">Description</label>
                <textarea 
                  rows="4"
                  className="w-full px-md py-sm bg-surface-container-lowest border border-outline-variant rounded-lg font-body-md focus:outline-none focus:border-primary resize-none"
                  placeholder="Describe the issue in detail..."
                  value={reportDescription}
                  onChange={(e) => setReportDescription(e.target.value)}
                  required
                ></textarea>
              </div>
              <div className="flex justify-end gap-sm mt-sm">
                <button 
                  type="button" 
                  onClick={() => setShowReportModal(false)}
                  className="px-lg py-sm font-label-bold rounded-lg hover:bg-surface-container text-on-surface-variant transition-colors"
                >
                  Cancel
                </button>
                <button 
                  type="submit"
                  disabled={reporting || !reportDescription}
                  className="px-lg py-sm bg-error text-white font-label-bold rounded-lg hover:bg-error/90 transition-colors disabled:opacity-50 flex items-center gap-2"
                >
                  {reporting ? <span className="material-symbols-outlined animate-spin text-[16px]">sync</span> : 'Submit Report'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

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
              <div className="flex flex-col gap-md">
                <div className="flex items-center gap-md">
                  <div className="w-12 h-12 rounded-full bg-secondary-container text-on-secondary-container flex items-center justify-center font-h3">
                    {asset.assignedUser.name?.substring(0, 2).toUpperCase()}
                  </div>
                  <div>
                    <p className="font-label-bold text-on-surface text-lg">{asset.assignedUser.name}</p>
                    <p className="font-body-sm text-outline">User ID: {asset.assignedUser.id}</p>
                  </div>
                </div>
                {(hasRole('ADMIN') || hasRole('MANAGER')) && (
                  <form onSubmit={handleReturn} className="mt-md pt-md border-t border-outline-variant bg-surface-container-lowest p-md rounded-lg">
                    <h4 className="font-label-bold text-on-surface mb-md">Process Return</h4>
                    <div className="grid grid-cols-1 md:grid-cols-3 gap-md">
                      <div>
                        <label className="block text-xs font-bold text-outline uppercase mb-1">Return Date</label>
                        <input type="date" className="w-full p-2 border border-outline-variant bg-white rounded text-sm focus:border-primary focus:outline-none" value={returnDate} onChange={e => setReturnDate(e.target.value)} required />
                      </div>
                      <div>
                        <label className="block text-xs font-bold text-outline uppercase mb-1">Condition</label>
                        <select className="w-full p-2 border border-outline-variant bg-white rounded text-sm focus:border-primary focus:outline-none" value={returnCondition} onChange={e => setReturnCondition(e.target.value)}>
                          <option value="GOOD">Good</option>
                          <option value="FAIR">Fair</option>
                          <option value="POOR">Poor</option>
                        </select>
                      </div>
                      <div>
                        <label className="block text-xs font-bold text-outline uppercase mb-1">New Status</label>
                        <select className="w-full p-2 border border-outline-variant bg-white rounded text-sm focus:border-primary focus:outline-none" value={returnStatus} onChange={e => setReturnStatus(e.target.value)}>
                          <option value="SPARE">Spare</option>
                          <option value="AVAILABLE">Available</option>
                          <option value="MAINTENANCE">Maintenance</option>
                          <option value="DECOMMISSIONED">Decommissioned</option>
                        </select>
                      </div>
                    </div>
                    <button type="submit" disabled={returning} className="mt-md bg-secondary text-white px-4 py-2 rounded text-sm font-bold hover:bg-secondary/90 transition-colors disabled:opacity-50">
                      {returning ? 'Processing...' : 'Return Asset'}
                    </button>
                  </form>
                )}
              </div>
            ) : (
              <div className="flex flex-col gap-md">
                <div className="flex items-center gap-sm p-md bg-surface-container-lowest border border-outline-variant rounded-lg border-dashed">
                  <span className="material-symbols-outlined text-outline">person_off</span>
                  <p className="font-body-md text-on-surface-variant italic">This asset is currently unassigned.</p>
                </div>
                {(hasRole('ADMIN') || hasRole('MANAGER')) && (asset.status === 'AVAILABLE' || asset.status === 'SPARE') && (
                  <form onSubmit={handleAllocate} className="mt-xs pt-md border-t border-outline-variant">
                    <h4 className="font-label-bold text-on-surface mb-md">Allocate Asset</h4>
                    <div className="flex flex-col sm:flex-row gap-md">
                      <div className="flex-1">
                        <label className="block text-xs font-bold text-outline uppercase mb-1">User ID</label>
                        <input type="number" placeholder="Enter User ID" className="w-full p-2 border border-outline-variant bg-white rounded text-sm focus:border-primary focus:outline-none" value={allocationUserId} onChange={e => setAllocationUserId(e.target.value)} required />
                      </div>
                      <div className="flex-1">
                        <label className="block text-xs font-bold text-outline uppercase mb-1">Allocation Date</label>
                        <input type="date" className="w-full p-2 border border-outline-variant bg-white rounded text-sm focus:border-primary focus:outline-none" value={allocationDate} onChange={e => setAllocationDate(e.target.value)} required />
                      </div>
                    </div>
                    <button type="submit" disabled={allocating} className="mt-md bg-primary text-white px-4 py-2 rounded text-sm font-bold hover:bg-primary/90 transition-colors disabled:opacity-50">
                      {allocating ? 'Allocating...' : 'Allocate Asset'}
                    </button>
                  </form>
                )}
              </div>
            )}
          </div>
        </div>

        {/* Right Column: Actions */}
        <div className="flex flex-col gap-lg">
          {/* Status Update */}
          {(hasRole('ADMIN') || hasRole('MANAGER')) && (
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
                  {updating ? <span className="material-symbols-outlined animate-spin text-[18px]">sync</span> : 'Update Status'}
                </button>
              </form>
            </div>
          )}

          {/* Danger Zone */}
          {hasRole('ADMIN') && (
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
          )}
        </div>
      </div>

      {/* History Section */}
      <div className="bg-surface border border-outline-variant rounded-xl shadow-sm p-lg mt-xs">
        <h3 className="font-h3 text-h3 text-on-surface border-b border-outline-variant pb-xs mb-lg flex items-center gap-2">
          <span className="material-symbols-outlined text-[20px]">history</span>
          Chain of Custody
        </h3>
        
        {history.loading ? (
          <div className="p-8 text-center text-secondary">
            <span className="material-symbols-outlined animate-spin">sync</span> Loading history...
          </div>
        ) : history.data.length === 0 ? (
          <div className="p-8 text-center text-secondary bg-surface-container-lowest rounded-lg border border-dashed border-outline-variant">
            No allocation history found for this asset.
          </div>
        ) : (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-surface-container-low text-secondary text-sm uppercase tracking-wider">
                  <th className="px-4 py-3 font-semibold">User</th>
                  <th className="px-4 py-3 font-semibold">Assigned Date</th>
                  <th className="px-4 py-3 font-semibold">Returned Date</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-outline-variant">
                {history.data.map((record) => (
                  <tr key={record.historyId} className="hover:bg-surface-container-lowest">
                    <td className="px-4 py-3">
                      <div className="font-semibold text-on-surface">{record.userName}</div>
                      <div className="text-xs text-secondary">ID: {record.userId}</div>
                    </td>
                    <td className="px-4 py-3 text-sm">{new Date(record.assignedDate).toLocaleDateString()}</td>
                    <td className="px-4 py-3 text-sm">
                      {record.returnedDate ? new Date(record.returnedDate).toLocaleDateString() : (
                        <span className="text-primary font-medium bg-primary-container/20 px-2 py-0.5 rounded text-xs">Current</span>
                      )}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            
            {/* Pagination for history */}
            {history.totalPages > 1 && (
              <div className="mt-4 flex items-center justify-between border-t border-outline-variant pt-4">
                <span className="text-sm text-secondary">Page {history.currentPage} of {history.totalPages}</span>
                <div className="flex gap-2">
                  <button 
                    disabled={history.currentPage === 1}
                    onClick={() => fetchHistory(history.currentPage - 1)}
                    className="px-3 py-1 border border-outline-variant rounded disabled:opacity-50 hover:bg-surface-container text-sm"
                  >
                    Prev
                  </button>
                  <button 
                    disabled={history.currentPage === history.totalPages}
                    onClick={() => fetchHistory(history.currentPage + 1)}
                    className="px-3 py-1 border border-outline-variant rounded disabled:opacity-50 hover:bg-surface-container text-sm"
                  >
                    Next
                  </button>
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
};

export default AssetDetails;

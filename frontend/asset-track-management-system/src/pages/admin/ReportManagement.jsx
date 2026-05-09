import { useState, useEffect } from 'react';
import { ShieldAlert } from 'lucide-react';
import { apiClient } from '../../api/apiClient';
import { useToast } from '../../components/ui/ToastContext';
import { Link } from 'react-router-dom';

const ReportManagement = () => {
  const [data, setData] = useState({ reports: [], totalItems: 0, totalPages: 1, currentPage: 1 });
  const [loading, setLoading] = useState(true);
  
  const [statusFilter, setStatusFilter] = useState('');
  const [issueTypeFilter, setIssueTypeFilter] = useState('');
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);
  
  const toast = useToast();

  const fetchReports = async () => {
    setLoading(true);
    try {
      const queryParams = new URLSearchParams();
      queryParams.append('page', currentPage);
      queryParams.append('size', itemsPerPage);
      if (statusFilter) queryParams.append('status', statusFilter);
      if (issueTypeFilter) queryParams.append('issueType', issueTypeFilter);
      
      const response = await apiClient.get(`/reports?${queryParams.toString()}`);
      setData(response);
    } catch (error) {
      toast.error(error.message || 'Failed to fetch reports');
      setData({ reports: [], totalItems: 0, totalPages: 1, currentPage: 1 });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    // eslint-disable-next-line react-hooks/set-state-in-effect
    fetchReports();
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [currentPage, statusFilter, issueTypeFilter]);

  return (
    <div className="p-8 flex flex-col gap-xl">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-3xl font-bold text-on-surface flex items-center gap-2">
            <ShieldAlert className="w-8 h-8 text-primary" />
            Condition Reports
          </h1>
          <p className="text-secondary mt-1">Review and manage hardware issues reported by users.</p>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-outline-variant overflow-hidden flex flex-col">
        <div className="p-4 border-b border-outline-variant flex flex-col sm:flex-row justify-between items-center bg-surface-container-low gap-4">
          <div className="flex items-center gap-4 w-full sm:w-auto">
            <select
              className="px-3 py-2 border border-outline-variant rounded-lg text-sm focus:ring-1 focus:ring-primary focus:border-primary outline-none"
              value={statusFilter}
              onChange={(e) => { setStatusFilter(e.target.value); setCurrentPage(1); }}
            >
              <option value="">All Statuses</option>
              <option value="PENDING">Pending</option>
              <option value="REVIEWED">Reviewed</option>
              <option value="RESOLVED">Resolved</option>
            </select>
            <select
              className="px-3 py-2 border border-outline-variant rounded-lg text-sm focus:ring-1 focus:ring-primary focus:border-primary outline-none"
              value={issueTypeFilter}
              onChange={(e) => { setIssueTypeFilter(e.target.value); setCurrentPage(1); }}
            >
              <option value="">All Types</option>
              <option value="HARDWARE">Hardware Failure</option>
              <option value="SOFTWARE">Software Issue</option>
              <option value="DAMAGE">Physical Damage</option>
              <option value="LOSS">Loss/Theft</option>
            </select>
          </div>
          <div className="text-sm text-secondary whitespace-nowrap">
            Showing <span className="font-medium text-on-surface">{data.reports.length}</span> of <span className="font-medium text-on-surface">{data.totalItems}</span> reports
          </div>
        </div>

        <div className="overflow-x-auto flex-1">
          <table className="w-full text-left border-collapse">
            <thead>
              <tr className="bg-surface-container-low text-secondary text-sm uppercase tracking-wider">
                <th className="px-6 py-4 font-semibold">Asset</th>
                <th className="px-6 py-4 font-semibold">Reported By</th>
                <th className="px-6 py-4 font-semibold">Issue</th>
                <th className="px-6 py-4 font-semibold">Status</th>
                <th className="px-6 py-4 font-semibold">Date</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-outline-variant">
              {loading ? (
                <tr>
                  <td colSpan="5" className="px-6 py-8 text-center text-secondary">
                    <span className="material-symbols-outlined animate-spin text-primary text-3xl">sync</span>
                    <p className="mt-2">Loading reports...</p>
                  </td>
                </tr>
              ) : data.reports.length === 0 ? (
                <tr>
                  <td colSpan="5" className="px-6 py-8 text-center text-secondary">
                    No condition reports found matching your criteria.
                  </td>
                </tr>
              ) : data.reports.map((r) => (
                <tr key={r.id} className="hover:bg-surface-container-lowest transition-colors">
                  <td className="px-6 py-4">
                    <Link to={`/assets/${r.assetId}`} className="font-semibold text-primary hover:underline">
                      {r.assetName}
                    </Link>
                    <div className="text-xs text-secondary mt-1">ID: {r.assetId}</div>
                  </td>
                  <td className="px-6 py-4 text-on-surface">{r.reporterName}</td>
                  <td className="px-6 py-4">
                    <div className="font-medium text-on-surface">{r.issueType}</div>
                    <div className="text-sm text-secondary truncate max-w-[200px]" title={r.description}>{r.description}</div>
                  </td>
                  <td className="px-6 py-4">
                    <span className={`inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium ${
                      r.status === 'RESOLVED' ? 'bg-green-100 text-green-800' :
                      r.status === 'REVIEWED' ? 'bg-blue-100 text-blue-800' :
                      'bg-orange-100 text-orange-800'
                    }`}>
                      {r.status || 'PENDING'}
                    </span>
                  </td>
                  <td className="px-6 py-4 text-sm text-secondary">
                    {new Date(r.submittedAt).toLocaleDateString()}
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

export default ReportManagement;

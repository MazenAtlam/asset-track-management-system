import React, { useState, useEffect } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, ResponsiveContainer, Cell } from 'recharts';
import { useToast } from '../../components/ui/ToastContext';
import { apiClient } from '../../api/apiClient';

const COLORS = {
  LAPTOP: '#003d9b',
  MONITOR: '#b2c5ff',
  ACCESSORY: '#515f74'
};

const InventoryDashboard = () => {
  const [data, setData] = useState(null);
  const [alerts, setAlerts] = useState([]);
  const [loading, setLoading] = useState(true);
  const toast = useToast();

  useEffect(() => {
    const fetchDashboardData = async () => {
      try {
        const [summaryData, alertsData] = await Promise.all([
          apiClient.get('/dashboard/summary'),
          apiClient.get('/alerts')
        ]);
        setData(summaryData);
        setAlerts(alertsData.alerts || []);
      } catch (error) {
        toast.error(error.message || 'Failed to fetch dashboard data.');
      } finally {
        setLoading(false);
      }
    };
    fetchDashboardData();
  }, []);

  if (loading) {
    return <div className="p-margin font-body-md text-on-surface-variant flex items-center gap-2"><span className="material-symbols-outlined animate-spin">sync</span> Loading dashboard...</div>;
  }

  const chartData = data ? Object.entries(data.byType).map(([key, value]) => ({
    name: key,
    value: value
  })) : [];

  return (
    <div className="flex flex-col gap-xl">
      {/* Breadcrumbs */}
      <nav className="flex items-center gap-xs">
        <span className="font-body-sm text-body-sm text-outline">Dashboard</span>
        <span className="material-symbols-outlined text-[16px] text-outline">chevron_right</span>
        <span className="font-body-sm text-body-sm text-primary font-semibold">Overview</span>
      </nav>

      {/* Metric Cards Bento Grid */}
      <section className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-lg">
        {/* Total Assets */}
        <div className="bg-surface border border-outline-variant rounded-xl p-lg shadow-sm relative overflow-hidden group">
          <div className="flex justify-between items-start mb-md">
            <div>
              <p className="font-label-bold text-label-bold text-on-surface-variant uppercase tracking-wider mb-xs">Total Assets</p>
              <h2 className="font-h1 text-h1">{data?.totalAssets || 0}</h2>
            </div>
            <div className="p-3 bg-primary-container/10 rounded-lg text-primary">
              <span className="material-symbols-outlined">inventory</span>
            </div>
          </div>
          <div className="flex items-center gap-xs">
            <span className="text-emerald-600 flex items-center font-label-bold text-label-bold">
              <span className="material-symbols-outlined text-[16px]">trending_up</span> 12%
            </span>
            <span className="font-body-sm text-body-sm text-outline">vs last month</span>
          </div>
          <div className="absolute bottom-0 left-0 w-full h-[2px] bg-primary scale-x-0 group-hover:scale-x-100 transition-transform origin-left"></div>
        </div>

        {/* Assigned */}
        <div className="bg-surface border border-outline-variant rounded-xl p-lg shadow-sm relative overflow-hidden group">
          <div className="flex justify-between items-start mb-md">
            <div>
              <p className="font-label-bold text-label-bold text-on-surface-variant uppercase tracking-wider mb-xs">Assigned</p>
              <h2 className="font-h1 text-h1">{data?.byStatus?.ASSIGNED || 0}</h2>
            </div>
            <div className="p-3 bg-secondary-container/20 rounded-lg text-secondary">
              <span className="material-symbols-outlined">person_check</span>
            </div>
          </div>
          <div className="w-full bg-surface-container-highest h-1.5 rounded-full mb-xs">
            <div className="bg-secondary h-full rounded-full" style={{ width: `${((data?.byStatus?.ASSIGNED || 0) / (data?.totalAssets || 1)) * 100}%` }}></div>
          </div>
          <p className="font-body-sm text-body-sm text-outline">{Math.round(((data?.byStatus?.ASSIGNED || 0) / (data?.totalAssets || 1)) * 100)}% Utilization rate</p>
        </div>

        {/* Available */}
        <div className="bg-surface border border-outline-variant rounded-xl p-lg shadow-sm relative overflow-hidden group">
          <div className="flex justify-between items-start mb-md">
            <div>
              <p className="font-label-bold text-label-bold text-on-surface-variant uppercase tracking-wider mb-xs">Available</p>
              <h2 className="font-h1 text-h1">{data?.byStatus?.AVAILABLE || 0}</h2>
            </div>
            <div className="p-3 bg-emerald-100 rounded-lg text-emerald-700">
              <span className="material-symbols-outlined">check_circle</span>
            </div>
          </div>
          <div className="flex items-center gap-xs">
            <span className="px-2 py-0.5 bg-emerald-100 text-emerald-700 rounded-full font-label-bold text-[10px]">READY TO DEPLOY</span>
          </div>
        </div>

        {/* Spare */}
        <div className="bg-surface border border-outline-variant rounded-xl p-lg shadow-sm relative overflow-hidden group">
          <div className="flex justify-between items-start mb-md">
            <div>
              <p className="font-label-bold text-label-bold text-on-surface-variant uppercase tracking-wider mb-xs">Spare Assets</p>
              <h2 className="font-h1 text-h1 text-primary">{data?.byStatus?.SPARE || 0}</h2>
            </div>
            <div className="p-3 bg-primary-container/20 rounded-lg text-primary">
              <span className="material-symbols-outlined">devices</span>
            </div>
          </div>
          <div className="flex items-center gap-xs">
            <span className="font-body-sm text-body-sm text-primary font-semibold">Backup Pool</span>
          </div>
        </div>
      </section>

      {/* Secondary Grid: Charts and Alerts */}
      <section className="grid grid-cols-1 lg:grid-cols-3 gap-lg">
        {/* Distribution Bar Chart */}
        <div className="lg:col-span-2 bg-surface border border-outline-variant rounded-xl p-lg shadow-sm flex flex-col">
          <div className="flex justify-between items-center mb-xl">
            <div>
              <h3 className="font-h3 text-h3 text-on-surface">Asset Distribution</h3>
              <p className="font-body-sm text-body-sm text-outline">Inventory breakdown by category</p>
            </div>
            <select className="bg-surface border border-outline-variant rounded-lg text-body-sm px-md py-1 outline-none">
              <option>Current State</option>
            </select>
          </div>
          <div className="flex-1 min-h-[250px]">
            <ResponsiveContainer width="100%" height="100%">
              <BarChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                <XAxis dataKey="name" tick={{ fontSize: 12, fill: '#737685' }} axisLine={false} tickLine={false} />
                <YAxis tick={{ fontSize: 12, fill: '#737685' }} axisLine={false} tickLine={false} />
                <Tooltip 
                  cursor={{ fill: 'rgba(0,0,0,0.02)' }}
                  contentStyle={{ borderRadius: '8px', border: '1px solid #e0e3e5', boxShadow: '0px 10px 15px -3px rgba(0,0,0,0.1)' }}
                />
                <Bar dataKey="value" radius={[4, 4, 0, 0]}>
                  {chartData.map((entry, index) => (
                    <Cell key={`cell-${index}`} fill={COLORS[entry.name] || '#003d9b'} />
                  ))}
                </Bar>
              </BarChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Recent Alerts */}
        <div className="bg-surface border border-outline-variant rounded-xl p-lg shadow-sm flex flex-col">
          <h3 className="font-h3 text-h3 text-on-surface mb-xl">System Alerts</h3>
          <div className="flex flex-col gap-md flex-1 overflow-y-auto max-h-[300px]">
            {alerts.length === 0 ? (
              <p className="text-on-surface-variant font-body-sm text-center py-md">No active alerts.</p>
            ) : (
              alerts.map((alert, idx) => (
                <div key={alert.alertId || idx} className={`flex gap-md p-md border-l-4 rounded-r-lg ${alert.type === 'WARRANTY_EXPIRING' ? 'bg-error-container/5 border-error' : 'bg-secondary-container/10 border-secondary'}`}>
                  <span className={`material-symbols-outlined ${alert.type === 'WARRANTY_EXPIRING' ? 'text-error' : 'text-secondary'}`}>
                    {alert.type === 'WARRANTY_EXPIRING' ? 'history_edu' : 'package_2'}
                  </span>
                  <div>
                    <p className="font-label-bold text-label-bold text-on-surface">
                      {alert.type.replace('_', ' ')}
                    </p>
                    <p className="font-body-sm text-body-sm text-on-surface-variant mb-xs">{alert.message}</p>
                  </div>
                </div>
              ))
            )}
          </div>
          <button className="mt-xl text-primary font-label-bold text-label-bold hover:underline flex items-center justify-center gap-xs">
            View All Alerts <span className="material-symbols-outlined text-[16px]">arrow_forward</span>
          </button>
        </div>
      </section>
    </div>
  );
};

export default InventoryDashboard;

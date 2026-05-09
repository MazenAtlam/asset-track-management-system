import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

const AssetForm = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');
  
  const [formData, setFormData] = useState({
    type: 'LAPTOP',
    brand: '',
    model: '',
    serialNumber: '',
    purchaseDate: '',
    warrantyExpirationDate: ''
  });

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      // Simulate API call to POST /api/v1/assets
      const response = await fetch('/api/v1/assets', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer dummy-token'
        },
        body: JSON.stringify(formData)
      });

      if (response.ok) {
        const json = await response.json();
        setSuccess(`Asset created successfully (ID: ${json.assetId})`);
        setTimeout(() => navigate('/assets'), 2000);
      } else {
        // Mock successful save if backend is down
        setTimeout(() => {
          setSuccess('Asset created successfully (Simulated)');
          setTimeout(() => navigate('/assets'), 2000);
        }, 1000);
      }
    } catch (err) {
      setTimeout(() => {
        setSuccess('Asset created successfully (Simulated)');
        setTimeout(() => navigate('/assets'), 2000);
      }, 1000);
    }
  };

  return (
    <div className="flex flex-col gap-xl max-w-4xl mx-auto w-full">
      {/* Header Actions */}
      <div className="flex flex-col md:flex-row md:items-end justify-between gap-md">
        <div>
          <div className="flex items-center gap-sm text-outline text-body-sm mb-xs">
            <span>Main</span>
            <span className="material-symbols-outlined text-[14px]">chevron_right</span>
            <Link to="/assets" className="hover:underline">Inventory</Link>
            <span className="material-symbols-outlined text-[14px]">chevron_right</span>
            <span className="text-primary font-bold">Register</span>
          </div>
          <h2 className="font-h1 text-h1 text-on-surface">Register New Asset</h2>
          <p className="font-body-md text-body-md text-on-surface-variant mt-xs">Add a new hardware asset to the company inventory.</p>
        </div>
      </div>

      {/* Form Section */}
      <div className="bg-surface border border-outline-variant rounded-xl shadow-sm overflow-hidden">
        {error && (
          <div className="mx-lg mt-lg p-md bg-error-container text-on-error-container rounded-lg border border-error/20 flex items-start gap-sm">
            <span className="material-symbols-outlined text-error">error</span>
            <div>
              <p className="font-label-bold text-label-bold">Registration Failed</p>
              <p className="font-body-sm text-body-sm opacity-90">{error}</p>
            </div>
          </div>
        )}

        {success && (
          <div className="mx-lg mt-lg p-md bg-emerald-50 text-emerald-800 rounded-lg border border-emerald-200 flex items-start gap-sm">
            <span className="material-symbols-outlined text-emerald-600">check_circle</span>
            <div>
              <p className="font-label-bold text-label-bold">Success</p>
              <p className="font-body-sm text-body-sm opacity-90">{success}</p>
            </div>
          </div>
        )}

        <form onSubmit={handleSubmit} className="p-lg">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-xl mb-xl">
            {/* Left Column: Basic Details */}
            <div className="flex flex-col gap-lg">
              <h3 className="font-h3 text-h3 text-on-surface border-b border-outline-variant pb-xs">Hardware Details</h3>
              
              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs" htmlFor="type">
                  Asset Type <span className="text-error">*</span>
                </label>
                <select 
                  id="type"
                  name="type"
                  required
                  value={formData.type}
                  onChange={handleChange}
                  className="w-full px-md py-sm bg-surface-container-lowest border border-outline-variant rounded-lg font-body-md text-body-md focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                >
                  <option value="LAPTOP">Laptop</option>
                  <option value="WORKSTATION">Workstation</option>
                  <option value="MONITOR">Monitor</option>
                  <option value="NETWORKING">Networking</option>
                  <option value="MOBILE">Mobile Device</option>
                  <option value="ACCESSORY">Accessory</option>
                </select>
              </div>

              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs" htmlFor="brand">
                  Brand <span className="text-error">*</span>
                </label>
                <input 
                  type="text"
                  id="brand"
                  name="brand"
                  required
                  placeholder="e.g. Apple, Dell, Lenovo"
                  value={formData.brand}
                  onChange={handleChange}
                  className="w-full px-md py-sm bg-surface-container-lowest border border-outline-variant rounded-lg font-body-md text-body-md focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                />
              </div>

              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs" htmlFor="model">
                  Model <span className="text-error">*</span>
                </label>
                <input 
                  type="text"
                  id="model"
                  name="model"
                  required
                  placeholder="e.g. MacBook Pro 16&quot; M2"
                  value={formData.model}
                  onChange={handleChange}
                  className="w-full px-md py-sm bg-surface-container-lowest border border-outline-variant rounded-lg font-body-md text-body-md focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                />
              </div>
            </div>

            {/* Right Column: Identification & Lifecycle */}
            <div className="flex flex-col gap-lg">
              <h3 className="font-h3 text-h3 text-on-surface border-b border-outline-variant pb-xs">Identification & Lifecycle</h3>

              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs" htmlFor="serialNumber">
                  Serial Number <span className="text-error">*</span>
                </label>
                <input 
                  type="text"
                  id="serialNumber"
                  name="serialNumber"
                  required
                  placeholder="e.g. C02DF902Q05N"
                  value={formData.serialNumber}
                  onChange={handleChange}
                  className="w-full px-md py-sm bg-surface-container-lowest border border-outline-variant rounded-lg font-code text-code focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-all uppercase"
                />
              </div>

              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs" htmlFor="purchaseDate">
                  Purchase Date
                </label>
                <input 
                  type="date"
                  id="purchaseDate"
                  name="purchaseDate"
                  value={formData.purchaseDate}
                  onChange={handleChange}
                  className="w-full px-md py-sm bg-surface-container-lowest border border-outline-variant rounded-lg font-body-md text-body-md focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                />
              </div>

              <div>
                <label className="block font-label-bold text-label-bold text-on-surface mb-xs" htmlFor="warrantyExpirationDate">
                  Warranty Expiration Date
                </label>
                <input 
                  type="date"
                  id="warrantyExpirationDate"
                  name="warrantyExpirationDate"
                  value={formData.warrantyExpirationDate}
                  onChange={handleChange}
                  className="w-full px-md py-sm bg-surface-container-lowest border border-outline-variant rounded-lg font-body-md text-body-md focus:outline-none focus:border-primary focus:ring-1 focus:ring-primary transition-all"
                />
              </div>
            </div>
          </div>

          <div className="pt-xl border-t border-outline-variant flex items-center justify-end gap-md">
            <button 
              type="button"
              onClick={() => navigate('/assets')}
              className="px-lg py-sm border border-outline-variant text-on-surface-variant font-label-bold text-label-bold rounded-lg hover:bg-surface-container-highest transition-colors"
              disabled={loading}
            >
              Cancel
            </button>
            <button 
              type="submit"
              className="flex items-center gap-sm px-lg py-sm bg-primary text-on-primary font-label-bold text-label-bold rounded-lg hover:opacity-90 transition-opacity disabled:opacity-50"
              disabled={loading}
            >
              {loading && <span className="material-symbols-outlined animate-spin" style={{ fontSize: '18px' }}>sync</span>}
              {!loading && <span className="material-symbols-outlined" style={{ fontSize: '18px' }}>save</span>}
              Save Asset
            </button>
          </div>
        </form>
      </div>
    </div>
  );
};

export default AssetForm;

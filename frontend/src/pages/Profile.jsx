import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import './Profile.css';

const Profile = () => {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [editMode, setEditMode] = useState(false);
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    currentPassword: '',
    newPassword: '',
    confirmPassword: ''
  });
  const [successMessage, setSuccessMessage] = useState('');
  const [passwordError, setPasswordError] = useState('');

  useEffect(() => {
    fetchUserProfile();
  }, []);

  const fetchUserProfile = async () => {
    try {
      const response = await api.get('/api/users/profile');
      setUser(response.data);
      setFormData({
        username: response.data.username,
        email: response.data.email,
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      });
    } catch (error) {
      setError('Profil bilgileri yüklenirken bir hata oluştu.');
      console.error('Profil yükleme hatası:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
    // Şifre alanları değiştiğinde hata mesajını temizle
    if (name.includes('Password')) {
      setPasswordError('');
    }
  };

  const validatePasswords = () => {
    if (formData.newPassword && formData.newPassword.length < 6) {
      setPasswordError('Yeni şifre en az 6 karakter olmalıdır.');
      return false;
    }
    if (formData.newPassword !== formData.confirmPassword) {
      setPasswordError('Yeni şifreler eşleşmiyor.');
      return false;
    }
    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError(null);
    setSuccessMessage('');
    setPasswordError('');

    if (formData.newPassword && !validatePasswords()) {
      return;
    }

    try {
      const updateData = {
        username: formData.username,
        email: formData.email
      };

      if (formData.currentPassword && formData.newPassword) {
        updateData.currentPassword = formData.currentPassword;
        updateData.newPassword = formData.newPassword;
      }

      await api.put('/api/users/profile', updateData);
      setSuccessMessage('Profil başarıyla güncellendi.');
      setEditMode(false);
      
      // Şifre alanlarını temizle
      setFormData(prev => ({
        ...prev,
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      }));
      
      fetchUserProfile();
    } catch (error) {
      const errorMessage = error.response?.data?.message || 'Profil güncellenirken bir hata oluştu.';
      if (errorMessage.includes('password')) {
        setPasswordError(errorMessage);
      } else {
        setError(errorMessage);
      }
    }
  };

  if (loading) {
    return (
      <div className="profile-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Profil yükleniyor...</p>
        </div>
      </div>
    );
  }

  if (error && !user) {
    return (
      <div className="profile-container">
        <div className="error-message">
          {error}
        </div>
      </div>
    );
  }

  return (
    <div className="profile-container">
      <div className="profile-card">
        <div className="profile-header">
          <h2>Profil Bilgileri</h2>
          <div className="header-actions">
            {editMode ? (
              <>
                <button 
                  type="button" 
                  className="cancel-button"
                  onClick={() => {
                    setEditMode(false);
                    setPasswordError('');
                    setFormData(prev => ({
                      ...prev,
                      currentPassword: '',
                      newPassword: '',
                      confirmPassword: ''
                    }));
                  }}
                >
                  İptal
                </button>
                <button 
                  type="button" 
                  className="save-button"
                  onClick={handleSubmit}
                >
                  Kaydet
                </button>
              </>
            ) : (
              <button
                className="edit-button"
                onClick={() => setEditMode(true)}
              >
                Düzenle
              </button>
            )}
          </div>
        </div>

        {successMessage && (
          <div className="success-message">
            {successMessage}
          </div>
        )}

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="profile-form">
          <div className="form-group">
            <label>Kullanıcı Adı</label>
            <input
              type="text"
              name="username"
              value={formData.username}
              onChange={handleInputChange}
              disabled={!editMode}
            />
          </div>

          <div className="form-group">
            <label>E-posta</label>
            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleInputChange}
              disabled={!editMode}
            />
          </div>

          {editMode && (
            <div className="password-section">
              <h3>Şifre Değiştir</h3>
              {passwordError && (
                <div className="password-error-message">
                  {passwordError}
                </div>
              )}
              <div className="form-group">
                <label>Mevcut Şifre</label>
                <input
                  type="password"
                  name="currentPassword"
                  value={formData.currentPassword}
                  onChange={handleInputChange}
                  placeholder="Mevcut şifrenizi girin"
                />
              </div>

              <div className="form-group">
                <label>Yeni Şifre</label>
                <input
                  type="password"
                  name="newPassword"
                  value={formData.newPassword}
                  onChange={handleInputChange}
                  placeholder="En az 6 karakter"
                />
              </div>

              <div className="form-group">
                <label>Yeni Şifre (Tekrar)</label>
                <input
                  type="password"
                  name="confirmPassword"
                  value={formData.confirmPassword}
                  onChange={handleInputChange}
                  placeholder="Yeni şifrenizi tekrar girin"
                />
              </div>
            </div>
          )}
        </form>

        <div className="profile-stats">
          <h3>İstatistikler</h3>
          <div className="stats-grid">
            <div className="stat-item">
              <span className="stat-value">{user.totalBooks || 0}</span>
              <span className="stat-label">Toplam Kitap</span>
            </div>
            <div className="stat-item">
              <span className="stat-value">{user.totalSavedWords || 0}</span>
              <span className="stat-label">Kaydedilen Kelime</span>
            </div>
            <div className="stat-item">
              <span className="stat-value">{user.totalReadingTime || 0}</span>
              <span className="stat-label">Toplam Okuma Süresi (dk)</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Profile; 
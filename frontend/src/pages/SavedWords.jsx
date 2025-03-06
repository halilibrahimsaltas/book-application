import React, { useState, useEffect } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import api from '../api/axios';
import './SavedWords.css';

const SavedWords = () => {
  const [savedWords, setSavedWords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const location = useLocation();
  const navigate = useNavigate();
  const bookId = location.state?.bookId;

  useEffect(() => {
    const fetchSavedWords = async () => {
      try {
        setLoading(true);
        const endpoint = bookId 
          ? `/api/saved-words/book/${bookId}`
          : '/api/saved-words';
        const response = await api.get(endpoint);
        setSavedWords(response.data);
      } catch (error) {
        setError('Kelimeler yüklenirken bir hata oluştu');
        console.error('Kelimeler yüklenirken hata oluştu:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchSavedWords();
  }, [bookId]);

  const handleDelete = async (wordId) => {
    try {
      await api.delete(`/api/saved-words/${wordId}`);
      setSavedWords(savedWords.filter(word => word.id !== wordId));
    } catch (error) {
      console.error('Kelime silinirken hata:', error);
    }
  };

  const handleBookClick = (bookId) => {
    navigate(`/books/${bookId}/read`);
  };

  const capitalizeFirstLetter = (string) => {
    if (!string) return '';
    return string.charAt(0).toUpperCase() + string.slice(1).toLowerCase();
  };

  const formatDate = (dateString) => {
    const options = { 
      year: 'numeric', 
      month: 'long', 
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    };
    return new Date(dateString).toLocaleDateString('tr-TR', options);
  };

  const renderTableContent = () => {
    if (savedWords.length === 0) {
      return (
        <tr>
          <td colSpan="5" className="no-words-message">
            <p>Henüz kaydedilmiş kelime bulunmuyor.</p>
          </td>
        </tr>
      );
    }

    return savedWords.map((word) => (
      <tr key={word.id}>
        <td className="word-cell english" title={word.englishWord}>
          {capitalizeFirstLetter(word.englishWord)}
        </td>
        <td className="word-cell turkish" title={word.turkishWord}>
          {capitalizeFirstLetter(word.turkishWord)}
        </td>
        <td className="book-cell" title={word.bookTitle}>
          <button 
            className="book-link"
            onClick={() => handleBookClick(word.bookId)}
          >
            {word.bookTitle || 'Kitaba Git'}
          </button>
        </td>
        <td className="date-cell" title={formatDate(word.createdAt)}>
          {formatDate(word.createdAt)}
        </td>
        <td className="actions-cell">
          <button 
            className="delete-button"
            onClick={() => handleDelete(word.id)}
            title="Kelimeyi Sil"
          >
            <svg viewBox="0 0 24 24" width="16" height="16">
              <path fill="currentColor" d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
          </button>
        </td>
      </tr>
    ));
  };

  if (loading) {
    return (
      <div className="saved-words-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Kelimeler yükleniyor...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="saved-words-container">
        <div className="error-message">
          <p>{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="saved-words-container">
      <div className="saved-words-header">
        <h2>{bookId ? 'Kitaptaki Kaydedilen Kelimeler' : 'Tüm Kaydedilen Kelimeler'}</h2>
        <p className="word-count">Toplam: {savedWords.length} kelime</p>
      </div>

      <div className="saved-words-table-container">
        <table className="saved-words-table">
          <thead>
            <tr>
              <th>İngilizce</th>
              <th>Türkçe</th>
              <th>Kitap</th>
              <th>Tarih</th>
              <th>İşlemler</th>
            </tr>
          </thead>
          <tbody>
            {renderTableContent()}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default SavedWords; 
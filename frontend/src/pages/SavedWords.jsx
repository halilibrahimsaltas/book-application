import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import './SavedWords.css';

const SavedWords = () => {
  const [savedWords, setSavedWords] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { bookId } = useParams();
  const navigate = useNavigate();

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
            {savedWords.map((word) => (
              <tr key={word.id}>
                <td className="word-cell english">{word.englishWord}</td>
                <td className="word-cell turkish">{word.turkishWord}</td>
                <td className="book-cell">
                  <button 
                    className="book-link"
                    onClick={() => handleBookClick(word.bookId)}
                  >
                    {word.bookTitle || 'Kitaba Git'}
                  </button>
                </td>
                <td className="date-cell">
                  {new Date(word.createdAt).toLocaleDateString('tr-TR')}
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
            ))}
          </tbody>
        </table>
      </div>

      {savedWords.length === 0 && (
        <div className="no-words-message">
          <p>Henüz kaydedilmiş kelime bulunmuyor.</p>
        </div>
      )}
    </div>
  );
};

export default SavedWords; 
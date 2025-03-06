import PropTypes from 'prop-types';
import './BookCard.css';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import { useState } from 'react';

const DeleteIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
    <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
  </svg>
);

const ReadIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
    <path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/>
  </svg>
);

const WordIcon = () => (
  <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
    <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14z"/>
    <path d="M7 12h10v2H7zm0-4h10v2H7zm0 8h7v2H7z"/>
  </svg>
);

const BookCard = ({ book, onDelete }) => {
  const navigate = useNavigate();
  const [isDeleting, setIsDeleting] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [imageError, setImageError] = useState(false);

  const getImageUrl = (imagePath) => {
    if (!imagePath) return '/default-book-cover.jpg';
    
    // Eğer tam URL ise doğrudan kullan
    if (imagePath.startsWith('http')) {
      return imagePath;
    }
    
    // Eğer /api ile başlıyorsa, baseURL'i kullan
    if (imagePath.startsWith('/api')) {
      return `${import.meta.env.VITE_API_URL}${imagePath}`;
    }
    
    // Diğer durumlar için /api ekle
    return `${import.meta.env.VITE_API_URL}/api${imagePath.startsWith('/') ? '' : '/'}${imagePath}`;
  };

  const handleReadClick = () => {
    navigate(`/books/${book.id}/read`);
  };

  const handleSavedWordsClick = (e) => {
    e.preventDefault();
    e.stopPropagation();
    navigate(`/saved-words`, { state: { bookId: book.id } });
  };

  const handleDeleteClick = async (e) => {
    e.preventDefault();
    e.stopPropagation();
    
    if (window.confirm('Bu kitabı silmek istediğinizden emin misiniz?')) {
      try {
        setIsDeleting(true);
        await api.delete(`/api/books/${book.id}`);
        if (onDelete) {
          onDelete(book.id);
        }
      } catch (error) {
        console.error('Kitap silinirken hata oluştu:', error);
        alert('Kitap silinirken bir hata oluştu.');
      } finally {
        setIsDeleting(false);
      }
    }
  };

  const handleImageError = () => {
    setImageError(true);
    console.log('Resim yüklenemedi:', book.imagePath);
  };

  return (
    <div className="book-card">
      <div className="book-cover">
        <img 
          src={imageError ? '/default-book-cover.jpg' : getImageUrl(book.imagePath)}
          alt={book.title}
          onError={handleImageError}
          className={imageError ? 'fallback-image' : ''}
        />
        <div className="book-actions-overlay">
          <button 
            onClick={handleDeleteClick}
            className="action-button delete-button"
            disabled={isDeleting || isLoading}
            title="Kitabı Sil"
          >
            <DeleteIcon />
          </button>
        </div>
      </div>
      <div className="book-info">
        <h3 className="book-title">{book.title}</h3>
        <p className="book-author">{book.author}</p>
      </div>
      <div className="book-actions">
        <button 
          onClick={handleReadClick} 
          className="book-action-btn read-button"
          disabled={isLoading}
        >
          <ReadIcon />
          Oku
        </button>
        <button 
          onClick={handleSavedWordsClick} 
          className="book-action-btn saved-words-button"
          disabled={isLoading}
        >
          <WordIcon />
          Kelimeler
        </button>
      </div>
    </div>
  );
};

BookCard.propTypes = {
  book: PropTypes.shape({
    id: PropTypes.number.isRequired,
    title: PropTypes.string.isRequired,
    author: PropTypes.string.isRequired,
    imagePath: PropTypes.string,
    content: PropTypes.string
  }).isRequired,
  onDelete: PropTypes.func
};

export default BookCard;
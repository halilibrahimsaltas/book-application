import PropTypes from 'prop-types';
import './BookCard.css';
import { useNavigate } from 'react-router-dom';

const BookIcon = () => (
  <svg width="24" height="24" viewBox="0 0 24 24" className="book-icon">
    <g stroke="none" strokeWidth="1" fill="currentColor" fillRule="evenodd">
      <path d="M6,2 L18,2 C19.1045695,2 20,2.8954305 20,4 L20,20 C20,21.1045695 19.1045695,22 18,22 L6,22 C4.8954305,22 4,21.1045695 4,20 L4,4 C4,2.8954305 4.8954305,2 6,2 Z" />
    </g>
  </svg>
);

const WordIcon = () => (
  <svg width="16" height="16" viewBox="0 0 16 16" className="word-icon">
    <path fill="currentColor" d="M14 4v8H2V4h12m1-1H1v10h14V3z M3 8h10v1H3z"/>
  </svg>
);

const BookCard = ({ book }) => {
  const navigate = useNavigate();

  // Tarih formatlamak için yardımcı fonksiyon
  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleDateString('tr-TR', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  };

  const handleCardClick = () => {
    navigate(`/books/${book.id}/read`);
  };

  const handleSavedWordsClick = (e) => {
    e.stopPropagation();
    navigate(`/books/${book.id}/words`);
  };

  return (
    <div className="book-card" onClick={handleCardClick}>
      <div className="book-card-header">
        <div className="book-title-container">
          <BookIcon />
          <h3 className="book-title">{book.title}</h3>
        </div>
        <p className="book-author">Yazar: {book.author}</p>
      </div>
  
      <div className="book-card-footer">
        <button 
          className="book-card-button primary"
          onClick={handleSavedWordsClick}
        >
          <WordIcon />
          Kaydedilen Kelimeler
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
    description: PropTypes.string,
    filePath: PropTypes.string.isRequired,
    createdAt: PropTypes.string.isRequired
  }).isRequired
};

export default BookCard;
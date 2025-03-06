import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import BookCard from '../components/BookCard';
import './Dashboard.css';

const Dashboard = () => {
  const [books, setBooks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showAddBook, setShowAddBook] = useState(false);
  const [newBook, setNewBook] = useState({
    title: '',
    author: ''
  });
  const [selectedFile, setSelectedFile] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchBooks();
  }, []);

  const fetchBooks = async () => {
    try {
      setLoading(true);
      const response = await api.get('/api/books');
      setBooks(response.data);
    } catch (error) {
      setError('Kitaplar yüklenirken bir hata oluştu');
      console.error('Kitaplar yüklenirken hata:', error);
    } finally {
      setLoading(false);
    }
  };

  const handleAddBook = async (e) => {
    e.preventDefault();
    if (!selectedFile || !newBook.title || !newBook.author) {
      setError('Lütfen tüm alanları doldurun ve bir PDF dosyası seçin.');
      return;
    }

    const formData = new FormData();
    formData.append('file', selectedFile);
    formData.append('book', new Blob([JSON.stringify(newBook)], {
      type: 'application/json'
    }));

    try {
      await api.post('/api/books', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      setShowAddBook(false);
      setNewBook({ title: '', author: '' });
      setSelectedFile(null);
      fetchBooks();
    } catch (error) {
      setError('Kitap kaydedilirken bir hata oluştu.');
      console.error("Error saving book:", error);
    }
  };

  const filteredBooks = books.filter(book =>
    book.title.toLowerCase().includes(searchTerm.toLowerCase())
  );

  if (loading) {
    return (
      <div className="dashboard-container">
        <div className="loading-spinner">
          <div className="spinner"></div>
          <p>Kitaplar yükleniyor...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="dashboard-container">
        <div className="error-message">
          <p>{error}</p>
        </div>
      </div>
    );
  }

  return (
    <div className="dashboard-container">
      <div className="dashboard-header">
        <h2>Kitaplık</h2>
        <div className="search-container">
          <input
            type="text"
            placeholder="Kitap ara..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="search-input"
          />
        </div>
        <div className="header-buttons">
          <button onClick={() => setShowAddBook(!showAddBook)} className="add-book-button">
            {showAddBook ? 'İptal' : 'Yeni Kitap Ekle'}
          </button>
        </div>
      </div>

      {showAddBook && (
        <form onSubmit={handleAddBook} className="add-book-form">
          <input
            type="text"
            placeholder="Kitap Başlığı"
            value={newBook.title}
            onChange={(e) => setNewBook({...newBook, title: e.target.value})}
          />
          <input
            type="text"
            placeholder="Yazar"
            value={newBook.author}
            onChange={(e) => setNewBook({...newBook, author: e.target.value})}
          />
          <input
            type="file"
            accept=".pdf"
            onChange={(e) => setSelectedFile(e.target.files[0])}
          />
          <button type="submit">Kitabı Kaydet</button>
        </form>
      )}

      <div className="books-grid">
        {filteredBooks.length > 0 ? (
          filteredBooks.map((book) => (
            <BookCard
              key={book.id}
              book={book}
              onDelete={(deletedBookId) => {
                setBooks(books.filter(b => b.id !== deletedBookId));
              }}
            />
          ))
        ) : (
          <div className="no-books-message">
            {searchTerm ? 'Aranan kitap bulunamadı.' : 'Henüz kitap eklenmemiş.'}
          </div>
        )}
      </div>
    </div>
  );
};

export default Dashboard;

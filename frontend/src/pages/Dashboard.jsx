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
  const [selectedImage, setSelectedImage] = useState(null);
  const [imagePreview, setImagePreview] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const navigate = useNavigate();

  useEffect(() => {
    fetchBooks();
  }, []);

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      if (file.size > 5 * 1024 * 1024) { // 5MB limit
        setError('Resim boyutu 5MB\'dan küçük olmalıdır.');
        return;
      }
      if (!file.type.startsWith('image/')) {
        setError('Lütfen geçerli bir resim dosyası seçin.');
        return;
      }
      setSelectedImage(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setImagePreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

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
      setError('Lütfen kitap bilgilerini ve PDF dosyasını ekleyin.');
      return;
    }

    const formData = new FormData();
    formData.append('file', selectedFile);
    if (selectedImage) {
      formData.append('image', selectedImage);
    }
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
      setSelectedImage(null);
      setImagePreview(null);
      setError(null);
      fetchBooks();
    } catch (error) {
      setError('Kitap kaydedilirken bir hata oluştu.');
      console.error("Error saving book:", error);
    }
  };

  const resetForm = () => {
    setNewBook({ title: '', author: '' });
    setSelectedFile(null);
    setSelectedImage(null);
    setImagePreview(null);
    setError(null);
    setShowAddBook(false);
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
          <button onClick={() => setError(null)} className="error-close">
            Tamam
          </button>
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
          <button onClick={() => showAddBook ? resetForm() : setShowAddBook(true)} className="add-book-button">
            {showAddBook ? 'İptal' : 'Yeni Kitap Ekle'}
          </button>
        </div>
      </div>

      {showAddBook && (
        <form onSubmit={handleAddBook} className="add-book-form">
          <div className="form-group">
            <label htmlFor="title">Kitap Başlığı</label>
            <input
              id="title"
              type="text"
              placeholder="Kitap Başlığı"
              value={newBook.title}
              onChange={(e) => setNewBook({...newBook, title: e.target.value})}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="author">Yazar</label>
            <input
              id="author"
              type="text"
              placeholder="Yazar"
              value={newBook.author}
              onChange={(e) => setNewBook({...newBook, author: e.target.value})}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="pdf">PDF Dosyası</label>
            <input
              id="pdf"
              type="file"
              accept=".pdf"
              onChange={(e) => setSelectedFile(e.target.files[0])}
              required
            />
          </div>

          <div className="form-group">
            <label htmlFor="image">Kapak Resmi (İsteğe bağlı)</label>
            <input
              id="image"
              type="file"
              accept="image/*"
              onChange={handleImageChange}
            />
            {imagePreview && (
              <div className="image-preview">
                <img src={imagePreview} alt="Kapak önizleme" />
                <button 
                  type="button" 
                  onClick={() => {
                    setSelectedImage(null);
                    setImagePreview(null);
                  }}
                  className="remove-image"
                >
                  Resmi Kaldır
                </button>
              </div>
            )}
          </div>

          <div className="form-actions">
            <button type="button" onClick={resetForm} className="cancel-button">
              İptal
            </button>
            <button type="submit" className="submit-button">
              Kitabı Kaydet
            </button>
          </div>
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

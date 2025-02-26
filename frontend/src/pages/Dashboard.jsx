import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
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
  const navigate = useNavigate();

  useEffect(() => {
    fetchBooks();
  }, []);

  const fetchBooks = async () => {
    try {
      const response = await api.get("/books");
      setBooks(response.data);
      setLoading(false);
    } catch (error) {
      setError('Kitaplar yüklenirken bir hata oluştu.');
      setLoading(false);
      console.error("Error fetching books:", error);
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
      await api.post('/books', formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });
      setShowAddBook(false);
      setNewBook({ title: '', author: '' });
      setSelectedFile(null);
      fetchBooks(); // Kitap listesini yenile
    } catch (error) {
      setError('Kitap kaydedilirken bir hata oluştu.');
      console.error("Error saving book:", error);
    }
  };

  const handleSaveWord = async (bookId) => {
    try {
      // Kelime kaydetme işlemi için yeni endpoint'e yönlendirme yapılacak
      navigate(`/books/${bookId}/words`);
    } catch (error) {
      console.error("Error navigating to word save page:", error);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  if (loading) {
    return <div className="dashboard-loading">Yükleniyor...</div>;
  }

  if (error) {
    return <div className="dashboard-error">{error}</div>;
  }

  return (
    <div className="dashboard-container">
      <header className="dashboard-header">
        <h1>Kitaplık</h1>
        <button onClick={() => setShowAddBook(!showAddBook)} className="add-book-button">
          {showAddBook ? 'İptal' : 'Yeni Kitap Ekle'}
        </button>
        <button onClick={handleLogout} className="logout-button">
          Çıkış Yap
        </button>
      </header>

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
          <button type="submit">Kaydet</button>
        </form>
      )}

      <div className="books-grid">
        {books.map((book) => (
          <BookCard
            key={book.id}
            book={book}
            onSaveWord={handleSaveWord}
          />
        ))}
      </div>

      {books.length === 0 && !loading && (
        <div className="no-books-message">
          Henüz kitap eklenmemiş.
        </div>
      )}
    </div>
  );
};

export default Dashboard;

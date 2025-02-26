import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import './BookReader.css';

const BookReader = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [bookContent, setBookContent] = useState('');
    const [currentPage, setCurrentPage] = useState(1);
    const [totalPages, setTotalPages] = useState(1);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [book, setBook] = useState(null);

    useEffect(() => {
        fetchBookDetails();
        fetchBookContent(1);
    }, [id]);

    const fetchBookDetails = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await api.get(`/books/${id}`);
            setBook(response.data);
        } catch (error) {
            setError(error.response?.data?.error || 'Kitap bilgileri yüklenirken bir hata oluştu.');
            console.error('Error fetching book details:', error);
        } finally {
            setLoading(false);
        }
    };

    const fetchBookContent = async (page = null) => {
        try {
            setLoading(true);
            setError(null);
            const response = await api.get(`/books/${id}/content`, {
                params: page ? { page } : {}
            });
            
            setBookContent(response.data.content);
            setCurrentPage(response.data.currentPage);
            setTotalPages(response.data.totalPages);
            
            // İsteğe bağlı: Son okuma zamanını göster
            if (response.data.lastReadAt) {
                const lastRead = new Date(response.data.lastReadAt);
                console.log(`Son okuma: ${lastRead.toLocaleString()}`);
            }
        } catch (error) {
            setError(error.response?.data?.error || 'İçerik yüklenirken bir hata oluştu.');
            console.error('Error fetching book content:', error);
        } finally {
            setLoading(false);
        }
    };

    const handlePageChange = (newPage) => {
        if (newPage >= 1 && newPage <= totalPages) {
            setCurrentPage(newPage);
            fetchBookContent(newPage);
        }
    };

    if (loading) return <div className="loading">Yükleniyor...</div>;
    if (error) return <div className="error">{error}</div>;

    return (
        <div className="book-reader">
            <div className="book-reader-header">
                <button onClick={() => navigate('/dashboard')} className="back-button">
                    ← Geri
                </button>
                <h1>{book?.title}</h1>
                <p className="author">Yazar: {book?.author}</p>
            </div>

            <div className="book-content">
                {bookContent.split('\n').map((paragraph, index) => (
                    <p key={index}>{paragraph}</p>
                ))}
            </div>

            <div className="pagination">
                <button
                    onClick={() => handlePageChange(currentPage - 1)}
                    disabled={currentPage === 1}
                >
                    Önceki
                </button>
                <span>
                    Sayfa {currentPage} / {totalPages}
                </span>
                <button
                    onClick={() => handlePageChange(currentPage + 1)}
                    disabled={currentPage === totalPages}
                >
                    Sonraki
                </button>
            </div>
        </div>
    );
};

export default BookReader; 
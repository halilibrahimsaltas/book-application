import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import './BookReader.css';

const BookReader = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [bookContent, setBookContent] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [book, setBook] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);

    useEffect(() => {
        fetchBookDetails();
    }, [id]);

    const fetchBookDetails = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await api.get(`/books/${id}`);
            setBook(response.data);
            
            // Split content by "=== Page X ==="
            const formattedContent = response.data.content.split(/=== Page \d+ ===/).filter(text => text.trim() !== "");
            setBookContent(formattedContent);
        } catch (error) {
            setError(error.response?.data?.error || 'Kitap bilgileri yüklenirken bir hata oluştu.');
            console.error('Error fetching book details:', error);
        } finally {
            setLoading(false);
        }
    };

    const goToNextPage = () => {
        if (currentPage < bookContent.length - 1) {
            setCurrentPage(currentPage + 1);
        }
    };

    const goToPreviousPage = () => {
        if (currentPage > 0) {
            setCurrentPage(currentPage - 1);
        }
    };

    if (loading) {
        return <div className="book-reader-loading">Yükleniyor...</div>;
    }

    if (error) {
        return <div className="book-reader-error">{error}</div>;
    }

    return (
        <div className="book-reader-container">
            <div className="book-reader-header">
                <div className="header-content">
                    <h1>{book?.title}</h1>
                    <p className="author">{book?.author}</p>
                </div>
                <button className="back-button" onClick={() => navigate(-1)}>← Geri</button>
            </div>

            <div className="book-content">
                {bookContent[currentPage]?.split('\n').map((paragraph, index) => (
                    <p key={index}>{paragraph.trim()}</p>
                ))}
            </div>

            <div className="page-controls">
                <button onClick={goToPreviousPage} disabled={currentPage === 0}>Önceki Sayfa</button>
                <span>Sayfa {currentPage + 1} / {bookContent.length}</span>
                <button onClick={goToNextPage} disabled={currentPage === bookContent.length - 1}>Sonraki Sayfa</button>
            </div>
        </div>
    );
};

export default BookReader;

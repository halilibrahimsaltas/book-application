import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import api from '../api/axios';
import './BookReader.css';
import WordCard from '../components/WordCard';

const BookReader = () => {
    const { id } = useParams();
    const navigate = useNavigate();
    const [bookContent, setBookContent] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState(null);
    const [book, setBook] = useState(null);
    const [currentPage, setCurrentPage] = useState(0);
    const [selectedWord, setSelectedWord] = useState(null);
    const [wordCardPosition, setWordCardPosition] = useState({ x: 0, y: 0 });

    useEffect(() => {
        // Sayfa yüklendiğinde body'ye özel class ekle
        document.body.classList.add('book-reader-page');
        
        fetchBookDetails();

        // Component unmount olduğunda class'ı kaldır
        return () => {
            document.body.classList.remove('book-reader-page');
        };
    }, [id]);

    // Sayfa değişikliğini takip et
    useEffect(() => {
        const contentElement = document.querySelector('.book-content');
        if (contentElement) {
            contentElement.scrollTop = 0;
        }
    }, [currentPage]);

    const fetchBookDetails = async () => {
        try {
            setLoading(true);
            setError(null);
            const response = await api.get(`/api/books/${id}`);
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
            // Sayfanın başına kaydır
            document.querySelector('.book-content').scrollTop = 0;
        }
    };

    const goToPreviousPage = () => {
        if (currentPage > 0) {
            setCurrentPage(currentPage - 1);
            // Sayfanın başına kaydır
            document.querySelector('.book-content').scrollTop = 0;
        }
    };

    const handleWordClick = (event, word) => {
        const rect = event.target.getBoundingClientRect();
        const viewportWidth = window.innerWidth;
        const viewportHeight = window.innerHeight;
        
        // Kartın genişliği ve yüksekliği (tahmini değerler)
        const cardWidth = 320;
        const cardHeight = 400;
        
        let x = rect.left;
        let y = rect.bottom + window.scrollY;
        
        // Sağ tarafa taşma kontrolü
        if (x + cardWidth > viewportWidth) {
            x = viewportWidth - cardWidth - 20;
        }
        
        // Alt tarafa taşma kontrolü
        if (y + cardHeight > viewportHeight + window.scrollY) {
            y = rect.top + window.scrollY - cardHeight - 10;
        }
        
        setWordCardPosition({ x, y });
        setSelectedWord(word);
    };

    const closeWordCard = () => {
        setSelectedWord(null);
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
                {bookContent[currentPage]?.split('\n').map((paragraph, pIndex) => (
                    <p key={pIndex}>
                        {paragraph.trim().split(/\s+/).map((word, wIndex) => (
                            <span
                                key={`${pIndex}-${wIndex}`}
                                className="clickable-word"
                                onClick={(e) => handleWordClick(e, word)}
                            >
                                {word}{' '}
                            </span>
                        ))}
                    </p>
                ))}
            </div>

            {selectedWord && (
                <WordCard
                    word={selectedWord}
                    onClose={closeWordCard}
                    position={wordCardPosition}
                    bookId={id}
                />
            )}

            <div className="page-controls">
                <button onClick={goToPreviousPage} disabled={currentPage === 0}>Önceki Sayfa</button>
                <span>Sayfa {currentPage + 1} / {bookContent.length}</span>
                <button onClick={goToNextPage} disabled={currentPage === bookContent.length - 1}>Sonraki Sayfa</button>
            </div>
        </div>
    );
};

export default BookReader;

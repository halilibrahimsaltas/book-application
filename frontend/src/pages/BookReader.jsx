import { useState, useEffect, useCallback, useRef } from 'react';
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
    const [isScrollingDown, setIsScrollingDown] = useState(false);
    const lastScrollY = useRef(0);
    const scrollTimeout = useRef(null);

    const handleScroll = useCallback(() => {
        const currentScrollY = window.scrollY;
        const navbar = document.querySelector('.navbar');
        
        if (!navbar) return;

        // En üstte veya en altta ise işlem yapma
        if (currentScrollY <= 0 || currentScrollY + window.innerHeight >= document.documentElement.scrollHeight) {
            navbar.classList.remove('hidden');
            return;
        }

        // Scroll yönünü belirle ve minimum mesafe kontrolü
        if (Math.abs(currentScrollY - lastScrollY.current) > 5) {
            if (currentScrollY > lastScrollY.current && !isScrollingDown) {
                navbar.classList.add('hidden');
                setIsScrollingDown(true);
            } else if (currentScrollY < lastScrollY.current && isScrollingDown) {
                navbar.classList.remove('hidden');
                setIsScrollingDown(false);
            }
            lastScrollY.current = currentScrollY;
        }
    }, [isScrollingDown]);

    useEffect(() => {
        document.body.classList.add('book-reader-page');
        fetchBookDetails();

        const debouncedScroll = () => {
            if (scrollTimeout.current) {
                window.cancelAnimationFrame(scrollTimeout.current);
            }
            scrollTimeout.current = window.requestAnimationFrame(() => {
                handleScroll();
            });
        };

        window.addEventListener('scroll', debouncedScroll, { passive: true });

        return () => {
            document.body.classList.remove('book-reader-page');
            window.removeEventListener('scroll', debouncedScroll);
            if (scrollTimeout.current) {
                window.cancelAnimationFrame(scrollTimeout.current);
            }
        };
    }, [id, handleScroll]);

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
            const [bookResponse, progressResponse] = await Promise.all([
                api.get(`/api/books/${id}`),
                api.get(`/api/books/${id}/progress`)
            ]);
            
            setBook(bookResponse.data);
            
            // İçeriği düzgün paragraflar halinde böl
            const content = bookResponse.data.content;
            const formattedContent = content
                .split(/=== Page \d+ ===/)
                .filter(text => text.trim() !== "")
                .map(pageContent => {
                    // Birden fazla boş satırı tek boş satıra indir
                    return pageContent.replace(/\n\s*\n/g, '\n\n')
                        // Satır sonlarındaki tire işaretlerini kaldır ve kelimeleri birleştir
                        .replace(/(\w+)-\n(\w+)/g, '$1$2')
                        // Satır sonlarını boşluğa çevir (cümlelerin devamı için)
                        .replace(/(?<!\n)\n(?!\n)/g, ' ')
                        .trim();
                });
            
            setBookContent(formattedContent);
            
            // Eğer okuma ilerlemesi varsa, son kaldığı sayfadan devam et
            if (progressResponse.data) {
                setCurrentPage(progressResponse.data.currentPage);
            }
        } catch (error) {
            setError(error.response?.data?.error || 'Kitap bilgileri yüklenirken bir hata oluştu.');
            console.error('Error fetching book details:', error);
        } finally {
            setLoading(false);
        }
    };

    // Sayfa değiştiğinde ilerlemeyi kaydet
    const saveProgress = async (pageNumber) => {
        try {
            await api.post(`/api/books/${id}/progress`, {
                currentPage: pageNumber,
                totalPages: bookContent.length
            });
        } catch (error) {
            console.error('Error saving progress:', error);
        }
    };

    const goToNextPage = () => {
        if (currentPage < bookContent.length - 1) {
            const newPage = currentPage + 1;
            setCurrentPage(newPage);
            saveProgress(newPage);
            // Sayfanın başına kaydır
            document.querySelector('.book-content').scrollTop = 0;
        }
    };

    const goToPreviousPage = () => {
        if (currentPage > 0) {
            const newPage = currentPage - 1;
            setCurrentPage(newPage);
            saveProgress(newPage);
            // Sayfanın başına kaydır
            document.querySelector('.book-content').scrollTop = 0;
        }
    };

    const handleWordClick = (event, word) => {
        const rect = event.target.getBoundingClientRect();
        const contentContainer = document.querySelector('.book-content');
        const contentRect = contentContainer.getBoundingClientRect();
        
        // Kartın genişliği ve yüksekliği
        const cardWidth = 260;
        const cardHeight = Math.min(400, window.innerHeight - 300);
        
        let x = rect.left - contentRect.left;
        let y = rect.top - contentRect.top + contentContainer.scrollTop;
        
        // Sağ tarafa taşma kontrolü
        if (x + cardWidth > contentRect.width) {
            x = contentRect.width - cardWidth - 20;
        }
        
        // Alt tarafa taşma kontrolü - sayfanın ortasından sonra yukarı doğru açılsın
        const middleOfPage = contentRect.height / 2;
        if (y > middleOfPage) {
            y = y - cardHeight - 30; // Kelimenin üstünde göster
        } else {
            y = y + 30; // Kelimenin altında göster
        }
        
        // Minimum ve maximum y değerlerini kontrol et
        const maxY = contentRect.height - cardHeight - 20;
        y = Math.max(20, Math.min(y, maxY));
        
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
                {bookContent[currentPage]?.split('\n\n').map((paragraph, pIndex) => (
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

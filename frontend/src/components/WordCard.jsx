import React, { useState, useEffect, useCallback } from 'react';
import api from '../api/axios';
import './WordCard.css';

const WordCard = ({ word, onClose, position, bookId }) => {
    const [isSaving, setIsSaving] = useState(false);
    const [saveError, setSaveError] = useState(null);
    const [translations, setTranslations] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [wordExists, setWordExists] = useState(false);
    const [showCard, setShowCard] = useState(false);

    const cleanWord = useCallback((word) => {
        if (!word) return '';

        return word
            // Özel tire işaretlerini standart tire ile değiştir
            .replace(/[—–]/g, '-')
            // Apostrof işaretlerini standartlaştır
            .replace(/['′']/g, "'")
            // Noktalama işaretlerini ve özel karakterleri kaldır
            .replace(/[.,!?;:"()\[\]{}]/g, '')
            // Birleşik kelimeleri ayır (örn: didn't -> didn t)
            .replace(/([a-z])'([a-z])/gi, '$1 $2')
            // Tire ile birleştirilmiş kelimeleri ayır
            .replace(/([a-z])-([a-z])/gi, '$1 $2')
            // Fazla boşlukları temizle
            .replace(/\s+/g, ' ')
            .trim()
            .toLowerCase();
    }, []);

    const handleWordClick = async (word) => {
        // Eğer kelime birleşik ise (örn: "didn't", "she's") her bir parçayı ayrı ayrı ara
        const parts = word.split(/[-'\s]+/);
        const cleanedParts = parts.map(part => cleanWord(part)).filter(Boolean);
        
        // Her bir parça için ayrı ayrı çeviri al
        const allTranslations = [];
        for (const part of cleanedParts) {
            try {
                const response = await api.get(`/api/translates/en/${encodeURIComponent(part)}`);
                if (response.data && Array.isArray(response.data)) {
                    allTranslations.push(...response.data);
                }
            } catch (error) {
                console.error(`Çeviri alınamadı: ${part}`, error);
            }
        }
        
        setTranslations(allTranslations);
        setWordExists(allTranslations.length > 0);
    };

    const fetchTranslations = useCallback(async () => {
        try {
            setIsLoading(true);
            setSaveError(null);
            const cleanedWord = cleanWord(word);
            
            if (!cleanedWord) {
                throw new Error('Geçerli bir kelime girilmedi');
            }

            // Kelimeyi parçalara ayır ve her parça için çeviri al
            const parts = cleanedWord.split(/[-'\s]+/);
            const uniqueParts = [...new Set(parts)].filter(Boolean);

            if (uniqueParts.length > 1) {
                // Birden fazla kelime varsa (örn: "didn't" -> "did" "not")
                const allTranslations = [];
                for (const part of uniqueParts) {
                    try {
                        const response = await api.get(`/api/translates/en/${encodeURIComponent(part)}`);
                        if (response.data && Array.isArray(response.data)) {
                            allTranslations.push(...response.data);
                        }
                    } catch (error) {
                        console.error(`Çeviri alınamadı: ${part}`, error);
                    }
                }
                setTranslations(allTranslations);
                setWordExists(allTranslations.length > 0);
            } else {
                // Tek kelime için normal çeviri
                const response = await api.get(`/api/translates/en/${encodeURIComponent(cleanedWord)}`);
                const translationResults = response.data && Array.isArray(response.data) ? response.data : [];
                setTranslations(translationResults);
                setWordExists(translationResults.length > 0);
            }

        } catch (error) {
            console.error('Kelime çevirisi alınırken hata:', error);
            setTranslations([]);
            setWordExists(false);
            setSaveError(
                error.response?.data?.message || 
                error.message || 
                'Kelime çevirisi alınamadı'
            );
        } finally {
            setIsLoading(false);
        }
    }, [word, cleanWord]);

    useEffect(() => {
        setShowCard(true);
        if (word) {
            fetchTranslations();
        }
        
        return () => {
            setShowCard(false);
            setTranslations([]);
            setSaveError(null);
        };
    }, [word, fetchTranslations]);

    const handleSaveWord = async (translation) => {
        if (!wordExists) {
            setSaveError('Bu kelime veritabanında bulunmadığı için kaydedilemez.');
            showNotification('Bu kelime veritabanında bulunmadığı için kaydedilemez.', 'error');
            return;
        }

        try {
            setIsSaving(true);
            setSaveError(null);
            
            const token = localStorage.getItem('token');
            if (!token) {
                const errorMsg = 'Kelime kaydetmek için giriş yapmalısınız';
                setSaveError(errorMsg);
                showNotification(errorMsg, 'error');
                return;
            }

            // Token'ı header'a ekle
            api.defaults.headers.common['Authorization'] = `Bearer ${token}`;
            
            const saveWordRequest = {
                englishWord: translation.enWord,
                turkishWord: translation.trWord,
                bookId: bookId
            };

            console.debug('Kelime kaydetme isteği:', saveWordRequest);
            
            const response = await api.post('/api/saved-words', saveWordRequest);
            
            if (response.status === 200) {
                showNotification('Kelime başarıyla kaydedildi!', 'success');
            } else {
                throw new Error('Beklenmeyen bir yanıt alındı');
            }
        } catch (error) {
            console.error('Kelime kaydedilirken hata oluştu:', error);
            let errorMessage;
            
            if (error.response) {
                // Backend'den gelen hata
                errorMessage = error.response.data || 'Sunucu hatası oluştu';
                if (error.response.status === 401) {
                    errorMessage = 'Oturum süreniz dolmuş. Lütfen tekrar giriş yapın.';
                    // Token'ı temizle ve kullanıcıyı login sayfasına yönlendir
                    localStorage.removeItem('token');
                    window.location.href = '/login';
                }
            } else if (error.request) {
                // İstek yapıldı ama cevap alınamadı
                errorMessage = 'Sunucuya ulaşılamıyor';
            } else {
                // İstek oluşturulurken hata oluştu
                errorMessage = error.message || 'Kelime kaydedilirken bir hata oluştu';
            }

            setSaveError(errorMessage);
            showNotification(errorMessage, 'error');
        } finally {
            setIsSaving(false);
        }
    };

    const showNotification = (message, type = 'success') => {
        const notification = document.createElement('div');
        notification.className = `save-notification ${type}`;
        notification.textContent = message;
        document.body.appendChild(notification);
        
        setTimeout(() => {
            notification.classList.add('fade-out');
            setTimeout(() => notification.remove(), 300);
        }, 2700);
    };

    const capitalizeFirstLetter = (str) => {
        return str.split(' ').map(word => 
            word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()
        ).join(' ');
    };

    if (!word) return null;

    return (
        <div 
            className={`word-card ${showCard ? 'show' : ''}`}
            style={{
                position: 'fixed',
                left: Math.min(Math.max(position.x, 10), window.innerWidth - 220),
                top: Math.min(Math.max(position.y, 10), window.innerHeight - 300),
                zIndex: 1000
            }}
        >
            <button className="close-button" onClick={onClose}>×</button>
            <div className="word-header">
                <h3>{cleanWord(word)}</h3>
            </div>
            <div className="word-details">
                {isLoading ? (
                    <div className="loading-spinner">
                        <div className="spinner"></div>
                    </div>
                ) : translations.length > 0 ? (
                    <div className={`translations-list ${translations.length > 3 ? 'has-more' : ''}`}>
                        {translations.map((translation, index) => (
                            <div key={translation.id || index} className="translation-item">
                                <button 
                                    className={`save-button ${isSaving ? 'saving' : ''}`}
                                    onClick={() => handleSaveWord(translation)}
                                    disabled={isSaving}
                                    title="Kelimeyi Kaydet"
                                >
                                    {isSaving ? '•' : '+'}
                                </button>
                                <div className="translation-header">
                                    <span className="word-type">{translation.type || 'Genel'}</span>
                                    <span className="category-tag">{translation.category || 'Genel'}</span>
                                </div>
                                <p className="turkish-translation">{capitalizeFirstLetter(translation.trWord)}</p>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="no-translation">
                        <p>Çeviri bulunamadı</p>
                    </div>
                )}
            </div>
            {saveError && (
                <div className="error-message">
                    <i className="error-icon">⚠️</i>
                    <p>{saveError}</p>
                </div>
            )}
        </div>
    );
};

export default WordCard; 
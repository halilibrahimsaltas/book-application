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
        return word
            ?.replace(/[—–]/g, '-')
            ?.replace(/[.,!?;:'"()\[\]{}]/g, '')
            ?.replace(/\s+/g, ' ')
            ?.trim()
            ?.toLowerCase() || '';
    }, []);

    const fetchTranslations = useCallback(async () => {
        try {
            setIsLoading(true);
            setSaveError(null);
            const cleanedWord = cleanWord(word);
            
            if (!cleanedWord) {
                throw new Error('Geçerli bir kelime girilmedi');
            }

            const response = await api.get(`/api/translates/en/${encodeURIComponent(cleanedWord)}`);
            const translationResults = response.data && Array.isArray(response.data) ? response.data : [];
            
            setTranslations(translationResults);
            setWordExists(translationResults.length > 0);

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
            return;
        }

        try {
            setIsSaving(true);
            setSaveError(null);
            
            const token = localStorage.getItem('token');
            if (!token) {
                throw new Error('Kelime kaydetmek için giriş yapmalısınız');
            }
            
            await api.post('/api/saved-words', {
                englishWord: translation.enWord,
                turkishWord: translation.trWord,
                type: translation.type || 'Belirtilmemiş',
                category: translation.category || 'Genel',
                bookId: bookId
            });
            
            showNotification('Kelime başarıyla kaydedildi!', 'success');
        } catch (error) {
            console.error('Kelime kaydedilirken hata oluştu:', error);
            const errorMessage = error.response?.data?.message || 
                               error.message || 
                               'Kelime kaydedilirken bir hata oluştu.';
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

    if (!word) return null;

    return (
        <div 
            className={`word-card ${showCard ? 'show' : ''}`}
            style={{
                position: 'fixed',
                left: Math.min(Math.max(position.x, 10), window.innerWidth - 260),
                top: Math.min(Math.max(position.y, 10), window.innerHeight - 350),
                zIndex: 1000
            }}
        >
            <button className="close-button" onClick={onClose}>×</button>
            <h3>{word}</h3>
            <div className="word-details">
                {isLoading ? (
                    <div className="loading-spinner">
                        <div className="spinner"></div>
                        <p>Çeviri yükleniyor...</p>
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
                                    <span className="word-type">{translation.type || 'Belirtilmemiş'}</span>
                                    <span className="category-tag">{translation.category || 'Genel'}</span>
                                </div>
                                <p className="turkish-translation">{translation.trWord}</p>
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="no-translation">
                        <p>Bu kelime için çeviri bulunamadı.</p>
                        <small>Yeni çeviri eklemek için yönetici ile iletişime geçin.</small>
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
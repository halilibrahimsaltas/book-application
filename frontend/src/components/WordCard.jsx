import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import './WordCard.css';

const WordCard = ({ word, onClose, position, bookId }) => {
    const [isSaving, setIsSaving] = useState(false);
    const [saveError, setSaveError] = useState(null);
    const [translations, setTranslations] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [wordExists, setWordExists] = useState(false);

    const cleanWord = (word) => {
        return word
            .replace(/[—–]/g, '-') // Uzun tireleri normal tire ile değiştir
            .replace(/[^\w\s-]/g, '') // Alfanumerik olmayan karakterleri kaldır
            .trim(); // Başındaki ve sonundaki boşlukları temizle
    };

    useEffect(() => {
        fetchTranslations();
    }, [word]);

    const fetchTranslations = async () => {
        try {
            setIsLoading(true);
            const cleanedWord = cleanWord(word);
            const response = await api.get(`/api/translates/en/${encodeURIComponent(cleanedWord)}`);
            if (response.data && response.data.length > 0) {
                setTranslations(response.data);
                setWordExists(true);
            } else {
                setTranslations([]);
                setWordExists(false);
            }
        } catch (error) {
            console.error('Kelime çevirisi alınırken hata:', error);
            setTranslations([]);
            setWordExists(false);
        } finally {
            setIsLoading(false);
        }
    };

    const handleSaveWord = async (translation) => {
        if (!wordExists) {
            setSaveError('Bu kelime veritabanında bulunmadığı için kaydedilemez.');
            return;
        }

        try {
            setIsSaving(true);
            setSaveError(null);
            
            await api.post('/api/saved-words', {
                word: translation.enWord,
                translation: translation.trWord,
                type: translation.type,
                category: translation.category,
                bookId: bookId
            });
            
            alert('Kelime başarıyla kaydedildi!');
        } catch (error) {
            console.error('Kelime kaydedilirken hata oluştu:', error);
            setSaveError('Kelime kaydedilirken bir hata oluştu.');
        } finally {
            setIsSaving(false);
        }
    };

    return (
        <div 
            className="word-card"
            style={{
                position: 'absolute',
                left: `${position.x}px`,
                top: `${position.y}px`
            }}
        >
            <button className="close-button" onClick={onClose}>×</button>
            <h3>{word}</h3>
            <div className="word-details">
                {isLoading ? (
                    <p>Yükleniyor...</p>
                ) : translations.length > 0 ? (
                    <div className="translations-list">
                        {translations.map((translation, index) => (
                            <div key={index} className="translation-item">
                                <p><strong>Türkçe:</strong> {translation.trWord}</p>
                                <p><strong>Tür:</strong> {translation.type}</p>
                                <p><strong>Kategori:</strong> {translation.category}</p>
                                <button 
                                    className="save-button" 
                                    onClick={() => handleSaveWord(translation)}
                                    disabled={isSaving}
                                >
                                    {isSaving ? 'Kaydediliyor...' : 'Bu Anlamı Kaydet'}
                                </button>
                            </div>
                        ))}
                    </div>
                ) : (
                    <p className="no-translation">Bu kelime için çeviri bulunamadı.</p>
                )}
            </div>
            {saveError && <p className="error-message">{saveError}</p>}
        </div>
    );
};

export default WordCard; 
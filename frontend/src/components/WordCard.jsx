import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import './WordCard.css';

const WordCard = ({ word, onClose, position, bookId }) => {
    const [isSaving, setIsSaving] = useState(false);
    const [saveError, setSaveError] = useState(null);
    const [wordData, setWordData] = useState({
        word: '',
        type: '',
        category: '',
        tr: ''
    });
    const [isLoading, setIsLoading] = useState(true);
    const [wordExists, setWordExists] = useState(false);

    useEffect(() => {
        fetchWordDetails();
    }, [word]);

    const fetchWordDetails = async () => {
        try {
            setIsLoading(true);
            const response = await api.get(`/api/words/search/${encodeURIComponent(word)}`);
            if (response.data) {
                setWordData({
                    word: response.data.word || word,
                    type: response.data.type || 'Tür bilgisi yok',
                    category: response.data.category || 'Kategori bilgisi yok',
                    tr: response.data.tr || 'Çeviri bulunamadı'
                });
                setWordExists(true);
            }
        } catch (error) {
            console.log('Kelime bulunamadı, varsayılan değerler kullanılıyor');
            setWordData({
                word: word,
                type: 'Tür bilgisi yok',
                category: 'Kategori bilgisi yok',
                tr: 'Çeviri bulunamadı'
            });
            setWordExists(false);
        } finally {
            setIsLoading(false);
        }
    };

    const handleSaveWord = async () => {
        if (!wordExists) {
            setSaveError('Bu kelime veritabanında bulunmadığı için kaydedilemez.');
            return;
        }

        try {
            setIsSaving(true);
            setSaveError(null);
            
            await api.post('/api/saved-words', {
                wordId: wordData.id,
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
            <h3>{wordData.word}</h3>
            <div className="word-details">
                {isLoading ? (
                    <p>Yükleniyor...</p>
                ) : (
                    <>
                        <p><strong>Türkçe:</strong> {wordData.tr}</p>
                        <p><strong>Tür:</strong> {wordData.type}</p>
                        <p><strong>Kategori:</strong> {wordData.category}</p>
                    </>
                )}
            </div>
            <div className="word-card-actions">
                <button 
                    className="save-button" 
                    onClick={handleSaveWord}
                    disabled={isSaving || !wordExists}
                >
                    {isSaving ? 'Kaydediliyor...' : (wordExists ? 'Kelimeyi Kaydet' : 'Kelime Bulunamadı')}
                </button>
                {saveError && <p className="error-message">{saveError}</p>}
                {!wordExists && !saveError && (
                    <p className="info-message">Bu kelime veritabanında bulunmamaktadır.</p>
                )}
            </div>
        </div>
    );
};

export default WordCard; 
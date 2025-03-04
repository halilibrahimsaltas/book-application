import React, { useState, useEffect } from 'react';
import api from '../api/axios';
import './WordCard.css';

const WordCard = ({ word, onClose, position, bookId }) => {
    const [isSaving, setIsSaving] = useState(false);
    const [saveError, setSaveError] = useState(null);
    const [wordData, setWordData] = useState({
        meaning: '',
        pronunciation: ''
    });
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        fetchWordDetails();
    }, [word]);

    const fetchWordDetails = async () => {
        try {
            setIsLoading(true);
            const response = await api.get(`/api/words/text/${encodeURIComponent(word)}`);
            if (response.data) {
                setWordData({
                    meaning: response.data.meaning || 'Anlam bulunamadı',
                    pronunciation: response.data.pronunciation || 'Telaffuz bulunamadı'
                });
            }
        } catch (error) {
            console.log('Kelime bulunamadı, varsayılan değerler kullanılıyor');
            setWordData({
                meaning: 'Anlam bulunamadı',
                pronunciation: 'Telaffuz bulunamadı'
            });
        } finally {
            setIsLoading(false);
        }
    };

    const handleSaveWord = async () => {
        try {
            setIsSaving(true);
            setSaveError(null);
            
            await api.post('/saved-words', {
                word: word,
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
                ) : (
                    <>
                        <p><strong>Anlam:</strong> {wordData.meaning}</p>
                        <p><strong>Telaffuz:</strong> {wordData.pronunciation}</p>
                    </>
                )}
            </div>
            <div className="word-card-actions">
                <button 
                    className="save-button" 
                    onClick={handleSaveWord}
                    disabled={isSaving}
                >
                    {isSaving ? 'Kaydediliyor...' : 'Kelimeyi Kaydet'}
                </button>
                {saveError && <p className="error-message">{saveError}</p>}
            </div>
        </div>
    );
};

export default WordCard; 
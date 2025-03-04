import React from 'react';
import './WordCard.css';

const WordCard = ({ word, onClose, position }) => {
    // Örnek veri (backend bağlantısı yapılana kadar)
    const wordData = {
        meaning: 'Örnek anlam',
        pronunciation: '/ˈeksampıl/'
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
                <p><strong>Anlam:</strong> {wordData.meaning}</p>
                <p><strong>Telaffuz:</strong> {wordData.pronunciation}</p>
            </div>
        </div>
    );
};

export default WordCard; 
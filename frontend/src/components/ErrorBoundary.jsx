import React from 'react';
import { useRouteError, Link } from 'react-router-dom';
import './ErrorBoundary.css';

const ErrorBoundary = () => {
  const error = useRouteError();

  return (
    <div className="error-boundary">
      <div className="error-content">
        <h1>Oops! Bir Hata Oluştu</h1>
        <p className="error-message">
          {error.status === 404
            ? 'Aradığınız sayfa bulunamadı.'
            : 'Beklenmeyen bir hata oluştu.'}
        </p>
        <p className="error-details">
          {error.statusText || error.message}
        </p>
        <div className="error-actions">
          <Link to="/dashboard" className="back-button">
            Ana Sayfaya Dön
          </Link>
        </div>
      </div>
    </div>
  );
};

export default ErrorBoundary; 
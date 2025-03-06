import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import './Navbar.css';

const BookIcon = () => (
  <svg viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
    <path d="M21 4H3a1 1 0 0 0-1 1v14a1 1 0 0 0 1 1h18a1 1 0 0 0 1-1V5a1 1 0 0 0-1-1zm-1 13H4V6h16v11z"/>
    <path d="M8 8h8v2H8zm0 4h8v2H8z"/>
  </svg>
);

const WordIcon = () => (
  <svg viewBox="0 0 24 24" fill="currentColor" width="24" height="24">
    <path d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm0 16H5V5h14v14z"/>
    <path d="M7 12h10v2H7zm0-4h10v2H7zm0 8h7v2H7z"/>
  </svg>
);

const Navbar = () => {
  const location = useLocation();
  const isAuthenticated = localStorage.getItem('token');
  const isLoginPage = location.pathname === '/login' || location.pathname === '/signup';

  if (isLoginPage) return null;

  const isActive = (path) => {
    if (path === '/') {
      return location.pathname === '/';
    }
    return location.pathname.startsWith(path);
  };

  return (
    <nav className="navbar">
      <div className="nav-container">
        <div className="nav-left">
          <Link to={isAuthenticated ? "/dashboard" : "/"} className="nav-brand">
            <BookIcon />
            <span>Kitaplık</span>
          </Link>
        </div>
        {isAuthenticated && (
          <div className="nav-right">
            <Link 
              to="/dashboard" 
              className={`nav-link ${isActive('/dashboard') ? 'active' : ''}`}
            >
              <BookIcon />
              <span>Kitaplarım</span>
            </Link>
            <Link 
              to="/saved-words" 
              className={`nav-link ${isActive('/saved-words') ? 'active' : ''}`}
            >
              <WordIcon />
              <span>Kelimelerim</span>
            </Link>
          </div>
        )}
      </div>
    </nav>
  );
};

export default Navbar; 
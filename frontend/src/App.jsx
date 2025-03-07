import { Outlet, useLocation } from 'react-router-dom';
import Navbar from './components/Navbar';
import './App.css';

function App() {
  const location = useLocation();
  const isAuthPage = location.pathname === '/login' || location.pathname === '/signup';
  const isBookReaderPage = location.pathname.match(/^\/books\/\d+\/read$/);
  const isHomePage = location.pathname === '/';

  // Eğer ana sayfadaysa veya auth sayfalarındaysa navbar'ı gösterme
  const shouldShowNavbar = !isAuthPage && !isBookReaderPage && !isHomePage;

  return (
    <div className="app">
      {shouldShowNavbar && <Navbar />}
      <main className={`main-content ${isAuthPage ? 'auth-page' : ''} ${isBookReaderPage ? 'reader-page' : ''}`}>
        <Outlet />
      </main>
    </div>
  );
}

export default App;

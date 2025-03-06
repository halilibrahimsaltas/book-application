import { Outlet, useLocation } from 'react-router-dom';
import Navbar from './components/Navbar';
import './App.css';

function App() {
  const location = useLocation();
  const isAuthPage = location.pathname === '/login' || location.pathname === '/signup';
  const isBookReaderPage = location.pathname.match(/^\/books\/\d+\/read$/);

  return (
    <div className="app">
      {!isAuthPage && !isBookReaderPage && <Navbar />}
      <main className={`main-content ${isAuthPage ? 'auth-page' : ''} ${isBookReaderPage ? 'reader-page' : ''}`}>
        <Outlet />
      </main>
    </div>
  );
}

export default App;

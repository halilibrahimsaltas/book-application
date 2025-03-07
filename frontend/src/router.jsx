import { createBrowserRouter } from 'react-router-dom';
import App from './App';
import HomePage from './pages/HomePage';
import LoginPage from './pages/LoginPage';
import SignUpPage from './pages/SignUpPage';
import Dashboard from './pages/Dashboard';
import BookReader from './pages/BookReader';
import SavedWords from './pages/SavedWords';
import Profile from './pages/Profile';

const router = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    children: [
      {
        path: '/',
        element: <HomePage />
      },
      {
        path: '/login',
        element: <LoginPage />
      },
      {
        path: '/signup',
        element: <SignUpPage />
      },
      {
        path: '/dashboard',
        element: <Dashboard />
      },
      {
        path: '/profile',
        element: <Profile />
      },
      {
        path: '/books/:id/read',
        element: <BookReader />
      },
      {
        path: '/saved-words',
        element: <SavedWords />
      },
      {
        path: '/books/:bookId/words',
        element: <SavedWords />
      }
    ]
  }
]);

export default router;

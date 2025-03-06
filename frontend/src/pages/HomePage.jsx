import { Link } from "react-router-dom";
import "./HomePage.css";

const HomePage = () => {
  return (
    <div className="home-container">
      <div className="home-content">
        <h1 className="home-title">Lexislate</h1>
        <p className="home-description">
          Kitaplarınızı okuyun, yeni kelimeler öğrenin ve kelime dağarcığınızı geliştirin.
        </p>
        <div className="home-buttons">
          <Link to="/login" className="home-button login-button">
            Giriş Yap
          </Link>
          <Link to="/signup" className="home-button signup-button">
            Kayıt Ol
          </Link>
        </div>
      </div>
    </div>
  );
};

export default HomePage;

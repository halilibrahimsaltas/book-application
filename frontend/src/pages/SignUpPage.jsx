import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from '../api/axios';
import './Auth.css';

const SignUpPage = () => {
  const [formData, setFormData] = useState({
    username: "",
    password: "",
    email: ""
  });
  const [error, setError] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleSignUp = async (e) => {
    e.preventDefault();
    setIsLoading(true);
    setError("");

    try {
      const response = await api.post("/api/auth/register", formData);
      
      if (response.data.token) {
        localStorage.setItem('token', response.data.token);
        navigate("/dashboard");
      } else {
        setError("Kayıt işlemi başarısız oldu. Lütfen tekrar deneyin.");
      }
    } catch (error) {
      if (error.response) {
        setError(error.response.data.message || "Kayıt işlemi başarısız oldu.");
      } else if (error.request) {
        setError("Sunucuya bağlanılamadı. Lütfen internet bağlantınızı kontrol edin.");
      } else {
        setError("Bir hata oluştu. Lütfen tekrar deneyin.");
      }
      console.error("SignUp error:", error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="auth-container">
      <div className="auth-box">
        <h2>Yeni Hesap Oluştur</h2>
        {error && <div className="error-message">{error}</div>}
        <div className="form-container">
          <form onSubmit={handleSignUp}>
            <div className="form-group">
              <label htmlFor="username" className="form-label">Kullanıcı Adı</label>
              <input
                id="username"
                type="text"
                name="username"
                placeholder="Kullanıcı adınızı girin"
                value={formData.username}
                onChange={handleChange}
                required
                autoComplete="username"
              />
            </div>
            <div className="form-group">
              <label htmlFor="email" className="form-label">E-posta Adresi</label>
              <input
                id="email"
                type="email"
                name="email"
                placeholder="E-posta adresinizi girin"
                value={formData.email}
                onChange={handleChange}
                required
                autoComplete="email"
              />
            </div>
            <div className="form-group">
              <label htmlFor="password" className="form-label">Şifre</label>
              <input
                id="password"
                type="password"
                name="password"
                placeholder="Güçlü bir şifre oluşturun"
                value={formData.password}
                onChange={handleChange}
                required
                autoComplete="new-password"
              />
            </div>
            <button 
              type="submit" 
              className="auth-button"
              disabled={isLoading}
            >
              {isLoading ? 'Kayıt Yapılıyor...' : 'Kayıt Ol'}
            </button>
          </form>
          <p className="auth-link">
            Zaten hesabınız var mı?<Link to="/login">Giriş Yap</Link>
          </p>
        </div>
      </div>
    </div>
  );
};

export default SignUpPage; 
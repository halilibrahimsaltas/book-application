import { useState, useEffect } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from '../api/axios';

const LoginPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  // Sayfa yüklendiğinde token kontrolü yap
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      navigate('/dashboard');
    }
  }, [navigate]);

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post("/api/auth/login", {
        username,
        password,
      });
      
      if (response.data.token) {
        // Token'ı localStorage'a kaydet
        localStorage.setItem('token', response.data.token);
        
        // Kullanıcıyı dashboard'a yönlendir
        navigate("/dashboard");
      } else {
        setError("Token alınamadı. Lütfen tekrar deneyin.");
      }
    } catch (error) {
      if (error.response) {
        // Sunucudan gelen hata mesajını göster
        setError(error.response.data.message || "Giriş başarısız. Lütfen kullanıcı adı ve şifrenizi kontrol edin.");
      } else if (error.request) {
        // Sunucuya ulaşılamadı
        setError("Sunucuya bağlanılamadı. Lütfen internet bağlantınızı kontrol edin.");
      } else {
        // Diğer hatalar
        setError("Bir hata oluştu. Lütfen tekrar deneyin.");
      }
      console.error("Login error:", error);
    }
  };

  return (
    <div className="login-container">
      <h2>Giriş Yap</h2>
      {error && <div className="error-message">{error}</div>}
      <form onSubmit={handleLogin}>
        <div>
          <input
            type="text"
            placeholder="Kullanıcı Adı"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
          />
        </div>
        <div>
          <input
            type="password"
            placeholder="Şifre"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
          />
        </div>
        <button type="submit">Giriş Yap</button>
      </form>
      <p className="auth-link">
        Hesabınız yok mu? <Link to="/signup">Kayıt Ol</Link>
      </p>
    </div>
  );
};

export default LoginPage;

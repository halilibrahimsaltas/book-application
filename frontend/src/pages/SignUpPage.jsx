import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import api from '../api/axios';

const SignUpPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [email, setEmail] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleSignUp = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post("/auth/register", {
        username,
        password,
        email,
      });
      
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
    }
  };

  return (
    <div className="login-container">
      <h2>Kayıt Ol</h2>
      {error && <div className="error-message">{error}</div>}
      <form onSubmit={handleSignUp}>
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
            type="email"
            placeholder="E-posta"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
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
        <button type="submit">Kayıt Ol</button>
      </form>
      <p className="auth-link">
        Zaten hesabınız var mı? <Link to="/login">Giriş Yap</Link>
      </p>
    </div>
  );
};

export default SignUpPage; 
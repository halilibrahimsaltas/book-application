import { useState } from "react";
import { useNavigate } from "react-router-dom";
import api from '../api/axios';

const LoginPage = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    try {
      const response = await api.post("/auth/login", {
        username,
        password,
      });
      
      // Token'ı localStorage'a kaydet
      localStorage.setItem('token', response.data.token);
      
      // API isteklerinde Authorization header'ını otomatik ekle
      api.defaults.headers.common['Authorization'] = `Bearer ${response.data.token}`;
      
      console.log("Login successful:", response.data);
      navigate("/dashboard");
    } catch (error) {
      setError("Giriş başarısız. Lütfen kullanıcı adı ve şifrenizi kontrol edin.");
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
          />
        </div>
        <div>
          <input
            type="password"
            placeholder="Şifre"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
          />
        </div>
        <button type="submit">Giriş Yap</button>
      </form>
    </div>
  );
};

export default LoginPage;

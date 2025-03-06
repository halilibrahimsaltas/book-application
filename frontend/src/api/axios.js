import axios from 'axios';

const api = axios.create({
    baseURL: '',
    headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
    }
});

// Request interceptor
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        
        // OPTIONS istekleri için Authorization header'ı ekleme
        if (config.method?.toLowerCase() === 'options') {
            return config;
        }

        // Token varsa ekle
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }

        return config;
    },
    (error) => {
        console.error('Request error:', error);
        return Promise.reject(error);
    }
);

// Response interceptor
api.interceptors.response.use(
    (response) => response,
    async (error) => {
        const originalRequest = error.config;

        // Token hatası ve yeniden deneme yapılmamışsa
        if (error.response?.status === 401 && !originalRequest._retry) {
            originalRequest._retry = true;
            localStorage.removeItem('token');
            
            // Kullanıcıyı login sayfasına yönlendir
            if (window.location.pathname !== '/login') {
                window.location.href = '/login';
            }
            return Promise.reject(error);
        }

        // Diğer hata durumları
        if (error.response) {
            // Sunucu yanıtı ile dönen hatalar
            console.error('Response error:', error.response.data);
        } else if (error.request) {
            // Sunucuya ulaşılamadığında
            console.error('Network error:', error.request);
        } else {
            // Diğer hatalar
            console.error('Error:', error.message);
        }

        return Promise.reject(error);
    }
);

export default api; 
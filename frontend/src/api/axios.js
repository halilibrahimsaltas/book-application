import axios from 'axios';

const api = axios.create({
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Request interceptor
api.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

// Response interceptor
api.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response) {
            // Token süresi dolmuşsa
            if (error.response.status === 401) {
                localStorage.removeItem('token');
                // Eğer login sayfasında değilsek, login sayfasına yönlendir
                if (!window.location.pathname.includes('/login')) {
                    window.location.href = '/login';
                }
            }
            // Diğer hata durumları
            console.error('API Error:', error.response.data);
        } else if (error.request) {
            console.error('Network Error:', error.request);
        } else {
            console.error('Error:', error.message);
        }
        return Promise.reject(error);
    }
);

export default api; 
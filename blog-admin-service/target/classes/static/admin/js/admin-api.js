const API_BASE_URL = '/admin';
const GATEWAY_BASE_URL = 'http://localhost:8070';

axios.interceptors.request.use(
    config => {
        const token = localStorage.getItem('adminToken');
        if (token) {
            config.headers['Authorization'] = 'Bearer ' + token;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

axios.interceptors.response.use(
    response => {
        return response;
    },
    error => {
        if (error.response && error.response.status === 401) {
            localStorage.removeItem('adminToken');
            localStorage.removeItem('adminInfo');
            window.location.href = '/index.html';
        }
        return Promise.reject(error);
    }
);

const api = {
    login: (username, password) => {
        return axios.post(`${API_BASE_URL}/login`, { username, password });
    },

    getAdminList: (page, size) => {
        return axios.get(`${API_BASE_URL}/list`, { params: { page, size } });
    },

    getAdminById: (id) => {
        return axios.get(`${API_BASE_URL}/${id}`);
    },

    createAdmin: (admin) => {
        return axios.post(`${API_BASE_URL}`, admin);
    },

    updateAdmin: (id, admin) => {
        return axios.put(`${API_BASE_URL}/${id}`, admin);
    },

    updateAdminStatus: (id, status) => {
        return axios.put(`${API_BASE_URL}/${id}/status`, null, { params: { status } });
    },

    resetAdminPassword: (id, newPassword) => {
        return axios.put(`${API_BASE_URL}/${id}/reset-password`, null, { params: { newPassword } });
    },

    deleteAdmin: (id) => {
        return axios.delete(`${API_BASE_URL}/${id}`);
    },

    getPendingReviewArticles: (page, size) => {
        return axios.get(`${GATEWAY_BASE_URL}/api/content/articles/pending-review`, { params: { page, size } });
    },

    getArticlesByStatus: (page, size, status) => {
        return axios.get(`${GATEWAY_BASE_URL}/api/content/articles`, { params: { page, size, status } });
    },

    approveArticle: (id, reviewComment) => {
        return axios.post(`${GATEWAY_BASE_URL}/api/content/articles/${id}/approve`, { reviewComment });
    },

    rejectArticle: (id, reviewComment) => {
        return axios.post(`${GATEWAY_BASE_URL}/api/content/articles/${id}/reject`, { reviewComment });
    },

    deleteArticle: (id) => {
        return axios.delete(`${GATEWAY_BASE_URL}/api/content/articles/${id}`);
    },

    getStatisticsOverview: () => {
        return axios.get(`${API_BASE_URL}/statistics/overview`);
    },

    getStatisticsTrend: (startDate, endDate) => {
        return axios.get(`${API_BASE_URL}/statistics/trend`, { 
            params: { startDate, endDate } 
        });
    },

    getLatestStatistics: () => {
        return axios.get(`${API_BASE_URL}/statistics/latest`);
    }
};
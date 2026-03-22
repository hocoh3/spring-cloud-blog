// API请求封装 - 使用axios
// 动态设置 API 基础 URL：如果当前端口是 8000（用户服务），则使用 8070（网关）
const currentPort = window.location.port;
const API_BASE_URL = currentPort === '8000' 
    ? `${window.location.protocol}//${window.location.hostname}:8070/api`
    : '/api';

// 创建axios实例
const axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    timeout: 10000,
    withCredentials: true,
    headers: {
        'Content-Type': 'application/json'
    }
});

// 请求拦截器 - 添加token
axiosInstance.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// 响应拦截器 - 统一处理错误
axiosInstance.interceptors.response.use(
    response => {
        return response.data;
    },
    error => {
        console.error('API请求失败:', error);
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    console.error('未授权，请重新登录');
                    localStorage.removeItem('token');
                    localStorage.removeItem('user');
                    window.location.href = 'src/pages/login.html';
                    break;
                case 403:
                    console.error('拒绝访问');
                    break;
                case 404:
                    console.error('请求的资源不存在');
                    break;
                case 500:
                    console.error('服务器错误');
                    break;
                default:
                    console.error('未知错误');
            }
        }
        return Promise.reject(error);
    }
);

// 获取本地存储的token
function getToken() {
    return localStorage.getItem('token');
}

// 设置本地存储的token
function setToken(token) {
    localStorage.setItem('token', token);
}

// 清除本地存储的token
function clearToken() {
    localStorage.removeItem('token');
}

// 检查用户是否已登录
function isLoggedIn() {
    return !!getToken();
}

// 通用GET请求
async function get(url, params = {}) {
    try {
        const response = await axiosInstance.get(url, { params });
        return response;
    } catch (error) {
        console.error('GET请求失败:', error);
        throw error;
    }
}

// 通用POST请求
async function post(url, data = {}) {
    try {
        const response = await axiosInstance.post(url, data);
        return response;
    } catch (error) {
        console.error('POST请求失败:', error);
        throw error;
    }
}

// 通用PUT请求
async function put(url, data = {}) {
    try {
        const response = await axiosInstance.put(url, data);
        return response;
    } catch (error) {
        console.error('PUT请求失败:', error);
        throw error;
    }
}

// 通用DELETE请求
async function del(url) {
    try {
        const response = await axiosInstance.delete(url);
        return response;
    } catch (error) {
        console.error('DELETE请求失败:', error);
        throw error;
    }
}

// 用户相关API
const userApi = {
    // 用户注册
    register: (userData) => post('/users/register', userData),
    
    // 用户登录
    login: (credentials) => post('/users/login', credentials),
    
    // 获取用户信息
    getUserById: (id) => get(`/users/${id}`),
    
    // 获取当前用户信息
    getCurrentUser: () => get('/users/current')
};

// 文章相关API
const articleApi = {
    // 获取文章列表
    getArticles: (params) => get('/content/articles', params),
    
    // 获取文章详情
    getArticleById: (id) => get(`/content/articles/${id}`),
    
    // 增加文章浏览量
    increaseViewCount: (id) => post(`/content/articles/${id}/view`),
    
    // 搜索文章
    searchArticles: (keyword, params) => get(`/search/articles?q=${keyword}`, params),
    
    // 获取用户文章
    getUserArticles: (userId, params) => get(`/content/articles/user/${userId}`, params)
};

// 评论相关API
const commentApi = {
    // 获取文章评论
    getCommentsByArticleId: (articleId, params) => get(`/interaction/comments/article/${articleId}`, params),
    
    // 添加评论
    addComment: (commentData) => post('/interaction/comments', commentData)
};

// 分类相关API
const categoryApi = {
    // 获取分类列表
    getCategories: () => get('/content/categories'),
    
    // 获取分类下的文章
    getArticlesByCategory: (categoryId, params) => get(`/content/articles/category/${categoryId}`, params)
};

// 通知相关API
const notificationApi = {
    // 获取用户通知
    getNotifications: (params) => get('/notification/notifications', params),
    
    // 获取未读通知数量
    getUnreadCount: (userId) => get(`/notification/notifications/unread/count/${userId}`)
};

// 私信相关API
const messageApi = {
    // 获取用户私信
    getMessages: (userId, params) => get('/message/user/' + userId, params),
    
    // 获取未读私信数量
    getUnreadCount: (userId) => get(`/message/unread/count/${userId}`),
    
    // 发送私信
    sendMessage: (messageData) => post('/message/send', messageData),
    
    // 标记私信为已读
    markAsRead: (id) => put('/message/read/' + id),
    
    // 标记所有私信为已读
    markAllAsRead: (userId) => put('/message/read/all/' + userId),
    
    // 删除私信
    deleteMessage: (id) => del('/message/' + id)
};

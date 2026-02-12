// 导入API模块
import { 
    get, 
    userApi, 
    articleApi, 
    commentApi, 
    categoryApi, 
    tagApi,
    isLoggedIn, 
    getToken
} from './api.js';

// DOM加载完成后执行
document.addEventListener('DOMContentLoaded', function() {
    // 检查页面类型并执行相应的初始化函数
    const path = window.location.pathname;
    
    if (path.includes('index.html') || path === '/') {
        // 首页初始化
        initHomePage();
    } else if (path.includes('login.html')) {
        // 登录页面初始化
        initLoginPage();
    } else if (path.includes('register.html')) {
        // 注册页面初始化
        initRegisterPage();
    } else if (path.includes('article.html')) {
        // 文章详情页初始化
        initArticlePage();
    } else if (path.includes('user.html')) {
        // 用户页面初始化
        initUserPage();
    } else if (path.includes('category.html')) {
        // 分类页面初始化
        initCategoryPage();
    } else if (path.includes('categories.html')) {
        // 分类列表页面初始化
        initCategoriesPage();
    } else if (path.includes('tag.html')) {
        // 标签页面初始化
        initTagPage();
    } else if (path.includes('tags.html')) {
        // 标签列表页面初始化
        initTagsPage();
    }
    
    // 初始化通用组件
    initCommonComponents();
});

// 初始化通用组件
function initCommonComponents() {
    // 搜索功能
    const searchBtn = document.getElementById('searchBtn');
    const searchInput = document.getElementById('searchInput');
    
    if (searchBtn && searchInput) {
        searchBtn.addEventListener('click', function() {
            const keyword = searchInput.value.trim();
            if (keyword) {
                // 跳转到搜索结果页面
                window.location.href = `src/pages/search.html?q=${encodeURIComponent(keyword)}`;
            }
        });
        
        // 回车键搜索
        searchInput.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchBtn.click();
            }
        });
    }
    
    // 更新用户登录状态
    updateUserStatus();
}

// 更新用户登录状态
function updateUserStatus() {
    const loginBtn = document.querySelector('.login-btn');
    const registerBtn = document.querySelector('.register-btn');
    const userInfo = document.querySelector('.user-info');
    
    if (isLoggedIn()) {
        // 用户已登录，隐藏登录注册按钮
        if (loginBtn) loginBtn.style.display = 'none';
        if (registerBtn) registerBtn.style.display = 'none';
        
        // 显示用户信息
        if (userInfo) {
            const userName = document.querySelector('.user-name');
            const userAvatar = document.querySelector('.user-avatar img');
            const userActions = document.querySelector('.user-actions');
            
            // 这里可以从localStorage获取用户信息，或者调用API获取
            const user = JSON.parse(localStorage.getItem('user'));
            if (user) {
                if (userName) userName.textContent = user.nickname || user.username;
                if (userAvatar && user.avatar) userAvatar.src = user.avatar;
                
                // 更新用户操作按钮
                if (userActions) {
                    userActions.innerHTML = `
                        <a href="src/pages/profile.html" class="btn btn-primary">个人中心</a>
                        <button class="btn btn-secondary" onclick="logout()">退出登录</button>
                    `;
                }
            }
        }
    } else {
        // 用户未登录，显示登录注册按钮
        if (loginBtn) loginBtn.style.display = 'inline-block';
        if (registerBtn) registerBtn.style.display = 'inline-block';
        
        // 显示未登录状态
        if (userInfo) {
            const userName = document.querySelector('.user-name');
            const userActions = document.querySelector('.user-actions');
            
            if (userName) userName.textContent = '未登录';
            if (userActions) {
                userActions.innerHTML = `
                    <a href="src/pages/login.html" class="btn btn-primary">登录</a>
                    <a href="src/pages/register.html" class="btn btn-secondary">注册</a>
                `;
            }
        }
    }
}

// 用户退出登录
function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    updateUserStatus();
    window.location.href = 'index.html';
}

// 初始化首页
function initHomePage() {
    // 加载文章列表
    loadArticles();
    
    // 加载热门文章
    loadHotArticles();
    
    // 加载分类列表
    loadCategories();
    
    // 加载标签云
    loadTags();
}

// 加载文章列表
async function loadArticles(page = 1, size = 10) {
    try {
        const response = await articleApi.getArticles({ page, size });
        renderArticles(response.records);
    } catch (error) {
        console.error('加载文章列表失败:', error);
        // 显示错误提示
        const articlesContainer = document.querySelector('.articles-container');
        if (articlesContainer) {
            articlesContainer.innerHTML = '<p class="error-message">加载文章失败，请稍后重试</p>';
        }
    }
}

// 渲染文章列表
function renderArticles(articles) {
    const articlesContainer = document.querySelector('.articles-container');
    if (!articlesContainer) return;
    
    if (articles.length === 0) {
        articlesContainer.innerHTML = '<p class="no-data">暂无文章</p>';
        return;
    }
    
    const articlesHtml = articles.map(article => `
        <article class="article-item">
            <div class="article-header">
                <h2 class="article-title"><a href="src/pages/article.html?id=${article.id}">${article.title}</a></h2>
                <div class="article-meta">
                    <span class="author">作者: <a href="src/pages/user.html?id=${article.userId}">${article.author?.nickname || '未知作者'}</a></span>
                    <span class="category">分类: <a href="src/pages/category.html?id=${article.categoryId}">${article.category?.name || '未分类'}</a></span>
                    <span class="date">${new Date(article.createTime).toLocaleDateString('zh-CN')}</span>
                    <span class="views">浏览量: ${article.viewCount || 0}</span>
                </div>
            </div>
            <div class="article-content">
                <p>${article.summary}</p>
            </div>
            <div class="article-footer">
                <a href="src/pages/article.html?id=${article.id}" class="read-more">阅读全文</a>
                <div class="article-stats">
                    <span class="comments">评论: ${article.commentCount || 0}</span>
                    <span class="likes">点赞: ${article.likeCount || 0}</span>
                </div>
            </div>
        </article>
    `).join('');
    
    articlesContainer.innerHTML = articlesHtml;
}

// 加载热门文章
async function loadHotArticles() {
    try {
        const response = await articleApi.getArticles({ page: 1, size: 5, sort: 'viewCount,desc' });
        renderHotArticles(response.records);
    } catch (error) {
        console.error('加载热门文章失败:', error);
    }
}

// 渲染热门文章
function renderHotArticles(articles) {
    const hotArticlesList = document.querySelector('.hot-articles');
    if (!hotArticlesList) return;
    
    const hotArticlesHtml = articles.map(article => `
        <li><a href="src/pages/article.html?id=${article.id}">${article.title}</a></li>
    `).join('');
    
    hotArticlesList.innerHTML = hotArticlesHtml;
}

// 加载分类列表
async function loadCategories() {
    try {
        const categories = await categoryApi.getCategories();
        renderCategories(categories);
    } catch (error) {
        console.error('加载分类列表失败:', error);
    }
}

// 渲染分类列表
function renderCategories(categories) {
    const categoryList = document.querySelector('.category-list');
    if (!categoryList) return;
    
    const categoriesHtml = categories.map(category => `
        <li><a href="src/pages/category.html?id=${category.id}">${category.name}</a> <span class="count">${category.articleCount || 0}</span></li>
    `).join('');
    
    categoryList.innerHTML = categoriesHtml;
}

// 加载标签云
async function loadTags() {
    try {
        const tags = await tagApi.getTags();
        renderTags(tags);
    } catch (error) {
        console.error('加载标签云失败:', error);
    }
}

// 渲染标签云
function renderTags(tags) {
    const tagCloud = document.querySelector('.tag-cloud');
    if (!tagCloud) return;
    
    const tagsHtml = tags.map(tag => `
        <a href="src/pages/tag.html?id=${tag.id}">${tag.name}</a>
    `).join('');
    
    tagCloud.innerHTML = tagsHtml;
}

// 初始化登录页面
function initLoginPage() {
    console.log('开始初始化登录页面');
    const loginForm = document.getElementById('loginForm');
    console.log('获取到登录表单：', loginForm);
    if (loginForm) {
        loginForm.addEventListener('submit', async function(e) {
            console.log('登录表单被提交');
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            console.log('用户名：', username, '密码：', password);
            
            try {
                console.log('开始调用登录API');
                const response = await userApi.login({ username, password });
                console.log('登录API返回结果：', response);
                if (response.token) {
                    // 登录成功，保存token和用户信息
                    localStorage.setItem('token', response.token);
                    localStorage.setItem('user', JSON.stringify(response.user));
                    
                    // 跳转到首页
                    window.location.href = 'index.html';
                } else {
                    alert('登录失败：' + (response.message || '用户名或密码错误'));
                }
            } catch (error) {
                console.error('登录过程中发生错误：', error);
                alert('登录失败：网络错误，请稍后重试');
            }
        });
    }
}

// 初始化注册页面
function initRegisterPage() {
    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        registerForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const username = document.getElementById('username').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;
            const confirmPassword = document.getElementById('confirmPassword').value;
            const nickname = document.getElementById('nickname').value;
            
            // 验证密码
            if (password !== confirmPassword) {
                alert('两次输入的密码不一致');
                return;
            }
            
            try {
                const response = await userApi.register({ username, email, password, nickname });
                if (response) {
                    alert('注册成功，请登录');
                    window.location.href = 'src/pages/login.html';
                } else {
                    alert('注册失败：用户名或邮箱已存在');
                }
            } catch (error) {
                alert('注册失败：网络错误，请稍后重试');
            }
        });
    }
}

// 初始化文章详情页
function initArticlePage() {
    // 获取文章ID
    const urlParams = new URLSearchParams(window.location.search);
    const articleId = urlParams.get('id');
    
    if (articleId) {
        // 加载文章详情
        loadArticleDetail(articleId);
        
        // 加载文章评论
        loadArticleComments(articleId);
        
        // 增加文章浏览量
        articleApi.increaseViewCount(articleId);
        
        // 初始化评论提交功能
        initCommentSubmission(articleId);
    }
}

// 加载文章详情
async function loadArticleDetail(articleId) {
    try {
        const article = await articleApi.getArticleById(articleId);
        renderArticleDetail(article);
    } catch (error) {
        console.error('加载文章详情失败:', error);
        const articleContainer = document.querySelector('.article-detail');
        if (articleContainer) {
            articleContainer.innerHTML = '<p class="error-message">加载文章失败，请稍后重试</p>';
        }
    }
}

// 渲染文章详情
function renderArticleDetail(article) {
    const articleDetail = document.querySelector('.article-detail');
    if (!articleDetail) return;
    
    articleDetail.innerHTML = `
        <article class="article-full">
            <div class="article-header">
                <h1 class="article-title">${article.title}</h1>
                <div class="article-meta">
                    <span class="author">作者: <a href="src/pages/user.html?id=${article.userId}">${article.author?.nickname || '未知作者'}</a></span>
                    <span class="category">分类: <a href="src/pages/category.html?id=${article.categoryId}">${article.category?.name || '未分类'}</a></span>
                    <span class="date">${new Date(article.createTime).toLocaleDateString('zh-CN')}</span>
                    <span class="views">浏览量: ${article.viewCount || 0}</span>
                </div>
            </div>
            <div class="article-content">
                ${article.content}
            </div>
            <div class="article-tags">
                ${article.tags?.map(tag => `<a href="src/pages/tag.html?id=${tag.id}" class="tag">${tag.name}</a>`).join('')}
            </div>
        </article>
    `;
}

// 加载文章评论
async function loadArticleComments(articleId) {
    try {
        const comments = await commentApi.getCommentsByArticleId(articleId, { page: 1, size: 10 });
        renderArticleComments(comments);
    } catch (error) {
        console.error('加载文章评论失败:', error);
        const commentsContainer = document.querySelector('.comments-container');
        if (commentsContainer) {
            commentsContainer.innerHTML = '<p class="error-message">加载评论失败，请稍后重试</p>';
        }
    }
}

// 渲染文章评论
function renderArticleComments(comments) {
    const commentsList = document.querySelector('.comments-list');
    if (!commentsList) return;
    
    if (comments.length === 0) {
        commentsList.innerHTML = '<p class="no-data">暂无评论</p>';
        return;
    }
    
    const commentsHtml = comments.map(comment => `
        <div class="comment-item">
            <div class="comment-header">
                <div class="comment-author">
                    <img src="${comment.user?.avatar || 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'40\' height=\'40\' viewBox=\'0 0 40 40\'%3E%3Crect width=\'40\' height=\'40\' fill=\'%23e9ecef\'/%3E%3Ctext x=\'20\' y=\'20\' font-family=\'Arial\' font-size=\'16\' fill=\'%236c757d\' text-anchor=\'middle\' dy=\'.3em\'%3E%3F%3C/text%3E%3C/svg%3E'}" alt="用户头像" class="comment-avatar">
                    <span class="author-name">${comment.user?.nickname || '未知用户'}</span>
                </div>
                <span class="comment-date">${new Date(comment.createTime).toLocaleString('zh-CN')}</span>
            </div>
            <div class="comment-content">
                ${comment.content}
            </div>
        </div>
    `).join('');
    
    commentsList.innerHTML = commentsHtml;
}

// 初始化用户页面
function initUserPage() {
    // 获取用户ID
    const urlParams = new URLSearchParams(window.location.search);
    const userId = urlParams.get('id');
    
    if (userId) {
        // 加载用户信息
        loadUserInfo(userId);
        
        // 加载用户文章
        loadUserArticles(userId);
    }
}

// 加载用户信息
async function loadUserInfo(userId) {
    try {
        const user = await userApi.getUserById(userId);
        renderUserInfo(user);
    } catch (error) {
        console.error('加载用户信息失败:', error);
    }
}

// 渲染用户信息
function renderUserInfo(user) {
    const userProfile = document.querySelector('.user-profile');
    if (!userProfile) return;
    
    userProfile.innerHTML = `
        <div class="user-avatar-large">
            <img src="${user.avatar || 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'150\' height=\'150\' viewBox=\'0 0 150 150\'%3E%3Crect width=\'150\' height=\'150\' fill=\'%23e9ecef\'/%3E%3Ctext x=\'75\' y=\'75\' font-family=\'Arial\' font-size=\'60\' fill=\'%236c757d\' text-anchor=\'middle\' dy=\'.3em\'%3E%3F%3C/text%3E%3C/svg%3E'}" alt="用户头像">
        </div>
        <div class="user-info-detail">
            <h1 class="user-name-large">${user.nickname || user.username}</h1>
            <p class="user-bio">${user.bio || '暂无简介'}</p>
            <div class="user-stats">
                <span class="stat-item">文章: ${user.articleCount || 0}</span>
                <span class="stat-item">粉丝: ${user.followerCount || 0}</span>
                <span class="stat-item">关注: ${user.followingCount || 0}</span>
            </div>
        </div>
    `;
}

// 加载用户文章
async function loadUserArticles(userId) {
    try {
        const articles = await articleApi.getArticles({ userId, page: 1, size: 10 });
        renderUserArticles(articles);
    } catch (error) {
        console.error('加载用户文章失败:', error);
    }
}

// 渲染用户文章
function renderUserArticles(articles) {
    const userArticles = document.querySelector('.user-articles');
    if (!userArticles) return;
    
    renderArticles(articles, userArticles);
}

// 初始化分类页面
function initCategoryPage() {
    // 获取分类ID
    const urlParams = new URLSearchParams(window.location.search);
    const categoryId = urlParams.get('id');
    
    if (categoryId) {
        // 加载分类信息
        loadCategoryInfo(categoryId);
        
        // 加载分类下的文章
        loadCategoryArticles(categoryId);
    }
}

// 加载分类信息
async function loadCategoryInfo(categoryId) {
    try {
        const categories = await categoryApi.getCategories();
        const category = categories.find(c => c.id === parseInt(categoryId));
        if (category) {
            renderCategoryInfo(category);
        }
    } catch (error) {
        console.error('加载分类信息失败:', error);
    }
}

// 渲染分类信息
function renderCategoryInfo(category) {
    const categoryHeader = document.querySelector('.category-header');
    if (categoryHeader) {
        categoryHeader.innerHTML = `<h1 class="category-title">${category.name}</h1>`;
    }
}

// 加载分类下的文章
async function loadCategoryArticles(categoryId) {
    try {
        const articles = await categoryApi.getArticlesByCategory(categoryId, { page: 1, size: 10 });
        renderArticles(articles);
    } catch (error) {
        console.error('加载分类文章失败:', error);
    }
}

// 初始化分类列表页面
function initCategoriesPage() {
    loadCategories();
}

// 初始化标签页面
function initTagPage() {
    // 获取标签ID
    const urlParams = new URLSearchParams(window.location.search);
    const tagId = urlParams.get('id');
    
    if (tagId) {
        // 加载标签信息
        loadTagInfo(tagId);
        
        // 加载标签下的文章
        loadTagArticles(tagId);
    }
}

// 加载标签信息
async function loadTagInfo(tagId) {
    try {
        const tags = await tagApi.getTags();
        const tag = tags.find(t => t.id === parseInt(tagId));
        if (tag) {
            renderTagInfo(tag);
        }
    } catch (error) {
        console.error('加载标签信息失败:', error);
    }
}

// 渲染标签信息
function renderTagInfo(tag) {
    const tagHeader = document.querySelector('.tag-header');
    if (tagHeader) {
        tagHeader.innerHTML = `<h1 class="tag-title"># ${tag.name}</h1>`;
    }
}

// 加载标签下的文章
async function loadTagArticles(tagId) {
    try {
        const articles = await tagApi.getArticlesByTag(tagId, { page: 1, size: 10 });
        renderArticles(articles);
    } catch (error) {
        console.error('加载标签文章失败:', error);
    }
}

// 初始化标签列表页面
function initTagsPage() {
    loadTags();
}

// 初始化评论提交功能
function initCommentSubmission(articleId) {
    const commentForm = document.getElementById('commentForm');
    if (commentForm) {
        commentForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            // 检查用户是否登录
            if (!isLoggedIn()) {
                alert('请先登录后再评论');
                window.location.href = 'src/pages/login.html';
                return;
            }
            
            const content = document.getElementById('commentContent').value.trim();
            if (!content) {
                alert('评论内容不能为空');
                return;
            }
            
            try {
                const commentData = {
                    articleId: parseInt(articleId),
                    content: content
                };
                
                await commentApi.addComment(commentData);
                
                // 清空评论框
                document.getElementById('commentContent').value = '';
                
                // 重新加载评论列表
                loadArticleComments(articleId);
            } catch (error) {
                console.error('提交评论失败:', error);
                alert('提交评论失败，请稍后重试');
            }
        });
    }
}

// 初始化搜索页面
function initSearchPage() {
    // 获取搜索关键词
    const urlParams = new URLSearchParams(window.location.search);
    const keyword = urlParams.get('q');
    
    if (keyword) {
        // 更新搜索框内容
        const searchInput = document.getElementById('searchInput');
        if (searchInput) {
            searchInput.value = decodeURIComponent(keyword);
        }
        
        // 加载搜索结果
        loadSearchResults(keyword);
    }
}

// 加载搜索结果
async function loadSearchResults(keyword, page = 1, size = 10) {
    try {
        const response = await articleApi.searchArticles(keyword, { page, size });
        renderSearchResults(response.records, keyword);
    } catch (error) {
        console.error('搜索失败:', error);
        const resultsContainer = document.querySelector('.search-results');
        if (resultsContainer) {
            resultsContainer.innerHTML = '<p class="error-message">搜索失败，请稍后重试</p>';
        }
    }
}

// 渲染搜索结果
function renderSearchResults(articles, keyword) {
    const resultsContainer = document.querySelector('.search-results');
    if (!resultsContainer) return;
    
    const searchTitle = document.querySelector('.search-title');
    if (searchTitle) {
        searchTitle.textContent = `搜索 "${decodeURIComponent(keyword)}" 的结果`;
    }
    
    if (articles.length === 0) {
        resultsContainer.innerHTML = '<p class="no-data">没有找到相关文章</p>';
        return;
    }
    
    renderArticles(articles);
}

// 初始化个人中心页面
function initProfilePage() {
    if (!isLoggedIn()) {
        window.location.href = 'src/pages/login.html';
        return;
    }
    
    // 加载用户个人信息
    loadUserProfile();
    
    // 加载用户的文章
    loadUserArticlesForProfile();
    
    // 初始化个人信息编辑功能
    initProfileEdit();
}

// 加载用户个人信息
async function loadUserProfile() {
    try {
        const user = JSON.parse(localStorage.getItem('user'));
        if (user) {
            // 调用API获取最新用户信息
            const updatedUser = await userApi.getUserById(user.id);
            localStorage.setItem('user', JSON.stringify(updatedUser));
            renderUserProfile(updatedUser);
        }
    } catch (error) {
        console.error('加载个人信息失败:', error);
    }
}

// 渲染用户个人信息
function renderUserProfile(user) {
    const profileContainer = document.querySelector('.profile-container');
    if (!profileContainer) return;
    
    profileContainer.innerHTML = `
        <div class="profile-header">
            <div class="profile-avatar">
                <img src="${user.avatar || 'data:image/svg+xml,%3Csvg xmlns=\'http://www.w3.org/2000/svg\' width=\'150\' height=\'150\' viewBox=\'0 0 150 150\'%3E%3Crect width=\'150\' height=\'150\' fill=\'%23e9ecef\'/%3E%3Ctext x=\'75\' y=\'75\' font-family=\'Arial\' font-size=\'60\' fill=\'%236c757d\' text-anchor=\'middle\' dy=\'.3em\'%3E%3F%3C/text%3E%3C/svg%3E'}" alt="用户头像">
            </div>
            <div class="profile-info">
                <h1 class="profile-name">${user.nickname || user.username}</h1>
                <p class="profile-email">${user.email}</p>
                <p class="profile-bio">${user.bio || '暂无简介'}</p>
                <div class="profile-stats">
                    <span>文章: ${user.articleCount || 0}</span>
                    <span>关注: ${user.followingCount || 0}</span>
                    <span>粉丝: ${user.followerCount || 0}</span>
                </div>
            </div>
        </div>
    `;
}

// 加载用户的文章（个人中心）
async function loadUserArticlesForProfile() {
    try {
        const user = JSON.parse(localStorage.getItem('user'));
        if (user) {
            const articles = await articleApi.getArticles({ userId: user.id, page: 1, size: 10 });
            renderUserProfileArticles(articles);
        }
    } catch (error) {
        console.error('加载个人文章失败:', error);
    }
}

// 渲染用户的文章（个人中心）
function renderUserProfileArticles(articles) {
    const articlesContainer = document.querySelector('.profile-articles');
    if (!articlesContainer) return;
    
    if (articles.length === 0) {
        articlesContainer.innerHTML = '<p class="no-data">暂无文章</p>';
        return;
    }
    
    renderArticles(articles);
}

// 初始化个人信息编辑功能
function initProfileEdit() {
    const editBtn = document.getElementById('editProfileBtn');
    if (editBtn) {
        editBtn.addEventListener('click', function() {
            showEditProfileForm();
        });
    }
}

// 显示个人信息编辑表单
function showEditProfileForm() {
    const user = JSON.parse(localStorage.getItem('user'));
    if (!user) return;
    
    const editForm = document.getElementById('editProfileForm');
    if (editForm) {
        // 填充表单数据
        document.getElementById('editNickname').value = user.nickname || '';
        document.getElementById('editBio').value = user.bio || '';
        
        // 显示编辑表单
        editForm.style.display = 'block';
        
        // 提交表单事件
        editForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            
            const nickname = document.getElementById('editNickname').value.trim();
            const bio = document.getElementById('editBio').value.trim();
            
            try {
                const updateData = {
                    nickname: nickname,
                    bio: bio
                };
                
                await userApi.updateUserInfo(user.id, updateData);
                
                // 更新localStorage中的用户信息
                user.nickname = nickname;
                user.bio = bio;
                localStorage.setItem('user', JSON.stringify(user));
                
                // 重新渲染个人信息
                renderUserProfile(user);
                
                // 隐藏编辑表单
                editForm.style.display = 'none';
                
                alert('个人信息更新成功');
            } catch (error) {
                console.error('更新个人信息失败:', error);
                alert('更新个人信息失败，请稍后重试');
            }
        });
    }
}

// 更新页面类型判断
const originalDOMContentLoaded = document.addEventListener.bind(document);
document.addEventListener = function(type, listener, options) {
    if (type === 'DOMContentLoaded') {
        const wrappedListener = function(event) {
            // 调用原始监听器
            listener(event);
            
            // 检查页面类型并执行相应的初始化函数
            const path = window.location.pathname;
            
            if (path.includes('search.html')) {
                // 搜索页面初始化
                initSearchPage();
            } else if (path.includes('profile.html')) {
                // 个人中心页面初始化
                initProfilePage();
            }
        };
        return originalDOMContentLoaded(type, wrappedListener, options);
    }
    return originalDOMContentLoaded(type, listener, options);
};

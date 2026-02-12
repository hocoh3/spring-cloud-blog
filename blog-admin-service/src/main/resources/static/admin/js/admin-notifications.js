const NotificationsComponent = {
    template: `
        <div class="notifications-management">
            <div class="management-header">
                <h2>通知管理</h2>
                <div class="header-actions">
                    <button @click="showCreateModal = true" class="btn btn-primary">创建通知</button>
                    <button @click="loadNotifications" class="btn btn-secondary">刷新</button>
                </div>
            </div>

            <div class="search-bar">
                <input type="text" v-model="searchKeyword" placeholder="搜索通知..." @keyup.enter="loadNotifications">
                <select v-model="filterStatus" @change="loadNotifications">
                    <option value="">全部状态</option>
                    <option value="0">未读</option>
                    <option value="1">已读</option>
                </select>
            </div>

            <div v-if="loading" class="loading">加载中...</div>
            <div v-else-if="error" class="error-message">{{ error }}</div>
            <div v-else class="notifications-list">
                <div v-if="notifications.length === 0" class="no-data">暂无通知</div>
                <div v-for="notification in filteredNotifications" :key="notification.id" class="notification-item">
                    <div class="notification-header">
                        <div class="notification-title">{{ notification.title }}</div>
                        <div class="notification-meta">
                            <span class="notification-user">用户ID: {{ notification.userId }}</span>
                            <span class="notification-time">{{ formatTime(notification.createTime) }}</span>
                            <span class="notification-status" :class="'status-' + notification.isRead">
                                {{ notification.isRead === 0 ? '未读' : '已读' }}
                            </span>
                        </div>
                    </div>
                    <div class="notification-content">{{ notification.content }}</div>
                    <div class="notification-actions">
                        <button @click.stop.prevent="viewNotification(notification)" class="btn btn-sm btn-info">查看详情</button>
                        <button @click.stop.prevent="deleteNotification(notification.id)" class="btn btn-sm btn-danger">删除</button>
                    </div>
                </div>
            </div>

            <div class="pagination">
                <button @click="prevPage" :disabled="currentPage <= 1" class="btn btn-sm">上一页</button>
                <span>第 {{ currentPage }} 页</span>
                <button @click="nextPage" :disabled="currentPage >= totalPages" class="btn btn-sm">下一页</button>
            </div>

            <div v-if="showCreateModal" class="modal-overlay" @click="closeCreateModal">
                <div class="modal-content" @click.stop>
                    <div class="modal-header">
                        <h3>创建通知</h3>
                        <button @click="closeCreateModal" class="close-btn">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="userId">接收用户:</label>
                            <select id="userId" v-model="createForm.userId">
                                <option value="all">全体用户</option>
                                <option value="">指定用户ID</option>
                            </select>
                        </div>
                        <div class="form-group" v-if="createForm.userId === ''">
                            <label for="specificUserId">用户ID:</label>
                            <input type="number" id="specificUserId" v-model="createForm.specificUserId" placeholder="请输入用户ID">
                        </div>
                        <div class="form-group">
                            <label for="title">标题:</label>
                            <input type="text" id="title" v-model="createForm.title" required>
                        </div>
                        <div class="form-group">
                            <label for="content">内容:</label>
                            <textarea id="content" v-model="createForm.content" rows="5" required></textarea>
                        </div>
                        <div class="form-group">
                            <label for="relatedId">关联ID (可选):</label>
                            <input type="number" id="relatedId" v-model="createForm.relatedId">
                        </div>
                        <div class="form-group">
                            <label for="relatedType">关联类型:</label>
                            <select id="relatedType" v-model="createForm.relatedType">
                                <option value="">无</option>
                                <option value="article">文章</option>
                                <option value="comment">评论</option>
                                <option value="user">用户</option>
                            </select>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button @click="closeCreateModal" class="btn btn-secondary">取消</button>
                        <button @click="createNotification" class="btn btn-primary" :disabled="createLoading">
                            {{ createLoading ? '创建中...' : '创建' }}
                        </button>
                    </div>
                </div>
            </div>

            <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
                <div class="modal-content" @click.stop>
                    <div class="modal-header">
                        <h3>通知详情</h3>
                        <button @click="closeDetailModal" class="close-btn">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="detail-item">
                            <label>标题:</label>
                            <span>{{ selectedNotification.title }}</span>
                        </div>
                        <div class="detail-item">
                            <label>内容:</label>
                            <span>{{ selectedNotification.content }}</span>
                        </div>
                        <div class="detail-item">
                            <label>用户ID:</label>
                            <span>{{ selectedNotification.userId }}</span>
                        </div>
                        <div class="detail-item">
                            <label>状态:</label>
                            <span :class="'status-' + selectedNotification.isRead">
                                {{ selectedNotification.isRead === 0 ? '未读' : '已读' }}
                            </span>
                        </div>
                        <div class="detail-item">
                            <label>创建时间:</label>
                            <span>{{ formatTime(selectedNotification.createTime) }}</span>
                        </div>
                        <div class="detail-item" v-if="selectedNotification.relatedId">
                            <label>关联ID:</label>
                            <span>{{ selectedNotification.relatedId }}</span>
                        </div>
                        <div class="detail-item" v-if="selectedNotification.relatedType">
                            <label>关联类型:</label>
                            <span>{{ selectedNotification.relatedType }}</span>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button @click="closeDetailModal" class="btn btn-secondary">关闭</button>
                    </div>
                </div>
            </div>

            <div v-if="showDeleteConfirm" class="modal-overlay" @click="cancelDelete">
                <div class="modal-content" @click.stop>
                    <div class="modal-header">
                        <h3>确认删除</h3>
                        <button @click="cancelDelete" class="close-btn">&times;</button>
                    </div>
                    <div class="modal-body">
                        <p>确定要删除这条通知吗？</p>
                    </div>
                    <div class="modal-footer">
                        <button @click="cancelDelete" class="btn btn-secondary">取消</button>
                        <button @click="confirmDelete" class="btn btn-danger">确定删除</button>
                    </div>
                </div>
            </div>
        </div>
    `,
    data() {
        return {
            notifications: [],
            loading: false,
            error: '',
            currentPage: 1,
            pageSize: 10,
            totalPages: 0,
            searchKeyword: '',
            filterStatus: '',
            showCreateModal: false,
            showDetailModal: false,
            showDeleteConfirm: false,
            selectedNotification: {},
            deleteTargetId: null,
            createForm: {
                userId: '',
                title: '',
                content: '',
                relatedId: '',
                relatedType: ''
            },
            createLoading: false
        };
    },
    computed: {
        filteredNotifications() {
            let filtered = this.notifications;
            
            if (this.searchKeyword) {
                const keyword = this.searchKeyword.toLowerCase();
                filtered = filtered.filter(n => 
                    n.title.toLowerCase().includes(keyword) || 
                    n.content.toLowerCase().includes(keyword)
                );
            }
            
            if (this.filterStatus !== '') {
                filtered = filtered.filter(n => n.isRead === parseInt(this.filterStatus));
            }
            
            return filtered;
        }
    },
    mounted() {
        this.loadNotifications();
    },
    methods: {
        async loadNotifications() {
            this.loading = true;
            this.error = '';
            try {
                const response = await axios.get('http://localhost:8070/api/notification/notifications', {
                    params: {
                        page: this.currentPage - 1,
                        size: this.pageSize
                    },
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
                    }
                });
                if (response.data) {
                    this.notifications = response.data;
                    this.totalPages = Math.ceil(this.notifications.length / this.pageSize);
                }
            } catch (error) {
                console.error('加载通知失败:', error);
                this.error = '加载通知失败，请稍后重试';
            } finally {
                this.loading = false;
            }
        },
        async createNotification() {
            if (!this.createForm.title || !this.createForm.content) {
                alert('请填写必填项');
                return;
            }
            
            if (this.createForm.userId === '' && !this.createForm.specificUserId) {
                alert('请选择接收用户或输入用户ID');
                return;
            }
            
            this.createLoading = true;
            try {
                const notificationData = {
                    title: this.createForm.title,
                    content: this.createForm.content,
                    relatedId: this.createForm.relatedId ? parseInt(this.createForm.relatedId) : null,
                    relatedType: this.createForm.relatedType || null
                };
                
                let apiUrl;
                if (this.createForm.userId === 'all') {
                    apiUrl = 'http://localhost:8070/api/notification/broadcast';
                } else {
                    notificationData.userId = parseInt(this.createForm.specificUserId);
                    apiUrl = 'http://localhost:8070/api/notification/notifications';
                }
                
                await axios.post(apiUrl, notificationData, {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
                    }
                });
                alert('通知创建成功');
                this.closeCreateModal();
                this.loadNotifications();
            } catch (error) {
                console.error('创建通知失败:', error);
                alert('创建通知失败，请稍后重试');
            } finally {
                this.createLoading = false;
            }
        },
        deleteNotification(id) {
            this.deleteTargetId = id;
            this.showDeleteConfirm = true;
        },
        async confirmDelete() {
            if (!this.deleteTargetId) return;
            
            try {
                await axios.delete('http://localhost:8070/api/notification/' + this.deleteTargetId, {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
                    }
                });
                alert('删除成功');
                this.cancelDelete();
                this.loadNotifications();
            } catch (error) {
                console.error('删除通知失败:', error);
                alert('删除通知失败，请稍后重试');
            }
        },
        cancelDelete() {
            this.showDeleteConfirm = false;
            this.deleteTargetId = null;
        },
        viewNotification(notification) {
            this.selectedNotification = notification;
            this.showDetailModal = true;
        },
        closeCreateModal() {
            this.showCreateModal = false;
            this.createForm = {
                userId: '',
                title: '',
                content: '',
                relatedId: '',
                relatedType: ''
            };
        },
        closeDetailModal() {
            this.showDetailModal = false;
            this.selectedNotification = {};
        },
        prevPage() {
            if (this.currentPage > 1) {
                this.currentPage--;
                this.loadNotifications();
            }
        },
        nextPage() {
            if (this.currentPage < this.totalPages) {
                this.currentPage++;
                this.loadNotifications();
            }
        },
        formatTime(timeString) {
            if (!timeString) return '';
            const date = new Date(timeString);
            return date.toLocaleString('zh-CN');
        }
    }
};

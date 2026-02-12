const NotificationComponent = {
    template: `
        <div class="notification-container">
            <div class="notification-bell" @click="toggleNotifications" :class="{ 'has-unread': unreadCount > 0 }">
                <span class="bell-icon">🔔</span>
                <span v-if="unreadCount > 0" class="unread-badge">{{ unreadCount > 99 ? '99+' : unreadCount }}</span>
            </div>
            
            <div v-if="showNotifications" class="notification-dropdown">
                <div class="notification-header">
                    <h3>通知</h3>
                    <button v-if="unreadCount > 0" @click="markAllAsRead" class="mark-read-btn">全部已读</button>
                </div>
                
                <div class="notification-list">
                    <div v-if="loading" class="loading">加载中...</div>
                    <div v-else-if="notifications.length === 0" class="empty">暂无通知</div>
                    <div v-else>
                        <div 
                            v-for="notification in notifications" 
                            :key="notification.id"
                            class="notification-item"
                            :class="{ 'unread': !notification.isRead }"
                            @click="handleNotificationClick(notification)"
                        >
                            <div class="notification-icon">
                                <span v-if="notification.type === 1">💬</span>
                                <span v-else-if="notification.type === 2">❤️</span>
                                <span v-else-if="notification.type === 3">📝</span>
                                <span v-else>📢</span>
                            </div>
                            <div class="notification-content">
                                <div class="notification-title">{{ notification.title }}</div>
                                <div class="notification-text">{{ notification.content }}</div>
                                <div class="notification-time">{{ formatTime(notification.createTime) }}</div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <div v-if="notifications.length > 0" class="notification-footer">
                    <a @click="loadMore" class="load-more">查看更多</a>
                </div>
            </div>
        </div>
    `,
    data() {
        return {
            showNotifications: false,
            notifications: [],
            unreadCount: 0,
            loading: false,
            currentPage: 0,
            pageSize: 10
        };
    },
    mounted() {
        this.setupWebSocket();
        this.loadUnreadCount();
        this.loadNotifications();
        
        document.addEventListener('click', this.handleClickOutside);
    },
    beforeDestroy() {
        document.removeEventListener('click', this.handleClickOutside);
        if (wsClient.isConnected()) {
            wsClient.removeListener('message', this.handleWebSocketMessage);
        }
    },
    methods: {
        setupWebSocket() {
            const user = JSON.parse(localStorage.getItem('user'));
            if (user && user.id) {
                wsClient.connect(user.id);
                wsClient.addListener('message', this.handleWebSocketMessage);
            }
        },
        
        handleWebSocketMessage(message) {
            console.log('收到WebSocket消息:', message);
            const notification = message.data || message;
            console.log('解析后的通知:', notification);
            this.unreadCount++;
            this.notifications.unshift(notification);
            this.showNotificationToast(notification);
        },
        
        showNotificationToast(notification) {
            const toast = document.createElement('div');
            toast.className = 'notification-toast';
            toast.innerHTML = `
                <div class="toast-content">
                    <div class="toast-icon">🔔</div>
                    <div class="toast-message">
                        <div class="toast-title">${notification.title}</div>
                        <div class="toast-text">${notification.content}</div>
                    </div>
                    <button class="toast-close">&times;</button>
                </div>
            `;
            
            document.body.appendChild(toast);
            
            const closeBtn = toast.querySelector('.toast-close');
            closeBtn.onclick = () => {
                toast.remove();
            };
            
            setTimeout(() => {
                toast.classList.add('show');
            }, 10);
            
            setTimeout(() => {
                toast.classList.remove('show');
                setTimeout(() => toast.remove(), 300);
            }, 5000);
        },
        
        toggleNotifications() {
            this.showNotifications = !this.showNotifications;
            if (this.showNotifications) {
                this.loadNotifications();
            }
        },
        
        handleClickOutside(event) {
            const container = this.$el;
            if (container && !container.contains(event.target)) {
                this.showNotifications = false;
            }
        },
        
        async loadUnreadCount() {
            const user = JSON.parse(localStorage.getItem('user'));
            if (!user) return;
            
            try {
                const response = await axios.get(`http://localhost:8070/api/notification/unread/count/${user.id}`);
                this.unreadCount = response.data || 0;
            } catch (error) {
                console.error('加载未读通知数量失败:', error);
            }
        },
        
        async loadNotifications() {
            const user = JSON.parse(localStorage.getItem('user'));
            if (!user) return;
            
            this.loading = true;
            try {
                const response = await axios.get(`http://localhost:8070/api/notification/user/${user.id}`, {
                    params: {
                        page: this.currentPage,
                        size: this.pageSize
                    }
                });
                if (this.currentPage === 0) {
                    this.notifications = response.data || [];
                } else {
                    this.notifications = [...this.notifications, ...(response.data || [])];
                }
            } catch (error) {
                console.error('加载通知列表失败:', error);
            } finally {
                this.loading = false;
            }
        },
        
        async markAllAsRead() {
            const user = JSON.parse(localStorage.getItem('user'));
            if (!user) return;
            
            try {
                await axios.put(`http://localhost:8070/api/notification/read/all/${user.id}`);
                this.notifications.forEach(n => n.isRead = 1);
                this.unreadCount = 0;
            } catch (error) {
                console.error('标记全部已读失败:', error);
            }
        },
        
        async handleNotificationClick(notification) {
            if (!notification.isRead) {
                try {
                    await axios.put(`http://localhost:8070/api/notification/read/${notification.id}`);
                    notification.isRead = 1;
                    this.unreadCount--;
                } catch (error) {
                    console.error('标记已读失败:', error);
                }
            }
            
            if (notification.relatedId) {
                window.location.href = `article.html?id=${notification.relatedId}`;
            }
        },
        
        loadMore() {
            this.currentPage++;
            this.loadNotifications();
        },
        
        formatTime(timeString) {
            if (!timeString) return '';
            const date = new Date(timeString);
            const now = new Date();
            const diff = now - date;
            
            const minutes = Math.floor(diff / 60000);
            const hours = Math.floor(diff / 3600000);
            const days = Math.floor(diff / 86400000);
            
            if (minutes < 1) return '刚刚';
            if (minutes < 60) return `${minutes}分钟前`;
            if (hours < 24) return `${hours}小时前`;
            if (days < 7) return `${days}天前`;
            
            return date.toLocaleDateString('zh-CN');
        }
    }
};
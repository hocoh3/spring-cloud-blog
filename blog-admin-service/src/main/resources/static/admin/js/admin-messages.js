const MessagesComponent = {
    template: `
        <div class="messages-management">
            <div class="management-header">
                <h2>私信管理</h2>
                <div class="header-actions">
                    <button @click="showCreateModal = true" class="btn btn-primary">发送私信</button>
                    <button @click="loadMessages" class="btn btn-secondary">刷新</button>
                </div>
            </div>

            <div class="search-bar">
                <input type="text" v-model="searchKeyword" placeholder="搜索私信..." @keyup.enter="loadMessages">
                <select v-model="filterStatus" @change="loadMessages">
                    <option value="">全部状态</option>
                    <option value="0">未读</option>
                    <option value="1">已读</option>
                </select>
                <select v-model="filterUser" @change="loadMessages">
                    <option value="">全部用户</option>
                    <option v-for="user in uniqueUsers" :key="user" :value="user">用户ID: {{ user }}</option>
                </select>
            </div>

            <div v-if="loading" class="loading">加载中...</div>
            <div v-else-if="error" class="error-message">{{ error }}</div>
            <div v-else class="messages-list">
                <div v-if="messages.length === 0" class="no-data">暂无私信</div>
                <div v-for="message in filteredMessages" :key="message.id" class="message-item">
                    <div class="message-header">
                        <div class="message-avatar">
                            <img :src="getAvatarUrl(message.senderAvatar)" alt="头像">
                        </div>
                        <div class="message-info">
                            <div class="message-sender">{{ message.senderName }}</div>
                            <div class="message-meta">
                                <span class="message-from">发送者ID: {{ message.senderId }}</span>
                                <span class="message-to">接收者ID: {{ message.receiverId }}</span>
                                <span class="message-time">{{ formatTime(message.createTime) }}</span>
                                <span class="message-status" :class="'status-' + message.isRead">
                                    {{ message.isRead === 0 ? '未读' : '已读' }}
                                </span>
                            </div>
                        </div>
                    </div>
                    <div class="message-content">{{ message.content }}</div>
                    <div class="message-actions">
                        <button @click.stop.prevent="viewMessage(message)" class="btn btn-sm btn-info">查看详情</button>
                        <button @click.stop.prevent="markAsRead(message)" v-if="message.isRead === 0" class="btn btn-sm btn-success">标记已读</button>
                        <button @click.stop.prevent="deleteMessage(message.id)" class="btn btn-sm btn-danger">删除</button>
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
                        <h3>发送私信</h3>
                        <button @click="closeCreateModal" class="close-btn">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="senderId">发送者ID:</label>
                            <input type="number" id="senderId" v-model="createForm.senderId" required>
                        </div>
                        <div class="form-group">
                            <label for="receiverId">接收者ID:</label>
                            <input type="number" id="receiverId" v-model="createForm.receiverId" required>
                        </div>
                        <div class="form-group">
                            <label for="content">内容:</label>
                            <textarea id="content" v-model="createForm.content" rows="5" required></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button @click="closeCreateModal" class="btn btn-secondary">取消</button>
                        <button @click="createMessage" class="btn btn-primary" :disabled="createLoading">
                            {{ createLoading ? '发送中...' : '发送' }}
                        </button>
                    </div>
                </div>
            </div>

            <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
                <div class="modal-content" @click.stop>
                    <div class="modal-header">
                        <h3>私信详情</h3>
                        <button @click="closeDetailModal" class="close-btn">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="detail-item">
                            <label>发送者:</label>
                            <div class="user-info">
                                <img :src="getAvatarUrl(selectedMessage.senderAvatar)" alt="头像">
                                <span>{{ selectedMessage.senderName }}</span>
                            </div>
                        </div>
                        <div class="detail-item">
                            <label>发送者ID:</label>
                            <span>{{ selectedMessage.senderId }}</span>
                        </div>
                        <div class="detail-item">
                            <label>接收者ID:</label>
                            <span>{{ selectedMessage.receiverId }}</span>
                        </div>
                        <div class="detail-item">
                            <label>内容:</label>
                            <span>{{ selectedMessage.content }}</span>
                        </div>
                        <div class="detail-item">
                            <label>状态:</label>
                            <span :class="'status-' + selectedMessage.isRead">
                                {{ selectedMessage.isRead === 0 ? '未读' : '已读' }}
                            </span>
                        </div>
                        <div class="detail-item">
                            <label>创建时间:</label>
                            <span>{{ formatTime(selectedMessage.createTime) }}</span>
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
                        <p>确定要删除这条私信吗？</p>
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
            messages: [],
            loading: false,
            error: '',
            currentPage: 1,
            pageSize: 10,
            totalPages: 0,
            searchKeyword: '',
            filterStatus: '',
            filterUser: '',
            showCreateModal: false,
            showDetailModal: false,
            showDeleteConfirm: false,
            selectedMessage: {},
            deleteTargetId: null,
            createForm: {
                senderId: '',
                receiverId: '',
                content: ''
            },
            createLoading: false
        };
    },
    computed: {
        filteredMessages() {
            let filtered = this.messages;
            
            if (this.searchKeyword) {
                const keyword = this.searchKeyword.toLowerCase();
                filtered = filtered.filter(m => 
                    m.content.toLowerCase().includes(keyword) ||
                    m.senderName.toLowerCase().includes(keyword)
                );
            }
            
            if (this.filterStatus !== '') {
                filtered = filtered.filter(m => m.isRead === parseInt(this.filterStatus));
            }
            
            if (this.filterUser !== '') {
                filtered = filtered.filter(m => 
                    m.senderId === parseInt(this.filterUser) || 
                    m.receiverId === parseInt(this.filterUser)
                );
            }
            
            return filtered;
        },
        uniqueUsers() {
            const users = new Set();
            this.messages.forEach(m => {
                users.add(m.senderId);
                users.add(m.receiverId);
            });
            return Array.from(users).sort((a, b) => a - b);
        }
    },
    mounted() {
        this.loadMessages();
    },
    methods: {
        async loadMessages() {
            this.loading = true;
            this.error = '';
            try {
                const response = await axios.get('http://localhost:8070/api/message/messages', {
                    params: {
                        page: this.currentPage - 1,
                        size: this.pageSize
                    },
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
                    }
                });
                if (response.data) {
                    this.messages = response.data;
                    this.totalPages = Math.ceil(this.messages.length / this.pageSize);
                }
            } catch (error) {
                console.error('加载私信失败:', error);
                this.error = '加载私信失败，请稍后重试';
            } finally {
                this.loading = false;
            }
        },
        async createMessage() {
            if (!this.createForm.senderId || !this.createForm.receiverId || !this.createForm.content) {
                alert('请填写必填项');
                return;
            }
            
            this.createLoading = true;
            try {
                const messageData = {
                    senderId: parseInt(this.createForm.senderId),
                    receiverId: parseInt(this.createForm.receiverId),
                    content: this.createForm.content
                };
                await axios.post('http://localhost:8070/api/message/create', messageData, {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
                    }
                });
                alert('私信发送成功');
                this.closeCreateModal();
                this.loadMessages();
            } catch (error) {
                console.error('发送私信失败:', error);
                alert('发送私信失败，请稍后重试');
            } finally {
                this.createLoading = false;
            }
        },
        async markAsRead(message) {
            try {
                await axios.put('http://localhost:8070/api/message/read/' + message.id, {}, {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
                    }
                });
                message.isRead = 1;
                alert('标记已读成功');
            } catch (error) {
                console.error('标记已读失败:', error);
                alert('标记已读失败，请稍后重试');
            }
        },
        deleteMessage(id) {
            this.deleteTargetId = id;
            this.showDeleteConfirm = true;
        },
        async confirmDelete() {
            if (!this.deleteTargetId) return;
            
            try {
                await axios.delete('http://localhost:8070/api/message/' + this.deleteTargetId, {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
                    }
                });
                alert('删除成功');
                this.cancelDelete();
                this.loadMessages();
            } catch (error) {
                console.error('删除私信失败:', error);
                alert('删除私信失败，请稍后重试');
            }
        },
        cancelDelete() {
            this.showDeleteConfirm = false;
            this.deleteTargetId = null;
        },
        viewMessage(message) {
            this.selectedMessage = message;
            this.showDetailModal = true;
        },
        closeCreateModal() {
            this.showCreateModal = false;
            this.createForm = {
                senderId: '',
                receiverId: '',
                content: ''
            };
        },
        closeDetailModal() {
            this.showDetailModal = false;
            this.selectedMessage = {};
        },
        prevPage() {
            if (this.currentPage > 1) {
                this.currentPage--;
                this.loadMessages();
            }
        },
        nextPage() {
            if (this.currentPage < this.totalPages) {
                this.currentPage++;
                this.loadMessages();
            }
        },
        formatTime(timeString) {
            if (!timeString) return '';
            const date = new Date(timeString);
            return date.toLocaleString('zh-CN');
        },
        getAvatarUrl(avatar) {
            if (!avatar) {
                return '/admin/assets/images/default-avatar.png';
            }
            if (avatar.startsWith('http://') || avatar.startsWith('https://')) {
                return avatar;
            }
            return 'http://localhost:8070' + avatar;
        }
    }
};

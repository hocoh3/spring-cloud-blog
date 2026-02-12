const UsersComponent = {
    template: `
        <div class="users-page">
            <div class="page-header">
                <h2>用户管理</h2>
            </div>

            <div class="search-bar">
                <input 
                    type="text" 
                    v-model="searchKeyword" 
                    placeholder="搜索用户名、邮箱或昵称"
                    @keyup.enter="loadUsers"
                >
                <button @click="loadUsers">搜索</button>
            </div>

            <div class="data-table">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>用户名</th>
                            <th>昵称</th>
                            <th>邮箱</th>
                            <th>角色</th>
                            <th>状态</th>
                            <th>注册时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="user in users" :key="user.id">
                            <td>{{ user.id }}</td>
                            <td>{{ user.username }}</td>
                            <td>{{ user.nickname }}</td>
                            <td>{{ user.email }}</td>
                            <td>{{ user.role }}</td>
                            <td>
                                <span class="status-badge" :class="user.status === 1 ? 'active' : 'inactive'">
                                    {{ user.status === 1 ? '正常' : '禁用' }}
                                </span>
                            </td>
                            <td>{{ formatDate(user.createTime) }}</td>
                            <td>
                                <div class="table-actions">
                                    <button 
                                        class="btn btn-sm" 
                                        :class="user.status === 1 ? 'btn-danger' : 'btn-success'"
                                        @click="toggleUserStatus(user)"
                                    >
                                        {{ user.status === 1 ? '禁用' : '启用' }}
                                    </button>
                                    <button 
                                        class="btn btn-sm btn-primary" 
                                        @click="viewUser(user)"
                                    >
                                        查看
                                    </button>
                                </div>
                            </td>
                        </tr>
                        <tr v-if="users.length === 0">
                            <td colspan="8" class="empty-state">暂无用户数据</td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="pagination">
                <button 
                    @click="changePage(currentPage - 1)" 
                    :disabled="currentPage <= 1"
                >
                    上一页
                </button>
                <span class="page-info">第 {{ currentPage }} 页，共 {{ totalPages }} 页</span>
                <button 
                    @click="changePage(currentPage + 1)" 
                    :disabled="currentPage >= totalPages"
                >
                    下一页
                </button>
            </div>

            <div v-if="showUserModal" class="modal-overlay" @click.self="closeUserModal">
                <div class="modal">
                    <div class="modal-header">
                        <h3>用户详情</h3>
                        <button class="modal-close" @click="closeUserModal">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label>用户名</label>
                            <input type="text" v-model="selectedUser.username" disabled>
                        </div>
                        <div class="form-group">
                            <label>昵称</label>
                            <input type="text" v-model="selectedUser.nickname" disabled>
                        </div>
                        <div class="form-group">
                            <label>邮箱</label>
                            <input type="text" v-model="selectedUser.email" disabled>
                        </div>
                        <div class="form-group">
                            <label>个人简介</label>
                            <textarea v-model="selectedUser.bio" disabled></textarea>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>角色</label>
                                <input type="text" v-model="selectedUser.role" disabled>
                            </div>
                            <div class="form-group">
                                <label>状态</label>
                                <input type="text" :value="selectedUser.status === 1 ? '正常' : '禁用'" disabled>
                            </div>
                        </div>
                        <div class="form-row">
                            <div class="form-group">
                                <label>注册时间</label>
                                <input type="text" :value="formatDate(selectedUser.createTime)" disabled>
                            </div>
                            <div class="form-group">
                                <label>更新时间</label>
                                <input type="text" :value="formatDate(selectedUser.updateTime)" disabled>
                            </div>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-primary" @click="closeUserModal">关闭</button>
                    </div>
                </div>
            </div>
        </div>
    `,
    data() {
        return {
            users: [],
            currentPage: 1,
            pageSize: 10,
            totalPages: 1,
            searchKeyword: '',
            showUserModal: false,
            selectedUser: {},
            loading: false
        };
    },
    mounted() {
        this.loadUsers();
    },
    methods: {
        async loadUsers() {
            this.loading = true;
            try {
                const response = await axios.get('/admin/users', {
                    params: {
                        page: this.currentPage,
                        size: this.pageSize
                    }
                });
                this.users = response.data.records;
                this.totalPages = Math.ceil(response.data.total / this.pageSize);
            } catch (error) {
                console.error('加载用户列表失败:', error);
            } finally {
                this.loading = false;
            }
        },
        changePage(page) {
            if (page < 1 || page > this.totalPages) return;
            this.currentPage = page;
            this.loadUsers();
        },
        async toggleUserStatus(user) {
            const newStatus = user.status === 1 ? 0 : 1;
            try {
                await axios.put(`/admin/users/${user.id}/status`, null, {
                    params: { status: newStatus }
                });
                user.status = newStatus;
                alert(newStatus === 1 ? '用户已启用' : '用户已禁用');
            } catch (error) {
                console.error('更新用户状态失败:', error);
                alert('操作失败，请重试');
            }
        },
        viewUser(user) {
            this.selectedUser = { ...user };
            this.showUserModal = true;
        },
        closeUserModal() {
            this.showUserModal = false;
            this.selectedUser = {};
        },
        formatDate(dateString) {
            if (!dateString) return '-';
            const date = new Date(dateString);
            return date.toLocaleString('zh-CN');
        }
    }
};
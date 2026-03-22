const CommentsComponent = {
    template: `
        <div class="comments-management">
            <div class="page-header">
                <h2>评论管理</h2>
            </div>

            <div class="comments-stats">
                <div class="stat-card">
                    <div class="stat-value">{{ commentStats.total || 0 }}</div>
                    <div class="stat-label">评论总数</div>
                </div>
            </div>

            <div class="comments-table-container">
                <table class="comments-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>文章ID</th>
                            <th>用户ID</th>
                            <th>评论内容</th>
                            <th>状态</th>
                            <th>创建时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-if="loading">
                            <td colspan="7" class="loading-cell">加载中...</td>
                        </tr>
                        <tr v-else-if="comments.length === 0">
                            <td colspan="7" class="empty-cell">暂无评论</td>
                        </tr>
                        <tr v-for="comment in comments" :key="comment.id">
                            <td>{{ comment.id }}</td>
                            <td>{{ comment.articleId }}</td>
                            <td>{{ comment.userId }}</td>
                            <td class="comment-content">{{ comment.content }}</td>
                            <td>
                                <span :class="['status-badge', getStatusClass(comment.status)]">
                                    {{ getStatusText(comment.status) }}
                                </span>
                            </td>
                            <td>{{ formatDateTime(comment.createTime) }}</td>
                            <td class="actions">
                                <button v-if="comment.status === 2" @click="approveComment(comment.id)" class="btn btn-sm btn-success">通过</button>
                                <button v-if="comment.status === 1" @click="deleteComment(comment.id)" class="btn btn-sm btn-danger">删除</button>
                                <button v-if="comment.status === 0" @click="restoreComment(comment.id)" class="btn btn-sm btn-warning">恢复</button>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>

            <div class="pagination" v-if="totalPages > 1">
                <button @click="loadComments(currentPage - 1)" :disabled="currentPage === 1" class="btn btn-sm">上一页</button>
                <span>第 {{ currentPage }} 页 / 共 {{ totalPages }} 页</span>
                <button @click="loadComments(currentPage + 1)" :disabled="currentPage === totalPages" class="btn btn-sm">下一页</button>
            </div>
        </div>
    `,
    data() {
        return {
            comments: [],
            commentStats: {},
            loading: false,
            currentPage: 1,
            pageSize: 10,
            totalPages: 1
        };
    },
    mounted() {
        this.loadComments();
        this.loadCommentStats();
    },
    methods: {
        async loadComments(page = 1) {
            this.currentPage = page;
            this.loading = true;
            try {
                const response = await axios.get('/admin/comments/all', {
                    params: {
                        page: page,
                        size: this.pageSize
                    },
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
                    }
                });
                if (response.data && response.data.records) {
                    this.comments = response.data.records;
                    this.totalPages = response.data.pages || 1;
                }
            } catch (error) {
                console.error('加载评论列表失败:', error);
                alert('加载评论列表失败');
            } finally {
                this.loading = false;
            }
        },
        async loadCommentStats() {
            try {
                const response = await axios.get('/admin/comments/count', {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
                    }
                });
                if (response.data) {
                    this.commentStats = response.data;
                }
            } catch (error) {
                console.error('加载评论统计失败:', error);
            }
        },
        async approveComment(id) {
            if (!confirm('确定要审核通过这条评论吗？')) return;
            try {
                await axios.put('/admin/comments/' + id + '/status', { status: 1 }, {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
                    }
                });
                this.loadComments(this.currentPage);
                this.loadCommentStats();
            } catch (error) {
                console.error('审核评论失败:', error);
                alert('审核评论失败');
            }
        },
        async deleteComment(id) {
            if (!confirm('确定要删除这条评论吗？')) return;
            try {
                await axios.delete('/admin/comments/' + id, {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
                    }
                });
                this.loadComments(this.currentPage);
                this.loadCommentStats();
            } catch (error) {
                console.error('删除评论失败:', error);
                alert('删除评论失败');
            }
        },
        async restoreComment(id) {
            if (!confirm('确定要恢复这条评论吗？')) return;
            try {
                await axios.put('/admin/comments/' + id + '/status', { status: 1 }, {
                    headers: {
                        'Authorization': 'Bearer ' + localStorage.getItem('adminToken')
                    }
                });
                this.loadComments(this.currentPage);
                this.loadCommentStats();
            } catch (error) {
                console.error('恢复评论失败:', error);
                alert('恢复评论失败');
            }
        },
        getStatusText(status) {
            const statusMap = {
                0: '已删除',
                1: '正常',
                2: '待审核'
            };
            return statusMap[status] || '未知';
        },
        getStatusClass(status) {
            const classMap = {
                0: 'status-deleted',
                1: 'status-normal',
                2: 'status-pending'
            };
            return classMap[status] || '';
        },
        formatDateTime(dateTime) {
            if (!dateTime) return '';
            const date = new Date(dateTime);
            return date.toLocaleString('zh-CN');
        }
    }
};
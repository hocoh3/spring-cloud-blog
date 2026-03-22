const ReviewComponent = {
    template: `
        <div class="review-page">
            <div class="page-header">
                <h2>内容审核</h2>
            </div>

            <div class="filter-bar">
                <select v-model="reviewStatus" @change="loadReviews">
                    <option value="">全部状态</option>
                    <option value="1">待审核</option>
                    <option value="2">已发布</option>
                    <option value="3">已拒绝</option>
                </select>
                <input 
                    type="text" 
                    v-model="searchKeyword" 
                    placeholder="搜索文章标题"
                    @keyup.enter="loadReviews"
                >
                <button @click="loadReviews">搜索</button>
            </div>

            <div class="data-table">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>文章标题</th>
                            <th>作者ID</th>
                            <th>提交时间</th>
                            <th>审核状态</th>
                            <th>置顶</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="review in reviews" :key="review.id">
                            <td>{{ review.id }}</td>
                            <td>
                                {{ review.title }}
                                <span v-if="review.isTop === 1" class="top-badge">已置顶</span>
                            </td>
                            <td>{{ review.userId }}</td>
                            <td>{{ formatDate(review.createTime) }}</td>
                            <td>
                                <span class="status-badge" :class="getStatusClass(review.status)">
                                    {{ getStatusText(review.status) }}
                                </span>
                            </td>
                            <td>
                                <span v-if="review.isTop === 1" class="top-status">是</span>
                                <span v-else class="top-status">否</span>
                            </td>
                            <td>
                                <div class="table-actions">
                                    <button 
                                        class="btn btn-sm btn-primary" 
                                        @click="viewReview(review)"
                                    >
                                        查看
                                    </button>
                                    <button 
                                        v-if="review.status === 1"
                                        class="btn btn-sm btn-success" 
                                        @click="viewReview(review)"
                                    >
                                        审核
                                    </button>
                                    <button 
                                        v-if="review.status === 2"
                                        class="btn btn-sm btn-warning" 
                                        @click="toggleTop(review)"
                                    >
                                        {{ review.isTop === 1 ? '取消置顶' : '置顶' }}
                                    </button>
                                    <button 
                                        class="btn btn-sm btn-danger" 
                                        @click="deleteArticle(review)"
                                    >
                                        删除
                                    </button>
                                </div>
                            </td>
                        </tr>
                        <tr v-if="reviews.length === 0">
                            <td colspan="7" class="empty-state">暂无审核数据</td>
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

            <div v-if="showReviewModal" class="modal-overlay" @click.self="closeReviewModal">
                <div class="modal">
                    <div class="modal-header">
                        <h3>文章审核</h3>
                        <button class="modal-close" @click="closeReviewModal">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label>文章标题</label>
                            <input type="text" v-model="selectedReview.title" disabled>
                        </div>
                        <div class="form-group">
                            <label>作者ID</label>
                            <input type="text" v-model="selectedReview.userId" disabled>
                        </div>
                        <div class="form-group">
                            <label>文章摘要</label>
                            <textarea v-model="selectedReview.summary" disabled></textarea>
                        </div>
                        <div class="form-group">
                            <label>文章内容</label>
                            <textarea v-model="selectedReview.content" disabled></textarea>
                        </div>
                        <div class="form-group">
                            <label>审核意见</label>
                            <textarea 
                                v-model="reviewComment" 
                                placeholder="请输入审核意见"
                                :disabled="selectedReview.status !== 1"
                            ></textarea>
                        </div>
                        <div v-if="selectedReview.status !== 1" class="form-group">
                            <label>审核结果</label>
                            <input 
                                type="text" 
                                :value="getStatusText(selectedReview.status)" 
                                disabled
                            >
                        </div>
                    </div>
                    <div class="modal-footer" v-if="selectedReview.status === 1">
                        <button class="btn btn-danger" @click="handleReject">拒绝</button>
                        <button class="btn btn-success" @click="handleApprove">通过</button>
                    </div>
                    <div class="modal-footer" v-else>
                        <button class="btn btn-primary" @click="closeReviewModal">关闭</button>
                    </div>
                </div>
            </div>
        </div>
    `,
    data() {
        return {
            reviews: [],
            currentPage: 1,
            pageSize: 10,
            totalPages: 1,
            reviewStatus: '1',
            searchKeyword: '',
            showReviewModal: false,
            selectedReview: {},
            reviewComment: '',
            loading: false
        };
    },
    mounted() {
        this.loadReviews();
    },
    methods: {
        async loadReviews() {
            this.loading = true;
            try {
                let response;
                if (this.searchKeyword && this.searchKeyword.trim()) {
                    response = await api.searchArticles(
                        this.searchKeyword.trim(),
                        this.currentPage,
                        this.pageSize
                    );
                    if (response.data && response.data.content) {
                        this.reviews = response.data.content;
                        this.totalPages = response.data.totalPages || 1;
                    } else if (response.data && response.data.records) {
                        this.reviews = response.data.records;
                        this.totalPages = Math.ceil(response.data.total / this.pageSize);
                    } else {
                        this.reviews = [];
                        this.totalPages = 1;
                    }
                } else if (this.reviewStatus === '1') {
                    response = await api.getPendingReviewArticles(
                        this.currentPage,
                        this.pageSize
                    );
                    this.reviews = response.data.records;
                    this.totalPages = Math.ceil(response.data.total / this.pageSize);
                } else if (this.reviewStatus === '2' || this.reviewStatus === '3' || this.reviewStatus === '') {
                    const statusParam = this.reviewStatus === '' ? null : parseInt(this.reviewStatus);
                    response = await api.getArticlesByStatus(
                        this.currentPage,
                        this.pageSize,
                        statusParam
                    );
                    this.reviews = response.data.records;
                    this.totalPages = Math.ceil(response.data.total / this.pageSize);
                } else {
                    this.reviews = [];
                    this.totalPages = 1;
                }
            } catch (error) {
                console.error('加载审核列表失败:', error);
                alert('加载审核列表失败');
            } finally {
                this.loading = false;
            }
        },
        async toggleTop(review) {
            const newTopStatus = review.isTop === 1 ? 0 : 1;
            const action = newTopStatus === 1 ? '置顶' : '取消置顶';
            
            if (!confirm(`确定要${action}文章《${review.title}》吗？`)) {
                return;
            }
            
            try {
                await api.toggleArticleTop(review.id, newTopStatus);
                alert(`${action}成功`);
                this.loadReviews();
            } catch (error) {
                console.error(`${action}失败:`, error);
                alert(`${action}失败，请重试`);
            }
        },
        changePage(page) {
            if (page < 1 || page > this.totalPages) return;
            this.currentPage = page;
            this.loadReviews();
        },
        viewReview(review) {
            this.selectedReview = { ...review };
            this.reviewComment = '';
            this.showReviewModal = true;
        },
        async handleApprove() {
            try {
                await api.approveArticle(this.selectedReview.id, this.reviewComment);
                alert('审核通过');
                this.closeReviewModal();
                this.loadReviews();
            } catch (error) {
                console.error('审核失败:', error);
                alert('审核失败，请重试');
            }
        },
        async handleReject() {
            if (!this.reviewComment.trim()) {
                alert('请输入拒绝原因');
                return;
            }
            try {
                await api.rejectArticle(this.selectedReview.id, this.reviewComment);
                alert('审核拒绝');
                this.closeReviewModal();
                this.loadReviews();
            } catch (error) {
                console.error('审核失败:', error);
                alert('审核失败，请重试');
            }
        },
        closeReviewModal() {
            this.showReviewModal = false;
            this.selectedReview = {};
            this.reviewComment = '';
        },
        getStatusClass(status) {
            const statusMap = {
                0: 'draft',
                1: 'pending',
                2: 'approved',
                3: 'rejected'
            };
            return statusMap[status] || '';
        },
        getStatusText(status) {
            const statusMap = {
                0: '草稿',
                1: '待审核',
                2: '已发布',
                3: '已拒绝'
            };
            return statusMap[status] || '未知';
        },
        async deleteArticle(review) {
            if (!confirm(`确定要删除文章《${review.title}》吗？`)) {
                return;
            }
            try {
                await api.deleteArticle(review.id);
                alert('删除成功');
                this.loadReviews();
            } catch (error) {
                console.error('删除文章失败:', error);
                alert('删除文章失败，请重试');
            }
        },
        formatDate(dateString) {
            if (!dateString) return '-';
            const date = new Date(dateString);
            return date.toLocaleString('zh-CN');
        }
    }
};
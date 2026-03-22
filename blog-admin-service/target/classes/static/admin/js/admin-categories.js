const CategoriesComponent = {
    template: `
        <div class="categories-page">
            <div class="page-header">
                <h2>分类管理</h2>
                <button @click="showAddModal = true" class="btn btn-primary">
                    添加分类
                </button>
            </div>

            <div class="search-bar">
                <input 
                    type="text" 
                    v-model="searchKeyword" 
                    placeholder="搜索分类名称"
                    @keyup.enter="searchCategories"
                >
                <button @click="searchCategories">搜索</button>
            </div>

            <div class="data-table">
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>分类名称</th>
                            <th>分类简介</th>
                            <th>文章数量</th>
                            <th>创建时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="category in categories" :key="category.id">
                            <td>{{ category.id }}</td>
                            <td>{{ category.name }}</td>
                            <td>{{ category.description || '-' }}</td>
                            <td>{{ category.articleCount || 0 }}</td>
                            <td>{{ formatDate(category.createTime) }}</td>
                            <td>
                                <div class="table-actions">
                                    <button 
                                        class="btn btn-sm btn-primary" 
                                        @click="editCategory(category)"
                                    >
                                        编辑
                                    </button>
                                    <button 
                                        class="btn btn-sm btn-danger" 
                                        @click="deleteCategory(category)"
                                        :disabled="category.articleCount > 0"
                                    >
                                        删除
                                    </button>
                                </div>
                            </td>
                        </tr>
                        <tr v-if="categories.length === 0">
                            <td colspan="6" class="empty-state">暂无分类数据</td>
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

            <div v-if="showAddModal" class="modal-overlay" @click.self="closeAddModal">
                <div class="modal">
                    <div class="modal-header">
                        <h3>{{ isEditing ? '编辑分类' : '添加分类' }}</h3>
                        <button class="modal-close" @click="closeAddModal">&times;</button>
                    </div>
                    <div class="modal-body">
                        <div class="form-group">
                            <label for="categoryName">分类名称</label>
                            <input 
                                type="text" 
                                id="categoryName" 
                                v-model="categoryForm.name" 
                                required
                            >
                        </div>
                        <div class="form-group">
                            <label for="categoryDescription">分类简介</label>
                            <textarea 
                                id="categoryDescription" 
                                v-model="categoryForm.description" 
                                rows="3"
                                placeholder="请输入分类简介（可选）"
                            ></textarea>
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button class="btn btn-primary" @click="saveCategory">保存</button>
                        <button class="btn" @click="closeAddModal">取消</button>
                    </div>
                </div>
            </div>
        </div>
    `,
    data() {
        return {
            categories: [],
            currentPage: 1,
            pageSize: 10,
            totalPages: 1,
            searchKeyword: '',
            showAddModal: false,
            isEditing: false,
            categoryForm: {
                id: null,
                name: '',
                description: ''
            }
        };
    },
    mounted() {
        this.loadCategories();
    },
    methods: {
        async loadCategories() {
            try {
                const params = {
                    page: this.currentPage,
                    size: this.pageSize
                };
                if (this.searchKeyword && this.searchKeyword.trim()) {
                    params.keyword = this.searchKeyword.trim();
                }
                const response = await axios.get('/admin/categories', { params });
                this.categories = response.data.records;
                this.totalPages = Math.ceil(response.data.total / this.pageSize);
            } catch (error) {
                console.error('加载分类列表失败:', error);
            }
        },
        changePage(page) {
            if (page < 1 || page > this.totalPages) return;
            this.currentPage = page;
            this.loadCategories();
        },
        searchCategories() {
            this.currentPage = 1;
            this.loadCategories();
        },
        editCategory(category) {
            this.isEditing = true;
            this.categoryForm = { ...category };
            this.showAddModal = true;
        },
        async saveCategory() {
            if (!this.categoryForm.name.trim()) {
                alert('分类名称不能为空');
                return;
            }
            try {
                if (this.isEditing) {
                    await axios.put(`/admin/categories/${this.categoryForm.id}`, this.categoryForm);
                    alert('分类更新成功');
                } else {
                    await axios.post('/admin/categories', this.categoryForm);
                    alert('分类添加成功');
                }
                this.closeAddModal();
                this.loadCategories();
            } catch (error) {
                console.error('保存分类失败:', error);
                alert('操作失败，请重试');
            }
        },
        async deleteCategory(category) {
            if (category.articleCount > 0) {
                alert('该分类下有文章，无法删除');
                return;
            }
            if (!confirm('确定要删除这个分类吗？')) {
                return;
            }
            try {
                await axios.delete(`/admin/categories/${category.id}`);
                this.categories = this.categories.filter(c => c.id !== category.id);
                alert('分类删除成功');
            } catch (error) {
                console.error('删除分类失败:', error);
                alert('操作失败，请重试');
            }
        },
        closeAddModal() {
            this.showAddModal = false;
            this.isEditing = false;
            this.categoryForm = {
                id: null,
                name: '',
                description: ''
            };
        },
        formatDate(dateString) {
            if (!dateString) return '-';
            const date = new Date(dateString);
            return date.toLocaleString('zh-CN');
        }
    }
};
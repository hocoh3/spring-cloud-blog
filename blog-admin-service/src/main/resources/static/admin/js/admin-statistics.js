const StatisticsComponent = {
    template: `
        <div class="statistics-page">
            <div class="page-header">
                <h2>数据统计</h2>
            </div>

            <div class="filter-bar">
                <label>开始日期：</label>
                <input type="date" v-model="startDate">
                <label>结束日期：</label>
                <input type="date" v-model="endDate">
                <button @click="loadStatistics">查询</button>
            </div>

            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">用户总数</div>
                    <div class="stat-value">{{ totalUsers }}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">文章总数</div>
                    <div class="stat-value">{{ totalArticles }}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">评论总数</div>
                    <div class="stat-value">{{ totalComments }}</div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">访问总数</div>
                    <div class="stat-value">{{ totalViews }}</div>
                </div>
            </div>

            <div class="data-table">
                <div class="table-header">
                    <h3>统计数据详情</h3>
                </div>
                <table>
                    <thead>
                        <tr>
                            <th>统计日期</th>
                            <th>用户数</th>
                            <th>文章数</th>
                            <th>评论数</th>
                            <th>访问数</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="stat in statistics" :key="stat.id">
                            <td>{{ stat.statDate }}</td>
                            <td>{{ stat.userCount }}</td>
                            <td>{{ stat.articleCount }}</td>
                            <td>{{ stat.commentCount }}</td>
                            <td>{{ stat.viewCount }}</td>
                        </tr>
                        <tr v-if="statistics.length === 0">
                            <td colspan="5" class="empty-state">暂无统计数据</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    `,
    data() {
        return {
            statistics: [],
            startDate: '',
            endDate: '',
            totalUsers: 0,
            totalArticles: 0,
            totalComments: 0,
            totalViews: 0,
            loading: false
        };
    },
    mounted() {
        this.setDefaultDates();
        this.loadStatistics();
    },
    methods: {
        setDefaultDates() {
            const endDate = new Date();
            const startDate = new Date();
            startDate.setDate(startDate.getDate() - 30);
            
            this.endDate = endDate.toISOString().split('T')[0];
            this.startDate = startDate.toISOString().split('T')[0];
        },
        async loadStatistics() {
            this.loading = true;
            try {
                const response = await api.getStatisticsTrend(this.startDate, this.endDate);
                this.statistics = response.data;
                this.calculateTotals();
            } catch (error) {
                console.error('加载统计数据失败:', error);
            } finally {
                this.loading = false;
            }
        },
        calculateTotals() {
            if (this.statistics.length === 0) {
                this.totalUsers = 0;
                this.totalArticles = 0;
                this.totalComments = 0;
                this.totalViews = 0;
                return;
            }

            const latest = this.statistics[this.statistics.length - 1];
            this.totalUsers = latest.userCount;
            this.totalArticles = latest.articleCount;
            this.totalComments = latest.commentCount;
            this.totalViews = latest.viewCount;
        }
    }
};
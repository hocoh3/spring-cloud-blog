const DashboardComponent = {
    template: `
        <div class="dashboard-page">
            <div class="page-header">
                <h2>数据概览</h2>
            </div>
            
            <div class="stats-grid">
                <div class="stat-card">
                    <div class="stat-label">用户总数</div>
                    <div class="stat-value">{{ overview.userCount || 0 }}</div>
                    <div class="stat-trend" :class="overview.userTrend >= 0 ? 'up' : 'down'">
                        {{ overview.userTrend >= 0 ? '↑' : '↓' }} {{ Math.abs(overview.userTrend || 0) }}%
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">文章总数</div>
                    <div class="stat-value">{{ overview.articleCount || 0 }}</div>
                    <div class="stat-trend" :class="overview.articleTrend >= 0 ? 'up' : 'down'">
                        {{ overview.articleTrend >= 0 ? '↑' : '↓' }} {{ Math.abs(overview.articleTrend || 0) }}%
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">评论总数</div>
                    <div class="stat-value">{{ overview.commentCount || 0 }}</div>
                    <div class="stat-trend" :class="overview.commentTrend >= 0 ? 'up' : 'down'">
                        {{ overview.commentTrend >= 0 ? '↑' : '↓' }} {{ Math.abs(overview.commentTrend || 0) }}%
                    </div>
                </div>
                <div class="stat-card">
                    <div class="stat-label">访问总数</div>
                    <div class="stat-value">{{ overview.viewCount || 0 }}</div>
                    <div class="stat-trend" :class="overview.viewTrend >= 0 ? 'up' : 'down'">
                        {{ overview.viewTrend >= 0 ? '↑' : '↓' }} {{ Math.abs(overview.viewTrend || 0) }}%
                    </div>
                </div>
            </div>

            <div class="data-table">
                <div class="table-header">
                    <h3>最新数据统计</h3>
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
                            <td colspan="5" class="empty-state">暂无数据</td>
                        </tr>
                    </tbody>
                </table>
            </div>
        </div>
    `,
    data() {
        return {
            overview: {},
            statistics: [],
            loading: false
        };
    },
    mounted() {
        this.loadOverview();
        this.loadStatistics();
    },
    methods: {
        async loadOverview() {
            try {
                const response = await api.getStatisticsOverview();
                this.overview = response.data;
            } catch (error) {
                console.error('加载概览数据失败:', error);
            }
        },
        async loadStatistics() {
            this.loading = true;
            try {
                const endDate = new Date();
                const startDate = new Date();
                startDate.setDate(startDate.getDate() - 7);

                const response = await api.getStatisticsTrend(
                    startDate.toISOString().split('T')[0],
                    endDate.toISOString().split('T')[0]
                );
                this.statistics = response.data;
            } catch (error) {
                console.error('加载统计数据失败:', error);
            } finally {
                this.loading = false;
            }
        }
    }
};
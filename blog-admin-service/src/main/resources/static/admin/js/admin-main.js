new Vue({
    el: '#app',
    components: {
        'dashboard': DashboardComponent,
        'users': UsersComponent,
        'review': ReviewComponent,
        'comments': CommentsComponent,
        'statistics': StatisticsComponent,
        'notifications': NotificationsComponent,
        'messages': MessagesComponent
    },
    data: {
        currentPage: 'dashboard',
        adminInfo: {},
        currentTime: ''
    },
    computed: {
        currentPageComponent() {
            return this.currentPage;
        },
        currentPageTitle() {
            const titles = {
                dashboard: '数据概览',
                users: '用户管理',
                review: '内容审核',
                comments: '评论管理',
                statistics: '数据统计',
                notifications: '通知管理',
                messages: '私信管理'
            };
            return titles[this.currentPage] || '管理后台';
        }
    },
    mounted() {
        this.checkLogin();
        this.updateTime();
        setInterval(this.updateTime, 1000);
    },
    methods: {
        checkLogin() {
            const token = localStorage.getItem('adminToken');
            const adminInfo = localStorage.getItem('adminInfo');
            
            if (!token || !adminInfo) {
                window.location.href = '/index.html';
                return;
            }
            
            this.adminInfo = JSON.parse(adminInfo);
        },
        navigateTo(page) {
            this.currentPage = page;
        },
        handleLogout() {
            if (confirm('确定要退出登录吗？')) {
                localStorage.removeItem('adminToken');
                localStorage.removeItem('adminInfo');
                window.location.href = '/index.html';
            }
        },
        updateTime() {
            const now = new Date();
            this.currentTime = now.toLocaleString('zh-CN', {
                year: 'numeric',
                month: '2-digit',
                day: '2-digit',
                hour: '2-digit',
                minute: '2-digit',
                second: '2-digit'
            });
        }
    }
});
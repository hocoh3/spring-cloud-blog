new Vue({
    el: '#app',
    data: {
        loginForm: {
            username: '',
            password: ''
        },
        loading: false,
        errorMessage: ''
    },
    methods: {
        async handleLogin() {
            if (!this.loginForm.username || !this.loginForm.password) {
                this.errorMessage = '请输入用户名和密码';
                return;
            }

            this.loading = true;
            this.errorMessage = '';

            try {
                const response = await api.login(this.loginForm.username, this.loginForm.password);
                
                if (response.data.token) {
                    localStorage.setItem('adminToken', response.data.token);
                    localStorage.setItem('adminInfo', JSON.stringify(response.data.admin));
                    window.location.href = '/admin.html';
                } else {
                    this.errorMessage = '登录失败，请重试';
                }
            } catch (error) {
                console.error('登录错误:', error);
                if (error.response && error.response.data && error.response.data.message) {
                    this.errorMessage = error.response.data.message;
                } else {
                    this.errorMessage = '登录失败，请检查用户名和密码';
                }
            } finally {
                this.loading = false;
            }
        }
    }
});
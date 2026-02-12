class WebSocketClient {
    constructor() {
        this.ws = null;
        this.reconnectAttempts = 0;
        this.maxReconnectAttempts = 5;
        this.reconnectInterval = 3000;
        this.listeners = [];
        this.userId = null;
    }

    connect(userId) {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            console.log('WebSocket already connected');
            return;
        }

        this.userId = userId;
        const wsUrl = `ws://localhost:8073/ws/notification?userId=${userId}`;
        
        try {
            this.ws = new WebSocket(wsUrl);
            this.setupEventHandlers();
            console.log('WebSocket connecting to:', wsUrl);
        } catch (error) {
            console.error('WebSocket connection error:', error);
            this.handleReconnect();
        }
    }

    setupEventHandlers() {
        this.ws.onopen = (event) => {
            console.log('WebSocket connected successfully');
            this.reconnectAttempts = 0;
            this.notifyListeners('connected', event);
        };

        this.ws.onmessage = (event) => {
            console.log('WebSocket message received:', event.data);
            try {
                const message = JSON.parse(event.data);
                this.notifyListeners('message', message);
            } catch (error) {
                console.error('Failed to parse WebSocket message:', error);
            }
        };

        this.ws.onerror = (error) => {
            console.error('WebSocket error:', error);
            this.notifyListeners('error', error);
        };

        this.ws.onclose = (event) => {
            console.log('WebSocket closed:', event);
            this.notifyListeners('closed', event);
            this.handleReconnect();
        };
    }

    handleReconnect() {
        if (this.reconnectAttempts < this.maxReconnectAttempts) {
            this.reconnectAttempts++;
            console.log(`Attempting to reconnect (${this.reconnectAttempts}/${this.maxReconnectAttempts})...`);
            setTimeout(() => {
                this.connect(this.userId);
            }, this.reconnectInterval);
        } else {
            console.error('Max reconnection attempts reached');
            this.notifyListeners('maxReconnectAttemptsReached');
        }
    }

    send(message) {
        if (this.ws && this.ws.readyState === WebSocket.OPEN) {
            this.ws.send(JSON.stringify(message));
        } else {
            console.error('WebSocket is not connected');
        }
    }

    close() {
        if (this.ws) {
            this.ws.close();
            this.ws = null;
            this.reconnectAttempts = 0;
        }
    }

    addListener(event, callback) {
        this.listeners.push({ event, callback });
    }

    removeListener(event, callback) {
        this.listeners = this.listeners.filter(
            listener => !(listener.event === event && listener.callback === callback)
        );
    }

    notifyListeners(event, data) {
        this.listeners
            .filter(listener => listener.event === event)
            .forEach(listener => listener.callback(data));
    }

    isConnected() {
        return this.ws && this.ws.readyState === WebSocket.OPEN;
    }
}

const wsClient = new WebSocketClient();
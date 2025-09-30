import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';

let stompClient = null;

export const connectToNotifications = (sellerId, onMessage) => {
    const socket = new SockJS('http://localhost:8082/ws');

    stompClient = new Client({
        webSocketFactory: () => socket,
        reconnectDelay: 5000,
        onConnect: () => {
            console.log('Connected to WebSocket');
            stompClient.subscribe(`/topic/seller/${sellerId}/notifications`, (message) => {
                if (message.body) {
                    onMessage(message.body);
                }
            });
        },
        onStompError: (frame) => {
            console.error('WebSocket error:', frame.headers['message']);
        },
    });

    stompClient.activate();
};

export const disconnectFromNotifications = () => {
    if (stompClient) {
        stompClient.deactivate();
        stompClient = null;
    }
};

// Establishing a WebSocket connection using SockJS
const socket = new SockJS('/websocket-Initializer'); // Replace with your WebSocket endpoint

// Creating a STOMP client over the WebSocket connection
const stompClient = Stomp.over(socket);

// Connect to the WebSocket server
stompClient.connect({}, function () {
    console.log('Connected to WebSocket');
    console.log('hello');
    // Subscribe to the user-specific WebSocket destination
     let userId = '1'; // Replace with the actual user ID
     stompClient.subscribe(`/user/${userId}/topic/private-notifications`, function (message) {
        // Handle incoming messages received from the subscribed destination
        console.log('Received message:', JSON.parse(message.body));
        // Display the received message content in the browser
        const messageList = document.getElementById('message-list');
        const newMessage = document.createElement('li');
        newMessage.textContent = JSON.parse(message.body).content;
        messageList.appendChild(newMessage);
    });
});

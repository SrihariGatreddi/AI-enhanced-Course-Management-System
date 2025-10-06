document.addEventListener('DOMContentLoaded', () => {
    const chatBox = document.getElementById('chat-box');
    const userInput = document.getElementById('user-input');
    const sendBtn = document.getElementById('send-btn');

    // --- NEW: Read the CSRF token from the meta tags ---
    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

    // Function to add a message to the chat box
    const addMessage = (message, sender) => {
        const messageElement = document.createElement('div');
        messageElement.classList.add('chat-message', sender);

        const p = document.createElement('p');
        p.textContent = message;
        messageElement.appendChild(p);

        chatBox.appendChild(messageElement);
        chatBox.scrollTop = chatBox.scrollHeight;
    };

    // --- MAIN FUNCTION TO SEND MESSAGE AND HANDLE RESPONSE ---
    const handleSendMessage = async (event) => {
        if (event) {
            event.preventDefault();
        }

        const question = userInput.value.trim();
        if (question === '') return;

        addMessage(question, 'user');
        userInput.value = '';

        try {
            // Call the backend API endpoint
            const response = await fetch('/student/ai-response', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    // --- NEW: Add the CSRF token to the request headers ---
                    [csrfHeader]: csrfToken
                },
                body: JSON.stringify({ question: question })
            });

            console.log("hello"); // Your debug line

            // This block handles the specific "limit exceeded" error (429)
            if (response.status === 429) {
                const data = await response.json();
                addMessage(data.answer || 'API limit exceeded. Please try again later.', 'ai');
                return;
            }

            // This block handles other server-side errors (e.g., 403, 500)
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            // This block handles all successful (200 OK) responses.
            const data = await response.json();
            const aiResponse = data.answer;
            addMessage(aiResponse, 'ai');

        } catch (error) {
            console.error('Error fetching AI response:', error);
            addMessage('Sorry, the AI assistant is currently unavailable due to a technical issue.', 'ai');
        }
    };

    // --- EVENT LISTENERS ---
    sendBtn.addEventListener('click', handleSendMessage);
    userInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter'){
            handleSendMessage(e);
        }
    });
});
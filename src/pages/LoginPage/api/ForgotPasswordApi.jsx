export const forgotPassword = async (data) => {
    try {
        const response = await fetch('http://localhost:8082/api/auth/reset', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(data)
        });

        if (!response.ok) {
            const errorData = await response.json();
            console.error('Error from backend:', errorData);
            throw new Error(errorData.message || 'Failed to send reset link');
        }

        return response.json();
    } catch (error) {
        console.error('Forgot Password Error:', error.message);
        throw error;
    }
};

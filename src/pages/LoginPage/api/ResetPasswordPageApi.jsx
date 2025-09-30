export const resetPassword = async (data) => {
    try {
        const response = await fetch(`http://localhost:8082/api/auth/reset-password?token=${data.token}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ newPassword: data.newPassword })
        });


        if (!response.ok) {
            const errorData = await response.json();
            console.error('Error from backend:', errorData.message);
            throw new Error(errorData.message || 'Failed to reset password');
        }

        const responseData = await response.json();
        return responseData;

    } catch (error) {
        console.error('Reset Password Error:', error.message);
        throw error;
    }
};

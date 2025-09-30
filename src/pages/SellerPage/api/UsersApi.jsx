const API_URL = 'http://localhost:8082/api/users';

const getAuthHeaders = () => ({
    'Content-Type': 'application/json',
    'Accept': 'application/json',
    'Authorization': `Bearer ${sessionStorage.getItem('token')}`
});

export const getAllUsers = async () => {
    try {
        const response = await fetch(API_URL, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            return await response.json();
        } else {
            throw new Error('Error fetching users');
        }
    } catch (error) {
        console.error('Error fetching users:', error);
        throw error;
    }
};

export const getSellers = async () => {
    try {
        const response = await fetch(`${API_URL}/sellers`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            return await response.json();
        } else {
            throw new Error('Error fetching sellers');
        }
    } catch (error) {
        console.error('Error fetching sellers:', error);
        throw error;
    }
};

export const getUserById = async (userId) => {
    try {
        const response = await fetch(`${API_URL}/${userId}`, {
            headers: getAuthHeaders()
        });
        if (response.ok) {
            return await response.json();
        } else {
            throw new Error('Error fetching user by ID');
        }
    } catch (error) {
        console.error('Error fetching user by ID:', error);
        throw error;
    }
};

export const createUser = async (userDTO) => {
    try {
        const response = await fetch(API_URL, {
            method: 'POST',
            headers: getAuthHeaders(),
            body: JSON.stringify(userDTO),
        });
        if (response.ok) {
            return await response.json();
        } else {
            throw new Error('Error creating user');
        }
    } catch (error) {
        console.error('Error creating user:', error);
        throw error;
    }
};

export const updateUser = async (userId, userDTO) => {
    try {
        const response = await fetch(`${API_URL}/${userId}`, {
            method: 'PUT',
            headers: getAuthHeaders(),
            body: JSON.stringify(userDTO),
        });
        if (response.ok) {
            return await response.json();
        } else {
            throw new Error('Error updating user');
        }
    } catch (error) {
        console.error('Error updating user:', error);
        throw error;
    }
};

export const deleteUser = async (userId) => {
    try {
        const response = await fetch(`${API_URL}/${userId}`, {
            method: 'DELETE',
            headers: getAuthHeaders()
        });
        if (response.ok) {
            return response.status;
        } else {
            throw new Error('Error deleting user');
        }
    } catch (error) {
        console.error('Error deleting user:', error);
        throw error;
    }
};

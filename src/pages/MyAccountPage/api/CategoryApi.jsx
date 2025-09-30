const BASE_URL = "http://localhost:8082/api/categories";

export const fetchAllCategories = async (token) => {
    const response = await fetch(BASE_URL, {
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error('Failed to fetch categories');
    }

    return await response.json();
};
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

export const createCategory = async (category, token) => {
    const response = await fetch(BASE_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(category)
    });

    if (!response.ok) {
        throw new Error("Failed to create category");
    }
};

export const updateCategory = async (idCategory, category, token) => {
    const response = await fetch(`${BASE_URL}/${idCategory}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(category)
    });

    if (!response.ok) {
        throw new Error("Failed to update category");
    }
};

export const deleteCategory = async (idCategory, token) => {
    const response = await fetch(`${BASE_URL}/${idCategory}`, {
        method: "DELETE",
        headers: {
            'Authorization': `Bearer ${token}`
        }
    });

    if (!response.ok) {
        throw new Error("Failed to delete category");
    }
};

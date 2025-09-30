export const fetchSellerProducts = async (token) => {
    const response = await fetch('http://localhost:8082/api/seller/products', {
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        throw new Error('Failed to fetch seller products');
    }

    return await response.json();
};

export const deleteProduct = async (productId, token) => {
    const response = await fetch(`http://localhost:8082/api/seller/products/${productId}`, {
        method: 'DELETE',
        headers: {
            'Authorization': `Bearer ${token}`,
        },
    });

    if (!response.ok) {
        throw new Error('Failed to delete product');
    }
};

export const updateProduct = async (productData, token) => {
    const response = await fetch('http://localhost:8082/api/seller/products', {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(productData)
    });

    if (!response.ok) {
        throw new Error('Failed to update product');
    }

    return await response.json();
};

export const fetchAssignedCategories = async (token) => {
    try {
        const response = await fetch('http://localhost:8082/api/seller-categories/categories', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (!response.ok) {
            throw new Error('Failed to fetch assigned categories');
        }

        return await response.json();
    } catch (error) {
        console.error("Error fetching assigned categories:", error);
        throw error;
    }
};
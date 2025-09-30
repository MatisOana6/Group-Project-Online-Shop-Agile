export const createProduct = async (productData) => {
    const token = sessionStorage.getItem('token');

    try {
        const response = await fetch('http://localhost:8082/api/seller/products', {
            method: 'POST',
            headers: {
                'Accept': 'application/json',
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify(productData),
        });

        if (!response.ok) {
            throw new Error('Failed to add product');
        }

        return await response.json();
    } catch (error) {
        console.error('Error adding product:', error);
        throw error;
    }
};

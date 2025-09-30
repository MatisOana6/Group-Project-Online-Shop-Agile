const endpoint = {
    history: (productId) => `http://localhost:8082/api/products/history/${productId}`,
    allProducts: 'http://localhost:8082/api/products'
};

function fetchProductHistory(productId) {
    const token = sessionStorage.getItem("token");

    return fetch(endpoint.history(productId), {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            return Promise.reject(new Error('Failed to fetch product history'));
        }
    });
}

function fetchAllProducts() {
    const token = sessionStorage.getItem("token");

    return fetch(endpoint.allProducts, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            return Promise.reject(new Error('Failed to fetch all products'));
        }
    });
}

export {
    fetchProductHistory,
    fetchAllProducts
};

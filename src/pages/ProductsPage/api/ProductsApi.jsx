const endpoint = {
    productsFilter: 'http://localhost:8082/api/products/filter'
};

function fetchProductById(productId, token) {
    const url = new URL(`http://localhost:8082/api/products/${productId}`);

    const request = new Request(url, {
        mode: 'cors',
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        credentials: 'include'
    });

    return fetch(request)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return Promise.reject(new Error('Failed to fetch product'));
            }
        })
        .then(data => {
            console.log('Fetched product:', data);
            return data;
        })
        .catch(error => {
            console.error('Error fetching product:', error);
            return Promise.reject(error);
        });
}


function fetchProducts(token, minPrice, maxPrice, category, minRating, maxRating) {
    let url = new URL(endpoint.productsFilter);

    if (minPrice !== undefined && maxPrice !== undefined) {
        url.searchParams.append('minPrice', minPrice);
        url.searchParams.append('maxPrice', maxPrice);
    }

    if (category) {
        url.searchParams.append('category', category);
    }

    if (minRating !== undefined && maxRating !== undefined) {
        url.searchParams.append('minRating', minRating);
        url.searchParams.append('maxRating', maxRating);
    }

    let request = new Request(url, {
        mode: 'cors',
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        credentials: 'include'
    });

    return fetch(request)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return Promise.reject(new Error('Failed to fetch products'));
            }
        })
        .then(data => {
            console.log('Fetched products:', data);
            return data;
        })
        .catch(error => {
            console.error('Error fetching products:', error);
            return Promise.reject(error);
        });
}

export async function fetchRecommendations() {
    const token = sessionStorage.getItem('token');

    const res = await fetch('http://localhost:8082/api/recommendations', {
        headers: {
            'Authorization': `Bearer ${token}`
        },
        credentials: 'include'
    });

    if (!res.ok) {
        throw new Error('Failed to fetch recommendations');
    }

    return res.json();
}


export {
    fetchProducts, fetchProductById
};



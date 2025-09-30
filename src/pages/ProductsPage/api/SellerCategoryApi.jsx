const endpoint = {
    assign: 'http://localhost:8082/api/seller-categories',
    remove: (sellerId, categoryId) => `http://localhost:8082/api/seller-categories/${sellerId}/${categoryId}`,
    getBySeller: (sellerId) => `http://localhost:8082/api/seller-categories/${sellerId}`
};

function assignCategory(sellerId, categoryId) {
    const token = sessionStorage.getItem("token");

    return fetch(endpoint.assign, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ sellerId, categoryId })
    }).then(response => {
        if (!response.ok) {
            return Promise.reject(new Error('Failed to assign category'));
        }
    });
}

function removeCategory(sellerId, categoryId) {
    const token = sessionStorage.getItem("token");

    return fetch(endpoint.remove(sellerId, categoryId), {
        method: 'DELETE',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        }
    }).then(response => {
        if (!response.ok) {
            return Promise.reject(new Error('Failed to remove category'));
        }
    });
}

function fetchAssignedCategories(sellerId) {
    const token = sessionStorage.getItem("token");
    console.log("TOKEN:", token);

    return fetch(endpoint.getBySeller(sellerId), {
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
            return Promise.reject(new Error('Failed to fetch assigned categories'));
        }
    });
}

export {
    assignCategory,
    removeCategory,
    fetchAssignedCategories
};

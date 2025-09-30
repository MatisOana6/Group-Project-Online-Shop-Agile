const endpoint = {
    categories: 'http://localhost:8082/api/categories'
};

function fetchCategories() {
    const token = sessionStorage.getItem("token");
    return fetch(endpoint.categories, {
        method: 'GET',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
    }).then(response => {
        if (response.ok) {
            return response.json();
        } else {
            return Promise.reject(new Error('Failed to fetch categories'));
        }
    });
}

export {
    fetchCategories
};

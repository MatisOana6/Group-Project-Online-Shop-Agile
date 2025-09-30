const endpoint = {
    sellers: 'http://localhost:8082/api/users/sellers'
};

function fetchSellers() {
    const token = sessionStorage.getItem("token");
    return fetch(endpoint.sellers, {
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
            return Promise.reject(new Error('Failed to fetch sellers'));
        }
    });
}

export {
    fetchSellers
};

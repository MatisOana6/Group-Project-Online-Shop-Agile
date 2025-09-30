const endpoint = {
    history: 'http://localhost:8082/api/orders/history'
};

function getOrderHistory(){
    const token = sessionStorage.getItem('token');

    return fetch(endpoint.history, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Accept': 'application/json'
        },
        credentials: 'include'
    }).then(res => {
        if (!res.ok) {
            throw new Error('Failed to fetch order history');
        }
        return res.json();
    });
}

export {
    getOrderHistory
};
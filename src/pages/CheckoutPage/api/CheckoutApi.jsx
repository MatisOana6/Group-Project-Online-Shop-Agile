const endpoint = {
    order: 'http://localhost:8082/api/orders'
};

const placeOrder = (orderData) => {
    const token = sessionStorage.getItem('token');

    let request = new Request(endpoint.order, {
        mode: 'cors',
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        credentials: 'include',
        body: JSON.stringify(orderData)
    });

    return fetch(request)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return response.json().then(errorData => {
                    throw new Error(errorData.message || 'Unable to place order');
                });
            }
        })
        .catch(error => {
            console.error("Order API error:", error);
            throw error;
        });
};

export { placeOrder };
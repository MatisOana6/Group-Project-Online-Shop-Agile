const endpoint = {
    register: 'http://localhost:8082/api/auth/register'
};

function register(userData) {
    const request = new Request(endpoint.register, {
        mode: 'cors',
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(userData),
        credentials: 'include'
    });

    return fetch(request)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else if (response.status === 409) {
                return Promise.reject(new Error('This email is already in use.'));
            } else {
                return Promise.reject(new Error('Registration failed.'));
            }
        })
        .catch(error => {
            console.error('Register error:', error);
            return Promise.reject(error);
        });
}

export {
    register
};

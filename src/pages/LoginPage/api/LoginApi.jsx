const endpoint = {
    login: 'http://localhost:8082/api/auth/login'
};

function login(credentials) {
    let request = new Request(endpoint.login, {
        mode: 'cors',
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(credentials),
        credentials: 'include'
    });

    return fetch(request)
        .then(response => {
            if (response.ok) {
                return response.json();
            } else {
                return Promise.reject(new Error('Login failed'));
            }
        })
        .then(data => {
            console.log(data);
            if (data.token) {
                sessionStorage.setItem('token', data.token);
                sessionStorage.setItem('profileImage', data.profileImage);
                console.log('Token saved in sessionStorage:', data.token);
            } else {
                console.log('No access token found in response');
            }
            return data;
        })
        .catch(error => {
            console.error('Error:', error);
            return Promise.reject(error);
        });
}

export {
    login
};

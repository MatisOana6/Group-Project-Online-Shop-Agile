const endpoint = {
    profile: 'http://localhost:8082/api/users/profile'
};

function getProfile() {
    const token = sessionStorage.getItem('token');

    return fetch(endpoint.profile, {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Accept': 'application/json'
        },
        credentials: 'include'
    }).then(res => {
        if (!res.ok) {
            throw new Error('Failed to fetch profile');
        }
        return res.json();
    });
}

function updateProfile(formData) {
    const token = sessionStorage.getItem('token');
    console.log(formData);

    return fetch(endpoint.profile, {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        credentials: 'include',
        body: JSON.stringify(formData)
    }).then(res => {
        if (!res.ok) {
            throw new Error('Failed to update profile');
        }
        return res.json();
    });
}

export {
    getProfile,
    updateProfile
};
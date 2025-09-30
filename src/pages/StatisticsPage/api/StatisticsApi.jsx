export const fetchStatisticsData = async (filters) => {
    const token = sessionStorage.getItem('token');

    const response = await fetch('http://localhost:8082/api/orders/admin/stats', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(filters)
    });

    if (!response.ok) {
        throw new Error('Failed to fetch statistics');
    }

    return await response.json();
};

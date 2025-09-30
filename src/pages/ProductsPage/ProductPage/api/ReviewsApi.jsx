const endpoint = {
    productReviews: 'http://localhost:8082/api/reviews/product',
    submitReview: 'http://localhost:8082/api/products'
};

function fetchReviewsByProductId(productId) {
    const token = sessionStorage.getItem("token");

    return fetch(`${endpoint.productReviews}/${productId}`, {
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
            return Promise.reject(new Error('Failed to fetch reviews'));
        }
    });
}

export async function submitReply(feedbackId, reply, token) {
    const response = await fetch(`http://localhost:8082/api/reviews/feedback/${feedbackId}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`
        },
        body: JSON.stringify(reply)
    });

    if (!response.ok) {
        throw new Error("Failed to submit reply");
    }
}

function submitReview(productId, rating, comment) {
    const token = sessionStorage.getItem("token");

    return fetch(`http://localhost:8082/api/products/${productId}/reviews`, {
        method: 'POST',
        headers: {
            'Accept': 'application/json',
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ rating, comment }),
    }).then(async response => {
        if (!response.ok) {
            const message = await response.text();
            throw new Error(message || 'Failed to submit review');
        }


        const text = await response.text();
        return text ? JSON.parse(text) : null;
    });
}


export {
    fetchReviewsByProductId,
    submitReview
};

import React from 'react';

export const renderStars = (rating) => {
    const stars = [];
    const fullStars = Math.floor(rating);
    const halfStar = rating % 1 >= 0.5;

    for (let i = 0; i < fullStars; i++) {
        stars.push(<span key={`full-${i}`}>★</span>);
    }

    if (halfStar) {
        stars.push(<span key="half">☆</span>);
    }

    for (let i = stars.length; i < 5; i++) {
        stars.push(<span key={`empty-${i}`}>☆</span>);
    }

    return stars;
};

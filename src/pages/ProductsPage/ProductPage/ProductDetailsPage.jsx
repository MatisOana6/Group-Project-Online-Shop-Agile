import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { jwtDecode } from 'jwt-decode';
import styles from './ProductDetailsPage.module.css';
import { fetchProductById } from '../api/ProductsApi';
import { fetchReviewsByProductId, submitReview } from './api/ReviewsApi';
import { renderStars } from '../../../utils/renderStars';

const ProductDetailsPage = () => {
    const { productId } = useParams();
    const [product, setProduct] = useState(null);
    const [reviews, setReviews] = useState([]);
    const [quantity, setQuantity] = useState(1);
    const [isAdding, setIsAdding] = useState(false);
    const [newRating, setNewRating] = useState(0);
    const [newComment, setNewComment] = useState('');
    const [hasSubmittedReview, setHasSubmittedReview] = useState(false);

    const token = sessionStorage.getItem('token');
    const decoded = token ? jwtDecode(token) : null;
    const username = decoded?.sub || null;

    useEffect(() => {
        if (!token) return;

        fetchProductById(productId, token)
            .then(setProduct)
            .catch(err => console.error('Error loading product', err));

        fetchReviewsByProductId(productId, token)
            .then(data => {
                setReviews(data);
                const alreadyReviewed = data.some(review => review.username === username);
                setHasSubmittedReview(alreadyReviewed);
            })
            .catch(err => console.error('Error loading reviews', err));
    }, [productId, token, username]);

    const handleQuantityChange = (e) => {
        const value = Number(e.target.value);
        if (value >= 1 && value <= product.quantity) {
            setQuantity(value);
        }
    };

    const handleAddToCart = () => {
        if (!product || product.quantity === 0) return;

        const cartItem = {
            id: product.idProduct,
            name: product.name,
            price: product.price,
            image: product.image,
            quantity,
        };

        const existingCart = JSON.parse(sessionStorage.getItem('cart') || '[]');
        existingCart.push(cartItem);
        sessionStorage.setItem('cart', JSON.stringify(existingCart));

        setIsAdding(true);
        setTimeout(() => setIsAdding(false), 1500);
    };

    const handleStarClick = (rating) => {
        if (!hasSubmittedReview) setNewRating(rating);
    };

    const handleReviewSubmit = () => {
        if (newRating === 0 || newComment.trim() === '') {
            alert("Please provide both a rating and a comment.");
            return;
        }

        submitReview(productId, newRating, newComment)
            .then(() => {
                return fetchReviewsByProductId(productId, token);
            })
            .then(data => {
                setReviews(data);
                setHasSubmittedReview(true);
                setNewRating(0);
                setNewComment('');
            })
            .catch(err => {
                alert(err.message);
            });
    };


    if (!product) return <div className={styles.loading}>Loading...</div>;

    const isOutOfStock = product.quantity === 0;

    return (
        <div className={styles.pageWrapper}>
            <div className={styles.detailsContainer}>
                <div className={styles.imageSection}>
                    <img
                        src={product.image}
                        alt={product.name}
                        className={styles.productImage}
                    />
                </div>

                <div className={styles.infoSection}>
                    <div className={styles.topSection}>
                        <h1 className={styles.title}>{product.name}</h1>
                        <p className={styles.description}>{product.description}</p>
                    </div>

                    <p className={styles.price}>${product.price.toFixed(2)}</p>
                    <div className={styles.rating}>{renderStars(product.averageRating)}</div>

                    <div className={styles.buySection}>
                        <label>
                            Quantity:
                            <input
                                type="number"
                                min="1"
                                max={product.quantity}
                                value={quantity}
                                onChange={handleQuantityChange}
                                className={styles.quantityInput}
                                disabled={isOutOfStock}
                            />
                        </label>

                        <p className={styles.totalPrice}>
                            Total: ${(product.price * quantity).toFixed(2)}
                        </p>

                        <button
                            className={styles.confirmButton}
                            onClick={handleAddToCart}
                            disabled={isOutOfStock}
                        >
                            {isOutOfStock ? 'Out of Stock' : isAdding ? 'Added!' : 'Add to Cart'}
                        </button>
                    </div>
                </div>
            </div>

            <div className={styles.reviewsSection}>
                <h2>Reviews</h2>
                {reviews.length === 0 ? (
                    <p>No reviews yet.</p>
                ) : (
                    reviews.map((review) => (
                        <div key={review.idReview} className={styles.reviewCard}>
                            <div className={styles.reviewHeader}>
                                <strong>{review.username}</strong>
                                <span>{renderStars(review.rating)}</span>
                            </div>
                            <p>{review.comment}</p>
                            {review.reply && (
                                <p className={styles.reply}><strong>Reply:</strong> {review.reply}</p>
                            )}
                        </div>
                    ))
                )}

                <div className={styles.reviewForm}>
                    <h3>Leave a Review</h3>
                    {hasSubmittedReview ? (
                        <p>You have already submitted a review for this product.</p>
                    ) : (
                        <>
                            <div className={styles.ratingInput}>
                                {[1, 2, 3, 4, 5].map((star) => (
                                    <span
                                        key={star}
                                        onClick={() => handleStarClick(star)}
                                        className={star <= newRating ? styles.filledStar : styles.emptyStar}
                                        style={{ cursor: 'pointer', fontSize: '1.5rem', marginRight: '4px' }}
                                    >
                                        ★
                                    </span>
                                ))}
                            </div>

                            <textarea
                                value={newComment}
                                onChange={(e) => setNewComment(e.target.value)}
                                placeholder="Write your review..."
                                className={styles.commentBox}
                            />

                            <button
                                className={styles.submitButton}
                                onClick={handleReviewSubmit}
                            >
                                Submit Review
                            </button>
                        </>
                    )}
                </div>
            </div>
        </div>
    );
};

export default ProductDetailsPage;

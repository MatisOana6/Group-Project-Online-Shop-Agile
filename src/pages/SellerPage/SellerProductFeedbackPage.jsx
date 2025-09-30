import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import styles from './SellerProductFeedbackPage.module.css';
import { fetchProductById } from '../ProductsPage/api/ProductsApi';
import { fetchReviewsByProductId } from '../ProductsPage/ProductPage/api/ReviewsApi';
import { renderStars } from '../../utils/renderStars';

const SellerProductFeedbackPage = () => {
    const { productId } = useParams();
    const [product, setProduct] = useState(null);
    const [reviews, setReviews] = useState([]);
    const [replyInputs, setReplyInputs] = useState({});

    const token = sessionStorage.getItem("token");

    useEffect(() => {
        fetchProductById(productId, token).then(setProduct).catch(console.error);
        fetchReviewsByProductId(productId, token).then(setReviews).catch(console.error);
    }, [productId]);

    const handleReplySubmit = async (reviewId) => {
        const replyText = replyInputs[reviewId];
        if (!replyText || replyText.trim() === "") return;

        const res = await fetch(`http://localhost:8082/api/reviews/feedback/${reviewId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                "Authorization": `Bearer ${token}`,
            },
            body: JSON.stringify({ reply: replyText }),
        });

        if (res.ok) {
            const updated = reviews.map(r =>
                r.idReview === reviewId ? { ...r, reply: replyText } : r
            );
            setReviews(updated);
            setReplyInputs(prev => ({ ...prev, [reviewId]: "" }));
        }
    };


    if (!product) return <p className={styles.loading}>Loading...</p>;

    return (
        <div className={styles.pageWrapper}>
            <div className={styles.layoutWrapper}>
                <div className={styles.imageSection}>
                    <img src={product.image} alt={product.name} className={styles.productImage} />
                    <h1 className={styles.productTitle}>{product.name}</h1>
                    <div className={styles.productRating}>{renderStars(product.averageRating)}</div>
                </div>

                <div className={styles.feedbackSection}>
                    <h2 className={styles.feedbackTitle}>Customer Feedback</h2>
                    <div className={styles.feedbackList}>
                        {reviews.length === 0 ? (
                            <p>No feedback yet.</p>
                        ) : (
                            reviews.map((review) => (
                                <div key={review.idReview} className={styles.reviewCard}>
                                    <div className={styles.header}>
                                        <strong>{review.username}</strong> — {renderStars(review.rating)}
                                    </div>
                                    <p>{review.comment}</p>
                                    <div className={styles.replySection}>
                                        {review.reply && (
                                            <p><strong>Current reply:</strong> {review.reply}</p>
                                        )}
                                        <input
                                            type="text"
                                            placeholder={review.reply ? "Edit your reply..." : "Write a reply..."}
                                            value={replyInputs[review.idReview] ?? review.reply ?? ""}
                                            onChange={(e) =>
                                                setReplyInputs({ ...replyInputs, [review.idReview]: e.target.value })
                                            }
                                        />
                                        <button onClick={() => handleReplySubmit(review.idReview)}>
                                            {review.reply ? "Update Reply" : "Send Reply"}
                                        </button>
                                    </div>
                                </div>
                            ))
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SellerProductFeedbackPage;

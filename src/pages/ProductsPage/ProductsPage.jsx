import React, { useEffect, useState } from 'react';
import styles from './ProductsPage.module.css';
import image1 from '../../assets/ProductsPage/products-page-background1.jpg';
import image2 from '../../assets/ProductsPage/products-page-background2.jpg';
import image3 from '../../assets/ProductsPage/products-page-background3.jpg';
import {fetchProducts, fetchRecommendations} from './api/ProductsApi';
import { fetchCategories } from './api/CategoriesApi';

import { renderStars } from '../../utils/renderStars';
import { Link } from 'react-router-dom';

const ProductsPage = () => {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [recommendations, setRecommendations] = useState([]); // 👈 Recomandări
    const [priceRange, setPriceRange] = useState("");
    const [category, setCategory] = useState("");
    const [ratingRange, setRatingRange] = useState("");

    useEffect(() => {
        loadCategories();
        loadRecommendations();
    }, []);

    useEffect(() => {
        loadProducts();
    }, [priceRange, category, ratingRange]);

    const loadCategories = async () => {
        const token = sessionStorage.getItem('token');
        try {
            const data = await fetchCategories(token);
            setCategories(data);
        } catch (err) {
            console.error('Failed to load categories', err);
        }
    };

    const loadRecommendations = async () => {
        try {
            const data = await fetchRecommendations();
            setRecommendations(data);
        } catch (err) {
            console.warn('No personalized recommendations found.');
        }
    };

    const loadProducts = async () => {
        const token = sessionStorage.getItem('token');
        try {
            let minPrice, maxPrice, minRating, maxRating;

            if (priceRange) {
                [minPrice, maxPrice] = priceRange.split('-').map(Number);
            }

            if (ratingRange) {
                [minRating, maxRating] = ratingRange.split('-').map(Number);
            }

            const data = await fetchProducts(token, minPrice, maxPrice, category, minRating, maxRating);
            setProducts(data);
        } catch (err) {
            console.error('Failed to load products', err);
        }
    };

    const handlePriceChange = (e) => setPriceRange(e.target.value);
    const handleCategoryChange = (e) => setCategory(e.target.value);
    const handleRatingChange = (e) => setRatingRange(e.target.value);

    return (
        <div className={styles.container}>
            <div className={styles.heroImages}>
                <img src={image1} alt="Street Style 1" className={styles.heroImage} />
                <img src={image2} alt="Street Style 2" className={styles.heroImage} />
                <img src={image3} alt="Street Style 3" className={styles.heroImage} />
            </div>



            {/* 🔽 Filtre */}
            <div className={styles.filters}>
                <select className={styles.select} onChange={handleCategoryChange}>
                    <option value="">All Categories</option>
                    {categories.map(cat => (
                        <option key={cat.idCategory} value={cat.idCategory}>{cat.name}</option>
                    ))}
                </select>

                <select className={styles.select} onChange={handlePriceChange}>
                    <option value="">All Prices</option>
                    <option value="0-50">$0 - $50</option>
                    <option value="50-100">$50 - $100</option>
                    <option value="100-200">$100 - $200</option>
                    <option value="200-999">$200+</option>
                </select>

                <select className={styles.select} onChange={handleRatingChange}>
                    <option value="">All Ratings</option>
                    <option value="1-2">1 - 2 stars</option>
                    <option value="2-3">2 - 3 stars</option>
                    <option value="3-4">3 - 4 stars</option>
                    <option value="4-5">4 - 5 stars</option>
                </select>
            </div>

            {/*  */}
            <div className={styles.productsGrid}>
                {products.map(product => (
                    <Link to={`/products/${product.idProduct}`} key={product.idProduct} className={styles.card}>
                        <img src={product.image} alt={product.name} className={styles.cardImage} />
                        <div className={styles.cardBody}>
                            <h3 className={styles.cardTitle}>{product.name}</h3>
                            <p className={styles.cardPrice}>${product.price.toFixed(2)}</p>
                            <div className={styles.rating}>
                                {renderStars(product.averageRating)}
                            </div>
                        </div>
                    </Link>
                ))}
            </div>

            {recommendations.length > 0 && (
                <div className={styles.recommendationSection}>
                    <h2 className={styles.recommendationTitle}>Recommended for you</h2>
                    <div className={styles.productsGrid}>
                        {recommendations.map((rec) => (
                            <Link to={`/products/${rec.id}`} key={rec.id} className={styles.card}>
                                <img
                                    src={rec.image || '/default-image.jpg'}
                                    alt={rec.name}
                                    className={styles.cardImage}
                                />
                                <div className={styles.cardBody}>
                                    <h3 className={styles.cardTitle}>{rec.name}</h3>
                                    <p className={styles.cardPrice}>${rec.price.toFixed(2)}</p>

                                    {/* Dacă ai rating în recomandări */}
                                    {rec.averageRating && (
                                        <div className={styles.rating}>
                                            {renderStars(rec.averageRating)}
                                        </div>
                                    )}

                                    {/*  */}
                                    <span className={styles.badge}>Because you liked...</span>
                                </div>
                            </Link>
                        ))}
                    </div>
                </div>
            )}

        </div>
    );
};

export default ProductsPage;

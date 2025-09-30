import React, { useEffect, useState } from 'react';
import styles from './SellerPage.module.css';

import image1 from '../../assets/ProductsPage/products-page-background1.jpg';
import image2 from '../../assets/ProductsPage/products-page-background2.jpg';
import image3 from '../../assets/ProductsPage/products-page-background3.jpg';
import notificationIcon from '../../assets/SellerPage/notification-icon.png';

import { renderStars } from '../../utils/renderStars';
import AddProductForm from "../../components/AddProductForm/AddProductForm";
import EditProductForm from "../../components/EditProductForm/EditProductForm";
import { fetchCategories } from "../ProductsPage/api/CategoriesApi";
import { fetchSellerProducts, deleteProduct } from "./api/SellerApi";
import { Link, useNavigate } from 'react-router-dom';
import { connectToNotifications, disconnectFromNotifications } from '../../websocket/notificationSocket';
import { jwtDecode } from 'jwt-decode';

const SellerPage = () => {
    const [products, setProducts] = useState([]);
    const [categories, setCategories] = useState([]);
    const [showAddModal, setShowAddModal] = useState(false);
    const [editingProduct, setEditingProduct] = useState(null);
    const [priceRange, setPriceRange] = useState("");
    const [category, setCategory] = useState("");
    const [ratingRange, setRatingRange] = useState("");
    const [notifications, setNotifications] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        loadCategories();
    }, []);

    useEffect(() => {
        loadProducts();
    }, [priceRange, category, ratingRange]);

    useEffect(() => {
        const token = sessionStorage.getItem('token');
        if (!token) return;
        const decoded = jwtDecode(token);
        const sellerId = decoded?.jti;
        connectToNotifications(sellerId, (msg) => {
            const id = Date.now();
            setNotifications(prev => [...prev, { id, text: msg }]);
            setTimeout(() => {
                setNotifications(prev => prev.filter(notification => notification.id !== id));
            }, 10000);
        });
        return () => disconnectFromNotifications();
    }, []);

    const dismissNotification = (id) => {
        setNotifications(prev => prev.filter(notification => notification.id !== id));
    };

    const loadCategories = async () => {
        try {
            const token = sessionStorage.getItem('token');
            const data = await fetchCategories(token);
            setCategories(data);
        } catch (err) {
            console.error('Failed to load categories', err);
        }
    };

    const loadProducts = async () => {
        try {
            const token = sessionStorage.getItem('token');
            const allProducts = await fetchSellerProducts(token);
            let filtered = [...allProducts];

            if (priceRange) {
                const [minPrice, maxPrice] = priceRange.split('-').map(Number);
                filtered = filtered.filter(product =>
                    product.price >= minPrice && product.price <= maxPrice
                );
            }

            if (category) {
                const selected = categories.find(c => String(c.idCategory) === category);
                filtered = filtered.filter(product =>
                    product.categoryName === selected?.name
                );
            }

            if (ratingRange) {
                const [minRating, maxRating] = ratingRange.split('-').map(Number);
                filtered = filtered.filter(product =>
                    product.averageRating >= minRating && product.averageRating <= maxRating
                );
            }

            setProducts(filtered);
        } catch (err) {
            console.error('Failed to load products', err);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm("Are you sure you want to delete this product?")) return;
        try {
            const token = sessionStorage.getItem('token');
            await deleteProduct(id, token);
            alert("Product deleted!");
            loadProducts();
        } catch (err) {
            console.error("Failed to delete product:", err);
            alert("Could not delete product.");
        }
    };

    const handleEdit = (product) => setEditingProduct(product);
    const toggleAddModal = () => setShowAddModal(!showAddModal);
    const handlePriceChange = (e) => setPriceRange(e.target.value);
    const handleCategoryChange = (e) => setCategory(e.target.value);
    const handleRatingChange = (e) => setRatingRange(e.target.value);

    return (
        <div className={styles.container}>
            <div className={styles.heroImages}>
                <img src={image1} alt="Street Style 1" className={styles.heroImage}/>
                <img src={image2} alt="Street Style 2" className={styles.heroImage}/>
                <img src={image3} alt="Street Style 3" className={styles.heroImage}/>
            </div>

            {notifications.length > 0 && (
                <div className={styles.notificationsBox}>
                    <h4>Notifications</h4>
                    <ul>
                        {notifications.map((note) => (
                            <li key={note.id}>
                                <img
                                    src={notificationIcon}
                                    alt="icon"
                                    className={styles.notificationItemIcon}
                                />
                                <span className={styles.notificationMessage}>{note.text}</span>
                                <button
                                    className={styles.closeNotificationButton}
                                    onClick={() => dismissNotification(note.id)}
                                >
                                    ✖
                                </button>
                            </li>
                        ))}
                    </ul>
                </div>
            )}

            <div className={styles.filters}>
                <select className={styles.select} value={category} onChange={handleCategoryChange}>
                    <option value="">All Categories</option>
                    {categories.map(cat => (
                        <option key={cat.idCategory} value={String(cat.idCategory)}>{cat.name}</option>
                    ))}
                </select>

                <select className={styles.select} value={priceRange} onChange={handlePriceChange}>
                    <option value="">All Prices</option>
                    <option value="0-50">$0 - $50</option>
                    <option value="50-100">$50 - $100</option>
                    <option value="100-200">$100 - $200</option>
                    <option value="200-999">$200+</option>
                </select>

                <select className={styles.select} value={ratingRange} onChange={handleRatingChange}>
                    <option value="">All Ratings</option>
                    <option value="1-2">1 - 2 stars</option>
                    <option value="2-3">2 - 3 stars</option>
                    <option value="3-4">3 - 4 stars</option>
                    <option value="4-5">4 - 5 stars</option>
                </select>
            </div>

            <div className={styles.manageSection}>
                <Link to="/seller/orders">
                    <button className={styles.manageOrdersButton}>Manage Orders</button>
                </Link>
            </div>

            <button className={styles.addProductButton} onClick={toggleAddModal}>Add New Product</button>

            {showAddModal && (
                <div className={styles.modal}>
                    <div className={styles.modalContent}>
                        <button className={styles.closeButton} onClick={toggleAddModal}>X</button>
                        <AddProductForm categories={categories} onClose={toggleAddModal} onProductAdded={loadProducts} />
                    </div>
                </div>
            )}

            {editingProduct && (
                <div className={styles.modal}>
                    <div className={styles.modalContent}>
                        <button className={styles.closeButton} onClick={() => setEditingProduct(null)}>X</button>
                        <EditProductForm
                            product={editingProduct}
                            categories={categories}
                            onClose={() => setEditingProduct(null)}
                            onProductUpdated={loadProducts}
                        />
                    </div>
                </div>
            )}

            <div className={styles.productsGrid}>
                {products.map(product => (
                    <div key={product.idProduct} className={styles.cardWrapper}>
                        <img src={product.image} alt={product.name} className={styles.cardImage} />
                        <div className={styles.cardBody}>
                            <h3 className={styles.cardTitle}>{product.name}</h3>
                            <p className={styles.cardPrice}>${product.price.toFixed(2)}</p>
                            <div className={styles.rating}>{renderStars(product.averageRating)}</div>
                        </div>
                        <div className={styles.cardActions}>
                            <button className={styles.editButton} onClick={() => handleEdit(product)}>Edit</button>
                            <button className={styles.deleteButton} onClick={() => handleDelete(product.idProduct)}>Delete</button>
                            <Link to={`/seller/feedback/${product.idProduct}`}>
                                <button className={styles.feedbackButton}>Manage Feedback</button>
                            </Link>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    );
};

export default SellerPage;

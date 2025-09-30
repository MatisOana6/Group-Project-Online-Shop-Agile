import React, { useState } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import styles from './NavBar.module.css';
import guestIcon from '../../assets/NavBar/guest-icon.png';
import SellerCategoryForm from '../../pages/ProductsPage/SellerCategoryForm';
import '@fortawesome/fontawesome-free/css/all.min.css';

const Navbar = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const [showSellerForm, setShowSellerForm] = useState(false);

    const isGuest = !sessionStorage.getItem("token");
    const userRole = sessionStorage.getItem("role");
    const profileImage = sessionStorage.getItem("profileImage");
    const isProductsPage = location.pathname === '/products';
    const hideUserInfo = ['/', '/login', '/register'].includes(location.pathname);
    const validProfileImage = profileImage && profileImage !== 'null' && profileImage !== '' ? profileImage : guestIcon;

    const handleLogout = () => {
        sessionStorage.clear();
        navigate('/');
    };

    const handleAccountClick = () => {
        navigate('/account');
    };

    const handleStatisticsClick = () => {
        navigate('/stats');
    };


    return (
        <>
            <nav className={styles.navbar}>
                <div className={styles.logo}>TADOR</div>

                <div className={styles.navItems}>
                    {isProductsPage && (
                        <div className={styles.utilityItem} onClick={handleAccountClick} style={{ cursor: 'pointer' }}>
                            <i className="fas fa-user"></i>
                            <span>My Account</span>
                        </div>
                    )}

                    {!hideUserInfo && (
                        <>
                            {userRole === 'ADMIN' && (
                                <button
                                    className={styles.adminButton}
                                    onClick={() => setShowSellerForm(true)}
                                >
                                    Manage Sellers
                                </button>
                            )}

                            {userRole === 'ADMIN' && (
                                <button
                                    className={styles.adminButton}
                                    onClick={() => handleStatisticsClick()}
                                >
                                    Statistics
                                </button>
                            )}

                            {userRole === 'ADMIN' && (
                                <button
                                    className={styles.adminButton}
                                    onClick={() => navigate('/product-history')}
                                >
                                    Product History
                                </button>
                            )}


                            {!isGuest && (
                                <>
                                    <button className={styles.cartButton} onClick={() => navigate('/cart')}>
                                        <i className="fas fa-shopping-cart" style={{ marginRight: '8px' }}></i>
                                        My Cart
                                    </button>

                                    <button className={styles.logoutButton} onClick={handleLogout}>
                                        Logout
                                    </button>
                                </>
                            )}

                            <div className={styles.userSection}>
                                {isGuest ? (
                                    <span className={styles.guestText}>Guest</span>
                                ) : (
                                    <span className={styles.guestText}>
                                        {userRole === 'SELLER' && 'Seller'}
                                        {userRole === 'CLIENT' && 'Client'}
                                        {userRole === 'ADMIN' && 'Admin'}
                                    </span>
                                )}
                                <img
                                    src={validProfileImage}
                                    alt={isGuest ? 'Guest' : 'Profile Picture'}
                                    className={styles.guestIcon}
                                />
                            </div>
                        </>
                    )}
                </div>
            </nav>

            {showSellerForm && (
                <SellerCategoryForm onClose={() => setShowSellerForm(false)} />
            )}
        </>
    );
};

export default Navbar;

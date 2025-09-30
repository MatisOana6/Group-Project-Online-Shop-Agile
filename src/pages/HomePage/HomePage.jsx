import React from 'react';
import styles from './HomePage.module.css';
import homeImage from '../../assets/HomePage/homepage-background.jpg';
import { useNavigate } from 'react-router-dom';

const HomePage = () => {
    const navigate = useNavigate();

    const handleLoginClick = () => {
        navigate('/login');
    };

    const handleRegisterClick = () => {
        navigate('/register');
    };

    return (
        <div className={styles.homeContainer}>
            <div className={styles.homeLeft}>
                <img src={homeImage} alt="Home Page Banner" />
            </div>
            <div className={styles.homeRight}>
                <div className={styles.title}>Online Clothing Store</div>
                <div className={styles.homeButtons}>
                    <button className={styles.loginButton} onClick={handleLoginClick}>LOGIN</button>
                    <button className={styles.registerButton} onClick={handleRegisterClick}>REGISTER</button>
                </div>
            </div>
        </div>
    );
};

export default HomePage;

import React, { useEffect, useState } from 'react';
import styles from './CartPage.module.css';
import {useNavigate} from 'react-router-dom';

const CartPage = () => {
    const [cartItems, setCartItems] = useState([]);
    const navigate = useNavigate();

    useEffect(() => {
        const storedCart = JSON.parse(sessionStorage.getItem('cart') || '[]');
        setCartItems(storedCart);
    }, []);

    const updateCart = (items) => {
        setCartItems(items);
        sessionStorage.setItem('cart', JSON.stringify(items));
    };

    const changeQuantity = (index, delta) => {
        const updatedItems = [...cartItems];
        const newQuantity = updatedItems[index].quantity + delta;

        if (newQuantity >= 1) {
            updatedItems[index].quantity = newQuantity;
            updateCart(updatedItems);
        }
    };

    const handlePlaceOrder = () => {
        navigate('/checkout');
    };

    const removeItem = (index) => {
        const updatedItems = [...cartItems];
        updatedItems.splice(index, 1);
        updateCart(updatedItems);
    };

    const getTotal = () => {
        return cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2);
    };

    return (
        <div className={styles.cartContainer}>
            <h1 className={styles.title}>My Cart</h1>

            {cartItems.length === 0 ? (
                <p className={styles.empty}>Your cart is empty.</p>
            ) : (
                <>
                    {cartItems.map((item, index) => (
                        <div key={index} className={styles.cartItem}>
                            <img src={item.image} alt={item.name} className={styles.image} />
                            <div className={styles.details}>
                                <h3>{item.name}</h3>
                                <p>Price: ${item.price.toFixed(2)}</p>

                                <div className={styles.quantityControls}>
                                    <button onClick={() => changeQuantity(index, -1)} disabled={item.quantity === 1}>-</button>
                                    <span>{item.quantity}</span>
                                    <button onClick={() => changeQuantity(index, 1)}>+</button>
                                </div>

                                <p>Total: ${(item.price * item.quantity).toFixed(2)}</p>

                                <button
                                    className={styles.deleteBtn}
                                    onClick={() => removeItem(index)}
                                >
                                    Delete from Cart
                                </button>
                            </div>
                        </div>
                    ))}

                    <div className={styles.summary}>
                        <h3>Cart Total: ${getTotal()}</h3>
                        <button className={styles.placeOrderBtn}  onClick={handlePlaceOrder} >Place Order</button>
                    </div>
                </>
            )}
        </div>
    );
};

export default CartPage;

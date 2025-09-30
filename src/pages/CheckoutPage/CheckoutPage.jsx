import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { Input, Modal, ModalHeader, ModalBody, ModalFooter } from 'reactstrap';
import './CheckoutPage.css';
import {placeOrder} from "./api/CheckoutApi";

const CheckoutPage = () => {
    const [address, setAddress] = useState('');
    const [region, setRegion] = useState('');
    const [phone, setPhone] = useState('');
    const [message, setMessage] = useState('');
    const [success, setSuccess] = useState(false);
    const [cartItems, setCartItems] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const storedCart = JSON.parse(sessionStorage.getItem('cart') || '[]');
        setCartItems(storedCart);
    }, []);

    const handlePlaceOrder = async () => {
        if (!address || !region || !phone) {
            setMessage('All fields are required.');
            setSuccess(false);
            return;
        }

        const orderData = {
            delivery: {
                address,
                region,
                phone
            },
            products: cartItems.map(item => ({
                productId: item.id,
                quantity: item.quantity,
            }))
        };

        try {
            const response = await placeOrder(orderData);
            console.log("Order placed successfully:", response);
            sessionStorage.removeItem('cart');
            setMessage('Order placed successfully!');
            setSuccess(true);
            setShowModal(true);
        } catch (error) {
            setMessage(`Unable to place order!`);
            setSuccess(false);
        }
    };

    const handleConfirm = () => {
        setShowModal(false);
        navigate('/products');
    };

    const getTotal = () => {
        return cartItems.reduce((sum, item) => sum + item.price * item.quantity, 0).toFixed(2);
    };

    return (
        <div className="checkoutContainer">
            <div className="formContainer">
                <h1 className="checkoutTitle">Checkout</h1>
                {message && (
                    <div className={`message ${success ? 'success' : 'error'}`}>
                        {message}
                    </div>
                )}
                <div className="formGroup">
                    <label className="label">Address</label>
                    <Input
                        type="text"
                        placeholder="Address"
                        value={address}
                        onChange={(e) => setAddress(e.target.value)}
                        className="input"
                    />
                </div>
                <div className="formGroup">
                    <label className="label">Region</label>
                    <Input
                        type="text"
                        placeholder="Region"
                        value={region}
                        onChange={(e) => setRegion(e.target.value)}
                        className="input"
                    />
                </div>
                <div className="formGroup">
                    <label className="label">Phone</label>
                    <Input
                        type="text"
                        placeholder="Phone"
                        value={phone}
                        onChange={(e) => setPhone(e.target.value)}
                        className="input"
                    />
                </div>
                <div className="buttonContainer">
                    <button className="placeOrderBtn" onClick={handlePlaceOrder}>Place Order</button>
                </div>
            </div>

            <div className="summaryContainer">
                <h2 className="summaryTitle">Order Summary</h2>
                {cartItems.map((item, index) => (
                    <div key={index} className="summaryItem">
                        <span>{item.name} (x{item.quantity})</span>
                        <span>${(item.price * item.quantity).toFixed(2)}</span>
                    </div>
                ))}
                <div className="total">Total: ${getTotal()}</div>
            </div>

            <Modal isOpen={showModal} toggle={handleConfirm}>
                <ModalHeader className="modalHeader">Order Confirmation</ModalHeader>
                <ModalBody className="modalBody">
                    Your order has been placed successfully!
                </ModalBody>
                <ModalFooter className="modalFooter">
                    <button className="confirmButton" onClick={handleConfirm}>OK</button>
                </ModalFooter>
            </Modal>
        </div>
    );
};

export default CheckoutPage;


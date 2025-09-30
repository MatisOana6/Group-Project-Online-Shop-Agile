import React, { useEffect, useState } from 'react';
import { getOrderHistory } from './api/OrderApi';
import './MyAccountPage.css';

const AccountOrders = () => {
    const [orders, setOrders] = useState([]);
    const [expandedOrderIndex, setExpandedOrderIndex] = useState(null);

    useEffect(() => {
        getOrderHistory()
            .then(data => {
                console.log(data);
                setOrders(data);
            })
            .catch(err => console.error(err));
    }, []);

    const toggleOrder = (index) => {
        setExpandedOrderIndex(expandedOrderIndex === index ? null : index);
    };

    if (orders.length === 0) {
        return <p>No orders found.</p>;
    }

    return (
        <div className="account-orders">
            <h3>My Orders</h3>
            {orders.map((order, idx) => (
                <div key={idx} className="order-card">
                    <div
                        className="order-header"
                        onClick={() => toggleOrder(idx)}
                        style={{ cursor: 'pointer', fontWeight: 'bold', marginBottom: '0.5rem' }}
                    >
                        Order from {new Date(order.date).toLocaleDateString()}
                    </div>

                    {expandedOrderIndex === idx && (
                        <div className="order-details">
                            <p><strong>Order Status:</strong> {order.status}</p>
                            <p><strong>Total:</strong> ${order.total.toFixed(2)}</p>

                            <p><strong>Items:</strong></p>

                            <ul>
                                {order.products.map((p, i) => (
                                    <li key={i}>
                                        <strong>{p.name}</strong> — Quantity: {p.quantity}, Unit Price: ${p.price.toFixed(2)}, Status: {p.status} <br />
                                    </li>
                                ))}
                            </ul>
                        </div>
                    )}
                </div>
            ))}
        </div>
    );
};

export default AccountOrders;

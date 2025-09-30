import React, { useEffect, useState } from 'react';
import styles from './SellerOrderPage.module.css';

const ORDER_STATUSES = [
    "PENDING", "SHIPPED", "CANCELLED"
];

const SellerOrderPage = () => {
    const [orders, setOrders] = useState([]);

    useEffect(() => {
        const token = sessionStorage.getItem("token");
        fetch("http://localhost:8082/api/orders/seller", {
            headers: { Authorization: `Bearer ${token}` }
        })
            .then(res => res.json())
            .then(setOrders)
            .catch(console.error);
    }, []);

    const handleStatusChange = async (orderId, orderItemId, newStatus) => {
        const token = sessionStorage.getItem("token");

        const res = await fetch(`http://localhost:8082/api/orders/seller/${orderId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify({
                orderItemId: orderItemId,
                newStatus
            }),
        });

        if (res.ok) {
            const updated = await res.json();
            setOrders(prev =>
                prev.map(order =>
                    order.orderId === orderId
                        ? {
                            ...order,
                            products: order.products.map(p =>
                                p.orderItemId === orderItemId
                                    ? { ...p, status: updated.status } : p
                            )
                        }
                        : order
                )
            );
        } else {
            const errorMsg = await res.text();
            alert("Failed to update order status: " + errorMsg);
        }
    };


    return (
        <div className={styles.container}>
            <h1 className={styles.title}>Manage Orders</h1>
            {orders.length === 0 ? (
                <p>No orders found.</p>
            ) : (
                orders.map(order => (
                    <div key={order.orderId} className={styles.orderCard}>
                        <h3>Order ID: {order.orderId}</h3>
                        <p><strong>Address:</strong> {order.address}, {order.region}</p>
                        <p><strong>Phone:</strong> {order.phone}</p>
                        <p><strong>Total:</strong> ${order.total.toFixed(2)}</p>
                        <table className={styles.table}>
                            <thead>
                            <tr>
                                <th>Product</th>
                                <th>Quantity</th>
                                <th>Price per item</th>
                                <th>Status</th>
                                <th>Change Status</th>
                            </tr>
                            </thead>
                            <tbody>
                            {order.products.map(product => (
                                <tr key={product.orderItemId}>
                                    <td>{product.name}</td>
                                    <td>{product.quantity}</td>
                                    <td>${product.price.toFixed(2)}</td>
                                    <td>{product.status}</td>
                                    <td>
                                        <select
                                            value={product.status}
                                            onChange={(e) =>
                                                handleStatusChange(order.orderId, product.orderItemId, e.target.value)
                                            }
                                        >
                                            {ORDER_STATUSES.map(status => (
                                                <option key={status} value={status}>{status}</option>
                                            ))}
                                        </select>
                                    </td>
                                </tr>
                            ))}
                            </tbody>
                        </table>
                    </div>
                ))
            )}
        </div>
    );
};

export default SellerOrderPage;

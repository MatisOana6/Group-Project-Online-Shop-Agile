import React, { useEffect, useState } from 'react';
import styles from './ProductHistoryPage.module.css';
import { fetchAllProducts, fetchProductHistory } from './api/ProductHistoryApi';

const ProductHistoryPage = () => {
    const [productHistories, setProductHistories] = useState([]);
    const [filter, setFilter] = useState('all');

    useEffect(() => {
        const loadHistories = async () => {
            try {
                const products = await fetchAllProducts();
                const allHistories = [];

                for (const product of products) {
                    const history = await fetchProductHistory(product.idProduct);
                    if (history.length > 0) {
                        allHistories.push({
                            productName: product.name,
                            productId: product.idProduct,
                            changes: history
                        });
                    }
                }

                setProductHistories(allHistories);
            } catch (err) {
                console.error('Error loading product histories:', err);
                alert('Failed to load product change history.');
            }
        };

        loadHistories();
    }, []);

    const handleFilterChange = (e) => {
        setFilter(e.target.value);
    };

    const filteredChanges = (changes) => {
        if (filter === 'all') return changes;
        return changes.filter(change => change.fieldChanged === filter);
    };

    return (
        <div className={styles.container}>
            <h2>All Product Change History</h2>

            <div className={styles.filterBox}>
                <label htmlFor="filter">Filter by field:</label>
                <select id="filter" value={filter} onChange={handleFilterChange} className={styles.select}>
                    <option value="all">All</option>
                    <option value="price">Price</option>
                    <option value="stock">Stock</option>
                </select>
            </div>

            {productHistories.length === 0 ? (
                <p>No changes found for any product.</p>
            ) : (
                productHistories.map((entry) => {
                    const changes = filteredChanges(entry.changes);
                    if (changes.length === 0) return null;

                    return (
                        <div key={entry.productId} className={styles.historyBlock}>
                            <h3>{entry.productName}</h3>
                            <ul className={styles.changeList}>
                                {changes.map((change, idx) => (
                                    <li key={idx}>
                                        <strong>{new Date(change.changedAt).toLocaleString()}</strong>: Field <em>{change.fieldChanged}</em> was changed from <strong>{change.oldValue}</strong> to <strong>{change.newValue}</strong>.
                                    </li>
                                ))}
                            </ul>
                        </div>
                    );
                })
            )}
        </div>
    );
};

export default ProductHistoryPage;

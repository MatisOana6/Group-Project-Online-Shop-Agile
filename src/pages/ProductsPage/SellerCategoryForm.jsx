import React, { useEffect, useState } from 'react';
import styles from './SellerCategoryForm.module.css';
import { fetchSellers } from './api/SellersApi';
import { fetchCategories } from './api/CategoriesApi';
import {
    assignCategory,
    removeCategory,
    fetchAssignedCategories
} from './api/SellerCategoryApi';
import defaultProfile from '../../assets/ProductsPage/SellerCategoryForm/default-profile-image.jpg';

const SellerCategoryForm = ({ onClose }) => {
    const [sellers, setSellers] = useState([]);
    const [categories, setCategories] = useState([]);
    const [selectedSeller, setSelectedSeller] = useState(null);
    const [assignedCategories, setAssignedCategories] = useState([]); // fetched from backend
    const [pendingCategories, setPendingCategories] = useState([]); // updated in UI
    const [showSuccess, setShowSuccess] = useState(false);

    useEffect(() => {
        const init = async () => {
            try {
                const [sellersData, categoriesData] = await Promise.all([
                    fetchSellers(),
                    fetchCategories()
                ]);
                setSellers(sellersData);
                setCategories(categoriesData);
            } catch (err) {
                console.error('Init error:', err);
            }
        };
        init();
    }, []);

    useEffect(() => {
        const loadAssigned = async () => {
            if (!selectedSeller) return;
            try {
                const data = await fetchAssignedCategories(selectedSeller.idUser);
                setAssignedCategories(data);
                setPendingCategories(data);
            } catch (err) {
                console.error('Failed to fetch assigned categories:', err);
            }
        };
        loadAssigned();
    }, [selectedSeller]);

    const handleSellerChange = (e) => {
        const seller = sellers.find(s => s.idUser === e.target.value);
        setSelectedSeller(seller);
        setAssignedCategories([]);
        setPendingCategories([]);
    };

    const handleCategoryAdd = (e) => {
        const categoryId = e.target.value;
        if (!categoryId) return;

        const exists = pendingCategories.some(
            cat => cat.categoryId === categoryId || cat.idCategory === categoryId
        );

        if (!exists) {
            const category = categories.find(c => c.idCategory === categoryId);
            const normalized = {
                categoryId: category.idCategory,
                categoryName: category.name
            };
            setPendingCategories(prev => [...prev, normalized]);
        }
    };

    const handleCategoryRemoveUI = (categoryId) => {
        setPendingCategories(prev => prev.filter(cat => cat.categoryId !== categoryId));
    };

    const handleSubmit = async () => {
        if (!selectedSeller) return;

        try {
            const originalIds = assignedCategories.map(c => c.categoryId);
            const updatedIds = pendingCategories.map(c => c.categoryId);

            const toAdd = updatedIds.filter(id => !originalIds.includes(id));
            const toRemove = originalIds.filter(id => !updatedIds.includes(id));

            await Promise.all([
                ...toAdd.map(id => assignCategory(selectedSeller.idUser, id)),
                ...toRemove.map(id => removeCategory(selectedSeller.idUser, id))
            ]);

            setShowSuccess(true);
            setTimeout(() => {
                setShowSuccess(false);
                onClose();
            }, 1500);
        } catch (err) {
            console.error('Error submitting assignments:', err);
            alert('Something went wrong while saving.');
        }
    };

    return (
        <div className={styles.overlay}>
            <div className={styles.modal}>
                <button className={styles.closeButton} onClick={onClose}>x</button>

                <div className={styles.container}>
                    <div className={styles.profileSection}>
                        {selectedSeller ? (
                            <img
                                src={selectedSeller.profileImage?.startsWith('data:')
                                    ? selectedSeller.profileImage
                                    : `data:image/jpeg;base64,${selectedSeller.profileImage}`}
                                alt="Seller"
                                className={styles.profileImage}
                            />
                        ) : (
                            <img src={defaultProfile} alt="Default" className={styles.defaultProfileImage} />
                        )}
                    </div>

                    <div className={styles.formSection}>
                        <div className={styles.dropdownGroup}>
                            <label>Select User:</label>
                            <select className={styles.select} onChange={handleSellerChange} value={selectedSeller?.idUser || ''}>
                                <option value=''>Select a Seller</option>
                                {sellers.map(s => (
                                    <option key={s.idUser} value={s.idUser}>{s.name}</option>
                                ))}
                            </select>
                        </div>

                        <div className={styles.dropdownGroup}>
                            <label>Assign Categories:</label>
                            <select className={styles.select} onChange={handleCategoryAdd}>
                                <option value=''>Select a Category</option>
                                {categories.map(cat => (
                                    <option key={cat.idCategory} value={cat.idCategory}>{cat.name}</option>
                                ))}
                            </select>
                        </div>

                        <div className={styles.selectedCategories}>
                            {pendingCategories.map(cat => (
                                <div key={cat.categoryId} className={styles.categoryTag}>
                                    {cat.categoryName}
                                    <button onClick={() => handleCategoryRemoveUI(cat.categoryId)} className={styles.removeButton}>x</button>
                                </div>
                            ))}
                        </div>

                        {showSuccess && <div className={styles.successMessage}>Categories updated successfully!</div>}

                        <button className={styles.submitButton} onClick={handleSubmit}>Submit</button>
                    </div>
                </div>
            </div>
        </div>
    );
};

export default SellerCategoryForm;

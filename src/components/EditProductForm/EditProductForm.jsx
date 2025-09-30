import React, { useState, useEffect } from 'react';
import styles from './EditProductForm.module.css';
import { updateProduct, fetchAssignedCategories } from "../../pages/SellerPage/api/SellerApi";

const EditProductForm = ({ product, onClose, onProductUpdated }) => {
    const [categories, setCategories] = useState([]);
    const [updatedProduct, setUpdatedProduct] = useState({ ...product });
    const [errors, setErrors] = useState({});

    useEffect(() => {
        const loadCategories = async () => {
            const token = sessionStorage.getItem('token');
            try {
                const data = await fetchAssignedCategories(token);
                setCategories(data);
            } catch (error) {
                console.error("Failed to load categories", error);
            }
        };
        loadCategories();
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setUpdatedProduct(prev => ({ ...prev, [name]: value }));
        validateField(name, value);
    };

    const validateField = (name, value) => {
        let error = "";
        if (name === "name" && !value) error = "Product name is required";
        if (name === "description" && !value) error = "Description is required";
        if (name === "price" && (value === "" || value < 1)) error = "Price must be greater than 0";
        if (name === "quantity" && (value === "" || value < 1)) error = "Stock must be greater than 0";
        if (name === "categoryName" && !value) error = "Category is required";
        if (name === "image" && !value) error = "Image is required";
        setErrors(prev => ({ ...prev, [name]: error }));
        return error === "";
    };

    const validateForm = () => {
        const fields = ["name", "description", "price", "quantity", "categoryName", "image"];
        return fields.every(field => validateField(field, updatedProduct[field]));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!validateForm()) {
            alert("Please fix the validation errors.");
            return;
        }

        try {
            const token = sessionStorage.getItem('token');
            await updateProduct(updatedProduct, token);
            alert("Product updated successfully!");
            onProductUpdated();
            onClose();
        } catch (error) {
            console.error("Error updating product:", error);
            alert("Error updating product");
        }
    };

    return (
        <div className={styles.formContainer2}>
            <h3>Edit Product</h3>
            <form onSubmit={handleSubmit}>
                {errors.name && <div className={styles.error}>{errors.name}</div>}
                <input type="text" name="name" value={updatedProduct.name} onChange={handleChange}
                       placeholder="Product name"/>

                {errors.description && <div className={styles.error}>{errors.description}</div>}
                <textarea name="description" value={updatedProduct.description} onChange={handleChange}
                          placeholder="Description"/>

                {errors.price && <div className={styles.error}>{errors.price}</div>}
                <input type="number" name="price" value={updatedProduct.price} onChange={handleChange}
                       placeholder="Price" min="0"/>

                {errors.quantity && <div className={styles.error}>{errors.quantity}</div>}
                <input type="number" name="quantity" value={updatedProduct.quantity} onChange={handleChange}
                       placeholder="Stock" min="0"/>

                {errors.idCategory && <div className={styles.error}>{errors.idCategory}</div>}
                <select
                    name="categoryName"
                    value={updatedProduct.categoryName}
                    onChange={(e) => {
                        const selectedName = e.target.value;
                        setUpdatedProduct((prev) => ({
                            ...prev,
                            categoryName: selectedName
                        }));
                        validateField("categoryName", selectedName);
                    }}
                >
                    <option value="">Select Category</option>
                    {categories.map(cat => (
                        <option key={cat.categoryId} value={cat.categoryName}>
                            {cat.categoryName}
                        </option>
                    ))}
                </select>


                {errors.image && <div className={styles.error}>{errors.image}</div>}
                <input type="text" name="image" value={updatedProduct.image} onChange={handleChange}
                       placeholder="Image URL / Base64"/>

                <button type="submit">Update</button>
                <button type="button" onClick={onClose}>Cancel</button>
            </form>
        </div>
    );
};

export default EditProductForm;

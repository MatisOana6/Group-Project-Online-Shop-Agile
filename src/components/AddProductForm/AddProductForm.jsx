import React, { useState, useEffect } from 'react';
import { createProduct } from "../../pages/SellerPage/api/AddProduct";
import styles from './AddProductForm.module.css';
import { fetchAssignedCategories } from "../../pages/SellerPage/api/SellerApi";

const AddProductForm = ({ onClose, onProductAdded }) => {
    const [categories, setCategories] = useState([]);
    const [newProduct, setNewProduct] = useState({
        name: '',
        description: '',
        price: '',
        stock: '',
        category: '',
        image: null
    });

    const [errors, setErrors] = useState({});

    useEffect(() => {
        const fetchCategories = async () => {
            try {
                const token = sessionStorage.getItem('token');
                const data = await fetchAssignedCategories(token);
                setCategories(data);
                console.log("Assigned categories:", data);
                setCategories(data);

            } catch (err) {
                console.error("Failed to fetch assigned categories", err);
            }
        };

        fetchCategories();
    }, []);


    const handleChange = (e) => {
        const { name, value } = e.target;
        setNewProduct(prev => ({ ...prev, [name]: value }));
        validateField(name, value);
    };

    const handleImageChange = async (e) => {
        const file = e.target.files[0];
        if (file) {
            const base64Image = await convertToBase64(file);
            setNewProduct(prev => ({ ...prev, image: base64Image }));
            validateField('image', base64Image);
        }
    };

    const convertToBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.onloadend = () => resolve(reader.result);
            reader.onerror = reject;
            reader.readAsDataURL(file);
        });
    };

    const validateField = (name, value) => {
        let error = '';
        if (name === 'name' && !value) error = 'Product name is required';
        if (name === 'description' && !value) error = 'Description is required';
        if (name === 'price' && (!value || value <= 0)) error = 'Price must be greater than 0';
        if (name === 'stock' && (!value || value <= 0)) error = 'Stock must be greater than 0';
        if (name === 'category' && !value) error = 'Category is required';
        if (name === 'image' && !value) error = 'Image is required';

        setErrors(prevErrors => ({ ...prevErrors, [name]: error }));
        return error === '';
    };

    const validateForm = () => {
        const fields = ['name', 'description', 'price', 'stock', 'category', 'image'];
        return fields.every(field => validateField(field, newProduct[field]));
    };

    const handleSubmit = async (e) => {
        e.preventDefault();

        if (!validateForm()) {
            alert('Please fix the validation errors first.');
            return;
        }

        const productData = {
            name: newProduct.name,
            description: newProduct.description,
            price: parseFloat(newProduct.price),
            quantity: parseInt(newProduct.stock),
            idCategory: newProduct.category,
            image: newProduct.image
        };

        try {
            const response = await createProduct(productData);
            alert('Product added successfully!');
            onProductAdded();
            onClose();
        } catch (err) {
            console.error('Error adding product:', err);
            alert('Error occurred while adding the product');
        }
    };

    return (
        <div className={styles.formContainer}>
            <h3>Add New Product</h3>
            <form onSubmit={handleSubmit}>
                {errors.name && <div className={styles.error}>{errors.name}</div>}
                <input type="text" name="name" placeholder="Product Name" value={newProduct.name} onChange={handleChange} />

                {errors.description && <div className={styles.error}>{errors.description}</div>}
                <textarea name="description" placeholder="Description" value={newProduct.description} onChange={handleChange} />

                {errors.price && <div className={styles.error}>{errors.price}</div>}
                <input type="number" name="price" placeholder="Price" value={newProduct.price} onChange={handleChange} />

                {errors.stock && <div className={styles.error}>{errors.stock}</div>}
                <input type="number" name="stock" placeholder="Stock" value={newProduct.stock} onChange={handleChange} />

                {errors.category && <div className={styles.error}>{errors.category}</div>}
                <select name="category" value={newProduct.category} onChange={handleChange}>
                    <option value="">Select Category</option>
                    {categories.map(cat => (
                        <option key={cat.categoryId} value={cat.categoryId}>{cat.categoryName}</option>
                    ))}

                </select>

                {errors.image && <div className={styles.error}>{errors.image}</div>}
                <input type="file" name="image" onChange={handleImageChange} />

                <button type="submit">Add Product</button>
                <button type="button" onClick={onClose}>Cancel</button>
            </form>
        </div>
    );
};

export default AddProductForm;

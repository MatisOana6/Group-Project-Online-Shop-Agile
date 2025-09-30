import React, { useState, useEffect } from 'react';
import './AdminPage.css';
import {
    fetchAllCategories,
    createCategory,
    updateCategory,
    deleteCategory
} from './api/CategoryApi';

const AdminPage = () => {
    const [categories, setCategories] = useState([]);
    const [newCategoryName, setNewCategoryName] = useState('');
    const [editingCategory, setEditingCategory] = useState(null);
    const [editedCategoryName, setEditedCategoryName] = useState('');

    const token = sessionStorage.getItem('token');

    useEffect(() => {
        loadCategories();
    }, []);

    const loadCategories = async () => {
        try {
            const data = await fetchAllCategories(token);
            setCategories(data);
        } catch (err) {
            console.error('Failed to fetch categories', err);
        }
    };

    const handleCreate = async () => {
        if (!newCategoryName.trim()) return;
        try {
            await createCategory({ name: newCategoryName }, token);
            setNewCategoryName('');
            loadCategories();
        } catch (err) {
            console.error('Error creating category', err);
        }
    };

    const handleDelete = async (id) => {
        try {
            await deleteCategory(id, token);
            loadCategories();
        } catch (err) {
            console.error('Error deleting category', err);
        }
    };

    const handleEdit = async () => {
        if (!editedCategoryName.trim()) return;
        try {
            await updateCategory(editingCategory.idCategory, { name: editedCategoryName }, token);
            setEditingCategory(null);
            setEditedCategoryName('');
            loadCategories();
        } catch (err) {
            console.error('Error updating category', err);
        }
    };

    return (
        <div className="manage-categories">
            <h2>Category Management</h2>
            <div className="category-form">
                <input
                    type="text"
                    value={newCategoryName}
                    onChange={(e) => setNewCategoryName(e.target.value)}
                    placeholder="Enter new category name"
                />
                <span className="action-button" onClick={handleCreate}>Create</span>
            </div>
            <table>
                <thead>
                <tr>
                    <th>Category</th>
                    <th>Options</th>
                </tr>
                </thead>
                <tbody>
                {categories.map((cat) => (
                    <tr key={cat.idCategory}>
                        <td>
                            {editingCategory?.idCategory === cat.idCategory ? (
                                <input
                                    type="text"
                                    value={editedCategoryName}
                                    onChange={(e) => setEditedCategoryName(e.target.value)}
                                />
                            ) : (
                                cat.name
                            )}
                        </td>
                        <td>
                            {editingCategory?.idCategory === cat.idCategory ? (
                                <>
                                    <span className="action-button" onClick={handleEdit}>Update</span>
                                    <span className="action-button" onClick={() => setEditingCategory(null)}>Discard</span>
                                </>
                            ) : (
                                <>
                    <span className="action-button" onClick={() => {
                        setEditingCategory(cat);
                        setEditedCategoryName(cat.name);
                    }}>Edit</span>
                                    <span className="action-button" onClick={() => handleDelete(cat.idCategory)}>Remove</span>
                                </>
                            )}
                        </td>
                    </tr>
                ))}
                </tbody>
            </table>
        </div>
    );
};

export default AdminPage;

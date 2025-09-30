import React, { useState } from 'react';
import { Button, Form, FormGroup, Input } from 'reactstrap';
import { useNavigate } from 'react-router-dom';
import './RegisterPage.css';
import { register } from './api/RegisterApi';

const RegisterPage = () => {
    const navigate = useNavigate();
    const [formData, setFormData] = useState({
        name: '',
        email: '',
        password: '',
        role: '',
        profileImage: '',
        address: '',
        phoneNumber: ''
    });

    const [errors, setErrors] = useState({});
    const [success, setSuccess] = useState('');

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
        setErrors(prev => ({ ...prev, [name]: '' }));
    };

    const handleImageChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const maxSize = 2 * 1024 * 1024; // 2MB
            if (file.size > maxSize) {
                setErrors(prev => ({ ...prev, profileImage: 'Image is too large. Max size is 2MB.' }));
                return;
            }

            const reader = new FileReader();
            reader.onloadend = () => {
                const img = new Image();
                img.onload = () => {
                    const canvas = document.createElement("canvas");
                    const ctx = canvas.getContext("2d");
                    const maxWidth = 500;
                    const scale = maxWidth / img.width;
                    canvas.width = maxWidth;
                    canvas.height = img.height * scale;
                    ctx.drawImage(img, 0, 0, canvas.width, canvas.height);
                    const resizedDataUrl = canvas.toDataURL(file.type);
                    setFormData(prev => ({ ...prev, profileImage: resizedDataUrl }));
                    setErrors(prev => ({ ...prev, profileImage: '' }));
                };
                img.src = reader.result;
            };
            reader.readAsDataURL(file);
        }
    };

    const validate = () => {
        const emailRegex = /\S+@\S+\.\S+/;
        const phoneRegex = /^[0-9]{10}$/;
        const newErrors = {};

        if (!formData.name) newErrors.name = 'Name is required';
        if (!formData.email) newErrors.email = 'Email is required';
        else if (!emailRegex.test(formData.email)) newErrors.email = 'Invalid email format';
        if (!formData.password) newErrors.password = 'Password is required';
        else if (formData.password.length < 6) newErrors.password = 'Minimum 6 characters';
        if (!formData.role) newErrors.role = 'Please select a role';
        if (!formData.address) newErrors.address = 'Address is required';
        if (!formData.phoneNumber) newErrors.phoneNumber = 'Phone number is required';
        else if (!phoneRegex.test(formData.phoneNumber)) newErrors.phoneNumber = 'Must be 10 digits';
        if (!formData.profileImage) newErrors.profileImage = 'Profile image is required';
        return newErrors;
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        const validationErrors = validate();
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            setSuccess('');
            return;
        }

        register(formData)
            .then(() => {
                setSuccess('Registration successful! Redirecting to login page...');
                setErrors({});
                setTimeout(() => navigate('/login'), 2000);
            })
            .catch((err) => {
                setErrors({ general: err.message });
                setSuccess('');
            });
    };

    return (
        <div className="register-wrapper">
            <div className="register-box">
                <h2 className="register-title">Sign Up</h2>

                {success && <p className="register-message success">{success}</p>}
                {errors.general && <p className="register-message error">{errors.general}</p>}

                <Form onSubmit={handleSubmit}>
                    <FormGroup>
                        <Input type="text" name="name" placeholder="Full Name" value={formData.name} onChange={handleChange} className="register-input" />
                        {errors.name && <p className="register-message error">{errors.name}</p>}
                    </FormGroup>
                    <FormGroup>
                        <Input type="email" name="email" placeholder="Email Address" value={formData.email} onChange={handleChange} className="register-input" />
                        {errors.email && <p className="register-message error">{errors.email}</p>}
                    </FormGroup>
                    <FormGroup>
                        <Input type="password" name="password" placeholder="Password" value={formData.password} onChange={handleChange} className="register-input" />
                        {errors.password && <p className="register-message error">{errors.password}</p>}
                    </FormGroup>
                    <FormGroup>
                        <Input type="select" name="role" value={formData.role} onChange={handleChange}
                               className="register-input">
                            <option value="" disabled>Role</option>
                            <option value="CLIENT">Client</option>
                            <option value="SELLER">Seller</option>
                        </Input>
                        {errors.role && <p className="register-message error">{errors.role}</p>}
                    </FormGroup>
                    <FormGroup>
                        <Input type="text" name="address" placeholder="Address" value={formData.address} onChange={handleChange} className="register-input" />
                        {errors.address && <p className="register-message error">{errors.address}</p>}
                    </FormGroup>
                    <FormGroup>
                        <Input type="text" name="phoneNumber" placeholder="Phone Number" value={formData.phoneNumber} onChange={handleChange} className="register-input" />
                        {errors.phoneNumber && <p className="register-message error">{errors.phoneNumber}</p>}
                    </FormGroup>
                    <FormGroup>
                        <p className="register-label">Upload your profile picture</p>
                        <Input type="file" name="profileImage" accept="image/*" onChange={handleImageChange} className="register-input" />
                        {errors.profileImage && <p className="register-message error">{errors.profileImage}</p>}
                    </FormGroup>

                    <div className="register-button-wrapper">
                        <Button type="submit" className="register-button">
                            Register
                        </Button>
                    </div>


                    <p className="register-footer">
                        Already have an account? <a href="/login">Login</a>
                    </p>
                </Form>
            </div>
        </div>
    );
};

export default RegisterPage;

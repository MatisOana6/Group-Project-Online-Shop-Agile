import React, { useState, useEffect } from 'react';
import { Button, Form, FormGroup, Input } from 'reactstrap';
import { useNavigate, useLocation } from 'react-router-dom';
import './ResetPasswordPage.module.css';
import {resetPassword} from "./api/ResetPasswordPageApi";

const ResetPasswordPage = () => {
    const [newPassword, setNewPassword] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();
    const location = useLocation();

    const queryParams = new URLSearchParams(location.search);
    const token = queryParams.get('token');

    const handleChange = (e) => {
        setNewPassword(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        resetPassword({ token, newPassword })
            .then((response) => {
                setMessage('Password reset successfully');
                setError('');
                setTimeout(() => navigate('/login'), 3000);
            })
            .catch((error) => {
                setMessage('');
                setError('Error resetting password, please try again.');
            });
    };

    return (
        <div className="auth-wrapper">
            <div className="auth-box">
                <h2 className="auth-title">Reset Password</h2>
                <Form onSubmit={handleSubmit}>
                    <FormGroup>
                        <Input
                            type="password"
                            name="newPassword"
                            placeholder="Enter your new password"
                            value={newPassword}
                            onChange={handleChange}
                            required
                            className="auth-input"
                        />
                    </FormGroup>
                    {message && <p className="auth-message success">{message}</p>}
                    {error && <p className="auth-message error">{error}</p>}
                    <div className="login-button-wrapper">
                        <Button type="submit" className="auth-button">Reset Password</Button>
                    </div>
                </Form>
            </div>
        </div>
    );
};

export default ResetPasswordPage;

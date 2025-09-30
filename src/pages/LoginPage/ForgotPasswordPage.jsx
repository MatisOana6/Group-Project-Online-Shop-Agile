import React, { useState } from 'react';
import { Button, Form, FormGroup, Input } from 'reactstrap';

import { useNavigate } from 'react-router-dom';
import './ForgotPasswordPage.module.css';
import {forgotPassword} from "./api/ForgotPasswordApi";

const ForgotPasswordPage = () => {
    const [email, setEmail] = useState('');
    const [message, setMessage] = useState('');
    const [error, setError] = useState('');
    const navigate = useNavigate();

    const handleChange = (e) => {
        setEmail(e.target.value);
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        forgotPassword({ email })
            .then((response) => {
                setMessage('Check your email for a reset link');
                setError('');
                setTimeout(() => navigate('/login'), 3000);
            })
            .catch((error) => {
                setMessage('');
                setError(`Error: ${error.message || 'Failed to send reset link, please try again.'}`);
            });
    };

    return (
        <div className="auth-wrapper">
            <div className="auth-box">
                <h2 className="auth-title">Forgot Password</h2>
                <Form onSubmit={handleSubmit}>
                    <FormGroup>
                        <Input
                            type="email"
                            name="email"
                            placeholder="Enter your email address"
                            value={email}
                            onChange={handleChange}
                            required
                            className="auth-input"
                        />
                    </FormGroup>
                    {message && <p className="auth-message success">{message}</p>}
                    {error && <p className="auth-message error">{error}</p>}  {/* Afișăm eroarea aici */}
                    <div className="login-button-wrapper">
                        <Button type="submit" className="auth-button">Send Reset Link</Button>
                    </div>
                </Form>
            </div>
        </div>
    );
};

export default ForgotPasswordPage;

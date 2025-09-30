import React, { useState } from 'react';
import { Button, Form, FormGroup, Input } from 'reactstrap';
import { Link, useNavigate } from 'react-router-dom';
import './LoginPage.css';
import { login } from './api/LoginApi';

const LoginPage = () => {
    const [credentials, setCredentials] = useState({
        email: '',
        password: '',
        role: ''
    });

    const navigate = useNavigate();

    const handleChange = (e) => {
        setCredentials({
            ...credentials,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        login(credentials)
            .then(response => {
                console.log('Login successful:', response);
                console.log('Response data:', response);

                const token = response.token;
                const role = response.role;

                if (token && role) {
                    sessionStorage.setItem("token", token);
                    sessionStorage.setItem("role", role);

                    if (role === 'SELLER') {
                        navigate('/seller');
                    } else if (role === 'ADMIN') {
                        navigate('/admin');
                    } else {
                        navigate('/products');
                    }

                } else {
                    console.log("No token or role in the response");
                }
            })
            .catch(error => {
                console.log('Login failed:', error);
            });
    };

    return (
        <div className="auth-wrapper">
            <div className="auth-box">
                <h2 className="auth-title">Welcome</h2>
                <Form onSubmit={handleSubmit}>
                    <FormGroup>
                        <Input
                            type="email"
                            name="email"
                            placeholder="Email Address"
                            value={credentials.email}
                            onChange={handleChange}
                            required
                            className="auth-input"
                        />
                    </FormGroup>
                    <FormGroup>
                        <Input
                            type="password"
                            name="password"
                            placeholder="Password"
                            value={credentials.password}
                            onChange={handleChange}
                            required
                            className="auth-input"
                        />
                    </FormGroup>

                    <div className="auth-links">
                        <Link to="/forgot-password">Forgot Password?</Link>
                    </div>

                    <div className="login-button-wrapper">
                        <Button type="submit" className="auth-button">
                            Login
                        </Button>
                    </div>

                    <p className="auth-footer">
                        Don’t have an account? <Link to="/register">Sign Up</Link>
                    </p>
                </Form>
            </div>
        </div>
    );
};

export default LoginPage;

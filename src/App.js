import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import NavBar from "./components/NavBar/NavBar";
import HomePage from "./pages/HomePage/HomePage";
import LoginPage from "./pages/LoginPage/LoginPage";
import RegisterPage from "./pages/RegisterPage/RegisterPage";
import ProductsPage from "./pages/ProductsPage/ProductsPage";
import SellerPage from "./pages/SellerPage/SellerPage";
import ProductHistoryPage from "./pages/ProductHistoryPage/ProductHistoryPage";
import AdminPage from "./pages/AdminPage/AdminPage";
import ProductDetailsPage from "./pages/ProductsPage/ProductPage/ProductDetailsPage";
import CartPage from "./pages/ProductsPage/CartPage/CartPage";
import CheckoutPage from "./pages/CheckoutPage/CheckoutPage";
import MyAccountPage from "./pages/MyAccountPage/MyAccountPage";
import SellerProductFeedbackPage from "./pages/SellerPage/SellerProductFeedbackPage";
import StatisticsPage from "./pages/StatisticsPage/StatisticsPage";
import SellerOrderPage from "./pages/SellerPage/SellerOrderPage";
import ForgotPasswordPage from "./pages/LoginPage/ForgotPasswordPage";
import ResetPasswordPage from "./pages/LoginPage/ResetPasswordPage";

function App() {
    return (
        <Router>
            <NavBar />
            <Routes>
                <Route path="/" element={<HomePage />} />
                <Route path="/login" element={<LoginPage />} />
                <Route path="/register" element={<RegisterPage />} />
                <Route path="/products" element={<ProductsPage />} />
                <Route path="/seller" element={<SellerPage />} />
                <Route path="/product-history" element={<ProductHistoryPage />} />
                <Route path="/admin" element={<AdminPage />} />
                <Route path="/products/:productId" element={<ProductDetailsPage />} />
                <Route path="/cart" element={<CartPage />} />
                <Route path="/checkout" element={<CheckoutPage />} />
                <Route path="/account" element={<MyAccountPage />} />
                <Route path="/seller/feedback/:productId" element={<SellerProductFeedbackPage />} />
                <Route path="/stats" element={<StatisticsPage />} />
                <Route path="/seller/orders" element={<SellerOrderPage />} />
                <Route path="/forgot-password" element={<ForgotPasswordPage />} />
                <Route path="/reset-password" element={<ResetPasswordPage />} />
            </Routes>
        </Router>
    );
}

export default App;

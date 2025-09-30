import React, { useState } from 'react';
import AccountOrders from './AccountOrders';
import AccountProfile from './AccountProfile';
import './MyAccountPage.css';

const MyAccountPage = () => {
    const [activeTab, setActiveTab] = useState('profile');

    const renderContent = () => {
        if (activeTab === 'orders') return <AccountOrders />;
        if (activeTab === 'profile') return <AccountProfile />;
        return null;
    };

    return (
        <div className="account-container">
            <div className="sidebar">
                <h2>MY ACCOUNT</h2>
                <button onClick={() => setActiveTab('profile')}
                        className={activeTab === 'profile' ? 'active' : ''}>Profile
                </button>
                <button onClick={() => setActiveTab('orders')}
                        className={activeTab === 'orders' ? 'active' : ''}>Orders
                </button>
            </div>
            <div className="content">
                {renderContent()}
            </div>
        </div>
    );
};

export default MyAccountPage;
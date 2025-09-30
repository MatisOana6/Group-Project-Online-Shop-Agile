import React, { useEffect, useState, useRef } from 'react';
import './MyAccountPage.css';
import { getProfile, updateProfile } from './api/ProfileApi';
import { fetchAllCategories } from './api/CategoryApi';

const AccountProfile = () => {
    const [profile, setProfile] = useState(null);
    const [formData, setFormData] = useState({
        name: '',
        profileImage: '',
        preference: '',
        phoneNumber: '',
        address: ''
    });
    const [successMessage, setSuccessMessage] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const fileInputRef = useRef(null);
    const [allCategories, setAllCategories] = useState([]);
    const [selectedPreferences, setSelectedPreferences] = useState([]);
    const [errors, setErrors] = useState({});

    useEffect(() => {
        const token = sessionStorage.getItem('token');

        getProfile()
            .then(data => {
                setProfile(data);
                setFormData({
                    name: data.name || '',
                    profileImage: data.profileImage || '',
                    preference: '',
                    phoneNumber: data.phoneNumber || '',
                    address: data.address || ''
                });
                setSelectedPreferences(data.preference ? data.preference.split(', ') : []);
            })
            .catch(() => setErrorMessage('Failed to load profile'));

        fetchAllCategories(token)
            .then(setAllCategories)
            .catch(() => console.error("Failed to load categories"));
    }, []);

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    const handleFileChange = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                setFormData(prev => ({ ...prev, profileImage: reader.result }));
            };
            reader.readAsDataURL(file);
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        setSuccessMessage('');
        setErrorMessage('');

        const newErrors = {};

        if (!formData.name.trim()) {
            newErrors.name = 'Name is required.';
        }

        if (!formData.phoneNumber.trim()) {
            newErrors.phoneNumber = 'Phone number is required.';
        } else if (!/^\+?\d{7,15}$/.test(formData.phoneNumber)) {
            newErrors.phoneNumber = 'Phone number is invalid.';
        }

        if (!formData.address.trim()) {
            newErrors.address = 'Address is required.';
        }

        if (Object.keys(newErrors).length > 0) {
            setErrors(newErrors);
            return;
        }

        setErrors({});

        const updatedData = {
            ...formData,
            preference: selectedPreferences.join(', ')
        };

        updateProfile(updatedData)
            .then(() => setSuccessMessage('Profile updated successfully!'))
            .catch(() => setErrorMessage('Update failed'));
    };

    if (!profile) return <p>Loading...</p>;

    return (
        <div className="profile-container">
            <h3>Edit Profile</h3>
            {successMessage && <p className="success-message">{successMessage}</p>}
            {errorMessage && <p className="error-message">{errorMessage}</p>}
            <form onSubmit={handleSubmit} className="profile-form">
                <div className="image-section">
                    <img src={formData.profileImage} alt="Profile" className="profile-image-preview" />
                    <button type="button" className="change-image-btn" onClick={() => fileInputRef.current.click()}>
                        Change Image
                    </button>
                    <input type="file" accept="image/*" ref={fileInputRef} onChange={handleFileChange} style={{ display: 'none' }} />
                </div>

                <div className="form-group">
                    <label>Name:</label>
                    <input type="text" name="name" value={formData.name} onChange={handleChange} />
                    {errors.name && <p className="error-message">{errors.name}</p>}
                </div>

                <div className="form-group">
                    <label>Email:</label>
                    <input type="text" value={profile.email} readOnly />
                </div>

                <div className="form-group">
                    <label>Role:</label>
                    <input type="text" value={profile.role} readOnly />
                </div>

                <div className="form-group">
                    <label>Phone Number:</label>
                    <input type="text" name="phoneNumber" value={formData.phoneNumber} onChange={handleChange} />
                    {errors.phoneNumber && <p className="error-message">{errors.phoneNumber}</p>}
                </div>

                <div className="form-group">
                    <label>Address:</label>
                    <input type="text" name="address" value={formData.address} onChange={handleChange} />
                    {errors.address && <p className="error-message">{errors.address}</p>}
                </div>

                    <div className="form-group">
                        <label>Preference:</label>
                        <select
                            value=""
                            onChange={(e) => {
                                const selected = e.target.value;
                                if (selected && !selectedPreferences.includes(selected)) {
                                    setSelectedPreferences((prev) => [...prev, selected]);
                                }
                            }}
                        >
                            <option value="">Select preference</option>
                            {allCategories.map((cat) => (
                                <option key={cat.name} value={cat.name}>
                                    {cat.name}
                                </option>
                            ))}
                        </select>
                        <div className="tag-container">
                            {selectedPreferences.map((pref, index) => (
                                <div className="tag" key={pref}>
                                    {pref}
                                    <button
                                        type="button"
                                        onClick={() =>
                                            setSelectedPreferences(selectedPreferences.filter((p) => p !== pref))
                                        }
                                    >
                                        ×
                                    </button>
                                </div>
                            ))}
                        </div>
                    </div>

                <div className="form-group">
                    <button type="submit">Save Changes</button>
                </div>
            </form>
        </div>
    );
};

export default AccountProfile;

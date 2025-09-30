import React, { useEffect, useState } from 'react';
import './StatisticsPage.css';
import { fetchCategories } from "../ProductsPage/api/CategoriesApi";
import { fetchStatisticsData } from "./api/StatisticsApi";
import {
    BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer, PieChart, Pie, Cell
} from 'recharts';

const StatisticsPage = () => {
    const [filters, setFilters] = useState({
        region: '',
        startDate: '',
        endDate: ''
    });
    const [categories, setCategories] = useState([]);  
    const [error, setError] = useState('');
    const [countries, setCountries] = useState([]);
    const [chartType, setChartType] = useState('');
    const [statisticsData, setStatisticsData] = useState([]);

    useEffect(() => {
        fetchCategories().then(setCategories).catch(() => setError('Failed to load categories'));
    }, []);


    const handleChange = (e) => {
        const { name, value } = e.target;
        setFilters((prev) => ({ ...prev, [name]: value }));
    };

    const fetchStatistics = (e) => {
        e.preventDefault();
        setError('');
        setStatisticsData([]);

        const finalFilters = {
            ...filters,
            category: ''  
        };

        fetchStatisticsData(finalFilters)
            .then((data) => {
                console.log(data);  
                setStatisticsData(data);
            })
            .catch(() => setError('Failed to load statistics'));

    };

    useEffect(() => {
        fetch('https://restcountries.com/v3.1/all')
            .then((response) => response.json())
            .then((data) => {
                const sortedCountries = data
                    .map((country) => country.name.common)
                    .sort((a, b) => a.localeCompare(b));
                setCountries(sortedCountries);
            })
            .catch(() => setError('Failed to load countries'));
    }, []);

    return (
        <div className="statistics-container">
            <form className="filter-form" onSubmit={fetchStatistics}>
                <h2>Sales Statistics</h2>

                <div className="form-group">
                    <label>Start Date</label>
                    <input type="date" name="startDate" value={filters.startDate} onChange={handleChange} />
                </div>

                <div className="form-group">
                    <label>End Date</label>
                    <input type="date" name="endDate" value={filters.endDate} onChange={handleChange} />
                </div>

                <div className="form-group">
                    <label>Region (Country)</label>
                    <select name="region" value={filters.region} onChange={handleChange}>
                        <option value="">Select region</option>
                        {countries.map((country) => (
                            <option key={country} value={country}>
                                {country}
                            </option>
                        ))}
                    </select>
                </div>

                <button type="submit">Get Statistics</button>
            </form>

            <div className="statistics-results">
                <h3>Results will appear here</h3>
                {error && <p className="error-message">{error}</p>}

                <div className="chart-buttons">
                    <button onClick={() => setChartType('revenueByCategory')}>Revenue by Category</button>
                    <button onClick={() => setChartType('ordersByRegion')}>Orders by Region</button>
                    <button onClick={() => setChartType('revenueByRegion')}>Revenue by Region</button>
                    <button onClick={() => setChartType('combined')}>Table</button>
                </div>

                {chartType === 'revenueByCategory' && (
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart data={statisticsData}>
                            <XAxis dataKey="category" />
                            <YAxis />
                            <Tooltip />
                            <Legend />
                            <Bar dataKey="totalRevenue" fill="#8884d8" name="Revenue" />
                        </BarChart>
                    </ResponsiveContainer>
                )}

                {chartType === 'ordersByRegion' && (
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart data={statisticsData}>
                            <XAxis dataKey="category" />  {/* Reprezentăm categoriile pe axa X */}
                            <YAxis />
                            <Tooltip />
                            <Legend />
                            <Bar dataKey="totalOrders" fill="#8884d8" name="Orders" />  {/*totalRevenue -- totalOrders*/}
                        </BarChart>
                    </ResponsiveContainer>
                )}

                {chartType === 'revenueByRegion' && (
                    <ResponsiveContainer width="100%" height={300}>
                        <PieChart>
                            <Pie
                                data={statisticsData}
                                dataKey="totalRevenue"
                                nameKey="region"
                                cx="50%"
                                cy="50%"
                                outerRadius={100}
                                label
                            >
                                {statisticsData.map((entry, index) => (
                                    <Cell key={`cell-${index}`} fill={`hsl(${index * 50}, 70%, 50%)`} />
                                ))}
                            </Pie>
                            <Tooltip />
                        </PieChart>
                    </ResponsiveContainer>
                )}

                {chartType === 'combined' && (
                    <table className="stats-table">
                        <thead>
                        <tr>
                            <th>Category</th>
                            <th>Region</th>
                            <th>Total Orders</th>
                            <th>Total Revenue</th>
                        </tr>
                        </thead>
                        <tbody>
                        {statisticsData.map((row, index) => (
                            <tr key={index}>
                                <td>{row.category}</td>
                                <td>{row.region}</td>
                                <td>{row.totalOrders}</td>
                                <td>${row.totalRevenue.toFixed(2)}</td>
                            </tr>
                        ))}
                        </tbody>
                    </table>
                )}
            </div>
        </div>
    );
};

export default StatisticsPage;

import React from 'react';
import { Navigate } from 'react-router-dom';

const ProtectedRoute = ({ element: Component }) => {
  const token = localStorage.getItem('authToken');

  return token ? <Component /> : <Navigate to="/signin" />;
};

export default ProtectedRoute;

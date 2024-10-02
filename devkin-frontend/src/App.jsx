// App.jsx
import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Home from './pages/Home'; 
import About from './pages/About';
import Navbar from './Navbar';
import Dashboard from "./pages/Dashboard"

function App() {
  return (
    <div>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
        {/* <Route path="/signin" element={<About />} />
        <Route path="/signup" element={<About />} /> */}
        {/* <Route
          path="/dashboard"
          element={isAuthenticated ? <Dashboard /> : <Navigate to="/signin" />}
        /> */}
      </Routes>
    </div> 
  );
}

export default App;

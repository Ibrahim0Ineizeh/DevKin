import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Home from './Pages/Home'; 
import About from './pages/About';
import { ThemeProvider } from './Components/Theme'; // Make sure to import ThemeProvider
import Navbar from './Navbar';
import Dashboard from "./pages/Dashboard";

function App() {
  return (
    <ThemeProvider> {/* Wrap the app with ThemeProvider */}
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
    </ThemeProvider>
  );
}

export default App;

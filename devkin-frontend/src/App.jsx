import React from 'react';
import { Routes, Route } from 'react-router-dom';
import Home from './Pages/Home'; 
import About from './Pages/About';
import Signin from "./Pages/Signin";
import Signup from "./Pages/Signup";
import { ThemeProvider } from './Components/Theme'; 
import Navbar from './Navbar';
import ProtectedRoute from './Components/ProtectedRoute';
import Dashboard from "./Pages/Dashboard"

function App() {
  return (
    <ThemeProvider> 
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/about" element={<About />} />
        <Route path="/signin" element={<Signin />} />
        <Route path="/signup" element={<Signup />} />
        <Route
          path="/dashboard"
          element={<ProtectedRoute element={Dashboard} />}
        />
      </Routes>
    </ThemeProvider>
  );
}

export default App;

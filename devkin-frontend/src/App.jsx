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
import DashboardNavbar from './DashboardNavbar';

function App() {
  return (
    <ThemeProvider> 
      <Routes>
      <Route
          path="/"
          element={
            <>
              <Navbar /> {/* Default Navbar */}
              <Home />
            </>
          }
        />
        <Route
          path="/about"
          element={
            <>
              <Navbar />
              <About />
            </>
          }
        />
        <Route
          path="/signin"
          element={
            <>
              <Navbar />
              <Signin />
            </>
          }
        />
        <Route
          path="/signup"
          element={
            <>
              <Navbar />
              <Signup />
            </>
          }
        />
        <Route
          path="/dashboard"
          element={
            <ProtectedRoute>
              <>
                <DashboardNavbar />
                <Dashboard />
              </>
            </ProtectedRoute>
          }
        />
      </Routes>
    </ThemeProvider>
  );
}

export default App;

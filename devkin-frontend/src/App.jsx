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
import CreateProject from './Pages/CreateProject';
import ProjectPage from './Pages/ProjectPage';
import ProjectSettings from "./Pages/ProjectSettings"

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
        <Route
          path="/dashboard/createProject"
          element={
            <ProtectedRoute>
              <>
                <DashboardNavbar />
                <CreateProject />
              </>
            </ProtectedRoute>
          }
        />
        <Route
          path="/projects/:slug"
          element={
            <ProtectedRoute>
              <>
                <DashboardNavbar />
                <ProjectPage />
              </>
            </ProtectedRoute>
          }
        />
         <Route
          path="/projects/:slug/settings"
          element={
            <ProtectedRoute>
              <>
                <DashboardNavbar />
                <ProjectSettings />
              </>
            </ProtectedRoute>
          }
        />
      </Routes>
    </ThemeProvider>
  );
}

export default App;

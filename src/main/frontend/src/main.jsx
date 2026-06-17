import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import Login from './Login.jsx'
import UserLoginDashboard from './user_dashboard.jsx'
createRoot(document.getElementById('root')).render(
  <StrictMode>
    <UserLoginDashboard />
  </StrictMode>,
)

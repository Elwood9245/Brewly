import { ref, computed } from 'vue'
import apiClient from '../api/client.js'

const isAuthenticated = ref(false)
const currentUser = ref(null)

export function useAuthStore() {
  const login = async (credentials) => {
    try {
      console.log('Logging in with:', credentials)
      
      const response = await apiClient.post('/auth/login', {
        account: credentials.email,
        password: credentials.password
      })

      const data = response.data
      
      // Store token first
      localStorage.setItem('auth_token', data.token)
      
      // Fetch current user from /api/me to ensure data consistency
      const userResult = await fetchCurrentUser()
      if (userResult.success) {
        return { success: true, user: userResult.user }
      } else {
        // Fallback to response data if /api/me fails
        const user = {
          id: data.user.id,
          username: data.user.username,
          email: data.user.email
        }
        currentUser.value = user
        isAuthenticated.value = true
        localStorage.setItem('user', JSON.stringify(user))
        return { success: true, user: user }
      }
    } catch (error) {
      console.error('Login error:', error)
      const errorMessage = error.response?.data?.message || error.message || 'Login failed'
      return { success: false, error: errorMessage }
    }
  }

  const register = async (userData) => {
    try {
      console.log('Registering user:', userData)
      const response = await apiClient.post('/auth/register', {
        username: userData.username,
        email: userData.email,
        password: userData.password
      })

      const data = response.data
      const user = {
        id: data.user.id,
        username: data.user.username,
        email: data.user.email
      }
      
      return { success: true, user: user }
    } catch (error) {
      console.error('Registration error:', error)
      const errorMessage = error.response?.data?.message || error.message || 'Registration failed'
      return { success: false, error: errorMessage }
    }
  }

  const fetchCurrentUser = async () => {
    try {
      const response = await apiClient.get('/auth/me')
      const user = {
        id: response.data.id,
        username: response.data.username,
        email: response.data.email
      }
      currentUser.value = user
      isAuthenticated.value = true
      localStorage.setItem('user', JSON.stringify(user))
      return { success: true, user: user }
    } catch (error) {
      console.error('Fetch current user error:', error)
      // If 401, auth interceptor will handle logout
      return { success: false, error: error.response?.data?.message || 'Failed to fetch user info' }
    }
  }

  const logout = () => {
    isAuthenticated.value = false
    currentUser.value = null
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user')
  }

  const checkAuthStatus = async () => {
    const token = localStorage.getItem('auth_token')
    
    if (token) {
      // Try to fetch current user to verify token validity
      const result = await fetchCurrentUser()
      if (result.success) {
        return true
      } else {
        // Token is invalid, clear auth state
        logout()
        return false
      }
    } else {
      // No token, try to restore from localStorage user (fallback)
      const user = localStorage.getItem('user')
      if (user) {
        try {
          currentUser.value = JSON.parse(user)
          // Don't set isAuthenticated without valid token
          return false
        } catch (e) {
          logout()
          return false
        }
      }
    }
    return false
  }

  // Getters
  const getUser = computed(() => currentUser.value)
  const getIsAuthenticated = computed(() => isAuthenticated.value)

  return {
    // State
    isAuthenticated: getIsAuthenticated,
    currentUser: getUser,
    
    // Action
    login,
    register,
    logout,
    checkAuthStatus,
    fetchCurrentUser
  }
}
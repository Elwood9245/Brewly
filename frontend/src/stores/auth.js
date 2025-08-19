import { ref, computed } from 'vue'
import apiClient from '../api/client.js'

const isAuthenticated = ref(false)
const currentUser = ref(null)

export function useAuthStore() {
  const login = async (credentials) => {
    try {
      const response = await apiClient.post('/auth/login', {
        account: credentials.email,
        password: credentials.password
      })

      const data = response.data
      
      localStorage.setItem('auth_token', data.token)
      
      const userResult = await fetchCurrentUser()
      if (userResult.success) {
        return { success: true, user: userResult.user }
      } else {
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
      const errorMessage = error.response?.data?.message || error.message || 'Login failed'
      return { success: false, error: errorMessage }
    }
  }

  const register = async (userData) => {
    try {
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
      const result = await fetchCurrentUser()
      if (result.success) {
        return true
      } else {
        logout()
        return false
      }
    } else {
      const user = localStorage.getItem('user')
      if (user) {
        try {
          currentUser.value = JSON.parse(user)
          return false
        } catch (e) {
          logout()
          return false
        }
      }
    }
    return false
  }

  const getUser = computed(() => currentUser.value)
  const getIsAuthenticated = computed(() => isAuthenticated.value)

  return {
    isAuthenticated: getIsAuthenticated,
    currentUser: getUser,
    login,
    register,
    logout,
    checkAuthStatus,
    fetchCurrentUser
  }
}
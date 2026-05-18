import { ref, computed } from 'vue'
import { defineStore } from 'pinia'
import apiClient from '../api/client.js'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem('auth_token') || null)
  const currentUser = ref(null)
  const isAuthenticated = ref(false)

  const login = async (credentials) => {
    try {
      const response = await apiClient.post('/auth/login', {
        account: credentials.email,
        password: credentials.password
      })

      const data = response.data

      token.value = data.token
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
    token.value = null
    isAuthenticated.value = false
    currentUser.value = null
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user')
  }

  const checkAuthStatus = async () => {
    const storedToken = localStorage.getItem('auth_token')

    if (storedToken) {
      const result = await fetchCurrentUser()
      if (result.success) {
        return true
      } else {
        logout()
        return false
      }
    } else {
      const cachedUser = localStorage.getItem('user')
      if (cachedUser) {
        try {
          currentUser.value = JSON.parse(cachedUser)
          return false
        } catch {
          logout()
          return false
        }
      }
    }
    return false
  }

  const user = computed(() => currentUser.value)
  const authenticated = computed(() => isAuthenticated.value)

  return {
    isAuthenticated: authenticated,
    currentUser: user,
    login,
    register,
    logout,
    checkAuthStatus,
    fetchCurrentUser
  }
})

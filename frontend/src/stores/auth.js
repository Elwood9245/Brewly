import { ref, computed } from 'vue'

const isAuthenticated = ref(false)
const currentUser = ref(null)

export function useAuthStore() {
  const login = async (credentials) => {
    try {
      console.log('Logging in with:', credentials)
      
      const response = await fetch('/api/auth/login', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          account: credentials.email,
          password: credentials.password
        })
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Login failed')
      }

      const data = await response.json()
      const user = {
        id: data.user.id,
        username: data.user.username,
        email: data.user.email,
        name: data.user.username
      }
      currentUser.value = user
      isAuthenticated.value = true
      
      // Store in localStorage for persistence
      localStorage.setItem('auth_token', data.token)
      localStorage.setItem('user', JSON.stringify(user))
      return { success: true, user: user }
    } catch (error) {
      console.error('Login error:', error)
      return { success: false, error: error.message }
    }
  }

  const register = async (userData) => {
    try {
      console.log('Registering user:', userData)
      const response = await fetch('/api/auth/register', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          username: userData.username,
          email: userData.email,
          password: userData.password
        })
      })

      if (!response.ok) {
        const errorData = await response.json()
        throw new Error(errorData.message || 'Registration failed')
      }

      const data = await response.json()
      const user = {
        id: data.user.id,
        username: data.user.username,
        email: data.user.email,
        name: data.user.username
      }
      
      return { success: true, user: user }
    } catch (error) {
      console.error('Registration error:', error)
      return { success: false, error: error.message }
    }
  }

  const logout = () => {
    isAuthenticated.value = false
    currentUser.value = null
    localStorage.removeItem('auth_token')
    localStorage.removeItem('user')
  }

  const checkAuthStatus = () => {
    const token = localStorage.getItem('auth_token')
    const user = localStorage.getItem('user')
    
    if (token && user) {
      try {
        currentUser.value = JSON.parse(user)
        isAuthenticated.value = true
        return true
      } catch (e) {
        logout()
        return false
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
    checkAuthStatus
  }
}
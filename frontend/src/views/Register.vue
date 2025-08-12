<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'

const router = useRouter()
const auth = useAuthStore()

// Form state
const isLoading = ref(false)
const errorMessage = ref('')
const successMessage = ref('')
const formData = ref({
  email: '',
  password: '',
  username: '',
  confirmPassword: ''
})

// Validate form data
const validateForm = () => {
  if (!formData.value.username) {
    errorMessage.value = 'Username is required'
    return false
  }
  
  if (!formData.value.email) {
    errorMessage.value = 'Email is required'
    return false
  }
  
  if (!formData.value.password) {
    errorMessage.value = 'Password is required'
    return false
  }
  
  if (formData.value.password.length < 6) {
    errorMessage.value = 'Password must be at least 6 characters long'
    return false
  }
  
  if (formData.value.password !== formData.value.confirmPassword) {
    errorMessage.value = 'Passwords do not match'
    return false
  }

  return true
}

const handleSubmit = async () => {
  errorMessage.value = ''
  successMessage.value = ''
  
  if (!validateForm()) {
    return
  }

  isLoading.value = true

  try {
    // Registration
    const result = await auth.register({
      username: formData.value.username,
      email: formData.value.email,
      password: formData.value.password
    })
    
    if (result.success) {
      successMessage.value = 'Registration successful! Redirecting to login...'
      formData.value = {
        email: '',
        password: '',
        username: '',
        confirmPassword: ''
      }
      setTimeout(() => {
        router.push('/login')
      }, 2000)
    } else {
      errorMessage.value = result.error || 'Registration failed. Please try again.'
    }
  } catch (error) {
    console.error('Registration error:', error)
    errorMessage.value = 'An unexpected error occurred. Please try again.'
  } finally {
    isLoading.value = false
  }
}

const goToLogin = () => {
  router.push('/login')
}
</script>

<template>
  <div class="auth-container">
    <div class="container-fluid d-flex align-items-center justify-content-center min-vh-100">
      <div class="row justify-content-center w-100">
        <div class="col-md-6 col-lg-4">
          <div class="card shadow-lg border-0">
            <div class="card-body p-5">
              <!-- Logo/Title -->
              <div class="text-center mb-4">
                <h2 class="fw-bold">Brewly</h2>
                <p>Join the coffee community!</p>
              </div>

              <!-- Registration Form -->
              <form @submit.prevent="handleSubmit">
                <!-- Username field -->
                <div class="mb-3">
                  <label for="username" class="form-label">Username</label>
                  <input
                    type="text"
                    class="form-control"
                    id="username"
                    v-model="formData.username"
                    :disabled="isLoading"
                    required
                    placeholder="Enter your username"
                  >
                </div>

                <!-- Email field -->
                <div class="mb-3">
                  <label for="email" class="form-label">Email</label>
                  <input
                    type="email"
                    class="form-control"
                    id="email"
                    v-model="formData.email"
                    :disabled="isLoading"
                    required
                    placeholder="Enter your email"
                  >
                </div>

                <!-- Password field -->
                <div class="mb-3">
                  <label for="password" class="form-label">Password</label>
                  <input
                    type="password"
                    class="form-control"
                    id="password"
                    v-model="formData.password"
                    :disabled="isLoading"
                    required
                    placeholder="Enter your password"
                  >
                  <div class="form-text">Password must be at least 6 characters long</div>
                </div>

                <!-- Confirm password field -->
                <div class="mb-3">
                  <label for="confirmPassword" class="form-label">Confirm Password</label>
                  <input
                    type="password"
                    class="form-control"
                    id="confirmPassword"
                    v-model="formData.confirmPassword"
                    :disabled="isLoading"
                    required
                    placeholder="Confirm your password"
                  >
                </div>

              <!-- Success Alert -->
              <div v-if="successMessage" class="alert alert-success mb-4" role="alert">
                <i class="bi bi-check-circle me-2"></i>{{ successMessage }}
              </div>

              <!-- Error Alert -->
              <div v-if="errorMessage" class="alert alert-danger mb-4" role="alert">
                <i class="bi bi-exclamation-triangle me-2"></i>{{ errorMessage }}
              </div>

                <!-- Submit button -->
                <div class="d-grid mb-3 mt-3">
                  <button type="submit" class="btn btn-primary btn-lg mt-2" :disabled="isLoading">
                    <span v-if="isLoading" class="spinner-border spinner-border-sm me-2" role="status" aria-hidden="true"></span>
                    {{ isLoading ? 'Creating Account...' : 'Create Account' }}
                  </button>
                </div>

                <!-- Back to login -->
                <div class="text-center">
                  <p class="mb-0">
                    Already have an account?
                    <a href="#" @click.prevent="goToLogin" class="text-decoration-none" :class="{ 'disabled': isLoading }">
                      Sign in
                    </a>
                  </p>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.auth-container {
  background: #f0f0f0;
  min-height: 100vh;
}

.card {
  border-radius: 30px;
  background-color: rgba(255, 255, 255, 0.95);
}

.form-control {
  border-radius: 20px;
  border: 1px solid #dee2e6;
  padding: 10px 16px;
}

.btn-primary {
  background: white;
  border: 2px solid #2d2d2d;
  border-radius: 50px;
  padding: 10px;
  font-weight: 600;
  color: black;
  transition: all 0.2s ease;
}

.btn-primary:hover {
  background: #1a1a1a;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  color: white;
}

.btn-primary:disabled {
  background: #9e9e9e;
  transform: none;
  box-shadow: none;
}

.card-body {
  position: relative;
}

.form-text {
  color: #6c757d;
  font-size: 0.875rem;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .card-body {
    padding: 2rem !important;
  }
}
</style>

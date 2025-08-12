<template>
  <div id="app">
    <!-- Authentication pages won't use template, use separated page instead -->
    <div v-if="isAuthPage">
      <router-view />
    </div>

    <div v-else-if="auth.isAuthenticated" class="main-app-layout">
    <!-- Header -->
    <nav class="navbar bg-white border-bottom fixed-top">
      <div class="container-fluid">
        <button
          class="btn m-0 p-0"
          type="button"
          data-bs-toggle="offcanvas"
          data-bs-target="#sidebar"
          aria-controls="sidebar"
          ref="sidebarToggleBtn"
        >
          <i class="bi bi-person user-avatar-toggle"></i>
        </button>
        <span class="navbar-brand mx-auto fw-bold">Brewly</span>
      </div>
    </nav>

    <!-- Sidebar User Profile -->
    <div
      class="offcanvas offcanvas-start"
      tabindex="-1"
      id="sidebar"
      aria-labelledby="sidebarLabel"
      ref="sidebar"
    >
      <div class="offcanvas-header">
        <h5 class="offcanvas-title" id="sidebarLabel">Profile</h5>
        <button type="button" class="btn-close" data-bs-dismiss="offcanvas" aria-label="Close"></button>
      </div>
      <div class="offcanvas-body">
        <!-- User Info -->
        <div class="text-center mb-4">
          <div
            class="bg-white border border-dark border-2 rounded-circle d-flex align-items-center justify-content-center mx-auto mb-3"
            style="width: 60px; height: 60px;"
          >
            <span class="text-black fw-bold fs-5">{{ userInitial }}</span>
          </div>
          <h6 class="mb-1 ">{{ userName }}</h6>
        </div>

        <!-- Quick Actions -->
        <div class="mb-3">
          <h6 class="mb-2">Quick Actions</h6>
          <div class="d-grid gap-2">
            <router-link to="/addbrew" class="btn btn-primary btn-sm">
              <i class="bi bi-plus-circle me-1"></i>Add New Brew
            </router-link>
            <a href="#" class="btn btn-outline-primary btn-sm">
              <i class="bi bi-plus-circle me-1"></i>Create Recipe
            </a>
          </div>
        </div>

        <!-- Account -->
        <hr class="my-3">
        <div class="mb-3">
          <h6 class="mb-2">Account</h6>
          <ul class="list-group list-group-flush">
            <li class="list-group-item px-0">
              <a href="#" class="text-decoration-none text-black">
                <i class="bi bi-person me-2"></i>My Profile
              </a>
            </li>
            <li class="list-group-item px-0">
              <a href="#" class="text-decoration-none text-black">
                <i class="bi bi-gear me-2"></i>Settings
              </a>
            </li>
            <li class="list-group-item px-0">
              <a href="#" class="text-decoration-none text-black">
                <i class="bi bi-question-circle me-2"></i>Help
              </a>
            </li>
            <li class="list-group-item px-0">
              <a href="#" @click.prevent="handleLogout" class="text-decoration-none text-danger">
                <i class="bi bi-box-arrow-right me-2"></i>Logout
              </a>
            </li>
          </ul>
        </div>
      </div>
    </div>

    <!-- Main Content Area for authenticated users -->
    <main class="container main-content">
      <router-view />
    </main>

    <!-- Footer Tabs -->
    <nav class="navbar bg-white border-top fixed-bottom">
      <div class="container-fluid">
        <div class="d-flex justify-content-between w-100">
          <router-link to="/" class="nav-link text-center flex-fill"><i class="bi bi-house fs-4"></i><br><small>Home</small></router-link>
          <router-link to="/explore" class="nav-link text-center flex-fill"><i class="bi bi-compass fs-4"></i><br><small>Explore</small></router-link>
          <router-link to="/inventory" class="nav-link text-center flex-fill"><i class="bi bi-boxes fs-4"></i><br><small>Inventory</small></router-link>
          <router-link to="/ai" class="nav-link text-center flex-fill"><i class="bi bi-robot fs-4"></i><br><small>AI</small></router-link>
        </div>
      </div>
    </nav>
  </div>

    <!-- Fallback for unauthenticated users not on auth pages -->
    <div v-else>
      <router-view />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from './stores/auth.js'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const sidebar = ref(null)

// Check authentication status on app load
onMounted(() => {
  // Check if user is already logged in
  auth.checkAuthStatus()
  
  // Initialize Bootstrap Offcanvas only for authenticated users
  if (auth.isAuthenticated && window.bootstrap && sidebar.value) {
    // eslint-disable-next-line no-undef
    new window.bootstrap.Offcanvas(sidebar.value)
  }
})

// Check if current route is an authentication page
const isAuthPage = computed(() => {
  return route.meta?.hideForAuth === true
})

// Computed properties for user display
const userName = computed(() => {
  return auth.currentUser.value?.name || auth.currentUser.value?.username || 'Guest'
})

const userInitial = computed(() => {
  return userName.value.charAt(0).toUpperCase()
})

// Logout
const handleLogout = () => {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.main-content {
  padding-top: 64px;
  padding-bottom: 70px;
}

.user-avatar-toggle {
  font-size: 1.5rem;
  color: #2d2d2d;
  transition: all 0.2s ease;
}

.user-avatar-toggle:hover {
  color: #1a1a1a;
  transform: scale(1.05);
}
</style>

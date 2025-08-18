<template>
  <div class="container-fluid py-4">
    <!-- Header Section -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <h1 class="h3 mb-0">Bean Inventory</h1>
          <router-link to="/beans/add" class="btn btn-primary">
            <i class="bi bi-plus-circle me-2"></i>Add Bean
          </router-link>
        </div>
      </div>
    </div>

    <!-- Tab Navigation -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="nav-container">
          <div class="nav-tabs">
            <button 
              class="nav-tab" 
              :class="{ 'active': activeTab === 'active' }"
              @click="setActiveTab('active')"
              type="button"
            >
              <i class="bi bi-check-circle-fill"></i>
              <span>Active Beans</span>
              <span>{{ activeBeans.length }}</span>
            </button>
            <button 
              class="nav-tab" 
              :class="{ 'active': activeTab === 'inactive' }"
              @click="setActiveTab('inactive')"
              type="button"
            >
              <i class="bi bi-pause-circle-fill"></i>
              <span>Inactive Beans</span>
              <span>{{ inactiveBeans.length }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Error Banner -->
    <div v-if="errorMsg" class="row mb-4">
      <div class="col-12">
        <div class="error-banner">{{ errorMsg }}</div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="row">
      <div class="col-12 text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-3 text-secondary">Loading beans...</p>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="!currentBeans.length" class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-body text-center py-5">
            <i class="bi bi-boxes fs-1 text-secondary mb-3"></i>
            <h5 class="card-title">No {{ activeTab }} beans found</h5>
            <p class="card-text text-secondary">
              {{ activeTab === 'active' 
                ? 'Start building your coffee collection by adding your first bean.' 
                : 'No inactive beans in your inventory.' 
              }}
            </p>
            <router-link v-if="activeTab === 'active'" to="/beans/add" class="btn btn-primary">
              <i class="bi bi-plus-circle me-2"></i>Add Your First Bean
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Beans Grid -->
    <div v-else class="row g-4">
      <div v-for="bean in currentBeans" :key="bean.id" class="col-md-6 col-lg-4">
        <BeanCard 
          :bean="bean" 
          :show-edit="true"
          :show-delete="true"
          :show-view="false"
          @delete="openDeleteDialog"
        />
      </div>
    </div>
  </div>

  <!-- Delete Confirmation Modal -->
  <div v-if="showDeleteConfirm" class="modal fade show d-block" tabindex="-1" style="background-color: rgba(0,0,0,0.5);">
    <div class="modal-dialog modal-dialog-centered">
      <div class="modal-content">
        <div class="modal-header">
          <h5 class="modal-title">Confirm Delete</h5>
          <button type="button" class="btn-close" @click="closeDeleteDialog"></button>
        </div>
        <div class="modal-body">
          <p>Are you sure you want to delete <strong>{{ beanToDelete?.name }}</strong>?</p>
          <p class="text-secondary small">This action cannot be undone.</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline-secondary" @click="closeDeleteDialog">Cancel</button>
          <button type="button" class="btn btn-danger" @click="confirmDelete">
            <i class="bi bi-trash me-2"></i>Delete Bean
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { beanAPI } from '../api/beans.js'
import { useAuthStore } from '../stores/auth.js'
import BeanCard from '../components/BeanCard.vue'

// Get auth store
const auth = useAuthStore()

const beans = ref([])
const loading = ref(false)
const errorMsg = ref('')
const activeTab = ref('active') // 'active' or 'inactive'

// Delete confirmation state
const showDeleteConfirm = ref(false)
const beanToDelete = ref(null)

// Computed properties for active/inactive beans
const activeBeans = computed(() => {
  return beans.value.filter(bean => bean.isActive)
})

const inactiveBeans = computed(() => {
  return beans.value.filter(bean => !bean.isActive)
})

// Current beans based on active tab
const currentBeans = computed(() => {
  return activeTab.value === 'active' ? activeBeans.value : inactiveBeans.value
})

function setActiveTab(tab) {
  activeTab.value = tab
}

function openDeleteDialog(bean) {
  beanToDelete.value = bean
  showDeleteConfirm.value = true
}

function closeDeleteDialog() {
  showDeleteConfirm.value = false
  beanToDelete.value = null
}

async function confirmDelete() {
  if (!beanToDelete.value) return
  errorMsg.value = ''
  try {
    await beanAPI.deleteBean(beanToDelete.value.id)
    await fetchBeans()
  } catch (e) {
    errorMsg.value = e.response?.data?.message || 'Failed to delete bean. Please try again.'
  } finally {
    closeDeleteDialog()
  }
}

async function fetchBeans() {
  loading.value = true
  errorMsg.value = ''
  
  // Check if user is authenticated and has user info
  if (!auth.isAuthenticated.value || !auth.currentUser.value?.id) {
    // Try to refresh auth status first
    const isAuth = await auth.checkAuthStatus()
    if (!isAuth || !auth.currentUser.value?.id) {
      errorMsg.value = 'Please log in to view your bean inventory.'
      loading.value = false
      return
    }
  }
  
  try {
    // Fetch user-specific beans
    beans.value = await beanAPI.getBeansByUserId(auth.currentUser.value.id)
  } catch (e) {
    beans.value = []
    errorMsg.value = e.response?.data?.message || 'Failed to load beans. Please try again.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  fetchBeans()
})

// Listen for bean-changed event from BeanForm (if used as a child)
if (typeof window !== 'undefined') {
  window.addEventListener('bean-changed', () => {
    fetchBeans()
  })
}
</script>

<style scoped>
/* Error banner styling */
.error-banner {
  background-color: #f8d7da;
  color: #721c24;
  padding: 1rem;
  border: 1px solid #f5c6cb;
  border-radius: 0.75rem;
  margin-bottom: 1rem;
}
</style>

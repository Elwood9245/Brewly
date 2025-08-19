<template>
  <div class="container-fluid py-4">
    <!-- Header Section -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <h1 class="h3 mb-0">Brew Logs</h1>
          <div class="d-flex gap-2">
            <button @click="showStatistics = true" class="btn btn-outline-primary">
              <i class="bi bi-graph-up me-2"></i>Statistics
            </button>
            <router-link to="/addbrew" class="btn btn-primary">
              <i class="bi bi-plus-circle me-2"></i>Add Brew
            </router-link>
          </div>
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
              :class="{ 'active': activeTab === 'all' }"
              @click="setActiveTab('all')"
              type="button"
            >
              <i class="bi bi-list-ul"></i>
              <span>All Brews</span>
              <span>{{ brewLogs.length }}</span>
            </button>
            <button 
              class="nav-tab" 
              :class="{ 'active': activeTab === 'recent' }"
              @click="setActiveTab('recent')"
              type="button"
            >
              <i class="bi bi-clock-history"></i>
              <span>Recent</span>
              <span>{{ recentBrews.length }}</span>
            </button>
            <button 
              class="nav-tab" 
              :class="{ 'active': activeTab === 'top' }"
              @click="setActiveTab('top')"
              type="button"
            >
              <i class="bi bi-star-fill"></i>
              <span>Top Rated</span>
              <span>{{ topRatedBrews.length }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Filters -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card">
          <div class="card-body">
            <div class="row g-3">
              <div class="col-md-3">
                <label for="method-filter" class="form-label">Method</label>
                <select v-model="filters.method" id="method-filter" class="form-select" @change="loadBrewLogs">
                  <option value="">All Methods</option>
                  <option value="V60">V60</option>
                  <option value="Chemex">Chemex</option>
                  <option value="AeroPress">AeroPress</option>
                  <option value="French Press">French Press</option>
                  <option value="Espresso">Espresso</option>
                  <option value="Moka Pot">Moka Pot</option>
                  <option value="Pour Over">Pour Over</option>
                  <option value="Kalita Wave">Kalita Wave</option>
                  <option value="Clever Dripper">Clever Dripper</option>
                </select>
              </div>
              <div class="col-md-3">
                <label for="rating-filter" class="form-label">Rating</label>
                <select v-model="filters.rating" id="rating-filter" class="form-select" @change="loadBrewLogs">
                  <option value="">All Ratings</option>
                  <option value="9-10">9-10</option>
                  <option value="7-8">7-8</option>
                  <option value="5-6">5-6</option>
                  <option value="3-4">3-4</option>
                  <option value="1-2">1-2</option>
                </select>
              </div>
              <div class="col-md-4">
                <label for="search" class="form-label">Search</label>
                <input 
                  v-model="filters.search" 
                  id="search" 
                  type="text" 
                  class="form-control"
                  placeholder="Search taste notes, bean names..."
                  @input="debounceSearch"
                >
              </div>
              <div class="col-md-2 d-flex align-items-end">
                <button @click="clearFilters" class="btn btn-outline-secondary w-100">
                  <i class="bi bi-x-circle me-1"></i>Clear
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Error Banner -->
    <div v-if="error" class="row mb-4">
      <div class="col-12">
        <div class="error-banner">{{ error }}</div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="row">
      <div class="col-12 text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-3 text-secondary">Loading brew logs...</p>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="!currentBrewLogs.length" class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-body text-center py-5">
            <i class="bi bi-cup-hot fs-1 text-secondary mb-3"></i>
            <h5 class="card-title">No brew logs found</h5>
            <p class="card-text text-secondary">
              Start tracking your coffee brewing journey by adding your first brew log.
            </p>
            <router-link to="/addbrew" class="btn btn-primary">
              <i class="bi bi-plus-circle me-2"></i>Add Your First Brew
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Brew Logs Grid -->
    <div v-else class="row g-4">
      <div v-for="brewLog in currentBrewLogs" :key="brewLog.id" class="col-md-6 col-lg-4">
        <BrewLogCard 
          :brew-log="brewLog" 
          :show-edit="true"
          :show-delete="true"
          :show-view="true"
          @delete="openDeleteDialog"
        />
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="row mt-4">
      <div class="col-12">
        <nav aria-label="Brew logs pagination">
          <ul class="pagination justify-content-center">
            <li class="page-item" :class="{ disabled: currentPage === 0 }">
              <button @click="changePage(currentPage - 1)" class="page-link" :disabled="currentPage === 0">
                <i class="bi bi-chevron-left"></i>
              </button>
            </li>
            <li v-for="page in visiblePages" :key="page" class="page-item" :class="{ active: page === currentPage }">
              <button @click="changePage(page)" class="page-link">{{ page + 1 }}</button>
            </li>
            <li class="page-item" :class="{ disabled: currentPage >= totalPages - 1 }">
              <button @click="changePage(currentPage + 1)" class="page-link" :disabled="currentPage >= totalPages - 1">
                <i class="bi bi-chevron-right"></i>
              </button>
            </li>
          </ul>
        </nav>
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
          <p>Are you sure you want to delete the brew log for <strong>{{ brewLogToDelete?.beanName }}</strong>?</p>
          <p class="text-secondary small">This action cannot be undone.</p>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-outline-secondary" @click="closeDeleteDialog">Cancel</button>
          <button type="button" class="btn btn-danger" @click="confirmDelete">
            <i class="bi bi-trash me-2"></i>Delete Brew Log
          </button>
        </div>
      </div>
    </div>
  </div>

  <!-- Statistics Modal -->
  <BrewLogStatistics 
    v-if="showStatistics"
    @close="showStatistics = false"
  />
</template>

<script setup>
import { ref, onMounted, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getUserBrewLogs, deleteBrewLog } from '../api/brewlogs.js'
import BrewLogStatistics from '../components/BrewLogStatistics.vue'
import BrewLogCard from '../components/BrewLogCard.vue'

const router = useRouter()
const brewLogs = ref([])
const loading = ref(false)
const error = ref(null)
const currentPage = ref(0)
const totalPages = ref(0)
const pageSize = ref(10)
const activeTab = ref('all') // 'all', 'recent', 'top'

// Delete confirmation state
const showDeleteConfirm = ref(false)
const brewLogToDelete = ref(null)

const showStatistics = ref(false)

const filters = reactive({
  method: '',
  rating: '',
  search: ''
})

let searchTimeout = null

// Computed properties for different tabs
const recentBrews = computed(() => {
  const oneWeekAgo = new Date()
  oneWeekAgo.setDate(oneWeekAgo.getDate() - 7)
  return brewLogs.value.filter(brewLog => {
    const brewDate = new Date(brewLog.createdAt)
    return brewDate >= oneWeekAgo
  })
})

const topRatedBrews = computed(() => {
  return brewLogs.value.filter(brewLog => brewLog.rating >= 8).sort((a, b) => b.rating - a.rating)
})

const currentBrewLogs = computed(() => {
  switch (activeTab.value) {
    case 'recent':
      return recentBrews.value
    case 'top':
      return topRatedBrews.value
    default:
      return brewLogs.value
  }
})

const visiblePages = computed(() => {
  const pages = []
  const maxVisible = 5
  const start = Math.max(0, Math.min(currentPage.value - Math.floor(maxVisible / 2), totalPages.value - maxVisible))
  const end = Math.min(start + maxVisible, totalPages.value)
  
  for (let i = start; i < end; i++) {
    pages.push(i)
  }
  return pages
})

function setActiveTab(tab) {
  activeTab.value = tab
}

function clearFilters() {
  filters.method = ''
  filters.rating = ''
  filters.search = ''
  loadBrewLogs()
}

function openDeleteDialog(brewLog) {
  brewLogToDelete.value = brewLog
  showDeleteConfirm.value = true
}

function closeDeleteDialog() {
  showDeleteConfirm.value = false
  brewLogToDelete.value = null
}

async function confirmDelete() {
  if (!brewLogToDelete.value) return
  error.value = ''
  try {
    await deleteBrewLog(brewLogToDelete.value.id)
    await loadBrewLogs()
  } catch (err) {
    error.value = 'Failed to delete brew log. Please try again.'
    console.error('Error deleting brew log:', err)
  } finally {
    closeDeleteDialog()
  }
}

const loadBrewLogs = async () => {
  loading.value = true
  error.value = null
  
  try {
    const response = await getUserBrewLogs(currentPage.value, pageSize.value)
    brewLogs.value = response.brewLogs || []
    totalPages.value = response.totalPages || 0
  } catch (err) {
    error.value = 'Failed to load brew logs. Please try again.'
    console.error('Error loading brew logs:', err)
  } finally {
    loading.value = false
  }
}

const changePage = (page) => {
  currentPage.value = page
  loadBrewLogs()
}

const debounceSearch = () => {
  clearTimeout(searchTimeout)
  searchTimeout = setTimeout(() => {
    currentPage.value = 0
    loadBrewLogs()
  }, 300)
}

onMounted(() => {
  loadBrewLogs()
})
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

/* Tab navigation styling */
.nav-container {
  background: white;
  border-radius: 0.75rem;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.nav-tabs {
  display: flex;
  border-bottom: 1px solid #e9ecef;
}

.nav-tab {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem;
  background: none;
  border: none;
  color: #6c757d;
  font-weight: 500;
  transition: all 0.2s ease;
  position: relative;
}

.nav-tab:hover {
  background-color: #f8f9fa;
  color: #495057;
}

.nav-tab.active {
  color: #007bff;
  background-color: #f8f9fa;
}

.nav-tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background-color: #007bff;
}

.nav-tab i {
  font-size: 1.25rem;
}

.nav-tab span:last-child {
  background-color: #e9ecef;
  color: #495057;
  padding: 0.25rem 0.5rem;
  border-radius: 1rem;
  font-size: 0.75rem;
  font-weight: 600;
}

.nav-tab.active span:last-child {
  background-color: #007bff;
  color: white;
}
</style>

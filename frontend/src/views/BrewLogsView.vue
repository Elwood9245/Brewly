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
    <div v-else-if="!brewLogs.length" class="row">
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
      <div v-for="brewLog in brewLogs" :key="brewLog.id" class="col-md-6 col-lg-4">
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
import { ref, onMounted, computed } from 'vue'
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

// Delete confirmation state
const showDeleteConfirm = ref(false)
const brewLogToDelete = ref(null)

const showStatistics = ref(false)

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
</style>

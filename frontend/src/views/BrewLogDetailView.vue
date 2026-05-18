<template>
  <div class="container-fluid py-4">
    <!-- Header Section -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div class="d-flex align-items-center gap-3">
            <button @click="goBack" class="btn btn-outline-secondary">
              <i class="bi bi-arrow-left me-2"></i>Back
            </button>
            <div>
              <h1 class="h3 mb-0">Brew Log Details</h1>
              <p v-if="brewLog" class="text-secondary mb-0">{{ brewLog.beanName }}</p>
            </div>
          </div>
          <div v-if="brewLog" class="d-flex gap-2">
            <button @click="editBrewLog" class="btn btn-outline-primary">
              <i class="bi bi-pencil me-2"></i>Edit
            </button>
            <button @click="deleteBrewLogHandler" class="btn btn-outline-danger">
              <i class="bi bi-trash me-2"></i>Delete
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="row">
      <div class="col-12 text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-3 text-secondary">Loading brew log...</p>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="row">
      <div class="col-12">
        <div class="error-banner">{{ error }}</div>
        <div class="text-center mt-3">
          <button @click="loadBrewLog" class="btn btn-secondary">Retry</button>
        </div>
      </div>
    </div>

    <!-- Brew Log Details -->
    <div v-else-if="brewLog" class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-body">
            <div class="detail-sections">

              <!-- Brewing Details -->
              <div class="section">
                <h3>Brewing Details</h3>
                <div class="detail-grid">
                  <div class="detail-item">
                    <span class="label">Bean Name:</span>
                    <span class="value">{{ brewLog.beanName }}</span>
                  </div>
                  <div class="detail-grid">
                    <div class="detail-item">
                      <span class="label">Method:</span>
                      <span class="value">{{ brewLog.method || 'Not specified' }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">Grind Size:</span>
                      <span class="value">{{ brewLog.grindSize || 'Not specified' }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">Bean Weight:</span>
                      <span class="value">{{
                          brewLog.beanWeightGram ? `${brewLog.beanWeightGram}g` : 'Not specified'
                        }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">Water Weight:</span>
                      <span class="value">{{
                          brewLog.waterWeightGram ? `${brewLog.waterWeightGram}g` : 'Not specified'
                        }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">Ratio:</span>
                      <span class="value">{{ brewLog.ratio ? `1:${brewLog.ratio}` : 'Not specified' }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">Water Temperature:</span>
                      <span class="value">{{
                          brewLog.waterTemperature ? `${brewLog.waterTemperature}°C` : 'Not specified'
                        }}</span>
                    </div>
                    <div class="detail-item">
                      <span class="label">Brew Time:</span>
                      <span class="value">{{ brewLog.formattedBrewTime || 'Not specified' }}</span>
                    </div>
                  </div>
                </div>

                <!-- Rating -->
                <div class="section" v-if="brewLog.rating">
                  <h3>Rating</h3>
                  <div class="rating-display">
                    <div class="stars">
              <span
                  v-for="i in 10"
                  :key="i"
                  class="star"
                  :class="{ filled: i <= brewLog.rating }"
              >
                ★
              </span>
                    </div>
                    <span class="rating-value">{{ brewLog.rating }}/10</span>
                  </div>
                </div>

                <!-- Taste Notes -->
                <div class="section" v-if="brewLog.tasteNotes">
                  <h3>Taste Notes</h3>
                  <div class="taste-notes">
                    <p>{{ brewLog.tasteNotes }}</p>
                  </div>
                </div>


                <!-- Recipe Information -->
                <div class="section" v-if="brewLog.recipeId || brewLog.importedRecipeTitle">
                  <h3>Recipe Information</h3>
                  <div class="detail-grid">
                    <div class="detail-item" v-if="brewLog.importedRecipeTitle">
                      <span class="label">Recipe Title:</span>
                      <span class="value">{{ brewLog.importedRecipeTitle }}</span>
                    </div>
                    <div class="detail-item" v-if="brewLog.importedRecipeMethod">
                      <span class="label">Recipe Method:</span>
                      <span class="value">{{ brewLog.importedRecipeMethod }}</span>
                    </div>
                    <div class="detail-item" v-if="brewLog.recipeId">
                      <span class="label">Recipe ID:</span>
                      <span class="value">{{ brewLog.recipeId }}</span>
                    </div>
                  </div>
                </div>

                <!-- Imported Recipe Steps -->
                <div class="section" v-if="brewLog.importedRecipeSteps && brewLog.importedRecipeSteps.length > 0">
                  <h3>Recipe Steps</h3>
                  <div class="recipe-steps">
                    <div
                        v-for="(step, index) in brewLog.importedRecipeSteps"
                        :key="index"
                        class="recipe-step"
                    >
                      <span class="step-number">{{ index + 1 }}.</span>
                      <span class="step-text">{{ step }}</span>
                    </div>
                  </div>
                </div>

                <!-- Timestamps -->
                <div class="section">
                  <div class="detail-item">
                    <span class="label">Created:</span>
                    <span class="value">{{ brewLog.formattedCreatedAt }}</span>
                  </div>
                  <div class="detail-item">
                    <span class="label">Last Updated:</span>
                    <span class="value">{{ brewLog.formattedUpdatedAt }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <ConfirmModal
      :visible="showDeleteModal"
      title="Delete Brew Log"
      message="Are you sure you want to delete this brew log?"
      @confirm="confirmDelete"
      @cancel="showDeleteModal = false"
    />
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue'
import {useRouter, useRoute} from 'vue-router'
import {getBrewLogById, deleteBrewLog} from '../api/brewlogs.js'
import ConfirmModal from '../components/ConfirmModal.vue'
import {useToast} from '../stores/toast.js'

const router = useRouter()
const route = useRoute()
const toast = useToast()

const brewLog = ref(null)
const loading = ref(false)
const error = ref(null)
const showDeleteModal = ref(false)

const goBack = () => {
  router.push('/brewlogs')
}

const editBrewLog = () => {
  router.push(`/brewlogs/${route.params.id}/edit`)
}

const deleteBrewLogHandler = () => {
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  showDeleteModal.value = false
  try {
    await deleteBrewLog(route.params.id)
    router.push('/brewlogs')
  } catch (err) {
    console.error('Error deleting brew log:', err)
    toast.error('Failed to delete brew log. Please try again.')
  }
}

const loadBrewLog = async () => {
  if (!route.params.id) return

  loading.value = true
  error.value = null

  try {
    const response = await getBrewLogById(route.params.id)
    brewLog.value = response
  } catch (err) {
    console.error('Error loading brew log:', err)
    error.value = 'Failed to load brew log. Please try again.'
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadBrewLog()
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

.detail-sections {
  padding: 0;
}

.section {
  margin-bottom: 2rem;
}

.section:last-child {
  margin-bottom: 0;
}

.section h3 {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1.25rem;
  border-bottom: 2px solid #e9ecef;
  padding-bottom: 0.5rem;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 1rem;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.label {
  font-size: 0.75rem;
  color: #6c757d;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.value {
  font-size: 0.875rem;
  color: #495057;
  font-weight: 500;
}

.rating-display {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.stars {
  display: flex;
  gap: 0.125rem;
}

.star {
  color: #e9ecef;
  font-size: 1.25rem;
}

.star.filled {
  color: #ffc107;
}

.rating-value {
  font-weight: 600;
  color: #495057;
  font-size: 1rem;
}

.taste-notes {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 0.5rem;
  border-left: 4px solid #007bff;
}

.taste-notes p {
  margin: 0;
  color: #495057;
  line-height: 1.6;
  font-style: italic;
}

.recipe-steps {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 0.5rem;
  border-left: 4px solid #28a745;
}

.recipe-step {
  display: flex;
  align-items: flex-start;
  margin-bottom: 0.5rem;
  padding: 0.5rem;
  background: white;
  border-radius: 0.25rem;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1);
}

.recipe-step:last-child {
  margin-bottom: 0;
}

.step-number {
  font-weight: 600;
  color: #28a745;
  margin-right: 0.75rem;
  min-width: 1.25rem;
}

.step-text {
  color: #495057;
  line-height: 1.5;
}

@media (max-width: 768px) {
  .detail-grid {
    grid-template-columns: 1fr;
  }
}
</style>

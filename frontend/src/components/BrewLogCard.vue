<template>
  <div class="card h-100">
    <div class="card-body">
      <!-- Header with title and actions -->
      <div class="d-flex justify-content-between align-items-start mb-1">
        <h5 class="card-title mb-0">
          {{ brewLog.beanName }}
        </h5>
        <div v-if="hasActions" class="dropdown">
          <button class="btn btn-outline-secondary btn-sm" type="button" data-bs-toggle="dropdown">
            <i class="bi bi-three-dots-vertical"></i>
          </button>
          <ul class="dropdown-menu">
            <li v-if="showView">
              <router-link :to="`/brewlogs/${brewLog.id}`" class="dropdown-item">
                <i class="bi bi-eye me-2"></i>View Details
              </router-link>
            </li>
            <li v-if="showEdit">
              <router-link :to="`/brewlogs/${brewLog.id}/edit`" class="dropdown-item">
                <i class="bi bi-pencil me-2"></i>Edit
              </router-link>
            </li>
            <li v-if="(showEdit || showView) && showDelete">
              <hr class="dropdown-divider">
            </li>
            <li v-if="showDelete">
              <button @click="handleDelete" class="dropdown-item text-danger">
                <i class="bi bi-trash me-2"></i>Delete
              </button>
            </li>
          </ul>
        </div>
      </div>

      <!-- Rating display -->
      <div v-if="brewLog.rating" class="mb-3">
        <div class="d-flex align-items-center">
          <div class="stars me-2">
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

      <!-- Brew details grid -->
      <div class="row g-1 mb-2">
        <div class="col-6">
          <small class="text-secondary">Method</small>
          <p class="mb-0">{{ brewLog.method || 'N/A' }}</p>
        </div>
        <div class="col-6">
          <small class="text-secondary">Ratio</small>
          <p class="mb-0">{{ brewLog.ratio ? `1:${brewLog.ratio}` : 'N/A' }}</p>
        </div>
        <div class="col-6">
          <small class="text-secondary">Bean Weight</small>
          <p class="mb-0">{{ brewLog.beanWeightGram ? `${brewLog.beanWeightGram}g` : 'N/A' }}</p>
        </div>
        <div class="col-6">
          <small class="text-secondary">Water Weight</small>
          <p class="mb-0">{{ brewLog.waterWeightGram ? `${brewLog.waterWeightGram}g` : 'N/A' }}</p>
        </div>
        <div class="col-6">
          <small class="text-secondary">Temperature</small>
          <p class="mb-0">{{ brewLog.waterTemperature ? `${brewLog.waterTemperature}°C` : 'N/A' }}</p>
        </div>
        <div class="col-6">
          <small class="text-secondary">Brew Time</small>
          <p class="mb-0">{{ brewLog.formattedBrewTime || 'N/A' }}</p>
        </div>
        <div class="col-6">
          <small class="text-secondary">Grind Size</small>
          <p class="mb-0">{{ brewLog.grindSize || 'N/A' }}</p>
        </div>
        <div class="col-6">
          <small class="text-secondary">Date</small>
          <p class="mb-0">{{ formatDate(brewLog.createdAt) }}</p>
        </div>
      </div>

      <!-- Taste Notes (if available) -->
      <div v-if="brewLog.tasteNotes">
        <small class="text-secondary">Taste Notes</small>
        <p class="mb-0 small">{{ truncateText(brewLog.tasteNotes, 120) }}</p>
      </div>

      <!-- Recipe Information (if available) -->
      <div v-if="brewLog.importedRecipeTitle" class="mt-2">
        <small class="text-secondary">Recipe</small>
        <p class="mb-0 small">{{ brewLog.importedRecipeTitle }}</p>
      </div>

    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

// Props
const props = defineProps({
  brewLog: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && typeof value === 'object' && value.id && value.beanName
    }
  },
  showEdit: {
    type: Boolean,
    default: true
  },
  showDelete: {
    type: Boolean,
    default: true
  },
  showView: {
    type: Boolean,
    default: true
  }
})

// Emits
const emit = defineEmits({
  delete: (brewLog) => {
    return brewLog && typeof brewLog === 'object' && brewLog.id
  }
})

// Computed properties
const hasActions = computed(() => {
  return props.showEdit || props.showDelete || props.showView
})

// Methods
function formatDate(dateStr) {
  if (!dateStr) return 'N/A'
  const date = new Date(dateStr)
  return date.toLocaleDateString('en-GB')
}

function truncateText(text, maxLength) {
  if (!text) return 'N/A'
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

function handleDelete() {
  emit('delete', props.brewLog)
}
</script>

<style scoped>
.stars {
  display: flex;
  gap: 1px;
}

.star {
  color: #e9ecef;
  font-size: 14px;
}

.star.filled {
  color: #ffc107;
}

.rating-value {
  font-weight: 600;
  color: #495057;
  font-size: 14px;
}
</style>

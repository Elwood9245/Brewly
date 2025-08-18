<template>
  <div class="card h-100">
    <div class="card-body">
      <!-- Header with title and actions -->
      <div class="d-flex justify-content-between align-items-start mb-3">
        <h5 class="card-title mb-0">
          {{ bean.name }}{{ bean.roastType ? ` (${formatEnum(bean.roastType)})` : '' }}
        </h5>
        <div v-if="hasActions" class="dropdown">
          <button class="btn btn-outline-secondary btn-sm" type="button" data-bs-toggle="dropdown">
            <i class="bi bi-three-dots-vertical"></i>
          </button>
          <ul class="dropdown-menu">
            <li v-if="showEdit">
              <router-link :to="`/beans/${bean.id}/edit`" class="dropdown-item">
                <i class="bi bi-pencil me-2"></i>Edit
              </router-link>
            </li>
            <li v-if="showView">
              <router-link :to="`/beans/${bean.id}`" class="dropdown-item">
                <i class="bi bi-eye me-2"></i>View Details
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

      <!-- Bean details grid -->
      <div class="row g-1 mb-2">
        <div class="col-6">
          <small class="text-secondary">Roast Date</small>
          <p class="mb-0">{{ formatDate(bean.roastedAt) }} ({{ calculateDaysSinceRoast() }} days)</p>
        </div>
        <div class="col-6">
          <small class="text-secondary">Weight</small>
          <p class="mb-0">{{ formatWeight() }}</p>
        </div>
        <div class="col-6">
          <small class="text-secondary">Origin</small>
          <p class="mb-0">{{ bean.origin || 'N/A' }}</p>
        </div>
        <div class="col-6">
          <small class="text-secondary">Roaster</small>
          <p class="mb-0">{{ bean.roaster || 'N/A' }}</p>
        </div>
        <div class="col-6">
          <small class="text-secondary">Blend Type</small>
          <p class="mb-0">{{ formatEnum(bean.blend) }}</p>
        </div>
        <div class="col-6">
          <small class="text-secondary">Rest Days</small>
          <p class="mb-0">{{ bean.restDays || 'N/A' }}</p>
        </div>
      </div>

      <!-- Flavour Profile (full width) -->
      <div class="mb-1">
        <small class="text-secondary">Flavour Profile</small>
        <p class="mb-0 small">{{ bean.flavour || 'No flavour profile available' }}</p>
      </div>

      <!-- Notes (if available) -->
      <div v-if="bean.notes">
        <small class="text-secondary">Notes</small>
        <p class="mb-0 small">{{ truncateText(bean.notes, 120) }}</p>
      </div>

    </div>
  </div>
</template>

<script setup>
import {computed} from 'vue'
import {beanUtils} from '../api/beans.js'

// Props
const props = defineProps({
  bean: {
    type: Object,
    required: true,
    validator: (value) => {
      return value && typeof value === 'object' && value.id && value.name
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
    default: false
  }
})

// Emits
const emit = defineEmits({
  delete: (bean) => {
    return bean && typeof bean === 'object' && bean.id
  }
})

// Computed properties
const hasActions = computed(() => {
  return props.showEdit || props.showDelete || props.showView
})

// Methods
function formatDate(dateStr) {
  return beanUtils.formatDate(dateStr)
}

function truncateText(text, maxLength) {
  if (!text) return 'N/A'
  if (text.length <= maxLength) return text
  return text.substring(0, maxLength) + '...'
}

function formatEnum(enumValue) {
  if (!enumValue) return 'N/A'

  const enumMappings = {
    'SINGLE_ORIGIN': 'Single Origin',
    'BLEND': 'Blend',
    'UNKNOWN': 'Unknown',
    'POUR_OVER': 'Pour Over',
    'ESPRESSO': 'Espresso',
    'OMNI': 'Omni'
  }

  return enumMappings[enumValue] || enumValue.replace(/_/g, ' ').toLowerCase().replace(/\b\w/g, l => l.toUpperCase())
}

function formatWeight() {
  const totalWeight = props.bean.weight || 0
  const consumed = props.bean.consumption || 0
  const remaining = totalWeight - consumed

  if (totalWeight === 0) return '0g'

  if (consumed === 0) {
    return `${totalWeight}g`
  }

  if (remaining <= 0) {
    return `${totalWeight}g (${consumed}/${totalWeight}g consumed)`
  }

  return `${remaining}g (${consumed}/${totalWeight}g consumed)`
}

function calculateDaysSinceRoast() {
  if (!props.bean.roastedAt) return 'N/A'
  const roastDate = new Date(props.bean.roastedAt)
  const today = new Date()
  const diffTime = Math.abs(today - roastDate)
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
  return diffDays
}

function handleDelete() {
  emit('delete', props.bean)
}
</script>

<style scoped>
</style>

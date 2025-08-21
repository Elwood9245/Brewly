<template>
  <div class="card h-100 recipe-card">
    <div class="card-body">
      <!-- Header with title and actions -->
      <div class="d-flex justify-content-between align-items-start mb-1">
        <h5 class="card-title mb-0">
          {{ recipe.title }}
        </h5>
        <div v-if="hasActions" class="dropdown">
          <button class="btn btn-outline-secondary btn-sm" type="button" data-bs-toggle="dropdown">
            <i class="bi bi-three-dots-vertical"></i>
          </button>
          <ul class="dropdown-menu">
            <li v-if="showView">
              <router-link :to="`/recipes/${recipe.id}`" class="dropdown-item">
                <i class="bi bi-eye me-2"></i>View Details
              </router-link>
            </li>
            <li v-if="showEdit">
              <router-link :to="`/recipes/${recipe.id}/edit`" class="dropdown-item">
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

      <!-- Method and visibility -->
      <div class="mb-3">
        <span v-if="recipe.method" class="badge bg-dark me-2">{{ recipe.method }}</span>
        <span :class="visibilityBadgeClass">{{ recipe.visibility }}</span>
      </div>

      <!-- Description -->
      <p v-if="recipe.description" class="card-text small text-secondary mb-3">
        {{ truncateText(recipe.description, 120) }}
      </p>

      <!-- Steps preview -->
      <div v-if="recipe.steps && recipe.steps.length > 0" class="mb-3">
        <small class="text-secondary">Steps:</small>
        <div class="steps-preview">
          <div 
            v-for="(step, index) in recipe.steps.slice(0, 2)" 
            :key="index"
            class="step-item"
          >
            <span class="step-number">{{ index + 1 }}.</span>
            <span class="step-text">{{ truncateText(step.instruction, 60) }}</span>
          </div>
          <div v-if="recipe.steps.length > 2" class="step-more">
            <small class="text-muted">+{{ recipe.steps.length - 2 }} more steps</small>
          </div>
        </div>
      </div>

      <!-- Stats -->
      <div class="d-flex justify-content-between align-items-center mb-3">
        <div class="stats">
          <span class="stat-item">
            <i class="bi bi-heart me-1"></i>{{ recipe.likeCount || 0 }}
          </span>
          <span class="stat-item">
            <i class="bi bi-chat me-1"></i>{{ recipe.commentCount || 0 }}
          </span>
        </div>
        <small class="text-muted">{{ formatDate(recipe.createdAt) }}</small>
      </div>

      <!-- Actions -->
      <div class="d-flex gap-2">
        <button 
          v-if="showLikeButton"
          @click="handleLike"
          :class="likeButtonClass"
          class="btn btn-sm"
        >
          <i :class="likeIconClass"></i>
          {{ recipe.isLikedByCurrentUser ? 'Liked' : 'Like' }}
        </button>
        <router-link 
          v-if="showViewButton"
          :to="`/recipes/${recipe.id}`" 
          class="btn btn-primary btn-sm"
        >
          View Recipe
        </router-link>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  recipe: {
    type: Object,
    required: true
  },
  showActions: {
    type: Boolean,
    default: false
  },
  showLikeButton: {
    type: Boolean,
    default: true
  },
  showViewButton: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['delete', 'like'])

const hasActions = computed(() => props.showActions)
const showView = computed(() => true)
const showEdit = computed(() => props.showActions)
const showDelete = computed(() => props.showActions)

const visibilityBadgeClass = computed(() => {
  return props.recipe.visibility === 'PUBLIC'
    ? 'badge bg-success'
    : 'badge bg-secondary'
})

const likeButtonClass = computed(() => {
  return props.recipe.isLikedByCurrentUser 
    ? 'btn-danger' 
    : 'btn-outline-danger'
})

const likeIconClass = computed(() => {
  return props.recipe.isLikedByCurrentUser 
    ? 'bi bi-heart-fill' 
    : 'bi bi-heart'
})

const handleDelete = () => {
  if (confirm('Are you sure you want to delete this recipe?')) {
    emit('delete', props.recipe.id)
  }
}

const handleLike = () => {
  emit('like', props.recipe.id)
}

const truncateText = (text, maxLength) => {
  if (!text) return ''
  return text.length > maxLength ? text.substring(0, maxLength) + '...' : text
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString()
}
</script>

<style scoped>
.recipe-card {
  transition: transform 0.2s, box-shadow 0.2s;
}

.recipe-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.steps-preview {
  background: #f8f9fa;
  padding: 0.5rem;
  border-radius: 0.25rem;
  margin-top: 0.25rem;
}

.step-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 0.25rem;
  font-size: 0.875rem;
}

.step-item:last-child {
  margin-bottom: 0;
}

.step-number {
  font-weight: 600;
  color: #6c757d;
  margin-right: 0.5rem;
  min-width: 1rem;
}

.step-text {
  color: #495057;
  line-height: 1.4;
}

.step-more {
  text-align: center;
  margin-top: 0.25rem;
}

.stats {
  display: flex;
  gap: 1rem;
}

.stat-item {
  font-size: 0.875rem;
  color: #6c757d;
}

.badge {
  font-size: 0.75rem;
}
</style>

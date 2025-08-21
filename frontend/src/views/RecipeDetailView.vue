<template>
  <div class="container-fluid py-4">
    <!-- Loading State -->
    <div v-if="loading" class="row">
      <div class="col-12 text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-3 text-secondary">Loading recipe...</p>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="row">
      <div class="col-12">
        <div class="error-banner">{{ error }}</div>
        <div class="text-center mt-3">
          <button @click="loadRecipe" class="btn btn-secondary">Retry</button>
        </div>
      </div>
    </div>

    <!-- Recipe Details -->
    <div v-else-if="recipe" class="row">
      <!-- Recipe Header -->
      <div class="col-12 mb-4">
        <div class="d-flex justify-content-between align-items-start">
          <div class="flex-grow-1">
            <div class="d-flex align-items-center gap-3 mb-2">
              <button @click="goBack" class="btn btn-outline-secondary">
                <i class="bi bi-arrow-left me-2"></i>Back
              </button>
              <h1 class="h2 mb-0">{{ recipe.title }}</h1>
            </div>
            <div class="d-flex align-items-center gap-3 mb-3">
              <span v-if="recipe.method" class="badge bg-primary fs-6">{{ recipe.method }}</span>
              <span :class="visibilityBadgeClass">{{ recipe.visibility }}</span>
              <span class="text-muted">Created {{ formatDate(recipe.createdAt) }}</span>
            </div>
            <p v-if="recipe.description" class="lead text-secondary">{{ recipe.description }}</p>
          </div>
          <div class="d-flex gap-2">
            <button 
              @click="handleLike"
              :class="likeButtonClass"
              class="btn"
            >
              <i :class="likeIconClass"></i>
              {{ recipe.likeCount || 0 }}
            </button>
            <button @click="toggleComments" class="btn btn-outline-primary">
              <i class="bi bi-chat me-2"></i>{{ recipe.commentCount || 0 }}
            </button>
            <div v-if="canEdit" class="dropdown">
              <button class="btn btn-outline-secondary" type="button" data-bs-toggle="dropdown">
                <i class="bi bi-three-dots-vertical"></i>
              </button>
              <ul class="dropdown-menu">
                <li>
                  <router-link :to="`/recipes/${recipe.id}/edit`" class="dropdown-item">
                    <i class="bi bi-pencil me-2"></i>Edit
                  </router-link>
                </li>
                <li>
                  <button @click="handleDelete" class="dropdown-item text-danger">
                    <i class="bi bi-trash me-2"></i>Delete
                  </button>
                </li>
              </ul>
            </div>
          </div>
        </div>
      </div>

      <!-- Recipe Content -->
      <div class="col-lg-8">
        <!-- Steps Section -->
        <div class="card mb-4">
          <div class="card-header">
            <h3 class="h5 mb-0">Recipe Steps</h3>
          </div>
          <div class="card-body">
            <div v-if="recipe.steps && recipe.steps.length > 0" class="steps-container">
              <div 
                v-for="(step, index) in recipe.steps" 
                :key="index"
                class="step-item"
              >
                <div class="step-header">
                  <span class="step-number">{{ index + 1 }}</span>
                  <h4 class="step-title">{{ step.instruction }}</h4>
                </div>
                <div class="step-details">
                  <span v-if="step.time" class="detail-item">
                    <i class="bi bi-clock me-2"></i>{{ step.time }} seconds
                  </span>
                  <span v-if="step.waterAmount" class="detail-item">
                    <i class="bi bi-droplet me-2"></i>{{ step.waterAmount }}g water
                  </span>
                </div>
              </div>
            </div>
            <div v-else class="text-center text-muted py-4">
              <i class="bi bi-exclamation-circle fs-1 mb-3"></i>
              <p>No steps defined for this recipe.</p>
            </div>
          </div>
        </div>

        <!-- Comments Section -->
        <div v-if="showComments" class="card">
          <div class="card-header">
            <h3 class="h5 mb-0">Comments ({{ recipe.commentCount || 0 }})</h3>
          </div>
          <div class="card-body">
            <!-- Add Comment -->
            <div class="mb-4">
              <form @submit.prevent="addComment">
                <div class="form-group">
                  <textarea 
                    v-model="newComment"
                    class="form-control"
                    rows="3"
                    placeholder="Share your thoughts about this recipe..."
                    :disabled="submittingComment"
                  ></textarea>
                </div>
                <div class="d-flex justify-content-end">
                  <button 
                    type="submit" 
                    class="btn btn-primary"
                    :disabled="!newComment.trim() || submittingComment"
                  >
                    <span v-if="submittingComment" class="spinner-border spinner-border-sm me-2"></span>
                    Post Comment
                  </button>
                </div>
              </form>
            </div>

            <!-- Comments List -->
            <div v-if="comments.length > 0" class="comments-list">
              <div 
                v-for="comment in comments" 
                :key="comment.id"
                class="comment-item"
              >
                <div class="comment-header">
                  <div class="d-flex justify-content-between align-items-start">
                    <div>
                      <strong>{{ comment.username }}</strong>
                      <small class="text-muted ms-2">{{ formatDate(comment.createdAt) }}</small>
                    </div>
                    <button 
                      v-if="canDeleteComment(comment)"
                      @click="deleteComment(comment.id)"
                      class="btn btn-outline-danger btn-sm"
                    >
                      <i class="bi bi-trash"></i>
                    </button>
                  </div>
                </div>
                <div class="comment-content">
                  <p class="mb-0">{{ comment.content }}</p>
                </div>
              </div>
            </div>
            <div v-else class="text-center text-muted py-4">
              <p>No comments yet. Be the first to share your thoughts!</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Sidebar -->
      <div class="col-lg-4">
        <!-- Recipe Stats -->
        <div class="card mb-4">
          <div class="card-header">
            <h4 class="h6 mb-0">Recipe Stats</h4>
          </div>
          <div class="card-body">
            <div class="stats-grid">
              <div class="stat-item">
                <div class="stat-value">{{ recipe.likeCount || 0 }}</div>
                <div class="stat-label">Likes</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ recipe.commentCount || 0 }}</div>
                <div class="stat-label">Comments</div>
              </div>
              <div class="stat-item">
                <div class="stat-value">{{ recipe.steps?.length || 0 }}</div>
                <div class="stat-label">Steps</div>
              </div>
            </div>
          </div>
        </div>

        <!-- Recipe Info -->
        <div class="card">
          <div class="card-header">
            <h4 class="h6 mb-0">Recipe Information</h4>
          </div>
          <div class="card-body">
            <div class="info-item">
              <span class="info-label">Method:</span>
              <span class="info-value">{{ recipe.method || 'Not specified' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Visibility:</span>
              <span class="info-value">{{ recipe.visibility }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Created:</span>
              <span class="info-value">{{ formatDate(recipe.createdAt) }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">Updated:</span>
              <span class="info-value">{{ formatDate(recipe.updatedAt) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { 
  getRecipeById, 
  likeRecipe, 
  unlikeRecipe, 
  getRecipeComments, 
  addComment as addCommentApi,
  deleteComment as deleteCommentApi,
  deleteRecipe
} from '../api/recipes.js'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()

const recipe = ref(null)
const comments = ref([])
const loading = ref(false)
const error = ref(null)
const showComments = ref(false)
const newComment = ref('')
const submittingComment = ref(false)

const canEdit = computed(() => {
  return recipe.value && recipe.value.userId === auth.currentUser.value?.id
})

const visibilityBadgeClass = computed(() => {
  return recipe.value?.visibility === 'PUBLIC' 
    ? 'badge bg-success' 
    : 'badge bg-secondary'
})

const likeButtonClass = computed(() => {
  return recipe.value?.isLikedByCurrentUser 
    ? 'btn-danger' 
    : 'btn-outline-danger'
})

const likeIconClass = computed(() => {
  return recipe.value?.isLikedByCurrentUser 
    ? 'bi bi-heart-fill' 
    : 'bi bi-heart'
})

const goBack = () => {
  router.back()
}

const loadRecipe = async () => {
  if (!route.params.id) return

  loading.value = true
  error.value = null

  try {
    const response = await getRecipeById(route.params.id)
    recipe.value = response
  } catch (err) {
    console.error('Error loading recipe:', err)
    error.value = 'Failed to load recipe. Please try again.'
  } finally {
    loading.value = false
  }
}

const loadComments = async () => {
  if (!route.params.id) return

  try {
    const response = await getRecipeComments(route.params.id)
    comments.value = response.content || []
  } catch (err) {
    console.error('Error loading comments:', err)
  }
}

// Load comments when comments section is shown
const toggleComments = () => {
  showComments.value = !showComments.value
  if (showComments.value && comments.value.length === 0) {
    loadComments()
  }
}

const handleLike = async () => {
  if (!recipe.value) return

  try {
    if (recipe.value.isLikedByCurrentUser) {
      await unlikeRecipe(recipe.value.id)
      recipe.value.isLikedByCurrentUser = false
      recipe.value.likeCount = Math.max(0, (recipe.value.likeCount || 0) - 1)
    } else {
      await likeRecipe(recipe.value.id)
      recipe.value.isLikedByCurrentUser = true
      recipe.value.likeCount = (recipe.value.likeCount || 0) + 1
    }
  } catch (err) {
    console.error('Error toggling like:', err)
    alert('Failed to update like. Please try again.')
  }
}

const addComment = async () => {
  if (!newComment.value.trim() || !recipe.value) return

  submittingComment.value = true

  try {
    const response = await addCommentApi(recipe.value.id, { content: newComment.value })
    comments.value.unshift(response)
    recipe.value.commentCount = (recipe.value.commentCount || 0) + 1
    newComment.value = ''
  } catch (err) {
    console.error('Error adding comment:', err)
    alert('Failed to add comment. Please try again.')
  } finally {
    submittingComment.value = false
  }
}

const deleteComment = async (commentId) => {
  if (!confirm('Are you sure you want to delete this comment?')) return

  try {
    await deleteCommentApi(commentId)
    comments.value = comments.value.filter(c => c.id !== commentId)
    recipe.value.commentCount = Math.max(0, (recipe.value.commentCount || 0) - 1)
  } catch (err) {
    console.error('Error deleting comment:', err)
    alert('Failed to delete comment. Please try again.')
  }
}

const canDeleteComment = (comment) => {
  return comment.userId === auth.currentUser.value?.id || 
         recipe.value?.userId === auth.currentUser.value?.id
}

const handleDelete = async () => {
  if (!confirm('Are you sure you want to delete this recipe?')) return

  try {
    await deleteRecipe(recipe.value.id)
    router.push('/recipes')
  } catch (err) {
    console.error('Error deleting recipe:', err)
    alert('Failed to delete recipe. Please try again.')
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('en-GB', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}

onMounted(() => {
  loadRecipe()
})
</script>

<style scoped>
.error-banner {
  background-color: #f8d7da;
  color: #721c24;
  padding: 1rem;
  border: 1px solid #f5c6cb;
  border-radius: 0.75rem;
  margin-bottom: 1rem;
}

.steps-container {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.step-item {
  border: 1px solid #dee2e6;
  border-radius: 0.75rem;
  padding: 1.5rem;
  background: #f8f9fa;
}

.step-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 1rem;
}

.step-number {
  background: #007bff;
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  margin-right: 1rem;
  flex-shrink: 0;
}

.step-title {
  margin: 0;
  font-size: 1.1rem;
  line-height: 1.4;
}

.step-details {
  display: flex;
  gap: 1.5rem;
  font-size: 0.9rem;
  color: #6c757d;
}

.detail-item {
  display: flex;
  align-items: center;
}

.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.comment-item {
  border: 1px solid #dee2e6;
  border-radius: 0.5rem;
  padding: 1rem;
  background: white;
}

.comment-header {
  margin-bottom: 0.5rem;
}

.comment-content {
  color: #495057;
  line-height: 1.5;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
}

.stat-item {
  text-align: center;
}

.stat-value {
  font-size: 1.5rem;
  font-weight: 600;
  color: #007bff;
}

.stat-label {
  font-size: 0.875rem;
  color: #6c757d;
  margin-top: 0.25rem;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.5rem 0;
  border-bottom: 1px solid #f1f3f4;
}

.info-item:last-child {
  border-bottom: none;
}

.info-label {
  font-weight: 500;
  color: #6c757d;
}

.info-value {
  color: #495057;
}

.badge {
  font-size: 0.875rem;
}
</style>

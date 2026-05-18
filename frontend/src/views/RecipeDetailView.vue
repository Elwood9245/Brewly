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
                <!-- Title Row -->
        <div class="d-flex align-items-center gap-3 mb-2">
          <button @click="goBack" class="btn btn-primary">
            <i class="bi bi-arrow-left"></i>
          </button>
          <h1 class="h2 mb-0">{{ recipe.title }}</h1>
        </div>

        <!-- Buttons Row -->
        <div class="d-flex gap-2 justify-content-center">
          <button
            @click="handleLike"
            class="btn btn-primary"
          >
            <i :class="likeIconClass"></i>
            {{ recipe.likeCount || 0 }}
          </button>

          <router-link
            v-if="canEdit"
            :to="`/recipes/${recipe.id}/edit`"
            class="btn btn-primary"
          >
            <i class="bi bi-pencil"></i>
          </router-link>

          <button 
            v-if="canEdit"
            @click="handleDelete" 
            class="btn btn-outline-danger"
          >
            <i class="bi bi-trash"></i>
          </button>
        </div>
      </div>

      <!-- Recipe Content -->
      <div class="col-12">
        <div v-if="recipe.description" class="card mb-4">
          <div class="card-body p-4">
            <div class="d-flex justify-content-between align-items-start mb-1">
              <h5 class="card-title mb-0">Description</h5>
            </div>
            <p class="mb-0">{{ recipe.description }}</p>
          </div>
        </div>

        <!-- Recipe Stats & Info -->
        <div class="card mb-4">
          <div class="card-body p-3">
            <div class="d-flex justify-content-between align-items-start mb-2">
              <h5 class="card-title mb-0">Recipe Information</h5>
            </div>
            <ul class="recipe-info-list">
              <li class="info-list-item">
                <span class="info-label">Likes:</span>
                <span class="info-value">{{ recipe.likeCount || 0 }}</span>
              </li>
              <li class="info-list-item">
                <span class="info-label">Comments:</span>
                <span class="info-value">{{ recipe.commentCount || 0 }}</span>
              </li>
              <li class="info-list-item">
                <span class="info-label">Steps:</span>
                <span class="info-value">{{ recipe.steps?.length || 0 }}</span>
              </li>
              <li class="info-list-item">
                <span class="info-label">Method:</span>
                <span class="info-value">{{ recipe.method || 'Not specified' }}</span>
              </li>
              <li class="info-list-item">
                <span class="info-label">Visibility:</span>
                <span class="info-value">{{ recipe.visibility }}</span>
              </li>
              <li class="info-list-item">
                <span class="info-label">Created:</span>
                <span class="info-value">{{ formatDate(recipe.createdAt) }}</span>
              </li>
              <li class="info-list-item">
                <span class="info-label">Updated:</span>
                <span class="info-value">{{ formatDate(recipe.updatedAt) }}</span>
              </li>
            </ul>
          </div>
        </div>

        <!-- Steps Section -->
        <div class="card mb-4">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-start mb-1">
              <h5 class="card-title mb-0">Recipe Steps</h5>
            </div>
            <StepDisplay v-if="recipe.steps && recipe.steps.length > 0" :steps="recipe.steps" />
            <div v-else class="text-center text-muted py-4">
              <i class="bi bi-exclamation-circle fs-1 mb-3"></i>
              <p>No steps defined for this recipe.</p>
            </div>
          </div>
        </div>

        <!-- Comments Section -->
        <CommentSection 
          :comments="comments"
          :comment-count="recipe.commentCount || 0"
          :submitting="submittingComment"
          :can-delete-comment="canDeleteComment"
          @add-comment="addComment"
          @delete-comment="deleteComment"
        />
      </div>
    </div>

    <ConfirmModal
      :visible="showDeleteModal"
      title="Delete Recipe"
      :message="`Are you sure you want to delete '${recipe?.title}'?`"
      @confirm="confirmDelete"
      @cancel="showDeleteModal = false"
    />
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
import StepDisplay from '../components/StepDisplay.vue'
import CommentSection from '../components/CommentSection.vue'
import ConfirmModal from '../components/ConfirmModal.vue'
import { useToast } from '../stores/toast.js'

const router = useRouter()
const route = useRoute()
const auth = useAuthStore()
const toast = useToast()

const recipe = ref(null)
const comments = ref([])
const loading = ref(false)
const error = ref(null)
const submittingComment = ref(false)
const showDeleteModal = ref(false)

const canEdit = computed(() => {
  return recipe.value && recipe.value.userId === auth.currentUser.value?.id
})

const visibilityBadgeClass = computed(() => {
  return recipe.value?.visibility === 'PUBLIC' 
    ? 'badge bg-success' 
    : 'badge bg-secondary'
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
    // Load comments when recipe is loaded
    await loadComments()
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
    toast.error('Failed to update like. Please try again.')
  }
}

const addComment = async (content) => {
  if (!content.trim() || !recipe.value) return

  submittingComment.value = true

  try {
    const response = await addCommentApi(recipe.value.id, { content })
    comments.value.unshift(response)
    recipe.value.commentCount = (recipe.value.commentCount || 0) + 1
  } catch (err) {
    console.error('Error adding comment:', err)
    toast.error('Failed to add comment. Please try again.')
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
    toast.error('Failed to delete comment. Please try again.')
  }
}

const canDeleteComment = (comment) => {
  return comment.userId === auth.currentUser.value?.id ||
         recipe.value?.userId === auth.currentUser.value?.id
}

const handleDelete = () => {
  showDeleteModal.value = true
}

const confirmDelete = async () => {
  showDeleteModal.value = false
  try {
    await deleteRecipe(recipe.value.id)
    router.push('/recipes')
  } catch (err) {
    console.error('Error deleting recipe:', err)
    toast.error('Failed to delete recipe. Please try again.')
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



.recipe-info-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.info-list-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.4rem 0;
  border-bottom: 1px solid #f1f3f4;
}

.info-list-item:last-child {
  border-bottom: none;
}

.info-label {
  font-weight: 500;
  color: #6c757d;
  font-size: 0.875rem;
}

.info-value {
  color: #495057;
  font-size: 0.875rem;
}
</style>

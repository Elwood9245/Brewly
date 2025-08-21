<template>
  <div class="card">
    <div class="card-body">
      <div class="d-flex justify-content-between align-items-start mb-1">
        <h5 class="card-title mb-0">Comments ({{ commentCount }})</h5>
      </div>
      
      <!-- Add Comment -->
      <div class="mb-4">
        <form @submit.prevent="handleAddComment">
          <div class="form-group">
            <textarea 
              v-model="newComment"
              class="form-control"
              rows="3"
              placeholder="Share your thoughts about this recipe..."
              :disabled="submitting"
            ></textarea>
            <div v-if="error" class="error-msg">{{ error }}</div>
          </div>
          <div class="d-flex justify-content-end">
            <button 
              type="submit" 
              class="btn btn-primary mt-3"
              :disabled="submitting"
            >
              <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
              Post Comment
            </button>
          </div>
        </form>
      </div>

      <!-- Comments List -->
      <div v-if="comments.length > 0" class="comments-list">
        <CommentItem 
          v-for="comment in comments" 
          :key="comment.id"
          :comment="comment"
          :can-delete="canDeleteComment(comment)"
          @delete="handleDeleteComment"
        />
      </div>
      <div v-else class="text-center text-muted py-4">
        <p>No comments yet. Be the first to share your thoughts!</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import CommentItem from './CommentItem.vue'

const props = defineProps({
  comments: {
    type: Array,
    required: true,
    default: () => []
  },
  commentCount: {
    type: Number,
    default: 0
  },
  submitting: {
    type: Boolean,
    default: false
  },
  canDeleteComment: {
    type: Function,
    required: true
  }
})

const emit = defineEmits(['add-comment', 'delete-comment'])

const newComment = ref('')
const error = ref('')

const handleAddComment = () => {
  error.value = ''
  
  if (!newComment.value.trim()) {
    error.value = 'Comment cannot be empty'
    return
  }
  
  emit('add-comment', newComment.value.trim())
  newComment.value = ''
}

const handleDeleteComment = (commentId) => {
  emit('delete-comment', commentId)
}
</script>

<style scoped>
.comments-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.error-msg {
  color: #e74c3c;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}
</style>

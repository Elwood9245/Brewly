<template>
  <div class="card mb-3">
    <div class="card-body p-3">
      <div class="d-flex justify-content-between align-items-start">
        <div>
          <strong>{{ comment.username }}</strong>
          <small class="text-muted ms-2">{{ formatDate(comment.createdAt) }}</small>
        </div>
        <button 
          v-if="canDelete"
          @click="handleDelete"
          class="btn btn-outline-danger btn-sm"
        >
          <i class="bi bi-trash"></i>
        </button>
      </div>
      <div class="comment-content">
        <p class="mb-0">{{ comment.content }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  comment: {
    type: Object,
    required: true
  },
  canDelete: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['delete'])

const handleDelete = () => {
  emit('delete', props.comment.id)
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  return new Date(dateString).toLocaleDateString('en-GB', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })
}
</script>

<style scoped>
.comment-content {
  color: #495057;
  line-height: 1.5;
  margin-top: 0.5rem;
}
</style>

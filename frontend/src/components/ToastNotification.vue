<template>
  <Teleport to="body">
    <div class="toast-container position-fixed top-0 end-0 p-3" style="z-index: 9999">
      <div
        v-for="toast in toasts"
        :key="toast.id"
        class="toast show align-items-center border-0"
        :class="toastClass(toast.type)"
        role="alert"
      >
        <div class="d-flex">
          <div class="toast-body d-flex align-items-center gap-2">
            <i :class="toastIcon(toast.type)"></i>
            {{ toast.message }}
          </div>
          <button type="button" class="btn-close btn-close-white me-2 m-auto" @click="removeToast(toast.id)"></button>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup>
import { useToast } from '../stores/toast.js'

const { toasts, remove: removeToast } = useToast()

function toastClass(type) {
  switch (type) {
    case 'success': return 'bg-success text-white'
    case 'error': return 'bg-danger text-white'
    case 'warning': return 'bg-warning'
    default: return 'bg-info text-white'
  }
}

function toastIcon(type) {
  switch (type) {
    case 'success': return 'bi bi-check-circle-fill'
    case 'error': return 'bi bi-exclamation-circle-fill'
    case 'warning': return 'bi bi-exclamation-triangle-fill'
    default: return 'bi bi-info-circle-fill'
  }
}
</script>

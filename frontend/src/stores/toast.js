import { ref } from 'vue'

const toasts = ref([])
let nextId = 0

export function useToast() {
  function show(message, type = 'info', duration = 4000) {
    const id = nextId++
    toasts.value.push({ id, message, type })
    if (duration > 0) {
      setTimeout(() => remove(id), duration)
    }
  }

  function remove(id) {
    toasts.value = toasts.value.filter(t => t.id !== id)
  }

  function success(message, duration) { show(message, 'success', duration) }
  function error(message, duration) { show(message, 'error', duration) }
  function warning(message, duration) { show(message, 'warning', duration) }
  function info(message, duration) { show(message, 'info', duration) }

  return { toasts, show, remove, success, error, warning, info }
}

import { ref } from 'vue'

const refreshTrigger = ref(0)

export function useBeanStore() {
  function triggerRefresh() {
    refreshTrigger.value++
  }

  return { refreshTrigger, triggerRefresh }
}

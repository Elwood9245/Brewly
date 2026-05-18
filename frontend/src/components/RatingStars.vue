<template>
  <span class="rating-stars" :class="sizeClass">
    <template v-for="i in max" :key="i">
      <i
        v-if="i <= Math.floor(rating)"
        class="bi bi-star-fill text-warning"
      ></i>
      <i
        v-else-if="i - 0.5 <= rating"
        class="bi bi-star-half text-warning"
      ></i>
      <i
        v-else
        class="bi bi-star text-muted"
      ></i>
    </template>
  </span>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  rating: { type: Number, default: 0 },
  max: { type: Number, default: 10 }
})

const sizeClass = computed(() => props.max > 5 ? 'rating-sm' : '')
</script>

<style scoped>
.rating-stars i { font-size: 0.85rem; }
.rating-sm i { font-size: 0.7rem; }
</style>

<template>
  <div class="ai-page">
    <div class="chat-wrapper">
      <AIChatInterface />
    </div>
  </div>
</template>

<script setup>
import AIChatInterface from '../components/AIChatInterface.vue'
</script>

<style scoped>
.ai-page {
  height: calc(100vh - 134px); /* Account for header and footer */
  display: flex;
  flex-direction: column;
}

.chat-wrapper {
  flex: 1;
  overflow: hidden;
}
</style>
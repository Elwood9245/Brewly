<template>
  <div class="step-display">
    <div 
      v-for="(step, index) in steps" 
      :key="index"
      class="step-item card"
    >
      <div class="row">
        <!-- Step Number Column -->
        <div class="col-auto">
          <div class="step-number-container">
            <span class="step-number">{{ index + 1 }}</span>
          </div>
        </div>
        
        <!-- Step Content Column -->
        <div class="col">
          <div class="step-instruction">{{ step.instruction || 'No instruction' }}</div>
          <div v-if="step.time || step.waterAmount" class="step-details">
            <span v-if="step.time" class="detail-item">
              <i class="bi bi-clock me-1"></i>{{ step.time }}s
            </span>
            <span v-if="step.waterAmount" class="detail-item">
              <i class="bi bi-droplet me-1"></i>{{ step.waterAmount }}g
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  steps: {
    type: Array,
    required: true,
    default: () => []
  }
})
</script>

<style scoped>
.step-display {
  margin-top: 1rem;
}

.step-item {
  margin-bottom: 0.5rem;
  padding: 1rem;
}

.step-item:last-child {
  margin-bottom: 0;
}

.step-number-container {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 100%;
}

.step-number {
  background: #2d2d2d;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 600;
  font-size: 0.875rem;
}

.step-instruction {
  line-height: 1.5;
  color: #2d2d2d;
  margin-bottom: 0.5rem;
}

.step-details {
  display: flex;
  gap: 1rem;
  font-size: 0.875rem;
  color: #6c757d;
}

.detail-item {
  display: flex;
  align-items: center;
}
</style>

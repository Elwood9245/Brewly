<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>Brew Log Details</h2>
        <button @click="closeModal" class="close-btn">&times;</button>
      </div>

      <div class="brew-log-detail">
        <!-- Bean Information -->
        <div class="section">
          <h3>Bean Information</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">Bean Name:</span>
              <span class="value">{{ brewLog.beanName }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Bean ID:</span>
              <span class="value">{{ brewLog.beanId }}</span>
            </div>
          </div>
        </div>

        <!-- Brewing Details -->
        <div class="section">
          <h3>Brewing Details</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">Method:</span>
              <span class="value">{{ brewLog.method || 'Not specified' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Grind Size:</span>
              <span class="value">{{ brewLog.grindSize || 'Not specified' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Bean Weight:</span>
              <span class="value">{{ brewLog.beanWeightGram ? `${brewLog.beanWeightGram}g` : 'Not specified' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Water Weight:</span>
              <span class="value">{{ brewLog.waterWeightGram ? `${brewLog.waterWeightGram}g` : 'Not specified' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Ratio:</span>
              <span class="value">{{ brewLog.ratio ? `1:${brewLog.ratio}` : 'Not specified' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Water Temperature:</span>
              <span class="value">{{ brewLog.waterTemperature ? `${brewLog.waterTemperature}°C` : 'Not specified' }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Brew Time:</span>
              <span class="value">{{ brewLog.formattedBrewTime || 'Not specified' }}</span>
            </div>
          </div>
        </div>

        <!-- Rating -->
        <div class="section" v-if="brewLog.rating">
          <h3>Rating</h3>
          <div class="rating-display">
            <div class="stars">
              <span 
                v-for="i in 10" 
                :key="i" 
                class="star" 
                :class="{ filled: i <= brewLog.rating }"
              >
                ★
              </span>
            </div>
            <span class="rating-value">{{ brewLog.rating }}/10</span>
          </div>
        </div>

        <!-- Taste Notes -->
        <div class="section" v-if="brewLog.tasteNotes">
          <h3>Taste Notes</h3>
          <div class="taste-notes">
            <p>{{ brewLog.tasteNotes }}</p>
          </div>
        </div>



        <!-- Recipe Information -->
        <div class="section" v-if="brewLog.recipeId || brewLog.importedRecipeTitle">
          <h3>Recipe Information</h3>
          <div class="detail-grid">
            <div class="detail-item" v-if="brewLog.importedRecipeTitle">
              <span class="label">Recipe Title:</span>
              <span class="value">{{ brewLog.importedRecipeTitle }}</span>
            </div>
            <div class="detail-item" v-if="brewLog.importedRecipeMethod">
              <span class="label">Recipe Method:</span>
              <span class="value">{{ brewLog.importedRecipeMethod }}</span>
            </div>
            <div class="detail-item" v-if="brewLog.recipeId">
              <span class="label">Recipe ID:</span>
              <span class="value">{{ brewLog.recipeId }}</span>
            </div>
          </div>
        </div>

        <!-- Timestamps -->
        <div class="section">
          <h3>Timestamps</h3>
          <div class="detail-grid">
            <div class="detail-item">
              <span class="label">Created:</span>
              <span class="value">{{ brewLog.formattedCreatedAt }}</span>
            </div>
            <div class="detail-item">
              <span class="label">Last Updated:</span>
              <span class="value">{{ brewLog.formattedUpdatedAt }}</span>
            </div>
          </div>
        </div>

        <!-- Actions -->
        <div class="actions">
          <button @click="editBrewLog" class="btn btn-secondary">
            Edit Brew Log
          </button>
          <button @click="deleteBrewLog" class="btn btn-danger">
            Delete Brew Log
          </button>
          <button @click="closeModal" class="btn btn-primary">
            Close
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BrewLogDetail',
  props: {
    brewLog: {
      type: Object,
      required: true
    }
  },
  emits: ['close', 'edit', 'delete'],
  setup(props, { emit }) {
    const closeModal = () => {
      emit('close')
    }

    const editBrewLog = () => {
      // Close the detail modal first
      emit('close')
      // Then emit the edit event
      emit('edit', props.brewLog)
    }

    const deleteBrewLog = () => {
      if (confirm('Are you sure you want to delete this brew log?')) {
        emit('delete', props.brewLog.id)
      }
    }

    return {
      closeModal,
      editBrewLog,
      deleteBrewLog
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 8px;
  max-width: 700px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h2 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6c757d;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #495057;
}

.brew-log-detail {
  padding: 20px;
}

.section {
  margin-bottom: 30px;
}

.section h3 {
  margin: 0 0 15px 0;
  color: #2c3e50;
  font-size: 18px;
  border-bottom: 2px solid #e9ecef;
  padding-bottom: 8px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 15px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.label {
  font-size: 12px;
  color: #6c757d;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.value {
  font-size: 14px;
  color: #495057;
  font-weight: 500;
}

.rating-display {
  display: flex;
  align-items: center;
  gap: 15px;
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  color: #e9ecef;
  font-size: 20px;
}

.star.filled {
  color: #ffc107;
}

.rating-value {
  font-weight: 600;
  color: #495057;
  font-size: 16px;
}

.taste-notes {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 6px;
  border-left: 4px solid #007bff;
}

.taste-notes p {
  margin: 0;
  color: #495057;
  line-height: 1.6;
  font-style: italic;
}

.flavor-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag {
  background: #e9ecef;
  color: #495057;
  padding: 6px 12px;
  border-radius: 16px;
  font-size: 13px;
  font-weight: 500;
}

.actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #e9ecef;
}
@media (max-width: 768px) {
  .modal-content {
    margin: 10px;
    max-height: 95vh;
  }
  
  .detail-grid {
    grid-template-columns: 1fr;
  }
  
  .actions {
    flex-direction: column;
  }
  
  .btn {
    width: 100%;
  }
}
</style>

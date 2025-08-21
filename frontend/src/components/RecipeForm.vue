<template>
  <div class="recipe-form">
    <form @submit.prevent="handleSubmit">
      <!-- Title -->
      <div class="form-group">
        <label for="title">Recipe Title *</label>
        <input 
          id="title" 
          v-model="form.title" 
          type="text" 
          placeholder="e.g., Perfect V60 Pour Over"
          :class="{ error: errors.title }"
          required
        >
        <div v-if="errors.title" class="error-msg">{{ errors.title }}</div>
      </div>

      <!-- Method -->
      <div class="form-group">
        <label for="method">Brewing Method</label>
        <select id="method" v-model="form.method" :class="{ error: errors.method }">
          <option value="">Select a method</option>
          <option value="V60">V60</option>
          <option value="Chemex">Chemex</option>
          <option value="AeroPress">AeroPress</option>
          <option value="French Press">French Press</option>
          <option value="Moka Pot">Moka Pot</option>
          <option value="Espresso">Espresso</option>
          <option value="Pour Over">Pour Over</option>
          <option value="Cold Brew">Cold Brew</option>
          <option value="Other">Other</option>
        </select>
        <div v-if="errors.method" class="error-msg">{{ errors.method }}</div>
      </div>

      <!-- Description -->
      <div class="form-group">
        <label for="description">Description</label>
        <textarea 
          id="description" 
          v-model="form.description" 
          rows="3"
          placeholder="Describe your recipe, tips, or notes..."
          :class="{ error: errors.description }"
        ></textarea>
        <div v-if="errors.description" class="error-msg">{{ errors.description }}</div>
      </div>

      <!-- Visibility -->
      <div class="form-group">
        <label for="visibility">Visibility *</label>
        <select id="visibility" v-model="form.visibility" :class="{ error: errors.visibility }" required>
          <option value="PRIVATE">Private (only you can see)</option>
          <option value="PUBLIC">Public (everyone can see)</option>
        </select>
        <div v-if="errors.visibility" class="error-msg">{{ errors.visibility }}</div>
      </div>

      <!-- Steps -->
      <div class="form-group">
        <label>Recipe Steps *</label>
        <div class="steps-container">
          <div 
            v-for="(step, index) in form.steps" 
            :key="index"
            class="step-item"
          >
            <div class="step-header">
              <h6>Step {{ index + 1 }}</h6>
              <button 
                type="button" 
                @click="removeStep(index)" 
                class="btn btn-outline-danger btn-sm"
                :disabled="form.steps.length <= 1"
              >
                <i class="bi bi-trash"></i>
              </button>
            </div>
            
            <div class="step-content">
              <div class="form-group">
                <label :for="`instruction-${index}`">Instruction *</label>
                <textarea 
                  :id="`instruction-${index}`"
                  v-model="step.instruction" 
                  rows="2"
                  placeholder="Describe what to do in this step..."
                  :class="{ error: errors[`steps.${index}.instruction`] }"
                  required
                ></textarea>
                <div v-if="errors[`steps.${index}.instruction`]" class="error-msg">
                  {{ errors[`steps.${index}.instruction`] }}
                </div>
              </div>
              
              <div class="row">
                <div class="col-md-6">
                  <div class="form-group">
                    <label :for="`time-${index}`">Time (seconds)</label>
                    <input 
                      :id="`time-${index}`"
                      v-model.number="step.time" 
                      type="number" 
                      min="0"
                      placeholder="e.g., 30"
                      :class="{ error: errors[`steps.${index}.time`] }"
                    >
                    <div v-if="errors[`steps.${index}.time`]" class="error-msg">
                      {{ errors[`steps.${index}.time`] }}
                    </div>
                  </div>
                </div>
                <div class="col-md-6">
                  <div class="form-group">
                    <label :for="`water-${index}`">Water Amount (g)</label>
                    <input 
                      :id="`water-${index}`"
                      v-model.number="step.waterAmount" 
                      type="number" 
                      min="0"
                      step="0.1"
                      placeholder="e.g., 50"
                      :class="{ error: errors[`steps.${index}.waterAmount`] }"
                    >
                    <div v-if="errors[`steps.${index}.waterAmount`]" class="error-msg">
                      {{ errors[`steps.${index}.waterAmount`] }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <button 
            type="button" 
            @click="addStep" 
            class="btn btn-outline-primary btn-sm mt-3"
          >
            <i class="bi bi-plus-circle me-2"></i>Add Step
          </button>
        </div>
        <div v-if="errors.steps" class="error-msg">{{ errors.steps }}</div>
      </div>

      <!-- Preview -->
      <div class="form-group" v-if="form.steps.length > 0">
        <label>Preview</label>
        <div class="preview-container">
          <div 
            v-for="(step, index) in form.steps" 
            :key="index"
            class="preview-step"
          >
            <div class="preview-step-header">
              <span class="step-number">{{ index + 1 }}</span>
              <span class="step-instruction">{{ step.instruction || 'No instruction' }}</span>
            </div>
            <div class="preview-step-details">
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

      <!-- Form Actions -->
      <div class="form-actions">
        <button type="submit" :disabled="submitting" class="btn btn-primary">
          <span v-if="submitting" class="spinner-border spinner-border-sm me-2"></span>
          {{ isEditing ? 'Update Recipe' : 'Create Recipe' }}
        </button>
        <button type="button" @click="handleCancel" class="btn btn-secondary ms-2">
          Cancel
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { createRecipe, updateRecipe, getRecipeById } from '../api/recipes.js'

const router = useRouter()
const route = useRoute()

const props = defineProps({
  isEditing: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['saved', 'cancelled'])

const submitting = ref(false)
const errors = reactive({})

const form = reactive({
  title: '',
  method: '',
  description: '',
  visibility: 'PRIVATE',
  steps: [
    {
      instruction: '',
      time: null,
      waterAmount: null
    }
  ]
})

const addStep = () => {
  form.steps.push({
    instruction: '',
    time: null,
    waterAmount: null
  })
}

const removeStep = (index) => {
  if (form.steps.length > 1) {
    form.steps.splice(index, 1)
  }
}

const validateForm = () => {
  const newErrors = {}
  
  if (!form.title.trim()) {
    newErrors.title = 'Title is required'
  }
  
  if (!form.visibility) {
    newErrors.visibility = 'Visibility is required'
  }
  
  if (!form.steps || form.steps.length === 0) {
    newErrors.steps = 'At least one step is required'
  } else {
    form.steps.forEach((step, index) => {
      if (!step.instruction.trim()) {
        newErrors[`steps.${index}.instruction`] = 'Instruction is required'
      }
    })
  }
  
  Object.assign(errors, newErrors)
  return Object.keys(newErrors).length === 0
}

const handleSubmit = async () => {
  if (!validateForm()) {
    return
  }
  
  submitting.value = true
  
  try {
    const recipeData = {
      title: form.title.trim(),
      method: form.method.trim() || null,
      description: form.description.trim() || null,
      visibility: form.visibility,
      steps: form.steps.map(step => ({
        instruction: step.instruction.trim(),
        time: step.time || null,
        waterAmount: step.waterAmount || null
      }))
    }
    
    if (props.isEditing) {
      await updateRecipe(route.params.id, recipeData)
    } else {
      await createRecipe(recipeData)
    }
    
    emit('saved')
  } catch (error) {
    console.error('Error saving recipe:', error)
    if (error.response?.data?.message) {
      alert(error.response.data.message)
    } else {
      alert('Failed to save recipe. Please try again.')
    }
  } finally {
    submitting.value = false
  }
}

const handleCancel = () => {
  emit('cancelled')
}

// Load recipe data if editing
onMounted(async () => {
  if (props.isEditing && route.params.id) {
    try {
      const recipe = await getRecipeById(route.params.id)
      Object.assign(form, {
        title: recipe.title,
        method: recipe.method || '',
        description: recipe.description || '',
        visibility: recipe.visibility,
        steps: recipe.steps || [{ instruction: '', time: null, waterAmount: null }]
      })
    } catch (error) {
      console.error('Error loading recipe:', error)
      alert('Failed to load recipe data.')
    }
  }
})
</script>

<style scoped>
.recipe-form {
  max-width: 800px;
  margin: 0 auto;
}

.form-group {
  margin-bottom: 1.5rem;
}

label {
  font-weight: 500;
  display: block;
  margin-bottom: 0.5rem;
}

input, select, textarea {
  width: 100%;
  padding: 0.75rem;
  border: 1px solid #ced4da;
  border-radius: 0.375rem;
  font-size: 1rem;
  transition: border-color 0.15s ease-in-out;
}

input:focus, select:focus, textarea:focus {
  outline: none;
  border-color: #86b7fe;
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
}

input.error, select.error, textarea.error {
  border-color: #dc3545;
}

.error-msg {
  color: #dc3545;
  font-size: 0.875rem;
  margin-top: 0.25rem;
}

.steps-container {
  border: 1px solid #dee2e6;
  border-radius: 0.375rem;
  padding: 1rem;
  background-color: #f8f9fa;
}

.step-item {
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 0.375rem;
  padding: 1rem;
  margin-bottom: 1rem;
}

.step-item:last-child {
  margin-bottom: 0;
}

.step-header {
  display: flex;
  justify-content: between;
  align-items: center;
  margin-bottom: 1rem;
}

.step-header h6 {
  margin: 0;
  flex: 1;
}

.preview-container {
  border: 1px solid #dee2e6;
  border-radius: 0.375rem;
  padding: 1rem;
  background-color: #f8f9fa;
}

.preview-step {
  background: white;
  border: 1px solid #dee2e6;
  border-radius: 0.375rem;
  padding: 1rem;
  margin-bottom: 0.5rem;
}

.preview-step:last-child {
  margin-bottom: 0;
}

.preview-step-header {
  display: flex;
  align-items: flex-start;
  margin-bottom: 0.5rem;
}

.step-number {
  background: #007bff;
  color: white;
  width: 24px;
  height: 24px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 0.875rem;
  font-weight: 600;
  margin-right: 0.75rem;
  flex-shrink: 0;
}

.step-instruction {
  flex: 1;
  line-height: 1.5;
}

.preview-step-details {
  display: flex;
  gap: 1rem;
  font-size: 0.875rem;
  color: #6c757d;
}

.detail-item {
  display: flex;
  align-items: center;
}

.form-actions {
  text-align: center;
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid #dee2e6;
}
</style>

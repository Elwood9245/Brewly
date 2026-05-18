<template>
  <div class="recipe-form">
    <form @submit.prevent="handleSubmit">
      <!-- Basic Information Card -->
      <div class="card mb-4">
        <div class="card-body p-4">
          <div class="d-flex justify-content-between align-items-start mb-1">
            <h5 class="card-title mb-0">Basic Information</h5>
          </div>
          
          <!-- Title -->
          <div class="form-group">
            <label for="title">Recipe Title *</label>
            <input 
              id="title" 
              v-model="form.title" 
              type="text" 
              class="form-control"
              placeholder="e.g., Perfect V60 Pour Over"
              :class="{ error: errors.title }"
              required
            >
            <div v-if="errors.title" class="error-msg">{{ errors.title }}</div>
          </div>

          <!-- Method -->
          <div class="form-group">
            <label for="method">Brewing Method</label>
            <select id="method" v-model="form.method" class="form-control" :class="{ error: errors.method }">
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
              class="form-control"
              placeholder="Describe your recipe, tips, or notes..."
              :class="{ error: errors.description }"
            ></textarea>
            <div v-if="errors.description" class="error-msg">{{ errors.description }}</div>
          </div>

          <!-- Visibility -->
          <div class="form-group">
            <label for="visibility">Visibility *</label>
            <select id="visibility" v-model="form.visibility" class="form-control" :class="{ error: errors.visibility }" required>
              <option value="PRIVATE">Private (only you can see)</option>
              <option value="PUBLIC">Public (everyone can see)</option>
            </select>
            <div v-if="errors.visibility" class="error-msg">{{ errors.visibility }}</div>
          </div>
        </div>
      </div>

      <!-- Recipe Steps Card -->
      <div class="card mb-4">
        <div class="card-body p-4">
          <div class="d-flex justify-content-between align-items-start mb-1">
            <h5 class="card-title mb-0">Recipe Steps *</h5>
          </div>
          
          <div class="steps-container">
            <div 
              v-for="(step, index) in form.steps" 
              :key="index"
              class="step-item card"
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
                    class="form-control"
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
                        class="form-control"
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
                        class="form-control"
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
      </div>

      <!-- Preview Card -->
      <div class="card mb-4" v-if="form.steps.length > 0">
        <div class="card-body p-4">
          <div class="d-flex justify-content-between align-items-start mb-1">
            <h5 class="card-title mb-0">Preview</h5>
          </div>
          
          <StepDisplay :steps="form.steps" />
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
import StepDisplay from './StepDisplay.vue'
import { useToast } from '../stores/toast.js'

const router = useRouter()
const route = useRoute()
const toast = useToast()

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
    toast.error(error.response?.data?.message || 'Failed to save recipe. Please try again.')
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
      toast.error('Failed to load recipe data.')
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
  color: #2d2d2d;
}

.steps-container {
  margin-top: 1rem;
}

.step-item {
  margin-bottom: 1rem;
  padding: 1.5rem;
}

.step-item:last-child {
  margin-bottom: 0;
}

.step-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1rem;
}

.step-header h6 {
  margin: 0;
  flex: 1;
  color: #2d2d2d;
  font-weight: 600;
}



.form-actions {
  text-align: center;
  margin-top: 2rem;
  padding-top: 1rem;
  border-top: 1px solid rgba(0, 0, 0, 0.1);
}
</style>

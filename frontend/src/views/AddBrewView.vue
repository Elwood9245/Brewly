<template>
  <div class="card px-5 py-4 m-4">
    <h2>{{ isEditing ? 'Edit Brew Log' : 'Add New Brew' }}</h2>
    <div v-if="submitError" class="error-banner">{{ submitError }}</div>
    <div v-if="submitSuccess" class="success-banner">{{ submitSuccess }}</div>
    
    <form @submit.prevent="onSubmit" novalidate>
      <!-- Bean Selection -->
      <div class="form-group">
        <label for="bean-select">Bean<span class="required">*</span></label>
        <select 
          id="bean-select" 
          v-model="form.beanId" 
          required
          @change="onBeanChange"
          :class="{ error: errors.beanId }"
        >
          <option value="">Select a bean</option>
          <option 
            v-for="bean in userBeans" 
            :key="bean.id" 
            :value="bean.id"
          >
            {{ bean.name }}
          </option>
        </select>
        <div v-if="errors.beanId" class="error-msg">{{ errors.beanId }}</div>
      </div>



      <!-- Brewing Method -->
      <div class="form-group">
        <label for="method">Brewing Method</label>
        <select id="method" v-model="form.method" :class="{ error: errors.method }">
          <option value="">Select method</option>
          <option value="V60">V60</option>
          <option value="Chemex">Chemex</option>
          <option value="AeroPress">AeroPress</option>
          <option value="French Press">French Press</option>
          <option value="Espresso">Espresso</option>
          <option value="Moka Pot">Moka Pot</option>
          <option value="Pour Over">Pour Over</option>
          <option value="Kalita Wave">Kalita Wave</option>
          <option value="Clever Dripper">Clever Dripper</option>
          <option value="Other">Other</option>
        </select>
        <div v-if="errors.method" class="error-msg">{{ errors.method }}</div>
      </div>

      <!-- Grind Size -->
      <div class="form-group">
        <label for="grind-size">Grind Size</label>
        <select id="grind-size" v-model="form.grindSize" :class="{ error: errors.grindSize }">
          <option value="">Select grind size</option>
          <option value="Extra Fine">Extra Fine</option>
          <option value="Fine">Fine</option>
          <option value="Medium-Fine">Medium-Fine</option>
          <option value="Medium">Medium</option>
          <option value="Medium-Coarse">Medium-Coarse</option>
          <option value="Coarse">Coarse</option>
          <option value="Extra Coarse">Extra Coarse</option>
        </select>
        <div v-if="errors.grindSize" class="error-msg">{{ errors.grindSize }}</div>
      </div>

      <!-- Weights and Measurements -->
      <div class="form-group">
        <label for="bean-weight">Bean Weight (g)</label>
        <input 
          id="bean-weight" 
          v-model="form.beanWeightGram" 
          type="number" 
          step="0.1" 
          min="0.1" 
          max="1000"
          placeholder="e.g., 18.0"
          :class="{ error: errors.beanWeightGram }"
        >
        <div v-if="errors.beanWeightGram" class="error-msg">{{ errors.beanWeightGram }}</div>
      </div>

      <div class="form-group">
        <label for="water-weight">Water Weight (g)</label>
        <input 
          id="water-weight" 
          v-model="form.waterWeightGram" 
          type="number" 
          step="0.1" 
          min="0.1" 
          max="5000"
          placeholder="e.g., 270.0"
          :class="{ error: errors.waterWeightGram }"
        >
        <div v-if="errors.waterWeightGram" class="error-msg">{{ errors.waterWeightGram }}</div>
      </div>

      <div class="form-group">
        <label for="water-temp">Water Temperature (°C)</label>
        <input 
          id="water-temp" 
          v-model="form.waterTemperature" 
          type="number" 
          step="0.1" 
          min="0"
          max="100"
          placeholder="e.g., 92.0"
          :class="{ error: errors.waterTemperature }"
        >
        <div v-if="errors.waterTemperature" class="error-msg">{{ errors.waterTemperature }}</div>
      </div>

      <div class="form-group">
        <label for="brew-time">Brew Time (seconds)</label>
        <input 
          id="brew-time" 
          v-model="form.brewTimeSeconds" 
          type="number" 
          min="1" 
          max="3600"
          placeholder="e.g., 180"
          :class="{ error: errors.brewTimeSeconds }"
        >
        <div v-if="errors.brewTimeSeconds" class="error-msg">{{ errors.brewTimeSeconds }}</div>
      </div>

      <!-- Rating -->
      <div class="form-group">
        <label for="rating">Rating (0-10)</label>
        <input 
          id="rating" 
          v-model="form.rating" 
          type="number" 
          min="0" 
          max="10" 
          step="0.5"
          placeholder="e.g., 8.5"
          :class="{ error: errors.rating }"
        >
        <div v-if="errors.rating" class="error-msg">{{ errors.rating }}</div>
      </div>

      <!-- Taste Notes -->
      <div class="form-group">
        <label for="taste-notes">Taste Notes</label>
        <textarea 
          id="taste-notes" 
          v-model="form.tasteNotes" 
          rows="4"
          placeholder="Describe the taste, aroma, body, acidity, and overall experience..."
          :class="{ error: errors.tasteNotes }"
        ></textarea>
        <div v-if="errors.tasteNotes" class="error-msg">{{ errors.tasteNotes }}</div>
      </div>

      <!-- Recipe Selection (Optional) -->
      <div class="form-group">
        <label for="recipe-select">Recipe (Optional)</label>
        <select id="recipe-select" v-model="form.recipeId" :class="{ error: errors.recipeId }">
          <option value="">No recipe</option>
          <option 
            v-for="recipe in userRecipes" 
            :key="recipe.id" 
            :value="recipe.id"
          >
            {{ recipe.title }}
          </option>
        </select>
        <div v-if="errors.recipeId" class="error-msg">{{ errors.recipeId }}</div>
      </div>

      <!-- Imported Recipe Information -->
      <div class="form-group">
        <label for="imported-recipe-title">Imported Recipe Title</label>
        <input 
          id="imported-recipe-title" 
          v-model="form.importedRecipeTitle" 
          type="text" 
          placeholder="e.g., James Hoffmann V60 Method"
          :class="{ error: errors.importedRecipeTitle }"
        >
        <div v-if="errors.importedRecipeTitle" class="error-msg">{{ errors.importedRecipeTitle }}</div>
      </div>

      <div class="form-group">
        <label for="imported-recipe-method">Imported Recipe Method</label>
        <input 
          id="imported-recipe-method" 
          v-model="form.importedRecipeMethod" 
          type="text" 
          placeholder="e.g., V60"
          :class="{ error: errors.importedRecipeMethod }"
        >
        <div v-if="errors.importedRecipeMethod" class="error-msg">{{ errors.importedRecipeMethod }}</div>
      </div>

      <div class="form-group">
        <label for="imported-recipe-steps">Imported Recipe Steps (JSON array)</label>
        <textarea 
          id="imported-recipe-steps" 
          v-model="form.importedRecipeStepsText" 
          rows="4"
          placeholder='["Step 1", "Step 2", "Step 3"]'
          @input="updateImportedRecipeSteps"
          :class="{ error: errors.importedRecipeSteps }"
        ></textarea>
        <small class="form-help">Enter steps as a JSON array, e.g., ["Rinse filter", "Add coffee", "Pour water"]</small>
        <div v-if="errors.importedRecipeSteps" class="error-msg">{{ errors.importedRecipeSteps }}</div>
      </div>

      <!-- Form Actions -->
      <div class="form-actions">
        <button type="submit" :disabled="submitting" class="btn btn-primary">
          {{ isEditing ? 'Update Brew Log' : 'Save Brew Log' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { createBrewLog, updateBrewLog, getBrewLogById } from '../api/brewlogs.js'
import { getUserBeans } from '../api/beans.js'

const router = useRouter()
const route = useRoute()

// If editing, brewLogId will be present in route params
const brewLogId = computed(() => route.params.id)
const isEditing = computed(() => !!brewLogId.value)

const form = reactive({
  beanId: '',
  method: '',
  grindSize: '',
  beanWeightGram: null,
  waterWeightGram: null,
  waterTemperature: null,
  brewTimeSeconds: null,
  tasteNotes: '',
  rating: null,
  recipeId: '',
  importedRecipeTitle: '',
  importedRecipeMethod: '',
  importedRecipeSteps: []
})

// Text representation of importedRecipeSteps for the textarea
const formData = reactive({
  importedRecipeStepsText: ''
})

const errors = reactive({
  beanId: '',
  method: '',
  grindSize: '',
  beanWeightGram: '',
  waterWeightGram: '',
  waterTemperature: '',
  brewTimeSeconds: '',
  tasteNotes: '',
  rating: '',
  recipeId: '',
  importedRecipeTitle: '',
  importedRecipeMethod: '',
  importedRecipeSteps: ''
})

const userBeans = ref([])
const userRecipes = ref([])
const submitting = ref(false)
const submitError = ref('')
const submitSuccess = ref('')

function validate() {
  // Clear all errors
  Object.keys(errors).forEach(key => errors[key] = '')

  // Bean ID: required
  if (!form.beanId) {
    errors.beanId = 'Bean selection is required.'
  }



  // Optional field validation
  if (form.method && form.method.length > 50) {
    errors.method = 'Method must be 50 characters or less.'
  }

  if (form.grindSize && form.grindSize.length > 50) {
    errors.grindSize = 'Grind size must be 50 characters or less.'
  }

  if (form.beanWeightGram && (isNaN(Number(form.beanWeightGram)) || Number(form.beanWeightGram) <= 0)) {
    errors.beanWeightGram = 'Bean weight must be a positive number.'
  }

  if (form.waterWeightGram && (isNaN(Number(form.waterWeightGram)) || Number(form.waterWeightGram) <= 0)) {
    errors.waterWeightGram = 'Water weight must be a positive number.'
  }

  if (form.waterTemperature && (isNaN(Number(form.waterTemperature)) || Number(form.waterTemperature) < 0 || Number(form.waterTemperature) > 100)) {
    errors.waterTemperature = 'Water temperature must be between 0 and 100°C.'
  }

  if (form.brewTimeSeconds && (isNaN(Number(form.brewTimeSeconds)) || Number(form.brewTimeSeconds) <= 0)) {
    errors.brewTimeSeconds = 'Brew time must be a positive number.'
  }

  if (form.rating && (isNaN(Number(form.rating)) || Number(form.rating) < 0 || Number(form.rating) > 10)) {
    errors.rating = 'Rating must be between 0 and 10.'
  }

  if (form.tasteNotes && form.tasteNotes.length > 2000) {
    errors.tasteNotes = 'Taste notes must be 2000 characters or less.'
  }

  if (form.importedRecipeTitle && form.importedRecipeTitle.length > 100) {
    errors.importedRecipeTitle = 'Imported recipe title must be 100 characters or less.'
  }

  if (form.importedRecipeMethod && form.importedRecipeMethod.length > 50) {
    errors.importedRecipeMethod = 'Imported recipe method must be 50 characters or less.'
  }

  // Return true if no errors
  return Object.values(errors).every(error => !error)
}

async function onSubmit() {
  submitError.value = ''
  submitSuccess.value = ''
  if (!validate()) return

  submitting.value = true
  try {
    const payload = {
      beanId: form.beanId,
      method: form.method.trim() || null,
      grindSize: form.grindSize.trim() || null,
      beanWeightGram: form.beanWeightGram ? Number(form.beanWeightGram) : null,
      waterWeightGram: form.waterWeightGram ? Number(form.waterWeightGram) : null,
      waterTemperature: form.waterTemperature ? Number(form.waterTemperature) : null,
      brewTimeSeconds: form.brewTimeSeconds ? Number(form.brewTimeSeconds) : null,
      tasteNotes: form.tasteNotes.trim() || null,
      rating: form.rating ? Number(form.rating) : null,
      recipeId: form.recipeId || null,
      importedRecipeTitle: form.importedRecipeTitle.trim() || null,
      importedRecipeMethod: form.importedRecipeMethod.trim() || null,
      importedRecipeSteps: form.importedRecipeSteps
    }

    if (isEditing.value) {
      await updateBrewLog(brewLogId.value, payload)
      submitSuccess.value = 'Brew log updated successfully.'
    } else {
      await createBrewLog(payload)
      submitSuccess.value = 'Brew log created successfully.'
      // Clear form after successful add
      resetForm()
    }

    // Navigate back to brew logs after a short delay
    setTimeout(() => {
      router.push('/brewlogs')
    }, 1500)
  } catch (e) {
    if (e.response?.data?.message) {
      submitError.value = e.response.data.message
    } else if (e.message) {
      submitError.value = e.message
    } else {
      submitError.value = 'Failed to submit. Please try again.'
    }
  } finally {
    submitting.value = false
  }
}

function resetForm() {
  Object.assign(form, {
    beanId: '',
    method: '',
    grindSize: '',
    beanWeightGram: null,
    waterWeightGram: null,
    waterTemperature: null,
    brewTimeSeconds: null,
    tasteNotes: '',
    rating: null,
    recipeId: '',
    importedRecipeTitle: '',
    importedRecipeMethod: '',
    importedRecipeSteps: []
  })
  formData.importedRecipeStepsText = ''
}

const loadUserBeans = async () => {
  try {
    const response = await getUserBeans()
    userBeans.value = response
  } catch (error) {
    console.error('Error loading user beans:', error)
  }
}

const onBeanChange = () => {
  // Bean name is now automatically handled by the backend
  // No need to set form.beanName anymore
}

const updateImportedRecipeSteps = () => {
  try {
    if (formData.importedRecipeStepsText.trim()) {
      form.importedRecipeSteps = JSON.parse(formData.importedRecipeStepsText)
    } else {
      form.importedRecipeSteps = []
    }
  } catch (error) {
    console.error('Invalid JSON for imported recipe steps:', error)
    // Keep the current value if JSON is invalid
  }
}

// If editing, fetch brew log data on mount
onMounted(async () => {
  await loadUserBeans()
  
  if (isEditing.value) {
    try {
      const brewLog = await getBrewLogById(brewLogId.value)
      Object.assign(form, {
        beanId: brewLog.beanId,
        method: brewLog.method || '',
        grindSize: brewLog.grindSize || '',
        beanWeightGram: brewLog.beanWeightGram,
        waterWeightGram: brewLog.waterWeightGram,
        waterTemperature: brewLog.waterTemperature,
        brewTimeSeconds: brewLog.brewTimeSeconds,
        tasteNotes: brewLog.tasteNotes || '',
        rating: brewLog.rating,
        recipeId: brewLog.recipeId || '',
        importedRecipeTitle: brewLog.importedRecipeTitle || '',
        importedRecipeMethod: brewLog.importedRecipeMethod || '',
        importedRecipeSteps: brewLog.importedRecipeSteps || []
      })
      
      // Update the text representation for the textarea
      formData.importedRecipeStepsText = JSON.stringify(brewLog.importedRecipeSteps || [], null, 2)
    } catch (e) {
      submitError.value = 'Failed to load brew log data.'
    }
  }
})
</script>

<style scoped>
h2 {
  text-align: center;
  margin-bottom: 1.5rem;
}
.form-group {
  margin-bottom: 1.25rem;
}
label {
  font-weight: 500;
  display: block;
  margin-bottom: 0.4rem;
}
input[type="text"],
input[type="number"],
input[type="date"],
select,
textarea {
  width: 100%;
  padding: 0.5rem 0.7rem;
  border: 1px solid #ccc;
  border-radius: 20px;
  font-size: 1rem;
  transition: border 0.2s;
  font-family: inherit;
  box-sizing: border-box;
}

textarea {
  resize: vertical;
  min-height: 60px;
}

select {
  background-color: white;
}

.form-actions {
  text-align: center;
  margin-top: 1em;
}

.form-help {
  display: block;
  margin-top: 5px;
  font-size: 12px;
  color: #6c757d;
  font-style: italic;
}
</style>

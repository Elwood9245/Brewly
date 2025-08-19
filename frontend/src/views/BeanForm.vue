<template>
  <div class="card px-5 py-4 m-4">
    <h2>{{ isEdit ? 'Edit Bean' : 'Add New Bean' }}</h2>
    <div v-if="submitError" class="error-banner">{{ submitError }}</div>
    <div v-if="submitSuccess" class="success-banner">{{ submitSuccess }}</div>
    <form @submit.prevent="onSubmit" novalidate>
      <div class="form-group">
        <label for="name">Name<span class="required">*</span></label>
        <input
          id="name"
          v-model="form.name"
          type="text"
          maxlength="100"
          :class="{ error: errors.name }"
        />
        <div v-if="errors.name" class="error-msg">{{ errors.name }}</div>
      </div>

      <div class="form-group">
        <label for="weight">Weight (g)<span class="required">*</span></label>
        <input
          id="weight"
          v-model="form.weight"
          type="number"
          min="1"
          max="10000"
          step="1"
          :class="{ error: errors.weight }"
        />
        <div v-if="errors.weight" class="error-msg">{{ errors.weight }}</div>
      </div>

      <div class="form-group">
        <label for="origin">Origin</label>
        <input
          id="origin"
          v-model="form.origin"
          type="text"
          maxlength="100"
          :class="{ error: errors.origin }"
        />
        <div v-if="errors.origin" class="error-msg">{{ errors.origin }}</div>
      </div>

      <div class="form-group">
        <label for="roaster">Roaster</label>
        <input
          id="roaster"
          v-model="form.roaster"
          type="text"
          maxlength="100"
          :class="{ error: errors.roaster }"
        />
        <div v-if="errors.roaster" class="error-msg">{{ errors.roaster }}</div>
      </div>

      <div class="form-group">
        <label for="blend">Blend Type<span class="required">*</span></label>
        <select
          id="blend"
          v-model="form.blend"
          :class="{ error: errors.blend }"
        >
          <option value="SINGLE_ORIGIN">Single Origin</option>
          <option value="BLEND">Blend</option>
          <option value="UNKNOWN">Unknown</option>
        </select>
        <div v-if="errors.blend" class="error-msg">{{ errors.blend }}</div>
      </div>

      <div class="form-group">
        <label for="roastType">Roast Type<span class="required">*</span></label>
        <select
          id="roastType"
          v-model="form.roastType"
          :class="{ error: errors.roastType }"
        >
          <option value="POUR_OVER">Pour Over</option>
          <option value="ESPRESSO">Espresso</option>
          <option value="OMNI">Omni</option>
        </select>
        <div v-if="errors.roastType" class="error-msg">{{ errors.roastType }}</div>
      </div>

      <div class="form-group">
        <label for="roastedAt">Roast Date<span class="required">*</span></label>
        <input
          id="roastedAt"
          v-model="form.roastedAt"
          type="date"
          :max="today"
          :class="{ error: errors.roastedAt }"
        />
        <div v-if="errors.roastedAt" class="error-msg">{{ errors.roastedAt }}</div>
      </div>

      <div class="form-group">
        <label for="restDays">Rest Days</label>
        <input
          id="restDays"
          v-model="form.restDays"
          type="number"
          min="0"
          max="365"
          :class="{ error: errors.restDays }"
        />
        <div v-if="errors.restDays" class="error-msg">{{ errors.restDays }}</div>
      </div>

      <div class="form-group">
        <label for="flavour">Flavour Profile</label>
        <input
          id="flavour"
          v-model="form.flavour"
          type="text"
          maxlength="200"
          :class="{ error: errors.flavour }"
          placeholder="e.g., fruity, nutty, chocolate"
        />
        <div v-if="errors.flavour" class="error-msg">{{ errors.flavour }}</div>
      </div>

      <div class="form-group">
        <label for="consumption">Consumption (g)</label>
        <input
          id="consumption"
          v-model="form.consumption"
          type="number"
          min="0"
          step="0.1"
          :class="{ error: errors.consumption }"
        />
        <div v-if="errors.consumption" class="error-msg">{{ errors.consumption }}</div>
      </div>

      <div class="form-group">
        <label for="notes">Notes</label>
        <textarea
          id="notes"
          v-model="form.notes"
          rows="3"
          :class="{ error: errors.notes }"
          placeholder="Additional notes about this bean..."
        ></textarea>
        <div v-if="errors.notes" class="error-msg">{{ errors.notes }}</div>
      </div>

      <div class="form-group">
        <label>
          <input
            v-model="form.isActive"
            type="checkbox"
          />
          Active (visible in inventory)
        </label>
      </div>

      <div class="form-actions">
        <button type="submit" :disabled="submitting" class="btn btn-primary">
          {{ isEdit ? 'Update Bean' : 'Add Bean' }}
        </button>
      </div>
    </form>
  </div>
</template>

<script setup>
import { ref, reactive, computed, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { createBean, updateBean, getBeanById, beanUtils, BLEND_TYPES, ROAST_LEVELS } from '../api/beans.js'
import { useAuthStore } from '../stores/auth.js'

const route = useRoute()

// If editing, beanId will be present in route params
const beanId = computed(() => route.params.id)
const isEdit = computed(() => !!beanId.value)

const auth = useAuthStore()

const form = reactive({
  name: '',
  origin: '',
  blend: 'SINGLE_ORIGIN',
  roaster: '',
  roastType: 'POUR_OVER',
  roastedAt: '',
  restDays: null,
  flavour: '',
  weight: '',
  consumption: '',
  notes: '',
  isActive: true
})

const errors = reactive({
  name: '',
  origin: '',
  blend: '',
  roaster: '',
  roastType: '',
  roastedAt: '',
  restDays: '',
  flavour: '',
  weight: '',
  consumption: '',
  notes: ''
})

const submitting = ref(false)
const submitError = ref('')
const submitSuccess = ref('')

// For roast date validation
const today = new Date().toISOString().split('T')[0]
const minDate = '1970-01-01'

function validate() {
  // Clear all errors
  Object.keys(errors).forEach(key => errors[key] = '')

  // Name: required, ≤100 chars
  if (!form.name || !form.name.trim()) {
    errors.name = 'Name is required.'
  } else if (form.name.length > 100) {
    errors.name = 'Name must be 100 characters or less.'
  }

  // Weight: required, positive, ≤10,000g
  if (!form.weight || isNaN(Number(form.weight))) {
    errors.weight = 'Weight is required.'
  } else if (Number(form.weight) <= 0) {
    errors.weight = 'Weight must be positive.'
  } else if (Number(form.weight) > 10000) {
    errors.weight = 'Weight must be 10,000g or less.'
  }

  // Roasted at: required, not in future, not before 1970-01-01
  if (!form.roastedAt) {
    errors.roastedAt = 'Roast date is required.'
  } else if (form.roastedAt > today) {
    errors.roastedAt = 'Roast date cannot be in the future.'
  } else if (form.roastedAt < minDate) {
    errors.roastedAt = 'Roast date cannot be before 1970-01-01.'
  }

  // Optional field validation
  if (form.origin && form.origin.length > 100) {
    errors.origin = 'Origin must be 100 characters or less.'
  }

  if (form.roaster && form.roaster.length > 100) {
    errors.roaster = 'Roaster must be 100 characters or less.'
  }

  if (form.flavour && form.flavour.length > 200) {
    errors.flavour = 'Flavour profile must be 200 characters or less.'
  }

  if (form.restDays && (isNaN(Number(form.restDays)) || Number(form.restDays) < 0 || Number(form.restDays) > 365)) {
    errors.restDays = 'Rest days must be between 0 and 365.'
  }

  if (form.consumption && (isNaN(Number(form.consumption)) || Number(form.consumption) < 0)) {
    errors.consumption = 'Consumption must be a positive number.'
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
      userId: auth.currentUser.value?.id || null,
      name: form.name.trim(),
      origin: form.origin.trim() || null,
      blend: form.blend,
      roaster: form.roaster.trim() || null,
      roastType: form.roastType,
      roastedAt: beanUtils.dateToISO(form.roastedAt),
      restDays: form.restDays ? Number(form.restDays) : null,
      flavour: form.flavour.trim() || null,
      weight: Number(form.weight),
      consumption: form.consumption ? Number(form.consumption) : 0,
      notes: form.notes.trim() || null,
      isActive: form.isActive
    }

    if (isEdit.value) {
      await updateBean(beanId.value, payload)
      submitSuccess.value = 'Bean updated successfully.'
    } else {
      await createBean(payload)
      submitSuccess.value = 'Bean added successfully.'
      // Clear form after successful add
      resetForm()
    }

    // Notify inventory to refresh
    if (typeof window !== 'undefined') {
      window.dispatchEvent(new Event('bean-changed'))
    }
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
    name: '',
    origin: '',
    blend: 'SINGLE_ORIGIN',
    roaster: '',
    roastType: 'POUR_OVER',
    roastedAt: '',
    restDays: null,
    flavour: '',
    weight: '',
    consumption: '',
    notes: '',
    isActive: true
  })
}

// If editing, fetch bean data on mount
onMounted(async () => {
  if (isEdit.value) {
    try {
      const bean = await getBeanById(beanId.value)
      form.name = bean.name || ''
      form.origin = bean.origin || ''
      form.blend = bean.blend || 'SINGLE_ORIGIN'
      form.roaster = bean.roaster || ''
      form.roastType = bean.roastType || 'POUR_OVER'
      form.roastedAt = beanUtils.isoToDateString(bean.roastedAt) || ''
      form.restDays = bean.restDays != null ? String(bean.restDays) : ''
      form.flavour = bean.flavour || ''
      form.weight = bean.weight != null ? String(bean.weight) : ''
      form.consumption = bean.consumption != null ? String(bean.consumption) : ''
      form.notes = bean.notes || ''
      form.isActive = bean.isActive !== false
    } catch (e) {
      submitError.value = 'Failed to load bean data.'
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

label input[type="checkbox"] {
  width: auto;
  margin-right: 0.5rem;
  transform: scale(1.1);
}
.form-actions {
  text-align: center;
  margin-top: 1em;
}
</style>
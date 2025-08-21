<template>
  <div class="container-fluid py-4">
    <!-- Header Section -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="h3 mb-0">{{ isEditing ? 'Edit Recipe' : 'Create New Recipe' }}</h1>
          </div>
          <div class="d-flex gap-2">
            <router-link 
              :to="isEditing ? `/recipes/${route.params.id}` : '/recipes'" 
              class="btn btn-secondary"
            >
              <i class="bi bi-arrow-left me-2"></i>Back
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Recipe Form -->
    <div class="row">
      <div class="col-12">
        <RecipeForm 
          :is-editing="isEditing"
          @saved="handleSaved"
          @cancelled="handleCancelled"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import RecipeForm from '../components/RecipeForm.vue'

const route = useRoute()
const router = useRouter()

const isEditing = computed(() => !!route.params.id)

const handleSaved = () => {
  if (isEditing.value) {
    router.push(`/recipes/${route.params.id}`)
  } else {
    router.push('/recipes')
  }
}

const handleCancelled = () => {
  if (isEditing.value) {
    router.push(`/recipes/${route.params.id}`)
  } else {
    router.push('/recipes')
  }
}
</script>

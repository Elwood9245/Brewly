<template>
  <div class="container-fluid py-4">
    <!-- Header Section -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="h3 mb-0">{{ isEditing ? 'Edit Recipe' : 'Create New Recipe' }}</h1>
            <p class="text-secondary mb-0">
              {{ isEditing ? 'Update your recipe details and steps' : 'Share your coffee brewing expertise with the community' }}
            </p>
          </div>
          <div class="d-flex gap-2">
            <router-link to="/recipes" class="btn btn-outline-secondary">
              <i class="bi bi-arrow-left me-2"></i>Back to Recipes
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Recipe Form -->
    <div class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-body">
            <RecipeForm 
              :is-editing="isEditing"
              @saved="handleSaved"
              @cancelled="handleCancelled"
            />
          </div>
        </div>
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
  router.push('/recipes')
}
</script>

<style scoped>
.card {
  border: none;
  box-shadow: 0 0.125rem 0.25rem rgba(0, 0, 0, 0.075);
}
</style>

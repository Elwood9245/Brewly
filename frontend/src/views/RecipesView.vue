<template>
  <div class="container-fluid py-4">
    <!-- Header Section -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="h3 mb-0">Recipes</h1>
          </div>
          <div class="d-flex gap-2">
            <router-link v-if="activeTab === 'my'" to="/recipes/create" class="btn btn-primary">
              <i class="bi bi-plus-circle me-2"></i>Create Recipe
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Tab Navigation -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="nav-container">
          <div class="nav-tabs">
            <button 
              :class="['nav-tab', { active: activeTab === 'my' }]"
              @click="switchTab('my')"
            >
              <i class="bi bi-journal-text me-2"></i>
              <span>My Recipes</span>
              <span>{{ myRecipesCount }}</span>
            </button>
            <button 
              :class="['nav-tab', { active: activeTab === 'bookmarked' }]"
              @click="switchTab('bookmarked')"
            >
              <i class="bi bi-bookmark-star me-2"></i>
              <span>Bookmarked</span>
              <span>{{ bookmarkedRecipesCount }}</span>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- Error Banner -->
    <div v-if="error" class="row mb-4">
      <div class="col-12">
        <div class="error-banner">{{ error }}</div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="row">
      <div class="col-12 text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-3 text-secondary">Loading recipes...</p>
      </div>
    </div>

    <!-- Empty State -->
    <div v-else-if="!recipes.length" class="row">
      <div class="col-12">
        <div class="card">
          <div class="card-body text-center py-5">
            <i :class="activeTab === 'my' ? 'bi bi-journal-bookmark' : 'bi bi-bookmark-star'" class="fs-1 text-secondary mb-3"></i>
            <h5 class="card-title mb-1">{{ activeTab === 'my' ? 'No recipes yet' : 'No bookmarked recipes' }}</h5>
            <p class="card-text text-secondary">
              {{ activeTab === 'my' 
                ? 'Start sharing your coffee brewing expertise by creating your first recipe.' 
                : 'Bookmark recipes from the explore page to save them for later.' 
              }}
            </p>
            <router-link v-if="activeTab === 'my'" to="/recipes/create" class="btn btn-primary">
              <i class="bi bi-plus-circle me-2"></i>Create Your First Recipe
            </router-link>
            <router-link v-else to="/explore" class="btn btn-primary">
              <i class="bi bi-search me-2"></i>Explore Recipes
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Recipes Grid -->
    <div v-else class="row g-4">
      <div 
        v-for="recipe in recipes" 
        :key="recipe.id"
        class="col-md-6 col-lg-4"
      >
        <RecipeCard 
          :recipe="recipe"
          :show-actions="true"
          :show-like-button="false"
          @delete="handleDeleteRecipe"
        />
      </div>
    </div>

    <!-- Pagination -->
    <div v-if="totalPages > 1" class="row mt-4">
      <div class="col-12">
        <nav aria-label="Recipe pagination">
          <ul class="pagination justify-content-center">
            <li class="page-item" :class="{ disabled: currentPage === 0 }">
              <button 
                class="page-link" 
                @click="changePage(currentPage - 1)"
                :disabled="currentPage === 0"
              >
                Previous
              </button>
            </li>
            
            <li 
              v-for="page in visiblePages" 
              :key="page"
              class="page-item"
              :class="{ active: page === currentPage }"
            >
              <button 
                class="page-link" 
                @click="changePage(page)"
              >
                {{ page + 1 }}
              </button>
            </li>
            
            <li class="page-item" :class="{ disabled: currentPage === totalPages - 1 }">
              <button 
                class="page-link" 
                @click="changePage(currentPage + 1)"
                :disabled="currentPage === totalPages - 1"
              >
                Next
              </button>
            </li>
          </ul>
        </nav>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import RecipeCard from '../components/RecipeCard.vue'
import { getMyRecipes, getBookmarkedRecipes, deleteRecipe, unbookmarkRecipe } from '../api/recipes.js'

const router = useRouter()

const recipes = ref([])
const loading = ref(false)
const error = ref(null)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = 12
const activeTab = ref('my')

// Count states for tabs
const myRecipesCount = ref(0)
const bookmarkedRecipesCount = ref(0)

const visiblePages = computed(() => {
  const pages = []
  const start = Math.max(0, currentPage.value - 2)
  const end = Math.min(totalPages.value - 1, currentPage.value + 2)
  
  for (let i = start; i <= end; i++) {
    pages.push(i)
  }
  
  return pages
})

const loadRecipes = async (page = 0) => {
  loading.value = true
  error.value = null

  try {
    let response
    if (activeTab.value === 'my') {
      response = await getMyRecipes(page, pageSize)
    } else {
      response = await getBookmarkedRecipes(page, pageSize)
    }
    
    console.log('API Response:', response) // 添加调试日志
    
    recipes.value = response.content || []
    totalPages.value = response.totalPages || 0
    totalElements.value = response.totalElements || 0
    currentPage.value = page
  } catch (err) {
    console.error('Error loading recipes:', err)
    error.value = 'Failed to load recipes. Please try again.'
  } finally {
    loading.value = false
  }
}

const switchTab = (tab) => {
  activeTab.value = tab
  currentPage.value = 0 // Reset to first page when switching tabs
  loadRecipes(0)
}

const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    loadRecipes(page)
  }
}

const handleDeleteRecipe = async (recipeId) => {
  try {
    if (activeTab.value === 'bookmarked') {
      await unbookmarkRecipe(recipeId)
    } else {
      await deleteRecipe(recipeId)
    }
    // Reload current page to refresh the list
    await loadRecipes(currentPage.value)
    // Refresh counts after deletion
    await loadRecipeCounts()
  } catch (err) {
    console.error('Error deleting recipe:', err)
    alert('Failed to delete recipe. Please try again.')
  }
}

// Load recipe counts for tabs
const loadRecipeCounts = async () => {
  try {
    // Get My Recipes count
    const myRecipesResponse = await getMyRecipes(0, 1)
    myRecipesCount.value = myRecipesResponse.totalElements || 0
    
    // Get Bookmarked Recipes count
    const bookmarkedResponse = await getBookmarkedRecipes(0, 1)
    bookmarkedRecipesCount.value = bookmarkedResponse.totalElements || 0
  } catch (err) {
    console.error('Error loading recipe counts:', err)
  }
}

onMounted(async () => {
  await loadRecipeCounts()
  await loadRecipes()
})
</script>

<style scoped>
.error-banner {
  background-color: #f8d7da;
  color: #721c24;
  padding: 1rem;
  border: 1px solid #f5c6cb;
  border-radius: 0.75rem;
  margin-bottom: 1rem;
}

.pagination {
  margin-bottom: 0;
}

.page-link {
  color: #007bff;
  border: 1px solid #dee2e6;
}

.page-link:hover {
  color: #0056b3;
  background-color: #e9ecef;
  border-color: #dee2e6;
}

.page-item.active .page-link {
  background-color: #007bff;
  border-color: #007bff;
}

.page-item.disabled .page-link {
  color: #6c757d;
  pointer-events: none;
  background-color: #fff;
  border-color: #dee2e6;
}
</style>

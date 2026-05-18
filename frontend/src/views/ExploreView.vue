<template>
  <div class="container-fluid py-4">
    <!-- Header Section -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="h3 mb-0">Explore Recipes</h1>
          </div>
          <div class="d-flex gap-2">
            <router-link to="/recipes/create" class="btn btn-primary">
              <i class="bi bi-plus-circle me-2"></i>Create Recipe
            </router-link>
          </div>
        </div>
      </div>
    </div>

    <!-- Search and Filter Section -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="card">
          <div class="card-body">
            <div class="row g-3">
              <div class="col-md-6">
                <div class="input-group">
                  <input 
                    v-model="searchKeyword"
                    type="text" 
                    class="form-control" 
                    placeholder="Search recipes..."
                    @keyup.enter="searchRecipes"
                  >
                  <button 
                    @click="searchRecipes"
                    class="btn btn-outline-secondary" 
                    type="button"
                  >
                    <i class="bi bi-search"></i>
                  </button>
                </div>
              </div>
              <div class="col-md-3">
                <select v-model="selectedMethod" class="form-select" @change="filterByMethod">
                  <option value="">All Methods</option>
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
              </div>
              <div class="col-md-3">
                <select v-model="sortBy" class="form-select" @change="loadRecipes">
                  <option value="latest">Latest</option>
                  <option value="popular">Most Popular</option>
                </select>
              </div>
            </div>
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
            <i class="bi bi-search fs-1 text-secondary mb-3"></i>
            <h5 class="card-title mb-1">No recipes found</h5>
            <p class="card-text text-secondary">
              {{ searchKeyword || selectedMethod ? 'Try adjusting your search criteria.' : 'Be the first to share a recipe!' }}
            </p>
            <router-link to="/recipes/create" class="btn btn-primary">
              <i class="bi bi-plus-circle me-2"></i>Create Recipe
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
          :show-actions="false"
          :show-like-button="true"
          :show-bookmark-button="true"
          :is-bookmarked="bookmarkStatuses.get(recipe.id)"
          @like="handleLike"
          @bookmark="handleBookmark"
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
import RecipeCard from '../components/RecipeCard.vue'
import {
  getPublicRecipes,
  searchPublicRecipes,
  getPublicRecipesByMethod,
  likeRecipe,
  unlikeRecipe,
  bookmarkRecipe,
  getBookmarkStatus
} from '../api/recipes.js'
import { useToast } from '../stores/toast.js'

const toast = useToast()

const recipes = ref([])
const loading = ref(false)
const error = ref(null)
const currentPage = ref(0)
const totalPages = ref(0)
const totalElements = ref(0)
const pageSize = 12

const searchKeyword = ref('')
const selectedMethod = ref('')
const sortBy = ref('latest')
const bookmarkStatuses = ref(new Map())

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
    if (searchKeyword.value.trim()) {
      response = await searchPublicRecipes(searchKeyword.value.trim(), page, pageSize)
    } else if (selectedMethod.value) {
      response = await getPublicRecipesByMethod(selectedMethod.value, page, pageSize)
    } else {
      response = await getPublicRecipes(page, pageSize, sortBy.value)
    }
    
    recipes.value = response.content || []
    totalPages.value = response.totalPages || 0
    totalElements.value = response.totalElements || 0
    currentPage.value = page
    
    // Load bookmark statuses for all recipes
    await loadBookmarkStatuses()
  } catch (err) {
    console.error('Error loading recipes:', err)
    error.value = 'Failed to load recipes. Please try again.'
  } finally {
    loading.value = false
  }
}

const searchRecipes = () => {
  currentPage.value = 0
  loadRecipes(0)
}

const filterByMethod = () => {
  currentPage.value = 0
  loadRecipes(0)
}

const changePage = (page) => {
  if (page >= 0 && page < totalPages.value) {
    loadRecipes(page)
  }
}

const handleLike = async (recipeId) => {
  try {
    const recipe = recipes.value.find(r => r.id === recipeId)
    if (recipe) {
      if (recipe.isLikedByCurrentUser) {
        await unlikeRecipe(recipeId)
        recipe.isLikedByCurrentUser = false
        recipe.likeCount = Math.max(0, (recipe.likeCount || 0) - 1)
      } else {
        await likeRecipe(recipeId)
        recipe.isLikedByCurrentUser = true
        recipe.likeCount = (recipe.likeCount || 0) + 1
      }
    }
  } catch (err) {
    console.error('Error toggling like:', err)
    toast.error('Failed to update like. Please try again.')
  }
}

const handleBookmark = async (recipeId) => {
  try {
    const isCurrentlyBookmarked = bookmarkStatuses.value.get(recipeId)

    if (isCurrentlyBookmarked) {
      toast.info('This recipe is already in your bookmarks.')
    } else {
      await bookmarkRecipe(recipeId)
      bookmarkStatuses.value.set(recipeId, true)
      toast.success('Recipe bookmarked! Find it in My Recipes > Bookmarked.')
    }
  } catch (err) {
    console.error('Error bookmarking recipe:', err)
    toast.error('Failed to bookmark recipe. Please try again.')
  }
}

const loadBookmarkStatuses = async () => {
  try {
    const results = await Promise.allSettled(
      recipes.value.map(recipe => getBookmarkStatus(recipe.id))
    )
    results.forEach((result, index) => {
      if (result.status === 'fulfilled') {
        bookmarkStatuses.value.set(recipes.value[index].id, result.value)
      }
    })
  } catch (err) {
    console.error('Error loading bookmark statuses:', err)
  }
}

onMounted(() => {
  loadRecipes()
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

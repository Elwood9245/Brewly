<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth.js'
import { getBrewLogStatistics } from '../api/brewlogs.js'
import { getUserBrewLogs } from '../api/brewlogs.js'

const router = useRouter()
const auth = useAuthStore()

// Empty data arrays - will be populated with real data from API
const brewLogs = ref([])
const recentRecipes = ref([])

const stats = ref({
  totalBrews: 0,
  favoriteMethod: null,
  averageRating: 0,
  streakDays: 0
})

const loading = ref(false)

const loadStatistics = async () => {
  loading.value = true
  try {
    const response = await getBrewLogStatistics()
    if (response) {
      stats.value = {
        totalBrews: response.totalBrewLogs || 0,
        favoriteMethod: response.methodStatistics && response.methodStatistics.length > 0 
          ? response.methodStatistics[0].method 
          : null,
        averageRating: response.averageRating || 0,
        streakDays: response.totalBrewsThisWeek || 0
      }
    }
  } catch (err) {
    console.error('Error loading statistics:', err)
  } finally {
    loading.value = false
  }
}

const loadRecentBrews = async () => {
  try {
    const response = await getUserBrewLogs(0, 3) // Get first 3 brews
    if (response && response.brewLogs) {
      brewLogs.value = response.brewLogs
    }
  } catch (err) {
    console.error('Error loading recent brews:', err)
  }
}

const formatDate = (dateString) => {
  if (!dateString) return ''
  const date = new Date(dateString)
  return date.toLocaleDateString('en-US', { 
    month: 'short', 
    day: 'numeric' 
  })
}

onMounted(() => {
  loadStatistics()
  loadRecentBrews()
})

const navigateToAddBrew = () => {
  router.push('/addbrew')
}

const likeRecipe = (recipeId) => {
  const recipe = recentRecipes.value.find(r => r.id === recipeId)
  if (recipe) {
    recipe.likes++
  }
}

const navigateToMyRecipes = () => {
  router.push('/recipes')
}

const navigateToExplore = () => {
  router.push('/explore')
}
</script>

<template>
  <div class="home-container">
    <!-- Welcome Section -->
    <div class="welcome-section mb-4">
      <div class="row align-items-center">
        <div class="col">
          <h1 class="h3 mb-1">Welcome back, {{ auth.currentUser.value?.username || 'Coffee Lover' }}!</h1>

        </div>
        <div class="col-auto">
          <button class="btn btn-primary" @click="navigateToAddBrew">
            <i class="bi bi-plus-circle me-1"></i>New Brew
          </button>
        </div>
      </div>
    </div>

    <!-- Stats Cards -->
    <div class="row mb-4">
      <div class="col-6 col-md-3 mb-3">
        <div class="card text-center">
          <div class="card-body p-2">
            <div class="mb-2">
              <i class="bi bi-graph-up fs-1 text-secondary"></i>
            </div>
            <div class="h5 mb-1 fw-bold">{{ stats.totalBrews }}</div>
            <div class="small text-secondary">Total Brews</div>
          </div>
        </div>
      </div>
      <div class="col-6 col-md-3 mb-3">
        <div class="card text-center">
          <div class="card-body p-2">
            <div class="mb-2">
              <i class="bi bi-star fs-1 text-secondary"></i>
            </div>
            <div class="h5 mb-1 fw-bold">{{ stats.averageRating.toFixed(1) }}</div>
            <div class="small text-secondary">Avg Rating</div>
          </div>
        </div>
      </div>
      <div class="col-6 col-md-3 mb-3">
        <div class="card text-center">
          <div class="card-body p-2">
            <div class="mb-2">
              <i class="bi bi-calendar-week fs-1 text-secondary"></i>
            </div>
            <div class="h5 mb-1 fw-bold">{{ stats.streakDays }}</div>
            <div class="small text-secondary">This Week</div>
          </div>
        </div>
      </div>
      <div class="col-6 col-md-3 mb-3">
        <div class="card text-center">
          <div class="card-body p-2">
            <div class="mb-2">
              <i class="bi bi-cup-hot fs-1 text-secondary"></i>
            </div>
            <div class="h5 mb-1 fw-bold">{{ stats.favoriteMethod || 'None' }}</div>
            <div class="small text-secondary">Top Method</div>
          </div>
        </div>
      </div>
    </div>

    <!-- Recent Brews -->
    <div class="section mb-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="h5 mb-0">Recent Brews</h2>
        <router-link to="/brewlogs" class="text-decoration-none small" v-if="brewLogs.length > 0">View All</router-link>
      </div>
      
      <div class="card" v-if="brewLogs.length > 0">
        <div class="card-body">
          <div 
            v-for="(brew, index) in brewLogs" 
            :key="brew.id"
            class="d-flex justify-content-between align-items-center mb-3"
            :class="{ 'mb-0': index === brewLogs.length - 1 }"
          >
            <div>
              <div class="fw-bold">{{ brew.beanName }}</div>
              <div class="text-secondary small">
                {{ brew.method }}<span v-if="brew.rating"> · {{ brew.rating }}/10</span> · {{ formatDate(brew.createdAt) }}
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Empty state for brews -->
      <div v-else class="card">
        <div class="card-body text-center py-5">
          <i class="bi bi-cup-hot fs-1 mb-3 text-secondary"></i>
          <h5 class="mb-2">No brews yet</h5>
          <p class="mb-3 text-secondary">Start your coffee journey by logging your first brew!</p>
          <button class="btn btn-primary" @click="navigateToAddBrew">
            <i class="bi bi-plus-circle me-1"></i>Add Your First Brew
          </button>
        </div>
      </div>
    </div>

    <!-- Popular Recipes -->
    <div class="section mb-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="h5 mb-0">Popular Recipes</h2>
        <div class="d-flex gap-2" v-if="recentRecipes.length > 0">
          <router-link to="/explore" class="text-decoration-none small">Explore More</router-link>
          <span class="text-muted">|</span>
          <router-link to="/recipes" class="text-decoration-none small">My Recipes</router-link>
        </div>
      </div>
      
      <div class="row" v-if="recentRecipes.length > 0">
        <div 
          v-for="recipe in recentRecipes" 
          :key="recipe.id"
          class="col-md-6 mb-3"
        >
          <div class="card recipe-card h-100"> 
            <div class="recipe-image" :style="`background-image: url(${recipe.image})`"></div>
            <div class="card-body p-3">
              <div class="d-flex justify-content-between align-items-start mb-1">
                <h5 class="card-title mb-0">{{ recipe.name }}</h5>
              </div>
              <p class="small mb-2">by {{ recipe.author }}</p>
              <div class="d-flex justify-content-between align-items-center">
                <button 
                  class="btn btn-outline-danger btn-sm"
                  @click="likeRecipe(recipe.id)"
                >
                  <i class="bi bi-heart me-1"></i>{{ recipe.likes }}
                </button>
                <a href="#" class="btn btn-primary btn-sm">Try It</a>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Empty state for recipes -->
      <div v-else class="card">
        <div class="card-body text-center py-5">
          <i class="bi bi-journal-bookmark fs-1 mb-3"></i>
          <h5 class="mb-1">No recipes to explore yet</h5>
          <p class="mb-3">Discover amazing coffee recipes from the community!</p>
          <div class="d-flex gap-2 justify-content-center">
            <button class="btn btn-outline-primary" @click="navigateToExplore">
              <i class="bi bi-search me-1"></i>Explore Recipes
            </button>
            <button class="btn btn-primary" @click="navigateToMyRecipes">
              <i class="bi bi-journal-text me-1"></i>My Recipes
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.home-container {
  padding: 1rem 0;
}

.welcome-section {
  background: rgba(255, 255, 255, 0.8);
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 30px;
  padding: 1.5rem;
  margin-bottom: 1.5rem;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
}

.welcome-section h1 {
  color: #2d2d2d;
  font-weight: 700;
}

.recipe-card {
  border: none;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.recipe-card:hover {
  transform: translateY(-2px);
}

.recipe-image {
  height: 100px;
  background-size: cover;
  background-position: center;
  background-color: #f8f9fa;
}

.rating {
  display: flex;
  align-items: center;
  gap: 0.25rem;
}

.section {
  margin-bottom: 2rem;
}

.section h2, .section h5 {
  color: #2d2d2d;
  font-weight: 600;
}

.section a {
  color: #495057;
  font-weight: 500;
}

.section a:hover {
  color: #2d2d2d;
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .home-container {
    padding: 0.5rem 0;
  }
  
  .welcome-section {
    padding: 1rem;
  }
  
  .stat-number {
    font-size: 1.25rem;
  }
  
  .recipe-image {
    height: 100px;
  }
}
</style>

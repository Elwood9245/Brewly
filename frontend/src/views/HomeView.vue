<script setup>
import { ref, onMounted } from 'vue'
import { useAuthStore } from '../stores/auth.js'

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

onMounted(() => {
  // In a real app, fetch user data here
  console.log('Home view mounted')
})

const navigateToAddBrew = () => {
  // TODO: Navigate to add brew page
  console.log('Navigate to add brew')
}

const likeRecipe = (recipeId) => {
  const recipe = recentRecipes.value.find(r => r.id === recipeId)
  if (recipe) {
    recipe.likes++
  }
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
        <div class="card stat-card text-center">
          <div class="card-body p-3">
            <div class="stat-number text-primary">{{ stats.totalBrews }}</div>
            <div class="stat-label">Total Brews</div>
          </div>
        </div>
      </div>
      <div class="col-6 col-md-3 mb-3">
        <div class="card stat-card text-center">
          <div class="card-body p-3">
            <div class="stat-number text-success">{{ stats.averageRating }}</div>
            <div class="stat-label">Avg Rating</div>
          </div>
        </div>
      </div>
      <div class="col-6 col-md-3 mb-3">
        <div class="card stat-card text-center">
          <div class="card-body p-3">
            <div class="stat-number text-warning">{{ stats.streakDays }}</div>
            <div class="stat-label">Day Streak</div>
          </div>
        </div>
      </div>
              <div class="col-6 col-md-3 mb-3">
          <div class="card stat-card text-center">
            <div class="card-body p-3">
              <div class="stat-icon text-info">
                <i class="bi bi-cup-hot fs-4"></i>
              </div>
              <div class="stat-label small">{{ stats.favoriteMethod || 'No preference yet' }}</div>
            </div>
          </div>
        </div>
    </div>

    <!-- Recent Brews -->
    <div class="section mb-4">
      <div class="d-flex justify-content-between align-items-center mb-3">
        <h2 class="h5 mb-0">Recent Brews</h2>
        <a href="#" class="text-decoration-none small" v-if="brewLogs.length > 0">View All</a>
      </div>
      
      <div class="brew-list" v-if="brewLogs.length > 0">
        <div 
          v-for="brew in brewLogs" 
          :key="brew.id"
          class="card mb-3 brew-card"
        >
          <div class="card-body p-3">
            <div class="row align-items-center">
              <div class="col">
                <h6 class="mb-1">{{ brew.method }}</h6>
                <p class="mb-1 small">{{ brew.bean }}</p>
                <p class="mb-0 small">{{ brew.notes }}</p>
              </div>
              <div class="col-auto text-end">
                <div class="rating mb-1">
                  <span class="text-warning">
                    <i class="bi bi-star-fill"></i>
                  </span>
                  <span class="small">{{ brew.rating }}</span>
                </div>
                <div class="small">{{ brew.time }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <!-- state for brews -->
      <div v-else class="card">
        <div class="card-body text-center py-5">
          <i class="bi bi-cup-hot fs-1 mb-3"></i>
          <h5 class="mb-2">No brews yet</h5>
          <p class="mb-3">Start your coffee journey by logging your first brew!</p>
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
        <a href="#" class="text-decoration-none small" v-if="recentRecipes.length > 0">Explore More</a>
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
              <h6 class="card-title mb-1">{{ recipe.name }}</h6>
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
          <h5 class="mb-2">No recipes to explore yet</h5>
          <p class="mb-3">Discover amazing coffee recipes from the community!</p>
          <button class="btn btn-outline-primary">
            <i class="bi bi-search me-1"></i>Explore Recipes
          </button>
        </div>
      </div>
    </div>

    <!-- Quick Actions -->
    <div class="section">
      <h2 class="h5 mb-3">Quick Actions</h2>
      <div class="row">
        <div class="col-6 col-md-3 mb-2">
          <button class="btn btn-outline-primary w-100 quick-action-btn">
            <i class="bi bi-plus-circle mb-1 d-block"></i>
            <small>Add Brew</small>
          </button>
        </div>
        <div class="col-6 col-md-3 mb-2">
          <button class="btn btn-outline-success w-100 quick-action-btn">
            <i class="bi bi-journal-plus mb-1 d-block"></i>
            <small>New Recipe</small>
          </button>
        </div>
        <div class="col-6 col-md-3 mb-2">
          <button class="btn btn-outline-info w-100 quick-action-btn">
            <i class="bi bi-search mb-1 d-block"></i>
            <small>Explore</small>
          </button>
        </div>
        <div class="col-6 col-md-3 mb-2">
          <button class="btn btn-outline-warning w-100 quick-action-btn">
            <i class="bi bi-boxes mb-1 d-block"></i>
            <small>Inventory</small>
          </button>
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



.stat-card {
  border: none;
  border-radius: 15px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  transition: transform 0.2s ease;
}

.stat-card:hover {
  transform: translateY(-2px);
}

.stat-number {
  font-size: 1.5rem;
  font-weight: bold;
  line-height: 1;
  color: #2d2d2d;
}

.stat-label {
  font-size: 0.75rem;
  color: #495057;
  margin-top: 0.25rem;
}

.stat-icon {
  line-height: 1;
}

.brew-card {
  border: none;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s ease;
}

.brew-card:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.12);
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

.quick-action-btn {
  border-radius: 12px;
  padding: 1rem 0.5rem;
  text-align: center;
  border-width: 2px;
  transition: all 0.2s ease;
}

.quick-action-btn:hover {
  transform: translateY(-1px);
}

.quick-action-btn i {
  font-size: 1.25rem;
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

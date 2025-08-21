<template>
  <div class="container-fluid py-4">
    <!-- Header Section -->
    <div class="row mb-4">
      <div class="col-12">
        <div class="d-flex justify-content-between align-items-center">
          <div>
            <h1 class="h3 mb-0">Brewing Statistics</h1>
            <p class="text-secondary mb-0">Track your coffee brewing journey and insights</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Loading State -->
    <div v-if="loading" class="row">
      <div class="col-12 text-center py-5">
        <div class="spinner-border text-primary" role="status">
          <span class="visually-hidden">Loading...</span>
        </div>
        <p class="mt-3 text-secondary">Loading statistics...</p>
      </div>
    </div>

    <!-- Error State -->
    <div v-else-if="error" class="row">
      <div class="col-12">
        <div class="error-banner">{{ error }}</div>
        <div class="text-center mt-3">
          <button @click="loadStatistics" class="btn btn-secondary">Retry</button>
        </div>
      </div>
    </div>

    <!-- Statistics Content -->
    <div v-else-if="statistics" class="row">
      <!-- Overview Cards -->
      <div class="col-12 mb-4">
        <div class="row g-3">
          <div class="col-6 col-md-3">
            <div class="card text-center">
              <div class="card-body p-3">
                <div class="mb-2">
                  <i class="bi bi-graph-up fs-1 text-secondary"></i>
                </div>
                <div class="h4 mb-1 fw-bold">{{ statistics.totalBrewLogs }}</div>
                <div class="small text-secondary">Total Brews</div>
              </div>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="card text-center">
              <div class="card-body p-3">
                <div class="mb-2">
                  <i class="bi bi-star fs-1 text-secondary"></i>
                </div>
                <div class="h4 mb-1 fw-bold">{{ formatRating(statistics.averageRating) }}</div>
                <div class="small text-secondary">Average Rating</div>
              </div>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="card text-center">
              <div class="card-body p-3">
                <div class="mb-2">
                  <i class="bi bi-calendar-month fs-1 text-secondary"></i>
                </div>
                <div class="h4 mb-1 fw-bold">{{ statistics.totalBrewsThisMonth }}</div>
                <div class="small text-secondary">This Month</div>
              </div>
            </div>
          </div>
          <div class="col-6 col-md-3">
            <div class="card text-center">
              <div class="card-body p-3">
                <div class="mb-2">
                  <i class="bi bi-calendar-week fs-1 text-secondary"></i>
                </div>
                <div class="h4 mb-1 fw-bold">{{ statistics.totalBrewsThisWeek }}</div>
                <div class="small text-secondary">This Week</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Method Statistics -->
      <div v-if="statistics.methodStatistics && statistics.methodStatistics.length > 0" class="col-12 col-lg-6 mb-4">
        <div class="card">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-start mb-1">
              <h5 class="card-title mb-0">Most Used Methods</h5>
            </div>
            <div class="method-stats">
              <div 
                v-for="method in statistics.methodStatistics" 
                :key="method.method"
                class="method-item mb-3"
              >
                <div class="d-flex justify-content-between align-items-center mb-2">
                  <span class="fw-bold">{{ method.method }}</span>
                  <span class="text-secondary small">{{ method.count }} brews</span>
                </div>
                <div class="progress" style="height: 6px;">
                  <div 
                    class="progress-bar bg-dark" 
                    :style="{ width: getMethodPercentage(method.count) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Top Beans -->
      <div v-if="statistics.topBeans && statistics.topBeans.length > 0" class="col-12 col-lg-6 mb-4">
        <div class="card">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-start mb-1">
              <h5 class="card-title mb-0">Top Beans</h5>
            </div>
            <div class="bean-stats">
              <div 
                v-for="bean in sortedTopBeans" 
                :key="bean.beanName"
                class="bean-item mb-3"
              >
                <div class="d-flex justify-content-between align-items-center">
                  <span class="fw-bold">{{ bean.beanName }}</span>
                  <span v-if="bean.averageRating" class="fw-bold">{{ formatRating(bean.averageRating) }}/10</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent Brews -->
      <div v-if="statistics.recentBrews && statistics.recentBrews.length > 0" class="col-12 col-lg-6 mb-4">
        <div class="card">
          <div class="card-body">
            <div class="d-flex justify-content-between align-items-start mb-1">
              <h5 class="card-title mb-0">Recent Brews</h5>
            </div>
            <div class="recent-brews">
              <div 
                v-for="brew in statistics.recentBrews" 
                :key="brew.date"
                class="recent-brew-item mb-3"
              >
                <div class="d-flex justify-content-between align-items-center mb-1">
                  <span class="fw-bold">{{ brew.beanName }}</span>
                  <span class="text-secondary small">{{ brew.method }}</span>
                </div>
                <div class="text-secondary small">
                  <span v-if="brew.rating">{{ brew.rating }}/10</span><span v-if="brew.rating"> · </span>{{ brew.date }}
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { getBrewLogStatistics } from '../api/brewlogs.js'

const statistics = ref(null)
const loading = ref(false)
const error = ref(null)

const loadStatistics = async () => {
  loading.value = true
  error.value = null
  
  try {
    const response = await getBrewLogStatistics()
    statistics.value = response
  } catch (err) {
    error.value = 'Failed to load statistics. Please try again.'
    console.error('Error loading statistics:', err)
  } finally {
    loading.value = false
  }
}

const formatRating = (rating) => {
  if (!rating) return 'N/A'
  return rating.toFixed(1)
}

const getMethodPercentage = (count) => {
  if (!statistics.value || !statistics.value.methodStatistics.length) return 0
  const maxCount = Math.max(...statistics.value.methodStatistics.map(m => m.count))
  return (count / maxCount) * 100
}

const sortedTopBeans = computed(() => {
  if (!statistics.value || !statistics.value.topBeans) return []
  return [...statistics.value.topBeans].sort((a, b) => {
    // Sort by average rating (descending), then by brew count (descending)
    if (a.averageRating && b.averageRating) {
      return b.averageRating - a.averageRating
    }
    if (a.averageRating) return -1
    if (b.averageRating) return 1
    return b.brewCount - a.brewCount
  })
})

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.stars {
  display: flex;
  gap: 1px;
}


@media (max-width: 768px) {
  .recent-brew-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 0.5rem;
  }
}
</style>

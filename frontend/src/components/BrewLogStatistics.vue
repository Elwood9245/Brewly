<template>
  <div class="modal-overlay" @click="closeModal">
    <div class="modal-content" @click.stop>
      <div class="modal-header">
        <h2>Brewing Statistics</h2>
        <button @click="closeModal" class="close-btn">&times;</button>
      </div>

      <div class="statistics-content">
        <!-- Loading state -->
        <div v-if="loading" class="loading">
          <div class="spinner"></div>
          <p>Loading statistics...</p>
        </div>

        <!-- Error state -->
        <div v-else-if="error" class="error">
          <p>{{ error }}</p>
          <button @click="loadStatistics" class="btn btn-secondary">Retry</button>
        </div>

        <!-- Statistics content -->
        <div v-else-if="statistics" class="statistics">
          <!-- Overview Cards -->
          <div class="overview-cards">
            <div class="stat-card">
              <div class="stat-icon">📊</div>
              <div class="stat-content">
                <h3>Total Brews</h3>
                <p class="stat-value">{{ statistics.totalBrewLogs }}</p>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon">⭐</div>
              <div class="stat-content">
                <h3>Average Rating</h3>
                <p class="stat-value">{{ formatRating(statistics.averageRating) }}</p>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon">📅</div>
              <div class="stat-content">
                <h3>This Month</h3>
                <p class="stat-value">{{ statistics.totalBrewsThisMonth }}</p>
              </div>
            </div>

            <div class="stat-card">
              <div class="stat-icon">📆</div>
              <div class="stat-content">
                <h3>This Week</h3>
                <p class="stat-value">{{ statistics.totalBrewsThisWeek }}</p>
              </div>
            </div>
          </div>

          <!-- Method Statistics -->
          <div class="section" v-if="statistics.methodStatistics && statistics.methodStatistics.length > 0">
            <h3>Most Used Methods</h3>
            <div class="method-stats">
              <div 
                v-for="method in statistics.methodStatistics" 
                :key="method.method"
                class="method-item"
              >
                <div class="method-info">
                  <span class="method-name">{{ method.method }}</span>
                  <span class="method-count">{{ method.count }} brews</span>
                </div>
                <div class="method-rating" v-if="method.averageRating">
                  <span class="stars">
                    <span 
                      v-for="i in 10" 
                      :key="i" 
                      class="star" 
                      :class="{ filled: i <= method.averageRating }"
                    >
                      ★
                    </span>
                  </span>
                  <span class="rating-value">{{ formatRating(method.averageRating) }}</span>
                </div>
                <div class="method-bar">
                  <div 
                    class="bar-fill" 
                    :style="{ width: getMethodPercentage(method.count) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>

          <!-- Top Beans -->
          <div class="section" v-if="statistics.topBeans && statistics.topBeans.length > 0">
            <h3>Top Beans</h3>
            <div class="bean-stats">
              <div 
                v-for="bean in statistics.topBeans" 
                :key="bean.beanName"
                class="bean-item"
              >
                <div class="bean-info">
                  <span class="bean-name">{{ bean.beanName }}</span>
                  <span class="bean-count">{{ bean.brewCount }} brews</span>
                </div>
                <div class="bean-rating" v-if="bean.averageRating">
                  <span class="stars">
                    <span 
                      v-for="i in 10" 
                      :key="i" 
                      class="star" 
                      :class="{ filled: i <= bean.averageRating }"
                    >
                      ★
                    </span>
                  </span>
                  <span class="rating-value">{{ formatRating(bean.averageRating) }}</span>
                </div>
                <div class="bean-bar">
                  <div 
                    class="bar-fill" 
                    :style="{ width: getBeanPercentage(bean.brewCount) + '%' }"
                  ></div>
                </div>
              </div>
            </div>
          </div>

          <!-- Rating Distribution -->
          <div class="section" v-if="statistics.ratingDistribution">
            <h3>Rating Distribution</h3>
            <div class="rating-distribution">
              <div 
                v-for="(count, rating) in statistics.ratingDistribution" 
                :key="rating"
                class="rating-bar"
              >
                <span class="rating-label">{{ rating }}</span>
                <div class="rating-bar-container">
                  <div 
                    class="rating-bar-fill" 
                    :style="{ width: getRatingPercentage(count) + '%' }"
                  ></div>
                </div>
                <span class="rating-count">{{ count }}</span>
              </div>
            </div>
          </div>

          <!-- Recent Brews -->
          <div class="section" v-if="statistics.recentBrews && statistics.recentBrews.length > 0">
            <h3>Recent Brews</h3>
            <div class="recent-brews">
              <div 
                v-for="brew in statistics.recentBrews" 
                :key="brew.date"
                class="recent-brew-item"
              >
                <div class="brew-info">
                  <span class="brew-bean">{{ brew.beanName }}</span>
                  <span class="brew-method">{{ brew.method }}</span>
                </div>
                <div class="brew-rating" v-if="brew.rating">
                  <span class="stars">
                    <span 
                      v-for="i in 10" 
                      :key="i" 
                      class="star" 
                      :class="{ filled: i <= brew.rating }"
                    >
                      ★
                    </span>
                  </span>
                  <span class="rating-value">{{ brew.rating }}/10</span>
                </div>
                <span class="brew-date">{{ brew.date }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { getBrewLogStatistics } from '../api/brewlogs.js'

export default {
  name: 'BrewLogStatistics',
  emits: ['close'],
  setup(props, { emit }) {
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

    const closeModal = () => {
      emit('close')
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

    const getBeanPercentage = (count) => {
      if (!statistics.value || !statistics.value.topBeans.length) return 0
      const maxCount = Math.max(...statistics.value.topBeans.map(b => b.brewCount))
      return (count / maxCount) * 100
    }

    const getRatingPercentage = (count) => {
      if (!statistics.value || !statistics.value.ratingDistribution) return 0
      const total = Object.values(statistics.value.ratingDistribution).reduce((sum, c) => sum + c, 0)
      return total > 0 ? (count / total) * 100 : 0
    }

    onMounted(() => {
      loadStatistics()
    })

    return {
      statistics,
      loading,
      error,
      loadStatistics,
      closeModal,
      formatRating,
      getMethodPercentage,
      getBeanPercentage,
      getRatingPercentage
    }
  }
}
</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 8px;
  max-width: 900px;
  width: 100%;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #e9ecef;
}

.modal-header h2 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #6c757d;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.close-btn:hover {
  color: #495057;
}

.statistics-content {
  padding: 20px;
}

.loading,
.error {
  text-align: center;
  padding: 60px 20px;
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #3498db;
  border-radius: 50%;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.overview-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 15px;
  border-left: 4px solid #007bff;
}

.stat-icon {
  font-size: 32px;
}

.stat-content h3 {
  margin: 0 0 5px 0;
  font-size: 14px;
  color: #6c757d;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.stat-value {
  margin: 0;
  font-size: 24px;
  font-weight: 700;
  color: #2c3e50;
}

.section {
  margin-bottom: 30px;
}

.section h3 {
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 18px;
  border-bottom: 2px solid #e9ecef;
  padding-bottom: 8px;
}

.method-stats,
.bean-stats {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.method-item,
.bean-item {
  background: #f8f9fa;
  border-radius: 6px;
  padding: 15px;
  position: relative;
}

.method-info,
.bean-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.method-name,
.bean-name {
  font-weight: 600;
  color: #2c3e50;
}

.method-count,
.bean-count {
  font-size: 14px;
  color: #6c757d;
}

.method-rating,
.bean-rating {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  color: #e9ecef;
  font-size: 14px;
}

.star.filled {
  color: #ffc107;
}

.rating-value {
  font-weight: 600;
  color: #495057;
  font-size: 14px;
}

.method-bar,
.bean-bar {
  height: 6px;
  background: #e9ecef;
  border-radius: 3px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #007bff, #0056b3);
  border-radius: 3px;
  transition: width 0.3s ease;
}

.rating-distribution {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.rating-bar {
  display: flex;
  align-items: center;
  gap: 15px;
}

.rating-label {
  width: 30px;
  font-weight: 600;
  color: #495057;
  text-align: center;
}

.rating-bar-container {
  flex: 1;
  height: 20px;
  background: #e9ecef;
  border-radius: 10px;
  overflow: hidden;
}

.rating-bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #28a745, #20c997);
  border-radius: 10px;
  transition: width 0.3s ease;
}

.rating-count {
  width: 40px;
  font-size: 14px;
  color: #6c757d;
  text-align: right;
}

.recent-brews {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.recent-brew-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #f8f9fa;
  border-radius: 6px;
}

.brew-info {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.brew-bean {
  font-weight: 600;
  color: #2c3e50;
}

.brew-method {
  font-size: 12px;
  color: #6c757d;
}

.brew-rating {
  display: flex;
  align-items: center;
  gap: 8px;
}

.brew-date {
  font-size: 12px;
  color: #6c757d;
}

.btn {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s ease;
}

@media (max-width: 768px) {
  .modal-content {
    margin: 10px;
    max-height: 95vh;
  }
  
  .overview-cards {
    grid-template-columns: 1fr;
  }
  
  .method-info,
  .bean-info,
  .recent-brew-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .rating-bar {
    flex-direction: column;
    align-items: flex-start;
    gap: 5px;
  }
  
  .rating-label,
  .rating-count {
    width: auto;
  }
}
</style>

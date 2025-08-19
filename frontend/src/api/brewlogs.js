import apiClient from './client.js'

/**
 * Create a new brew log
 * @param {Object} brewLogData - Brew log data
 * @returns {Promise<Object>} Created brew log
 */
export const createBrewLog = async (brewLogData) => {
  try {
    const response = await apiClient.post('/brewlogs', brewLogData)
    return response.data
  } catch (error) {
    console.error('Error creating brew log:', error)
    throw error
  }
}

/**
 * Get user's brew logs with pagination
 * @param {number} page - Page number (default: 0)
 * @param {number} size - Page size (default: 10)
 * @param {string} sortBy - Sort field (default: 'createdAt')
 * @param {string} sortDir - Sort direction (default: 'DESC')
 * @returns {Promise<Object>} Paginated brew logs response
 */
export const getUserBrewLogs = async (page = 0, size = 10, sortBy = 'createdAt', sortDir = 'DESC') => {
  try {
    const params = new URLSearchParams({
      page: page.toString(),
      size: size.toString(),
      sortBy,
      sortDir
    })
    const response = await apiClient.get(`/brewlogs?${params}`)
    return response.data
  } catch (error) {
    console.error('Error fetching brew logs:', error)
    throw error
  }
}

/**
 * Get a specific brew log by ID
 * @param {string} id - Brew log ID
 * @returns {Promise<Object>} Brew log object
 */
export const getBrewLogById = async (id) => {
  try {
    const response = await apiClient.get(`/brewlogs/${id}`)
    return response.data
  } catch (error) {
    console.error('Error fetching brew log:', error)
    throw error
  }
}

/**
 * Update a brew log
 * @param {string} id - Brew log ID
 * @param {Object} brewLogData - Updated brew log data
 * @returns {Promise<Object>} Updated brew log
 */
export const updateBrewLog = async (id, brewLogData) => {
  try {
    const response = await apiClient.put(`/brewlogs/${id}`, brewLogData)
    return response.data
  } catch (error) {
    console.error('Error updating brew log:', error)
    throw error
  }
}

/**
 * Delete a brew log
 * @param {string} id - Brew log ID
 * @returns {Promise<boolean>} Success status
 */
export const deleteBrewLog = async (id) => {
  try {
    await apiClient.delete(`/brewlogs/${id}`)
    return true
  } catch (error) {
    console.error('Error deleting brew log:', error)
    throw error
  }
}

/**
 * Get brew log statistics
 * @returns {Promise<Object>} Brew log statistics
 */
export const getBrewLogStatistics = async () => {
  try {
    const response = await apiClient.get('/brewlogs/statistics')
    return response.data
  } catch (error) {
    console.error('Error fetching brew log statistics:', error)
    throw error
  }
}

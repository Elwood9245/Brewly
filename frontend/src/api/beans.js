import apiClient from './client.js'

export const BLEND_TYPES = ['SINGLE_ORIGIN', 'BLEND', 'UNKNOWN']
export const ROAST_LEVELS = ['POUR_OVER', 'ESPRESSO', 'OMNI']

/**
 * Get beans for a specific user
 * @param {string} userId - User ID
 * @returns {Promise<Array>} Array of beans
 */
export const getBeansByUserId = async (userId) => {
  const response = await apiClient.get(`/beans/user/${userId}`)
  return response.data
}

/**
 * Get bean by ID
 * @param {string} id - Bean ID
 * @returns {Promise<Object>} Bean object
 */
export const getBeanById = async (id) => {
  const response = await apiClient.get(`/beans/${id}`)
  return response.data
}

/**
 * Create a new bean
 * @param {Object} beanData - Bean data
 * @returns {Promise<Object>} Created bean
 */
export const createBean = async (beanData) => {
  const response = await apiClient.post('/beans', beanData)
  return response.data
}

/**
 * Update an existing bean
 * @param {string} id - Bean ID
 * @param {Object} beanData - Updated bean data
 * @returns {Promise<Object>} Updated bean
 */
export const updateBean = async (id, beanData) => {
  const response = await apiClient.put(`/beans/${id}`, beanData)
  return response.data
}

/**
 * Delete a bean
 * @param {string} id - Bean ID
 * @returns {Promise<void>}
 */
export const deleteBean = async (id) => {
  await apiClient.delete(`/beans/${id}`)
}

/**
 * Get beans for the current authenticated user
 * @returns {Promise<Array>} Array of beans
 */
export const getUserBeans = async () => {
  const response = await apiClient.get('/beans')
  return response.data
}

/**
 * Bean data transformation utilities
 */
export const beanUtils = {
  /**
   * Format date for display
   * @param {string|Date} dateStr - Date string or Date object
   * @returns {string} Formatted date string
   */
  formatDate(dateStr) {
    if (!dateStr) return ''
    const date = new Date(dateStr)
    return date.toLocaleDateString('en-GB')
  },

  /**
   * Convert date string to ISO format for API
   * @param {string} dateStr - Date string (YYYY-MM-DD)
   * @returns {string} ISO date string
   */
  dateToISO(dateStr) {
    if (!dateStr) return null
    return new Date(dateStr).toISOString()
  },

  /**
   * Convert ISO date to date string for form inputs
   * @param {string} isoStr - ISO date string
   * @returns {string} Date string (YYYY-MM-DD)
   */
  isoToDateString(isoStr) {
    if (!isoStr) return ''
    return new Date(isoStr).toISOString().split('T')[0]
  }
}

import apiClient from './client.js'

export const BLEND_TYPES = ['SINGLE_ORIGIN', 'BLEND', 'UNKNOWN']
export const ROAST_LEVELS = ['POUR_OVER', 'ESPRESSO', 'OMNI']

/**
 * Bean API service
 * Provides methods to interact with the Bean management endpoints
 */
export const beanAPI = {
  /**
   * Get all beans
   * @returns {Promise<Array>} Array of beans
   */
  async getAllBeans() {
    const response = await apiClient.get('/beans')
    return response.data
  },

  /**
   * Get beans for a specific user
   * @param {string} userId - User ID
   * @returns {Promise<Array>} Array of beans
   */
  async getBeansByUserId(userId) {
    const response = await apiClient.get(`/beans/user/${userId}`)
    return response.data
  },

  /**
   * Get bean by ID
   * @param {string} id - Bean ID
   * @returns {Promise<Object>} Bean object
   */
  async getBeanById(id) {
    const response = await apiClient.get(`/beans/${id}`)
    return response.data
  },

  /**
   * Create a new bean
   * @param {Object} beanData - Bean data
   * @returns {Promise<Object>} Created bean
   */
  async createBean(beanData) {
    const response = await apiClient.post('/beans', beanData)
    return response.data
  },

  /**
   * Update an existing bean
   * @param {string} id - Bean ID
   * @param {Object} beanData - Updated bean data
   * @returns {Promise<Object>} Updated bean
   */
  async updateBean(id, beanData) {
    const response = await apiClient.put(`/beans/${id}`, beanData)
    return response.data
  },

  /**
   * Delete a bean
   * @param {string} id - Bean ID
   * @returns {Promise<void>}
   */
  async deleteBean(id) {
    await apiClient.delete(`/beans/${id}`)
  }
}

/**
 * Bean data transformation utilities
 */
export const beanUtils = {
  /**
   * Create a new bean payload with default values
   * @param {string} userId - User ID
   * @returns {Object} Bean payload template
   */
  createBeanPayload(userId) {
    return {
      userId,
      name: '',
      origin: '',
      blend: 'SINGLE_ORIGIN', // Default to single origin
      roaster: '',
      roastType: 'POUR_OVER', // Default roast type
      roastedAt: null,
      restDays: null,
      flavour: '',
      weight: 0,
      consumption: 0,
      notes: '',
      isActive: true
    }
  },

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

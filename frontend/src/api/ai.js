import apiClient from './client.js'

/**
 * Send a message to the AI assistant
 * @param {string} message - User's message
 * @returns {Promise<Object>} AI response
 */
export const chatWithAI = async (message) => {
  try {
    const response = await apiClient.post('/ai/chat', { message })
    return response.data
  } catch (error) {
    throw error
  }
}

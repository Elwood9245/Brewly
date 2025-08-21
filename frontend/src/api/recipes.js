import apiClient from './client.js'

// Recipe CRUD operations
export const createRecipe = async (recipeData) => {
  const response = await apiClient.post('/recipes', recipeData)
  return response.data
}

export const getRecipeById = async (recipeId) => {
  const response = await apiClient.get(`/recipes/${recipeId}`)
  return response.data
}

export const updateRecipe = async (recipeId, recipeData) => {
  const response = await apiClient.put(`/recipes/${recipeId}`, recipeData)
  return response.data
}

export const deleteRecipe = async (recipeId) => {
  await apiClient.delete(`/recipes/${recipeId}`)
}

// User recipes
export const getUserRecipes = async (page = 0, size = 10) => {
  const response = await apiClient.get('/recipes/user', {
    params: { page, size }
  })
  return response.data
}

// Public recipes
export const getPublicRecipes = async (page = 0, size = 10, sort = 'latest') => {
  const response = await apiClient.get('/recipes/public', {
    params: { page, size, sort }
  })
  return response.data
}

export const searchPublicRecipes = async (keyword, page = 0, size = 10) => {
  const response = await apiClient.get('/recipes/public/search', {
    params: { keyword, page, size }
  })
  return response.data
}

export const getPublicRecipesByMethod = async (method, page = 0, size = 10) => {
  const response = await apiClient.get(`/recipes/public/method/${method}`, {
    params: { page, size }
  })
  return response.data
}

// Like operations
export const likeRecipe = async (recipeId) => {
  await apiClient.post(`/recipes/${recipeId}/likes`)
}

export const unlikeRecipe = async (recipeId) => {
  await apiClient.delete(`/recipes/${recipeId}/likes`)
}

// Comment operations
export const addComment = async (recipeId, commentData) => {
  const response = await apiClient.post(`/recipes/${recipeId}/comments`, commentData)
  return response.data
}

export const getRecipeComments = async (recipeId, page = 0, size = 10) => {
  const response = await apiClient.get(`/recipes/${recipeId}/comments`, {
    params: { page, size }
  })
  return response.data
}

export const deleteComment = async (commentId) => {
  await apiClient.delete(`/comments/${commentId}`)
}

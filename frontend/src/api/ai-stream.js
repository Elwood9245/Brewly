/**
 * AI Streaming API Client
 * Uses fetch API with ReadableStream and SSE line parser
 */

/**
 * Send a message to AI and receive streaming response
 * @param {string} message - User's message
 * @param {function} onChunk - Callback for each chunk of text received
 * @param {function} onComplete - Callback when streaming is complete
 * @param {function} onError - Callback for errors
 */
export async function chatWithAIStream(message, onChunk, onComplete, onError) {
  try {
    const token = localStorage.getItem('auth_token')

    if (!token) {
      throw new Error('No authentication token found. Please login again.')
    }

    const response = await fetch('/api/ai/chat/stream', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({ message })
    })

    if (!response.ok) {
      const errorText = await response.text()
      throw new Error(`HTTP error! status: ${response.status}`)
    }

    const reader = response.body.getReader()
    const decoder = new TextDecoder()
    let fullText = ''
    let buffer = ''

    while (true) {
      const { done, value } = await reader.read()

      if (done) {
        if (buffer.trim()) {
          const content = parseSSELine(buffer.trim())
          if (content && content !== '[DONE]') {
            fullText += content
            onChunk && onChunk(content, fullText)
          }
        }
        onComplete && onComplete(fullText)
        break
      }

      buffer += decoder.decode(value, { stream: true })

      const lines = buffer.split('\n')
      buffer = lines.pop() || ''

      for (const line of lines) {
        const content = parseSSELine(line)
        if (content === null) continue
        if (content === '[DONE]') continue
        fullText += content
        onChunk && onChunk(content, fullText)
      }
    }

  } catch (error) {
    console.error('Streaming error:', error)
    onError && onError(error)
  }
}

function parseSSELine(line) {
    line = line.replace(/\r$/, '');
    if (!line.startsWith('data:')) return null;
    return line.slice(5);
}

/**
 * Fallback to non-streaming API
 * @param {string} message - User's message
 * @returns {Promise<Object>} AI response
 */
export async function chatWithAINonStream(message) {
  const { default: apiClient } = await import('./client.js')
  const response = await apiClient.post('/ai/chat', { message })
  return response.data
}

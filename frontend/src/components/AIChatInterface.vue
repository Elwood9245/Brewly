<template>
  <div class="card">
    <div class="card-body">
      <!-- Welcome Message (shown when no messages) -->
      <div v-if="messages.length === 0" class="welcome-message">
        <div class="welcome-icon">
          <i class="bi bi-cup-hot"></i>
        </div>
        <h4>Brew Assistant</h4>
        <p>Ask me anything about coffee!</p>
      </div>

      <!-- Messages List (shown when there are messages) -->
      <div v-else class="messages-list">
        <div v-for="(message, index) in messages" :key="index" class="message-item">
          <div v-if="message.type === 'user'" class="user-message">
            <div class="message-label">You:</div>
            <div class="message-content">{{ message.text }}</div>
          </div>
          <div v-else-if="message.type === 'ai'" class="ai-message">
            <div class="message-label">AI Assistant:</div>
            <div class="message-content">{{ message.text }}</div>
          </div>
        </div>
      </div>

      <!-- Chat Input -->
      <form @submit.prevent="handleSubmit" class="chat-input-form">
        <div class="input-group">
          <input
            v-model="inputMessage"
            type="text"
            placeholder="Ask about your brewing technique..."
            class="form-control chat-input"
            :disabled="isLoading"
            maxlength="1000"
          />
          <div v-if="inputMessage.length > 800" class="char-count">
            {{ inputMessage.length }}/1000
          </div>
          <button 
            type="submit" 
            class="btn btn-primary send-btn"
            :disabled="isLoading || !inputMessage.trim()"
          >
            <i class="bi bi-send"></i>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'
import { chatWithAIStream } from '../api/ai-stream.js'

const messages = ref([])
const inputMessage = ref('')
const isLoading = ref(false)

const sendMessage = async (message) => {
  if (!message.trim() || isLoading.value) return
  
  // Add user message to list
  messages.value.push({
    type: 'user',
    text: message,
    timestamp: new Date()
  })
  
  isLoading.value = true
  
  // Add AI message placeholder
  const aiMessageIndex = messages.value.length
  messages.value.push({
    type: 'ai',
    text: '',
    timestamp: new Date()
  })
  
  inputMessage.value = ''
  
  try {
    await chatWithAIStream(
      message,
      // onChunk - update AI message as chunks arrive
      (chunk, fullText) => {
        messages.value[aiMessageIndex].text = fullText
        // Auto-scroll to bottom
        nextTick(() => {
          const messagesList = document.querySelector('.messages-list')
          if (messagesList) {
            messagesList.scrollTop = messagesList.scrollHeight
          }
        })
      },
      // onComplete
      (fullText) => {
        messages.value[aiMessageIndex].text = fullText
        isLoading.value = false
      },
      // onError
      (error) => {
        console.error('Streaming error:', error)
        messages.value[aiMessageIndex].text = 'Sorry, I encountered an error. Please try again.'
        isLoading.value = false
      }
    )
  } catch (error) {
    console.error('Error sending message:', error)
    messages.value[aiMessageIndex].text = 'Sorry, I encountered an error. Please try again.'
    isLoading.value = false
  }
}

const handleSubmit = () => {
  sendMessage(inputMessage.value)
}
</script>

<style scoped>
.card {
  height: calc(100vh - 134px); /* Account for header and footer */
  display: flex;
  flex-direction: column;
}

.card-body {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.welcome-message {
  text-align: center;
  margin-bottom: 2rem;
  color: #6c757d;
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.welcome-icon {
  font-size: 3rem;
  color: black;
  margin-bottom: 1rem;
}

.welcome-message h4 {
  color: #212529;
  margin-bottom: 0.5rem;
}

.welcome-message p {
  line-height: 1.5;
  margin-bottom: 0;
}

.messages-list {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 1rem;
  padding-right: 0.5rem;
}

.message-item {
  margin-bottom: 1rem;
}

.message-item:last-child {
  margin-bottom: 0;
}

.user-message, .ai-message {
  border-radius: 8px;
  padding: 0.75rem 1rem;
  margin-bottom: 0.5rem;
}

.user-message {
  background: #e3f2fd;
  border: 1px solid #bbdefb;
}

.ai-message {
  background: #f3e5f5;
  border: 1px solid #e1bee7;
}

.message-label {
  font-weight: 600;
  font-size: 0.8rem;
  margin-bottom: 0.25rem;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.user-message .message-label {
  color: #1976d2;
}

.ai-message .message-label {
  color: #7b1fa2;
}

.message-content {
  color: #212529;
  font-size: 0.9rem;
  line-height: 1.4;
  white-space: pre-line;
}

.chat-input-form {
  width: 100%;
  flex-shrink: 0;
}

.input-group {
  display: flex;
  gap: 0.5rem;
  position: relative;
}

.chat-input {
  border-radius: 25px;
  border: 1px solid #dee2e6;
  padding: 0.75rem 1rem;
  font-size: 0.9rem;
  flex: 1;
}

.chat-input:focus {
  border-color: #0d6efd;
  box-shadow: 0 0 0 0.2rem rgba(13, 110, 253, 0.25);
}

.send-btn {
  border-radius: 50%;
  width: 45px;
  height: 45px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0;
}

.char-count {
  font-size: 0.75rem;
  color: #6c757d;
  text-align: right;
  margin-top: 0.25rem;
  position: absolute;
  right: 60px;
  top: 50%;
  transform: translateY(-50%);
}
</style>

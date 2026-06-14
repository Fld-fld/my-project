<template>
  <div class="loading-container" v-if="visible">
    <div class="loading-overlay">
      <div class="loading-spinner">
        <div class="spinner-circle"></div>
        <div class="loading-text">{{ text }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { defineProps } from 'vue'

defineProps({
  visible: {
    type: Boolean,
    default: false
  },
  text: {
    type: String,
    default: '加载中...'
  }
})
</script>

<style scoped>
.loading-container {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 9999;
}

.loading-overlay {
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
}

.loading-spinner {
  text-align: center;
}

.spinner-circle {
  width: 50px;
  height: 50px;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top-color: #667eea;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
  margin: 0 auto 15px;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

.loading-text {
  color: white;
  font-size: 14px;
  margin-top: 10px;
}
</style>

<script setup lang="ts">
import { CheckCircle2, CircleAlert, Info, X } from 'lucide-vue-next'
import { useToast } from '@/composables/useToast'
const { toasts, remove } = useToast()
</script>

<template>
  <RouterView />
  <div class="toast-stack" aria-live="polite">
    <TransitionGroup name="toast">
      <div v-for="item in toasts.items" :key="item.id" class="toast" :class="`toast--${item.type}`">
        <CheckCircle2 v-if="item.type === 'success'" :size="19" />
        <CircleAlert v-else-if="item.type === 'error'" :size="19" />
        <Info v-else :size="19" />
        <span>{{ item.message }}</span>
        <button aria-label="关闭提示" @click="remove(item.id)"><X :size="16" /></button>
      </div>
    </TransitionGroup>
  </div>
</template>


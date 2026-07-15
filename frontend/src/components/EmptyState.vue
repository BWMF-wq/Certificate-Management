<script setup lang="ts">
import { Award, Plus } from 'lucide-vue-next'
defineProps<{ title?: string; description?: string; action?: string }>()
defineEmits<{ action: [] }>()
</script>

<template>
  <div class="empty-state">
    <div class="empty-seal"><Award :size="30" /></div>
    <h3>{{ title || '还没有证书记录' }}</h3>
    <p>{{ description || '把第一份学习成果收进档案，让每一步成长都有迹可循。' }}</p>
    <button v-if="action" class="btn btn-primary" @click="$emit('action')"><Plus :size="17" />{{ action }}</button>
  </div>
</template>

<style scoped>
.empty-state { padding: 64px 24px; display: flex; flex-direction: column; align-items: center; text-align: center; }
.empty-seal { width: 74px; height: 74px; display: grid; place-items: center; color: var(--red); border: 1px solid rgba(201,80,61,.32); border-radius: 50%; position: relative; background: rgba(201,80,61,.055); }
.empty-seal::after { content: ''; position: absolute; inset: 6px; border: 1px dashed rgba(201,80,61,.35); border-radius: 50%; }
h3 { margin: 20px 0 7px; font-size: 19px; } p { max-width: 390px; margin: 0 0 22px; color: var(--ink-2); font-size: 14px; line-height: 1.7; }
</style>


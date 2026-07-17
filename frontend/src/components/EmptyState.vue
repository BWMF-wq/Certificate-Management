<script setup lang="ts">
import { Award, Plus } from 'lucide-vue-next'
defineProps<{ title?: string; description?: string; action?: string }>()
defineEmits<{ action: [] }>()
</script>

<template>
  <div class="empty-state">
    <div class="empty-seal"><Award :size="30" /></div>
    <h3>{{ title || '还没有荣誉证书记录' }}</h3>
    <p>{{ description || '把第一份学习成果收进档案，让每一步成长都有迹可循。' }}</p>
    <button v-if="action" class="btn btn-primary" @click="$emit('action')"><Plus :size="17" />{{ action }}</button>
  </div>
</template>

<style scoped>
.empty-state { padding: 52px 24px; display: flex; flex-direction: column; align-items: center; text-align: center; }
.empty-seal { width: 52px; height: 52px; display: grid; place-items: center; color: var(--red); border: 1px solid var(--line); border-radius: 10px; background: var(--paper); }
h3 { margin: 17px 0 7px; font-size: 18px; } p { max-width: 390px; margin: 0 0 20px; color: var(--ink-2); font-size: 13px; line-height: 1.65; }
</style>


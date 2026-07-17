<script setup lang="ts">
import { AlertTriangle, X } from 'lucide-vue-next'
withDefaults(defineProps<{ open: boolean; title: string; message: string; loading?: boolean; confirmText?: string }>(), {
  loading: false,
  confirmText: '确认删除',
})
defineEmits<{ close: []; confirm: [] }>()
</script>

<template>
  <Transition name="modal">
    <div v-if="open" class="modal-backdrop" @click.self="$emit('close')">
      <div class="confirm-card">
        <button class="close" aria-label="关闭" @click="$emit('close')"><X :size="18" /></button>
        <span class="danger-icon"><AlertTriangle :size="25" /></span>
        <h2>{{ title }}</h2>
        <p>{{ message }}</p>
        <div class="actions">
          <button class="btn btn-secondary" :disabled="loading" @click="$emit('close')">取消</button>
          <button class="btn btn-danger" :disabled="loading" @click="$emit('confirm')">{{ loading ? '处理中…' : confirmText }}</button>
        </div>
      </div>
    </div>
  </Transition>
</template>

<style scoped>
.confirm-card { width: min(430px, 100%); padding: 28px; border-radius: 12px; background: var(--paper); box-shadow: 0 18px 56px rgba(7,20,30,.28); position: relative; }
.close { position: absolute; right: 17px; top: 17px; border: 0; background: transparent; cursor: pointer; opacity: .6; }
.danger-icon { width: 46px; height: 46px; display: grid; place-items: center; border-radius: 9px; background: #fbe6e1; color: var(--red); }
h2 { margin: 20px 0 8px; font-size: 21px; } p { margin: 0; color: var(--ink-2); line-height: 1.7; font-size: 14px; }
.actions { display: flex; justify-content: flex-end; gap: 10px; margin-top: 26px; }
</style>


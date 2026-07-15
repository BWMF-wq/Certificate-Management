<script setup lang="ts">
defineProps<{ compact?: boolean; light?: boolean }>()
</script>

<template>
  <div class="brand" :class="{ compact, light }" aria-label="证途">
    <span class="brand-mark"><i>证</i></span>
    <span v-if="!compact" class="brand-word"><b>证途</b><small>CERTPATH</small></span>
  </div>
</template>

<style scoped>
.brand { display: inline-flex; align-items: center; gap: 11px; color: var(--ink); }
.brand-mark { width: 42px; height: 46px; display: grid; place-items: center; color: var(--paper); background: var(--red); clip-path: polygon(50% 0, 95% 18%, 90% 76%, 50% 100%, 10% 76%, 5% 18%); position: relative; }
.brand-mark::after { content: ''; position: absolute; inset: 5px; border: 1px solid rgba(255,255,255,.55); clip-path: inherit; }
.brand-mark i { font-style: normal; font-family: 'Noto Sans SC'; font-size: 17px; font-weight: 700; position: relative; z-index: 1; }
.brand-word { display: flex; flex-direction: column; line-height: 1; }
.brand-word b { font-family: 'DM Serif Display','Noto Sans SC',serif; font-size: 23px; letter-spacing: .08em; }
.brand-word small { margin-top: 6px; font-size: 7px; letter-spacing: .31em; font-weight: 700; opacity: .58; }
.light { color: var(--paper); }.compact .brand-mark { width: 38px; height: 42px; }
</style>


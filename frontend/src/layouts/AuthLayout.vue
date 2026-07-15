<script setup lang="ts">
import { computed, onBeforeUnmount, onMounted, ref } from 'vue'

const now = ref(new Date())

const dateTime = computed(() => {
  const dateParts = new Intl.DateTimeFormat('zh-CN', {
    month: 'long',
    day: 'numeric',
    weekday: 'long',
  }).formatToParts(now.value)
  const month = dateParts.find(part => part.type === 'month')?.value ?? ''
  const day = dateParts.find(part => part.type === 'day')?.value ?? ''
  const weekday = dateParts.find(part => part.type === 'weekday')?.value ?? ''
  const time = new Intl.DateTimeFormat('zh-CN', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  }).format(now.value)
  return `${month} 月 ${day} 日 · ${weekday} · ${time}`
})

let clock: ReturnType<typeof setInterval> | undefined
onMounted(() => {
  clock = setInterval(() => { now.value = new Date() }, 60_000)
})
onBeforeUnmount(() => { if (clock) clearInterval(clock) })
</script>

<template>
  <main class="auth-shell">
    <section class="story-panel">
      <div class="story-content">
        <div class="day-meta">
          <span>{{ dateTime }}</span>
        </div>
      </div>
    </section>
    <section class="form-panel"><div class="form-wrap"><slot /></div></section>
  </main>
</template>

<style scoped>
.auth-shell { min-height:100vh; min-height:100dvh; display:grid; grid-template-rows:76px minmax(calc(100dvh - 76px),1fr); background:var(--paper); }
.story-panel { position:relative; overflow:hidden; padding:0 clamp(28px,5vw,72px); display:flex; align-items:center; background:#17324d; color:var(--paper); }
.story-panel::before { content:''; position:absolute; inset:0; opacity:.22; background:radial-gradient(circle at 10% 10%, rgba(71,139,145,.62), transparent 34%), linear-gradient(145deg, transparent 50%, rgba(217,164,65,.13)); }
.story-content { width:100%; position:relative; z-index:2; }
.day-meta { display:flex; justify-content:center; align-items:center; color:rgba(255,255,255,.7); font-size:11px; letter-spacing:.08em; }
.form-panel { min-height:calc(100dvh - 76px); display:grid; place-items:center; padding:40px; position:relative; }.form-panel::before{content:'';position:absolute;inset:0;background-image:linear-gradient(rgba(23,50,77,.03) 1px,transparent 1px),linear-gradient(90deg,rgba(23,50,77,.03) 1px,transparent 1px);background-size:28px 28px;mask-image:linear-gradient(to bottom right,black,transparent 72%)}
.form-wrap{position:relative;width:min(430px,100%)}
@media(max-width:880px){.story-panel{padding:0 28px}.form-panel{padding:36px 22px}}
</style>


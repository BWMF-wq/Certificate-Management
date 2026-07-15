<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Award, LayoutDashboard, LogOut, Menu, Plus, UserRound, X } from 'lucide-vue-next'
import LogoMark from '@/components/LogoMark.vue'
import { useAuthStore } from '@/stores/auth'

const route = useRoute(); const router = useRouter(); const auth = useAuthStore()
const menuOpen = ref(false)
const nav = [
  { name: 'dashboard', label: '成长概览', icon: LayoutDashboard },
  { name: 'certificates', label: '证书档案', icon: Award },
  { name: 'profile', label: '个人信息', icon: UserRound },
]
const initials = computed(() => auth.user?.name?.slice(-2) || '同学')

onMounted(() => auth.refresh().catch(() => {}))
function logout() { auth.logout(); router.push('/login') }
function navigate(name: string) { menuOpen.value = false; router.push({ name }) }
</script>

<template>
  <div class="app-shell">
    <Transition name="fade"><div v-if="menuOpen" class="nav-scrim" @click="menuOpen = false" /></Transition>
    <aside class="sidebar" :class="{ open: menuOpen }">
      <div class="sidebar-head"><LogoMark light /><button class="mobile-close" @click="menuOpen=false"><X :size="20" /></button></div>
      <p class="nav-eyebrow">我的学习档案</p>
      <nav>
        <button v-for="item in nav" :key="item.name" :class="{ active: route.name === item.name }" @click="navigate(item.name)">
          <component :is="item.icon" :size="19" /><span>{{ item.label }}</span><i />
        </button>
      </nav>
      <div class="sidebar-note">
        <span class="note-index">01</span>
        <p>每一张证书，都是你为未来写下的一行注脚。</p>
      </div>
      <div class="sidebar-user">
        <div class="avatar">{{ initials }}</div>
        <div><b>{{ auth.user?.name || '同学' }}</b><small>{{ auth.user?.studentId }}</small></div>
        <button aria-label="退出登录" title="退出登录" @click="logout"><LogOut :size="17" /></button>
      </div>
    </aside>
    <main class="main-area">
      <header class="mobile-header">
        <button class="menu-button" @click="menuOpen=true"><Menu :size="22" /></button><LogoMark compact />
        <button class="quick-add" aria-label="新增证书" @click="router.push({name:'certificates', query:{add:'1'}})"><Plus :size="20" /></button>
      </header>
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app-shell { min-height: 100vh; background: var(--paper); }
.sidebar { position: fixed; z-index: 100; inset: 0 auto 0 0; width: 260px; padding: 29px 20px 20px; display: flex; flex-direction: column; color: #f7f4ec; background: #17324d; overflow: hidden; }
.sidebar::before { content: ''; position: absolute; width: 330px; height: 330px; border: 1px solid rgba(255,255,255,.08); border-radius: 50%; left: -160px; bottom: 100px; box-shadow: 0 0 0 48px rgba(255,255,255,.025), 0 0 0 96px rgba(255,255,255,.015); pointer-events: none; }
.sidebar-head { position: relative; display: flex; justify-content: space-between; align-items: center; padding: 0 7px; }
.mobile-close { display: none; background: transparent; border: 0; color: white; }
.nav-eyebrow { margin: 47px 12px 13px; font-size: 10px; letter-spacing: .2em; opacity: .46; font-weight: 700; }
nav { display: grid; gap: 6px; position: relative; }
nav button { width: 100%; height: 47px; padding: 0 12px; border: 0; border-radius: 11px; color: rgba(255,255,255,.66); background: transparent; display: grid; grid-template-columns: 25px 1fr 6px; gap: 8px; align-items: center; text-align: left; font-size: 14px; cursor: pointer; transition: .2s; }
nav button:hover { background: rgba(255,255,255,.06); color: white; }
nav button.active { background: rgba(255,255,255,.1); color: white; }
nav button i { width: 5px; height: 5px; background: var(--red); border-radius: 50%; opacity: 0; }
nav button.active i { opacity: 1; box-shadow: 0 0 0 4px rgba(201,80,61,.18); }
.sidebar-note { margin: auto 6px 26px; padding: 18px; border: 1px solid rgba(255,255,255,.1); border-radius: 14px; background: rgba(255,255,255,.04); position: relative; }
.note-index { font-family: 'DM Serif Display'; font-size: 28px; color: var(--gold); opacity: .75; }
.sidebar-note p { margin: 7px 0 0; font-size: 12px; line-height: 1.75; color: rgba(255,255,255,.6); }
.sidebar-user { position: relative; display: grid; grid-template-columns: 38px 1fr 30px; gap: 10px; align-items: center; padding: 14px 10px 0; border-top: 1px solid rgba(255,255,255,.1); }
.avatar { width: 38px; height: 38px; display: grid; place-items: center; border-radius: 50%; color: #17324d; background: var(--gold); font-size: 12px; font-weight: 700; }
.sidebar-user b,.sidebar-user small { display: block; }.sidebar-user b { font-size: 13px; }.sidebar-user small { margin-top: 4px; color: rgba(255,255,255,.45); font-size: 10px; }
.sidebar-user button { border: 0; background: transparent; color: rgba(255,255,255,.5); cursor: pointer; }.sidebar-user button:hover { color: white; }
.main-area { margin-left: 260px; min-height: 100vh; }
.mobile-header { display: none; }
.nav-scrim { display: none; }
.fade-enter-active,.fade-leave-active{transition:opacity .2s}.fade-enter-from,.fade-leave-to{opacity:0}
@media (max-width: 860px) {
  .sidebar { transform: translateX(-100%); transition: transform .28s ease; box-shadow: 20px 0 70px rgba(8,20,30,.25); }.sidebar.open { transform: translateX(0); }.mobile-close { display: grid; }
  .main-area { margin-left: 0; }.nav-scrim { display: block; position: fixed; inset: 0; background: rgba(14,30,43,.45); z-index: 90; backdrop-filter: blur(3px); }
  .mobile-header { height: 68px; padding: 0 20px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid var(--line); background: rgba(247,244,236,.88); backdrop-filter: blur(12px); position: sticky; top: 0; z-index: 50; }
  .menu-button,.quick-add { width: 38px; height: 38px; display: grid; place-items: center; border: 0; border-radius: 10px; background: transparent; }.quick-add { color: white; background: var(--red); }
}
</style>

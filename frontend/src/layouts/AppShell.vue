<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Award, BarChart3, FolderKanban, LayoutDashboard, LogOut, Menu, Moon, PanelLeftClose, PanelLeftOpen, Sun, Trash2, UserRound, X } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import ConfirmDialog from '@/components/ConfirmDialog.vue'

const route = useRoute(); const router = useRouter(); const auth = useAuthStore()
const menuOpen = ref(false)
const sidebarCollapsed = ref(false)
const darkMode = ref(false)
const logoutConfirmOpen = ref(false)
const nav = [
  { name: 'dashboard', label: '证书概览', icon: LayoutDashboard },
  { name: 'certificates', label: '证书管理', icon: FolderKanban },
  { name: 'analytics', label: '数据分析', icon: BarChart3 },
  { name: 'trash', label: '回收站', icon: Trash2 },
  { name: 'profile', label: '账户设置', icon: UserRound, divider: true },
]
const initials = computed(() => auth.user?.name?.slice(-2) || '同学')

onMounted(() => {
  auth.refresh().catch(() => {})
  sidebarCollapsed.value = localStorage.getItem('certificate_sidebar') === 'collapsed'
  const savedTheme = localStorage.getItem('certificate_theme')
  const hour = new Date().getHours()
  darkMode.value = savedTheme ? savedTheme === 'dark' : window.matchMedia('(prefers-color-scheme: dark)').matches || hour < 6 || hour >= 18
})
function confirmLogout() {
  logoutConfirmOpen.value = false
  auth.logout()
  router.push('/login')
}
function navigate(name: string) { menuOpen.value = false; router.push({ name }) }
function toggleSidebar() {
  sidebarCollapsed.value = !sidebarCollapsed.value
  localStorage.setItem('certificate_sidebar', sidebarCollapsed.value ? 'collapsed' : 'expanded')
}
function toggleTheme() {
  darkMode.value = !darkMode.value
  localStorage.setItem('certificate_theme', darkMode.value ? 'dark' : 'light')
}
</script>

<template>
  <div class="app-shell" :class="{ 'dark-mode': darkMode, 'nav-collapsed': sidebarCollapsed }">
    <Transition name="fade"><div v-if="menuOpen" class="nav-scrim" @click="menuOpen = false" /></Transition>
    <aside class="sidebar" :class="{ open: menuOpen, collapsed: sidebarCollapsed }">
      <div class="sidebar-head"><div class="app-name"><Award :size="20"/><span>个人证书管理</span></div><button class="mobile-close" @click="menuOpen=false"><X :size="20" /></button></div>
      <p class="nav-eyebrow">导航</p>
      <nav>
        <button v-for="item in nav" :key="item.name" :title="sidebarCollapsed ? item.label : undefined" :class="{ active: route.name === item.name, divider: item.divider }" @click="navigate(item.name)">
          <component :is="item.icon" :size="19" /><span>{{ item.label }}</span><i />
        </button>
      </nav>
      <div class="sidebar-tools">
        <button :aria-pressed="darkMode" :title="darkMode ? '切换至白天模式' : '切换至夜间模式'" @click="toggleTheme">
          <Sun v-if="darkMode" :size="18"/><Moon v-else :size="18"/><span>{{darkMode?'夜间模式':'白天模式'}}</span>
        </button>
        <button class="collapse-button" :aria-label="sidebarCollapsed?'展开导航':'收起导航'" :title="sidebarCollapsed?'展开导航':'收起导航'" @click="toggleSidebar">
          <PanelLeftOpen v-if="sidebarCollapsed" :size="18"/><PanelLeftClose v-else :size="18"/><span>{{sidebarCollapsed?'展开导航':'收起导航'}}</span>
        </button>
      </div>
      <div class="sidebar-user">
        <div class="avatar">{{ initials }}</div>
        <div><b>{{ auth.user?.name || '同学' }}</b><small>{{ auth.user?.studentId }}</small></div>
        <button aria-label="退出登录" title="退出登录" @click="logoutConfirmOpen=true"><LogOut :size="17" /></button>
      </div>
    </aside>
    <main class="main-area">
      <header class="mobile-header">
        <button class="menu-button" @click="menuOpen=true"><Menu :size="22" /></button><span class="mobile-title">个人证书管理</span>
        <span class="header-spacer" aria-hidden="true"></span>
      </header>
      <RouterView />
    </main>
    <ConfirmDialog
      :open="logoutConfirmOpen"
      title="确认退出登录？"
      message="退出后需要重新输入账号和密码才能继续管理个人证书。"
      confirm-text="确认退出"
      @close="logoutConfirmOpen=false"
      @confirm="confirmLogout"
    />
  </div>
</template>

<style scoped>
.app-shell { min-height: 100vh; background: var(--paper); }
.app-shell,.sidebar,.main-area,.mobile-header{transition:background-color .25s,color .25s,margin .25s,width .25s}
.sidebar { position: fixed; z-index: 100; inset: 0 auto 0 0; width: 220px; padding: 24px 14px 18px; display: flex; flex-direction: column; color: #f7f4ec; background: #17324d; overflow: hidden; }
.sidebar-head { position: relative; display: flex; justify-content: space-between; align-items: center; padding: 0 7px; }
.app-name{display:flex;align-items:center;gap:10px;font-size:16px;font-weight:600}.app-name svg{color:rgba(255,255,255,.72)}
.mobile-close { display: none; background: transparent; border: 0; color: white; }
.nav-eyebrow { margin: 34px 12px 10px; font-size: 9px; letter-spacing: .16em; opacity: .48; font-weight: 700; text-transform: uppercase; }
nav { display: grid; gap: 4px; position: relative; }
nav button { width: 100%; height: 46px; padding: 0 12px; border: 0; border-radius: 10px; color: rgba(255,255,255,.68); background: transparent; display: grid; grid-template-columns: 25px 1fr; gap: 8px; align-items: center; text-align: left; font-size: 13px; cursor: pointer; transition: background-color .15s,color .15s,transform .15s; }
nav button:hover { background: rgba(255,255,255,.07); color: white; transform:translateX(2px); }
nav button.active { background: rgba(255,255,255,.13); color: white; box-shadow: inset 3px 0 var(--red),0 8px 18px rgba(0,0,0,.08); }
nav button.divider { margin-top: 16px; }
nav button.divider::before { content: ''; position: absolute; left: 10px; right: 10px; top: -11px; height: 1px; background: rgba(255,255,255,.1); }
nav button { position: relative; }
nav button i { display:none; }
.sidebar-tools{margin-top:auto;padding:0 4px 15px;display:grid;gap:5px}.sidebar-tools button{height:40px;padding:0 10px;border:0;border-radius:9px;display:flex;align-items:center;gap:10px;background:transparent;color:rgba(255,255,255,.62);font-size:11px;cursor:pointer;text-align:left}.sidebar-tools button:hover{background:rgba(255,255,255,.07);color:white}
.sidebar-user { position: relative; display: grid; grid-template-columns: 38px 1fr 30px; gap: 10px; align-items: center; padding: 17px 8px 0; border-top: 1px solid rgba(255,255,255,.1); }
.avatar { width: 38px; height: 38px; display: grid; place-items: center; border-radius: 50%; color: white; background: rgba(255,255,255,.13); font-size: 12px; font-weight: 700; }
.sidebar-user b,.sidebar-user small { display: block; }.sidebar-user b { font-size: 13px; }.sidebar-user small { margin-top: 4px; color: rgba(255,255,255,.45); font-size: 10px; }
.sidebar-user button { border: 0; background: transparent; color: rgba(255,255,255,.5); cursor: pointer; }.sidebar-user button:hover { color: white; }
.main-area { margin-left: 220px; min-height: 100vh; }
.mobile-header { display: none; }
.mobile-title{font-size:15px;font-weight:600}
.nav-scrim { display: none; }
.fade-enter-active,.fade-leave-active{transition:opacity .2s}.fade-enter-from,.fade-leave-to{opacity:0}
.sidebar.collapsed{width:72px;padding-left:10px;padding-right:10px}.collapsed .sidebar-head{justify-content:center}.collapsed .app-name span,.collapsed .nav-eyebrow,.collapsed nav span,.collapsed nav i,.collapsed .sidebar-tools span,.collapsed .sidebar-user .avatar,.collapsed .sidebar-user>div{display:none}.collapsed nav{margin-top:40px}.collapsed nav button{grid-template-columns:1fr;padding:0;place-items:center}.collapsed .sidebar-tools{padding-left:4px;padding-right:4px}.collapsed .sidebar-tools button{width:44px;padding:0;justify-content:center}.collapsed .sidebar-user{grid-template-columns:1fr;padding:17px 0 0}.collapsed .sidebar-user button{justify-self:center}.nav-collapsed .main-area{margin-left:72px}
.app-shell.dark-mode{--paper:#111820;--cream:#18232d;--white:#1a2630;--ink:#e8edf1;--ink-2:#a8b5bf;--line:rgba(225,235,241,.13);--shadow:0 18px 48px rgba(0,0,0,.22);background:var(--paper);color:var(--ink);color-scheme:dark}.dark-mode .sidebar{background:#0c1822}.dark-mode :deep(.panel),.dark-mode :deep(.stat),.dark-mode :deep(.filter-bar){background:rgba(26,38,48,.92);border-color:var(--line);box-shadow:none}.dark-mode :deep(.field input),.dark-mode :deep(.field select),.dark-mode :deep(.field textarea),.dark-mode :deep(.search-box),.dark-mode :deep(.select-wrap){background:#15212b;color:var(--ink);border-color:var(--line)}.dark-mode :deep(.field input:disabled){background:#202c35;color:#84929d}.dark-mode :deep(.btn-secondary),.dark-mode :deep(.icon-btn),.dark-mode :deep(.modal){background:#1a2630;color:var(--ink);border-color:var(--line)}.dark-mode :deep(.modal-head){background:rgba(17,24,32,.94)}.dark-mode :deep(.skeleton){background:#263540}.dark-mode :deep(select option){background:#15212b;color:var(--ink)}
.dark-mode :deep(.certificate-card){background:#1a2630;border-color:rgba(225,235,241,.14);box-shadow:none}
.dark-mode :deep(.certificate-card:hover){border-color:color-mix(in srgb,var(--accent) 42%,rgba(225,235,241,.14));box-shadow:none}
.dark-mode :deep(.certificate-card .attachment:not(.muted)){color:var(--accent)}
.dark-mode :deep(.certificate-card .icon-action:hover){background:rgba(232,237,241,.08);color:#f4f7f9}
.dark-mode :deep(.certificate-card .icon-action.danger:hover){background:rgba(201,80,61,.16);color:#f09a8d}
.dark-mode :deep(.certificate-card.cat-academic-education){--accent:#78a9d2}.dark-mode :deep(.certificate-card.cat-language-exam){--accent:#58b6aa}.dark-mode :deep(.certificate-card.cat-professional-qualification){--accent:#eb7a68}.dark-mode :deep(.certificate-card.cat-skill-certification){--accent:#d7a45d}.dark-mode :deep(.certificate-card.cat-competition-award){--accent:#eb7a68}.dark-mode :deep(.certificate-card.cat-innovation-entrepreneurship){--accent:#d7a45d}.dark-mode :deep(.certificate-card.cat-training-certificate){--accent:#82abc8}.dark-mode :deep(.certificate-card.cat-culture-sports){--accent:#b39bd2}.dark-mode :deep(.certificate-card.cat-social-practice){--accent:#62b4d4}.dark-mode :deep(.certificate-card.cat-volunteer-service){--accent:#58b6aa}.dark-mode :deep(.certificate-card.cat-honorary-title){--accent:#dbbb61}.dark-mode :deep(.certificate-card.cat-intellectual-property){--accent:#b39bd2}.dark-mode :deep(.certificate-card.cat-other){--accent:#a4b5c2}
@media (max-width: 860px) {
  .sidebar,.sidebar.collapsed { width:240px;padding:27px 18px 20px;transform: translateX(-100%); transition: transform .28s ease; box-shadow: 20px 0 70px rgba(8,20,30,.25); }.sidebar.open { transform: translateX(0); }.mobile-close { display: grid; }
  .collapsed .sidebar-head{justify-content:space-between}.collapsed .app-name span,.collapsed .nav-eyebrow,.collapsed nav span,.collapsed nav i,.collapsed .sidebar-tools span,.collapsed .sidebar-user .avatar,.collapsed .sidebar-user>div{display:initial}.collapsed nav{margin-top:0}.collapsed nav button{grid-template-columns:25px 1fr 6px;padding:0 12px;place-items:initial}.collapsed .sidebar-tools{padding:0 4px 15px}.collapsed .sidebar-tools button{width:auto;padding:0 10px;justify-content:flex-start}.collapsed .sidebar-user{grid-template-columns:38px 1fr 30px;padding:17px 8px 0}.collapsed .sidebar-user button{justify-self:auto}.collapse-button{display:none!important}
  .main-area { margin-left: 0; }.nav-scrim { display: block; position: fixed; inset: 0; background: rgba(14,30,43,.45); z-index: 90; backdrop-filter: blur(3px); }
  .mobile-header { height: 68px; padding: 0 20px; display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid var(--line); background:color-mix(in srgb,var(--paper) 88%,transparent); backdrop-filter: blur(12px); position: sticky; top: 0; z-index: 50; }
  .menu-button { width: 38px; height: 38px; display: grid; place-items: center; border: 0; border-radius: 10px; background: transparent; }
  .header-spacer { width: 38px; height: 38px; }
}
</style>

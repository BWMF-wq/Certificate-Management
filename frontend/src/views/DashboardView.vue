<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Award, BarChart3, Building2, FileArchive, FolderKanban, LayoutDashboard, Plus, Trash2 } from 'lucide-vue-next'
import { dashboardApi } from '@/api/services'
import { errorMessage } from '@/api/client'
import { useToast } from '@/composables/useToast'
import { useAuthStore } from '@/stores/auth'
import { LEVEL_LABELS } from '@/constants'
import type { DashboardData } from '@/types'
import CertificateCard from '@/components/CertificateCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const data = ref<DashboardData | null>(null)
const loading = ref(true)
const auth = useAuthStore()
const router = useRouter()
const toast = useToast()
const greeting = computed(() => {
  const hour = new Date().getHours()
  return hour < 11 ? '早上好' : hour < 18 ? '下午好' : '晚上好'
})
const attachmentRate = computed(() => data.value?.total ? Math.round(data.value.withAttachment / data.value.total * 100) : 0)
const maxLevel = computed(() => Math.max(1, ...(data.value?.levels.map(item => item.count) || [1])))

async function load() {
  loading.value = true
  try {
    data.value = (await dashboardApi.get()).data
  } catch (error) {
    toast.error(errorMessage(error))
  } finally {
    loading.value = false
  }
}

onMounted(load)
</script>

<template>
  <div class="dashboard">
    <header class="page-header">
      <div><span class="eyebrow">{{ greeting }}，{{ auth.user?.name || '同学' }}</span><h1 class="page-title">个人证书概览</h1><p class="page-subtitle">集中查看证书数量、归档进度和最近记录。</p></div>
      <button class="btn btn-primary" @click="router.push({ name: 'certificates', query: { add: '1' } })"><Plus :size="18"/>新增证书</button>
    </header>

    <template v-if="loading">
      <div class="stats-grid"><div v-for="index in 4" :key="index" class="skeleton stat-skeleton"/></div>
      <div class="overview-grid"><div class="skeleton overview-skeleton"/><div class="skeleton overview-skeleton"/></div>
    </template>

    <template v-else-if="data">
      <section class="stats-grid" aria-label="个人证书数据摘要">
        <article class="stat"><div><small>证书总数</small><strong>{{ data.total }}</strong><span>已收入个人证书档案</span></div><span class="stat-icon red"><Award :size="21"/></span></article>
        <article class="stat"><div><small>本年度新增</small><strong>{{ data.thisYear }}</strong><span>{{ new Date().getFullYear() }} 年取得</span></div><span class="stat-icon gold"><LayoutDashboard :size="21"/></span></article>
        <article class="stat"><div><small>电子附件</small><strong>{{ data.withAttachment }}</strong><span>归档覆盖率 {{ attachmentRate }}%</span></div><span class="stat-icon teal"><FileArchive :size="21"/></span></article>
        <article class="stat"><div><small>颁发机构</small><strong>{{ data.issuerCount }}</strong><span>不同机构来源</span></div><span class="stat-icon navy"><Building2 :size="21"/></span></article>
      </section>

      <section class="overview-grid">
        <article class="panel function-panel">
          <header><div><small>WORKSPACE</small><h2>主要功能</h2></div><span>统一入口</span></header>
          <div class="function-list">
            <button class="feature current" aria-current="page"><span class="feature-icon"><LayoutDashboard :size="20"/></span><span><b>证书概览</b><small>查看核心数据与最近归档</small></span><i>当前</i></button>
            <button class="feature" @click="router.push({ name: 'certificates' })"><span class="feature-icon"><FolderKanban :size="20"/></span><span><b>证书管理</b><small>新增、查询和维护个人证书</small></span><ArrowRight :size="17"/></button>
            <button class="feature" @click="router.push({ name: 'analytics' })"><span class="feature-icon"><BarChart3 :size="20"/></span><span><b>数据分析</b><small>分析证书类型、级别与归属构成</small></span><ArrowRight :size="17"/></button>
            <button class="feature" @click="router.push({ name: 'trash' })"><span class="feature-icon"><Trash2 :size="20"/></span><span><b>回收站</b><small>恢复或永久删除已移除的证书</small></span><i>{{ data.trashCount }} 项</i></button>
          </div>
        </article>

        <article class="panel level-panel">
          <header><div><small>CERTIFICATE LEVEL</small><h2>证书级别概况</h2></div><button @click="router.push({ name: 'analytics' })">完整分析<ArrowRight :size="14"/></button></header>
          <div v-if="data.levels.length" class="level-list">
            <div v-for="item in data.levels" :key="item.level"><div><span>{{ LEVEL_LABELS[item.level] }}</span><b>{{ item.count }}</b></div><i><em :style="{ width: `${item.count / maxLevel * 100}%` }"/></i></div>
          </div>
          <EmptyState v-else title="暂无级别数据" description="新增证书后，这里会显示证书级别构成。"/>
        </article>
      </section>

      <section class="recent-section">
        <header><div><span class="eyebrow">RECENT ARCHIVE</span><h2>最近记录</h2></div><button class="btn btn-ghost btn-sm" @click="router.push({ name: 'certificates' })">查看全部<ArrowRight :size="16"/></button></header>
        <div v-if="data.recent.length" class="recent-grid"><CertificateCard v-for="item in data.recent.slice(0, 3)" :key="item.id" :certificate="item" compact @delete="router.push({ name: 'certificates' })" @edit="router.push({ name: 'certificates' })" @download="router.push({ name: 'certificates' })"/></div>
        <div v-else class="panel"><EmptyState title="暂无证书记录" description="新增证书后，最近记录会显示在这里。" action="新增证书" @action="router.push({ name: 'certificates', query: { add: '1' } })"/></div>
      </section>
    </template>
  </div>
</template>

<style scoped>
.dashboard{padding:clamp(28px,4vw,52px);max-width:1400px;margin:0 auto}
.page-header{display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:28px}.eyebrow{display:block;color:var(--ink-2);font-size:10px;letter-spacing:.1em;font-weight:700;margin-bottom:9px}
.stats-grid{display:grid;grid-template-columns:repeat(4,1fr);gap:10px}.stat{min-height:124px;padding:19px;border:1px solid var(--line);border-radius:10px;background:var(--white);display:flex;justify-content:space-between;align-items:flex-start}.stat>div{display:grid}.stat small{font-size:11px;color:var(--ink-2)}.stat strong{font-size:34px;line-height:1.15;margin-top:5px;font-weight:650}.stat>div>span{font-size:10px;color:#87939c}.stat-icon{width:36px;height:36px;border-radius:8px;display:grid;place-items:center}.stat-icon.red{color:var(--red);background:rgba(201,80,61,.09)}.stat-icon.gold{color:#9b721e;background:rgba(217,164,65,.13)}.stat-icon.teal{color:var(--teal);background:rgba(47,125,120,.1)}.stat-icon.navy{color:var(--ink-2);background:rgba(23,50,77,.07)}
.overview-grid{display:grid;grid-template-columns:1.08fr .92fr;gap:16px;margin-top:17px}.function-panel,.level-panel{padding:24px;box-shadow:none;min-height:320px}.function-panel>header,.level-panel>header{display:flex;justify-content:space-between;align-items:flex-start}.function-panel header small,.level-panel header small{font-size:8px;letter-spacing:.2em;color:var(--red);font-weight:700}.function-panel h2,.level-panel h2{font-size:19px;margin:5px 0 0}.function-panel header>span{font-size:10px;color:var(--ink-2)}
.function-list{display:grid;gap:8px;margin-top:20px}.feature{width:100%;min-height:60px;padding:9px 11px;border:1px solid var(--line);border-radius:8px;background:var(--paper);display:grid;grid-template-columns:40px 1fr auto;gap:11px;align-items:center;text-align:left;cursor:pointer;transition:border-color .15s,background-color .15s}.feature:hover{border-color:rgba(23,50,77,.28);background:var(--white)}.feature.current{cursor:default;border-color:rgba(47,125,120,.28);background:rgba(47,125,120,.055)}.feature-icon{width:40px;height:40px;border-radius:8px;display:grid;place-items:center;background:var(--white);color:var(--teal);border:1px solid var(--line)}.feature b,.feature small{display:block}.feature b{font-size:13px}.feature small{margin-top:3px;color:var(--ink-2);font-size:10px}.feature>i{font-style:normal;font-size:9px;color:var(--teal);padding:3px 6px;border-radius:4px;background:rgba(47,125,120,.1)}
.level-panel header button{border:0;background:transparent;color:var(--red);font-size:10px;display:flex;align-items:center;gap:4px;cursor:pointer}.level-list{display:grid;gap:18px;margin-top:27px}.level-list>div>div{display:flex;justify-content:space-between;font-size:11px}.level-list b{font-size:15px}.level-list i{display:block;margin-top:7px;height:6px;border-radius:3px;background:var(--cream);overflow:hidden}.level-list em{display:block;height:100%;border-radius:inherit;background:var(--red)}
.recent-section{margin-top:39px}.recent-section>header{display:flex;justify-content:space-between;align-items:end;margin-bottom:16px}.recent-section h2{font-size:27px;margin:0}.recent-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:14px}.stat-skeleton{height:130px}.overview-skeleton{height:320px}
@media(max-width:1100px){.stats-grid{grid-template-columns:repeat(2,1fr)}.overview-grid{grid-template-columns:1fr}.recent-grid{grid-template-columns:repeat(2,1fr)}}
@media(max-width:650px){.dashboard{padding:24px 18px 45px}.page-header{align-items:flex-start;gap:12px}.page-header .btn{height:40px;padding:0 13px;font-size:12px}.stats-grid{grid-template-columns:1fr 1fr;gap:10px}.stat{min-height:118px;padding:17px}.stat strong{font-size:34px}.function-panel,.level-panel{padding:19px}.recent-grid{grid-template-columns:1fr}.recent-grid>*:nth-child(n+3){display:none}.feature{grid-template-columns:39px 1fr auto;padding:10px}.feature-icon{width:39px;height:39px}}
</style>

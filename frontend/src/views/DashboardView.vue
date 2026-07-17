<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, Award, Building2, FileArchive, LayoutDashboard, Plus } from 'lucide-vue-next'
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
    <template v-if="loading">
      <div class="skeleton dashboard-head-skeleton" />
      <div class="stats-grid"><div v-for="index in 4" :key="index" class="skeleton stat-skeleton" /></div>
      <div class="skeleton level-skeleton" />
    </template>

    <template v-else-if="data">
      <header class="dashboard-head reveal reveal-1">
        <div><span class="eyebrow"><i />{{ greeting }}，{{ auth.user?.name || '同学' }}</span><h1>证书概览</h1><p>查看个人证书档案的最新状态。</p></div>
        <button class="btn btn-primary" @click="router.push({ name: 'certificates', query: { add: '1' } })"><Plus :size="18" />新增证书</button>
      </header>

      <section class="stats-grid reveal reveal-2" aria-label="个人证书数据摘要">
        <article class="stat"><span class="stat-icon red"><Award :size="19" /></span><div><small>证书总数</small><strong>{{ data.total }}</strong><span>份个人记录</span></div></article>
        <article class="stat"><span class="stat-icon gold"><LayoutDashboard :size="19" /></span><div><small>本年新增</small><strong>{{ data.thisYear }}</strong><span>{{ new Date().getFullYear() }} 年</span></div></article>
        <article class="stat"><span class="stat-icon teal"><FileArchive :size="19" /></span><div><small>电子附件</small><strong>{{ data.withAttachment }}</strong><span>已妥善归档</span></div></article>
        <article class="stat"><span class="stat-icon navy"><Building2 :size="19" /></span><div><small>颁发机构</small><strong>{{ data.issuerCount }}</strong><span>个不同来源</span></div></article>
      </section>

      <section class="level-section reveal reveal-3" aria-labelledby="level-title">
        <header class="section-head">
          <div><span class="eyebrow"><i />CERTIFICATE LEVEL</span><h2 id="level-title">证书级别概况</h2></div>
          <button class="text-button" @click="router.push({ name: 'analytics' })">查看完整分析<ArrowRight :size="16" /></button>
        </header>
        <div v-if="data.levels.length" class="level-panel">
          <div v-for="item in data.levels.slice(0, 6)" :key="item.level" class="level-row">
            <div class="level-label"><span>{{ LEVEL_LABELS[item.level] }}</span><b>{{ item.count }}</b></div>
            <div class="level-track"><i :style="{ width: `${item.count / maxLevel * 100}%` }" /></div>
          </div>
        </div>
        <div v-else class="panel level-empty"><EmptyState title="暂无级别数据" description="新增证书后，这里会显示证书级别构成。" /></div>
      </section>

      <section class="recent-section reveal reveal-4">
        <header class="section-head">
          <div><span class="eyebrow"><i />RECENT ARCHIVE</span><h2>最近记录</h2></div>
          <button class="text-button" @click="router.push({ name: 'certificates' })">查看全部<ArrowRight :size="16" /></button>
        </header>
        <div v-if="data.recent.length" class="recent-grid"><CertificateCard v-for="item in data.recent.slice(0, 3)" :key="item.id" :certificate="item" compact @delete="router.push({ name: 'certificates' })" @edit="router.push({ name: 'certificates' })" @download="router.push({ name: 'certificates' })" /></div>
        <div v-else class="panel empty-panel"><EmptyState title="从第一份证书开始" description="添加后，最近整理的记录会出现在这里。" action="新增证书" @action="router.push({ name: 'certificates', query: { add: '1' } })" /></div>
      </section>
    </template>
  </div>
</template>

<style scoped>
.dashboard{width:min(1420px,100%);margin:0 auto;padding:clamp(24px,3.4vw,52px)}
.eyebrow{display:flex;align-items:center;gap:8px;color:var(--ink-2);font-size:9px;letter-spacing:.16em;font-weight:700}.eyebrow i{width:18px;height:1px;background:currentColor}
.dashboard-head{display:flex;align-items:end;justify-content:space-between;margin-bottom:27px}.dashboard-head h1{margin:8px 0 7px;font-family:'DM Serif Display','Noto Sans SC',serif;font-size:clamp(32px,4vw,48px);font-weight:400;letter-spacing:-.025em}.dashboard-head p{margin:0;color:var(--ink-2);font-size:12px}.dashboard-head .btn{height:42px}
.stats-grid{display:grid;grid-template-columns:repeat(4,1fr);margin-top:14px;border:1px solid var(--line);border-radius:15px;background:var(--white);overflow:hidden}.stat{min-height:104px;padding:21px 23px;display:flex;align-items:center;gap:15px;border-right:1px solid var(--line)}.stat:last-child{border:0}.stat-icon{width:39px;height:39px;display:grid;place-items:center;flex:0 0 auto;border-radius:50%}.stat-icon.red{color:var(--red);background:rgba(201,80,61,.09)}.stat-icon.gold{color:#9b721e;background:rgba(217,164,65,.13)}.stat-icon.teal{color:var(--teal);background:rgba(47,125,120,.1)}.stat-icon.navy{color:var(--ink-2);background:rgba(23,50,77,.07)}.stat>div{display:grid;grid-template-columns:auto 1fr;align-items:baseline;column-gap:7px}.stat small{grid-column:1/-1;color:var(--ink-2);font-size:10px}.stat strong{font-family:'DM Serif Display',serif;font-size:31px;font-weight:400;line-height:1.1}.stat div span{color:#87939c;font-size:9px}
.section-head{display:flex;align-items:end;justify-content:space-between;margin-bottom:18px}.section-head h2{margin:7px 0 0;font-family:'DM Serif Display','Noto Sans SC',serif;font-size:30px;font-weight:400}.level-section{margin-top:52px}.level-panel{padding:27px 30px;border:1px solid var(--line);border-radius:16px;background:var(--white);display:grid;gap:20px}.level-row{display:grid;grid-template-columns:125px 1fr;align-items:center;gap:22px}.level-label{display:flex;justify-content:space-between;align-items:baseline;color:var(--ink-2);font-size:11px}.level-label b{color:var(--ink);font-family:'DM Serif Display',serif;font-size:22px;font-weight:400}.level-track{height:9px;border-radius:9px;background:var(--cream);overflow:hidden}.level-track i{display:block;height:100%;border-radius:inherit;background:linear-gradient(90deg,var(--red),#e28b65);transform-origin:left;animation:bar-in .7s cubic-bezier(.22,.7,.25,1) both}.level-row:nth-child(2) .level-track i{background:linear-gradient(90deg,var(--teal),#71b5a8)}.level-row:nth-child(3) .level-track i{background:linear-gradient(90deg,var(--gold),#e4c27a)}.level-empty{box-shadow:none;padding:4px}.text-button{display:flex;align-items:center;gap:7px;padding:7px 0;border:0;border-bottom:1px solid var(--line);background:transparent;color:var(--ink-2);font-size:11px;cursor:pointer}.text-button:hover{color:var(--red);border-color:var(--red)}.recent-section{margin-top:52px}.recent-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:13px}.empty-panel{box-shadow:none}
.reveal{animation:rise-in .55s cubic-bezier(.22,.7,.25,1) both}.reveal-2{animation-delay:.07s}.reveal-3{animation-delay:.13s}.reveal-4{animation-delay:.19s}@keyframes rise-in{from{opacity:0;transform:translateY(14px)}to{opacity:1;transform:none}}
.dashboard-head-skeleton{height:96px;margin-bottom:27px}.stat-skeleton{height:104px;border-radius:0}.level-skeleton{height:280px;margin-top:52px}
@keyframes bar-in{from{transform:scaleX(0)}to{transform:scaleX(1)}}
@media(max-width:1180px){.recent-grid{grid-template-columns:repeat(2,1fr)}}
@media(max-width:780px){.dashboard{padding:22px 18px 44px}.dashboard-head{align-items:flex-start;gap:18px}.dashboard-head h1{font-size:36px}.dashboard-head .btn{height:40px;padding:0 13px;font-size:12px}.stats-grid{grid-template-columns:1fr 1fr}.stat:nth-child(2){border-right:0}.stat:nth-child(-n+2){border-bottom:1px solid var(--line)}.level-section{margin-top:40px}.level-panel{padding:22px 18px;gap:18px}.level-row{grid-template-columns:90px 1fr;gap:12px}.level-label{font-size:10px}.level-label b{font-size:19px}.recent-grid{grid-template-columns:1fr}.recent-grid>*:nth-child(n+3){display:none}}
@media(max-width:460px){.dashboard-head{display:block}.dashboard-head .btn{margin-top:18px}.stats-grid{display:grid}.stat{min-height:92px;padding:16px 13px;gap:10px}.stat-icon{width:34px;height:34px}.stat strong{font-size:27px}.stat div span{display:none}.section-head h2{font-size:26px}.section-head>p{display:none}}
@media(prefers-reduced-motion:reduce){.reveal,.nav-card,.round-arrow,.plus-mark{animation:none;transition:none}}
</style>

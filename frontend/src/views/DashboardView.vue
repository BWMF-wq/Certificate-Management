<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { AlertCircle, ArrowRight, Award, CalendarClock, ChevronRight, Plus, Sparkles } from 'lucide-vue-next'
import { dashboardApi } from '@/api/services'
import { errorMessage } from '@/api/client'
import { useToast } from '@/composables/useToast'
import { useAuthStore } from '@/stores/auth'
import { CATEGORY_LABELS } from '@/constants'
import type { DashboardData } from '@/types'
import CertificateCard from '@/components/CertificateCard.vue'
import EmptyState from '@/components/EmptyState.vue'

const data=ref<DashboardData|null>(null);const loading=ref(true);const auth=useAuthStore();const router=useRouter();const toast=useToast()
const greeting=computed(()=>{const h=new Date().getHours();return h<11?'早上好':h<18?'下午好':'晚上好'})
const maxMonth=computed(()=>Math.max(1,...(data.value?.monthlyTrend.map(i=>i.count)||[1])))
const maxCategory=computed(()=>Math.max(1,...(data.value?.categories.map(i=>i.count)||[1])))
async function load(){loading.value=true;try{data.value=(await dashboardApi.get()).data}catch(e){toast.error(errorMessage(e))}finally{loading.value=false}}
onMounted(load)
</script>

<template>
  <div class="dashboard page-enter">
    <header class="page-header"><div><span class="eyebrow">{{greeting}}，{{auth.user?.name||'同学'}}</span><h1 class="display page-title">今天也在积累未来</h1><p class="page-subtitle">所有努力，都值得被好好收藏。</p></div><button class="btn btn-primary desktop-add" @click="router.push({name:'certificates',query:{add:'1'}})"><Plus :size="18"/>新增证书</button></header>
    <template v-if="loading"><div class="stats-grid"><div v-for="i in 4" :key="i" class="skeleton stat-skeleton"/></div><div class="dashboard-grid"><div class="skeleton chart-skeleton"/><div class="skeleton chart-skeleton"/></div></template>
    <template v-else-if="data">
      <section class="stats-grid">
        <div class="stat stat-total"><div><small>证书总数</small><strong class="display">{{data.total}}</strong><span>份学习成果</span></div><span class="stat-icon"><Award :size="24"/></span></div>
        <div class="stat"><div><small>长期有效</small><strong class="display">{{data.permanent}}</strong><span>安心留存</span></div><span class="stat-dot teal"/></div>
        <div class="stat"><div><small>目前有效</small><strong class="display">{{data.valid}}</strong><span>状态良好</span></div><span class="stat-dot green"/></div>
        <div class="stat" :class="{attention:data.expiring>0}"><div><small>即将到期</small><strong class="display">{{data.expiring}}</strong><span>{{data.expiring?'需要关注':'暂无提醒'}}</span></div><span class="stat-icon warning"><CalendarClock :size="23"/></span></div>
      </section>
      <div v-if="data.expiring>0" class="notice"><AlertCircle :size="20"/><div><b>有 {{data.expiring}} 份证书将在 90 天内到期</b><span>及时确认续期或重新认证安排，别让重要资格悄悄失效。</span></div><button @click="router.push({name:'certificates',query:{status:'EXPIRING'}})">立即查看<ChevronRight :size="17"/></button></div>
      <section class="dashboard-grid">
        <div class="panel chart-panel"><header><div><small>12 MONTHS</small><h2>成长轨迹</h2></div><Sparkles :size="19"/></header><div class="bar-chart"><div v-for="item in data.monthlyTrend" :key="item.month" class="month-bar"><span class="bar-value">{{item.count||''}}</span><i :style="{height:`${Math.max(5,item.count/maxMonth*100)}%`}"/><small>{{item.month.slice(5)}}月</small></div></div></div>
        <div class="panel category-panel"><header><div><small>PORTFOLIO MIX</small><h2>证书构成</h2></div><span>{{data.categories.length}} 类</span></header><div v-if="data.categories.length" class="category-list"><div v-for="item in data.categories" :key="item.category"><div><span>{{CATEGORY_LABELS[item.category]}}</span><b>{{item.count}}</b></div><i><em :style="{width:`${item.count/maxCategory*100}%`}"/></i></div></div><EmptyState v-else title="等待第一份分类" description="新增证书后，这里会呈现你的能力版图。"/></div>
      </section>
      <section class="recent-section"><header><div><span class="eyebrow">RECENTLY EARNED</span><h2 class="display">最近获得</h2></div><button class="btn btn-ghost btn-sm" @click="router.push('/certificates')">查看全部<ArrowRight :size="16"/></button></header><div v-if="data.recent.length" class="recent-grid"><CertificateCard v-for="item in data.recent.slice(0,3)" :key="item.id" :certificate="item" compact @delete="router.push('/certificates')" @edit="router.push('/certificates')" @download="router.push('/certificates')"/></div><div v-else class="panel"><EmptyState action="添加第一份证书" @action="router.push({name:'certificates',query:{add:'1'}})"/></div></section>
    </template>
  </div>
</template>

<style scoped>
.dashboard{padding:clamp(28px,4vw,52px);max-width:1500px;margin:0 auto}.page-enter{animation:rise .5s ease both}@keyframes rise{from{opacity:0;transform:translateY(8px)}}.page-header{display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:31px}.eyebrow{display:block;color:var(--red);font-size:10px;letter-spacing:.17em;font-weight:700;margin-bottom:9px}.stats-grid{display:grid;grid-template-columns:1.25fr repeat(3,1fr);gap:14px}.stat{min-height:133px;padding:21px 20px;border:1px solid var(--line);border-radius:15px;background:rgba(255,253,248,.72);display:flex;justify-content:space-between;align-items:flex-start;position:relative;overflow:hidden}.stat>div{display:grid}.stat small{font-size:11px;color:var(--ink-2)}.stat strong{font-size:41px;line-height:1.15;margin-top:3px}.stat span:not(.stat-icon):not(.stat-dot){font-size:10px;color:#87939c}.stat-total{background:var(--ink);color:white}.stat-total small,.stat-total span:not(.stat-icon){color:rgba(255,255,255,.53)}.stat-icon{width:44px;height:44px;border-radius:12px;background:rgba(255,255,255,.1);display:grid;place-items:center;color:var(--gold)}.stat-icon.warning{color:var(--red);background:rgba(201,80,61,.1)}.stat-dot{width:10px;height:10px;border-radius:50%;box-shadow:0 0 0 6px rgba(47,125,120,.09)}.stat-dot.teal{background:var(--teal)}.stat-dot.green{background:#6c9c62}.attention{border-color:rgba(201,80,61,.25)}
.notice{margin-top:15px;padding:14px 17px;display:grid;grid-template-columns:auto 1fr auto;gap:12px;align-items:center;border-radius:13px;background:#f8eadb;color:#87501e}.notice div{display:flex;gap:8px;align-items:baseline}.notice b{font-size:12px}.notice span{font-size:10px;opacity:.75}.notice button{border:0;background:transparent;font-size:11px;font-weight:700;color:inherit;display:flex;align-items:center;cursor:pointer}.dashboard-grid{display:grid;grid-template-columns:1.45fr .9fr;gap:16px;margin-top:17px}.chart-panel,.category-panel{padding:23px;min-height:300px;box-shadow:none}.chart-panel header,.category-panel header{display:flex;justify-content:space-between;align-items:flex-start}.chart-panel header small,.category-panel header small{font-size:8px;letter-spacing:.2em;color:var(--red);font-weight:700}.chart-panel h2,.category-panel h2{font-size:17px;margin:5px 0 0}.chart-panel header>svg{color:var(--gold)}.category-panel header>span{font-family:'DM Serif Display';font-size:20px;color:var(--ink-2)}.bar-chart{height:210px;padding-top:30px;display:flex;align-items:flex-end;gap:9px}.month-bar{height:100%;flex:1;display:flex;flex-direction:column;align-items:center;justify-content:flex-end;gap:6px}.month-bar i{width:min(22px,70%);min-height:5px;border-radius:5px 5px 1px 1px;background:var(--teal);opacity:.78;transition:.3s}.month-bar:hover i{opacity:1;background:var(--red)}.month-bar small,.bar-value{font-size:8px;color:#89959e}.bar-value{height:10px;color:var(--ink-2)}.category-list{display:grid;gap:17px;margin-top:24px}.category-list>div>div{display:flex;justify-content:space-between;font-size:11px}.category-list b{font-family:'DM Serif Display';font-size:15px}.category-list i{display:block;margin-top:7px;height:5px;border-radius:3px;background:#e7e2d8;overflow:hidden}.category-list em{display:block;height:100%;border-radius:inherit;background:var(--red)}.recent-section{margin-top:38px}.recent-section>header{display:flex;justify-content:space-between;align-items:end;margin-bottom:16px}.recent-section h2{font-size:27px;margin:0}.recent-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:14px}.stat-skeleton{height:133px}.chart-skeleton{height:300px}.desktop-add{height:46px}
@media(max-width:1100px){.stats-grid{grid-template-columns:repeat(2,1fr)}.dashboard-grid{grid-template-columns:1fr}.recent-grid{grid-template-columns:repeat(2,1fr)}}@media(max-width:650px){.dashboard{padding:24px 18px 45px}.desktop-add{display:none}.stats-grid{grid-template-columns:1fr 1fr;gap:10px}.stat{min-height:118px;padding:17px}.stat strong{font-size:34px}.notice{grid-template-columns:auto 1fr}.notice div{display:grid;gap:3px}.notice button{grid-column:2}.chart-panel,.category-panel{padding:19px}.bar-chart{gap:4px}.month-bar small{font-size:7px}.recent-grid{grid-template-columns:1fr}.recent-grid>*:nth-child(n+3){display:none}}
</style>

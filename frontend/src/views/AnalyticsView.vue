<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, BarChart3, Layers3, Medal, UsersRound } from 'lucide-vue-next'
import { dashboardApi } from '@/api/services'
import { errorMessage } from '@/api/client'
import { useToast } from '@/composables/useToast'
import { AWARD_TYPE_LABELS, CATEGORY_LABELS, LEVEL_LABELS } from '@/constants'
import type { DashboardData } from '@/types'
import EmptyState from '@/components/EmptyState.vue'

const data = ref<DashboardData | null>(null)
const loading = ref(true)
const toast = useToast()
const router = useRouter()
const maxMonth = computed(() => Math.max(1, ...(data.value?.monthlyTrend.map(item => item.count) || [1])))
const maxLevel = computed(() => Math.max(1, ...(data.value?.levels.map(item => item.count) || [1])))
const maxCategory = computed(() => Math.max(1, ...(data.value?.categories.map(item => item.count) || [1])))
const leadingLevel = computed(() => data.value?.levels[0])
const leadingAward = computed(() => data.value?.awardTypes[0])
const percentage = (count: number) => data.value?.total ? Math.round(count / data.value.total * 100) : 0

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
  <div class="analytics-page">
    <header class="page-header"><div><span class="eyebrow">DATA INSIGHT</span><h1 class="page-title">数据分析</h1><p class="page-subtitle">从荣誉级别、奖项分类和取得时间理解证书构成。</p></div><button class="btn btn-secondary" @click="router.push({ name: 'certificates' })">管理证书<ArrowRight :size="16"/></button></header>

    <template v-if="loading"><div class="summary-grid"><div v-for="index in 3" :key="index" class="skeleton summary-skeleton"/></div><div class="analysis-grid"><div class="skeleton chart-skeleton"/><div class="skeleton chart-skeleton"/></div></template>

    <template v-else-if="data">
      <section class="summary-grid">
        <article class="summary"><span><BarChart3 :size="20"/></span><div><small>数据样本</small><strong>{{ data.total }}</strong><p>份证书档案</p></div></article>
        <article class="summary"><span><Medal :size="20"/></span><div><small>主要荣誉级别</small><strong class="text-value">{{ leadingLevel ? LEVEL_LABELS[leadingLevel.level] : '暂无数据' }}</strong><p v-if="leadingLevel">占全部档案 {{ percentage(leadingLevel.count) }}%</p></div></article>
        <article class="summary"><span><UsersRound :size="20"/></span><div><small>主要奖项分类</small><strong class="text-value">{{ leadingAward ? AWARD_TYPE_LABELS[leadingAward.awardType] : '暂无数据' }}</strong><p v-if="leadingAward">共 {{ leadingAward.count }} 项</p></div></article>
      </section>

      <section class="analysis-grid top-grid">
        <article class="panel trend-panel">
          <header><div><small>TREND</small><h2>近 12 个月取得趋势</h2></div><span>按取得日期统计</span></header>
          <div v-if="data.total" class="bar-chart"><div v-for="item in data.monthlyTrend" :key="item.month" class="month-bar"><span>{{ item.count || '' }}</span><i :style="{ height: `${Math.max(4, item.count / maxMonth * 100)}%` }"/><small>{{ item.month.slice(5) }}月</small></div></div>
          <EmptyState v-else title="暂无趋势数据" description="录入证书后将自动形成时间趋势。"/>
        </article>

        <article class="panel award-panel">
          <header><div><small>AWARD TYPE</small><h2>奖项分类</h2></div><UsersRound :size="18"/></header>
          <div v-if="data.awardTypes.length" class="award-list"><div v-for="item in data.awardTypes" :key="item.awardType" class="award-item"><div class="ring" :style="{ '--ratio': `${percentage(item.count) * 3.6}deg` }"><b>{{ percentage(item.count) }}%</b></div><div><strong>{{ AWARD_TYPE_LABELS[item.awardType] }}</strong><small>{{ item.count }} 份证书</small></div></div></div>
          <EmptyState v-else title="暂无奖项数据" description="新增证书时选择个人奖、团体奖或单位奖。"/>
        </article>
      </section>

      <section class="analysis-grid bottom-grid">
        <article class="panel distribution-panel">
          <header><div><small>HONOR LEVEL</small><h2>荣誉级别分布</h2></div><Medal :size="18"/></header>
          <div v-if="data.levels.length" class="distribution-list"><div v-for="item in data.levels" :key="item.level"><div><span>{{ LEVEL_LABELS[item.level] }}</span><b>{{ item.count }} <small>/ {{ percentage(item.count) }}%</small></b></div><i><em :style="{ width: `${item.count / maxLevel * 100}%` }"/></i></div></div>
          <EmptyState v-else title="暂无级别数据" description="荣誉级别数据会在这里形成分布。"/>
        </article>

        <article class="panel distribution-panel category-panel">
          <header><div><small>ACHIEVEMENT TYPE</small><h2>成果类型构成</h2></div><Layers3 :size="18"/></header>
          <div v-if="data.categories.length" class="distribution-list"><div v-for="item in data.categories" :key="item.category"><div><span>{{ CATEGORY_LABELS[item.category] }}</span><b>{{ item.count }} <small>/ {{ percentage(item.count) }}%</small></b></div><i><em :style="{ width: `${item.count / maxCategory * 100}%` }"/></i></div></div>
          <EmptyState v-else title="暂无类型数据" description="成果类型数据会在这里形成分布。"/>
        </article>
      </section>
    </template>
  </div>
</template>

<style scoped>
.analytics-page{padding:clamp(28px,4vw,52px);max-width:1450px;margin:0 auto;animation:rise .5s ease both}@keyframes rise{from{opacity:0;transform:translateY(8px)}}.page-header{display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:28px}.eyebrow{display:block;color:var(--red);font-size:10px;letter-spacing:.18em;font-weight:700;margin-bottom:9px}
.summary-grid{display:grid;grid-template-columns:repeat(3,1fr);gap:12px}.summary{min-height:108px;padding:20px;border:1px solid var(--line);border-radius:15px;background:color-mix(in srgb,var(--white) 84%,transparent);display:flex;align-items:center;gap:15px}.summary>span{width:43px;height:43px;border-radius:12px;display:grid;place-items:center;color:var(--red);background:rgba(201,80,61,.09)}.summary small,.summary strong,.summary p{display:block}.summary small{color:var(--ink-2);font-size:10px}.summary strong{font-family:'DM Serif Display','Noto Sans SC',serif;font-size:28px;line-height:1.15;margin-top:3px}.summary strong.text-value{font-family:'Noto Sans SC',sans-serif;font-size:16px;font-weight:700;margin-top:7px}.summary p{margin:4px 0 0;color:#83909b;font-size:9px}
.analysis-grid{display:grid;gap:16px}.top-grid{grid-template-columns:1.35fr .65fr;margin-top:17px}.bottom-grid{grid-template-columns:1fr 1fr;margin-top:16px}.trend-panel,.award-panel,.distribution-panel{padding:24px;box-shadow:none;min-height:320px}.panel header{display:flex;justify-content:space-between;align-items:flex-start}.panel header small{font-size:8px;letter-spacing:.2em;color:var(--red);font-weight:700}.panel h2{font-size:18px;margin:5px 0 0}.panel header>span,.panel header>svg{font-size:9px;color:var(--ink-2)}
.bar-chart{height:225px;padding-top:34px;display:flex;align-items:flex-end;gap:10px}.month-bar{height:100%;flex:1;display:flex;flex-direction:column;align-items:center;justify-content:flex-end;gap:7px}.month-bar>span{height:11px;font-size:8px;color:var(--ink-2)}.month-bar i{width:min(25px,72%);min-height:4px;border-radius:6px 6px 2px 2px;background:linear-gradient(180deg,var(--red),#d98455);opacity:.83;transition:.25s}.month-bar:hover i{opacity:1;transform:scaleX(1.08)}.month-bar small{font-size:8px;color:#89959e}
.award-list{display:grid;gap:13px;margin-top:24px}.award-item{padding:12px;border:1px solid var(--line);border-radius:13px;display:flex;align-items:center;gap:13px;background:color-mix(in srgb,var(--paper) 55%,transparent)}.ring{--ratio:0deg;width:49px;height:49px;flex:0 0 auto;border-radius:50%;display:grid;place-items:center;background:conic-gradient(var(--teal) var(--ratio),color-mix(in srgb,var(--cream) 85%,transparent) 0);position:relative}.ring::after{content:'';position:absolute;inset:6px;background:var(--white);border-radius:50%}.ring b{position:relative;z-index:1;font-size:9px}.award-item strong,.award-item small{display:block}.award-item strong{font-size:13px}.award-item small{margin-top:4px;color:var(--ink-2);font-size:9px}
.distribution-list{display:grid;gap:18px;margin-top:25px}.distribution-list>div>div{display:flex;justify-content:space-between;gap:12px;font-size:11px}.distribution-list b{font-family:'DM Serif Display';font-size:15px}.distribution-list b small{font-family:'Noto Sans SC';font-weight:400;color:var(--ink-2);font-size:8px}.distribution-list i{display:block;margin-top:7px;height:7px;border-radius:5px;background:color-mix(in srgb,var(--cream) 82%,transparent);overflow:hidden}.distribution-list em{display:block;height:100%;border-radius:inherit;background:linear-gradient(90deg,var(--red),var(--gold))}.category-panel .distribution-list em{background:linear-gradient(90deg,var(--teal),#68a09b)}
.summary-skeleton{height:108px}.chart-skeleton{height:320px}
@media(max-width:1050px){.top-grid,.bottom-grid{grid-template-columns:1fr}.summary-grid{grid-template-columns:1fr 1fr}.summary:last-child{grid-column:span 2}}
@media(max-width:650px){.analytics-page{padding:24px 18px 45px}.page-header{align-items:flex-start;gap:12px}.page-header .btn{height:40px;padding:0 12px;font-size:11px}.summary-grid{grid-template-columns:1fr}.summary:last-child{grid-column:auto}.trend-panel,.award-panel,.distribution-panel{padding:19px}.bar-chart{gap:4px}.month-bar small{font-size:7px}}
</style>

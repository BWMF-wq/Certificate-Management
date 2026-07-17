<script setup lang="ts">
import { onMounted, ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowLeft, CalendarDays, ChevronLeft, ChevronRight, FileBadge, RotateCcw, Search, Trash2, X } from 'lucide-vue-next'
import { certificateApi } from '@/api/services'
import { errorMessage } from '@/api/client'
import { useToast } from '@/composables/useToast'
import { AWARD_TYPE_LABELS, CATEGORY_LABELS, LEVEL_LABELS } from '@/constants'
import type { Certificate, PageResponse } from '@/types'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import EmptyState from '@/components/EmptyState.vue'

const router = useRouter()
const toast = useToast()
const result = ref<PageResponse<Certificate> | null>(null)
const loading = ref(true)
const keyword = ref('')
const page = ref(0)
const restoringId = ref<number | null>(null)
const permanentTarget = ref<Certificate | null>(null)
const deletingNow = ref(false)
let searchTimer = 0

async function load() {
  loading.value = true
  try {
    result.value = (await certificateApi.trash({ keyword: keyword.value || undefined, page: page.value, size: 12 })).data
  } catch (error) {
    toast.error(errorMessage(error))
  } finally {
    loading.value = false
  }
}

async function restore(item: Certificate) {
  restoringId.value = item.id
  try {
    await certificateApi.restore(item.id)
    toast.success('荣誉证书已恢复')
    if (result.value?.content.length === 1 && page.value > 0) page.value--
    await load()
  } catch (error) {
    toast.error(errorMessage(error))
  } finally {
    restoringId.value = null
  }
}

async function deletePermanently() {
  if (!permanentTarget.value) return
  deletingNow.value = true
  try {
    await certificateApi.removePermanently(permanentTarget.value.id)
    toast.success('荣誉证书已永久删除')
    permanentTarget.value = null
    if (result.value?.content.length === 1 && page.value > 0) page.value--
    await load()
  } catch (error) {
    toast.error(errorMessage(error))
  } finally {
    deletingNow.value = false
  }
}

function goPage(value: number) {
  page.value = value
  load()
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

function formatDeletedAt(value?: string | null) {
  if (!value) return '删除时间未知'
  return new Date(value).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit',
  })
}

watch(keyword, () => {
  window.clearTimeout(searchTimer)
  searchTimer = window.setTimeout(() => { page.value = 0; load() }, 350)
})
onMounted(load)
</script>

<template>
  <div class="trash-page">
    <header class="page-header">
      <div><span class="eyebrow">回收站</span><h1 class="page-title">已删除的荣誉证书</h1><p class="page-subtitle">删除后的记录和附件会保留在这里，可恢复或永久删除。</p></div>
      <button class="btn btn-secondary" @click="router.push({ name: 'certificates' })"><ArrowLeft :size="17"/>返回证书管理</button>
    </header>

    <section class="trash-toolbar panel">
      <div class="search-box"><Search :size="17"/><input v-model="keyword" placeholder="搜索已删除的荣誉证书"/><button v-if="keyword" aria-label="清空搜索" @click="keyword=''"> <X :size="15"/></button></div>
      <p v-if="result"><Trash2 :size="15"/>共 {{ result.totalElements }} 项</p>
    </section>

    <section v-if="loading" class="trash-list"><div v-for="index in 5" :key="index" class="skeleton row-skeleton"/></section>
    <section v-else-if="result?.content.length" class="trash-list">
      <article v-for="item in result.content" :key="item.id" class="trash-row panel">
        <div class="record-mark"><Trash2 :size="19"/></div>
        <div class="record-copy">
          <div class="record-tags"><span>{{ CATEGORY_LABELS[item.category] }}</span><span>{{ LEVEL_LABELS[item.level] }}</span><span>{{ AWARD_TYPE_LABELS[item.awardType] }}</span></div>
          <h2>{{ item.name }}</h2>
          <p>{{ item.issuer }}</p>
        </div>
        <div class="record-meta">
          <span><CalendarDays :size="14"/>{{ formatDeletedAt(item.deletedAt) }}</span>
          <span :class="{ muted: !item.hasAttachment }"><FileBadge :size="14"/>{{ item.hasAttachment ? item.fileName : '无附件' }}</span>
        </div>
        <div class="row-actions">
          <button class="btn btn-secondary btn-sm" :disabled="restoringId === item.id" @click="restore(item)"><RotateCcw :size="15"/>{{ restoringId === item.id ? '恢复中…' : '恢复' }}</button>
          <button class="btn btn-danger btn-sm" @click="permanentTarget=item"><Trash2 :size="15"/>永久删除</button>
        </div>
      </article>
    </section>
    <section v-else class="panel empty-panel"><EmptyState :title="keyword ? '没有匹配的已删除记录' : '回收站为空'" :description="keyword ? '请尝试其他关键词。' : '删除的荣誉证书会显示在这里。'" action="返回证书管理" @action="router.push({ name: 'certificates' })"/></section>

    <nav v-if="result && result.totalPages > 1" class="pagination" aria-label="分页">
      <button :disabled="page===0" @click="goPage(page-1)"><ChevronLeft :size="17"/></button>
      <button v-for="n in result.totalPages" v-show="Math.abs(n-1-page)<=2||n===1||n===result.totalPages" :key="n" :class="{active:n-1===page}" @click="goPage(n-1)">{{n}}</button>
      <button :disabled="page>=result.totalPages-1" @click="goPage(page+1)"><ChevronRight :size="17"/></button>
    </nav>

    <ConfirmDialog :open="Boolean(permanentTarget)" title="永久删除这份证书？" :message="`“${permanentTarget?.name||''}”及其附件将被永久删除，之后无法找回。`" confirm-text="永久删除" :loading="deletingNow" @close="permanentTarget=null" @confirm="deletePermanently"/>
  </div>
</template>

<style scoped>
.trash-page{padding:clamp(28px,4vw,52px);max-width:1380px;margin:0 auto}.page-header{display:flex;justify-content:space-between;align-items:flex-end;gap:20px;margin-bottom:24px}.eyebrow{display:block;color:var(--ink-2);font-size:10px;letter-spacing:.08em;font-weight:700;margin-bottom:8px}.trash-toolbar{min-height:60px;padding:9px;display:flex;align-items:center;justify-content:space-between;gap:16px;box-shadow:none}.search-box{width:min(520px,100%);height:40px;display:flex;align-items:center;gap:8px;padding:0 11px;border:1px solid var(--line);border-radius:7px;background:var(--paper);color:var(--ink-2)}.search-box input{flex:1;min-width:0;border:0;outline:0;background:transparent;color:var(--ink);font-size:12px}.search-box button{border:0;background:transparent;display:grid;cursor:pointer}.trash-toolbar p{margin:0 8px 0 0;color:var(--ink-2);display:flex;align-items:center;gap:6px;font-size:11px}.trash-list{display:grid;gap:9px;margin-top:16px}.trash-row{min-height:116px;padding:16px;display:grid;grid-template-columns:42px minmax(240px,1.3fr) minmax(230px,.8fr) auto;align-items:center;gap:16px;box-shadow:none}.record-mark{width:38px;height:38px;border-radius:8px;background:var(--cream);color:var(--ink-2);display:grid;place-items:center}.record-tags{display:flex;flex-wrap:wrap;gap:5px}.record-tags span{padding:3px 6px;border:1px solid var(--line);border-radius:4px;color:var(--ink-2);font-size:9px}.record-copy h2{margin:8px 0 3px;font-size:15px}.record-copy p{margin:0;color:var(--ink-2);font-size:11px}.record-meta{display:grid;gap:8px;color:var(--ink-2);font-size:10px}.record-meta span{display:flex;align-items:center;gap:6px}.record-meta .muted{color:#98a2aa}.row-actions{display:flex;gap:7px;justify-content:flex-end}.row-skeleton{height:116px}.empty-panel{margin-top:16px;box-shadow:none}.pagination{display:flex;justify-content:center;gap:6px;margin-top:28px}.pagination button{min-width:36px;height:36px;border:1px solid var(--line);border-radius:7px;background:var(--white);cursor:pointer}.pagination button.active{background:var(--teal);color:#fff;border-color:var(--teal)}.pagination button:disabled{opacity:.4;cursor:not-allowed}
@media(max-width:960px){.trash-row{grid-template-columns:42px 1fr auto}.record-meta{grid-column:2}.row-actions{grid-column:3;grid-row:1/3;flex-direction:column}}
@media(max-width:650px){.trash-page{padding:24px 18px 45px}.page-header{align-items:flex-start;flex-direction:column}.trash-toolbar{align-items:stretch;flex-direction:column}.trash-toolbar p{margin:0 2px}.trash-row{grid-template-columns:36px 1fr;gap:12px}.record-mark{width:34px;height:34px}.record-meta,.row-actions{grid-column:2;grid-row:auto}.row-actions{flex-direction:row;justify-content:flex-start}.row-actions .btn{flex:1}.pagination button{min-width:33px;height:33px}}
</style>

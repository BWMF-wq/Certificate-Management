<script setup lang="ts">
import { nextTick, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ChevronLeft, ChevronRight, Filter, Plus, Search, SlidersHorizontal, X } from 'lucide-vue-next'
import { certificateApi } from '@/api/services'
import { errorMessage } from '@/api/client'
import { useToast } from '@/composables/useToast'
import { CATEGORY_OPTIONS, STATUS_LABELS } from '@/constants'
import type { Certificate, CertificateStatus, Category, PageResponse } from '@/types'
import CertificateCard from '@/components/CertificateCard.vue'
import CertificateFormModal from '@/components/CertificateFormModal.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import EmptyState from '@/components/EmptyState.vue'

const route=useRoute();const router=useRouter();const toast=useToast();const result=ref<PageResponse<Certificate>|null>(null);const loading=ref(true)
const keyword=ref('');const category=ref<Category|''>('');const status=ref<CertificateStatus|''>((route.query.status as CertificateStatus)||'');const sort=ref('issueDate,desc');const page=ref(0)
const formOpen=ref(false);const editing=ref<Certificate|null>(null);const deleting=ref<Certificate|null>(null);const deletingNow=ref(false);let searchTimer=0
async function load(){loading.value=true;try{result.value=(await certificateApi.list({keyword:keyword.value||undefined,category:category.value,status:status.value,page:page.value,size:12,sort:sort.value})).data}catch(e){toast.error(errorMessage(e))}finally{loading.value=false}}
function openCreate(){editing.value=null;formOpen.value=true}function openEdit(value:Certificate){editing.value=value;formOpen.value=true}
function saved(){load()}
async function confirmDelete(){if(!deleting.value)return;deletingNow.value=true;try{await certificateApi.remove(deleting.value.id);toast.success('证书档案已删除');deleting.value=null;if(result.value?.content.length===1&&page.value>0)page.value--;await load()}catch(e){toast.error(errorMessage(e))}finally{deletingNow.value=false}}
async function download(value:Certificate){try{const response=await certificateApi.download(value.id);const url=URL.createObjectURL(response.data);const anchor=document.createElement('a');anchor.href=url;anchor.download=value.fileName||'证书附件';anchor.click();URL.revokeObjectURL(url);toast.success('附件下载已开始')}catch(e){toast.error(errorMessage(e))}}
function resetFilters(){keyword.value='';category.value='';status.value='';sort.value='issueDate,desc';page.value=0;load()}
function goPage(value:number){page.value=value;load();window.scrollTo({top:0,behavior:'smooth'})}
watch([category,status,sort],()=>{page.value=0;load()})
watch(keyword,()=>{window.clearTimeout(searchTimer);searchTimer=window.setTimeout(()=>{page.value=0;load()},350)})
onMounted(async()=>{await load();if(route.query.add==='1'){await nextTick();openCreate();router.replace({name:'certificates',query:{...route.query,add:undefined}})}})
</script>

<template>
  <div class="certificates-page">
    <header class="page-header"><div><span class="eyebrow">证书管理</span><h1 class="page-title">证书</h1><p class="page-subtitle">新增、查询和维护证书记录。</p></div><button class="btn btn-primary" @click="openCreate"><Plus :size="18"/>新增证书</button></header>
    <section class="filter-bar panel">
      <div class="search-box"><Search :size="18"/><input v-model="keyword" placeholder="搜索证书名称、机构或编号"/><button v-if="keyword" @click="keyword=''" aria-label="清空"><X :size="15"/></button></div>
      <div class="select-wrap"><Filter :size="15"/><select v-model="category"><option value="">全部分类</option><option v-for="[value,label] in CATEGORY_OPTIONS" :key="value" :value="value">{{label}}</option></select></div>
      <div class="select-wrap"><span class="status-dot"/><select v-model="status"><option value="">全部状态</option><option v-for="(label,value) in STATUS_LABELS" :key="value" :value="value">{{label}}</option></select></div>
      <div class="select-wrap sort"><SlidersHorizontal :size="15"/><select v-model="sort"><option value="issueDate,desc">取得时间：从新到旧</option><option value="issueDate,asc">取得时间：从旧到新</option><option value="name,asc">证书名称：A-Z</option><option value="createdAt,desc">最近录入</option></select></div>
    </section>
    <div class="result-meta"><p v-if="result">共找到 <b>{{result.totalElements}}</b> 份证书</p><button v-if="keyword||category||status||sort!=='issueDate,desc'" @click="resetFilters">重置筛选</button></div>
    <section v-if="loading" class="certificate-grid"><div v-for="i in 8" :key="i" class="skeleton card-skeleton"/></section>
    <section v-else-if="result?.content.length" class="certificate-grid"><CertificateCard v-for="item in result.content" :key="item.id" :certificate="item" @edit="openEdit" @delete="deleting=$event" @download="download"/></section>
    <section v-else class="panel empty-panel"><EmptyState :title="keyword||category||status?'没有匹配的证书':'暂无证书记录'" :description="keyword||category||status?'请调整关键词或筛选条件。':'新增证书后，可在这里统一查询和维护。'" :action="keyword||category||status?'重置筛选':'新增证书'" @action="keyword||category||status?resetFilters():openCreate()"/></section>
    <nav v-if="result&&result.totalPages>1" class="pagination" aria-label="分页"><button :disabled="page===0" @click="goPage(page-1)"><ChevronLeft :size="17"/></button><button v-for="n in result.totalPages" v-show="Math.abs(n-1-page)<=2||n===1||n===result.totalPages" :key="n" :class="{active:n-1===page}" @click="goPage(n-1)">{{n}}</button><button :disabled="page>=result.totalPages-1" @click="goPage(page+1)"><ChevronRight :size="17"/></button></nav>
    <CertificateFormModal :open="formOpen" :certificate="editing" @close="formOpen=false" @saved="saved"/>
    <ConfirmDialog :open="Boolean(deleting)" title="删除这份证书？" :message="`“${deleting?.name||''}”及其附件将被永久删除，且无法恢复。`" :loading="deletingNow" @close="deleting=null" @confirm="confirmDelete"/>
  </div>
</template>

<style scoped>
.certificates-page{padding:clamp(28px,4vw,52px);max-width:1500px;margin:0 auto;animation:rise .5s ease both}@keyframes rise{from{opacity:0;transform:translateY(8px)}}.page-header{display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:29px}.eyebrow{display:block;color:var(--red);font-size:10px;letter-spacing:.18em;font-weight:700;margin-bottom:9px}.filter-bar{padding:12px;display:grid;grid-template-columns:minmax(240px,1.4fr) repeat(3,minmax(130px,.55fr));gap:9px;box-shadow:none}.search-box,.select-wrap{height:42px;display:flex;align-items:center;gap:9px;padding:0 12px;border:1px solid var(--line);border-radius:10px;background:var(--paper);color:var(--ink-2)}.search-box input{flex:1;min-width:0;border:0;outline:0;background:transparent;color:var(--ink);font-size:12px}.search-box button{border:0;background:transparent;display:grid;cursor:pointer}.select-wrap select{width:100%;min-width:0;border:0;outline:0;background:transparent;color:var(--ink-2);font-size:11px;cursor:pointer}.status-dot{width:7px;height:7px;flex:0 0 auto;border-radius:50%;background:var(--teal);box-shadow:0 0 0 4px rgba(47,125,120,.1)}.result-meta{height:54px;display:flex;align-items:center;justify-content:space-between}.result-meta p{margin:0;color:var(--ink-2);font-size:11px}.result-meta b{color:var(--ink);font-family:'DM Serif Display';font-size:16px}.result-meta button{border:0;background:transparent;color:var(--red);font-size:11px;cursor:pointer}.certificate-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:15px}.card-skeleton{height:264px}.empty-panel{box-shadow:none}.pagination{display:flex;justify-content:center;gap:6px;margin-top:32px}.pagination button{min-width:36px;height:36px;border:1px solid var(--line);border-radius:9px;background:var(--white);cursor:pointer}.pagination button.active{background:var(--ink);color:white;border-color:var(--ink)}.pagination button:disabled{opacity:.4;cursor:not-allowed}
@media(min-width:1450px){.certificate-grid{grid-template-columns:repeat(4,minmax(0,1fr))}}@media(max-width:1120px){.filter-bar{grid-template-columns:1fr 1fr}.certificate-grid{grid-template-columns:repeat(2,minmax(0,1fr))}}@media(max-width:650px){.certificates-page{padding:24px 18px 45px}.page-header{align-items:flex-start}.page-header>div{min-width:0}.page-header .btn{width:auto;height:40px;padding:0 13px;flex:0 0 auto;border-radius:10px;font-size:12px}.filter-bar{grid-template-columns:1fr 1fr}.search-box,.sort{grid-column:span 2}.certificate-grid{grid-template-columns:1fr}.pagination button{min-width:33px;height:33px}}
</style>


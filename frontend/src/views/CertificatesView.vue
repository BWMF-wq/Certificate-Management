<script setup lang="ts">
import { nextTick, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ChevronLeft, ChevronRight, Filter, Grid2X2, List, Medal, Plus, Search, SlidersHorizontal, Trash2, UsersRound, X } from 'lucide-vue-next'
import { certificateApi } from '@/api/services'
import { errorMessage } from '@/api/client'
import { useToast } from '@/composables/useToast'
import { AWARD_TYPE_OPTIONS, CATEGORY_OPTIONS, LEVEL_OPTIONS } from '@/constants'
import type { AwardType, Certificate, Category, Level, PageResponse } from '@/types'
import CertificateCard from '@/components/CertificateCard.vue'
import CertificateFormModal from '@/components/CertificateFormModal.vue'
import ConfirmDialog from '@/components/ConfirmDialog.vue'
import EmptyState from '@/components/EmptyState.vue'

const route=useRoute();const router=useRouter();const toast=useToast();const result=ref<PageResponse<Certificate>|null>(null);const loading=ref(true)
const keyword=ref('');const category=ref<Category|''>((route.query.category as Category)||'');const level=ref<Level|''>((route.query.level as Level)||'');const awardType=ref<AwardType|''>((route.query.awardType as AwardType)||'');const sort=ref('issueDate,desc');const page=ref(0)
const savedView=localStorage.getItem('certificate_view');const viewMode=ref<'grid'|'list'>(savedView==='list'?'list':'grid')
const formOpen=ref(false);const editing=ref<Certificate|null>(null);const deleting=ref<Certificate|null>(null);const deletingNow=ref(false);let searchTimer=0
async function load(){loading.value=true;try{result.value=(await certificateApi.list({keyword:keyword.value||undefined,category:category.value,level:level.value,awardType:awardType.value,page:page.value,size:12,sort:sort.value})).data}catch(e){toast.error(errorMessage(e))}finally{loading.value=false}}
function openCreate(){editing.value=null;formOpen.value=true}function openEdit(value:Certificate){editing.value=value;formOpen.value=true}
function saved(){load()}
async function confirmDelete(){if(!deleting.value)return;deletingNow.value=true;try{await certificateApi.remove(deleting.value.id);toast.success('荣誉证书已移入回收站');deleting.value=null;if(result.value?.content.length===1&&page.value>0)page.value--;await load()}catch(e){toast.error(errorMessage(e))}finally{deletingNow.value=false}}
async function download(value:Certificate){try{const response=await certificateApi.download(value.id);const url=URL.createObjectURL(response.data);const anchor=document.createElement('a');anchor.href=url;anchor.download=value.fileName||'荣誉证书附件';anchor.click();URL.revokeObjectURL(url);toast.success('附件下载已开始')}catch(e){toast.error(errorMessage(e))}}
function resetFilters(){keyword.value='';category.value='';level.value='';awardType.value='';sort.value='issueDate,desc';page.value=0;load()}
function goPage(value:number){page.value=value;load();window.scrollTo({top:0,behavior:'smooth'})}
function setView(value:'grid'|'list'){viewMode.value=value;localStorage.setItem('certificate_view',value)}
watch([category,level,awardType,sort],()=>{page.value=0;load()})
watch(keyword,()=>{window.clearTimeout(searchTimer);searchTimer=window.setTimeout(()=>{page.value=0;load()},350)})
onMounted(async()=>{await load();if(route.query.add==='1'){await nextTick();openCreate();router.replace({name:'certificates',query:{...route.query,add:undefined}})}})
</script>

<template>
  <div class="certificates-page">
    <header class="page-header"><div><span class="eyebrow">荣誉证书管理</span><h1 class="page-title">荣誉证书</h1><p class="page-subtitle">新增、查询和维护个人荣誉证书记录。</p></div><div class="header-actions"><button class="btn btn-secondary" @click="router.push({name:'trash'})"><Trash2 :size="17"/>回收站</button><button class="btn btn-primary" @click="openCreate"><Plus :size="18"/>新增荣誉证书</button></div></header>
    <section class="filter-bar panel">
      <div class="search-box"><Search :size="18"/><input v-model="keyword" placeholder="搜索荣誉名称、颁发机构或编号"/><button v-if="keyword" @click="keyword=''" aria-label="清空"><X :size="15"/></button></div>
      <div class="select-wrap"><Filter :size="15"/><select v-model="category"><option value="">全部荣誉类型</option><option v-for="[value,label] in CATEGORY_OPTIONS" :key="value" :value="value">{{label}}</option></select></div>
      <div class="select-wrap"><Medal :size="15"/><select v-model="level"><option value="">全部荣誉级别</option><option v-for="[value,label] in LEVEL_OPTIONS" :key="value" :value="value">{{label}}</option></select></div>
      <div class="select-wrap"><UsersRound :size="15"/><select v-model="awardType"><option value="">全部奖项分类</option><option v-for="[value,label] in AWARD_TYPE_OPTIONS" :key="value" :value="value">{{label}}</option></select></div>
      <div class="select-wrap sort"><SlidersHorizontal :size="15"/><select v-model="sort"><option value="issueDate,desc">取得时间：从新到旧</option><option value="issueDate,asc">取得时间：从旧到新</option><option value="name,asc">荣誉名称：A-Z</option><option value="createdAt,desc">最近录入</option></select></div>
    </section>
    <div class="result-meta"><div><p v-if="result">共找到 <b>{{result.totalElements}}</b> 份荣誉证书</p><button v-if="keyword||category||level||awardType||sort!=='issueDate,desc'" @click="resetFilters">重置筛选</button></div><div class="view-switch" aria-label="显示方式"><button title="网格显示" :class="{active:viewMode==='grid'}" @click="setView('grid')"><Grid2X2 :size="16"/><span>网格</span></button><button title="列表显示" :class="{active:viewMode==='list'}" @click="setView('list')"><List :size="17"/><span>列表</span></button></div></div>
    <section v-if="loading" class="certificate-grid"><div v-for="i in 8" :key="i" class="skeleton card-skeleton"/></section>
    <section v-else-if="result?.content.length" class="certificate-grid" :class="{'list-view':viewMode==='list'}"><CertificateCard v-for="item in result.content" :key="item.id" :certificate="item" :layout="viewMode" @edit="openEdit" @delete="deleting=$event" @download="download"/></section>
    <section v-else class="panel empty-panel"><EmptyState :title="keyword||category||level||awardType?'没有匹配的荣誉证书':'暂无荣誉证书记录'" :description="keyword||category||level||awardType?'请调整关键词或筛选条件。':'新增荣誉证书后，可在这里统一查询和维护。'" :action="keyword||category||level||awardType?'重置筛选':'新增荣誉证书'" @action="keyword||category||level||awardType?resetFilters():openCreate()"/></section>
    <nav v-if="result&&result.totalPages>1" class="pagination" aria-label="分页"><button :disabled="page===0" @click="goPage(page-1)"><ChevronLeft :size="17"/></button><button v-for="n in result.totalPages" v-show="Math.abs(n-1-page)<=2||n===1||n===result.totalPages" :key="n" :class="{active:n-1===page}" @click="goPage(n-1)">{{n}}</button><button :disabled="page>=result.totalPages-1" @click="goPage(page+1)"><ChevronRight :size="17"/></button></nav>
    <CertificateFormModal :open="formOpen" :certificate="editing" @close="formOpen=false" @saved="saved"/>
    <ConfirmDialog :open="Boolean(deleting)" title="移入回收站？" :message="`“${deleting?.name||''}”将从证书列表移除，附件会保留，之后可在回收站恢复。`" confirm-text="移入回收站" :loading="deletingNow" @close="deleting=null" @confirm="confirmDelete"/>
  </div>
</template>

<style scoped>
.certificates-page{padding:clamp(28px,4vw,52px);max-width:1500px;margin:0 auto}.page-header{display:flex;justify-content:space-between;align-items:flex-end;margin-bottom:24px}.header-actions{display:flex;gap:8px}.eyebrow{display:block;color:var(--ink-2);font-size:10px;letter-spacing:.08em;font-weight:700;margin-bottom:8px}.filter-bar{padding:10px;display:grid;grid-template-columns:minmax(230px,1.35fr) repeat(4,minmax(120px,.55fr));gap:8px;box-shadow:none}.search-box,.select-wrap{height:40px;display:flex;align-items:center;gap:8px;padding:0 11px;border:1px solid var(--line);border-radius:7px;background:var(--paper);color:var(--ink-2)}.search-box input{flex:1;min-width:0;border:0;outline:0;background:transparent;color:var(--ink);font-size:12px}.search-box button{border:0;background:transparent;display:grid;cursor:pointer}.select-wrap select{width:100%;min-width:0;border:0;outline:0;background:transparent;color:var(--ink-2);font-size:11px;cursor:pointer}.result-meta{min-height:54px;display:flex;align-items:center;justify-content:space-between;gap:16px}.result-meta>div:first-child{display:flex;align-items:center;gap:14px}.result-meta p{margin:0;color:var(--ink-2);font-size:11px}.result-meta b{color:var(--ink);font-size:15px}.result-meta>div:first-child button{border:0;background:transparent;color:var(--red);font-size:11px;cursor:pointer}.view-switch{display:flex;padding:3px;border:1px solid var(--line);border-radius:7px;background:var(--white)}.view-switch button{height:29px;padding:0 9px;border:0;border-radius:5px;background:transparent;color:var(--ink-2);display:flex;align-items:center;gap:5px;font-size:10px;cursor:pointer}.view-switch button.active{background:var(--teal);color:#fff}.certificate-grid{display:grid;grid-template-columns:repeat(3,minmax(0,1fr));gap:12px}.certificate-grid.list-view{grid-template-columns:1fr}.card-skeleton{height:250px}.empty-panel{box-shadow:none}.pagination{display:flex;justify-content:center;gap:6px;margin-top:32px}.pagination button{min-width:36px;height:36px;border:1px solid var(--line);border-radius:7px;background:var(--white);cursor:pointer}.pagination button.active{background:var(--teal);color:#fff;border-color:var(--teal)}.pagination button:disabled{opacity:.4;cursor:not-allowed}
@media(min-width:1450px){.certificate-grid:not(.list-view){grid-template-columns:repeat(4,minmax(0,1fr))}}@media(max-width:1120px){.filter-bar{grid-template-columns:1fr 1fr}.certificate-grid:not(.list-view){grid-template-columns:repeat(2,minmax(0,1fr))}}@media(max-width:650px){.certificates-page{padding:24px 18px 45px}.page-header{align-items:flex-start;gap:12px}.header-actions{flex-direction:column-reverse;align-items:stretch}.page-header>div{min-width:0}.page-header .btn{width:auto;height:38px;padding:0 11px;flex:0 0 auto;font-size:11px}.filter-bar{grid-template-columns:1fr 1fr}.search-box,.sort{grid-column:span 2}.certificate-grid{grid-template-columns:1fr!important}.result-meta{align-items:flex-start;padding:10px 0}.result-meta>div:first-child{display:grid;gap:3px}.view-switch span{display:none}.pagination button{min-width:33px;height:33px}}
</style>


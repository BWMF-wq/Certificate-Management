<script setup lang="ts">
import { Award, BookOpenCheck, BriefcaseBusiness, CalendarDays, Download, ExternalLink, FileBadge, Languages, Medal, MoreHorizontal, Pencil, Trash2, Trophy } from 'lucide-vue-next'
import { computed } from 'vue'
import type { Certificate } from '@/types'
import { CATEGORY_LABELS, LEVEL_LABELS, STATUS_LABELS } from '@/constants'

const props = defineProps<{ certificate: Certificate; compact?: boolean }>()
defineEmits<{ edit: [value: Certificate]; delete: [value: Certificate]; download: [value: Certificate] }>()
const icon = computed(()=>({LANGUAGE:Languages,PROFESSIONAL:BriefcaseBusiness,COMPETITION:Trophy,SKILL:BookOpenCheck,ACADEMIC:FileBadge,HONOR:Medal,OTHER:Award})[props.certificate.category])
const issueYear = computed(()=>props.certificate.issueDate?.slice(0,4))
</script>

<template>
  <article class="certificate-card" :class="[`cat-${certificate.category.toLowerCase()}`, {compact}]">
    <div class="card-index">{{ issueYear }}</div>
    <header><span class="category-icon"><component :is="icon" :size="20"/></span><div class="badges"><span class="status" :class="`status-${certificate.status.toLowerCase()}`">{{ STATUS_LABELS[certificate.status] }}</span><span class="level">{{ LEVEL_LABELS[certificate.level] }}</span></div></header>
    <div class="card-copy"><small>{{ CATEGORY_LABELS[certificate.category] }}</small><h3>{{ certificate.name }}</h3><p>{{ certificate.issuer }}</p></div>
    <div class="date-row"><CalendarDays :size="15"/><span>{{ certificate.issueDate }} 获得</span><template v-if="certificate.expiryDate"><i/> <span>{{ certificate.expiryDate }} 到期</span></template></div>
    <footer>
      <div class="attachment" :class="{muted:!certificate.hasAttachment}"><FileBadge :size="15"/><span>{{ certificate.hasAttachment ? certificate.fileName : '暂无附件' }}</span></div>
      <div class="card-actions">
        <a v-if="certificate.credentialUrl" class="icon-action" :href="certificate.credentialUrl" target="_blank" rel="noopener" title="官网验证"><ExternalLink :size="16"/></a>
        <button v-if="certificate.hasAttachment" class="icon-action" title="下载附件" @click="$emit('download',certificate)"><Download :size="16"/></button>
        <button class="icon-action" title="编辑" @click="$emit('edit',certificate)"><Pencil :size="16"/></button>
        <button class="icon-action danger" title="删除" @click="$emit('delete',certificate)"><Trash2 :size="16"/></button>
      </div>
    </footer>
  </article>
</template>

<style scoped>
.certificate-card{position:relative;min-height:264px;padding:22px;display:flex;flex-direction:column;overflow:hidden;background:color-mix(in srgb,var(--white) 88%,transparent);border:1px solid var(--line);border-radius:16px;transition:.25s ease;box-shadow:0 9px 25px rgba(22,42,58,.045)}
.certificate-card::before{content:'';position:absolute;left:0;top:0;bottom:0;width:4px;background:var(--accent,#2f7d78)}.certificate-card::after{content:'';position:absolute;right:-46px;top:-46px;width:125px;height:125px;border:1px solid color-mix(in srgb,var(--accent,#2f7d78) 20%,transparent);border-radius:50%;box-shadow:0 0 0 18px color-mix(in srgb,var(--accent,#2f7d78) 3%,transparent)}
.certificate-card:hover{transform:translateY(-3px);box-shadow:0 17px 36px rgba(22,42,58,.10);border-color:rgba(23,50,77,.2)}
.cat-language{--accent:#397f9b}.cat-professional{--accent:#b27935}.cat-competition{--accent:#c9503d}.cat-skill{--accent:#2f7d78}.cat-academic{--accent:#5d638d}.cat-honor{--accent:#b89431}.cat-other{--accent:#687a88}
.card-index{position:absolute;right:12px;bottom:30px;font-family:'DM Serif Display';font-size:68px;color:var(--ink);opacity:.035;line-height:1;pointer-events:none}
header{display:flex;align-items:center;justify-content:space-between;position:relative;z-index:1}.category-icon{width:39px;height:39px;display:grid;place-items:center;border-radius:11px;background:color-mix(in srgb,var(--accent) 11%,transparent);color:var(--accent)}.badges{display:flex;gap:6px}.badges span{padding:5px 8px;border-radius:99px;font-size:9px;font-weight:700}.status{background:#e8f2eb;color:#31714f}.status-expiring{background:#f9efda;color:#94671d}.status-expired{background:#f6e5e1;color:#a23a2d}.status-permanent{background:#e6f1f0;color:#28726d}.level{border:1px solid var(--line);color:var(--ink-2)}
.card-copy{margin-top:24px;position:relative;z-index:1}.card-copy small{color:var(--accent);font-size:10px;letter-spacing:.12em;font-weight:700}.card-copy h3{margin:7px 0 6px;font-family:'DM Serif Display','Noto Sans SC',serif;font-size:21px;line-height:1.3;font-weight:600;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden}.card-copy p{margin:0;color:var(--ink-2);font-size:12px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.date-row{display:flex;align-items:center;gap:7px;margin-top:17px;color:#718294;font-size:10px}.date-row i{width:3px;height:3px;border-radius:50%;background:#a8b1b7}
footer{margin-top:auto;padding-top:16px;border-top:1px solid var(--line);display:flex;justify-content:space-between;align-items:center;gap:10px;position:relative;z-index:2}.attachment{display:flex;align-items:center;gap:6px;color:var(--teal);font-size:10px;min-width:0}.attachment span{max-width:115px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.attachment.muted{color:#96a0a8}.card-actions{display:flex;gap:3px}.icon-action{width:30px;height:30px;padding:0;display:grid;place-items:center;border:0;border-radius:8px;background:transparent;color:var(--ink-2);cursor:pointer}.icon-action:hover{background:rgba(23,50,77,.07);color:var(--ink)}.icon-action.danger:hover{background:#f7e4df;color:var(--red)}
.compact{min-height:unset;padding:17px}.compact .card-copy{margin-top:15px}.compact .card-copy h3{font-size:17px}.compact .date-row,.compact .badges .level{display:none}.compact footer{padding-top:11px}.compact .card-actions button:not(:last-child),.compact .card-actions a{display:none}
</style>


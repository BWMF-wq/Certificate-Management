<script setup lang="ts">
import { Award, CalendarDays, Download, Drama, ExternalLink, FileBadge, Handshake, HeartHandshake, Lightbulb, Medal, Pencil, Trash2, Trophy } from 'lucide-vue-next'
import { computed } from 'vue'
import type { Certificate } from '@/types'
import { AWARD_TYPE_LABELS, CATEGORY_LABELS, LEVEL_LABELS } from '@/constants'

const props = withDefaults(defineProps<{ certificate: Certificate; compact?: boolean; layout?: 'grid' | 'list' }>(), { layout: 'grid' })
defineEmits<{ edit: [value: Certificate]; delete: [value: Certificate]; download: [value: Certificate] }>()
const icon = computed(()=>({ACADEMIC_COMPETITION:Trophy,INNOVATION_ENTREPRENEURSHIP:Lightbulb,CULTURE_SPORTS:Drama,SOCIAL_PRACTICE:Handshake,VOLUNTEER_SERVICE:HeartHandshake,HONORARY_TITLE:Medal,OTHER:Award})[props.certificate.category])
const categoryClass = computed(()=>`cat-${props.certificate.category.toLowerCase().replace(/_/g,'-')}`)
</script>

<template>
  <article class="certificate-card" :class="[categoryClass, layout, {compact}]">
    <header><span class="category-icon"><component :is="icon" :size="20"/></span><div class="badges"><span class="award-type">{{ AWARD_TYPE_LABELS[certificate.awardType] }}</span><span class="level">{{ LEVEL_LABELS[certificate.level] }}</span></div></header>
    <div class="card-copy"><small>{{ CATEGORY_LABELS[certificate.category] }}</small><h3>{{ certificate.name }}</h3><p>{{ certificate.issuer }}</p></div>
    <div class="date-row"><CalendarDays :size="15"/><span>{{ certificate.issueDate }} 获得</span><template v-if="certificate.expiryDate"><i/> <span>有效期至 {{ certificate.expiryDate }}</span></template></div>
    <footer>
      <div class="attachment" :class="{muted:!certificate.hasAttachment}"><FileBadge :size="15"/><span>{{ certificate.hasAttachment ? certificate.fileName : '暂无附件' }}</span></div>
      <div class="card-actions">
        <a v-if="certificate.credentialUrl" class="icon-action" :href="certificate.credentialUrl" target="_blank" rel="noopener" title="官网验证"><ExternalLink :size="15"/><span>验证</span></a>
        <button v-if="certificate.hasAttachment" class="icon-action" title="下载附件" @click="$emit('download',certificate)"><Download :size="15"/><span>下载</span></button>
        <button class="icon-action" title="编辑" @click="$emit('edit',certificate)"><Pencil :size="15"/><span>编辑</span></button>
        <button class="icon-action danger" title="移入回收站" @click="$emit('delete',certificate)"><Trash2 :size="15"/><span>回收站</span></button>
      </div>
    </footer>
  </article>
</template>

<style scoped>
.certificate-card{position:relative;min-height:250px;padding:19px;display:flex;flex-direction:column;background:var(--white);border:1px solid var(--line);border-left:3px solid var(--accent,#2f7d78);border-radius:10px;transition:border-color .15s}
.certificate-card:hover{border-color:color-mix(in srgb,var(--accent) 52%,var(--line));border-left-color:var(--accent)}
.cat-academic-competition{--accent:#c9503d}.cat-innovation-entrepreneurship{--accent:#b27935}.cat-culture-sports{--accent:#735f91}.cat-social-practice{--accent:#397f9b}.cat-volunteer-service{--accent:#2f7d78}.cat-honorary-title{--accent:#b89431}.cat-other{--accent:#687a88}
header{display:flex;align-items:center;justify-content:space-between}.category-icon{width:36px;height:36px;display:grid;place-items:center;border-radius:8px;background:color-mix(in srgb,var(--accent) 10%,transparent);color:var(--accent)}.badges{display:flex;gap:6px;flex-wrap:wrap;justify-content:flex-end}.badges span{padding:4px 7px;border-radius:5px;font-size:9px;font-weight:700}.award-type{background:color-mix(in srgb,var(--accent) 10%,transparent);color:var(--accent)}.level{border:1px solid var(--line);color:var(--ink-2)}
.card-copy{margin-top:20px}.card-copy small{color:var(--accent);font-size:10px;font-weight:700}.card-copy h3{margin:7px 0 6px;font-size:17px;line-height:1.45;font-weight:650;display:-webkit-box;-webkit-line-clamp:2;-webkit-box-orient:vertical;overflow:hidden}.card-copy p{margin:0;color:var(--ink-2);font-size:12px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}
.date-row{display:flex;align-items:center;gap:7px;margin-top:17px;color:#718294;font-size:10px}.date-row i{width:3px;height:3px;border-radius:50%;background:#a8b1b7}
footer{margin-top:auto;padding-top:14px;border-top:1px solid var(--line);display:flex;justify-content:space-between;align-items:center;gap:10px}.attachment{display:flex;align-items:center;gap:6px;color:var(--teal);font-size:10px;min-width:0}.attachment span{max-width:110px;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.attachment.muted{color:#96a0a8}.card-actions{display:flex;gap:4px}.icon-action{height:30px;padding:0 7px;display:flex;align-items:center;gap:4px;border:1px solid transparent;border-radius:6px;background:transparent;color:var(--ink-2);font-size:10px;cursor:pointer}.icon-action:hover{border-color:var(--line);background:var(--paper);color:var(--ink)}.icon-action.danger:hover{border-color:#efd0c9;background:#f9eeeb;color:var(--red)}
.certificate-card.list{min-height:0;display:grid;grid-template-columns:115px minmax(220px,1fr) 175px minmax(320px,auto);align-items:center;gap:18px;padding:14px 16px}.list .card-copy{margin:0}.list .card-copy h3{-webkit-line-clamp:1}.list .date-row{margin:0}.list footer{margin:0;padding:0;border:0}.list .attachment span{max-width:100px}
.compact{min-height:unset;padding:16px}.compact .card-copy{margin-top:14px}.compact .card-copy h3{font-size:16px}.compact .date-row,.compact .badges .level{display:none}.compact footer{padding-top:11px}.compact .card-actions button:not(:last-child),.compact .card-actions a{display:none}.compact .icon-action span{display:none}.compact .icon-action{width:30px;padding:0;justify-content:center}
@media(max-width:1050px){.certificate-card.list{display:flex;min-height:250px}.certificate-card.list .card-copy{margin-top:20px}.certificate-card.list .date-row{margin-top:17px}.certificate-card.list footer{margin-top:auto;padding-top:14px;border-top:1px solid var(--line)}}
@media(max-width:480px){.icon-action span{display:none}.icon-action{width:30px;padding:0;justify-content:center}}
</style>


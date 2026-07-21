<script setup lang="ts">
import { Award, BadgeCheck, BookOpenCheck, BriefcaseBusiness, CalendarDays, Copyright, Download, Drama, ExternalLink, FileBadge, GraduationCap, Handshake, HeartHandshake, Languages, Lightbulb, Medal, Pencil, Trash2, Trophy } from 'lucide-vue-next'
import { computed } from 'vue'
import type { Certificate } from '@/types'
import { AWARD_TYPE_LABELS, CATEGORY_LABELS, LEVEL_LABELS } from '@/constants'

const props = withDefaults(defineProps<{ certificate: Certificate; compact?: boolean; layout?: 'grid' | 'list' }>(), { layout: 'grid' })
defineEmits<{ edit: [value: Certificate]; delete: [value: Certificate]; download: [value: Certificate] }>()
const icon = computed(()=>({ACADEMIC_EDUCATION:GraduationCap,LANGUAGE_EXAM:Languages,PROFESSIONAL_QUALIFICATION:BriefcaseBusiness,SKILL_CERTIFICATION:BadgeCheck,COMPETITION_AWARD:Trophy,INNOVATION_ENTREPRENEURSHIP:Lightbulb,TRAINING_CERTIFICATE:BookOpenCheck,HONORARY_TITLE:Medal,CULTURE_SPORTS:Drama,SOCIAL_PRACTICE:Handshake,VOLUNTEER_SERVICE:HeartHandshake,INTELLECTUAL_PROPERTY:Copyright,OTHER:Award})[props.certificate.category])
const categoryClass = computed(()=>`cat-${props.certificate.category.toLowerCase().replace(/_/g,'-')}`)
</script>

<template>
  <article class="certificate-card" :class="[categoryClass, layout, {compact}]">
    <span class="category-icon"><component :is="icon" :size="20"/></span>

    <div class="badges">
      <span class="award-type">{{ AWARD_TYPE_LABELS[certificate.awardType] }}</span>
      <span class="level">{{ LEVEL_LABELS[certificate.level] }}</span>
    </div>

    <div class="card-copy">
      <small>{{ CATEGORY_LABELS[certificate.category] }}</small>
      <h3>{{ certificate.name }}</h3>
      <p>{{ certificate.issuer }}</p>
    </div>

    <div class="date-row">
      <CalendarDays :size="15"/>
      <span>{{ certificate.issueDate }} 获得</span>
      <template v-if="certificate.expiryDate"><i/><span>有效期至 {{ certificate.expiryDate }}</span></template>
    </div>

    <div class="attachment" :class="{muted:!certificate.hasAttachment}">
      <FileBadge :size="15"/>
      <span>{{ certificate.hasAttachment ? certificate.fileName : '暂无附件' }}</span>
    </div>

    <div class="card-actions">
      <a v-if="certificate.credentialUrl" class="icon-action" :href="certificate.credentialUrl" target="_blank" rel="noopener" title="官网验证"><ExternalLink :size="15"/><span>验证</span></a>
      <button v-if="certificate.hasAttachment" class="icon-action" title="下载附件" @click="$emit('download',certificate)"><Download :size="15"/><span>下载</span></button>
      <button class="icon-action" title="编辑" @click="$emit('edit',certificate)"><Pencil :size="15"/><span>编辑</span></button>
      <button class="icon-action danger" title="移入回收站" @click="$emit('delete',certificate)"><Trash2 :size="15"/><span>回收站</span></button>
    </div>
  </article>
</template>

<style scoped>
/* ========== Grid card (default) ========== */
.certificate-card{
  position:relative;
  min-height:250px;
  height:100%;
  padding:22px;
  display:grid;
  grid-template-columns:1fr auto;
  grid-template-rows:auto auto 1fr auto;
  grid-template-areas:
    "icon badges"
    "copy copy"
    "date date"
    "attach actions";
  align-content:stretch;
  column-gap:12px;
  row-gap:0;
  background:var(--white);
  border:1px solid var(--line);
  border-left:3px solid var(--accent,#2f7d78);
  border-radius:14px;
  transition:border-color .15s;
}
.certificate-card:hover{
  border-color:color-mix(in srgb,var(--accent) 52%,var(--line));
  border-left-color:var(--accent);
}
.cat-academic-education{--accent:#315c82}
.cat-language-exam{--accent:#2f7d78}
.cat-professional-qualification{--accent:#a84b3e}
.cat-skill-certification{--accent:#8b6f35}
.cat-competition-award{--accent:#c9503d}
.cat-innovation-entrepreneurship{--accent:#b27935}
.cat-training-certificate{--accent:#4f718e}
.cat-culture-sports{--accent:#735f91}
.cat-social-practice{--accent:#397f9b}
.cat-volunteer-service{--accent:#2f7d78}
.cat-honorary-title{--accent:#b89431}
.cat-intellectual-property{--accent:#6b5a8b}
.cat-other{--accent:#687a88}

.category-icon{
  grid-area:icon;
  width:40px;
  height:40px;
  display:grid;
  place-items:center;
  border-radius:10px;
  background:color-mix(in srgb,var(--accent) 10%,transparent);
  color:var(--accent);
  flex-shrink:0;
}
.badges{
  grid-area:badges;
  display:flex;
  gap:8px;
  flex-wrap:wrap;
  justify-content:flex-end;
  align-items:center;
}
.badges span{
  padding:5px 9px;
  border-radius:6px;
  font-size:10px;
  font-weight:700;
  line-height:1.2;
}
.award-type{
  background:color-mix(in srgb,var(--accent) 10%,transparent);
  color:var(--accent);
}
.level{
  border:1px solid var(--line);
  color:var(--ink-2);
}

.card-copy{
  grid-area:copy;
  margin-top:18px;
  min-width:0;
}
.card-copy small{
  display:inline-block;
  color:var(--accent);
  font-size:11px;
  font-weight:700;
  letter-spacing:.02em;
}
.card-copy h3{
  margin:10px 0 8px;
  font-size:18px;
  line-height:1.5;
  font-weight:650;
  display:-webkit-box;
  -webkit-line-clamp:2;
  -webkit-box-orient:vertical;
  overflow:hidden;
}
.card-copy p{
  margin:0;
  color:var(--ink-2);
  font-size:13px;
  line-height:1.5;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
}

.date-row{
  grid-area:date;
  display:flex;
  align-items:center;
  gap:8px;
  margin-top:16px;
  align-self:start;
  color:#718294;
  font-size:12px;
  line-height:1.4;
}
.date-row i{
  width:3px;
  height:3px;
  border-radius:50%;
  background:#a8b1b7;
}

.attachment{
  grid-area:attach;
  display:flex;
  align-items:center;
  gap:7px;
  margin-top:0;
  padding-top:16px;
  border-top:1px solid var(--line);
  color:var(--teal);
  font-size:12px;
  min-width:0;
  align-self:stretch;
}
.attachment span{
  max-width:140px;
  white-space:nowrap;
  overflow:hidden;
  text-overflow:ellipsis;
}
.attachment.muted{color:#96a0a8}

.card-actions{
  grid-area:actions;
  display:flex;
  gap:6px;
  flex-shrink:0;
  margin-top:0;
  padding-top:16px;
  border-top:1px solid var(--line);
  align-self:stretch;
  align-items:center;
  justify-content:flex-end;
}
.icon-action{
  height:34px;
  padding:0 9px;
  display:inline-flex;
  align-items:center;
  gap:5px;
  border:1px solid transparent;
  border-radius:8px;
  background:transparent;
  color:var(--ink-2);
  font-size:11px;
  cursor:pointer;
  white-space:nowrap;
  flex-shrink:0;
}
.icon-action:hover{
  border-color:var(--line);
  background:var(--paper);
  color:var(--ink);
}
.icon-action.danger:hover{
  border-color:#efd0c9;
  background:#f9eeeb;
  color:var(--red);
}

/* ========== List row (distinct from grid) ========== */
.certificate-card.list{
  min-height:0;
  height:auto;
  padding:14px 16px;
  grid-template-columns:44px minmax(0,1.6fr) minmax(160px,.85fr) auto;
  grid-template-rows:auto auto;
  grid-template-areas:
    "icon copy badges actions"
    "icon copy meta actions";
  column-gap:16px;
  row-gap:6px;
  align-items:center;
  align-content:center;
  border-radius:12px;
}
.list .category-icon{
  grid-row:1 / span 2;
  width:42px;
  height:42px;
  border-radius:10px;
  align-self:center;
}
.list .badges{
  grid-area:badges;
  justify-content:flex-end;
  align-self:end;
  gap:6px;
}
.list .card-copy{
  grid-area:copy;
  margin:0;
  padding-right:0;
  align-self:center;
}
.list .card-copy small{font-size:10px}
.list .card-copy h3{
  margin:4px 0 4px;
  font-size:16px;
  -webkit-line-clamp:1;
  line-height:1.4;
}
.list .card-copy p{font-size:12px}
.list .date-row{
  grid-area:meta;
  margin:0;
  justify-self:start;
  align-self:start;
}
.list .attachment{
  grid-area:meta;
  margin:0;
  padding:0;
  border:0;
  align-self:start;
  justify-self:end;
}
.list .attachment span{max-width:150px}
.list .card-actions{
  grid-area:actions;
  grid-row:1 / span 2;
  margin:0;
  padding:0;
  border:0;
  flex-direction:row;
  align-items:center;
  justify-content:flex-end;
  gap:4px;
  align-self:center;
}
.list .icon-action{
  height:32px;
  padding:0 8px;
}

/* Compact (dashboard) */
.compact{
  min-height:unset;
  height:auto;
  padding:16px;
  grid-template-rows:auto auto auto;
  grid-template-areas:
    "icon badges"
    "copy copy"
    "attach actions";
}
.compact .card-copy{margin-top:14px}
.compact .card-copy h3{font-size:16px}
.compact .date-row,
.compact .badges .level{display:none}
.compact .attachment,
.compact .card-actions{
  margin-top:12px;
  padding-top:12px;
}
.compact .card-actions a,
.compact .card-actions button:not(:last-child){display:none}
.compact .icon-action span{display:none}
.compact .icon-action{width:34px;padding:0;justify-content:center}

/* Tablet list: still row-like, slightly stacked meta */
@media(max-width:1050px){
  .certificate-card.list{
    grid-template-columns:42px minmax(0,1fr) auto;
    grid-template-areas:
      "icon copy actions"
      "icon meta actions";
    column-gap:12px;
    row-gap:4px;
    padding:14px;
  }
  .list .badges{
    position:static;
    grid-area:meta;
    justify-self:start;
    align-self:center;
    margin:0;
    gap:6px;
  }
  .list .card-copy{
    padding-right:0;
    align-self:end;
  }
  .list .card-copy h3{-webkit-line-clamp:1;margin:2px 0 2px}
  .list .date-row{
    grid-area:meta;
    margin-left:auto;
    justify-self:end;
  }
  .list .attachment{display:none}
  .list .card-actions{
    flex-direction:row;
    flex-wrap:wrap;
    max-width:120px;
    justify-content:flex-end;
  }
  .list .icon-action span{display:none}
  .list .icon-action{
    width:34px;
    min-width:34px;
    padding:0;
    justify-content:center;
  }
}

/* Mobile grid cards */
@media(max-width:650px){
  .certificate-card:not(.list){
    min-height:0;
    height:auto;
    padding:18px 16px 16px;
    grid-template-columns:auto 1fr;
    grid-template-rows:auto auto auto auto;
    grid-template-areas:
      "icon badges"
      "copy copy"
      "date date"
      "attach actions";
    text-align:left;
  }
  .certificate-card:not(.list) .category-icon{width:38px;height:38px}
  .certificate-card:not(.list) .badges{justify-content:flex-start}
  .certificate-card:not(.list) .card-copy{margin-top:16px}
  .certificate-card:not(.list) .card-copy small{display:block}
  .certificate-card:not(.list) .card-copy h3{margin:8px 0;font-size:17px;line-height:1.55}
  .certificate-card:not(.list) .date-row{margin-top:14px}
  .certificate-card:not(.list) .attachment,
  .certificate-card:not(.list) .card-actions{
    margin-top:18px;
    padding-top:14px;
  }
  .certificate-card:not(.list) .attachment span{max-width:min(42vw,150px)}
  .certificate-card:not(.list) .icon-action{
    height:36px;
    min-width:36px;
    padding:0 8px;
  }
}

/* Mobile list: dense single-row sheet */
@media(max-width:650px){
  .certificate-card.list{
    grid-template-columns:40px minmax(0,1fr) auto;
    grid-template-areas:
      "icon copy actions"
      "icon meta actions";
    padding:12px 12px;
    column-gap:10px;
    row-gap:3px;
    border-radius:12px;
    border-left-width:3px;
  }
  .list .category-icon{
    width:40px;
    height:40px;
  }
  .list .card-copy{align-self:end}
  .list .card-copy small{display:none}
  .list .card-copy h3{
    margin:0;
    font-size:15px;
    -webkit-line-clamp:1;
  }
  .list .card-copy p{
    font-size:11px;
    margin-top:2px;
  }
  .list .badges{
    grid-area:meta;
    justify-self:start;
    gap:4px;
  }
  .list .badges span{
    padding:3px 6px;
    font-size:9px;
  }
  .list .date-row{
    grid-area:meta;
    justify-self:end;
    font-size:11px;
    gap:4px;
  }
  .list .date-row svg{width:13px;height:13px}
  .list .attachment{display:none}
  .list .card-actions{
    flex-direction:column;
    gap:2px;
    max-width:none;
  }
  .list .icon-action{
    width:32px;
    min-width:32px;
    height:32px;
    padding:0;
    justify-content:center;
  }
  .list .icon-action span{display:none}
}

@media(max-width:480px){
  .certificate-card:not(.list) .icon-action span{display:none}
  .certificate-card:not(.list) .icon-action{
    width:36px;
    min-width:36px;
    padding:0;
    justify-content:center;
  }
  .list .date-row span{max-width:78px;overflow:hidden;text-overflow:ellipsis;white-space:nowrap}
}
</style>

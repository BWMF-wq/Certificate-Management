<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { FileCheck2, LoaderCircle, Paperclip, Trash2, Upload, X } from 'lucide-vue-next'
import { certificateApi } from '@/api/services'
import { AWARD_TYPE_OPTIONS, CATEGORY_OPTIONS, LEVEL_OPTIONS } from '@/constants'
import type { AwardType, Certificate, CertificatePayload, Category, Level } from '@/types'
import { useToast } from '@/composables/useToast'
import { errorMessage } from '@/api/client'

const props=defineProps<{open:boolean;certificate?:Certificate|null}>();const emit=defineEmits<{close:[];saved:[Certificate]}>()
const toast=useToast();const saving=ref(false);const removingFile=ref(false);const pickedFile=ref<File|null>(null);const fileInput=ref<HTMLInputElement|null>(null)
const today=new Date().toISOString().slice(0,10)
const form=reactive<CertificatePayload>({name:'',issuer:'',category:'HONOR',level:'UNIVERSITY',awardType:'INDIVIDUAL',issueDate:today,expiryDate:null,credentialNo:'',credentialUrl:'',description:''})
const editing=computed(()=>Boolean(props.certificate)); const title=computed(()=>editing.value?'编辑证书档案':'收录一份新证书')
watch(()=>props.open,(open)=>{if(open){const c=props.certificate;Object.assign(form,{name:c?.name||'',issuer:c?.issuer||'',category:(c?.category||'HONOR') as Category,level:(c?.level||'UNIVERSITY') as Level,awardType:(c?.awardType||'INDIVIDUAL') as AwardType,issueDate:c?.issueDate||today,expiryDate:c?.expiryDate||null,credentialNo:c?.credentialNo||'',credentialUrl:c?.credentialUrl||'',description:c?.description||''});pickedFile.value=null}})
function chooseFile(event:Event){const file=(event.target as HTMLInputElement).files?.[0]||null;if(file&&file.size>10*1024*1024){toast.error('附件不能超过 10MB');return}pickedFile.value=file}
async function removeExisting(){if(!props.certificate)return;removingFile.value=true;try{const{data}=await certificateApi.removeAttachment(props.certificate.id);emit('saved',data);toast.success('附件已移除')}catch(e){toast.error(errorMessage(e))}finally{removingFile.value=false}}
async function submit(){
  if(!form.name.trim()||!form.issuer.trim()||!form.issueDate)return toast.error('请填写证书名称、颁发机构和取得日期')
  if(form.expiryDate&&form.expiryDate<form.issueDate)return toast.error('到期日期不能早于取得日期')
  saving.value=true
  try{
    const payload={...form,expiryDate:form.expiryDate||null}
    let saved=(editing.value?await certificateApi.update(props.certificate!.id,payload):await certificateApi.create(payload)).data
    if(pickedFile.value)saved=(await certificateApi.upload(saved.id,pickedFile.value)).data
    toast.success(editing.value?'证书档案已更新':'证书已收入档案');emit('saved',saved);emit('close')
  }catch(e){toast.error(errorMessage(e))}finally{saving.value=false}
}
</script>

<template>
  <Transition name="modal"><div v-if="open" class="modal-backdrop" @click.self="$emit('close')"><section class="modal" role="dialog" aria-modal="true">
    <header class="modal-head"><div><small>{{ editing?'ARCHIVE UPDATE':'NEW ARCHIVE' }}</small><h2 class="display">{{ title }}</h2></div><button class="icon-btn" @click="$emit('close')"><X :size="19"/></button></header>
    <form class="modal-body" @submit.prevent="submit">
      <div class="form-grid"><div class="field span-2"><label>证书名称 *</label><input v-model="form.name" maxlength="120" placeholder="例如：大学英语六级证书"/></div><div class="field span-2"><label>颁发机构 *</label><input v-model="form.issuer" maxlength="120" placeholder="例如：教育部教育考试院"/></div>
        <div class="field"><label>成果类型 *</label><select v-model="form.category"><option v-for="[value,label] in CATEGORY_OPTIONS" :key="value" :value="value">{{label}}</option></select></div><div class="field"><label>荣誉级别 *</label><select v-model="form.level"><option v-for="[value,label] in LEVEL_OPTIONS" :key="value" :value="value">{{label}}</option></select></div>
        <div class="field"><label>奖项分类 *</label><select v-model="form.awardType"><option v-for="[value,label] in AWARD_TYPE_OPTIONS" :key="value" :value="value">{{label}}</option></select></div><div class="field"><label>取得日期 *</label><input v-model="form.issueDate" type="date" :max="today"/></div>
        <div class="field"><label>证书编号</label><input v-model="form.credentialNo" maxlength="100" placeholder="可选"/></div><div class="field"><label>有效期至</label><input v-model="form.expiryDate" type="date" :min="form.issueDate"/><small>大多数荣誉无需填写，仅在证书明确注明时记录</small></div>
        <div class="field span-2"><label>官网验证地址</label><input v-model="form.credentialUrl" type="url" maxlength="500" placeholder="https://..."/></div>
        <div class="field span-2"><label>备注</label><textarea v-model="form.description" maxlength="1000" placeholder="记录考试成绩、获奖说明或其他重要信息…"/></div>
      </div>
      <div class="upload-block"><div class="upload-copy"><Paperclip :size="20"/><div><b>证书附件</b><small>支持 PDF、JPG、PNG、WebP，最大 10MB</small></div></div>
        <div v-if="certificate?.hasAttachment&&!pickedFile" class="existing-file"><FileCheck2 :size="17"/><span>{{certificate.fileName}}</span><button type="button" :disabled="removingFile" @click="removeExisting"><Trash2 :size="15"/>移除</button></div>
        <button v-else type="button" class="file-picker" @click="fileInput?.click()"><Upload :size="18"/><span>{{pickedFile?.name||'选择本地附件'}}</span></button><input ref="fileInput" hidden type="file" accept=".pdf,.jpg,.jpeg,.png,.webp,application/pdf,image/jpeg,image/png,image/webp" @change="chooseFile"/>
      </div>
      <div class="form-actions"><button type="button" class="btn btn-secondary" :disabled="saving" @click="$emit('close')">取消</button><button class="btn btn-primary" :disabled="saving"><LoaderCircle v-if="saving" class="spin" :size="18"/>{{saving?'正在保存…':editing?'保存修改':'收入档案'}}</button></div>
    </form>
  </section></div></Transition>
</template>

<style scoped>
.modal-head small{display:block;color:var(--red);font-size:9px;font-weight:700;letter-spacing:.2em;margin-bottom:5px}.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:17px 15px}.span-2{grid-column:span 2}.upload-block{margin-top:22px;padding:17px;border:1px dashed rgba(23,50,77,.22);border-radius:13px;background:rgba(255,255,255,.32)}.upload-copy{display:flex;align-items:center;gap:10px}.upload-copy>svg{color:var(--teal)}.upload-copy b,.upload-copy small{display:block}.upload-copy b{font-size:13px}.upload-copy small{margin-top:4px;color:var(--ink-2);font-size:10px}.file-picker,.existing-file{width:100%;height:42px;margin-top:13px;padding:0 12px;border:1px solid var(--line);border-radius:10px;background:var(--white);display:flex;align-items:center;gap:9px;color:var(--ink-2);font-size:12px}.file-picker{justify-content:center;cursor:pointer}.file-picker:hover{border-color:var(--teal);color:var(--teal)}.existing-file span{flex:1;min-width:0;white-space:nowrap;overflow:hidden;text-overflow:ellipsis}.existing-file button{border:0;background:transparent;color:var(--red);font-size:11px;display:flex;gap:4px;align-items:center;cursor:pointer}.form-actions{display:flex;justify-content:flex-end;gap:10px;margin-top:25px}.spin{animation:spin .8s linear infinite}@keyframes spin{to{transform:rotate(360deg)}}@media(max-width:600px){.form-grid{grid-template-columns:1fr}.span-2{grid-column:span 1}}
</style>


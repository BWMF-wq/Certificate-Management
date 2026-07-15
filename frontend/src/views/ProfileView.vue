<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { CalendarDays, GraduationCap, LoaderCircle, Mail, Save, School, UserRound } from 'lucide-vue-next'
import { useAuthStore } from '@/stores/auth'
import { userApi } from '@/api/services'
import { useToast } from '@/composables/useToast'
import { errorMessage } from '@/api/client'

const auth=useAuthStore();const toast=useToast();const saving=ref(false)
const form=reactive({name:'',email:'',school:'',major:'',graduationYear:undefined as number|undefined})
watch(()=>auth.user,(user)=>{if(user)Object.assign(form,{name:user.name,email:user.email,school:user.school||'',major:user.major||'',graduationYear:user.graduationYear})},{immediate:true})
const initials=computed(()=>auth.user?.name?.slice(-2)||'同学');const joined=computed(()=>auth.user?.createdAt?new Date(auth.user.createdAt).toLocaleDateString('zh-CN',{year:'numeric',month:'long'}):'')
async function submit(){if(!form.name.trim()||!form.email.trim())return toast.error('姓名和邮箱不能为空');saving.value=true;try{const{data}=await userApi.update(form);auth.setUser(data);toast.success('个人信息已保存')}catch(e){toast.error(errorMessage(e))}finally{saving.value=false}}
</script>

<template>
  <div class="profile-page">
    <header><span class="eyebrow">账户</span><h1 class="page-title">个人信息</h1><p class="page-subtitle">查看和修改账户基本信息。</p></header>
    <div class="profile-grid">
      <aside class="identity-card panel">
        <div class="avatar-large">{{initials}}</div><h2>{{auth.user?.name}}</h2><p>{{auth.user?.studentId}}</p>
        <div class="identity-list"><div><Mail :size="15"/><span>{{auth.user?.email}}</span></div><div><School :size="15"/><span>{{auth.user?.school||'学校信息待完善'}}</span></div><div><GraduationCap :size="15"/><span>{{auth.user?.major||'专业信息待完善'}}</span></div><div><CalendarDays :size="15"/><span>{{joined}} 注册</span></div></div>
      </aside>
      <section class="panel form-panel"><div class="section-heading"><span><UserRound :size="19"/></span><div><h2>基本资料</h2><p>这些信息只会展示在你的个人档案中。</p></div></div>
        <form @submit.prevent="submit"><div class="form-grid"><div class="field"><label>姓名 *</label><input v-model="form.name" maxlength="50"/></div><div class="field readonly"><label>学号</label><input :value="auth.user?.studentId" disabled/><small>学号是唯一身份标识，无法自行修改</small></div><div class="field span-2"><label>联系邮箱 *</label><input v-model="form.email" type="email" maxlength="120"/></div><div class="field"><label>学校</label><input v-model="form.school" maxlength="100" placeholder="例如：XX 大学"/></div><div class="field"><label>专业</label><input v-model="form.major" maxlength="100" placeholder="例如：计算机科学与技术"/></div><div class="field"><label>预计毕业年份</label><input v-model.number="form.graduationYear" type="number" min="2000" max="2100" placeholder="2028"/></div></div><div class="save-row"><p>最后修改将自动保存至个人账户</p><button class="btn btn-primary" :disabled="saving"><LoaderCircle v-if="saving" class="spin" :size="17"/><Save v-else :size="17"/>{{saving?'保存中…':'保存信息'}}</button></div></form>
      </section>
    </div>
  </div>
</template>

<style scoped>
.profile-page{padding:clamp(28px,4vw,52px);max-width:1250px;margin:0 auto}.eyebrow{display:block;color:var(--ink-2);font-size:10px;letter-spacing:.08em;font-weight:600;margin-bottom:9px}.profile-grid{display:grid;grid-template-columns:300px 1fr;gap:18px;margin-top:28px;align-items:start}.identity-card{padding:28px 24px;color:var(--ink);box-shadow:none}.avatar-large{width:64px;height:64px;display:grid;place-items:center;border-radius:50%;background:rgba(23,50,77,.08);color:var(--ink);font-size:17px;font-weight:700}.identity-card h2{margin:17px 0 5px;font-size:21px}.identity-card>p{margin:0;color:var(--ink-2);font-size:11px}.identity-list{display:grid;gap:16px;margin-top:28px;padding-top:22px;border-top:1px solid var(--line)}.identity-list div{display:flex;gap:11px;align-items:center;color:var(--ink-2);font-size:11px}.identity-list svg{color:var(--teal)}.form-panel{padding:29px;box-shadow:none}.section-heading{display:flex;gap:12px;align-items:center;padding-bottom:22px;border-bottom:1px solid var(--line)}.section-heading>span{width:39px;height:39px;display:grid;place-items:center;border-radius:11px;background:rgba(47,125,120,.1);color:var(--teal)}.section-heading h2{margin:0;font-size:17px}.section-heading p{margin:4px 0 0;color:var(--ink-2);font-size:11px}.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:19px 16px;padding-top:24px}.span-2{grid-column:span 2}.readonly input{background:#ebe8df;color:#879099}.save-row{display:flex;align-items:center;justify-content:space-between;margin-top:30px;padding-top:20px;border-top:1px solid var(--line)}.save-row p{margin:0;color:#87939d;font-size:10px}.spin{animation:spin .8s linear infinite}@keyframes spin{to{transform:rotate(360deg)}}
@media(max-width:900px){.profile-grid{grid-template-columns:1fr}.identity-card{min-height:380px}.id-footer{position:static;margin-top:35px}}@media(max-width:600px){.profile-page{padding:24px 18px 45px}.form-panel{padding:21px}.form-grid{grid-template-columns:1fr}.span-2{grid-column:span 1}.save-row{align-items:flex-end}.save-row p{max-width:130px}}
</style>

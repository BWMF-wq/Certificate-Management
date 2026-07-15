<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { BadgeCheck, CalendarDays, GraduationCap, LoaderCircle, Mail, Save, School, UserRound } from 'lucide-vue-next'
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
    <header><span class="eyebrow">STUDENT PROFILE</span><h1 class="display page-title">个人信息</h1><p class="page-subtitle">完善档案信息，让你的学习履历更加完整。</p></header>
    <div class="profile-grid">
      <aside class="identity-card">
        <div class="id-pattern"/><div class="avatar-large">{{initials}}<BadgeCheck :size="21"/></div><h2>{{auth.user?.name}}</h2><p>{{auth.user?.studentId}}</p>
        <div class="identity-list"><div><Mail :size="15"/><span>{{auth.user?.email}}</span></div><div><School :size="15"/><span>{{auth.user?.school||'学校信息待完善'}}</span></div><div><GraduationCap :size="15"/><span>{{auth.user?.major||'专业信息待完善'}}</span></div><div><CalendarDays :size="15"/><span>{{joined}} 加入证途</span></div></div>
        <div class="id-footer"><span>STUDENT ARCHIVE</span><i/><small>NO. {{String(auth.user?.id||0).padStart(6,'0')}}</small></div>
      </aside>
      <section class="panel form-panel"><div class="section-heading"><span><UserRound :size="19"/></span><div><h2>基本资料</h2><p>这些信息只会展示在你的个人档案中。</p></div></div>
        <form @submit.prevent="submit"><div class="form-grid"><div class="field"><label>姓名 *</label><input v-model="form.name" maxlength="50"/></div><div class="field readonly"><label>学号</label><input :value="auth.user?.studentId" disabled/><small>学号是唯一身份标识，无法自行修改</small></div><div class="field span-2"><label>联系邮箱 *</label><input v-model="form.email" type="email" maxlength="120"/></div><div class="field"><label>学校</label><input v-model="form.school" maxlength="100" placeholder="例如：XX 大学"/></div><div class="field"><label>专业</label><input v-model="form.major" maxlength="100" placeholder="例如：计算机科学与技术"/></div><div class="field"><label>预计毕业年份</label><input v-model.number="form.graduationYear" type="number" min="2000" max="2100" placeholder="2028"/></div></div><div class="save-row"><p>最后修改将自动保存至个人账户</p><button class="btn btn-primary" :disabled="saving"><LoaderCircle v-if="saving" class="spin" :size="17"/><Save v-else :size="17"/>{{saving?'保存中…':'保存信息'}}</button></div></form>
      </section>
    </div>
  </div>
</template>

<style scoped>
.profile-page{padding:clamp(28px,4vw,52px);max-width:1250px;margin:0 auto;animation:rise .5s ease both}@keyframes rise{from{opacity:0;transform:translateY(8px)}}.eyebrow{display:block;color:var(--red);font-size:10px;letter-spacing:.18em;font-weight:700;margin-bottom:9px}.profile-grid{display:grid;grid-template-columns:330px 1fr;gap:18px;margin-top:31px;align-items:start}.identity-card{min-height:510px;padding:37px 27px 24px;color:white;background:var(--ink);border-radius:18px;overflow:hidden;position:relative;box-shadow:var(--shadow)}.id-pattern{position:absolute;right:-70px;top:-70px;width:230px;height:230px;border:1px solid rgba(255,255,255,.09);border-radius:50%;box-shadow:0 0 0 35px rgba(255,255,255,.025),0 0 0 70px rgba(255,255,255,.018)}.avatar-large{position:relative;width:84px;height:84px;display:grid;place-items:center;border-radius:50%;background:var(--gold);color:var(--ink);font-size:22px;font-weight:700;border:4px solid rgba(255,255,255,.12)}.avatar-large svg{position:absolute;right:-2px;bottom:-1px;color:#79b6a2;fill:var(--ink)}.identity-card h2{position:relative;margin:19px 0 5px;font-family:'DM Serif Display','Noto Sans SC';font-size:26px}.identity-card>p{margin:0;color:rgba(255,255,255,.45);font-size:11px;letter-spacing:.08em}.identity-list{display:grid;gap:16px;margin-top:40px;padding-top:23px;border-top:1px solid rgba(255,255,255,.12)}.identity-list div{display:flex;gap:11px;align-items:center;color:rgba(255,255,255,.61);font-size:11px}.identity-list svg{color:var(--gold);opacity:.85}.id-footer{position:absolute;left:27px;right:27px;bottom:23px;display:flex;align-items:center;gap:9px;color:rgba(255,255,255,.35)}.id-footer span,.id-footer small{font-size:7px;letter-spacing:.18em}.id-footer i{height:1px;flex:1;background:rgba(255,255,255,.12)}.form-panel{padding:29px;box-shadow:none}.section-heading{display:flex;gap:12px;align-items:center;padding-bottom:22px;border-bottom:1px solid var(--line)}.section-heading>span{width:39px;height:39px;display:grid;place-items:center;border-radius:11px;background:rgba(47,125,120,.1);color:var(--teal)}.section-heading h2{margin:0;font-size:17px}.section-heading p{margin:4px 0 0;color:var(--ink-2);font-size:11px}.form-grid{display:grid;grid-template-columns:1fr 1fr;gap:19px 16px;padding-top:24px}.span-2{grid-column:span 2}.readonly input{background:#ebe8df;color:#879099}.save-row{display:flex;align-items:center;justify-content:space-between;margin-top:30px;padding-top:20px;border-top:1px solid var(--line)}.save-row p{margin:0;color:#87939d;font-size:10px}.spin{animation:spin .8s linear infinite}@keyframes spin{to{transform:rotate(360deg)}}
@media(max-width:900px){.profile-grid{grid-template-columns:1fr}.identity-card{min-height:380px}.id-footer{position:static;margin-top:35px}}@media(max-width:600px){.profile-page{padding:24px 18px 45px}.form-panel{padding:21px}.form-grid{grid-template-columns:1fr}.span-2{grid-column:span 1}.save-row{align-items:flex-end}.save-row p{max-width:130px}}
</style>

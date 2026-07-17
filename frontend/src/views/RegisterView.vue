<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import { ArrowRight, LoaderCircle } from 'lucide-vue-next'
import AuthLayout from '@/layouts/AuthLayout.vue'
import { useAuthStore } from '@/stores/auth'
import { useToast } from '@/composables/useToast'
import { errorMessage } from '@/api/client'

const form = ref({ name:'', studentId:'', email:'', password:'', confirm:'' }); const loading=ref(false)
const strength = computed(()=> { const p=form.value.password; return [p.length>=8,/[A-Za-z]/.test(p),/\d/.test(p)].filter(Boolean).length })
const auth=useAuthStore();const toast=useToast();const router=useRouter()
async function submit(){
  if(!form.value.name.trim()||!form.value.studentId.trim()||!form.value.email.trim()||!form.value.password)return toast.error('请完整填写注册信息')
  if(form.value.password.length<8)return toast.error('密码至少需要 8 位')
  if(form.value.password!==form.value.confirm)return toast.error('两次输入的密码不一致')
  loading.value=true
  try{await auth.register(form.value);toast.success('档案创建成功');await router.push('/dashboard')}
  catch(error){toast.error(errorMessage(error))}finally{loading.value=false}
}
</script>

<template>
  <AuthLayout>
    <div class="auth-heading"><span>NEW HONOR ARCHIVE</span><h2 class="display">创建你的荣誉档案</h2><p>只需一分钟，开始整理荣誉证书</p></div>
    <form @submit.prevent="submit">
      <div class="row"><div class="field"><label>姓名</label><input v-model="form.name" autocomplete="name" placeholder="你的姓名" maxlength="50"/></div><div class="field"><label>学号</label><input v-model="form.studentId" autocomplete="username" placeholder="校园学号" maxlength="30"/></div></div>
      <div class="field"><label>校园邮箱</label><input v-model="form.email" type="email" autocomplete="email" placeholder="name@university.edu.cn"/></div>
      <div class="field"><label>设置密码</label><input v-model="form.password" type="password" autocomplete="new-password" placeholder="至少 8 位，建议包含字母与数字"/><div class="strength"><i v-for="n in 3" :key="n" :class="{on:strength>=n}"/><small>{{ ['请输入密码','强度较弱','强度适中','强度良好'][strength] }}</small></div></div>
      <div class="field"><label>确认密码</label><input v-model="form.confirm" type="password" autocomplete="new-password" placeholder="再次输入密码"/></div>
      <button class="btn btn-primary submit" :disabled="loading"><LoaderCircle v-if="loading" class="spin" :size="18"/><template v-else>创建档案<ArrowRight :size="18"/></template></button>
    </form>
    <p class="switch">已有学生档案？<RouterLink to="/login">返回登录</RouterLink></p>
  </AuthLayout>
</template>

<style scoped>
.auth-heading span{font-size:10px;letter-spacing:.23em;color:var(--red);font-weight:700}.auth-heading h2{margin:13px 0 9px;font-size:34px;line-height:1.2}.auth-heading p{margin:0 0 29px;color:var(--ink-2);font-size:14px}form{display:grid;gap:15px}.row{display:grid;grid-template-columns:1fr 1fr;gap:12px}.submit{width:100%;height:48px;margin-top:5px}.switch{text-align:center;color:var(--ink-2);font-size:13px;margin:21px 0 0}.switch a{color:var(--red);font-weight:600;margin-left:4px}.strength{display:grid;grid-template-columns:repeat(3,1fr) auto;gap:4px;align-items:center;margin-top:2px}.strength i{height:3px;border-radius:2px;background:#ddd7ca}.strength i.on{background:var(--teal)}.strength small{margin-left:5px;min-width:50px;font-size:10px}.spin{animation:spin .8s linear infinite}@keyframes spin{to{transform:rotate(360deg)}}@media(max-width:460px){.row{grid-template-columns:1fr}}
</style>

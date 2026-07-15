<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowRight, Eye, EyeOff, LoaderCircle } from 'lucide-vue-next'
import AuthLayout from '@/layouts/AuthLayout.vue'
import { useAuthStore } from '@/stores/auth'
import { errorMessage } from '@/api/client'
import { useToast } from '@/composables/useToast'

const account = ref(''); const password = ref(''); const showPassword = ref(false); const loading = ref(false)
const auth = useAuthStore(); const router = useRouter(); const route = useRoute(); const toast = useToast()
async function submit() {
  if (!account.value.trim() || !password.value) return toast.error('请输入账号和密码')
  loading.value = true
  try { await auth.login(account.value.trim(), password.value); toast.success('欢迎回来'); await router.push((route.query.redirect as string) || '/dashboard') }
  catch (error) { toast.error(errorMessage(error)) } finally { loading.value = false }
}
</script>

<template>
  <AuthLayout>
    <div class="auth-heading"><span>WELCOME BACK</span><h2 class="display">继续你的成长档案</h2><p>使用学号或校园邮箱登录</p></div>
    <form @submit.prevent="submit">
      <div class="field"><label for="account">学号 / 邮箱</label><input id="account" v-model="account" autocomplete="username" placeholder="例如：20260001" /></div>
      <div class="field"><label for="password">密码</label><div class="password-input"><input id="password" v-model="password" :type="showPassword?'text':'password'" autocomplete="current-password" placeholder="输入你的密码" /><button type="button" :aria-label="showPassword?'隐藏密码':'显示密码'" @click="showPassword=!showPassword"><EyeOff v-if="showPassword" :size="18"/><Eye v-else :size="18"/></button></div></div>
      <button class="btn btn-primary submit" :disabled="loading"><LoaderCircle v-if="loading" class="spin" :size="18"/><template v-else>登录<ArrowRight :size="18"/></template></button>
    </form>
    <p class="switch">还没有账号？<RouterLink to="/register">创建学生档案</RouterLink></p>
  </AuthLayout>
</template>

<style scoped>
.auth-heading span{font-size:10px;letter-spacing:.23em;color:var(--red);font-weight:700}.auth-heading h2{margin:13px 0 9px;font-size:34px;line-height:1.2}.auth-heading p{margin:0 0 35px;color:var(--ink-2);font-size:14px}form{display:grid;gap:21px}.password-input{position:relative}.password-input input{padding-right:46px}.password-input button{position:absolute;right:7px;top:4px;width:36px;height:36px;display:grid;place-items:center;border:0;background:transparent;cursor:pointer;color:var(--ink-2)}.submit{width:100%;height:48px;margin-top:6px}.switch{text-align:center;color:var(--ink-2);font-size:13px;margin:25px 0 0}.switch a{color:var(--red);font-weight:600;margin-left:4px}.spin{animation:spin .8s linear infinite}@keyframes spin{to{transform:rotate(360deg)}}
</style>


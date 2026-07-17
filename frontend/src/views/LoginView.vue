<script setup lang="ts">
import { ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ArrowRight, Eye, EyeOff, LoaderCircle, ShieldCheck } from 'lucide-vue-next'
import AuthLayout from '@/layouts/AuthLayout.vue'
import { useAuthStore } from '@/stores/auth'
import { errorMessage } from '@/api/client'
import { useToast } from '@/composables/useToast'

const account = ref(''); const password = ref(''); const showPassword = ref(false); const acceptedTerms = ref(false); const loading = ref(false)
const auth = useAuthStore(); const router = useRouter(); const route = useRoute(); const toast = useToast()
async function submit() {
  if (!account.value.trim() || !password.value) return toast.error('请输入账号和密码')
  if (!acceptedTerms.value) return toast.error('请先阅读并同意证书记录与真实性责任条款')
  loading.value = true
  try { await auth.login(account.value.trim(), password.value); toast.success('欢迎回来'); await router.push((route.query.redirect as string) || '/dashboard') }
  catch (error) { toast.error(errorMessage(error)) } finally { loading.value = false }
}
</script>

<template>
  <AuthLayout>
    <div class="auth-heading"><span>WELCOME BACK</span><h2 class="display">管理你的证书</h2><p>使用学号或校园邮箱登录</p></div>
    <form @submit.prevent="submit">
      <div class="field"><label for="account">学号 / 邮箱</label><input id="account" v-model="account" autocomplete="username" placeholder="例如：20260001" /></div>
      <div class="field"><label for="password">密码</label><div class="password-input"><input id="password" v-model="password" :type="showPassword?'text':'password'" autocomplete="current-password" placeholder="输入你的密码" /><button type="button" :aria-label="showPassword?'隐藏密码':'显示密码'" @click="showPassword=!showPassword"><EyeOff v-if="showPassword" :size="18"/><Eye v-else :size="18"/></button></div></div>
      <label class="terms-card">
        <input v-model="acceptedTerms" type="checkbox"/>
        <span class="terms-check"><ShieldCheck :size="15"/></span>
        <span class="terms-copy">
          <b>证书记录与真实性责任条款</b>
          <small>本平台仅用于记录证书，所录入证书的真实性由用户本人保证；不得将相关记录用于虚假营销、欺骗或隐瞒事实。</small>
          <small class="disclaimer"><strong>免责说明：</strong>平台仅提供记录与管理工具，不对用户上传内容作实质性审查或真实性背书。因用户提交虚假、不完整或侵权信息，或将记录用于不当用途造成的责任，由用户依法承担；平台依法应承担的责任不因此排除。</small>
        </span>
      </label>
      <button class="btn btn-primary submit" :disabled="loading"><LoaderCircle v-if="loading" class="spin" :size="18"/><template v-else>登录<ArrowRight :size="18"/></template></button>
    </form>
    <p class="switch">还没有账号？<RouterLink to="/register">创建账户</RouterLink></p>
  </AuthLayout>
</template>

<style scoped>
.auth-heading span{font-size:10px;letter-spacing:.23em;color:var(--red);font-weight:700}.auth-heading h2{margin:13px 0 9px;font-size:34px;line-height:1.2}.auth-heading p{margin:0 0 35px;color:var(--ink-2);font-size:14px}form{display:grid;gap:21px}.password-input{position:relative}.password-input input{padding-right:46px}.password-input button{position:absolute;right:7px;top:4px;width:36px;height:36px;display:grid;place-items:center;border:0;background:transparent;cursor:pointer;color:var(--ink-2)}
.terms-card{position:relative;padding:13px 14px;border:1px solid var(--line);border-radius:13px;background:color-mix(in srgb,var(--white) 62%,transparent);display:grid;grid-template-columns:21px 1fr;gap:10px;align-items:start;cursor:pointer;transition:.2s}.terms-card:hover{border-color:rgba(47,125,120,.34);background:color-mix(in srgb,var(--white) 84%,transparent)}.terms-card input{position:absolute;opacity:0;pointer-events:none}.terms-check{width:21px;height:21px;border:1px solid rgba(23,50,77,.24);border-radius:6px;display:grid;place-items:center;color:transparent;background:var(--paper);transition:.2s}.terms-card input:checked+.terms-check{border-color:var(--teal);background:var(--teal);color:white;box-shadow:0 0 0 3px rgba(47,125,120,.1)}.terms-card:focus-within{outline:3px solid rgba(47,125,120,.12);outline-offset:2px}.terms-copy b,.terms-copy small{display:block}.terms-copy b{font-size:11px;line-height:1.5}.terms-copy small{margin-top:4px;color:var(--ink-2);font-size:9px;line-height:1.65}.terms-copy .disclaimer{margin-top:7px;padding-top:7px;border-top:1px solid var(--line);color:#667989}.terms-copy .disclaimer strong{color:var(--ink-2);font-weight:700}.submit{width:100%;height:48px;margin-top:0}.switch{text-align:center;color:var(--ink-2);font-size:13px;margin:25px 0 0}.switch a{color:var(--red);font-weight:600;margin-left:4px}.spin{animation:spin .8s linear infinite}@keyframes spin{to{transform:rotate(360deg)}}
</style>


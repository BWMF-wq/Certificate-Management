import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { authApi, userApi } from '@/api/services'
import type { AuthResponse, User } from '@/types'

const TOKEN_KEY = 'certificate_token'
const USER_KEY = 'certificate_user'

export const useAuthStore = defineStore('auth', () => {
  const token = ref(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref<User | null>(readUser())
  const isAuthenticated = computed(() => Boolean(token.value))

  function readUser(): User | null {
    try { return JSON.parse(localStorage.getItem(USER_KEY) || 'null') } catch { return null }
  }

  function persist(response: AuthResponse) {
    token.value = response.token
    user.value = response.user
    localStorage.setItem(TOKEN_KEY, response.token)
    localStorage.setItem(USER_KEY, JSON.stringify(response.user))
  }

  async function login(account: string, password: string) {
    const { data } = await authApi.login({ account, password }); persist(data)
  }

  async function register(payload: { name: string; studentId: string; email: string; password: string }) {
    const { data } = await authApi.register(payload); persist(data)
  }

  async function refresh() {
    if (!token.value) return
    const { data } = await userApi.me(); user.value = data; localStorage.setItem(USER_KEY, JSON.stringify(data))
  }

  function setUser(value: User) { user.value = value; localStorage.setItem(USER_KEY, JSON.stringify(value)) }

  function logout() {
    token.value = ''; user.value = null
    localStorage.removeItem(TOKEN_KEY); localStorage.removeItem(USER_KEY)
  }

  return { token, user, isAuthenticated, login, register, refresh, setUser, logout }
})


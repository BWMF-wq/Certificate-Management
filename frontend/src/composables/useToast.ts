import { reactive } from 'vue'

export type ToastType = 'success' | 'error' | 'info'
export interface Toast { id: number; message: string; type: ToastType }

const state = reactive<{ items: Toast[] }>({ items: [] })
let id = 0

export function useToast() {
  function show(message: string, type: ToastType = 'info') {
    const toast = { id: ++id, message, type }
    state.items.push(toast)
    window.setTimeout(() => remove(toast.id), 3400)
  }
  function remove(toastId: number) { state.items = state.items.filter(item => item.id !== toastId) }
  return { toasts: state, show, success: (m: string) => show(m, 'success'), error: (m: string) => show(m, 'error'), remove }
}


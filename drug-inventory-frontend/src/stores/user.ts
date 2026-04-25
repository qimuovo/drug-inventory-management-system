import { computed, ref } from 'vue'
import { defineStore } from 'pinia'

const TOKEN_KEY = 'drug_inventory_token'
const USER_KEY = 'drug_inventory_user'

export const useUserStore = defineStore('user', () => {
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) ?? '')
  const userInfo = ref<API.UserVO | null>(
    localStorage.getItem(USER_KEY) ? (JSON.parse(localStorage.getItem(USER_KEY) as string) as API.UserVO) : null,
  )

  const isLogin = computed(() => !!token.value)

  const setLogin = (newToken: string, newUserInfo: API.UserVO | null) => {
    token.value = newToken
    userInfo.value = newUserInfo
    localStorage.setItem(TOKEN_KEY, newToken)
    if (newUserInfo) {
      localStorage.setItem(USER_KEY, JSON.stringify(newUserInfo))
    } else {
      localStorage.removeItem(USER_KEY)
    }
  }

  const logout = () => {
    token.value = ''
    userInfo.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  return { token, userInfo, isLogin, setLogin, logout }
})


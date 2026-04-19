import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useUserStore = defineStore('user', () => {
  const token = ref(localStorage.getItem('token') || '')
  const userId = ref(localStorage.getItem('userId') || '')
  const username = ref(localStorage.getItem('username') || '')
  const realName = ref(localStorage.getItem('realName') || '')
  const roleCode = ref(localStorage.getItem('roleCode') || '')
  const roleName = ref(localStorage.getItem('roleName') || '')

  function setUser(data) {
    token.value = data.token || ''
    userId.value = data.userId || ''
    username.value = data.username || ''
    realName.value = data.realName || ''
    roleCode.value = data.roleCode || ''
    roleName.value = data.roleName || ''

    // 持久化
    localStorage.setItem('token', token.value)
    localStorage.setItem('userId', userId.value)
    localStorage.setItem('username', username.value)
    localStorage.setItem('realName', realName.value)
    localStorage.setItem('roleCode', roleCode.value)
    localStorage.setItem('roleName', roleName.value)
  }

  function logout() {
    token.value = ''
    userId.value = ''
    username.value = ''
    realName.value = ''
    roleCode.value = ''
    roleName.value = ''

    localStorage.removeItem('token')
    localStorage.removeItem('userId')
    localStorage.removeItem('username')
    localStorage.removeItem('realName')
    localStorage.removeItem('roleCode')
    localStorage.removeItem('roleName')
  }

  return {
    token,
    userId,
    username,
    realName,
    roleCode,
    roleName,
    setUser,
    logout
  }
})

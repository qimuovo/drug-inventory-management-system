<template>
  <div id="loginPage">
    <el-row justify="center" align="middle" style="min-height: 100vh">
      <el-col :xs="22" :sm="14" :md="10" :lg="6">
        <div class="title">药品库存管理系统</div>
        <el-card>
          <el-form :model="form" label-width="80px" @submit.prevent>
            <el-form-item label="账号">
              <el-input v-model="form.account" placeholder="请输入账号" />
            </el-form-item>
            <el-form-item label="密码">
              <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" style="width: 100%" :loading="loading" @click="handleLogin">登录</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { loginUsingPost } from '@/api/userController'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const form = reactive({
  account: '',
  password: '',
})

const loading = ref(false)

const handleLogin = async () => {
  if (loading.value) return
  loading.value = true
  try {
    const res = await loginUsingPost({
      account: form.account,
      password: form.password,
    })
    const { code, data, message } = res.data ?? {}
    if (code !== 0 || !data?.token) {
      ElMessage.error(message ?? '登录失败')
      return
    }
    userStore.setLogin(data.token, data.userInfo ?? null)
    ElMessage.success('登录成功')

    const redirect = typeof route.query.redirect === 'string' ? route.query.redirect : ''
    if (redirect) {
      await router.push(redirect)
      return
    }
    await router.push('/manufacturer')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
#loginPage {
  min-height: 100vh;
  background: #eaf3ff;
}

.title {
  text-align: center;
  font-size: 20px;
  font-weight: 600;
  margin-bottom: 12px;
  color: #1f4a7a;
}
</style>

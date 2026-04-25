<template>
  <div id="appHeader">
    <div class="left">
      <el-button text circle @click="emit('toggle-collapse')">
        <el-icon :size="18">
          <Expand v-if="collapsed" />
          <Fold v-else />
        </el-icon>
      </el-button>
      <span class="pageTitle">{{ pageTitle }}</span>
    </div>

    <div class="right">
      <el-dropdown trigger="click" @command="handleCommand">
        <div class="userEntry">
          <el-avatar :src="avatarUrl" :size="32">
            {{ avatarText }}
          </el-avatar>
          <span class="userName">{{ displayName }}</span>
        </div>
        <template #dropdown>
          <el-dropdown-menu>
            <el-dropdown-item command="logout">退出登录</el-dropdown-item>
          </el-dropdown-menu>
        </template>
      </el-dropdown>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { Expand, Fold } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const props = defineProps<{
  collapsed: boolean
}>()

const emit = defineEmits<{
  (e: 'toggle-collapse'): void
}>()

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const displayName = computed(() => userStore.userInfo?.userName || userStore.userInfo?.userAccount || '未登录')
const avatarUrl = computed(() => userStore.userInfo?.avatar || '')
const avatarText = computed(() => (displayName.value ? displayName.value.slice(0, 1) : 'U'))
const pageTitle = computed(() => (typeof route.meta?.title === 'string' ? route.meta.title : '系统首页'))

const handleCommand = async (command: string | number | object) => {
  if (command === 'logout') {
    userStore.logout()
    await router.push('/login')
  }
}
</script>

<style scoped>
#appHeader {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  border-bottom: 1px solid var(--el-border-color-light);
  background: var(--el-bg-color);
}

.left {
  display: inline-flex;
  align-items: center;
  gap: 12px;
}

.pageTitle {
  font-size: 16px;
  font-weight: 600;
  color: var(--el-text-color-primary);
}

.userEntry {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  user-select: none;
}

.userName {
  font-size: 14px;
  color: var(--el-text-color-primary);
}
</style>


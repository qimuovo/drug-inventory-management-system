<template>
  <div id="appSidebar">
    <div class="logo">
      <span class="logoText" v-if="!collapsed">药品库存系统</span>
      <span class="logoText" v-else>药</span>
    </div>

    <el-menu
      :default-active="activeMenu"
      :collapse="collapsed"
      :collapse-transition="false"
      router
      background-color="#1f2937"
      text-color="#e5e7eb"
      active-text-color="#60a5fa"
      class="menu"
    >
      <el-menu-item index="/manufacturer">
        <el-icon><OfficeBuilding /></el-icon>
        <template #title>厂家管理</template>
      </el-menu-item>
      <el-menu-item index="/drug">
        <el-icon><Box /></el-icon>
        <template #title>药品管理</template>
      </el-menu-item>
      <el-menu-item index="/inbound">
        <el-icon><Download /></el-icon>
        <template #title>药品入库管理</template>
      </el-menu-item>
      <el-menu-item index="/inbound-return">
        <el-icon><RefreshLeft /></el-icon>
        <template #title>入库退货管理</template>
      </el-menu-item>
    </el-menu>
  </div>
</template>

<script setup lang="ts">
import { OfficeBuilding, Box, Download, RefreshLeft } from '@element-plus/icons-vue'

defineProps<{
  collapsed: boolean
  activeMenu: string
}>()
</script>

<style scoped>
#appSidebar {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: #1f2937;
}

.logo {
  height: 56px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-bottom: 1px solid #374151;
}

.logoText {
  color: #f9fafb;
  font-size: 16px;
  font-weight: 600;
}

.menu {
  border-right: none;
  flex: 1;
}
</style>


<template>
  <div id="appFooter">
    <span>© {{ year }} 药品库存管理系统</span>
  </div>
</template>

<script setup lang="ts">
const year = new Date().getFullYear()
</script>

<style scoped>
#appFooter {
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-top: 1px solid var(--el-border-color-light);
  color: var(--el-text-color-secondary);
  background: var(--el-bg-color);
}
</style>


<template>
  <div id="manufacturerPage">
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="name" label="厂家名称" />
      <el-table-column prop="contact" label="联系人" />
      <el-table-column prop="phone" label="联系电话" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

type ManufacturerRow = {
  name: string
  contact: string
  phone: string
}

const tableData = ref<ManufacturerRow[]>([
  { name: '示例厂家 A', contact: '张三', phone: '13800000000' },
  { name: '示例厂家 B', contact: '李四', phone: '13900000000' },
])
</script>

<style scoped>
#manufacturerPage {
}
</style>

<template>
  <div id="drugPage">
    <el-table :data="tableData" border style="width: 100%">
      <el-table-column prop="name" label="药品名称" />
      <el-table-column prop="spec" label="规格" />
      <el-table-column prop="stock" label="库存" />
    </el-table>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'

type DrugRow = {
  name: string
  spec: string
  stock: number
}

const tableData = ref<DrugRow[]>([
  { name: '示例药品 A', spec: '10mg*10片', stock: 100 },
  { name: '示例药品 B', spec: '20mg*12片', stock: 50 },
])
</script>

<style scoped>
#drugPage {
}
</style>

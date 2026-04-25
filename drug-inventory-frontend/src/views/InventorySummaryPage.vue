<template>
  <div id="inventorySummaryPage">
    <el-card shadow="never" class="searchCard">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="药品名称">
          <el-input
            v-model="searchForm.drugName"
            clearable
            placeholder="请输入药品名称"
            @keyup.enter="handleRefresh"
          />
        </el-form-item>
        <el-form-item label="药品编号">
          <el-input
            v-model="searchForm.drugCode"
            clearable
            placeholder="请输入药品编号"
            @keyup.enter="handleRefresh"
          />
        </el-form-item>
        <el-form-item label="生产厂家">
          <el-input
            v-model="searchForm.manufacturerName"
            clearable
            placeholder="请输入生产厂家"
            @keyup.enter="handleRefresh"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleRefresh">刷新</el-button>
          <el-button @click="handleExport">导出</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="tableCard">
      <el-table v-loading="tableLoading" :data="tableData" border style="width: 100%" @sort-change="handleSortChange">
        <el-table-column prop="drugId" label="药品id" width="90" />
        <el-table-column prop="drugCode" label="药品编码" min-width="150" sortable="custom" />
        <el-table-column prop="drugName" label="药品名称" min-width="180" sortable="custom" />
        <el-table-column prop="specification" label="规格" min-width="160" show-overflow-tooltip />
        <el-table-column prop="manufacturerName" label="生产厂家" min-width="180" show-overflow-tooltip />
        <el-table-column prop="inboundQuantity" label="入库数量" width="110" />
        <el-table-column prop="inboundReturnQuantity" label="入库退货数量" width="150" />
        <el-table-column prop="outboundQuantity" label="出库数量" width="110" />
        <el-table-column prop="outboundReturnQuantity" label="出库退货数量" width="150" />
        <el-table-column prop="currentInventory" label="当前库存" min-width="120" sortable="custom" />
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="pageState.current"
          v-model:page-size="pageState.pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="pageState.total"
          :page-sizes="[10, 20, 50, 100]"
          @current-change="loadData"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { listDrugInventorySummaryByPageUsingPost } from '@/api/drugController'

const searchForm = reactive({
  drugName: '',
  drugCode: '',
  manufacturerName: '',
})

const tableData = ref<API.DrugInventorySummaryVO[]>([])
const tableLoading = ref(false)

const pageState = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
})

const sortState = reactive({
  sortField: '',
  sortOrder: '',
})

const loadData = async () => {
  tableLoading.value = true
  try {
    const res = await listDrugInventorySummaryByPageUsingPost({
      current: pageState.current,
      pageSize: pageState.pageSize,
      drugName: searchForm.drugName.trim() || undefined,
      drugCode: searchForm.drugCode.trim() || undefined,
      manufacturerName: searchForm.manufacturerName.trim() || undefined,
      sortField: sortState.sortField || undefined,
      sortOrder: sortState.sortOrder || undefined,
    })
    const { code, data, message } = res.data ?? {}
    if (code !== 0 || !data) {
      ElMessage.error(message ?? '获取库存汇总失败')
      return
    }
    tableData.value = data.records ?? []
    pageState.total = Number(data.total ?? 0)
  } finally {
    tableLoading.value = false
  }
}

const handleRefresh = () => {
  pageState.current = 1
  loadData()
}

const handleExport = () => {
  ElMessage.info('导出功能暂未实现')
}

const handleSizeChange = () => {
  pageState.current = 1
  loadData()
}

const handleSortChange = (sortInfo: { prop: string; order: string | null }) => {
  if (!sortInfo.order) {
    sortState.sortField = ''
    sortState.sortOrder = ''
  } else {
    sortState.sortOrder = sortInfo.order === 'ascending' ? 'ascend' : 'descend'
    // 后端 mapper 支持 drug_code/drug_name/current_inventory 或同名驼峰字段
    if (sortInfo.prop === 'drugCode') sortState.sortField = 'drug_code'
    else if (sortInfo.prop === 'drugName') sortState.sortField = 'drug_name'
    else if (sortInfo.prop === 'currentInventory') sortState.sortField = 'current_inventory'
    else sortState.sortField = ''
  }
  pageState.current = 1
  loadData()
}

onMounted(loadData)
</script>

<style scoped>
#inventorySummaryPage {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.searchCard :deep(.el-card__body) {
  padding-bottom: 4px;
}

.tableCard :deep(.el-card__body) {
  padding-bottom: 12px;
}

.pager {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}
</style>


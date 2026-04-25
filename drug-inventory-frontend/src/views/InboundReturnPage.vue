<template>
  <div id="inboundReturnPage">
    <el-card shadow="never" class="searchCard">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="入库明细ID">
          <el-input-number v-model="searchForm.inboundItemId" :min="1" :precision="0" placeholder="可选" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="goCreatePage">新增退货</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="tableCard">
      <el-table v-loading="tableLoading" :data="tableData" border @sort-change="handleSortChange">
        <el-table-column prop="id" label="ID" width="90" sortable="custom" />
        <el-table-column prop="inboundItemId" label="入库明细ID" width="120" />
        <el-table-column prop="drugName" label="药品名称" min-width="180" />
        <el-table-column prop="drugCode" label="药品编码" min-width="140" />
        <el-table-column prop="batchNo" label="批号" min-width="120" />
        <el-table-column prop="returnQuantity" label="退货数量" width="100" />
        <el-table-column prop="returnPrice" label="退货单价" width="110" />
        <el-table-column prop="reason" label="退货原因" min-width="180" show-overflow-tooltip />
        <el-table-column prop="returnDate" label="退货日期" min-width="160" sortable="custom" />
        <el-table-column prop="createTime" label="创建时间" min-width="180" sortable="custom" />
      </el-table>

      <div class="pager">
        <el-pagination
          v-model:current-page="pageState.current"
          v-model:page-size="pageState.pageSize"
          :total="pageState.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
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
import { useRouter } from 'vue-router'
import { listInboundReturnByPageUsingPost } from '@/api/inboundReturnController'

const router = useRouter()

const SORT_FIELD_MAP: Record<string, string> = {
  id: 'id',
  returnDate: 'return_date',
  createTime: 'create_time',
}

const searchForm = reactive<{
  inboundItemId: number | undefined
}>({
  inboundItemId: undefined,
})

const tableData = ref<API.InboundReturnVO[]>([])
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
    const res = await listInboundReturnByPageUsingPost({
      current: pageState.current,
      pageSize: pageState.pageSize,
      inboundItemId: searchForm.inboundItemId,
      sortField: sortState.sortField || undefined,
      sortOrder: sortState.sortOrder || undefined,
    })
    const { code, data, message } = res.data ?? {}
    if (code !== 0 || !data) {
      ElMessage.error(message ?? '获取入库退货列表失败')
      return
    }
    tableData.value = data.records ?? []
    pageState.total = Number(data.total ?? 0)
  } finally {
    tableLoading.value = false
  }
}

const handleSearch = () => {
  pageState.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.inboundItemId = undefined
  sortState.sortField = ''
  sortState.sortOrder = ''
  pageState.current = 1
  loadData()
}

const handleSortChange = (sortInfo: { prop: string; order: string | null }) => {
  sortState.sortField = sortInfo.order ? (SORT_FIELD_MAP[sortInfo.prop] ?? '') : ''
  sortState.sortOrder = sortInfo.order ?? ''
  pageState.current = 1
  loadData()
}

const handleSizeChange = () => {
  pageState.current = 1
  loadData()
}

const goCreatePage = () => {
  router.push('/inbound-return/create')
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
#inboundReturnPage {
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

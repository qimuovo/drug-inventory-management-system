<template>
  <div id="outboundReturnPage">
    <el-card shadow="never" class="searchCard">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="搜索">
          <el-input
            v-model="searchForm.search"
            clearable
            placeholder="请输入药品名称或药品编码"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="goCreatePage">新增退库</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="tableCard">
      <el-table v-loading="tableLoading" :data="tableData" border @sort-change="handleSortChange">
        <el-table-column prop="id" label="ID" width="90" sortable="custom" />
        <el-table-column prop="outboundItemId" label="出库明细ID" width="120" />
        <el-table-column prop="outboundNo" label="原出库单号" min-width="160" />
        <el-table-column prop="drugName" label="药品名称" min-width="180" />
        <el-table-column prop="drugCode" label="药品编码" min-width="140" />
        <el-table-column prop="batchNo" label="批号" min-width="120" />
        <el-table-column prop="returnQuantity" label="退库数量" width="100" />
        <el-table-column prop="returnPrice" label="退库单价" width="110" />
        <el-table-column prop="reason" label="退库原因" min-width="180" show-overflow-tooltip />
        <el-table-column prop="returnDate" label="退库日期" min-width="160" sortable="custom" />
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
import { listOutboundReturnByPageUsingPost } from '@/api/outboundReturnController'

const router = useRouter()
const SORT_FIELD_MAP: Record<string, string> = { id: 'id', returnDate: 'return_date', createTime: 'create_time' }
const searchForm = reactive<{ search: string }>({ search: '' })
const tableData = ref<API.OutboundReturnVO[]>([])
const tableLoading = ref(false)
const pageState = reactive({ current: 1, pageSize: 10, total: 0 })
const sortState = reactive({ sortField: '', sortOrder: '' })

const loadData = async () => {
  tableLoading.value = true
  try {
    const res = await listOutboundReturnByPageUsingPost({
      current: pageState.current,
      pageSize: pageState.pageSize,
      search: searchForm.search.trim() || undefined,
      sortField: sortState.sortField || undefined,
      sortOrder: sortState.sortOrder || undefined,
    })
    const { code, data, message } = res.data ?? {}
    if (code !== 0 || !data) return ElMessage.error(message ?? '获取出库退库列表失败')
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
  searchForm.search = ''
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
const goCreatePage = () => router.push('/outbound-return/create')
onMounted(loadData)
</script>

<style scoped>
#outboundReturnPage { display: flex; flex-direction: column; gap: 12px; }
.searchCard :deep(.el-card__body) { padding-bottom: 4px; }
.tableCard :deep(.el-card__body) { padding-bottom: 12px; }
.pager { margin-top: 12px; display: flex; justify-content: flex-end; }
</style>

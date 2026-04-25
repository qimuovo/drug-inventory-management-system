<template>
  <div id="inboundPage">
    <el-card shadow="never" class="searchCard">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="入库单号">
          <el-input v-model="searchForm.inboundNo" clearable placeholder="请输入入库单号" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="dialogVisible = true">新增入库</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="tableCard">
      <el-table v-loading="tableLoading" :data="tableData" border @sort-change="handleSortChange">
        <el-table-column type="expand">
          <template #default="{ row }">
            <el-table :data="row.itemList ?? []" size="small" border>
              <el-table-column prop="id" label="明细ID" width="90" />
              <el-table-column prop="drugName" label="药品名称" min-width="180" />
              <el-table-column prop="drugCode" label="药品编码" min-width="140" />
              <el-table-column prop="batchNo" label="批号" min-width="130" />
              <el-table-column prop="quantity" label="数量" width="90" />
              <el-table-column prop="price" label="单价" width="110" />
              <el-table-column prop="amount" label="金额" width="120" />
            </el-table>
          </template>
        </el-table-column>
        <el-table-column prop="id" label="ID" width="90" sortable="custom" />
        <el-table-column prop="inboundNo" label="入库单号" min-width="180" sortable="custom" />
        <el-table-column prop="inboundDate" label="入库日期" min-width="140" sortable="custom" />
        <el-table-column prop="remark" label="备注" min-width="200" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" min-width="180" sortable="custom" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button type="warning" link @click="goReturnPage(row)">退货</el-button>
          </template>
        </el-table-column>
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

    <InboundFormDialog v-model="dialogVisible" :loading="submitLoading" @submit="handleCreateInbound" />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRouter } from 'vue-router'
import { addInboundUsingPost, listInboundByPageUsingPost } from '@/api/inboundController'
import InboundFormDialog from '@/components/inbound/InboundFormDialog.vue'

const router = useRouter()

const SORT_FIELD_MAP: Record<string, string> = {
  id: 'id',
  inboundNo: 'inbound_no',
  inboundDate: 'inbound_date',
  createTime: 'create_time',
}

const searchForm = reactive({
  inboundNo: '',
})

const tableData = ref<API.InboundVO[]>([])
const tableLoading = ref(false)
const submitLoading = ref(false)
const dialogVisible = ref(false)

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
    const res = await listInboundByPageUsingPost({
      current: pageState.current,
      pageSize: pageState.pageSize,
      inboundNo: searchForm.inboundNo.trim() || undefined,
      sortField: sortState.sortField || undefined,
      sortOrder: sortState.sortOrder || undefined,
    })
    const { code, data, message } = res.data ?? {}
    if (code !== 0 || !data) {
      ElMessage.error(message ?? '获取入库列表失败')
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
  searchForm.inboundNo = ''
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

const handleCreateInbound = async (payload: API.InboundAddRequest) => {
  submitLoading.value = true
  try {
    const res = await addInboundUsingPost(payload)
    const { code, message } = res.data ?? {}
    if (code !== 0) {
      ElMessage.error(message ?? '新增入库失败')
      return
    }
    ElMessage.success('新增入库成功')
    dialogVisible.value = false
    pageState.current = 1
    await loadData()
  } finally {
    submitLoading.value = false
  }
}

const goReturnPage = (row: API.InboundVO) => {
  router.push({
    path: '/inbound-return/create',
    query: {
      inboundNo: row.inboundNo ?? '',
    },
  })
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
#inboundPage {
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

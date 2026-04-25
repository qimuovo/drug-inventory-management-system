<template>
  <div id="manufacturerPage">
    <el-card shadow="never" class="searchCard">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="厂家名称">
          <el-input
            v-model="searchForm.manufacturerName"
            clearable
            placeholder="请输入厂家名称"
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleCreate">新增厂家</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="tableCard">
      <el-table v-loading="tableLoading" :data="tableData" border style="width: 100%" @sort-change="handleSortChange">
        <el-table-column prop="id" label="ID" width="90" sortable="custom" />
        <el-table-column prop="manufacturerName" label="厂家名称" min-width="180" />
        <el-table-column prop="contactPerson" label="联系人" min-width="120" />
        <el-table-column prop="phone" label="联系电话" min-width="140" />
        <el-table-column prop="address" label="地址" min-width="220" show-overflow-tooltip />
        <el-table-column prop="createTime" label="创建时间" width="180" sortable="custom" />
        <el-table-column prop="updateTime" label="更新时间" width="180" sortable="custom" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
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

    <ManufacturerFormDialog
      v-model="dialogVisible"
      :mode="dialogMode"
      :loading="submitLoading"
      :initial-data="currentRow"
      @submit="handleSubmitDialog"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  addManufacturerUsingPost,
  deleteManufacturerUsingDelete,
  listManufacturerByPageUsingPost,
  updateManufacturerUsingPut,
} from '@/api/manufacturerController'
import ManufacturerFormDialog from '@/components/manufacturer/ManufacturerFormDialog.vue'

type DialogMode = 'create' | 'edit'

const SORT_FIELD_MAP: Record<string, string> = {
  id: 'id',
  createTime: 'create_time',
  updateTime: 'update_time',
}

const searchForm = reactive({
  manufacturerName: '',
})

const tableData = ref<API.Manufacturer[]>([])
const tableLoading = ref(false)
const submitLoading = ref(false)

const pageState = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
})

const sortState = reactive({
  sortField: '',
  sortOrder: '',
})

const dialogVisible = ref(false)
const dialogMode = ref<DialogMode>('create')
const currentRow = ref<API.Manufacturer | null>(null)

const loadData = async () => {
  tableLoading.value = true
  try {
    const res = await listManufacturerByPageUsingPost({
      current: pageState.current,
      pageSize: pageState.pageSize,
      manufacturerName: searchForm.manufacturerName.trim(),
      sortField: sortState.sortField || undefined,
      sortOrder: sortState.sortOrder || undefined,
    })
    const { code, data, message } = res.data ?? {}
    if (code !== 0 || !data) {
      ElMessage.error(message ?? '获取厂家列表失败')
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
  searchForm.manufacturerName = ''
  sortState.sortField = ''
  sortState.sortOrder = ''
  pageState.current = 1
  loadData()
}

const handleSizeChange = () => {
  pageState.current = 1
  loadData()
}

const handleSortChange = (sortInfo: { prop: string; order: string | null }) => {
  sortState.sortField = sortInfo.order ? (SORT_FIELD_MAP[sortInfo.prop] ?? '') : ''
  sortState.sortOrder = sortInfo.order ?? ''
  pageState.current = 1
  loadData()
}

const handleCreate = () => {
  dialogMode.value = 'create'
  currentRow.value = null
  dialogVisible.value = true
}

const handleEdit = (row: API.Manufacturer) => {
  dialogMode.value = 'edit'
  currentRow.value = row
  dialogVisible.value = true
}

const handleSubmitDialog = async (formData: API.ManufacturerAddRequest) => {
  submitLoading.value = true
  try {
    if (dialogMode.value === 'create') {
      const res = await addManufacturerUsingPost(formData)
      const { code, message } = res.data ?? {}
      if (code !== 0) {
        ElMessage.error(message ?? '新增厂家失败')
        return
      }
      ElMessage.success('新增成功')
    } else {
      const id = currentRow.value?.id
      if (!id) {
        ElMessage.error('厂家 ID 不存在')
        return
      }
      const res = await updateManufacturerUsingPut({ id }, formData)
      const { code, message } = res.data ?? {}
      if (code !== 0) {
        ElMessage.error(message ?? '编辑厂家失败')
        return
      }
      ElMessage.success('编辑成功')
    }
    dialogVisible.value = false
    await loadData()
  } finally {
    submitLoading.value = false
  }
}

const handleDelete = async (row: API.Manufacturer) => {
  if (!row.id) {
    ElMessage.error('厂家 ID 不存在')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除厂家「${row.manufacturerName ?? row.id}」吗？此操作不可恢复。`,
      '删除确认',
      {
        type: 'warning',
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
      },
    )
    const res = await deleteManufacturerUsingDelete({ id: row.id })
    const { code, message } = res.data ?? {}
    if (code !== 0) {
      ElMessage.error(message ?? '删除失败')
      return
    }
    ElMessage.success('删除成功')
    if (tableData.value.length === 1 && pageState.current > 1) {
      pageState.current -= 1
    }
    await loadData()
  } catch (error) {
    // 用户取消删除时不提示错误
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
#manufacturerPage {
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

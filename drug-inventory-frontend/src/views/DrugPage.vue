<template>
  <div id="drugPage">
    <el-card shadow="never" class="searchCard">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="药品名称">
          <el-input v-model="searchForm.drugName" clearable placeholder="请输入药品名称" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item label="药品编码">
          <el-input v-model="searchForm.drugCode" clearable placeholder="请输入药品编码" @keyup.enter="handleSearch" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">搜索</el-button>
          <el-button @click="handleReset">重置</el-button>
          <el-button type="success" @click="handleCreate">新增药品</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card shadow="never" class="tableCard">
      <el-table v-loading="tableLoading" :data="tableData" border style="width: 100%" @sort-change="handleSortChange">
        <el-table-column prop="id" label="ID" width="90" sortable="custom" />
        <el-table-column prop="drugName" label="药品名称" min-width="180" sortable="custom" />
        <el-table-column prop="drugCode" label="药品编码" min-width="140" sortable="custom" />
        <el-table-column prop="manufacturerName" label="厂家名称" min-width="180" show-overflow-tooltip />
        <el-table-column prop="specification" label="规格" min-width="180" show-overflow-tooltip />
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

    <DrugFormDialog
      v-model="dialogVisible"
      :mode="dialogMode"
      :loading="submitLoading"
      :initial-data="currentRow"
      :manufacturer-options="manufacturerOptions"
      @submit="handleSubmitDialog"
    />
  </div>
</template>

<script setup lang="ts">
import { onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { addDrugUsingPost, deleteDrugUsingDelete, listDrugByPageUsingPost, updateDrugUsingPut } from '@/api/drugController'
import DrugFormDialog from '@/components/drug/DrugFormDialog.vue'

type DialogMode = 'create' | 'edit'

const SORT_FIELD_MAP: Record<string, string> = {
  id: 'id',
  drugName: 'drug_name',
  drugCode: 'drug_code',
  createTime: 'create_time',
  updateTime: 'update_time',
}

const searchForm = reactive({
  drugName: '',
  drugCode: '',
})

const tableData = ref<API.DrugVO[]>([])
const tableLoading = ref(false)
const submitLoading = ref(false)
const manufacturerOptions = ref<{ id: number; manufacturerName: string }[]>([])

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
const currentRow = ref<API.DrugVO | null>(null)

const loadData = async () => {
  tableLoading.value = true
  try {
    const res = await listDrugByPageUsingPost({
      current: pageState.current,
      pageSize: pageState.pageSize,
      drugName: searchForm.drugName.trim(),
      drugCode: searchForm.drugCode.trim(),
      sortField: sortState.sortField || undefined,
      sortOrder: sortState.sortOrder || undefined,
    })
    const { code, data, message } = res.data ?? {}
    if (code !== 0 || !data) {
      ElMessage.error(message ?? '获取药品列表失败')
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
  searchForm.drugName = ''
  searchForm.drugCode = ''
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

const handleEdit = (row: API.DrugVO) => {
  dialogMode.value = 'edit'
  currentRow.value = row
  dialogVisible.value = true
}

const handleSubmitDialog = async (formData: API.DrugAddRequest) => {
  submitLoading.value = true
  try {
    if (dialogMode.value === 'create') {
      const res = await addDrugUsingPost(formData)
      const { code, message } = res.data ?? {}
      if (code !== 0) {
        ElMessage.error(message ?? '新增药品失败')
        return
      }
      ElMessage.success('新增成功')
    } else {
      const id = currentRow.value?.id
      if (!id) {
        ElMessage.error('药品 ID 不存在')
        return
      }
      const res = await updateDrugUsingPut({ id }, formData)
      const { code, message } = res.data ?? {}
      if (code !== 0) {
        ElMessage.error(message ?? '编辑药品失败')
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

const handleDelete = async (row: API.DrugVO) => {
  if (!row.id) {
    ElMessage.error('药品 ID 不存在')
    return
  }
  try {
    await ElMessageBox.confirm(
      `确定要删除药品「${row.drugName ?? row.id}」吗？此操作不可恢复。`,
      '删除确认',
      {
        type: 'warning',
        confirmButtonText: '确认删除',
        cancelButtonText: '取消',
      },
    )
    const res = await deleteDrugUsingDelete({ id: row.id })
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
#drugPage {
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

<template>
  <el-dialog :model-value="modelValue" title="新增入库" width="900px" destroy-on-close @close="handleClose">
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px">
      <el-row :gutter="12">
        <el-col :span="8">
          <el-form-item label="入库单号" prop="inboundNo">
            <el-input v-model="formData.inboundNo" placeholder="请输入入库单号" maxlength="64" />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="入库日期">
            <el-date-picker
              v-model="formData.inboundDate"
              style="width: 100%"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="请选择入库日期"
            />
          </el-form-item>
        </el-col>
        <el-col :span="8">
          <el-form-item label="备注">
            <el-input v-model="formData.remark" placeholder="可选" maxlength="200" />
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>

    <div class="itemHeader">
      <div class="itemTitle">入库明细</div>
      <el-button type="primary" link @click="addItem">+ 添加明细</el-button>
    </div>

    <div v-for="(item, index) in itemList" :key="item.uid" class="itemRow">
      <el-row :gutter="10">
        <el-col :span="8">
          <el-form-item :label="index === 0 ? '药品' : ''" required>
            <el-select
              v-model="item.drugId"
              filterable
              remote
              clearable
              remote-show-suffix
              :loading="drugLoading"
              style="width: 100%"
              placeholder="输入药品名称/编码搜索"
              :remote-method="handleDrugSearch"
              @visible-change="handleDrugVisibleChange"
              @popup-scroll="handleDrugPopupScroll"
            >
              <el-option v-for="option in drugOptions" :key="option.id" :label="option.label" :value="option.id" />
            </el-select>
          </el-form-item>
        </el-col>
        <el-col :span="6">
          <el-form-item :label="index === 0 ? '批号' : ''" required>
            <el-input v-model="item.batchNo" maxlength="64" placeholder="请输入批号" />
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item :label="index === 0 ? '数量' : ''" required>
            <el-input-number v-model="item.quantity" :min="1" :precision="0" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="4">
          <el-form-item :label="index === 0 ? '单价' : ''" required>
            <el-input-number v-model="item.price" :min="0" :precision="2" style="width: 100%" />
          </el-form-item>
        </el-col>
        <el-col :span="2">
          <el-form-item :label="index === 0 ? '操作' : ''">
            <el-button type="danger" link @click="removeItem(index)">删除</el-button>
          </el-form-item>
        </el-col>
      </el-row>
    </div>

    <div class="summary">总种类：{{ itemList.length }}，总数量：{{ totalQuantity }}，总金额：{{ totalAmount.toFixed(2) }}</div>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">提交入库</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import type { FormInstance, FormRules } from 'element-plus'
import { listDrugByPageUsingPost } from '@/api/drugController'

type LocalItem = {
  uid: number
  drugId?: number
  batchNo: string
  quantity: number
  price: number
}

type DrugOption = {
  id: number
  label: string
}

const props = withDefaults(
  defineProps<{
    modelValue: boolean
    loading?: boolean
  }>(),
  {
    loading: false,
  },
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit', value: API.InboundAddRequest): void
}>()

const formRef = ref<FormInstance>()
let uidSeed = 0

const formData = reactive({
  inboundNo: '',
  inboundDate: '',
  remark: '',
})

const rules: FormRules = {
  inboundNo: [{ required: true, message: '请输入入库单号', trigger: 'blur' }],
}

const itemList = ref<LocalItem[]>([])
const drugOptions = ref<DrugOption[]>([])
const drugLoading = ref(false)
const drugKeyword = ref('')
const drugCurrent = ref(1)
const drugTotal = ref(0)
const DRUG_PAGE_SIZE = 20

const hasMoreDrug = computed(() => drugOptions.value.length < drugTotal.value)
const totalQuantity = computed(() => itemList.value.reduce((sum, item) => sum + (item.quantity || 0), 0))
const totalAmount = computed(() => itemList.value.reduce((sum, item) => sum + (item.quantity || 0) * (item.price || 0), 0))

const createEmptyItem = (): LocalItem => ({
  uid: ++uidSeed,
  drugId: undefined,
  batchNo: '',
  quantity: 1,
  price: 0,
})

const resetState = () => {
  formData.inboundNo = ''
  formData.inboundDate = ''
  formData.remark = ''
  itemList.value = [createEmptyItem()]
  drugKeyword.value = ''
  drugCurrent.value = 1
  drugTotal.value = 0
  drugOptions.value = []
}

const loadDrugOptions = async (reset = false) => {
  if (drugLoading.value) return
  if (!reset && !hasMoreDrug.value) return
  drugLoading.value = true
  try {
    if (reset) {
      drugCurrent.value = 1
      drugTotal.value = 0
      drugOptions.value = []
    }
    const res = await listDrugByPageUsingPost({
      current: drugCurrent.value,
      pageSize: DRUG_PAGE_SIZE,
      drugName: drugKeyword.value || undefined,
      sortField: 'update_time',
      sortOrder: 'descend',
    })
    const { code, data, message } = res.data ?? {}
    if (code !== 0 || !data) {
      ElMessage.error(message ?? '获取药品列表失败')
      return
    }
    const currentOptions =
      (data.records ?? []).map((item) => ({
        id: Number(item.id),
        label: `${item.drugName ?? '-'} / ${item.drugCode ?? '-'} / ${item.specification ?? '-'}`,
      })) ?? []
    const map = new Map<number, DrugOption>()
    drugOptions.value.forEach((item) => map.set(item.id, item))
    currentOptions.forEach((item) => map.set(item.id, item))
    drugOptions.value = Array.from(map.values())
    drugTotal.value = Number(data.total ?? 0)
    drugCurrent.value += 1
  } finally {
    drugLoading.value = false
  }
}

const addItem = () => {
  itemList.value.push(createEmptyItem())
}

const removeItem = (index: number) => {
  if (itemList.value.length === 1) {
    ElMessage.warning('至少保留一条明细')
    return
  }
  itemList.value.splice(index, 1)
}

const handleClose = () => {
  emit('update:modelValue', false)
}

const handleDrugSearch = (keyword: string) => {
  drugKeyword.value = keyword.trim()
  loadDrugOptions(true)
}

const handleDrugVisibleChange = (visible: boolean) => {
  if (visible && !drugOptions.value.length) {
    loadDrugOptions(true)
  }
}

const handleDrugPopupScroll = (event: Event) => {
  const target = event.target as HTMLElement
  if (!target || drugLoading.value || !hasMoreDrug.value) return
  const reachBottom = target.scrollTop + target.clientHeight >= target.scrollHeight - 32
  if (reachBottom) {
    loadDrugOptions()
  }
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  const payloadItemList: API.InboundAddItemRequest[] = []
  for (const item of itemList.value) {
    if (!item.drugId || item.drugId <= 0) {
      ElMessage.warning('请选择药品')
      return
    }
    if (!item.batchNo.trim()) {
      ElMessage.warning('请填写批号')
      return
    }
    if (!item.quantity || item.quantity <= 0) {
      ElMessage.warning('入库数量必须大于 0')
      return
    }
    if (item.price < 0) {
      ElMessage.warning('入库单价不能小于 0')
      return
    }
    payloadItemList.push({
      drugId: item.drugId,
      batchNo: item.batchNo.trim(),
      quantity: item.quantity,
      price: item.price,
    })
  }
  emit('submit', {
    inboundNo: formData.inboundNo.trim(),
    inboundDate: formData.inboundDate || undefined,
    remark: formData.remark.trim() || undefined,
    itemList: payloadItemList,
  })
}

watch(
  () => props.modelValue,
  (visible) => {
    if (!visible) return
    resetState()
    loadDrugOptions(true)
  },
)
</script>

<style scoped>
.itemHeader {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.itemTitle {
  font-weight: 600;
}

.itemRow {
  margin-bottom: 6px;
}

.summary {
  margin-top: 8px;
  text-align: right;
  color: var(--el-text-color-secondary);
}
</style>

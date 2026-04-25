<template>
  <el-dialog
    :model-value="modelValue"
    :title="dialogTitle"
    width="560px"
    destroy-on-close
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px">
      <el-form-item label="药品名称" prop="drugName">
        <el-input v-model="formData.drugName" maxlength="100" placeholder="请输入药品名称" />
      </el-form-item>
      <el-form-item label="药品编码" prop="drugCode">
        <el-input v-model="formData.drugCode" maxlength="50" placeholder="请输入药品编码" />
      </el-form-item>
      <el-form-item label="厂家" prop="manufacturerId">
        <el-select
          v-model="formData.manufacturerId"
          remote
          filterable
          remote-show-suffix
          clearable
          :loading="manufacturerLoading"
          placeholder="请选择厂家"
          style="width: 100%"
          @visible-change="handleManufacturerVisibleChange"
          @popup-scroll="handleManufacturerPopupScroll"
          @clear="handleManufacturerSearch('')"
          :remote-method="handleManufacturerSearch"
        >
          <el-option
            v-for="item in manufacturerOptions"
            :key="item.id"
            :label="item.manufacturerName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="规格" prop="specification">
        <el-input
          v-model="formData.specification"
          type="textarea"
          :rows="3"
          maxlength="200"
          show-word-limit
          placeholder="请输入规格，如 10mg*10片"
        />
      </el-form-item>
    </el-form>

    <template #footer>
      <el-button @click="handleClose">取消</el-button>
      <el-button type="primary" :loading="loading" @click="handleSubmit">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { computed, reactive, ref, watch } from 'vue'
import type { FormInstance, FormRules } from 'element-plus'
import { ElMessage } from 'element-plus'
import { listManufacturerByPageUsingPost } from '@/api/manufacturerController'

type DialogMode = 'create' | 'edit'

type ManufacturerOption = {
  id: number
  manufacturerName: string
}

const props = withDefaults(
  defineProps<{
    modelValue: boolean
    mode: DialogMode
    loading?: boolean
    initialData?: API.DrugVO | null
    manufacturerOptions?: ManufacturerOption[]
  }>(),
  {
    loading: false,
    initialData: null,
    manufacturerOptions: () => [],
  },
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit', value: API.DrugAddRequest): void
}>()

const formRef = ref<FormInstance>()
const manufacturerOptions = ref<ManufacturerOption[]>([])
const manufacturerLoading = ref(false)
const manufacturerKeyword = ref('')
const manufacturerPage = ref(1)
const manufacturerTotal = ref(0)
const manufacturerPageSize = 20

const formData = reactive<API.DrugAddRequest>({
  drugName: '',
  drugCode: '',
  manufacturerId: undefined,
  specification: '',
})

const rules: FormRules = {
  drugName: [{ required: true, message: '请输入药品名称', trigger: 'blur' }],
  drugCode: [{ required: true, message: '请输入药品编码', trigger: 'blur' }],
  manufacturerId: [{ required: true, message: '请选择厂家', trigger: 'change' }],
}

const dialogTitle = computed(() => (props.mode === 'create' ? '新增药品' : '编辑药品'))
const hasMoreManufacturer = computed(() => manufacturerOptions.value.length < manufacturerTotal.value)

const resetForm = () => {
  formData.drugName = ''
  formData.drugCode = ''
  formData.manufacturerId = undefined
  formData.specification = ''
}

const fetchManufacturerOptions = async (reset = false) => {
  if (manufacturerLoading.value) return
  if (!reset && !hasMoreManufacturer.value) return
  manufacturerLoading.value = true
  try {
    if (reset) {
      manufacturerPage.value = 1
      manufacturerTotal.value = 0
      manufacturerOptions.value = []
    }
    const res = await listManufacturerByPageUsingPost({
      current: manufacturerPage.value,
      pageSize: manufacturerPageSize,
      manufacturerName: manufacturerKeyword.value || undefined,
      sortField: 'update_time',
      sortOrder: 'descend',
    })
    const { code, data, message } = res.data ?? {}
    if (code !== 0 || !data) {
      ElMessage.error(message ?? '获取厂家列表失败')
      return
    }
    const currentRecords =
      (data.records ?? [])
        .filter((item) => !!item.id && !!item.manufacturerName)
        .map((item) => ({
          id: Number(item.id),
          manufacturerName: item.manufacturerName as string,
        })) ?? []
    const optionMap = new Map<number, ManufacturerOption>()
    manufacturerOptions.value.forEach((item) => optionMap.set(item.id, item))
    currentRecords.forEach((item) => optionMap.set(item.id, item))
    manufacturerOptions.value = Array.from(optionMap.values())
    manufacturerTotal.value = Number(data.total ?? 0)
    manufacturerPage.value += 1
  } finally {
    manufacturerLoading.value = false
  }
}

const ensureEditOption = () => {
  if (!props.initialData?.manufacturerId || !props.initialData?.manufacturerName) return
  const id = Number(props.initialData.manufacturerId)
  if (!id || manufacturerOptions.value.some((item) => item.id === id)) return
  manufacturerOptions.value.unshift({
    id,
    manufacturerName: props.initialData.manufacturerName,
  })
}

watch(
  () => props.modelValue,
  (visible) => {
    if (!visible) return
    resetForm()
    manufacturerKeyword.value = ''
    fetchManufacturerOptions(true)
    if (props.mode === 'edit' && props.initialData) {
      formData.drugName = props.initialData.drugName ?? ''
      formData.drugCode = props.initialData.drugCode ?? ''
      formData.manufacturerId = props.initialData.manufacturerId
      formData.specification = props.initialData.specification ?? ''
      ensureEditOption()
    }
  },
)

const handleClose = () => {
  emit('update:modelValue', false)
}

const handleManufacturerSearch = (keyword: string) => {
  manufacturerKeyword.value = keyword.trim()
  fetchManufacturerOptions(true)
}

const handleManufacturerVisibleChange = (visible: boolean) => {
  if (!visible) return
  if (!manufacturerOptions.value.length) {
    fetchManufacturerOptions(true)
  }
}

const handleManufacturerPopupScroll = (event: Event) => {
  const target = event.target as HTMLElement
  if (!target || manufacturerLoading.value || !hasMoreManufacturer.value) return
  const threshold = 32
  const reachBottom = target.scrollTop + target.clientHeight >= target.scrollHeight - threshold
  if (reachBottom) {
    fetchManufacturerOptions()
  }
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  emit('submit', {
    drugName: formData.drugName?.trim(),
    drugCode: formData.drugCode?.trim(),
    manufacturerId: formData.manufacturerId,
    specification: formData.specification?.trim(),
  })
}
</script>

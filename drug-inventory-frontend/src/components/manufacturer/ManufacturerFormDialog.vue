<template>
  <el-dialog
    :model-value="modelValue"
    :title="dialogTitle"
    width="520px"
    destroy-on-close
    @close="handleClose"
  >
    <el-form ref="formRef" :model="formData" :rules="rules" label-width="90px">
      <el-form-item label="厂家名称" prop="manufacturerName">
        <el-input v-model="formData.manufacturerName" maxlength="50" placeholder="请输入厂家名称" />
      </el-form-item>
      <el-form-item label="联系人" prop="contactPerson">
        <el-input v-model="formData.contactPerson" maxlength="30" placeholder="请输入联系人" />
      </el-form-item>
      <el-form-item label="联系电话" prop="phone">
        <el-input v-model="formData.phone" maxlength="20" placeholder="请输入联系电话" />
      </el-form-item>
      <el-form-item label="地址" prop="address">
        <el-input
          v-model="formData.address"
          type="textarea"
          :rows="3"
          maxlength="200"
          show-word-limit
          placeholder="请输入厂家地址"
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

type DialogMode = 'create' | 'edit'

const props = withDefaults(
  defineProps<{
    modelValue: boolean
    mode: DialogMode
    loading?: boolean
    initialData?: API.Manufacturer | null
  }>(),
  {
    loading: false,
    initialData: null,
  },
)

const emit = defineEmits<{
  (e: 'update:modelValue', value: boolean): void
  (e: 'submit', value: API.ManufacturerAddRequest): void
}>()

const formRef = ref<FormInstance>()
const formData = reactive<API.ManufacturerAddRequest>({
  manufacturerName: '',
  contactPerson: '',
  phone: '',
  address: '',
})

const rules: FormRules = {
  manufacturerName: [{ required: true, message: '请输入厂家名称', trigger: 'blur' }],
}

const dialogTitle = computed(() => (props.mode === 'create' ? '新增厂家' : '编辑厂家'))

const resetForm = () => {
  formData.manufacturerName = ''
  formData.contactPerson = ''
  formData.phone = ''
  formData.address = ''
}

watch(
  () => props.modelValue,
  (visible) => {
    if (!visible) return
    resetForm()
    if (props.mode === 'edit' && props.initialData) {
      formData.manufacturerName = props.initialData.manufacturerName ?? ''
      formData.contactPerson = props.initialData.contactPerson ?? ''
      formData.phone = props.initialData.phone ?? ''
      formData.address = props.initialData.address ?? ''
    }
  },
)

const handleClose = () => {
  emit('update:modelValue', false)
}

const handleSubmit = async () => {
  await formRef.value?.validate()
  emit('submit', {
    manufacturerName: formData.manufacturerName?.trim(),
    contactPerson: formData.contactPerson?.trim(),
    phone: formData.phone?.trim(),
    address: formData.address?.trim(),
  })
}
</script>

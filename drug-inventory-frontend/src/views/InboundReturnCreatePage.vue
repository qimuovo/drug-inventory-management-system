<template>
  <div id="inboundReturnCreatePage">
    <el-card shadow="never">
      <template #header>
        <div class="cardHeader">
          <span>入库退货</span>
          <el-button @click="router.push('/inbound-return')">返回列表</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="headerForm">
        <el-form-item label="退货日期">
          <el-date-picker
            v-model="headerForm.returnDate"
            type="date"
            value-format="YYYY-MM-DD"
            placeholder="请选择退货日期"
          />
        </el-form-item>
        <el-form-item label="统一原因">
          <el-input v-model="headerForm.reason" placeholder="可选，未填明细原因时使用该值" style="width: 280px" />
        </el-form-item>
      </el-form>

      <el-divider content-position="left">添加退货明细</el-divider>
      <el-row :gutter="10">
        <el-col :span="10">
          <el-select
            v-model="draftItem.inboundItemId"
            filterable
            remote
            clearable
            remote-show-suffix
            :loading="inboundItemLoading"
            style="width: 100%"
            placeholder="输入药品名称/编码/批号搜索"
            :remote-method="handleInboundItemSearch"
            @popup-scroll="handleInboundItemPopupScroll"
            @visible-change="handleInboundItemVisibleChange"
          >
            <el-option
              v-for="item in filteredInboundItemOptions"
              :key="item.id"
              :label="item.label"
              :value="item.id"
            />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-input-number v-model="draftItem.returnQuantity" :min="1" :precision="0" style="width: 100%" />
        </el-col>
        <el-col :span="4">
          <el-input-number v-model="draftItem.returnPrice" :min="0" :precision="2" style="width: 100%" />
        </el-col>
        <el-col :span="4">
          <el-input v-model="draftItem.reason" placeholder="明细原因(可选)" />
        </el-col>
        <el-col :span="2">
          <el-button type="primary" @click="addDraftItem">添加</el-button>
        </el-col>
      </el-row>

      <el-divider content-position="left">退货明细列表</el-divider>
      <el-table :data="itemList" border>
        <el-table-column prop="inboundItemId" label="入库明细ID" width="120" />
        <el-table-column prop="drugName" label="药品名称" min-width="180" />
        <el-table-column prop="drugCode" label="药品编码" min-width="140" />
        <el-table-column prop="batchNo" label="批号" min-width="120" />
        <el-table-column prop="maxQuantity" label="可参考入库数量" width="120" />
        <el-table-column prop="returnQuantity" label="退货数量" width="100" />
        <el-table-column prop="returnPrice" label="退货单价" width="110" />
        <el-table-column prop="reason" label="原因" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" link @click="removeItem($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="footerBar">
        <div>总条数：{{ itemList.length }}，总退货数量：{{ totalReturnQuantity }}</div>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交退货</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { addInboundReturnUsingPost } from '@/api/inboundReturnController'
import { listInboundByPageUsingPost } from '@/api/inboundController'

type InboundItemOption = {
  id: number
  inboundNo: string
  drugName: string
  drugCode: string
  batchNo: string
  quantity: number
  label: string
}

type ReturnItem = {
  inboundItemId: number
  drugName: string
  drugCode: string
  batchNo: string
  maxQuantity: number
  returnQuantity: number
  returnPrice: number
  reason: string
}

const router = useRouter()
const route = useRoute()

const headerForm = reactive({
  returnDate: '',
  reason: '',
})

const draftItem = reactive({
  inboundItemId: undefined as number | undefined,
  returnQuantity: 1,
  returnPrice: 0,
  reason: '',
})

const inboundItemOptions = ref<InboundItemOption[]>([])
const inboundItemLoading = ref(false)
const inboundCurrent = ref(1)
const inboundHasMore = ref(true)
const inboundKeyword = ref('')
const INBOUND_PAGE_SIZE = 20

const itemList = ref<ReturnItem[]>([])
const submitLoading = ref(false)

const hasMoreInbound = computed(() => inboundHasMore.value)
const totalReturnQuantity = computed(() => itemList.value.reduce((sum, item) => sum + item.returnQuantity, 0))

const filteredInboundItemOptions = computed(() => {
  if (!inboundKeyword.value) return inboundItemOptions.value
  const keyword = inboundKeyword.value.toLowerCase()
  return inboundItemOptions.value.filter((item) => item.label.toLowerCase().includes(keyword))
})

const normalizeInboundItems = (records: API.InboundVO[]): InboundItemOption[] => {
  const result: InboundItemOption[] = []
  for (const inbound of records) {
    const inboundNo = inbound.inboundNo ?? '-'
    for (const item of inbound.itemList ?? []) {
      const id = Number(item.id)
      if (!id) continue
      const drugName = item.drugName ?? '-'
      const drugCode = item.drugCode ?? '-'
      const batchNo = item.batchNo ?? '-'
      const quantity = Number(item.quantity ?? 0)
      result.push({
        id,
        inboundNo,
        drugName,
        drugCode,
        batchNo,
        quantity,
        label: `${drugName} / ${drugCode} / 批号:${batchNo} / 入库单:${inboundNo}`,
      })
    }
  }
  return result
}

const loadInboundItems = async (reset = false) => {
  if (inboundItemLoading.value) return
  if (!reset && !hasMoreInbound.value) return
  inboundItemLoading.value = true
  try {
    if (reset) {
      inboundCurrent.value = 1
      inboundHasMore.value = true
      inboundItemOptions.value = []
    }
    const inboundNoQuery = typeof route.query.inboundNo === 'string' ? route.query.inboundNo.trim() : ''
    const res = await listInboundByPageUsingPost({
      current: inboundCurrent.value,
      pageSize: INBOUND_PAGE_SIZE,
      inboundNo: inboundNoQuery || undefined,
      sortField: 'create_time',
      sortOrder: 'descend',
    })
    const { code, data, message } = res.data ?? {}
    if (code !== 0 || !data) {
      ElMessage.error(message ?? '获取入库明细失败')
      return
    }
    const currentItems = normalizeInboundItems(data.records ?? [])
    const map = new Map<number, InboundItemOption>()
    inboundItemOptions.value.forEach((item) => map.set(item.id, item))
    currentItems.forEach((item) => map.set(item.id, item))
    inboundItemOptions.value = Array.from(map.values())
    const currentPage = Number(data.current ?? inboundCurrent.value)
    const totalPages = Number(data.pages ?? 0)
    inboundHasMore.value = totalPages > 0 ? currentPage < totalPages : currentItems.length >= INBOUND_PAGE_SIZE
    inboundCurrent.value += 1
  } finally {
    inboundItemLoading.value = false
  }
}

const handleInboundItemSearch = (keyword: string) => {
  inboundKeyword.value = keyword.trim()
  if (inboundItemOptions.value.length === 0) {
    loadInboundItems(true)
  }
}

const handleInboundItemVisibleChange = (visible: boolean) => {
  if (visible && !inboundItemOptions.value.length) {
    loadInboundItems(true)
  }
}

const handleInboundItemPopupScroll = (event: Event) => {
  const target = event.target as HTMLElement
  if (!target || inboundItemLoading.value || !hasMoreInbound.value) return
  const reachBottom = target.scrollTop + target.clientHeight >= target.scrollHeight - 32
  if (reachBottom) {
    loadInboundItems()
  }
}

const addDraftItem = () => {
  if (!draftItem.inboundItemId) {
    ElMessage.warning('请选择入库明细')
    return
  }
  const selected = inboundItemOptions.value.find((item) => item.id === draftItem.inboundItemId)
  if (!selected) {
    ElMessage.warning('入库明细不存在，请重新选择')
    return
  }
  if (draftItem.returnQuantity <= 0) {
    ElMessage.warning('退货数量必须大于 0')
    return
  }
  if (draftItem.returnQuantity > selected.quantity) {
    ElMessage.warning(`退货数量不能大于入库数量（${selected.quantity}）`)
    return
  }
  const duplicatedIndex = itemList.value.findIndex((item) => item.inboundItemId === selected.id)
  const nextItem: ReturnItem = {
    inboundItemId: selected.id,
    drugName: selected.drugName,
    drugCode: selected.drugCode,
    batchNo: selected.batchNo,
    maxQuantity: selected.quantity,
    returnQuantity: draftItem.returnQuantity,
    returnPrice: draftItem.returnPrice,
    reason: draftItem.reason.trim(),
  }
  if (duplicatedIndex >= 0) {
    itemList.value.splice(duplicatedIndex, 1, nextItem)
  } else {
    itemList.value.push(nextItem)
  }
  draftItem.inboundItemId = undefined
  draftItem.returnQuantity = 1
  draftItem.returnPrice = 0
  draftItem.reason = ''
}

const removeItem = (index: number) => {
  itemList.value.splice(index, 1)
}

const handleSubmit = async () => {
  if (!itemList.value.length) {
    ElMessage.warning('请至少添加一条退货明细')
    return
  }
  submitLoading.value = true
  try {
    const res = await addInboundReturnUsingPost({
      returnDate: headerForm.returnDate || undefined,
      reason: headerForm.reason.trim() || undefined,
      itemList: itemList.value.map((item) => ({
        inboundItemId: item.inboundItemId,
        returnQuantity: item.returnQuantity,
        returnPrice: item.returnPrice,
        reason: item.reason || undefined,
      })),
    })
    const { code, message } = res.data ?? {}
    if (code !== 0) {
      ElMessage.error(message ?? '新增退货失败')
      return
    }
    ElMessage.success('新增退货成功')
    router.push('/inbound-return')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadInboundItems(true)
  const inboundItemId = Number(route.query.inboundItemId)
  if (inboundItemId > 0) {
    draftItem.inboundItemId = inboundItemId
  }
})
</script>

<style scoped>
#inboundReturnCreatePage {
  min-height: 100%;
}

.cardHeader {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.footerBar {
  margin-top: 12px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
</style>

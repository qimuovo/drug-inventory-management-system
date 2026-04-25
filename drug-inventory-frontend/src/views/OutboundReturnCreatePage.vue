<template>
  <div id="outboundReturnCreatePage">
    <el-card shadow="never">
      <template #header>
        <div class="cardHeader">
          <span>出库退库</span>
          <el-button @click="router.push('/outbound-return')">返回列表</el-button>
        </div>
      </template>

      <el-form :inline="true" :model="headerForm">
        <el-form-item label="退库日期">
          <el-date-picker v-model="headerForm.returnDate" type="date" value-format="YYYY-MM-DD" placeholder="请选择退库日期" />
        </el-form-item>
        <el-form-item label="统一原因">
          <el-input v-model="headerForm.reason" placeholder="可选，未填明细原因时使用该值" style="width: 280px" />
        </el-form-item>
      </el-form>

      <el-divider content-position="left">添加退库明细</el-divider>
      <el-row :gutter="10">
        <el-col :span="10">
          <el-select
            v-model="draftItem.outboundItemId"
            filterable
            remote
            clearable
            remote-show-suffix
            :loading="outboundItemLoading"
            style="width: 100%"
            placeholder="输入药品名称/编码/批号搜索"
            :remote-method="handleOutboundItemSearch"
            @popup-scroll="handleOutboundItemPopupScroll"
            @visible-change="handleOutboundItemVisibleChange"
          >
            <el-option v-for="item in filteredOutboundItemOptions" :key="item.id" :label="item.label" :value="item.id" />
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

      <el-divider content-position="left">退库明细列表</el-divider>
      <el-table :data="itemList" border>
        <el-table-column prop="outboundItemId" label="出库明细ID" width="120" />
        <el-table-column prop="drugName" label="药品名称" min-width="180" />
        <el-table-column prop="drugCode" label="药品编码" min-width="140" />
        <el-table-column prop="batchNo" label="批号" min-width="120" />
        <el-table-column prop="maxQuantity" label="可参考出库数量" width="120" />
        <el-table-column prop="returnQuantity" label="退库数量" width="100" />
        <el-table-column prop="returnPrice" label="退库单价" width="110" />
        <el-table-column prop="reason" label="原因" min-width="160" show-overflow-tooltip />
        <el-table-column label="操作" width="90" fixed="right">
          <template #default="{ $index }">
            <el-button type="danger" link @click="removeItem($index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="footerBar">
        <div>总条数：{{ itemList.length }}，总退库数量：{{ totalReturnQuantity }}</div>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">提交退库</el-button>
      </div>
    </el-card>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { useRoute, useRouter } from 'vue-router'
import { addOutboundReturnUsingPost } from '@/api/outboundReturnController'
import { listOutboundByPageUsingPost } from '@/api/outboundController'

type OutboundItemOption = {
  id: number
  outboundNo: string
  drugName: string
  drugCode: string
  batchNo: string
  quantity: number
  label: string
}
type ReturnItem = {
  outboundItemId: number
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
const headerForm = reactive({ returnDate: '', reason: '' })
const draftItem = reactive({ outboundItemId: undefined as number | undefined, returnQuantity: 1, returnPrice: 0, reason: '' })
const outboundItemOptions = ref<OutboundItemOption[]>([])
const outboundItemLoading = ref(false)
const outboundCurrent = ref(1)
const outboundHasMore = ref(true)
const outboundKeyword = ref('')
const OUTBOUND_PAGE_SIZE = 20
const itemList = ref<ReturnItem[]>([])
const submitLoading = ref(false)

const hasMoreOutbound = computed(() => outboundHasMore.value)
const totalReturnQuantity = computed(() => itemList.value.reduce((sum, item) => sum + item.returnQuantity, 0))
const filteredOutboundItemOptions = computed(() => {
  if (!outboundKeyword.value) return outboundItemOptions.value
  const keyword = outboundKeyword.value.toLowerCase()
  return outboundItemOptions.value.filter((item) => item.label.toLowerCase().includes(keyword))
})

const normalizeOutboundItems = (records: API.OutboundVO[]): OutboundItemOption[] => {
  const result: OutboundItemOption[] = []
  for (const outbound of records) {
    const outboundNo = outbound.outboundNo ?? '-'
    for (const item of outbound.itemList ?? []) {
      const id = Number(item.id)
      if (!id) continue
      const drugName = item.drugName ?? '-'
      const drugCode = item.drugCode ?? '-'
      const batchNo = item.batchNo ?? '-'
      const quantity = Number(item.quantity ?? 0)
      result.push({
        id,
        outboundNo,
        drugName,
        drugCode,
        batchNo,
        quantity,
        label: `${drugName} / ${drugCode} / 批号:${batchNo} / 出库单:${outboundNo}`,
      })
    }
  }
  return result
}

const loadOutboundItems = async (reset = false) => {
  if (outboundItemLoading.value) return
  if (!reset && !hasMoreOutbound.value) return
  outboundItemLoading.value = true
  try {
    if (reset) {
      outboundCurrent.value = 1
      outboundHasMore.value = true
      outboundItemOptions.value = []
    }
    const outboundNoQuery = typeof route.query.outboundNo === 'string' ? route.query.outboundNo.trim() : ''
    const res = await listOutboundByPageUsingPost({
      current: outboundCurrent.value,
      pageSize: OUTBOUND_PAGE_SIZE,
      outboundNo: outboundNoQuery || undefined,
      sortField: 'create_time',
      sortOrder: 'descend',
    })
    const { code, data, message } = res.data ?? {}
    if (code !== 0 || !data) return ElMessage.error(message ?? '获取出库明细失败')
    const currentItems = normalizeOutboundItems(data.records ?? [])
    const map = new Map<number, OutboundItemOption>()
    outboundItemOptions.value.forEach((item) => map.set(item.id, item))
    currentItems.forEach((item) => map.set(item.id, item))
    outboundItemOptions.value = Array.from(map.values())
    const currentPage = Number(data.current ?? outboundCurrent.value)
    const totalPages = Number(data.pages ?? 0)
    outboundHasMore.value = totalPages > 0 ? currentPage < totalPages : currentItems.length >= OUTBOUND_PAGE_SIZE
    outboundCurrent.value += 1
  } finally {
    outboundItemLoading.value = false
  }
}
const handleOutboundItemSearch = (keyword: string) => {
  outboundKeyword.value = keyword.trim()
  if (!outboundItemOptions.value.length) loadOutboundItems(true)
}
const handleOutboundItemVisibleChange = (visible: boolean) => {
  if (visible && !outboundItemOptions.value.length) loadOutboundItems(true)
}
const handleOutboundItemPopupScroll = (event: Event) => {
  const target = event.target as HTMLElement
  if (!target || outboundItemLoading.value || !hasMoreOutbound.value) return
  if (target.scrollTop + target.clientHeight >= target.scrollHeight - 32) loadOutboundItems()
}

const addDraftItem = () => {
  if (!draftItem.outboundItemId) return ElMessage.warning('请选择出库明细')
  const selected = outboundItemOptions.value.find((item) => item.id === draftItem.outboundItemId)
  if (!selected) return ElMessage.warning('出库明细不存在，请重新选择')
  if (draftItem.returnQuantity <= 0) return ElMessage.warning('退库数量必须大于 0')
  if (draftItem.returnQuantity > selected.quantity) {
    return ElMessage.warning(`退库数量不能大于出库数量（${selected.quantity}）`)
  }
  const nextItem: ReturnItem = {
    outboundItemId: selected.id,
    drugName: selected.drugName,
    drugCode: selected.drugCode,
    batchNo: selected.batchNo,
    maxQuantity: selected.quantity,
    returnQuantity: draftItem.returnQuantity,
    returnPrice: draftItem.returnPrice,
    reason: draftItem.reason.trim(),
  }
  const duplicatedIndex = itemList.value.findIndex((item) => item.outboundItemId === selected.id)
  if (duplicatedIndex >= 0) itemList.value.splice(duplicatedIndex, 1, nextItem)
  else itemList.value.push(nextItem)
  draftItem.outboundItemId = undefined
  draftItem.returnQuantity = 1
  draftItem.returnPrice = 0
  draftItem.reason = ''
}
const removeItem = (index: number) => itemList.value.splice(index, 1)

const handleSubmit = async () => {
  if (!itemList.value.length) return ElMessage.warning('请至少添加一条退库明细')
  submitLoading.value = true
  try {
    const res = await addOutboundReturnUsingPost({
      returnDate: headerForm.returnDate || undefined,
      reason: headerForm.reason.trim() || undefined,
      itemList: itemList.value.map((item) => ({
        outboundItemId: item.outboundItemId,
        returnQuantity: item.returnQuantity,
        returnPrice: item.returnPrice,
        reason: item.reason || undefined,
      })),
    })
    const { code, message } = res.data ?? {}
    if (code !== 0) return ElMessage.error(message ?? '新增退库失败')
    ElMessage.success('新增退库成功')
    router.push('/outbound-return')
  } finally {
    submitLoading.value = false
  }
}

onMounted(() => {
  loadOutboundItems(true)
  const outboundItemId = Number(route.query.outboundItemId)
  if (outboundItemId > 0) draftItem.outboundItemId = outboundItemId
})
</script>

<style scoped>
#outboundReturnCreatePage { min-height: 100%; }
.cardHeader { display: flex; justify-content: space-between; align-items: center; }
.footerBar { margin-top: 12px; display: flex; align-items: center; justify-content: space-between; }
</style>

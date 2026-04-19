<template>
  <div class="order-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>采购订单</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新建订单
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="code" label="订单编号" width="150" />
        <el-table-column label="供应商" min-width="200">
          <template #default="{ row }">
            {{ getSupplierName(row.supplierId) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="订单金额" width="120">
          <template #default="{ row }">
            ¥{{ row.totalAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="expectedDate" label="期望日期" width="120" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
            <el-button v-if="row.status === 'DRAFT'" link type="success" @click="handleConfirm(row)">确认</el-button>
            <el-button v-if="row.status === 'DRAFT'" link type="danger" @click="handleCancel(row)">取消</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page"
        v-model:page-size="pageSize"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
        style="margin-top: 20px;"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isView ? '订单详情' : '新建订单'" width="700px">
      <el-form :model="form" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="供应商">
              <el-select v-model="form.supplierId" placeholder="请选择" style="width: 100%;" :disabled="isView">
                <el-option v-for="s in supplierList" :key="s.id" :label="s.name" :value="s.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="期望日期">
              <el-date-picker v-model="form.expectedDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" :disabled="isView" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="备注">
          <el-input v-model="form.remark" :disabled="isView" />
        </el-form-item>

        <el-divider v-if="!isView">订单明细</el-divider>

        <div v-if="!isView">
          <div v-for="(item, index) in items" :key="index" class="item-row">
            <el-select v-model="item.materialId" placeholder="物资" style="width: 200px;" filterable>
              <el-option v-for="m in materialList" :key="m.id" :label="`${m.code} - ${m.name}`" :value="m.id" />
            </el-select>
            <el-input-number v-model="item.quantity" :min="1" style="width: 120px;" />
            <el-input-number v-model="item.unitPrice" :min="0" :precision="2" style="width: 120px;" />
            <el-button type="danger" @click="removeItem(index)" :disabled="items.length <= 1">删除</el-button>
          </div>
          <el-button type="primary" plain @click="addItem" style="margin-top: 10px;">添加物资</el-button>
        </div>

        <div v-else>
          <el-table :data="viewItems" stripe size="small">
            <el-table-column label="物资" min-width="200">
              <template #default="{ row }">
                {{ getMaterialName(row.materialId) }}
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="100" />
            <el-table-column prop="unitPrice" label="单价" width="100">
              <template #default="{ row }">¥{{ row.unitPrice?.toFixed(2) }}</template>
            </el-table-column>
            <el-table-column prop="totalPrice" label="小计" width="100">
              <template #default="{ row }">¥{{ row.totalPrice?.toFixed(2) }}</template>
            </el-table-column>
          </el-table>
        </div>
      </el-form>

      <template #footer>
        <div v-if="!isView">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="handleSubmit">创建订单</el-button>
        </div>
        <div v-else>
          <el-button @click="dialogVisible = false">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { orderApi } from '../api/order'
import { supplierApi } from '../api/supplier'
import { materialApi } from '../api/material'

const loading = ref(false)
const tableData = ref([])
const supplierList = ref([])
const materialList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const isView = ref(false)
const viewItems = ref([])
const form = reactive({ id: null, supplierId: null, expectedDate: '', remark: '' })
const items = ref([{ materialId: null, quantity: 1, unitPrice: 0 }])

const getSupplierName = (id) => supplierList.value.find(s => s.id === id)?.name || '-'
const getMaterialName = (id) => materialList.value.find(m => m.id === id)?.name || '-'

const getStatusType = (s) => ({ DRAFT: 'info', CONFIRMED: 'primary', SHIPPED: 'warning', RECEIVED: 'success', COMPLETED: '', CANCELLED: 'danger' })[s] || ''
const getStatusText = (s) => ({ DRAFT: '草稿', CONFIRMED: '已确认', SHIPPED: '已发货', RECEIVED: '已收货', COMPLETED: '已完成', CANCELLED: '已取消' })[s] || s

const loadData = async () => {
  loading.value = true
  try {
    const res = await orderApi.list({ page: page.value, pageSize: pageSize.value })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) { console.error(error) } finally { loading.value = false }
}

const loadSuppliers = async () => {
  const res = await supplierApi.all()
  supplierList.value = res.data || []
}

const loadMaterials = async () => {
  const res = await materialApi.all()
  materialList.value = res.data || []
}

const handleAdd = () => {
  isView.value = false
  Object.assign(form, { id: null, supplierId: null, expectedDate: '', remark: '' })
  items.value = [{ materialId: null, quantity: 1, unitPrice: 0 }]
  dialogVisible.value = true
}

const handleView = async (row) => {
  isView.value = true
  Object.assign(form, row)
  try {
    const res = await orderApi.items(row.id)
    viewItems.value = res.data || []
  } catch (error) { console.error(error) }
  dialogVisible.value = true
}

const handleConfirm = async (row) => {
  await ElMessageBox.confirm('确认该订单？', '提示', { type: 'info' })
  try {
    await orderApi.confirm(row.id)
    ElMessage.success('订单已确认')
    loadData()
  } catch (error) { console.error(error) }
}

const handleCancel = async (row) => {
  await ElMessageBox.confirm('取消该订单？', '提示', { type: 'warning' })
  try {
    await orderApi.cancel(row.id)
    ElMessage.success('订单已取消')
    loadData()
  } catch (error) { console.error(error) }
}

const addItem = () => { items.value.push({ materialId: null, quantity: 1, unitPrice: 0 }) }
const removeItem = (index) => { items.value.splice(index, 1) }

const handleSubmit = async () => {
  if (!form.supplierId) { ElMessage.warning('请选择供应商'); return }
  const validItems = items.value.filter(i => i.materialId && i.quantity > 0)
  if (validItems.length === 0) { ElMessage.warning('请添加至少一个物资'); return }
  try {
    const orderRes = await orderApi.create(form)
    await orderApi.addItems(orderRes.data.id, validItems)
    ElMessage.success('订单创建成功')
    dialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
}

onMounted(() => { loadData(); loadSuppliers(); loadMaterials() })
</script>

<style scoped>
.order-page { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.item-row { display: flex; gap: 10px; margin-bottom: 10px; align-items: center; }
</style>

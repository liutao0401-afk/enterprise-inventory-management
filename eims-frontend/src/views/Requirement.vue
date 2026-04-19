<template>
  <div class="requirement-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>采购需求</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增需求
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="code" label="需求单号" width="150" />
        <el-table-column label="物资" min-width="200">
          <template #default="{ row }">
            {{ getMaterialName(row.materialId) }}
          </template>
        </el-table-column>
        <el-table-column prop="quantity" label="数量" width="100" />
        <el-table-column prop="unitPrice" label="期望单价" width="100">
          <template #default="{ row }">
            ¥{{ row.unitPrice?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="totalAmount" label="总金额" width="100">
          <template #default="{ row }">
            ¥{{ row.totalAmount?.toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="deptName" label="需求部门" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <template v-if="row.status === 'PENDING'">
              <el-button link type="success" @click="handleApprove(row)">通过</el-button>
              <el-button link type="danger" @click="handleReject(row)">驳回</el-button>
            </template>
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page" v-model:page-size="pageSize" :total="total"
        :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="loadData" @current-change="loadData" style="margin-top: 20px;"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑需求' : '新增需求'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="物资" prop="materialId">
          <el-select v-model="form.materialId" placeholder="请选择" style="width: 100%;" filterable>
            <el-option v-for="m in materialList" :key="m.id" :label="`${m.code} - ${m.name}`" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="需求数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="期望单价">
              <el-input-number v-model="form.unitPrice" :min="0" :precision="2" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="需求部门">
          <el-input v-model="form.deptName" />
        </el-form-item>
        <el-form-item label="期望日期">
          <el-date-picker v-model="form.expectedDate" type="date" value-format="YYYY-MM-DD" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="采购用途">
          <el-input v-model="form.purpose" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="rejectDialogVisible" title="驳回原因" width="400px">
      <el-input v-model="rejectComment" type="textarea" rows="3" placeholder="请输入驳回原因" />
      <template #footer>
        <el-button @click="rejectDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="confirmReject">确定驳回</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { requirementApi } from '../api/requirement'
import { materialApi } from '../api/material'

const loading = ref(false)
const tableData = ref([])
const materialList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const rejectDialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const currentRow = ref(null)
const rejectComment = ref('')

const form = reactive({
  id: null, materialId: null, quantity: 1, unitPrice: 0, totalAmount: 0,
  purpose: '', expectedDate: '', deptName: ''
})

const rules = {
  materialId: [{ required: true, message: '请选择物资', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入数量', trigger: 'blur' }]
}

const getMaterialName = (id) => {
  const m = materialList.value.find(item => item.id === id)
  return m ? `${m.code} - ${m.name}` : '-'
}

const getStatusType = (status) => {
  const map = { PENDING: 'warning', APPROVED: 'success', REJECTED: 'danger', CANCELLED: 'info', COMPLETED: '', PURCHASING: 'primary' }
  return map[status] || ''
}

const getStatusText = (status) => {
  const map = { PENDING: '待审批', APPROVED: '已审批', REJECTED: '已驳回', CANCELLED: '已取消', COMPLETED: '已完成', PURCHASING: '采购中' }
  return map[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await requirementApi.list({ page: page.value, pageSize: pageSize.value })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) { console.error(error) } finally {
    loading.value = false
  }
}

const loadMaterials = async () => {
  try {
    const res = await materialApi.all()
    materialList.value = res.data || []
  } catch (error) { console.error(error) }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, materialId: null, quantity: 1, unitPrice: 0, totalAmount: 0, purpose: '', expectedDate: '', deptName: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleApprove = async (row) => {
  await ElMessageBox.confirm('确定通过该需求吗？', '提示', { type: 'success' })
  try {
    await requirementApi.approve(row.id)
    ElMessage.success('已审批通过')
    loadData()
  } catch (error) { console.error(error) }
}

const handleReject = (row) => {
  currentRow.value = row
  rejectComment.value = ''
  rejectDialogVisible.value = true
}

const confirmReject = async () => {
  try {
    await requirementApi.reject(currentRow.value.id, rejectComment.value)
    ElMessage.success('已驳回')
    rejectDialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    form.totalAmount = form.quantity * form.unitPrice
    if (isEdit.value) {
      await requirementApi.create(form)
      ElMessage.success('更新成功')
    } else {
      await requirementApi.create(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
}

onMounted(() => { loadData(); loadMaterials() })
</script>

<style scoped>
.requirement-page { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>

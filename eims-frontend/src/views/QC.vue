<template>
  <div class="qc-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>质检记录</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增质检
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="code" label="质检单号" width="150" />
        <el-table-column label="物资" min-width="200">
          <template #default="{ row }">{{ getMaterialName(row.materialId) }}</template>
        </el-table-column>
        <el-table-column prop="batchNo" label="批次号" width="120" />
        <el-table-column prop="quantity" label="到货数量" width="100" />
        <el-table-column label="结果" width="100">
          <template #default="{ row }">
            <el-tag :type="row.result === 'PASS' ? 'success' : 'danger'">
              {{ row.result === 'PASS' ? '合格' : '不合格' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="qualifiedQty" label="合格数量" width="100" />
        <el-table-column prop="inspectTime" label="质检时间" width="160" />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page" v-model:page-size="pageSize" :total="total"
        :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="loadData" @current-change="loadData" style="margin-top: 20px;"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑质检' : '新增质检'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="物资" prop="materialId">
          <el-select v-model="form.materialId" placeholder="请选择" style="width: 100%;" filterable :disabled="isEdit">
            <el-option v-for="m in materialList" :key="m.id" :label="`${m.code} - ${m.name}`" :value="m.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="批次号">
          <el-input v-model="form.batchNo" :disabled="isEdit" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="到货数量" prop="quantity">
              <el-input-number v-model="form.quantity" :min="1" style="width: 100%;" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="抽样数量">
              <el-input-number v-model="form.sampleQty" :min="1" style="width: 100%;" :disabled="isEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="质检结果" prop="result">
          <el-radio-group v-model="form.result" :disabled="isEdit">
            <el-radio label="PASS">合格</el-radio>
            <el-radio label="FAIL">不合格</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="合格数量">
              <el-input-number v-model="form.qualifiedQty" :min="0" style="width: 100%;" :disabled="isEdit" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="不合格数量">
              <el-input-number v-model="form.unqualifiedQty" :min="0" style="width: 100%;" :disabled="isEdit" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="不合格原因" v-if="form.result === 'FAIL'">
          <el-input v-model="form.unqualifiedReason" type="textarea" rows="2" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :disabled="isEdit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { qcApi } from '../api/qc'
import { materialApi } from '../api/material'

const loading = ref(false)
const tableData = ref([])
const materialList = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  id: null, materialId: null, batchNo: '', quantity: 1, sampleQty: 1,
  result: 'PASS', qualifiedQty: 0, unqualifiedQty: 0, unqualifiedReason: '', remark: ''
})

const rules = {
  materialId: [{ required: true, message: '请选择物资', trigger: 'change' }],
  quantity: [{ required: true, message: '请输入到货数量', trigger: 'blur' }],
  result: [{ required: true, message: '请选择质检结果', trigger: 'change' }]
}

const getMaterialName = (id) => materialList.value.find(m => m.id === id)?.name || '-'

const loadData = async () => {
  loading.value = true
  try {
    const res = await qcApi.list({ page: page.value, pageSize: pageSize.value })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) { console.error(error) } finally { loading.value = false }
}

const loadMaterials = async () => {
  const res = await materialApi.all()
  materialList.value = res.data || []
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null, materialId: null, batchNo: '', quantity: 1, sampleQty: 1,
    result: 'PASS', qualifiedQty: 0, unqualifiedQty: 0, unqualifiedReason: '', remark: ''
  })
  dialogVisible.value = true
}

const handleView = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    await qcApi.create(form)
    ElMessage.success('质检记录已创建')
    dialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
}

onMounted(() => { loadData(); loadMaterials() })
</script>

<style scoped>
.qc-page { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>

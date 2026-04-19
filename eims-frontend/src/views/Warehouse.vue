<template>
  <div class="warehouse-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>仓库管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增仓库
          </el-button>
        </div>
      </template>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="code" label="仓库编码" width="120" />
        <el-table-column prop="name" label="仓库名称" min-width="200" />
        <el-table-column prop="address" label="地址" min-width="200" />
        <el-table-column prop="capacity" label="容量(m³)" width="100" />
        <el-table-column label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'info'">
              {{ row.status === 'ACTIVE' ? '正常' : '停用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑仓库' : '新增仓库'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="仓库编码" prop="code">
          <el-input v-model="form.code" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="仓库名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="容量">
          <el-input-number v-model="form.capacity" :min="0" style="width: 100%;" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.description" type="textarea" rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { warehouseApi } from '../api/warehouse'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({ id: null, code: '', name: '', address: '', capacity: 0, description: '' })
const rules = {
  code: [{ required: true, message: '请输入仓库编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入仓库名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await warehouseApi.list()
    tableData.value = res.data || []
  } catch (error) { console.error(error) } finally { loading.value = false }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, code: '', name: '', address: '', capacity: 0, description: '' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该仓库吗？', '提示', { type: 'warning' })
  try {
    await warehouseApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) { console.error(error) }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) {
      await warehouseApi.update(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await warehouseApi.create(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
}

onMounted(() => { loadData() })
</script>

<style scoped>
.warehouse-page { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
</style>

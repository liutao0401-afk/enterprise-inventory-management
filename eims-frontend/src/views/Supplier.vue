<template>
  <div class="supplier-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>供应商管理</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增供应商
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input v-model="keyword" placeholder="搜索供应商名称/编码" style="width: 300px;" clearable @keyup.enter="loadData">
          <template #prefix><el-icon><Search /></el-icon></template>
        </el-input>
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="code" label="供应商编码" width="120" />
        <el-table-column prop="name" label="供应商名称" min-width="200" />
        <el-table-column prop="contact" label="联系人" width="100" />
        <el-table-column prop="phone" label="联系电话" width="130" />
        <el-table-column prop="level" label="评级" width="80">
          <template #default="{ row }">
            <el-tag :type="getLevelType(row.level)">{{ row.level }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="onTimeRate" label="准时率" width="100">
          <template #default="{ row }">
            {{ row.onTimeRate }}%
          </template>
        </el-table-column>
        <el-table-column prop="qualifyRate" label="合格率" width="100">
          <template #default="{ row }">
            {{ row.qualifyRate }}%
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="page" v-model:page-size="pageSize" :total="total"
        :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
        @size-change="loadData" @current-change="loadData" style="margin-top: 20px;"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑供应商' : '新增供应商'" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="供应商编码" prop="code">
          <el-input v-model="form.code" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="供应商名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="联系人">
              <el-input v-model="form.contact" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话">
              <el-input v-model="form.phone" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="地址">
          <el-input v-model="form.address" />
        </el-form-item>
        <el-form-item label="主营产品">
          <el-input v-model="form.mainProduct" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="评级">
              <el-select v-model="form.level" style="width: 100%;">
                <el-option label="A级" value="A" />
                <el-option label="B级" value="B" />
                <el-option label="C级" value="C" />
                <el-option label="D级" value="D" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
import { supplierApi } from '../api/supplier'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const keyword = ref('')

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  id: null, code: '', name: '', contact: '', phone: '', address: '',
  mainProduct: '', level: 'B'
})

const rules = {
  code: [{ required: true, message: '请输入供应商编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入供应商名称', trigger: 'blur' }]
}

const getLevelType = (level) => {
  const map = { A: 'success', B: '', C: 'warning', D: 'danger' }
  return map[level] || ''
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await supplierApi.list({ page: page.value, pageSize: pageSize.value, keyword: keyword.value })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, { id: null, code: '', name: '', contact: '', phone: '', address: '', mainProduct: '', level: 'B' })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该供应商吗？', '提示', { type: 'warning' })
  try {
    await supplierApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) { console.error(error) }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  try {
    if (isEdit.value) {
      await supplierApi.update(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await supplierApi.create(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) { console.error(error) }
}

onMounted(() => { loadData() })
</script>

<style scoped>
.supplier-page { padding: 20px; }
.card-header { display: flex; justify-content: space-between; align-items: center; }
.search-bar { margin-bottom: 20px; display: flex; gap: 10px; }
</style>

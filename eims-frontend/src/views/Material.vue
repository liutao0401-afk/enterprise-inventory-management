<template>
  <div class="material-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>物资主档</span>
          <el-button type="primary" @click="handleAdd">
            <el-icon><Plus /></el-icon> 新增物资
          </el-button>
        </div>
      </template>

      <div class="search-bar">
        <el-input
          v-model="keyword"
          placeholder="搜索物资名称/编码"
          style="width: 300px;"
          clearable
          @clear="loadData"
          @keyup.enter="loadData"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-button type="primary" @click="loadData">搜索</el-button>
      </div>

      <el-table :data="tableData" stripe v-loading="loading">
        <el-table-column prop="code" label="物资编码" width="120" />
        <el-table-column prop="name" label="物资名称" min-width="200" />
        <el-table-column prop="spec" label="规格型号" min-width="150" />
        <el-table-column prop="unit" label="单位" width="80" />
        <el-table-column prop="currentStock" label="当前库存" width="100" />
        <el-table-column prop="safetyStock" label="安全库存" width="100" />
        <el-table-column prop="price" label="参考单价" width="100">
          <template #default="{ row }">
            ¥{{ row.price?.toFixed(2) }}
          </template>
        </el-table-column>
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

    <!-- 新增/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑物资' : '新增物资'"
      width="600px"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="物资编码" prop="code">
          <el-input v-model="form.code" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="物资名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="物资分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择" style="width: 100%;">
            <el-option
              v-for="cat in categoryList"
              :key="cat.id"
              :label="cat.name"
              :value="cat.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规格型号">
          <el-input v-model="form.spec" />
        </el-form-item>
        <el-form-item label="单位">
          <el-input v-model="form.unit" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="安全库存">
              <el-input-number v-model="form.safetyStock" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="最低库存">
              <el-input-number v-model="form.minStock" :min="0" style="width: 100%;" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="参考单价">
          <el-input-number v-model="form.price" :min="0" :precision="2" style="width: 100%;" />
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
import { materialApi } from '../api/material'
import { categoryApi } from '../api/category'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const page = ref(1)
const pageSize = ref(10)
const keyword = ref('')

const categoryList = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const form = reactive({
  id: null,
  code: '',
  name: '',
  categoryId: null,
  spec: '',
  unit: '个',
  safetyStock: 0,
  minStock: 0,
  currentStock: 0,
  price: 0
})

const rules = {
  code: [{ required: true, message: '请输入物资编码', trigger: 'blur' }],
  name: [{ required: true, message: '请输入物资名称', trigger: 'blur' }]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await materialApi.list({
      page: page.value,
      pageSize: pageSize.value,
      keyword: keyword.value
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadCategories = async () => {
  try {
    const res = await categoryApi.list()
    categoryList.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(form, {
    id: null,
    code: '',
    name: '',
    categoryId: null,
    spec: '',
    unit: '个',
    safetyStock: 0,
    minStock: 0,
    currentStock: 0,
    price: 0
  })
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确定要删除该物资吗？', '提示', { type: 'warning' })
  try {
    await materialApi.delete(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    if (isEdit.value) {
      await materialApi.update(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await materialApi.create(form)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>

<style scoped>
.material-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  gap: 10px;
}
</style>

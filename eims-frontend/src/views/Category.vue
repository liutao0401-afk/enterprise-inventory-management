<template>
  <div class="category-page">
    <el-card>
      <template #header>
        <div class="card-header">
          <span>物资分类</span>
          <el-button type="primary" @click="handleAdd(null)">
            <el-icon><Plus /></el-icon> 新增分类
          </el-button>
        </div>
      </template>

      <el-tree
        :data="treeData"
        :props="{ children: 'children', label: 'name' }"
        node-key="id"
        default-expand-all
        style="background: transparent;"
      >
        <template #default="{ node, data }">
          <span class="tree-node">
            <span>{{ data.name }}</span>
            <span class="tree-actions">
              <el-button link type="primary" size="small" @click.stop="handleAdd(data)">
                <el-icon><Plus /></el-icon>
              </el-button>
              <el-button link type="primary" size="small" @click.stop="handleEdit(data)">
                <el-icon><Edit /></el-icon>
              </el-button>
              <el-button link type="danger" size="small" @click.stop="handleDelete(data)">
                <el-icon><Delete /></el-icon>
              </el-button>
            </span>
          </span>
        </template>
      </el-tree>
    </el-card>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="400px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="上级分类">
          <el-input v-model="parentName" disabled />
        </el-form-item>
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="form.name" />
        </el-form-item>
        <el-form-item label="分类编码">
          <el-input v-model="form.code" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" style="width: 100%;" />
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { categoryApi } from '../api/category'

const loading = ref(false)
const tableData = ref([])
const treeData = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref()
const currentNode = ref(null)
const parentName = ref('顶级分类')

const form = reactive({
  id: null,
  parentId: 0,
  name: '',
  code: '',
  sort: 0
})

const rules = {
  name: [{ required: true, message: '请输入分类名称', trigger: 'blur' }]
}

const buildTree = (list, parentId = 0) => {
  return list
    .filter(item => item.parentId === parentId)
    .map(item => ({
      ...item,
      children: buildTree(list, item.id)
    }))
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await categoryApi.list()
    tableData.value = res.data || []
    treeData.value = buildTree(tableData.value)
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleAdd = (node) => {
  isEdit.value = false
  currentNode.value = node
  form.id = null
  form.parentId = node ? node.id : 0
  form.name = ''
  form.code = ''
  form.sort = 0
  parentName.value = node ? node.name : '顶级分类'
  dialogVisible.value = true
}

const handleEdit = (data) => {
  isEdit.value = true
  currentNode.value = data
  Object.assign(form, data)
  const parent = tableData.value.find(item => item.id === data.parentId)
  parentName.value = parent ? parent.name : '顶级分类'
  dialogVisible.value = true
}

const handleDelete = async (data) => {
  if (data.children && data.children.length > 0) {
    ElMessage.warning('存在子分类，无法删除')
    return
  }
  await ElMessageBox.confirm('确定要删除该分类吗？', '提示', { type: 'warning' })
  try {
    await categoryApi.delete(data.id)
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
      await categoryApi.update(form.id, form)
      ElMessage.success('更新成功')
    } else {
      await categoryApi.create(form)
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
})
</script>

<style scoped>
.category-page {
  padding: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tree-node {
  display: flex;
  justify-content: space-between;
  width: 100%;
  padding-right: 10px;
}

.tree-actions {
  display: none;
}

.tree-node:hover .tree-actions {
  display: flex;
  gap: 5px;
}
</style>

<template>
  <div class="inventory-page">
    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <template #header>
            <span>库存流水</span>
          </template>
          <div class="search-bar">
            <el-input v-model="keyword" placeholder="物资名称/编码" style="width: 200px;" clearable @keyup.enter="loadLog" />
            <el-button type="primary" @click="loadLog">搜索</el-button>
          </div>
          <el-table :data="logData" stripe v-loading="loading" size="small">
            <el-table-column prop="createTime" label="时间" width="160" />
            <el-table-column label="物资" min-width="150">
              <template #default="{ row }">{{ getMaterialName(row.materialId) }}</template>
            </el-table-column>
            <el-table-column label="类型" width="80">
              <template #default="{ row }">
                <el-tag :type="row.type === 'IN' ? 'success' : 'danger'" size="small">
                  {{ row.type === 'IN' ? '入库' : '出库' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="quantity" label="数量" width="80" />
            <el-table-column label="库存" width="120">
              <template #default="{ row }">{{ row.beforeStock }} → {{ row.afterStock }}</template>
            </el-table-column>
            <el-table-column prop="reason" label="原因" min-width="150" />
          </el-table>
          <el-pagination
            v-model:current-page="logPage" v-model:page-size="logPageSize" :total="logTotal"
            :page-sizes="[10, 20, 50]" layout="total, sizes, prev, pager, next"
            @size-change="loadLog" @current-change="loadLog" style="margin-top: 15px;"
          />
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="操作面板">
          <template #header>
            <span>快捷操作</span>
          </template>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="入库" name="in">
              <el-form :model="inForm" label-width="80px">
                <el-form-item label="物资">
                  <el-select v-model="inForm.materialId" placeholder="请选择" style="width: 100%;" filterable>
                    <el-option v-for="m in materialList" :key="m.id" :label="`${m.code} - ${m.name}`" :value="m.id" />
                  </el-select>
                </el-form-item>
                <el-form-item label="数量">
                  <el-input-number v-model="inForm.quantity" :min="1" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="原因">
                  <el-input v-model="inForm.reason" />
                </el-form-item>
                <el-form-item>
                  <el-button type="primary" style="width: 100%;" @click="handleIn">确认入库</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
            <el-tab-pane label="出库" name="out">
              <el-form :model="outForm" label-width="80px">
                <el-form-item label="物资">
                  <el-select v-model="outForm.materialId" placeholder="请选择" style="width: 100%;" filterable>
                    <el-option v-for="m in materialList" :key="m.id" :label="`${m.code} - ${m.name}`" :value="m.id" />
                  </el-select>
                </el-form-item>
                <el-form-item label="数量">
                  <el-input-number v-model="outForm.quantity" :min="1" style="width: 100%;" />
                </el-form-item>
                <el-form-item label="原因">
                  <el-input v-model="outForm.reason" />
                </el-form-item>
                <el-form-item>
                  <el-button type="danger" style="width: 100%;" @click="handleOut">确认出库</el-button>
                </el-form-item>
              </el-form>
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { inventoryApi } from '../api/inventory'
import { materialApi } from '../api/material'

const loading = ref(false)
const logData = ref([])
const materialList = ref([])
const logPage = ref(1)
const logPageSize = ref(10)
const logTotal = ref(0)
const keyword = ref('')
const activeTab = ref('in')

const inForm = reactive({ materialId: null, quantity: 1, reason: '采购入库' })
const outForm = reactive({ materialId: null, quantity: 1, reason: '领用出库' })

const getMaterialName = (id) => materialList.value.find(m => m.id === id)?.name || '-'

const loadLog = async () => {
  loading.value = true
  try {
    const res = await inventoryApi.log({ page: logPage.value, pageSize: logPageSize.value, keyword: keyword.value })
    logData.value = res.data?.records || []
    logTotal.value = res.data?.total || 0
  } catch (error) { console.error(error) } finally { loading.value = false }
}

const loadMaterials = async () => {
  const res = await materialApi.all()
  materialList.value = res.data || []
}

const handleIn = async () => {
  if (!inForm.materialId) { ElMessage.warning('请选择物资'); return }
  try {
    await inventoryApi.inbound(inForm)
    ElMessage.success('入库成功')
    inForm.quantity = 1
    loadLog()
  } catch (error) { console.error(error) }
}

const handleOut = async () => {
  if (!outForm.materialId) { ElMessage.warning('请选择物资'); return }
  try {
    await inventoryApi.outbound(outForm)
    ElMessage.success('出库成功')
    outForm.quantity = 1
    loadLog()
  } catch (error) { console.error(error) }
}

onMounted(() => { loadLog(); loadMaterials() })
</script>

<style scoped>
.inventory-page { padding: 20px; }
.search-bar { margin-bottom: 15px; display: flex; gap: 10px; }
</style>

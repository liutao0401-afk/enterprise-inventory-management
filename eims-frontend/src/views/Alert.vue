<template>
  <div class="alert-page">
    <el-card class="stats-card">
      <template #header>
        <span>库存预警统计</span>
      </template>
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item critical">
            <div class="stat-value">{{ stats.critical }}</div>
            <div class="stat-label">严重（断货）</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item warning">
            <div class="stat-value">{{ stats.warning }}</div>
            <div class="stat-label">警告（紧急补货）</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item info">
            <div class="stat-value">{{ stats.info }}</div>
            <div class="stat-label">提醒（安全库存）</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item normal">
            <div class="stat-value">{{ stats.normal }}</div>
            <div class="stat-label">正常</div>
          </div>
        </el-col>
      </el-row>
      <div class="stats-footer">
        <el-button type="primary" @click="handleCheckAlert">立即检测预警</el-button>
      </div>
    </el-card>

    <el-card class="alert-list-card">
      <template #header>
        <span>预警记录</span>
      </template>
      <el-table :data="alertList" stripe>
        <el-table-column prop="materialCode" label="物资编码" width="120" />
        <el-table-column prop="materialName" label="物资名称" min-width="150" />
        <el-table-column prop="currentStock" label="当前库存" width="100" />
        <el-table-column prop="safetyStock" label="安全库存" width="100" />
        <el-table-column prop="minStock" label="最低库存" width="100" />
        <el-table-column prop="status" label="预警级别" width="120">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { alertApi } from '../api/alert'
import { materialApi } from '../api/material'

const stats = reactive({
  critical: 0,
  warning: 0,
  info: 0,
  normal: 0,
  total: 0
})

const alertList = ref([])

const loadStats = async () => {
  try {
    const res = await alertApi.getStats()
    Object.assign(stats, res.data)
  } catch (error) {
    console.error(error)
  }
}

const loadAlertList = async () => {
  try {
    const res = await materialApi.lowStock()
    alertList.value = res.data || []
  } catch (error) {
    console.error(error)
  }
}

const handleCheckAlert = async () => {
  try {
    await alertApi.check()
    ElMessage.success('预警检测完成')
    await loadStats()
    await loadAlertList()
  } catch (error) {
    ElMessage.error('检测失败')
  }
}

const getStatusType = (status) => {
  const types = { CRITICAL: 'danger', WARNING: 'warning', INFO: 'info', NORMAL: 'success' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { CRITICAL: '断货', WARNING: '紧急补货', INFO: '安全库存', NORMAL: '正常' }
  return texts[status] || status
}

onMounted(() => {
  loadStats()
  loadAlertList()
})
</script>

<style scoped>
.alert-page {
  padding: 20px;
}

.stats-card {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
  border-radius: 8px;
}

.stat-item.critical { background-color: #fef0f0; }
.stat-item.warning { background-color: #fdf6ec; }
.stat-item.info { background-color: #ecf5ff; }
.stat-item.normal { background-color: #f0f9eb; }

.stat-value {
  font-size: 32px;
  font-weight: bold;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #666;
}

.stats-footer {
  margin-top: 20px;
  text-align: center;
}
</style>

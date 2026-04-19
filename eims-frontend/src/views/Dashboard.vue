<template>
  <div class="dashboard">
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #409eff;">
            <el-icon><Box /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.materialCount }}</div>
            <div class="stat-label">物资种类</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #67c23a;">
            <el-icon><TrendCharts /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.lowStockCount }}</div>
            <div class="stat-label">库存预警</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #e6a23c;">
            <el-icon><Document /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingRequirement }}</div>
            <div class="stat-label">待审批需求</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card">
          <div class="stat-icon" style="background: #f56c6c;">
            <el-icon><ShoppingCart /></el-icon>
          </div>
          <div class="stat-info">
            <div class="stat-value">{{ stats.pendingOrder }}</div>
            <div class="stat-label">待采购订单</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <el-row :gutter="20" class="content-row">
      <el-col :span="16">
        <el-card class="card">
          <template #header>
            <span>库存预警</span>
          </template>
          <el-table :data="lowStockList" stripe>
            <el-table-column prop="code" label="物资编码" width="120" />
            <el-table-column prop="name" label="物资名称" />
            <el-table-column prop="currentStock" label="当前库存" width="100" />
            <el-table-column prop="safetyStock" label="安全库存" width="100" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag type="danger" v-if="row.currentStock <= row.minStock">紧急</el-tag>
                <el-tag type="warning" v-else>预警</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="card">
          <template #header>
            <span>待审批需求</span>
          </template>
          <el-table :data="pendingList" stripe size="small">
            <el-table-column prop="code" label="单号" />
            <el-table-column prop="quantity" label="数量" width="80" />
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { materialApi } from '../api/material'
import { requirementApi } from '../api/requirement'
import { orderApi } from '../api/order'

const stats = ref({
  materialCount: 0,
  lowStockCount: 0,
  pendingRequirement: 0,
  pendingOrder: 0
})

const lowStockList = ref([])
const pendingList = ref([])

const loadData = async () => {
  try {
    // 物资总数
    const materialRes = await materialApi.all()
    stats.value.materialCount = materialRes.data?.length || 0

    // 库存预警
    const lowStockRes = await materialApi.lowStock()
    lowStockList.value = lowStockRes.data || []
    stats.value.lowStockCount = lowStockList.value.length

    // 待审批需求
    const pendingRes = await requirementApi.pending()
    pendingList.value = pendingRes.data || []
    stats.value.pendingRequirement = pendingList.value.length

    // 待处理订单
    const orderRes = await orderApi.list({ page: 1, pageSize: 100 })
    const orders = orderRes.data?.records || []
    stats.value.pendingOrder = orders.filter(o => ['DRAFT', 'CONFIRMED'].includes(o.status)).length
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #fff;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  color: #999;
  font-size: 14px;
}

.card {
  height: 100%;
}

.content-row {
  height: 400px;
}
</style>

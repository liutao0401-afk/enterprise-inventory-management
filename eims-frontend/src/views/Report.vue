<template>
  <div class="report-page">
    <el-card>
      <template #header>
        <div class="header-actions">
          <span>报表中心</span>
          <div class="report-type-select">
            <el-select v-model="reportType" placeholder="选择报表类型">
              <el-option label="库存台账" value="inventory-ledger" />
              <el-option label="采购汇总表" value="purchase-summary" />
              <el-option label="到货及时率" value="delivery-rate" />
              <el-option label="质检合格率" value="quality-rate" />
              <el-option label="物资周转率" value="turnover-rate" />
              <el-option label="需求漏斗" value="requirement-funnel" />
              <el-option label="供应商绩效" value="supplier-performance" />
              <el-option label="出入库流水" value="inventory-flow" />
              <el-option label="在途订单" value="pending-orders" />
            </el-select>
            <el-button type="primary" @click="loadReport">查询</el-button>
            <el-button @click="handleExport">导出</el-button>
          </div>
        </div>
      </template>

      <!-- 日期筛选 -->
      <div v-if="showDateFilter" class="date-filter">
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          format="YYYY-MM-DD"
          value-format="YYYY-MM-DD"
        />
      </div>

      <!-- 报表内容 -->
      <div class="report-content">
        <!-- 库存台账 -->
        <el-table v-if="reportType === 'inventory-ledger'" :data="tableData" stripe>
          <el-table-column prop="code" label="物资编码" width="120" />
          <el-table-column prop="name" label="物资名称" min-width="150" />
          <el-table-column prop="spec" label="规格型号" width="120" />
          <el-table-column prop="unit" label="单位" width="60" />
          <el-table-column prop="currentStock" label="当前库存" width="100" align="right" />
          <el-table-column prop="safetyStock" label="安全库存" width="100" align="right" />
          <el-table-column prop="minStock" label="最低库存" width="100" align="right" />
          <el-table-column prop="price" label="参考单价" width="100" align="right" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ row.status }}</el-tag>
            </template>
          </el-table-column>
        </el-table>

        <!-- 采购汇总表 -->
        <el-table v-else-if="reportType === 'purchase-summary'" :data="tableData" stripe>
          <el-table-column prop="month" label="月份" width="120" />
          <el-table-column prop="amount" label="采购金额" align="right">
            <template #default="{ row }">
              {{ formatCurrency(row.amount) }}
            </template>
          </el-table-column>
        </el-table>

        <!-- 到货及时率 -->
        <el-table v-else-if="reportType === 'delivery-rate'" :data="tableData" stripe>
          <el-table-column prop="supplierName" label="供应商名称" min-width="150" />
          <el-table-column prop="totalOrders" label="总订单数" width="100" align="right" />
          <el-table-column prop="onTimeOrders" label="准时订单数" width="100" align="right" />
          <el-table-column prop="onTimeRate" label="准时率" width="100" align="right">
            <template #default="{ row }">
              {{ row.onTimeRate }}%
            </template>
          </el-table-column>
        </el-table>

        <!-- 质检合格率 -->
        <el-table v-else-if="reportType === 'quality-rate'" :data="tableData" stripe>
          <el-table-column prop="materialName" label="物资名称" min-width="150" />
          <el-table-column prop="qualifyRate" label="合格率" width="100" align="right">
            <template #default="{ row }">
              {{ row.qualifyRate }}%
            </template>
          </el-table-column>
        </el-table>

        <!-- 物资周转率 -->
        <el-table v-else-if="reportType === 'turnover-rate'" :data="tableData" stripe>
          <el-table-column prop="materialCode" label="物资编码" width="120" />
          <el-table-column prop="materialName" label="物资名称" min-width="150" />
          <el-table-column prop="currentStock" label="当前库存" width="100" align="right" />
          <el-table-column prop="outAmount" label="出库总量" width="100" align="right" />
          <el-table-column prop="turnoverDays" label="周转天数" width="100" align="right" />
        </el-table>

        <!-- 需求漏斗 -->
        <div v-else-if="reportType === 'requirement-funnel'" class="funnel-container">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="funnel-item">
                <div class="funnel-value">{{ funnelData.total }}</div>
                <div class="funnel-label">总需求</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="funnel-item">
                <div class="funnel-value">{{ funnelData.approved }}</div>
                <div class="funnel-label">已审批</div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="funnel-item">
                <div class="funnel-value">{{ funnelData.completed }}</div>
                <div class="funnel-label">已完成</div>
              </div>
            </el-col>
          </el-row>
          <el-row :gutter="20" style="margin-top: 20px;">
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-value">{{ funnelData.pending }}</div>
                <div class="stat-label">待审批</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-value">{{ funnelData.purchasing }}</div>
                <div class="stat-label">采购中</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-value">{{ funnelData.rejected }}</div>
                <div class="stat-label">已驳回</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-card">
                <div class="stat-value">{{ funnelData.completionRate }}%</div>
                <div class="stat-label">完成率</div>
              </div>
            </el-col>
          </el-row>
        </div>

        <!-- 供应商绩效 -->
        <el-table v-else-if="reportType === 'supplier-performance'" :data="tableData" stripe>
          <el-table-column prop="supplierName" label="供应商名称" min-width="150" />
          <el-table-column prop="level" label="评级" width="80" />
          <el-table-column prop="onTimeRate" label="准时率" width="100" align="right" />
          <el-table-column prop="qualifyRate" label="合格率" width="100" align="right" />
          <el-table-column prop="score" label="综合评分" width="100" align="right" />
        </el-table>

        <!-- 出入库流水 -->
        <el-table v-else-if="reportType === 'inventory-flow'" :data="tableData" stripe>
          <el-table-column prop="createTime" label="操作时间" width="160" />
          <el-table-column prop="materialName" label="物资名称" min-width="120" />
          <el-table-column prop="type" label="类型" width="80">
            <template #default="{ row }">
              <el-tag :type="row.type === 'IN' ? 'success' : 'warning'">{{ row.type }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="quantity" label="数量" width="100" align="right" />
          <el-table-column prop="beforeStock" label="变动前库存" width="100" align="right" />
          <el-table-column prop="afterStock" label="变动后库存" width="100" align="right" />
          <el-table-column prop="reason" label="原因" min-width="120" />
        </el-table>

        <!-- 在途订单 -->
        <el-table v-else-if="reportType === 'pending-orders'" :data="tableData" stripe>
          <el-table-column prop="orderCode" label="订单编号" width="150" />
          <el-table-column prop="supplierName" label="供应商" min-width="120" />
          <el-table-column prop="totalAmount" label="订单金额" width="120" align="right">
            <template #default="{ row }">
              {{ formatCurrency(row.totalAmount) }}
            </template>
          </el-table-column>
          <el-table-column prop="expectedDate" label="期望日期" width="120" />
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag>{{ row.status }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="itemCount" label="明细数量" width="100" align="right" />
        </el-table>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'
import { reportApi } from '../api/report'

const reportType = ref('inventory-ledger')
const dateRange = ref([])
const tableData = ref([])
const funnelData = ref({})

const showDateFilter = computed(() => {
  return ['purchase-summary', 'delivery-rate', 'quality-rate', 'turnover-rate', 'inventory-flow'].includes(reportType.value)
})

const loadReport = async () => {
  try {
    let params = {}
    if (showDateFilter.value && dateRange.value) {
      params.startDate = dateRange.value[0]
      params.endDate = dateRange.value[1]
    }

    let res
    switch (reportType.value) {
      case 'inventory-ledger':
        res = await reportApi.inventoryLedger(params)
        tableData.value = res.data || []
        break
      case 'purchase-summary':
        res = await reportApi.purchaseSummary(params)
        tableData.value = res.data || []
        break
      case 'delivery-rate':
        res = await reportApi.deliveryRate(params)
        tableData.value = res.data || []
        break
      case 'quality-rate':
        res = await reportApi.qualityRate(params)
        tableData.value = res.data || []
        break
      case 'turnover-rate':
        res = await reportApi.turnoverRate(params)
        tableData.value = res.data || []
        break
      case 'requirement-funnel':
        res = await reportApi.requirementFunnel()
        funnelData.value = res.data || {}
        break
      case 'supplier-performance':
        res = await reportApi.supplierPerformance()
        tableData.value = res.data || []
        break
      case 'inventory-flow':
        res = await reportApi.inventoryFlow(params)
        tableData.value = res.data || []
        break
      case 'pending-orders':
        res = await reportApi.pendingOrders()
        tableData.value = res.data || []
        break
    }
  } catch (error) {
    ElMessage.error('加载报表失败')
  }
}

const handleExport = () => {
  ElMessage.info('导出功能开发中')
}

const getStatusType = (status) => {
  const types = { CRITICAL: 'danger', WARNING: 'warning', INFO: 'info', NORMAL: 'success' }
  return types[status] || 'info'
}

const formatCurrency = (value) => {
  if (!value) return '¥0.00'
  return '¥' + Number(value).toFixed(2)
}

loadReport()
</script>

<style scoped>
.report-page {
  padding: 20px;
}

.header-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.report-type-select {
  display: flex;
  gap: 10px;
}

.date-filter {
  margin: 20px 0;
}

.report-content {
  margin-top: 20px;
}

.funnel-container {
  padding: 20px;
}

.funnel-item {
  text-align: center;
  padding: 30px;
  background-color: #f5f7fa;
  border-radius: 8px;
}

.funnel-value {
  font-size: 36px;
  font-weight: bold;
  color: #409eff;
}

.funnel-label {
  margin-top: 10px;
  font-size: 14px;
  color: #666;
}

.stat-card {
  text-align: center;
  padding: 20px;
  background-color: #f5f7fa;
  border-radius: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
}

.stat-label {
  margin-top: 8px;
  font-size: 12px;
  color: #666;
}
</style>

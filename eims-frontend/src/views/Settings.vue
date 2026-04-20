<template>
  <div class="settings-page">
    <el-tabs v-model="activeTab">
      <!-- AI配置 -->
      <el-tab-pane label="AI配置" name="ai">
        <el-card>
          <template #header>
            <span>AI重复检测配置</span>
          </template>
          <el-form :model="aiForm" label-width="120px">
            <el-form-item label="启用AI检测">
              <el-switch v-model="aiForm.enabled" @change="handleAiSave" />
              <span class="tip">关闭后使用系统逻辑检测</span>
            </el-form-item>
            <el-form-item label="AI提供商">
              <el-select v-model="aiForm.provider" style="width: 300px;" @change="handleAiSave">
                <el-option label="MiniMax" value="MINIMAX" />
                <el-option label="OpenAI" value="OPENAI" />
                <el-option label="自定义" value="CUSTOM" />
              </el-select>
            </el-form-item>
            <el-form-item label="API地址">
              <el-input v-model="aiForm.apiEndpoint" style="width: 400px;" :disabled="!aiForm.enabled" />
            </el-form-item>
            <el-form-item label="API密钥">
              <el-input v-model="aiForm.apiKey" type="password" style="width: 400px;" show-password />
            </el-form-item>
            <el-form-item label="模型名称">
              <el-input v-model="aiForm.model" style="width: 300px;" :disabled="!aiForm.enabled" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleAiSave">保存配置</el-button>
              <el-button @click="handleAiTest">测试连接</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>

      <!-- 飞书配置 -->
      <el-tab-pane label="飞书配置" name="feishu">
        <el-card>
          <template #header>
            <span>飞书机器人配置</span>
          </template>
          <el-form :model="feishuForm" label-width="120px">
            <el-form-item label="启用飞书通知">
              <el-switch v-model="feishuForm.enabled" @change="handleFeishuSave" />
              <span class="tip">开启后将在关键事件时发送通知</span>
            </el-form-item>
            <el-form-item label="Webhook地址">
              <el-input v-model="feishuForm.webhookUrl" style="width: 500px;" placeholder="请输入飞书机器人Webhook地址" />
            </el-form-item>
            <el-form-item label="获取Webhook">
              <el-link type="primary" href="https://open.feishu.cn/document/uAjLw4CM/ukTMukTMukTM/bot-v3/bot/create" target="_blank">
                点击获取飞书机器人Webhook
              </el-link>
            </el-form-item>
            <el-form-item>
              <el-button type="primary" @click="handleFeishuSave">保存配置</el-button>
              <el-button @click="handleFeishuTest">发送测试消息</el-button>
            </el-form-item>
          </el-form>
        </el-card>

        <el-card style="margin-top: 20px;">
          <template #header>
            <span>通知场景</span>
          </template>
          <el-table :data="notifyScenarios" stripe>
            <el-table-column prop="event" label="事件" />
            <el-table-column prop="description" label="说明" />
            <el-table-column label="状态" width="100">
              <template #default="{ row }">
                <el-tag :type="row.enabled ? 'success' : 'info'">
                  {{ row.enabled ? '已启用' : '已禁用' }}
                </el-tag>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-tab-pane>

      <!-- 审批配置 -->
      <el-tab-pane label="审批配置" name="approval">
        <el-card>
          <template #header>
            <span>审批流程配置</span>
          </template>
          <el-form label-width="150px">
            <el-form-item label="一级审批金额上限">
              <el-input-number v-model="approvalConfig.level1Threshold" :min="0" :step="1000" />
              <span class="tip">金额小于此值，一级审批即可通过</span>
            </el-form-item>
            <el-form-item label="二级审批金额上限">
              <el-input-number v-model="approvalConfig.level2Threshold" :min="0" :step="10000" />
              <span class="tip">金额小于此值，二级审批即可通过</span>
            </el-form-item>
            <el-form-item>
              <el-button type="primary">保存配置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { aiConfigApi, feishuApi } from '../api/duplicateCheck'

const activeTab = ref('ai')

const aiForm = reactive({
  enabled: false,
  provider: 'MINIMAX',
  apiEndpoint: 'https://api.minimax.chat/v1',
  apiKey: '',
  model: 'MiniMax-01'
})

const feishuForm = reactive({
  enabled: false,
  webhookUrl: ''
})

const approvalConfig = reactive({
  level1Threshold: 10000,
  level2Threshold: 100000
})

const notifyScenarios = ref([
  { event: 'requirement_submit', description: '采购需求提交', enabled: true },
  { event: 'requirement_approve', description: '需求审批通过', enabled: true },
  { event: 'requirement_reject', description: '需求审批驳回', enabled: true },
  { event: 'stock_alert', description: '库存预警', enabled: true },
  { event: 'goods_arrived', description: '采购到货通知', enabled: true }
])

const loadAiConfig = async () => {
  try {
    const res = await aiConfigApi.getConfig()
    Object.assign(aiForm, res.data)
  } catch (error) {
    console.error(error)
  }
}

const loadFeishuConfig = async () => {
  try {
    const res = await feishuApi.getConfig()
    Object.assign(feishuForm, res.data)
  } catch (error) {
    console.error(error)
  }
}

const handleAiSave = async () => {
  try {
    await aiConfigApi.updateConfig(aiForm)
    ElMessage.success('AI配置已保存')
  } catch (error) {
    console.error(error)
  }
}

const handleAiTest = async () => {
  try {
    await aiConfigApi.test()
    ElMessage.success('AI连接正常')
  } catch (error) {
    ElMessage.error('AI连接失败')
  }
}

const handleFeishuSave = async () => {
  try {
    await feishuApi.updateConfig(feishuForm)
    ElMessage.success('飞书配置已保存')
  } catch (error) {
    console.error(error)
  }
}

const handleFeishuTest = async () => {
  try {
    const res = await feishuApi.test()
    if (res.data.success) {
      ElMessage.success('测试消息发送成功')
    } else {
      ElMessage.error(res.data.message)
    }
  } catch (error) {
    ElMessage.error('发送失败')
  }
}

onMounted(() => {
  loadAiConfig()
  loadFeishuConfig()
})
</script>

<style scoped>
.settings-page {
  padding: 20px;
}

.tip {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}
</style>

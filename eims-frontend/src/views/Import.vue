<template>
  <div class="import-page">
    <el-card>
      <template #header>
        <span>数据导入</span>
      </template>

      <el-steps :active="activeStep" finish-status="success" align-center>
        <el-step title="选择导入类型" />
        <el-step title="上传文件" />
        <el-step title="导入结果" />
      </el-steps>

      <div class="step-content">
        <!-- 步骤1: 选择导入类型 -->
        <div v-if="activeStep === 0" class="type-selection">
          <el-radio-group v-model="importType">
            <el-radio label="materials">物资主档</el-radio>
            <el-radio label="suppliers">供应商信息</el-radio>
            <el-radio label="requirements">采购需求</el-radio>
          </el-radio-group>
          <div class="template-download">
            <el-button type="text" @click="downloadTemplate">下载 {{ getTypeName() }} 导入模板</el-button>
          </div>
          <div class="next-btn">
            <el-button type="primary" @click="activeStep = 1">下一步</el-button>
          </div>
        </div>

        <!-- 步骤2: 上传文件 -->
        <div v-if="activeStep === 1" class="upload-section">
          <el-upload
            ref="uploadRef"
            class="upload-demo"
            drag
            :auto-upload="false"
            :limit="1"
            :on-change="handleFileChange"
            accept=".xlsx,.xls"
          >
            <el-icon class="el-icon--upload"><upload-filled /></el-icon>
            <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
            <template #tip>
              <div class="el-upload__tip">只能上传 xlsx/xls 文件</div>
            </template>
          </el-upload>
          <div class="upload-actions">
            <el-button @click="activeStep = 0">上一步</el-button>
            <el-button type="primary" :disabled="!selectedFile" @click="handleImport">开始导入</el-button>
          </div>
        </div>

        <!-- 步骤3: 导入结果 -->
        <div v-if="activeStep === 2" class="result-section">
          <el-alert
            v-if="importResult.fail === 0"
            title="导入成功"
            type="success"
            :description="`成功导入 ${importResult.success} 条数据`"
            show-icon
          />
          <el-alert
            v-else
            title="导入完成"
            type="warning"
            :description="`成功 ${importResult.success} 条，失败 ${importResult.fail} 条`"
            show-icon
          />
          <div v-if="importResult.errors && importResult.errors.length > 0" class="error-list">
            <h4>错误详情：</h4>
            <ul>
              <li v-for="(err, idx) in importResult.errors" :key="idx">{{ err }}</li>
            </ul>
          </div>
          <div class="result-actions">
            <el-button @click="resetImport">重新导入</el-button>
          </div>
        </div>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { importApi } from '../api/import'

const activeStep = ref(0)
const importType = ref('materials')
const selectedFile = ref(null)
const importResult = ref({ success: 0, fail: 0, errors: [] })

const getTypeName = () => {
  const names = { materials: '物资主档', suppliers: '供应商', requirements: '采购需求' }
  return names[importType.value] || ''
}

const downloadTemplate = () => {
  ElMessage.info('请从系统管理界面下载标准导入模板')
}

const handleFileChange = (file) => {
  selectedFile.value = file.raw
}

const handleImport = async () => {
  if (!selectedFile.value) {
    ElMessage.warning('请选择要导入的文件')
    return
  }

  try {
    let res
    switch (importType.value) {
      case 'materials':
        res = await importApi.importMaterials(selectedFile.value)
        break
      case 'suppliers':
        res = await importApi.importSuppliers(selectedFile.value)
        break
      case 'requirements':
        res = await importApi.importRequirements(selectedFile.value)
        break
    }
    importResult.value = res.data || { success: 0, fail: 0, errors: [] }
    activeStep.value = 2
    ElMessage.success('导入完成')
  } catch (error) {
    ElMessage.error('导入失败: ' + (error.message || '未知错误'))
  }
}

const resetImport = () => {
  activeStep.value = 0
  selectedFile.value = null
  importResult.value = { success: 0, fail: 0, errors: [] }
}
</script>

<style scoped>
.import-page {
  padding: 20px;
}

.step-content {
  margin-top: 40px;
  min-height: 300px;
}

.type-selection {
  text-align: center;
}

.type-selection .el-radio-group {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.template-download {
  margin-top: 30px;
}

.next-btn {
  margin-top: 40px;
}

.upload-section {
  text-align: center;
}

.upload-demo {
  display: flex;
  justify-content: center;
}

.upload-actions {
  margin-top: 30px;
  display: flex;
  justify-content: center;
  gap: 20px;
}

.result-section {
  text-align: center;
}

.error-list {
  margin-top: 20px;
  text-align: left;
  background-color: #fff5f5;
  padding: 15px;
  border-radius: 4px;
}

.error-list ul {
  margin: 0;
  padding-left: 20px;
}

.result-actions {
  margin-top: 30px;
}
</style>

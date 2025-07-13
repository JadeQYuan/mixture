<template>
  <div class="return-manage-container">
    <DataTable
      :data="records"
      :total="total"
      :loading="loading"
      :columns="columns"
      :search-fields="searchFields"
      :action-buttons="actionButtons"
      :header-buttons="headerButtons"
      @search="handleSearch"
      @reset="handleReset"
      @page-change="handlePageChange"
      @size-change="handleSizeChange"
      @action="handleAction"
      @header-action="handleHeaderAction"
    />

    <!-- 退料操作对话框 -->
    <el-dialog v-model="returnDialog.visible" title="退料操作" width="700px" :close-on-click-modal="false">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          退料操作
        </div>
      </template>
      <el-steps :active="returnDialog.step" finish-status="success" align-center style="margin-bottom: 24px; margin-top: 32px;">
        <el-step title="确认罐号/当前重量" />
        <el-step title="退料重量" />
        <el-step title="确认提交" />
      </el-steps>
      <el-form :model="returnDialog.form" :rules="returnDialog.rules" ref="returnFormRef" label-width="180px" style="margin-top: 32px;">
        <template v-if="returnDialog.step === 0">
          <el-form-item label="罐号">
            <el-input v-model="returnDialog.form.bucketNo" disabled size="large" />
          </el-form-item>
          <el-form-item label="当前重量">
            <el-input v-model="returnDialog.form.currentWeight" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
        </template>
        <template v-else-if="returnDialog.step === 1">
          <el-form-item label="罐号">
            <el-input v-model="returnDialog.form.bucketNo" disabled size="large" />
          </el-form-item>
          <el-form-item label="当前重量">
            <el-input v-model="returnDialog.form.currentWeight" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
          <el-form-item label="退料重量" prop="returnWeight" label-width="180px">
            <el-input v-model="returnDialog.form.returnWeight" type="number" style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
        </template>
        <template v-else-if="returnDialog.step === 2">
          <el-form-item label="罐号">
            <el-input v-model="returnDialog.form.bucketNo" disabled size="large" />
          </el-form-item>
          <el-form-item label="当前重量">
            <el-input v-model="returnDialog.form.currentWeight" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
          <el-form-item label="退料重量" label-width="180px">
            <el-input v-model="returnDialog.form.returnWeight" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <div style="padding: 0;">
          <el-button @click="closeReturnDialog" size="large">取消</el-button>
          <el-button v-if="returnDialog.step > 0" @click="prevStep" size="large">上一步</el-button>
          <el-button v-if="returnDialog.step < 2" type="primary" @click="nextStep" size="large">下一步</el-button>
          <el-button v-else type="success" @click="submitReturn" size="large" :loading="returnDialog.loading">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReturnManageList, submitReturnOperation, getTankWeightData } from '@/request/api'
import DataTable from '@/components/DataTable'
import { searchFields, columns, actionButtons, headerButtons } from './config'

// 数据
const records = ref([])
const loading = ref(false)
const total = ref(0)

// 退料对话框
const returnDialog = reactive({
  visible: false,
  step: 0,
  index: null,
  loading: false,
  form: { bucketNo: '', currentWeight: null, returnWeight: null },
  rules: {
    returnWeight: [ { required: true, message: '请输入退料重量', trigger: 'blur' } ]
  }
})

// 定时器相关
const weightTimer = ref(null)
const currentTankId = ref(null)

// 获取重量数据的函数
async function fetchWeightData() {
  if (currentTankId.value) {
    try {
      const response = await getTankWeightData()
      if (response.data) {
        returnDialog.form.currentWeight = response.data
      }
    } catch (error) {
      ElMessage.error('获取重量数据失败')
    }
  }
}

// 启动重量数据定时器
function startWeightTimer() {
  stopWeightTimer() // 先停止之前的定时器
  
  // 立即获取一次数据
  fetchWeightData()
  
  // 启动定时器，每秒获取一次
  weightTimer.value = setInterval(() => {
    fetchWeightData()
  }, 1000)
}

// 停止重量数据定时器
function stopWeightTimer() {
  if (weightTimer.value) {
    clearInterval(weightTimer.value)
    weightTimer.value = null
  }
}

// 事件处理函数
async function handleSearch(params) {
  loading.value = true
  try {
    const response = await getReturnManageList(params)
    records.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('获取退料管理列表失败')
  } finally {
    loading.value = false
  }
}

async function handleReset(params) {
  await handleSearch(params)
}

async function handlePageChange(params) {
  await handleSearch(params)
}

async function handleSizeChange(params) {
  await handleSearch(params)
}

function handleAction({ action, row, index }) {
  if (action === 'return') {
    openReturnDialog(index)
  }
}

function handleHeaderAction({ action }) {
  if (action === 'export') {
    ElMessage.success('导出功能待实现')
  }
}

// 退料对话框相关函数
function openReturnDialog(index) {
  returnDialog.visible = true
  returnDialog.step = 0
  returnDialog.index = index
  if (index >= 0) {
    const record = records.value[index]
    returnDialog.form.bucketNo = record.bucketNo
    currentTankId.value = record.bucketNo // 设置当前罐号
    // 启动定时器，获取当前重量
    startWeightTimer()
  } else {
    returnDialog.form.bucketNo = ''
    currentTankId.value = null
  }
  returnDialog.form.currentWeight = null
  returnDialog.form.returnWeight = null
}

function nextStep() {
  if (returnDialog.step === 0) {
    // 第0步：检查当前重量是否已获取
    if (returnDialog.form.currentWeight !== null && returnDialog.form.currentWeight !== undefined) {
      // 当前重量确定，进入第二步
      stopWeightTimer()
      returnDialog.step++
    } else {
      // 如果数据还未获取，立即获取一次
      fetchWeightData().then(() => {
        if (returnDialog.form.currentWeight !== null && returnDialog.form.currentWeight !== undefined) {
          stopWeightTimer()
          returnDialog.step++
        } else {
          ElMessage.warning('正在获取当前重量数据，请稍候')
        }
      })
    }
  } else if (returnDialog.step === 1) {
    // 第1步：验证退料重量输入
    returnFormRef.value.validateField('returnWeight', valid => {
      if (valid) {
        returnDialog.step++
        // 第三步不需要定时器，显示确认信息
      }
    })
  }
}

function prevStep() {
  if (returnDialog.step > 0) {
    returnDialog.step--
    // 如果返回到需要获取数据的步骤，重新启动定时器
    if (returnDialog.step === 0) {
      startWeightTimer()
    }
  }
}

function closeReturnDialog() {
  returnDialog.visible = false
  stopWeightTimer()
  returnDialog.step = 0
  returnDialog.form = { bucketNo: '', currentWeight: null, returnWeight: null }
}

async function submitReturn() {
  returnFormRef.value.validate(async (valid) => {
    if (valid) {
      returnDialog.loading = true
      try {
        const params = {
          bucketNo: returnDialog.form.bucketNo,
          returnWeight: returnDialog.form.returnWeight
        }
        await submitReturnOperation(params)
        ElMessage.success('退料操作成功')
        closeReturnDialog()
        await handleSearch({ page: 1, pageSize: 10 })
      } catch (error) {
        ElMessage.error('退料操作失败')
      } finally {
        returnDialog.loading = false
      }
    }
  })
}

const returnFormRef = ref()

// 页面加载时获取数据
onMounted(() => {
  handleSearch({ page: 1, pageSize: 10 })
})

// 组件卸载时清理定时器
onUnmounted(() => {
  stopWeightTimer()
})
</script>

<style scoped>
.return-manage-container {
  height: 100vh;
}
</style> 
<template>
  <div class="feed-manage-container">
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
    />

    <!-- 加料操作对话框 -->
    <el-dialog v-model="feedDialog.visible" title="加料操作" width="700px" :close-on-click-modal="false">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          加料操作
        </div>
      </template>
      <el-steps :active="feedDialog.step" finish-status="success" align-center style="margin-bottom: 24px; margin-top: 32px;">
        <el-step title="确认罐号/底罐重量" />
        <el-step title="加料重量" />
        <el-step title="阻燃粉重量" />
        <el-step title="确认提交" />
      </el-steps>
      <el-form :model="feedDialog.form" :rules="feedDialog.rules" ref="feedFormRef" label-width="180px" style="margin-top: 32px;">
        <template v-if="feedDialog.step === 0">
          <el-form-item label="罐号">
            <el-input v-model="feedDialog.form.bucketNo" disabled size="large" />
          </el-form-item>
          <el-form-item label="加料规格">
            <el-input v-model="feedDialog.form.spec" disabled size="large" />
          </el-form-item>
          <el-form-item label="底罐重量">
            <el-input v-model="feedDialog.form.capacity" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
        </template>
        <template v-else-if="feedDialog.step === 1">
          <el-form-item label="罐号">
            <el-input v-model="feedDialog.form.bucketNo" disabled size="large" />
          </el-form-item>
          <el-form-item label="加料规格">
            <el-input v-model="feedDialog.form.spec" disabled size="large" />
          </el-form-item>
          <el-form-item label="底罐重量">
            <el-input v-model="feedDialog.form.capacity" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
          <el-form-item label="加料重量">
            <el-input v-model="feedDialog.form.capacityAdd" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
        </template>
        <template v-else-if="feedDialog.step === 2">
          <el-form-item label="罐号">
            <el-input v-model="feedDialog.form.bucketNo" disabled size="large" />
          </el-form-item>
          <el-form-item label="加料规格">
            <el-input v-model="feedDialog.form.spec" disabled size="large" />
          </el-form-item>
          <el-form-item label="底罐重量">
            <el-input v-model="feedDialog.form.capacity" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
          <el-form-item label="加料重量">
            <el-input v-model="feedDialog.form.capacityAdd" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
          <el-form-item label="阻燃粉重量" prop="abs" label-width="180px">
            <el-input v-model="feedDialog.form.abs" type="number" :disabled="feedDialog.step > 2" style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
        </template>
        <template v-else-if="feedDialog.step === 3">
          <el-form-item label="罐号">
            <el-input v-model="feedDialog.form.bucketNo" disabled size="large" />
          </el-form-item>
          <el-form-item label="加料规格">
            <el-input v-model="feedDialog.form.spec" disabled size="large" />
          </el-form-item>
          <el-form-item label="底罐重量">
            <el-input v-model="feedDialog.form.capacity" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
          <el-form-item label="加料重量">
            <el-input v-model="feedDialog.form.capacityAdd" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
          <el-form-item label="阻燃粉重量" label-width="180px">
            <el-input v-model="feedDialog.form.abs" type="number" disabled style="width: 100%;" size="large">
              <template #suffix>kg</template>
            </el-input>
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <div style="padding: 0;">
          <el-button @click="closeFeedDialog" size="large">取消</el-button>
          <el-button v-if="feedDialog.step > 0" @click="prevStep" size="large">上一步</el-button>
          <el-button v-if="feedDialog.step < 3" type="primary" @click="nextStep" size="large">下一步</el-button>
          <el-button v-else type="success" @click="submitFeed" size="large" :loading="feedDialog.loading">提交</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedManageList, submitFeedOperation, getTankWeightData } from '@/request/api'
import DataTable from '@/components/DataTable'
import { searchFields, columns, actionButtons } from './config'

// 数据
const records = ref([])
const loading = ref(false)
const total = ref(0)

// 加料对话框
const feedDialog = reactive({
  visible: false,
  step: 0,
  index: null,
  loading: false,
  form: { bucketNo: '', spec: '', capacity: null, capacityAdd: null, abs: null },
  rules: {
    abs: [ { required: true, message: '请输入阻燃粉重量', trigger: 'blur' } ]
  }
})

// 定时器相关
const weightTimer = ref(null)
const currentTankId = ref(null)
const currentStep = ref(0)

// 获取重量数据的函数
async function fetchWeightData(step) {
  if (currentTankId.value) {
    try {
      const response = await getTankWeightData()
      if (response.data) {
        if (step === 0) {
          // 第一步：只获取底罐重量
          feedDialog.form.capacity = response.data
        } else if (step === 1) {
          // 第二步：获取加料重量
          feedDialog.form.capacityAdd = response.data
        }
      }
    } catch (error) {
      ElMessage.error('获取重量数据失败')
    }
  }
}

// 启动重量数据定时器
function startWeightTimer(step) {
  stopWeightTimer() // 先停止之前的定时器
  currentStep.value = step
  
  // 立即获取一次数据
  fetchWeightData(step)
  
  // 启动定时器，每秒获取一次
  weightTimer.value = setInterval(() => {
    fetchWeightData(step)
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
    const response = await getFeedManageList(params)
    records.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('获取加料管理列表失败')
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
  if (action === 'feed') {
    openFeedDialog(index)
  }
}

function handleHeaderAction({ action }) {
  if (action === 'add') {
    ElMessage.success('新增功能待实现')
  }
}





// 加料对话框相关函数
function openFeedDialog(index) {
  feedDialog.visible = true
  feedDialog.step = 0
  currentStep.value = 0
  feedDialog.index = index
  if (index >= 0) {
    const record = records.value[index]
    feedDialog.form.bucketNo = record.bucketNo
    feedDialog.form.spec = record.spec
    currentTankId.value = record.bucketNo // 设置当前罐号
    // 启动第一步定时器，获取底罐重量
    startWeightTimer(0)
  } else {
    feedDialog.form.bucketNo = ''
    feedDialog.form.spec = ''
    currentTankId.value = null
  }
  feedDialog.form.capacity = null
  feedDialog.form.capacityAdd = null
  feedDialog.form.abs = null
}

function nextStep() {
  if (feedDialog.step === 0) {
    // 第0步：检查底罐重量是否已获取
    if (feedDialog.form.capacity !== null && feedDialog.form.capacity !== undefined) {
      // 底罐重量确定，进入第二步
      stopWeightTimer()
      feedDialog.step++
      // 启动第二步定时器，立即获取加料重量
      startWeightTimer(1)
    } else {
      // 如果数据还未获取，立即获取一次
      fetchWeightData(0).then(() => {
        if (feedDialog.form.capacity !== null && feedDialog.form.capacity !== undefined) {
          stopWeightTimer()
          feedDialog.step++
          startWeightTimer(1)
        } else {
          ElMessage.warning('正在获取底罐重量数据，请稍候')
        }
      })
    }
  } else if (feedDialog.step === 1) {
    // 第1步：检查加料重量是否已获取
    if (feedDialog.form.capacityAdd !== null && feedDialog.form.capacityAdd !== undefined) {
      // 加料重量确定，进入第三步
      stopWeightTimer()
      feedDialog.step++
      // 第三步不需要定时器，用户手动输入阻燃粉重量
    } else {
      // 如果数据还未获取，立即获取一次
      fetchWeightData(1).then(() => {
        if (feedDialog.form.capacityAdd !== null && feedDialog.form.capacityAdd !== undefined) {
          stopWeightTimer()
          feedDialog.step++
        } else {
          ElMessage.warning('正在获取加料重量数据，请稍候')
        }
      })
    }
  } else if (feedDialog.step === 2) {
    // 第三步：验证阻燃粉重量输入
    feedFormRef.value.validateField('abs', valid => {
      if (valid) {
        feedDialog.step++
        // 第四步不需要定时器，显示确认信息
      }
    })
  }
}

function prevStep() {
  if (feedDialog.step > 0) {
    feedDialog.step--
    // 如果返回到需要获取数据的步骤，重新启动对应步骤的定时器并立即获取数据
    if (feedDialog.step === 0) {
      // 返回第一步，立即启动底罐重量获取
      startWeightTimer(0)
    } else if (feedDialog.step === 1) {
      // 返回第二步，立即启动加料重量获取
      startWeightTimer(1)
    }
  }
}

function closeFeedDialog() {
  feedDialog.visible = false
  stopWeightTimer()
  currentStep.value = 0
}

async function submitFeed() {
  if (feedDialog.loading) return // 防止重复提交
  
  try {
    feedDialog.loading = true
    // 确保清除定时器
    stopWeightTimer()
    currentStep.value = 0
    await submitFeedOperation(feedDialog.form)
    ElMessage.success('加料数据已提交！')
    feedDialog.visible = false
    await handleSearch({ page: 1, pageSize: 10 }) // 重新获取列表
  } catch (error) {
    ElMessage.error('提交失败')
  } finally {
    feedDialog.loading = false
  }
}

// 初始化
onMounted(() => {
  handleSearch({ page: 1, pageSize: 10 })
})

onUnmounted(() => {
  stopWeightTimer()
})
</script>

<style scoped>
.feed-manage-container {
  height: 100vh;
}

/* FeedManage页面特定样式 */

/* 弹窗表单输入框宽度 */
:deep(.el-dialog .el-input) {
  width: 85% !important;
}

:deep(.el-dialog .el-input-number) {
  width: 85% !important;
}

:deep(.el-input-number .el-input__suffix),
:deep(.el-input .el-input__suffix) {
  padding-left: 0 !important;
  margin-left: -6px !important;
  color: #333;
  font-size: 1em;
}

/* 确保弹窗表单字体大小 */
:deep(.el-dialog .el-form-item__label) {
  font-size: 20px !important;
}

:deep(.el-dialog .el-input__inner) {
  font-size: 20px !important;
}

:deep(.el-dialog .el-button) {
  font-size: 20px !important;
}
</style> 
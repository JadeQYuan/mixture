<template>
  <div class="feed-manage-container">
    <el-card class="feed-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="人员">
          <el-input v-model="searchForm.person" placeholder="请输入人员" clearable size="large" />
        </el-form-item>
        <el-form-item label="罐号">
          <el-input v-model="searchForm.tank" placeholder="请输入罐号" clearable size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="handleSearch">查询</el-button>
          <el-button size="large" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="table-header-bar">
        <div style="flex:1"></div>
        <div style="width: 120px;"></div>
      </div>
      <el-table :data="pagedRecords" style="width: 100%;" class="feed-table" v-loading="loading">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="person" label="人员" width="160" />
        <el-table-column prop="tank" label="罐号" width="160" />
        <el-table-column prop="spec" label="计划加料规格" width="160" />
        <el-table-column prop="weight" label="计划加料重量" width="160" />
        <el-table-column prop="time" label="时间" />
        <el-table-column label="操作" width="240">
          <template #default="scope">
            <el-button size="large" type="primary" @click="openFeedDialog(scope.$index)">加料</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-box">
        <el-pagination
          background
          layout="prev, pager, next, jumper, total"
          :total="filteredRecords.length"
          :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    <el-dialog v-model="feedDialog.visible" title="加料操作" width="500px" :close-on-click-modal="false">
      <template #header>
        <div style="text-align: center; font-size: 18px; font-weight: 900; color: #000;">
          加料操作
        </div>
      </template>
      <el-steps :active="feedDialog.step" finish-status="success" align-center style="margin-bottom: 24px;">
        <el-step title="确认罐号/底罐重量" />
        <el-step title="加料重量" />
        <el-step title="阻燃粉重量" />
        <el-step title="确认提交" />
      </el-steps>
      <el-form :model="feedDialog.form" :rules="feedDialog.rules" ref="feedFormRef" label-width="120px">
        <template v-if="feedDialog.step === 0">
          <el-form-item label="罐号">
            <el-input v-model="feedDialog.form.tank" disabled />
          </el-form-item>
          <el-form-item label="底罐重量" prop="baseWeight">
            <el-input-number v-model="feedDialog.form.baseWeight" :min="0.01" :precision="2" :disabled="feedDialog.step > 0" style="width: 100%;" />
          </el-form-item>
        </template>
        <template v-else-if="feedDialog.step === 1">
          <el-form-item label="罐号">
            <el-input v-model="feedDialog.form.tank" disabled />
          </el-form-item>
          <el-form-item label="底罐重量">
            <el-input-number v-model="feedDialog.form.baseWeight" :precision="2" disabled style="width: 100%;" />
          </el-form-item>
          <el-form-item label="加料重量" prop="feedWeight">
            <el-input-number v-model="feedDialog.form.feedWeight" :min="0.01" :precision="2" :disabled="feedDialog.step > 1" style="width: 100%;" />
          </el-form-item>
        </template>
        <template v-else-if="feedDialog.step === 2">
          <el-form-item label="罐号">
            <el-input v-model="feedDialog.form.tank" disabled />
          </el-form-item>
          <el-form-item label="底罐重量">
            <el-input-number v-model="feedDialog.form.baseWeight" :precision="2" disabled style="width: 100%;" />
          </el-form-item>
          <el-form-item label="加料重量">
            <el-input-number v-model="feedDialog.form.feedWeight" :precision="2" disabled style="width: 100%;" />
          </el-form-item>
          <el-form-item label="阻燃粉重量" prop="flameWeight">
            <el-input-number v-model="feedDialog.form.flameWeight" :min="0.00" :precision="2" :disabled="feedDialog.step > 2" style="width: 100%;" />
          </el-form-item>
        </template>
        <template v-else-if="feedDialog.step === 3">
          <el-form-item label="罐号">
            <el-input v-model="feedDialog.form.tank" disabled />
          </el-form-item>
          <el-form-item label="底罐重量">
            <el-input-number v-model="feedDialog.form.baseWeight" :precision="2" disabled style="width: 100%;" />
          </el-form-item>
          <el-form-item label="加料重量">
            <el-input-number v-model="feedDialog.form.feedWeight" :precision="2" disabled style="width: 100%;" />
          </el-form-item>
          <el-form-item label="阻燃粉重量">
            <el-input-number v-model="feedDialog.form.flameWeight" :precision="2" disabled style="width: 100%;" />
          </el-form-item>
        </template>
      </el-form>
      <template #footer>
        <div style="padding: 8px 0;">
          <el-button v-if="feedDialog.step > 0" @click="prevStep">上一步</el-button>
          <el-button v-if="feedDialog.step < 3" type="primary" @click="nextStep">确认</el-button>
          <el-button v-else type="success" @click="submitFeed">提交</el-button>
          <el-button @click="closeFeedDialog">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getFeedManageList, submitFeedOperation } from '../request/api'

const searchForm = reactive({
  person: '',
  tank: ''
})

const records = ref([])
const loading = ref(false)

const pageSize = 5
const currentPage = ref(1)

// 获取加料管理列表
async function fetchRecords() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize,
      person: searchForm.person,
      tank: searchForm.tank
    }
    const response = await getFeedManageList(params)
    records.value = response.data || []
  } catch (error) {
    console.error('获取加料管理列表失败:', error)
    ElMessage.error('获取加料管理列表失败')
  } finally {
    loading.value = false
  }
}

const filteredRecords = computed(() => {
  return records.value.filter(r => {
    const personMatch = !searchForm.person || r.person.includes(searchForm.person)
    const tankMatch = !searchForm.tank || r.tank.includes(searchForm.tank)
    return personMatch && tankMatch
  })
})

const pagedRecords = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredRecords.value.slice(start, start + pageSize)
})

async function handleSearch() {
  currentPage.value = 1
  await fetchRecords()
}
async function resetSearch() {
  searchForm.person = ''
  searchForm.tank = ''
  currentPage.value = 1
  await fetchRecords()
}
async function handlePageChange(page) {
  currentPage.value = page
  await fetchRecords()
}

const feedDialog = reactive({
  visible: false,
  step: 0,
  index: null,
  form: { tank: '', baseWeight: null, feedWeight: null, flameWeight: null },
  rules: {
    baseWeight: [ { required: true, message: '请输入底罐重量', trigger: 'blur' } ],
    feedWeight: [ { required: true, message: '请输入加料重量', trigger: 'blur' } ],
    flameWeight: [ { required: true, message: '请输入阻燃粉重量', trigger: 'blur' } ]
  }
})
const feedFormRef = ref()

function openFeedDialog(index) {
  feedDialog.visible = true
  feedDialog.step = 0
  feedDialog.index = index
  if (index >= 0) {
    const record = records.value[index]
    feedDialog.form.tank = record.tank
  } else {
    feedDialog.form.tank = ''
  }
  feedDialog.form.baseWeight = null
  feedDialog.form.feedWeight = null
  feedDialog.form.flameWeight = null
}
function nextStep() {
  if (feedDialog.step === 0) {
    feedFormRef.value.validateField('baseWeight', valid => {
      if (valid) feedDialog.step++
    })
  } else if (feedDialog.step === 1) {
    feedFormRef.value.validateField('feedWeight', valid => {
      if (valid) feedDialog.step++
    })
  } else if (feedDialog.step === 2) {
    feedFormRef.value.validateField('flameWeight', valid => {
      if (valid) feedDialog.step++
    })
  }
}
function prevStep() {
  if (feedDialog.step > 0) feedDialog.step--
}
function closeFeedDialog() {
  feedDialog.visible = false
}

async function submitFeed() {
  try {
    await submitFeedOperation(feedDialog.form)
    ElMessage.success('加料数据已提交！')
    feedDialog.visible = false
    await fetchRecords() // 重新获取列表
  } catch (error) {
    console.error('提交失败:', error)
    ElMessage.error('提交失败')
  }
}

// 页面加载时获取数据
onMounted(() => {
  fetchRecords()
})
</script>

<style scoped>
.feed-manage-container {
  margin: 20px;
}
.feed-card {
  background: rgba(255,255,255,0.8);
  border-radius: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.10);
  padding: 48px 40px 32px 40px;
  font-size: 1.08em;
  width: 100%;
  height: 100%;
  box-sizing: border-box;
  display: flex;
  flex-direction: column;
}
.table-header-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 12px;
}
.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 24px;
  margin-bottom: 24px;
}
.add-btn {
  margin-left: 24px;
}
.feed-table {
  margin-bottom: 24px;
  font-size: 14px;
}
.pagination-box {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
}
.el-form-item__label {
  font-size: 1em;
}
</style> 
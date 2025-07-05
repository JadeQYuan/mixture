<template>
  <div class="return-manage-container">
    <el-card class="return-card">
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
      <el-table :data="pagedRecords" style="width: 100%;" class="return-table" v-loading="loading">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="person" label="人员" width="160" />
        <el-table-column prop="tank" label="罐号" width="160" />
        <el-table-column prop="time" label="时间" />
        <el-table-column label="操作" width="240">
          <template #default="scope">
            <el-button size="large" type="primary" @click="openReturnDialog(scope.$index)">退料</el-button>
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
    <el-dialog v-model="returnDialog.visible" title="退料操作" width="400px" :close-on-click-modal="false">
      <template #header>
        <div style="text-align: center; font-size: 18px; font-weight: 900; color: #000;">
          退料操作
        </div>
      </template>
      <el-form :model="returnDialog.form" :rules="returnDialog.rules" ref="returnFormRef" label-width="120px">
        <el-form-item label="罐号">
          <el-input v-model="returnDialog.form.tank" disabled />
        </el-form-item>
        <el-form-item label="底罐重量" prop="baseWeight">
          <el-input-number v-model="returnDialog.form.baseWeight" :min="0.01" :precision="2" style="width: 100%;" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="padding: 8px 0;">
          <el-button type="primary" @click="submitReturn">确认</el-button>
          <el-button @click="closeReturnDialog">取消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getReturnManageList, submitReturnOperation } from '../request/api'

const searchForm = reactive({
  person: '',
  tank: ''
})

const records = ref([])
const loading = ref(false)

const pageSize = 5
const currentPage = ref(1)

// 获取退料管理列表
async function fetchRecords() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize,
      person: searchForm.person,
      tank: searchForm.tank
    }
    const response = await getReturnManageList(params)
    records.value = response.data || []
  } catch (error) {
    console.error('获取退料管理列表失败:', error)
    ElMessage.error('获取退料管理列表失败')
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

const returnDialog = reactive({
  visible: false,
  index: null,
  form: { tank: '', baseWeight: null },
  rules: {
    baseWeight: [ { required: true, message: '请输入底罐重量', trigger: 'blur' } ]
  }
})
const returnFormRef = ref()

function openReturnDialog(index) {
  returnDialog.visible = true
  returnDialog.index = index
  if (index >= 0) {
    const record = records.value[index]
    returnDialog.form.tank = record.tank
  } else {
    returnDialog.form.tank = ''
  }
  returnDialog.form.baseWeight = null
}
function closeReturnDialog() {
  returnDialog.visible = false
}

async function submitReturn() {
  returnFormRef.value.validate(async valid => {
    if (valid) {
      try {
        await submitReturnOperation(returnDialog.form)
        ElMessage.success('退料数据已提交！')
        returnDialog.visible = false
        await fetchRecords() // 重新获取列表
      } catch (error) {
        console.error('提交失败:', error)
        ElMessage.error('提交失败')
      }
    }
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchRecords()
})
</script>

<style scoped>
.return-manage-container {
  margin: 20px;
}
.return-card {
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
.return-table {
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
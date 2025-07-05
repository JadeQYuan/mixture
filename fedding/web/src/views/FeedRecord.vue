<template>
  <div class="feed-record-container">
    <el-card class="feed-record-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="人员">
          <el-input v-model="searchForm.person" placeholder="请输入人员" clearable size="large" />
        </el-form-item>
        <el-form-item label="罐号">
          <el-input v-model="searchForm.tank" placeholder="请输入罐号" clearable size="large" />
        </el-form-item>
        <el-form-item label="时间">
          <el-date-picker
            v-model="searchForm.time"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            value-format="YYYY-MM-DD"
            style="width: 240px;"
            size="large"
          />
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
      <el-table :data="pagedRecords" style="width: 100%;" class="feed-record-table" v-loading="loading">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="person" label="人员" width="160" />
        <el-table-column prop="tank" label="罐号" width="160" />
        <el-table-column prop="baseWeight" label="罐底重量" width="160" />
        <el-table-column prop="feedWeight" label="加料重量" width="160" />
        <el-table-column prop="flameWeight" label="阻燃粉" width="160" />
        <el-table-column prop="time" label="时间" />
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
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { getFeedRecordList } from '../request/api'
import { ElMessage } from 'element-plus'

const searchForm = reactive({
  person: '',
  tank: '',
  time: []
})

const records = ref([])
const loading = ref(false)

const pageSize = 5
const currentPage = ref(1)

// 获取加料记录列表
async function fetchRecords() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize,
      person: searchForm.person,
      tank: searchForm.tank,
      startTime: searchForm.time?.[0],
      endTime: searchForm.time?.[1]
    }
    const response = await getFeedRecordList(params)
    records.value = response.data || []
  } catch (error) {
    console.error('获取加料记录列表失败:', error)
    ElMessage.error('获取加料记录列表失败')
  } finally {
    loading.value = false
  }
}

const filteredRecords = computed(() => {
  return records.value.filter(r => {
    const personMatch = !searchForm.person || r.person.includes(searchForm.person)
    const tankMatch = !searchForm.tank || r.tank.includes(searchForm.tank)
    let timeMatch = true
    if (searchForm.time && searchForm.time.length === 2) {
      const [start, end] = searchForm.time
      timeMatch = r.time >= start && r.time <= end
    }
    return personMatch && tankMatch && timeMatch
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
  searchForm.time = []
  currentPage.value = 1
  await fetchRecords()
}
async function handlePageChange(page) {
  currentPage.value = page
  await fetchRecords()
}

// 页面加载时获取数据
onMounted(() => {
  fetchRecords()
})
</script>

<style scoped>
.feed-record-container {
  margin: 20px;
}
.feed-record-card {
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
.search-form {
  display: flex;
  flex-wrap: wrap;
  align-items: flex-end;
  gap: 24px;
  margin-bottom: 24px;
}
.table-header-bar {
  display: flex;
  justify-content: flex-end;
  align-items: center;
  margin-bottom: 12px;
}
.feed-record-table {
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
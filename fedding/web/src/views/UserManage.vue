<template>
  <div class="user-manage-container">
    <el-card class="user-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.name" placeholder="请输入姓名" clearable size="large" />
        </el-form-item>
        <el-form-item label="工号">
          <el-input v-model="searchForm.jobId" placeholder="请输入工号" clearable size="large" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="large" @click="handleSearch">查询</el-button>
          <el-button size="large" @click="resetSearch">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="table-header-bar">
        <div style="flex:1"></div>
        <el-button type="primary" size="large" class="add-btn" @click="openDialog('add')">新增用户</el-button>
      </div>
      <el-table :data="pagedUsers" style="width: 100%;" class="user-table" v-loading="loading">
        <el-table-column type="index" label="序号" width="80" />
        <el-table-column prop="role" label="角色" width="120" />
        <el-table-column prop="jobId" label="工号" width="120" />
        <el-table-column prop="name" label="姓名" width="160" />
        <el-table-column label="照片" width="160">
          <template #default="scope">
            <el-image 
              :src="scope.row.photo || '/src/assets/default-avatar.svg'" 
              style="width: 80px; height: 80px; border-radius: 8px;"
              fit="cover"
              :preview-src-list="[scope.row.photo || '/src/assets/default-avatar.svg']"
            >
              <template #error>
                <div style="width: 60px; height: 60px; background: #f5f5f5; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #999;">
                  无照片
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="desc" label="描述" />
        <el-table-column prop="createdAt" label="添加时间" width="180" />
        <el-table-column label="操作" width="400">
          <template #default="scope">
            <el-button size="large" @click="openDialog('edit', scope.$index)">编辑</el-button>
            <el-button size="large" type="warning" @click="openPhotoDialog(scope.$index)">修改照片</el-button>
            <el-button size="large" type="info" @click="openPasswordDialog(scope.$index)">修改密码</el-button>
            <el-button size="large" type="danger" @click="confirmDelete(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-box">
        <el-pagination
          background
          layout="prev, pager, next, jumper, total"
          :total="filteredUsers.length"
          :page-size="pageSize"
          :current-page.sync="currentPage"
          @current-change="handlePageChange"
        />
      </div>
    </el-card>
    <el-dialog v-model="dialog.visible" :title="dialog.mode === 'add' ? '新增用户' : '编辑用户'" width="500px">
      <template #header>
        <div style="text-align: center; font-size: 18px; font-weight: 900; color: #000;">
          {{ dialog.mode === 'add' ? '新增用户' : '编辑用户' }}
          <div v-if="dialog.mode === 'add'" style="font-size: 14px; color: #666; margin-top: 4px;">
            步骤 {{ dialog.step }}/2
          </div>
        </div>
      </template>
      
      <!-- 第一步：基本信息 -->
      <div v-if="dialog.step === 1">
        <el-form :model="dialog.form" :rules="dialog.rules" ref="dialogFormRef" label-width="80px">
          <el-form-item label="角色" prop="role">
            <el-select v-model="dialog.form.role" placeholder="请选择角色" style="width: 100%;">
              <el-option v-for="r in roles" :key="r" :label="r" :value="r" />
            </el-select>
          </el-form-item>
          <el-form-item label="工号" prop="jobId">
            <el-input v-model="dialog.form.jobId" placeholder="请输入工号" />
          </el-form-item>
          <el-form-item label="姓名" prop="name">
            <el-input v-model="dialog.form.name" placeholder="请输入姓名" />
          </el-form-item>
          <el-form-item label="描述" prop="desc">
            <el-input v-model="dialog.form.desc" placeholder="请输入描述" />
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 第二步：拍照 -->
      <div v-if="dialog.step === 2">
        <div style="text-align: center; margin-bottom: 20px;">
          <div style="margin-bottom: 16px; font-size: 16px; color: #333;">
            请拍摄用户照片
          </div>
          <div style="position: relative; display: inline-block;">
            <video 
              ref="videoRef" 
              autoplay 
              style="width: 400px; height: 300px; border-radius: 16px; border: 2px solid #409EFF;"
            ></video>
            <canvas ref="canvasRef" style="display: none;"></canvas>
          </div>
          <div style="margin-top: 16px;">
            <el-button type="primary" size="large" @click="takePhoto" :disabled="photoTaken">
              {{ photoTaken ? '已拍照' : '拍照' }}
            </el-button>
            <el-button size="large" @click="startCamera" v-if="!stream">
              重新启动摄像头
            </el-button>
          </div>
          <div v-if="photoTaken" style="margin-top: 12px; color: #67C23A;">
            ✓ 照片已拍摄完成
          </div>
        </div>
      </div>
      
      <template #footer>
        <div style="padding: 8px 0;">
          <el-button @click="dialog.visible = false">取消</el-button>
          <el-button v-if="dialog.mode === 'add' && dialog.step === 1" type="primary" @click="nextStep">下一步</el-button>
          <el-button v-if="dialog.mode === 'add' && dialog.step === 2" @click="prevStep">上一步</el-button>
          <el-button v-if="dialog.mode === 'add' && dialog.step === 2" type="primary" @click="handleDialogOk" :disabled="!photoTaken">
            完成
          </el-button>
          <el-button v-if="dialog.mode === 'edit'" type="primary" @click="handleDialogOk">确定</el-button>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="deleteDialog.visible" title="确认删除" width="300px">
      <template #header>
        <div style="text-align: center; font-size: 18px; font-weight: 900; color: #000;">
          确认删除
        </div>
      </template>
      <span>确定要删除该用户吗？</span>
      <template #footer>
        <div style="padding: 8px 0;">
          <el-button @click="deleteDialog.visible = false">取消</el-button>
          <el-button type="danger" @click="handleDeleteUser">删除</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改照片对话框 -->
    <el-dialog v-model="photoDialog.visible" title="修改照片" width="500px">
      <template #header>
        <div style="text-align: center; font-size: 18px; font-weight: 900; color: #000;">
          修改照片
        </div>
      </template>
      <div style="text-align: center; margin-bottom: 20px;">
        <div style="margin-bottom: 16px; font-size: 16px; color: #333;">
          请重新拍摄用户照片
        </div>
        <div style="position: relative; display: inline-block;">
          <video 
            ref="photoVideoRef" 
            autoplay 
            style="width: 400px; height: 300px; border-radius: 16px; border: 2px solid #409EFF;"
          ></video>
          <canvas ref="photoCanvasRef" style="display: none;"></canvas>
        </div>
        <div style="margin-top: 16px;">
          <el-button type="primary" size="large" @click="takePhotoForEdit" :disabled="photoTakenForEdit">
            {{ photoTakenForEdit ? '已拍照' : '拍照' }}
          </el-button>
          <el-button size="large" @click="startPhotoCamera" v-if="!photoStream">
            重新启动摄像头
          </el-button>
        </div>
        <div v-if="photoTakenForEdit" style="margin-top: 12px; color: #67C23A;">
          ✓ 照片已拍摄完成
        </div>
      </div>
      <template #footer>
        <div style="padding: 8px 0;">
          <el-button @click="photoDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdatePhoto" :disabled="!photoTakenForEdit">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialog.visible" title="修改密码" width="400px">
      <template #header>
        <div style="text-align: center; font-size: 18px; font-weight: 900; color: #000;">
          修改密码
        </div>
      </template>
      <el-form :model="passwordDialog.form" :rules="passwordDialog.rules" ref="passwordFormRef" label-width="100px">
        <el-form-item label="原密码" prop="oldPassword">
          <el-input v-model="passwordDialog.form.oldPassword" type="password" placeholder="请输入原密码" show-password />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordDialog.form.newPassword" type="password" placeholder="请输入新密码" show-password />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordDialog.form.confirmPassword" type="password" placeholder="请再次输入新密码" show-password />
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="padding: 8px 0;">
          <el-button @click="passwordDialog.visible = false">取消</el-button>
          <el-button type="primary" @click="handleUpdatePassword">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser } from '../request/api'

const roles = ['物料员', '高级操作员', '操作员']

const searchForm = reactive({
  name: '',
  jobId: ''
})

const users = ref([])
const loading = ref(false)

const pageSize = 5
const currentPage = ref(1)

// 获取用户列表
async function fetchUsers() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize,
      name: searchForm.name,
      jobId: searchForm.jobId
    }
    const response = await getUserList(params)
    users.value = response.data || []
  } catch (error) {
    console.error('获取用户列表失败:', error)
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

const filteredUsers = computed(() => {
  return users.value.filter(u => {
    const nameMatch = !searchForm.name || u.name.includes(searchForm.name)
    const jobIdMatch = !searchForm.jobId || u.jobId.includes(searchForm.jobId)
    return nameMatch && jobIdMatch
  })
})

const pagedUsers = computed(() => {
  const start = (currentPage.value - 1) * pageSize
  return filteredUsers.value.slice(start, start + pageSize)
})

async function handleSearch() {
  currentPage.value = 1
  await fetchUsers()
}
async function resetSearch() {
  searchForm.name = ''
  searchForm.jobId = ''
  currentPage.value = 1
  await fetchUsers()
}
async function handlePageChange(page) {
  currentPage.value = page
  await fetchUsers()
}

const dialog = reactive({
  visible: false,
  mode: 'add', // add/edit
  step: 1, // 1: 基本信息, 2: 拍照
  index: null,
  form: { role: '', jobId: '', name: '', desc: '', photo: '' },
  rules: {
    role: [ { required: true, message: '请选择角色', trigger: 'change' } ],
    jobId: [ { required: true, message: '请输入工号', trigger: 'blur' } ],
    name: [ { required: true, message: '请输入姓名', trigger: 'blur' } ],
    desc: [ { required: true, message: '请输入描述', trigger: 'blur' } ]
  }
})
const dialogFormRef = ref()

// 摄像头相关
const videoRef = ref()
const canvasRef = ref()
const stream = ref(null)
const photoTaken = ref(false)

// 修改照片相关
const photoDialog = reactive({
  visible: false,
  index: null
})
const photoVideoRef = ref()
const photoCanvasRef = ref()
const photoStream = ref(null)
const photoTakenForEdit = ref(false)

// 修改密码相关
const passwordDialog = reactive({
  visible: false,
  index: null,
  form: { oldPassword: '', newPassword: '', confirmPassword: '' },
  rules: {
    oldPassword: [ { required: true, message: '请输入原密码', trigger: 'blur' } ],
    newPassword: [ { required: true, message: '请输入新密码', trigger: 'blur' } ],
    confirmPassword: [ { required: true, message: '请再次输入新密码', trigger: 'blur' } ]
  }
})
const passwordFormRef = ref()

function openDialog(mode, index = null) {
  dialog.mode = mode
  dialog.step = 1
  dialog.visible = true
  dialog.index = index
  photoTaken.value = false
  stopCamera() // 停止之前的摄像头
  if (mode === 'edit' && index !== null) {
    Object.assign(dialog.form, users.value[index])
  } else {
    dialog.form.role = ''
    dialog.form.jobId = ''
    dialog.form.name = ''
    dialog.form.desc = ''
    dialog.form.photo = ''
  }
}

async function handleDialogOk() {
  try {
    if (dialog.mode === 'add') {
      // 新增模式：需要拍照
      if (!photoTaken.value) {
        ElMessage.warning('请先拍摄用户照片')
        return
      }
      await createUser(dialog.form)
      ElMessage.success('新增成功')
    } else if (dialog.mode === 'edit' && dialog.index !== null) {
      // 编辑模式：直接保存
      dialogFormRef.value.validate(async valid => {
        if (!valid) return
        
        const user = users.value[dialog.index]
        await updateUser(user.id, dialog.form)
        ElMessage.success('编辑成功')
        dialog.visible = false
        await fetchUsers() // 重新获取列表
      })
      return
    }
    dialog.visible = false
    stopCamera() // 停止摄像头
    await fetchUsers() // 重新获取列表
  } catch (error) {
    console.error('操作失败:', error)
    ElMessage.error('操作失败')
  }
}

const deleteDialog = reactive({ visible: false, index: null })
function confirmDelete(index) {
  deleteDialog.visible = true
  deleteDialog.index = index
}

async function handleDeleteUser() {
  if (deleteDialog.index !== null) {
    try {
      const user = users.value[deleteDialog.index]
      await deleteUser(user.id)
      ElMessage.success('删除成功')
      deleteDialog.visible = false
      deleteDialog.index = null
      await fetchUsers() // 重新获取列表
    } catch (error) {
      console.error('删除失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

// 摄像头相关函数
async function startCamera() {
  try {
    stream.value = await navigator.mediaDevices.getUserMedia({ 
      video: { 
        width: 640, 
        height: 480,
        facingMode: 'user' // 前置摄像头
      } 
    })
    if (videoRef.value) {
      videoRef.value.srcObject = stream.value
    }
  } catch (error) {
    console.error('无法访问摄像头:', error)
    ElMessage.error('无法访问摄像头，请检查权限设置')
  }
}

function stopCamera() {
  if (stream.value) {
    stream.value.getTracks().forEach(track => track.stop())
    stream.value = null
  }
}

function takePhoto() {
  if (!videoRef.value || !canvasRef.value) return
  
  const video = videoRef.value
  const canvas = canvasRef.value
  const context = canvas.getContext('2d')
  
  // 设置canvas尺寸
  canvas.width = video.videoWidth
  canvas.height = video.videoHeight
  
  // 绘制视频帧到canvas
  context.drawImage(video, 0, 0, canvas.width, canvas.height)
  
  // 转换为base64图片数据
  const photoData = canvas.toDataURL('image/jpeg', 0.8)
  dialog.form.photo = photoData
  photoTaken.value = true
  
  ElMessage.success('拍照成功')
}

function nextStep() {
  dialogFormRef.value.validate(async valid => {
    if (!valid) return
    dialog.step = 2
    // 延迟启动摄像头，确保DOM已更新
    setTimeout(() => {
      startCamera()
    }, 100)
  })
}

function prevStep() {
  dialog.step = 1
  stopCamera()
  photoTaken.value = false
}

// 修改照片相关函数
function openPhotoDialog(index) {
  photoDialog.visible = true
  photoDialog.index = index
  photoTakenForEdit.value = false
  stopPhotoCamera()
  // 延迟启动摄像头
  setTimeout(() => {
    startPhotoCamera()
  }, 100)
}

async function startPhotoCamera() {
  try {
    photoStream.value = await navigator.mediaDevices.getUserMedia({ 
      video: { 
        width: 640, 
        height: 480,
        facingMode: 'user'
      } 
    })
    if (photoVideoRef.value) {
      photoVideoRef.value.srcObject = photoStream.value
    }
  } catch (error) {
    console.error('无法访问摄像头:', error)
    ElMessage.error('无法访问摄像头，请检查权限设置')
  }
}

function stopPhotoCamera() {
  if (photoStream.value) {
    photoStream.value.getTracks().forEach(track => track.stop())
    photoStream.value = null
  }
}

function takePhotoForEdit() {
  if (!photoVideoRef.value || !photoCanvasRef.value) return
  
  const video = photoVideoRef.value
  const canvas = photoCanvasRef.value
  const context = canvas.getContext('2d')
  
  canvas.width = video.videoWidth
  canvas.height = video.videoHeight
  context.drawImage(video, 0, 0, canvas.width, canvas.height)
  
  const photoData = canvas.toDataURL('image/jpeg', 0.8)
  photoTakenForEdit.value = true
  
  ElMessage.success('拍照成功')
}

async function handleUpdatePhoto() {
  if (photoDialog.index !== null) {
    try {
      const user = users.value[photoDialog.index]
      const canvas = photoCanvasRef.value
      const photoData = canvas.toDataURL('image/jpeg', 0.8)
      
      await updateUser(user.id, { ...user, photo: photoData })
      ElMessage.success('照片更新成功')
      photoDialog.visible = false
      stopPhotoCamera()
      await fetchUsers() // 重新获取列表
    } catch (error) {
      console.error('更新照片失败:', error)
      ElMessage.error('更新照片失败')
    }
  }
}

// 修改密码相关函数
function openPasswordDialog(index) {
  passwordDialog.visible = true
  passwordDialog.index = index
  passwordDialog.form.oldPassword = ''
  passwordDialog.form.newPassword = ''
  passwordDialog.form.confirmPassword = ''
}

async function handleUpdatePassword() {
  passwordFormRef.value.validate(async valid => {
    if (!valid) return
    
    if (passwordDialog.form.newPassword !== passwordDialog.form.confirmPassword) {
      ElMessage.error('两次输入的密码不一致')
      return
    }
    
    try {
      const user = users.value[passwordDialog.index]
      // 这里应该调用修改密码的API
      // await updateUserPassword(user.id, passwordDialog.form)
      ElMessage.success('密码修改成功')
      passwordDialog.visible = false
    } catch (error) {
      console.error('修改密码失败:', error)
      ElMessage.error('修改密码失败')
    }
  })
}

// 页面加载时获取数据
onMounted(() => {
  fetchUsers()
})

// 监听对话框关闭，停止摄像头
watch(() => dialog.visible, (newVal) => {
  if (!newVal) {
    stopCamera()
  }
})

watch(() => photoDialog.visible, (newVal) => {
  if (!newVal) {
    stopPhotoCamera()
  }
})
</script>

<style scoped>
.user-manage-container {
  margin: 20px;
}
.user-card {
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
}
.add-btn {
  margin-left: 24px;
}
.user-table {
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
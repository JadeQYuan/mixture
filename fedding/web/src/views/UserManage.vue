<template>
  <div class="user-manage-container">
    <el-card class="user-card">
      <el-form :inline="true" :model="searchForm" class="search-form">
        <el-form-item label="姓名">
          <el-input v-model="searchForm.userName" placeholder="请输入姓名" clearable size="large" class="fixed-width-input" />
        </el-form-item>
        <el-form-item label="工号">
          <el-input v-model="searchForm.account" placeholder="请输入工号" clearable size="large" class="fixed-width-input" />
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
        <el-table-column type="index" label="序号" width="100" />
        <el-table-column prop="roleCode" label="角色" width="150">
          <template #default="scope">
            <el-tag :type="getRoleTagType(scope.row.roleCode)" size="large">
              {{ ROLE_MAP[scope.row.roleCode]?.name || scope.row.roleCode }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="account" label="工号" width="180" />
        <el-table-column prop="userName" label="姓名" width="240" />
        <el-table-column label="照片" width="120">
          <template #default="scope">
            <el-image 
              :src="scope.row.avatar || '/src/assets/default-avatar.svg'" 
              style="width: 60px; height: 60px; border-radius: 8px;"
              fit="cover"
              :preview-src-list="[scope.row.avatar || '/src/assets/default-avatar.svg']"
            >
              <template #error>
                <div style="width: 60px; height: 60px; background: #f5f5f5; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #999;">
                  无照片
                </div>
              </template>
            </el-image>
          </template>
        </el-table-column>
        <el-table-column prop="remark" label="描述" width="240" show-overflow-tooltip>
          <template #default="scope">
            <div class="desc-cell">{{ scope.row.remark || '-' }}</div>
          </template>
        </el-table-column>
        <!-- <el-table-column prop="createdAt" label="添加时间" width="240" /> -->
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
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          :page-sizes="[10, 15, 20, 25, 30, 50, 100]"
          size="large"
          prev-text="上一页"
          next-text="下一页"
          :pager-count="7"
          :hide-on-single-page="false"
          @current-change="handlePageChange"
          @size-change="handleSizeChange"
        />
      </div>
    </el-card>
    <el-dialog v-model="dialog.visible" :title="dialog.mode === 'add' ? '新增用户' : '编辑用户'" width="700px" :close-on-click-modal="false">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          {{ dialog.mode === 'add' ? '新增用户' : '编辑用户' }}
        </div>
      </template>
      
      <el-steps v-if="dialog.mode === 'add'" :active="dialog.step" finish-status="success" align-center style="margin-bottom: 24px; margin-top: 32px;">
        <el-step title="基本信息" />
        <el-step title="拍摄照片" />
      </el-steps>
      
      <el-form :model="dialog.form" :rules="dialog.rules" ref="dialogFormRef" label-width="180px" style="margin-top: 32px;">
        <!-- 新增用户：显示步骤 -->
        <template v-if="dialog.mode === 'add'">
          <template v-if="dialog.step === 0">
            <el-form-item label="角色" prop="roleCode">
              <el-radio-group v-model="dialog.form.roleCode" style="width: 100%;">
                <el-radio v-for="r in roles" :key="r" :label="r" size="large">{{ ROLE_MAP[r].name }}</el-radio>
              </el-radio-group>
            </el-form-item>
                      <el-form-item label="工号" prop="account">
            <el-input v-model="dialog.form.account" placeholder="请输入工号" size="large" />
          </el-form-item>
          <el-form-item label="姓名" prop="userName">
            <el-input v-model="dialog.form.userName" placeholder="请输入姓名" size="large" />
          </el-form-item>
          <el-form-item label="描述" prop="remark">
            <el-input v-model="dialog.form.remark" placeholder="请输入描述" type="textarea" :rows="3" size="large" />
          </el-form-item>
          </template>
          
          <template v-else-if="dialog.step === 1">
            <div style="text-align: center; margin-bottom: 20px;">
              <div style="margin-bottom: 16px; font-size: 16px; color: #333;">
                请拍摄用户照片
              </div>
                          <div style="position: relative; display: inline-block;">
              <video 
                ref="videoRef" 
                autoplay 
                style="width: 500px; height: 375px; border-radius: 16px; border: 2px solid #409EFF;"
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
          </template>
        </template>
        
        <!-- 编辑用户：只显示基本信息 -->
        <template v-else-if="dialog.mode === 'edit'">
          <el-form-item label="角色" prop="roleCode">
            <el-radio-group v-model="dialog.form.roleCode" style="width: 100%;">
              <el-radio v-for="r in roles" :key="r" :label="r" size="large">{{ ROLE_MAP[r].name }}</el-radio>
            </el-radio-group>
          </el-form-item>
          <el-form-item label="工号" prop="account">
            <el-input v-model="dialog.form.account" placeholder="请输入工号" size="large" />
          </el-form-item>
          <el-form-item label="姓名" prop="userName">
            <el-input v-model="dialog.form.userName" placeholder="请输入姓名" size="large" />
          </el-form-item>
          <el-form-item label="描述" prop="remark">
            <el-input v-model="dialog.form.remark" placeholder="请输入描述" type="textarea" :rows="3" size="large" />
          </el-form-item>
        </template>
      </el-form>
      
      <template #footer>
        <div style="padding: 0;">
          <!-- 新增用户：显示步骤按钮 -->
          <template v-if="dialog.mode === 'add'">
            <el-button @click="dialog.visible = false" size="large">取消</el-button>
            <el-button v-if="dialog.step > 0" @click="prevStep" size="large">上一步</el-button>
            <el-button v-if="dialog.step < 1" type="primary" @click="nextStep" size="large">下一步</el-button>
            <el-button v-else type="success" @click="handleDialogOk" size="large">提交</el-button>
          </template>
          
          <!-- 编辑用户：只显示确定和取消按钮 -->
          <template v-else-if="dialog.mode === 'edit'">
            <el-button @click="dialog.visible = false" size="large">取消</el-button>
            <el-button type="primary" @click="handleDialogOk" size="large">确定</el-button>
          </template>
        </div>
      </template>
    </el-dialog>
    <el-dialog v-model="deleteDialog.visible" title="确认删除" width="400px">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          确认删除
        </div>
      </template>
      <span style="font-size: 18px; margin-top: 32px; display: block;">确定要删除该用户吗？</span>
      <template #footer>
        <div style="padding: 0;">
          <el-button @click="deleteDialog.visible = false" size="large">取消</el-button>
          <el-button type="danger" @click="handleDeleteUser" size="large">删除</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改照片对话框 -->
    <el-dialog v-model="photoDialog.visible" title="修改照片" width="600px">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          修改照片
        </div>
      </template>
      <div style="text-align: center; margin-bottom: 20px; margin-top: 32px;">
        <div style="margin-bottom: 16px; font-size: 18px; color: #333;">
          请重新拍摄用户照片
        </div>
        <div style="position: relative; display: inline-block;">
          <video 
            ref="photoVideoRef" 
            autoplay 
            style="width: 500px; height: 375px; border-radius: 16px; border: 2px solid #409EFF;"
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
        <div v-if="photoTakenForEdit" style="margin-top: 12px; color: #67C23A; font-size: 16px;">
          ✓ 照片已拍摄完成
        </div>
      </div>
      <template #footer>
        <div style="padding: 0;">
          <el-button @click="photoDialog.visible = false" size="large">取消</el-button>
          <el-button type="primary" @click="handleUpdatePhoto" :disabled="!photoTakenForEdit" size="large">
            确定
          </el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialog.visible" title="修改密码" width="500px">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          修改密码
        </div>
      </template>
      <el-form :model="passwordDialog.form" :rules="passwordDialog.rules" ref="passwordFormRef" label-width="180px" style="margin-top: 32px;">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordDialog.form.newPassword" type="password" placeholder="请输入新密码" show-password size="large" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordDialog.form.confirmPassword" type="password" placeholder="请再次输入新密码" show-password size="large" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="padding: 0;">
          <el-button @click="passwordDialog.visible = false" size="large">取消</el-button>
          <el-button type="primary" @click="handleUpdatePassword" size="large">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser } from '../request/api'
import { pageSizeCalculators } from '../utils/pagination'
import { ROLE_MAP } from '../utils/roleMap'
const roles = Object.keys(ROLE_MAP)

// 获取角色标签类型
function getRoleTagType(role) {
  switch (role) {
    case '物料员':
      return 'success'
    case '高级操作员':
      return 'warning'
    case '操作员':
      return 'info'
    default:
      return 'info'
  }
}

const searchForm = reactive({
  userName: '',
  account: ''
})

const users = ref([])
const loading = ref(false)
const total = ref(0)

// 动态计算每页显示条数
const pageSize = ref(5)
const currentPage = ref(1)

// 计算合适的每页显示条数
function calculatePageSize() {
  pageSize.value = pageSizeCalculators.userManage()
}

// 获取用户列表
async function fetchUsers() {
  loading.value = true
  try {
    const params = {
      page: currentPage.value,
      pageSize: pageSize.value,
      userName: searchForm.userName,
      account: searchForm.account
    }
    const response = await getUserList(params)
    users.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('获取用户列表失败')
  } finally {
    loading.value = false
  }
}

// 直接使用后端返回的数据，不再进行客户端分页
const pagedUsers = computed(() => {
  return users.value
})

async function handleSearch() {
  currentPage.value = 1
  await fetchUsers()
}
async function resetSearch() {
  searchForm.userName = ''
  searchForm.account = ''
  currentPage.value = 1
  await fetchUsers()
}
async function handlePageChange(page) {
  currentPage.value = page
  await fetchUsers()
}

async function handleSizeChange(size) {
  pageSize.value = size
  currentPage.value = 1
  await fetchUsers()
}

const dialog = reactive({
  visible: false,
  mode: 'add', // add/edit
  step: 1, // 1: 基本信息, 2: 拍照
  index: null,
  form: { roleCode: '', account: '', userName: '', remark: '', avatar: '' },
  rules: {
    roleCode: [ { required: true, message: '请选择角色', trigger: 'change' } ],
    account: [ { required: true, message: '请输入工号', trigger: 'blur' } ],
    userName: [ { required: true, message: '请输入姓名', trigger: 'blur' } ],
    remark: [ { required: false, message: '请输入描述', trigger: 'blur' } ]
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
  form: { newPassword: '', confirmPassword: '' },
  rules: {
    newPassword: [ { required: true, message: '请输入新密码', trigger: 'blur' } ],
    confirmPassword: [ { required: true, message: '请再次输入新密码', trigger: 'blur' } ]
  }
})
const passwordFormRef = ref()

function openDialog(mode, index = null) {
  dialog.mode = mode
  dialog.step = 0
  dialog.visible = true
  dialog.index = index
  photoTaken.value = false
  stopCamera() // 停止之前的摄像头
  if (mode === 'edit' && index !== null) {
    // 先重置form，避免残留
    dialog.form = { roleCode: '', account: '', userName: '', remark: '', avatar: '' }
    Object.assign(dialog.form, users.value[index])
  } else {
    dialog.form = { roleCode: '', account: '', userName: '', remark: '', avatar: '' }
  }
}

async function handleDialogOk() {
  try {
    if (dialog.mode === 'add') {
      // 新增模式：提交步骤
      if (!photoTaken.value) {
        ElMessage.warning('请先拍摄用户照片')
        return
      }
      await createUser(dialog.form)
      ElMessage.success('新增成功')
      dialog.visible = false
      stopCamera() // 停止摄像头
      await fetchUsers() // 新增后立即刷新列表
    } else if (dialog.mode === 'edit' && dialog.index !== null) {
      dialogFormRef.value.validate(async valid => {
        if (!valid) return
        await updateUser(dialog.form.userId, dialog.form)
        ElMessage.success('编辑成功')
        dialog.visible = false
        await fetchUsers() // 编辑后立即刷新列表
      })
      return
    }
  } catch (error) {
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
      await deleteUser(user.userId)
      ElMessage.success('删除成功')
      deleteDialog.visible = false
      deleteDialog.index = null
      await fetchUsers() // 重新获取列表
      } catch (error) {
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
  dialog.form.avatar = photoData
  photoTaken.value = true
  
  ElMessage.success('拍照成功')
}

function nextStep() {
  if (dialog.step === 0) {
    dialogFormRef.value.validate(async valid => {
      if (!valid) return
      dialog.step = 1
      // 延迟启动摄像头，确保DOM已更新
      setTimeout(() => {
        startCamera()
      }, 100)
    })
  }
}

function prevStep() {
  if (dialog.step > 0) {
    dialog.step--
    if (dialog.step === 0) {
      stopCamera()
      photoTaken.value = false
    }
  }
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
      
      await updateUser(user.userId, { ...user, avatar: photoData })
      ElMessage.success('照片更新成功')
      photoDialog.visible = false
      stopPhotoCamera()
      await fetchUsers() // 重新获取列表
    } catch (error) {
      ElMessage.error('更新照片失败')
    }
  }
}

// 修改密码相关函数
function openPasswordDialog(index) {
  passwordDialog.visible = true
  passwordDialog.index = index
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
      // await updateUserPassword(user.id, { newPassword: passwordDialog.form.newPassword })
      ElMessage.success('密码修改成功')
      passwordDialog.visible = false
    } catch (error) {
      ElMessage.error('修改密码失败')
    }
  })
}

// 页面加载时获取数据
onMounted(() => {
  calculatePageSize()
  fetchUsers()
  
  // 监听窗口大小变化
  window.addEventListener('resize', calculatePageSize)
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

// 组件卸载时清理事件监听
onUnmounted(() => {
  window.removeEventListener('resize', calculatePageSize)
})

</script>

<style scoped>
.user-manage-container {
  margin: 20px;
  height: calc(100vh - 40px);
  display: flex;
  flex-direction: column;
}
.user-card {
  background: rgba(255,255,255,0.8);
  border-radius: 32px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.10);
  padding: 48px 40px 32px 40px;
  font-size: 1.2em;
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

/* 查询表单标签样式 */
:deep(.search-form .el-form-item__label) {
  font-size: 20px !important;
  color: #333 !important;
  font-weight: 500 !important;
}
.add-btn {
  margin-left: 24px;
}
.user-table {
  margin-bottom: 24px;
  font-size: 20px;
  flex: 1;
  min-height: 0;
  max-height: calc(100vh - 300px);
  overflow: auto;
}

/* 增加表格行高，确保文字完整显示 */
:deep(.user-table .el-table__row) {
  height: 60px !important;
  min-height: 60px !important;
}

:deep(.user-table .el-table__cell) {
  padding: 12px 0 !important;
  line-height: 1.5 !important;
  height: 60px !important;
  min-height: 60px !important;
}

/* 确保表格内容垂直居中且完整显示 */
:deep(.user-table .el-table) {
  --el-table-row-height: 60px !important;
}

:deep(.user-table .el-table__cell) {
  vertical-align: middle !important;
}

/* 强制设置表格行高和对齐 */
:deep(.user-table .el-table) {
  --el-table-row-height: 60px !important;
}

:deep(.user-table .el-table__row) {
  height: 60px !important;
  min-height: 60px !important;
}

:deep(.user-table .el-table__cell) {
  height: 60px !important;
  min-height: 60px !important;
  padding: 0 12px !important;
  vertical-align: middle !important;
  display: table-cell !important;
}

:deep(.user-table .el-table__header .el-table__cell) {
  height: 60px !important;
  min-height: 60px !important;
  vertical-align: middle !important;
}

/* 确保单元格内容垂直居中 */
:deep(.user-table .el-table__cell .cell) {
  height: 60px !important;
  min-height: 60px !important;
  line-height: 60px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: flex-start !important;
}

/* 调整照片列 */
:deep(.user-table .el-table__cell .el-image) {
  width: 60px !important;
  height: 60px !important;
  display: flex !important;
  align-items: center !important;
  justify-content: center !important;
}

:deep(.user-table .el-table__cell .el-image img) {
  width: 60px !important;
  height: 60px !important;
  border-radius: 8px !important;
}

/* 调整照片错误显示 */
:deep(.user-table .el-table__cell .el-image .el-image__error) {
  width: 60px !important;
  height: 60px !important;
}

/* 调整标签和按钮 */
:deep(.user-table .el-table__cell .el-tag) {
  display: inline-flex !important;
  align-items: center !important;
  justify-content: center !important;
}

:deep(.user-table .el-table__cell .el-button) {
  margin: 2px !important;
}
.pagination-box {
  display: flex;
  justify-content: flex-end;
  margin-top: 12px;
  flex-shrink: 0;
  padding: 10px 0;
}
/* 使用更高优先级的选择器确保表单标签字体生效 */
:deep(.el-form-item__label) {
  font-size: 20px !important;
}

:deep(.search-form .el-form-item__label),
:deep(.el-dialog .el-form-item__label) {
  font-size: 20px !important;
}

/* 确保所有表单标签字体生效 */
:deep(.el-form-item) .el-form-item__label {
  font-size: 20px !important;
}

/* 固定输入框宽度，避免清除按钮导致宽度变化 */
.fixed-width-input {
  width: 180px !important;
}

.fixed-width-input .el-input__wrapper {
  width: 100% !important;
}

/* 弹窗表单输入框宽度 */
:deep(.el-dialog .el-input) {
  width: 85% !important;
}

:deep(.el-dialog .el-input-number) {
  width: 85% !important;
}

/* 弹窗文本域宽度 */
:deep(.el-dialog .el-textarea) {
  width: 85% !important;
}

/* 描述列样式 */
.desc-cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  max-width: 180px;
}

/* 角色单选按钮组样式 */
:deep(.el-radio-group) {
  display: flex;
  flex-direction: row;
  gap: 24px;
  flex-wrap: wrap;
}

:deep(.el-radio) {
  margin-right: 0;
  margin-bottom: 0;
}

/* 分页组件中文样式 */
:deep(.el-pagination .el-pagination__total) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__jump) {
  font-size: 16px;
}

/* 隐藏分页组件的英文文本 */
:deep(.el-pagination .el-pagination__total) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__jump) {
  font-size: 16px;
}

/* 隐藏英文文本，只显示中文 */
:deep(.el-pagination .el-pagination__total) {
  font-size: 0;
}

:deep(.el-pagination .el-pagination__total::before) {
  content: "共 ";
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__total::after) {
  content: " 条";
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__total span) {
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner) {
  font-size: 0;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner::after) {
  content: " 条/页";
  font-size: 16px;
  color: #606266;
}

:deep(.el-pagination .el-pagination__sizes .el-select .el-input__inner input) {
  font-size: 16px;
  padding-right: 50px;
}

/* 隐藏下拉选项中的英文文本 */
:deep(.el-pagination .el-pagination__sizes .el-select-dropdown .el-select-dropdown__item) {
  font-size: 0;
}

:deep(.el-pagination .el-pagination__sizes .el-select-dropdown .el-select-dropdown__item::after) {
  content: " 条/页";
  font-size: 16px;
  color: #606266;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto) {
  font-size: 0;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto::before) {
  content: "前往第 ";
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto::after) {
  content: " 页";
  font-size: 16px;
}

:deep(.el-pagination .el-pagination__jump .el-pagination__goto input) {
  font-size: 16px;
}
</style> 
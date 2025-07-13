<template>
  <div class="user-manage-container">
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
      @link-click="handleLinkClick"
    />

    <!-- 用户管理对话框 -->
    <Dialog
      :visible="dialog.visible"
      :title="dialog.mode === 'add' ? '新增用户' : '编辑用户'"
      :width="dialogConfig.width"
      @update:visible="val => dialog.visible = val"
    >
      <Form
        :fields="dialogConfig.fields"
        :rules="dialogConfig.rules"
        :form-data="dialog.form"
        :loading="dialog.loading"
        @submit="handleDialogSubmit"
        @cancel="closeDialog"
      />
    </Dialog>

    <!-- 删除确认对话框 -->
    <ConfirmDialog
      :visible="deleteDialog.visible"
      title="确认删除"
      message="确定要删除该用户吗？"
      confirm-type="danger"
      confirm-text="删除"
      @update:visible="val => deleteDialog.visible = val"
      @confirm="handleDeleteUser"
      @cancel="closeDeleteDialog"
    />

    <!-- 人脸录入对话框 -->
    <Dialog
      :visible="photoDialog.visible"
      title="人脸录入"
      width="900px"
      @update:visible="val => {
        photoDialog.visible = val
        if (!val) closePhotoDialog()
      }"
    >
      <div style="margin-top: 24px;">
        <!-- 摄像头/照片区域 -->
        <div style="text-align: center; margin-bottom: 16px;">
          <!-- 摄像头区域 -->
          <video 
            v-if="!photoDialog.currentPhoto"
            ref="videoRef" 
            style="width: 600px; height: 450px; border-radius: 8px; background: #000;"
            autoplay 
            muted
          ></video>
          
          <!-- 已拍摄照片区域 -->
          <div v-if="photoDialog.currentPhoto" style="text-align: center;">
            <el-image 
              :src="`data:image/png;base64,${photoDialog.currentPhoto}`" 
              style="width: 600px; height: 450px; border-radius: 8px;"
              fit="cover"
            />
          </div>
        </div>
      </div>
      
      <!-- 底部按钮区域 -->
      <template #footer>
        <div style="text-align: center;">
          <!-- 摄像头未就绪 -->
          <template v-if="photoDialog.cameraLoading">
            <el-button @click="closePhotoDialog" size="large">取消</el-button>
            <el-button type="primary" size="large" :loading="true">准备中...</el-button>
          </template>
          
          <!-- 摄像头已就绪 -->
          <template v-else-if="photoDialog.isCapturing && !photoDialog.currentPhoto">
            <el-button @click="closePhotoDialog" size="large">取消</el-button>
            <el-button type="success" size="large" @click="capturePhoto">拍照</el-button>
          </template>
          
          <!-- 已拍照 -->
          <template v-else-if="photoDialog.currentPhoto">
            <el-button @click="closePhotoDialog" size="large">取消</el-button>
            <el-button type="warning" size="large" @click="retakePhoto">重拍</el-button>
            <el-button 
              type="success" 
              size="large" 
              @click="submitPhoto"
              :loading="photoDialog.submitLoading"
            >
              提交
            </el-button>
          </template>
          
          <!-- 摄像头获取失败 -->
          <template v-else>
            <el-button @click="closePhotoDialog" size="large">取消</el-button>
            <el-button type="primary" size="large" @click="getCamera">重新获取摄像头</el-button>
          </template>
        </div>
      </template>
    </Dialog>

    <!-- 修改密码对话框 -->
    <Dialog
      :visible="passwordDialog.visible"
      :title="passwordDialogConfig.title"
      :width="passwordDialogConfig.width"
      @update:visible="val => passwordDialog.visible = val"
    >
      <Form
        :fields="passwordDialogConfig.fields"
        :rules="passwordDialogConfig.rules"
        :form-data="passwordDialog.form"
        :loading="passwordDialog.loading"
        @submit="handlePasswordSubmit"
        @cancel="closePasswordDialog"
      />
    </Dialog>

    <!-- 照片预览对话框 -->
    <Dialog
      :visible="photoPreviewDialog.visible"
      title="照片预览"
      width="700px"
      @update:visible="val => photoPreviewDialog.visible = val"
    >
      <div style="text-align: center; margin-top: 20px;">
        <el-image 
          :src="photoPreviewDialog.imageSrc" 
          style="max-width: 600px; max-height: 450px; border-radius: 8px;"
          fit="contain"
        />
      </div>
      <template #footer>
        <div style="text-align: center;">
          <el-button @click="closePhotoPreview" size="large">关闭</el-button>
        </div>
      </template>
    </Dialog>

  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, updateUserPassword, updateUserPhoto } from '@/request/api'
import { ROLE_MAP } from '@/utils/roleMap'
import { encryptPassword } from '@/utils/http'
import { getCameraErrorMessage, startGlobalCamera } from '@/utils/camera'
import { base64ToFile, canvasToBase64 } from '@/utils/fileUtils'
import DataTable from '@/components/DataTable'
import { Dialog, ConfirmDialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { searchFields, columns, actionButtons, headerButtons } from './config'
import { getUserFormConfig, getPasswordFormConfig } from './formConfig'

const roles = Object.keys(ROLE_MAP)

// 数据
const records = ref([])
const loading = ref(false)
const total = ref(0)

// 对话框配置
const dialogConfig = getUserFormConfig()
const passwordDialogConfig = getPasswordFormConfig()

// 对话框
const dialog = reactive({
  visible: false,
  mode: 'add', // add/edit
  index: null,
  loading: false,
  form: { roleCode: '', account: '', userName: '', remark: '' }
})

const deleteDialog = reactive({
  visible: false,
  index: null
})

const photoDialog = reactive({
  visible: false,
  index: null,
  currentPhoto: null,
  isCapturing: false,
  cameraLoading: false,
  submitLoading: false
})

const passwordDialog = reactive({
  visible: false,
  index: null,
  loading: false,
  form: { newPassword: '', confirmPassword: '' }
})

const photoPreviewDialog = reactive({
  visible: false,
  imageSrc: ''
})



const videoRef = ref()

// 事件处理函数
async function handleSearch(params) {
  loading.value = true
  try {
    const response = await getUserList(params)
    records.value = response.data || []
    total.value = response.total || 0
  } catch (error) {
    ElMessage.error('获取用户列表失败')
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
  switch (action) {
    case 'edit':
      openDialog('edit', index)
      break
    case 'delete':
      confirmDelete(index)
      break
    case 'photo':
      openPhotoDialog(index)
      break
    case 'password':
      openPasswordDialog(index)
      break
  }
}

function handleHeaderAction({ action }) {
  switch (action) {
    case 'add':
      openDialog('add')
      break
    case 'batch-import':
      ElMessage.success('批量导入功能待实现')
      break
    case 'export':
      ElMessage.success('导出功能待实现')
      break
  }
}

// 处理链接点击事件（照片查看）
function handleLinkClick({ row, index }) {
  if (row.facePath) {
    // 显示照片预览 - facePath字段存储的是base64格式的图片数据
    photoPreviewDialog.imageSrc = `data:image/png;base64,${row.facePath}`
    photoPreviewDialog.visible = true
  } else {
    ElMessage.warning('该用户暂无照片')
  }
}

// 对话框相关函数
function openDialog(mode, index = null) {
  dialog.mode = mode
  dialog.visible = true
  dialog.index = index
  if (mode === 'edit' && index !== null) {
    Object.assign(dialog.form, records.value[index])
  } else {
    dialog.form = { roleCode: '', account: '', userName: '', remark: '' }
  }
}

async function handleDialogSubmit(formData) {
  if (dialog.loading) return // 防止重复提交
  
  dialog.loading = true
  try {
    if (dialog.mode === 'add') {
      await createUser(formData)
      ElMessage.success('新增用户成功')
    } else {
      const user = records.value[dialog.index]
      const userId = user.userId || user.id
      await updateUser(userId, formData)
      ElMessage.success('编辑用户成功')
    }
    dialog.visible = false
    await handleSearch({ page: 1, pageSize: 10 })
  } catch (error) {
    ElMessage.error(dialog.mode === 'add' ? '新增用户失败' : '编辑用户失败')
  } finally {
    dialog.loading = false
  }
}

function closeDialog() {
  dialog.visible = false
}

function confirmDelete(index) {
  deleteDialog.index = index
  deleteDialog.visible = true
}

async function handleDeleteUser() {
  const index = deleteDialog.index
  try {
    const user = records.value[index]
    const userId = user.userId || user.id
    await deleteUser(userId)
    ElMessage.success('删除用户成功')
    deleteDialog.visible = false
    await handleSearch({ page: 1, pageSize: 10 })
  } catch (error) {
    ElMessage.error('删除用户失败')
  }
}

function closeDeleteDialog() {
  deleteDialog.visible = false
}

// 人脸录入相关
let cameraStream = null

function openPhotoDialog(index) {
  console.log('打开人脸录入弹窗 - 索引:', index, '用户数据:', records.value[index])
  
  photoDialog.index = index
  photoDialog.visible = true
  photoDialog.currentPhoto = null
  photoDialog.isCapturing = false
  photoDialog.cameraLoading = false
  
  // 弹窗打开后自动获取全局摄像头
  nextTick(() => {
    getCamera()
  })
}

async function getCamera() {
  if (photoDialog.cameraLoading) return
  
  photoDialog.cameraLoading = true
  try {
    // 直接获取全局摄像头
    cameraStream = await startGlobalCamera()
    
    // 将摄像头流绑定到video元素
    if (videoRef.value && cameraStream) {
      videoRef.value.srcObject = cameraStream
      photoDialog.isCapturing = true
    }
  } catch (error) {
    const errorMsg = getCameraErrorMessage(error)
    ElMessage.error(errorMsg)
    photoDialog.isCapturing = false
  } finally {
    photoDialog.cameraLoading = false
  }
}

function capturePhoto() {
  if (!videoRef.value || !photoDialog.isCapturing) {
    ElMessage.warning('请先启动摄像头')
    return
  }
  
  try {
    // 创建canvas来捕获视频帧
    const canvas = document.createElement('canvas')
    const context = canvas.getContext('2d')
    
    // 设置canvas尺寸为视频尺寸
    canvas.width = videoRef.value.videoWidth
    canvas.height = videoRef.value.videoHeight
    
    // 将视频帧绘制到canvas
    context.drawImage(videoRef.value, 0, 0, canvas.width, canvas.height)
    
    // 使用工具类将canvas转换为base64图片数据
    const photoData = canvasToBase64(canvas, 'image/png', 0.8)
    photoDialog.currentPhoto = photoData
    
    ElMessage.success('拍照成功')
  } catch (error) {
    console.error('拍照失败:', error)
    ElMessage.error('拍照失败，请重试')
  }
}

async function retakePhoto() {
  photoDialog.currentPhoto = null
  photoDialog.isCapturing = false
  
  // 重新获取摄像头
  await getCamera()
}

async function submitPhoto() {
  photoDialog.submitLoading = true
  try {
    // 检查用户ID
    if (photoDialog.index === null || photoDialog.index === undefined) {
      throw new Error('用户索引为空')
    }
    
    const user = records.value[photoDialog.index]
    if (!user) {
      throw new Error('用户数据不存在')
    }
    
    const userId = user.userId || user.id
    if (!userId) {
      throw new Error('用户ID为空')
    }
    
    console.log('提交照片 - 用户ID:', userId, '用户数据:', user)
    
    // 使用工具类将base64字符串转换为File对象
    const imageFile = base64ToFile(photoDialog.currentPhoto, 'face_photo.png', 'image/png')
    
    // 提交照片逻辑
    await updateUserPhoto(userId, imageFile)
    ElMessage.success('照片提交成功')
    photoDialog.visible = false
    await handleSearch({ page: 1, pageSize: 10 })
  } catch (error) {
    console.error('照片提交失败:', error)
    ElMessage.error(`照片提交失败: ${error.message}`)
  } finally {
    photoDialog.submitLoading = false
  }
}

// 关闭人脸录入弹窗
function closePhotoDialog() {
  photoDialog.visible = false
  photoDialog.currentPhoto = null
  photoDialog.isCapturing = false
  photoDialog.cameraLoading = false
}

// 修改密码相关
function openPasswordDialog(index) {
  passwordDialog.index = index
  passwordDialog.visible = true
  passwordDialog.form = { newPassword: '', confirmPassword: '' }
}

async function handlePasswordSubmit(formData) {
  if (passwordDialog.loading) return // 防止重复提交
  
  if (formData.newPassword !== formData.confirmPassword) {
    ElMessage.error('两次输入的密码不一致')
    return
  }
  
  passwordDialog.loading = true
  try {
    const encryptedPassword = encryptPassword(formData.newPassword)
    const user = records.value[passwordDialog.index]
    const userId = user.userId || user.id
    const requestData = {
      userId: userId,
      password: encryptedPassword
    }
    await updateUserPassword(requestData)
    ElMessage.success('密码修改成功')
    passwordDialog.visible = false
  } catch (error) {
    ElMessage.error('密码修改失败')
  } finally {
    passwordDialog.loading = false
  }
}

function closePasswordDialog() {
  passwordDialog.visible = false
}

function closePhotoPreview() {
  photoPreviewDialog.visible = false
}

// 页面加载时获取数据
onMounted(() => {
  handleSearch({ page: 1, pageSize: 10 })
})


</script>

<style scoped>
.user-manage-container {
  height: 100vh;
}
</style> 
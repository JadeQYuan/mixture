<template>
  <div class="user-manage-container">
    <DataTable
      ref="table"
      :columns="columns"
      :search-fields="searchFields"
      :action-buttons="actionButtons"
      :header-buttons="headerButtons"
      :request="getUserList"
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
      width="800px"
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
      width="800px"
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
import { ref, reactive, onMounted, onUnmounted, computed, nextTick } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, updateUserPassword, updateUserPhoto } from '@/api/user'
import { encryptPassword } from '@/utils/crypto'
import { getCameraErrorMessage, startGlobalCamera } from '@/utils/camera'
import { base64ToFile, canvasToBase64 } from '@/utils/fileUtils'
import DataTable from '@/components/DataTable'
import { Dialog, ConfirmDialog } from '@/components/Dialog'
import Form from '@/components/Form'
import { searchFields, columns } from './config'
import { getUserFormConfig, getPasswordFormConfig } from './config'
import { useStore } from 'vuex'

const table = ref()

const store = useStore()
const userId = computed(() => store.state.userInfo?.id || '')
const actionButtons = [
  {
    key: 'edit',
    text: '编辑',
    type: 'primary',
    size: 'large',
    action: ( row ) => openDialog('edit', row)
  },
  {
    key: 'photo',
    text: '人脸录入',
    type: 'warning',
    size: 'large',
    action: ( row ) => openPhotoDialog(row)
  },
  {
    key: 'photoUpload',
    text: '照片上传',
    type: 'warning',
    size: 'large',
    action: ( row ) => openPhotoDialog(row)
  },
  {
    key: 'password',
    text: '修改密码',
    type: 'info',
    size: 'large',
    action: ( row ) => openPasswordDialog(row)
  },
  {
    key: 'delete',
    text: '删除',
    type: 'danger',
    size: 'large',
    action: ( row ) => confirmDelete(row),
    disabled: (row) => row.id === userId.value
  }
]

const headerButtons = [
  {
    key: 'add',
    text: '新增用户',
    type: 'primary',
    size: 'large',
    action: () => openDialog('add')
  }
]

// 对话框配置
const dialogConfig = getUserFormConfig()
const passwordDialogConfig = getPasswordFormConfig()

// 对话框
const dialog = reactive({
  visible: false,
  mode: 'add', // add/edit
  loading: false,
  form: { id: null, roleCode: '', account: '', userName: '', remark: '' }
})

// 对话框相关函数
function openDialog(mode, row) {
  dialog.mode = mode
  dialog.visible = true
  if (mode === 'edit' && row !== null) {
    Object.assign(dialog.form, row)
  } else {
    dialog.form = { roleCode: '', account: '', userName: '', remark: '' }
  }
}

async function handleDialogSubmit(formData) {
  if (dialog.loading) return // 防止重复提交
  
  try {
    dialog.loading = true
    if (dialog.mode === 'add') {
      const res = await createUser(formData)
      ElMessage.success('新增用户成功')
      dialog.visible = false
      // 新增：刷新后自动弹出人脸录入弹窗（用id查找）
      await table.value.search()
      // const newId = res && res.id
      // if (newId) {
      //   const idx = records.value.findIndex(u => u.id === newId)
      //   if (idx !== -1) {
      //     openPhotoDialog(idx)
      //   }
      // }
      return
    } else {
      await updateUser(formData)
      ElMessage.success('编辑用户成功')
    }
    dialog.visible = false
    await table.value.search()
  } finally {
    dialog.loading = false
  }
}

function closeDialog() {
  dialog.visible = false
}

// 删除
const deleteDialog = reactive({
  visible: false,
  id: null
})

function confirmDelete(row) {
  deleteDialog.id = row.id
  deleteDialog.visible = true
}

async function handleDeleteUser() {
  try {
    await deleteUser(deleteDialog.id)
    ElMessage.success('删除用户成功')
  } finally {
    deleteDialog.visible = false
    await table.value.search()
  }
}

function closeDeleteDialog() {
  deleteDialog.visible = false
}

// 人脸录入相关
const photoDialog = reactive({
  visible: false,
  id: null,
  currentPhoto: null,
  isCapturing: false,
  cameraLoading: false,
  submitLoading: false
})

const videoRef = ref()

let cameraStream = null

function openPhotoDialog(row) {
  photoDialog.id = row.id
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
  
  try {
    photoDialog.cameraLoading = true
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
    const id = photoDialog.id
    if (!id) {
      throw new Error('用户ID为空')
    }
    
    // 使用工具类将base64字符串转换为File对象
    const imageFile = base64ToFile(photoDialog.currentPhoto, 'face_photo.png', 'image/png')
    
    // 提交照片逻辑
    await updateUserPhoto(id, imageFile)
    ElMessage.success('照片提交成功')
    photoDialog.visible = false
    await table.value.search()
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
const passwordDialog = reactive({
  visible: false,
  id: null,
  loading: false,
  form: { newPassword: '', confirmPassword: '' }
})

function openPasswordDialog(row) {
  passwordDialog.id = row.id
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
    const requestData = {
      id: passwordDialog.id,
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

// 处理链接点击事件（照片查看）
const photoPreviewDialog = reactive({
  visible: false,
  imageSrc: ''
})

function handleLinkClick({ row, index }) {
  if (row.facePath) {
    // 显示照片预览 - facePath字段存储的是base64格式的图片数据
    photoPreviewDialog.imageSrc = `data:image/png;base64,${row.facePath}`
    photoPreviewDialog.visible = true
  } else {
    ElMessage.warning('该用户暂无照片')
  }
}

function closePhotoPreview() {
  photoPreviewDialog.visible = false
}
</script>

<style scoped>
.user-manage-container {
}
</style> 
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
    <el-dialog v-model="dialog.visible" :title="dialog.mode === 'add' ? '新增用户' : '编辑用户'" width="700px" :close-on-click-modal="false">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          {{ dialog.mode === 'add' ? '新增用户' : '编辑用户' }}
        </div>
      </template>
      
      <el-form :model="dialog.form" :rules="dialog.rules" ref="dialogFormRef" label-width="180px" style="margin-top: 32px;">
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
      </el-form>
      
      <template #footer>
        <div style="padding: 0;">
          <el-button @click="dialog.visible = false" size="large">取消</el-button>
          <el-button type="primary" @click="handleDialogOk" size="large" :loading="dialog.loading">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
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

    <!-- 头像预览对话框 -->
    <el-dialog v-model="avatarPreviewDialog.visible" title="头像预览" width="700px" :close-on-click-modal="true">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          头像预览
        </div>
      </template>
      <div style="text-align: center; margin-top: 20px;">
        <el-image 
          :src="avatarPreviewDialog.imageSrc" 
          style="max-width: 600px; max-height: 450px; border-radius: 12px;"
          fit="contain"
        >
          <template #error>
            <div style="width: 600px; height: 450px; background: #f5f5f5; border-radius: 12px; display: flex; align-items: center; justify-content: center; color: #999; font-size: 18px;">
              无照片
            </div>
          </template>
        </el-image>
      </div>
      <template #footer>
        <div style="padding: 0; text-align: center;">
          <el-button @click="avatarPreviewDialog.visible = false" size="large">关闭</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 人脸录入对话框 -->
    <el-dialog v-model="photoDialog.visible" title="人脸录入" width="800px">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          人脸录入
        </div>
      </template>
      <div style="margin-top: 32px;">
        <!-- 照片展示区域 -->
        <div v-if="photoDialog.photos.length > 0" style="margin-bottom: 30px;">
          <div style="margin-bottom: 16px; font-size: 16px; color: #333; text-align: center;">
            已拍摄的照片
          </div>
          <div style="display: flex; flex-wrap: wrap; gap: 16px; justify-content: center;">
            <div 
              v-for="(photo, index) in photoDialog.photos" 
              :key="index"
              style="position: relative; display: inline-block;"
            >
              <el-image 
                :src="`data:image/png;base64,${photo}`" 
                style="width: 120px; height: 120px; border-radius: 8px; cursor: pointer;"
                fit="cover"
                @click="previewPhoto(photo)"
              />
              <el-button 
                type="danger" 
                size="small" 
                circle 
                style="position: absolute; top: -8px; right: -8px;"
                @click="removePhoto(index)"
              >
                ×
              </el-button>
            </div>
          </div>
        </div>
        
        <!-- 摄像头区域 -->
        <div style="text-align: center; margin-bottom: 20px;">
          <video 
            ref="videoRef" 
            style="width: 400px; height: 300px; border-radius: 8px; background: #000;"
            autoplay 
            muted
          ></video>
        </div>
        
        <!-- 控制按钮 -->
        <div style="text-align: center; margin-bottom: 20px;">
          <el-button 
            v-if="!photoDialog.isCapturing" 
            type="primary" 
            size="large" 
            @click="startCamera"
            :loading="photoDialog.cameraLoading"
          >
            启动摄像头
          </el-button>
          <el-button 
            v-if="photoDialog.isCapturing" 
            type="success" 
            size="large" 
            @click="capturePhoto"
          >
            拍照
          </el-button>
          <el-button 
            v-if="photoDialog.isCapturing" 
            type="warning" 
            size="large" 
            @click="stopCamera"
          >
            停止摄像头
          </el-button>
        </div>
        
        <!-- 提交按钮 -->
        <div style="text-align: center;">
          <el-button 
            type="success" 
            size="large" 
            @click="submitPhotos"
            :disabled="photoDialog.photos.length === 0"
            :loading="photoDialog.submitLoading"
          >
            提交照片
          </el-button>
        </div>
      </div>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog v-model="passwordDialog.visible" title="修改密码" width="500px">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          修改密码
        </div>
      </template>
      <el-form :model="passwordDialog.form" :rules="passwordDialog.rules" ref="passwordFormRef" label-width="120px" style="margin-top: 32px;">
        <el-form-item label="新密码" prop="newPassword">
          <el-input v-model="passwordDialog.form.newPassword" type="password" placeholder="请输入新密码" size="large" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="passwordDialog.form.confirmPassword" type="password" placeholder="请再次输入新密码" size="large" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div style="padding: 0;">
          <el-button @click="passwordDialog.visible = false" size="large">取消</el-button>
          <el-button type="primary" @click="handlePasswordChange" size="large" :loading="passwordDialog.loading">确定</el-button>
        </div>
      </template>
    </el-dialog>

    <!-- 照片预览对话框 -->
    <el-dialog v-model="photoPreviewDialog.visible" title="照片预览" width="600px">
      <template #header>
        <div style="text-align: center; font-size: 24px; font-weight: 900; color: #000;">
          照片预览
        </div>
      </template>
      <div style="text-align: center; margin-top: 20px;">
        <el-image 
          :src="photoPreviewDialog.imageSrc" 
          style="max-width: 500px; max-height: 400px; border-radius: 8px;"
          fit="contain"
        />
      </div>
      <template #footer>
        <div style="padding: 0; text-align: center;">
          <el-button @click="photoPreviewDialog.visible = false" size="large">关闭</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, updateUserPassword, updateUserPhoto } from '@/request/api'
import { ROLE_MAP } from '@/utils/roleMap'
import { encryptPassword } from '@/utils/http'
import { getCameraErrorMessage, startGlobalCamera } from '@/utils/camera'
import DataTable from '@/components/DataTable'
import { searchFields, columns, actionButtons, headerButtons } from './config'

const roles = Object.keys(ROLE_MAP)

// 数据
const records = ref([])
const loading = ref(false)
const total = ref(0)

// 对话框
const dialog = reactive({
  visible: false,
  mode: 'add', // add/edit
  index: null,
  loading: false,
  form: { roleCode: '', account: '', userName: '', remark: '' },
  rules: {
    roleCode: [ { required: true, message: '请选择角色', trigger: 'change' } ],
    account: [ { required: true, message: '请输入工号', trigger: 'blur' } ],
    userName: [ { required: true, message: '请输入姓名', trigger: 'blur' } ]
  }
})

const deleteDialog = reactive({
  visible: false,
  index: null
})

const avatarPreviewDialog = reactive({
  visible: false,
  imageSrc: ''
})

const photoDialog = reactive({
  visible: false,
  index: null,
  photos: [],
  isCapturing: false,
  cameraLoading: false,
  submitLoading: false
})

const passwordDialog = reactive({
  visible: false,
  index: null,
  loading: false,
  form: { newPassword: '', confirmPassword: '' },
  rules: {
    newPassword: [ { required: true, message: '请输入新密码', trigger: 'blur' } ],
    confirmPassword: [ { required: true, message: '请再次输入新密码', trigger: 'blur' } ]
  }
})

const photoPreviewDialog = reactive({
  visible: false,
  imageSrc: ''
})

const dialogFormRef = ref()
const passwordFormRef = ref()
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

async function handleDialogOk() {
  dialogFormRef.value.validate(async (valid) => {
    if (valid) {
      dialog.loading = true
      try {
        if (dialog.mode === 'add') {
          await createUser(dialog.form)
          ElMessage.success('新增用户成功')
        } else {
          await updateUser(dialog.form)
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
  })
}

function confirmDelete(index) {
  deleteDialog.index = index
  deleteDialog.visible = true
}

async function handleDeleteUser() {
  const index = deleteDialog.index
  try {
    await deleteUser(records.value[index].id)
    ElMessage.success('删除用户成功')
    deleteDialog.visible = false
    await handleSearch({ page: 1, pageSize: 10 })
  } catch (error) {
    ElMessage.error('删除用户失败')
  }
}

// 头像预览相关
function openAvatarPreview(row) {
  if (row.facePath) {
    avatarPreviewDialog.imageSrc = `data:image/png;base64,${row.facePath}`
  } else {
    avatarPreviewDialog.imageSrc = '/src/assets/avatar.svg'
  }
  avatarPreviewDialog.visible = true
}

// 处理链接点击事件
function handleLinkClick({ row }) {
  openAvatarPreview(row)
}

// 人脸录入相关
function openPhotoDialog(index) {
  photoDialog.index = index
  photoDialog.visible = true
  photoDialog.photos = []
  photoDialog.isCapturing = false
}

async function startCamera() {
  photoDialog.cameraLoading = true
  try {
    await startGlobalCamera()
    photoDialog.isCapturing = true
    ElMessage.success('摄像头启动成功')
  } catch (error) {
    const errorMsg = getCameraErrorMessage(error)
    ElMessage.error(errorMsg)
  } finally {
    photoDialog.cameraLoading = false
  }
}

function stopCamera() {
  // 停止摄像头逻辑
  photoDialog.isCapturing = false
  ElMessage.success('摄像头已停止')
}

function capturePhoto() {
  // 拍照逻辑
  const mockPhoto = 'mock_base64_photo_data'
  photoDialog.photos.push(mockPhoto)
  ElMessage.success('拍照成功')
}

function removePhoto(index) {
  photoDialog.photos.splice(index, 1)
}

function previewPhoto(photo) {
  photoPreviewDialog.imageSrc = `data:image/png;base64,${photo}`
  photoPreviewDialog.visible = true
}

async function submitPhotos() {
  photoDialog.submitLoading = true
  try {
    // 提交照片逻辑
    await updateUserPhoto(records.value[photoDialog.index].id, photoDialog.photos)
    ElMessage.success('照片提交成功')
    photoDialog.visible = false
    await handleSearch({ page: 1, pageSize: 10 })
  } catch (error) {
    ElMessage.error('照片提交失败')
  } finally {
    photoDialog.submitLoading = false
  }
}

// 修改密码相关
function openPasswordDialog(index) {
  passwordDialog.index = index
  passwordDialog.visible = true
  passwordDialog.form = { newPassword: '', confirmPassword: '' }
}

async function handlePasswordChange() {
  passwordFormRef.value.validate(async (valid) => {
    if (valid) {
      if (passwordDialog.form.newPassword !== passwordDialog.form.confirmPassword) {
        ElMessage.error('两次输入的密码不一致')
        return
      }
      
      passwordDialog.loading = true
      try {
        const encryptedPassword = encryptPassword(passwordDialog.form.newPassword)
        await updateUserPassword(records.value[passwordDialog.index].id, encryptedPassword)
        ElMessage.success('密码修改成功')
        passwordDialog.visible = false
      } catch (error) {
        ElMessage.error('密码修改失败')
      } finally {
        passwordDialog.loading = false
      }
    }
  })
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
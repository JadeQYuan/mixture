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
            <el-tag :type="getRoleTagType(scope.row.roleCode)" size="large" effect="dark">
              {{ ROLE_MAP[scope.row.roleCode]?.name || scope.row.roleCode }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="account" label="工号" width="180" />
        <el-table-column prop="userName" label="姓名" width="240" />
        <el-table-column label="照片" width="120">
          <template #default="scope">
            <el-image 
              :src="scope.row.facePath ? `data:image/png;base64,${scope.row.facePath}` : '/src/assets/avatar.svg'" 
              style="width: 60px; height: 60px; border-radius: 8px; cursor: pointer;"
              fit="cover"
              @click="openAvatarPreview(scope.row)"
            >
              <template #error>
                <div style="width: 60px; height: 60px; background: #f5f5f5; border-radius: 8px; display: flex; align-items: center; justify-content: center; color: #999; cursor: pointer;">
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
            <el-button size="large" type="warning" @click="openPhotoDialog(scope.$index)">人脸录入</el-button>
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
      

      
      <el-form :model="dialog.form" :rules="dialog.rules" ref="dialogFormRef" label-width="180px" style="margin-top: 32px;">
        <!-- 新增用户：只显示基本信息 -->
        <template v-if="dialog.mode === 'add'">
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
          <!-- 新增用户：只显示确定和取消按钮 -->
          <template v-if="dialog.mode === 'add'">
            <el-button @click="dialog.visible = false" size="large">取消</el-button>
            <el-button type="primary" @click="handleDialogOk" size="large" :loading="dialog.loading">确定</el-button>
          </template>
          
          <!-- 编辑用户：只显示确定和取消按钮 -->
          <template v-else-if="dialog.mode === 'edit'">
            <el-button @click="dialog.visible = false" size="large">取消</el-button>
            <el-button type="primary" @click="handleDialogOk" size="large" :loading="dialog.loading">确定</el-button>
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
              style="position: relative; border: 2px solid #ddd; border-radius: 8px; overflow: hidden;"
              :class="{ 'selected-avatar': photo.isAvatar }"
            >
              <img 
                :src="photo.dataUrl" 
                style="width: 120px; height: 90px; object-fit: cover;"
                @click="selectAsAvatar(index)"
              />
              <div style="position: absolute; top: 4px; right: 4px;">
                <el-button 
                  type="danger" 
                  size="small" 
                  circle 
                  @click="deletePhoto(index)"
                  style="width: 24px; height: 24px; padding: 0;"
                >
                  ×
                </el-button>
              </div>
              <div 
                v-if="photo.isAvatar" 
                style="position: absolute; bottom: 4px; left: 4px; background: #67C23A; color: white; padding: 2px 6px; border-radius: 4px; font-size: 12px;"
              >
                头像
              </div>
            </div>
          </div>
          <div style="margin-top: 16px; text-align: center; color: #666; font-size: 14px;">
            点击照片可设为头像，点击 × 可删除照片
          </div>
        </div>
        
        <!-- 拍摄区域 -->
        <div style="text-align: center;">
          <div style="margin-bottom: 16px; font-size: 18px; color: #333;">
            请拍摄用户人脸照片 (最多5张)
          </div>
          <div style="position: relative; display: inline-block;">
            <video 
              ref="photoVideoRef" 
              autoplay 
              style="width: 600px; height: 450px; border-radius: 24px; border: 2px solid #409EFF;"
            ></video>
            <canvas ref="photoCanvasRef" style="display: none;"></canvas>
          </div>
          <div style="margin-top: 16px;">
            <el-button type="primary" size="large" @click="takePhotoForEdit" :disabled="photoDialog.photos.length >= 5">
              {{ photoDialog.photos.length >= 5 ? '已达上限' : '拍摄' }}
            </el-button>
            <el-button size="large" @click="startPhotoCamera" v-if="!photoStream">
              重新启动摄像头
            </el-button>
          </div>
          <div style="margin-top: 12px; color: #666; font-size: 14px;">
            已拍摄 {{ photoDialog.photos.length }}/5 张照片
          </div>
        </div>
      </div>
      <template #footer>
        <div style="padding: 0;">
          <el-button @click="photoDialog.visible = false" size="large">取消</el-button>
          <el-button type="primary" @click="handleUpdatePhoto" :disabled="photoDialog.photos.length === 0" size="large" :loading="photoDialog.loading">
            确定 ({{ photoDialog.photos.length }}张)
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
          <el-button type="primary" @click="handleUpdatePassword" size="large" :loading="passwordDialog.loading">确定</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onUnmounted, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { getUserList, createUser, updateUser, deleteUser, updateUserPassword, updateUserPhoto } from '../request/api'
import { pageSizeCalculators } from '../utils/pagination'
import { ROLE_MAP } from '../utils/roleMap'
import { encryptPassword } from '../utils/http'
import { getCameraErrorMessage, startGlobalCamera } from '../utils/camera'
const roles = Object.keys(ROLE_MAP)

// 获取角色标签类型
function getRoleTagType(role) {
  switch (role) {
    case 'MaterialClerk':
      return 'primary'
    case 'SeniorOperator':
      return 'warning'
    case 'Operator':
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
  index: null,
  loading: false, // 添加loading状态
  form: { roleCode: '', account: '', userName: '', remark: '', avatar: '' },
  rules: {
    roleCode: [ { required: true, message: '请选择角色', trigger: 'change' } ],
    account: [ { required: true, message: '请输入工号', trigger: 'blur' } ],
    userName: [ { required: true, message: '请输入姓名', trigger: 'blur' } ],
    remark: [ { required: false, message: '请输入描述', trigger: 'blur' } ]
  }
})
const dialogFormRef = ref()



// 修改照片相关
const photoDialog = reactive({
  visible: false,
  index: null,
  loading: false, // 添加loading状态
  photos: [] // 存储多张照片
})
const photoVideoRef = ref()
const photoCanvasRef = ref()
const photoStream = ref(null)

// 修改密码相关
const passwordDialog = reactive({
  visible: false,
  index: null,
  loading: false, // 添加loading状态
  form: { newPassword: '', confirmPassword: '' },
  rules: {
    newPassword: [ { required: true, message: '请输入新密码', trigger: 'blur' } ],
    confirmPassword: [ { required: true, message: '请再次输入新密码', trigger: 'blur' } ]
  }
})
const passwordFormRef = ref()

// 头像预览相关
const avatarPreviewDialog = reactive({
  visible: false,
  imageSrc: ''
})

function openAvatarPreview(user) {
  avatarPreviewDialog.imageSrc = user.facePath ? `data:image/png;base64,${user.facePath}` : '/src/assets/avatar.svg'
  avatarPreviewDialog.visible = true
}

function openDialog(mode, index = null) {
  dialog.mode = mode
  dialog.visible = true
  dialog.index = index
  if (mode === 'edit' && index !== null) {
    // 先重置form，避免残留
    dialog.form = { roleCode: '', account: '', userName: '', remark: '', avatar: '' }
    Object.assign(dialog.form, users.value[index])
  } else {
    dialog.form = { roleCode: '', account: '', userName: '', remark: '', avatar: '' }
  }
}

async function handleDialogOk() {
  if (dialog.loading) return // 防止重复提交
  
  try {
    dialog.loading = true
    if (dialog.mode === 'add') {
      // 新增模式：直接提交表单
      dialogFormRef.value.validate(async valid => {
        if (!valid) {
          dialog.loading = false
          return
        }
        try {
          const result = await createUser(dialog.form)
          ElMessage.success('新增成功')
          dialog.visible = false
          await fetchUsers() // 新增后立即刷新列表
          
          // 新增成功后自动打开人脸录入
          if (result && result.data) {
            // 使用返回的用户信息
            const newUser = result.data
                          // 延迟一下确保对话框已关闭
              setTimeout(() => {
                openPhotoDialog(users.value.findIndex(user => user.userId === newUser.userId))
              }, 300)
          }
        } catch (error) {
          ElMessage.error('操作失败')
        } finally {
          dialog.loading = false
        }
      })
      return
    } else if (dialog.mode === 'edit' && dialog.index !== null) {
      dialogFormRef.value.validate(async valid => {
        if (!valid) {
          dialog.loading = false
          return
        }
        try {
          await updateUser(dialog.form.userId, dialog.form)
          ElMessage.success('编辑成功')
          dialog.visible = false
          await fetchUsers() // 编辑后立即刷新列表
        } catch (error) {
          ElMessage.error('操作失败')
        } finally {
          dialog.loading = false
        }
      })
      return
    }
  } catch (error) {
    ElMessage.error('操作失败')
    dialog.loading = false
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

// 修改照片相关函数
function openPhotoDialog(index) {
  photoDialog.visible = true
  photoDialog.index = index
  photoDialog.photos = [] // 清空照片数组
  // 启动摄像头
  startPhotoCamera()
}

async function startPhotoCamera() {
  try {
    // 使用智能摄像头启动
    photoStream.value = await startGlobalCamera()
    
    if (photoVideoRef.value) {
      photoVideoRef.value.srcObject = photoStream.value
    }
  } catch (error) {
    console.error('摄像头启动失败:', error)
    
    // 使用统一的错误信息处理
    const errorMessage = getCameraErrorMessage(error)
    ElMessage.error(errorMessage)
  }
}

function takePhotoForEdit() {
  if (!photoVideoRef.value || !photoCanvasRef.value) return
  if (photoDialog.photos.length >= 5) {
    ElMessage.warning('已达到最大照片数量限制')
    return
  }
  
  const video = photoVideoRef.value
  const canvas = photoCanvasRef.value
  const context = canvas.getContext('2d')
  
  canvas.width = video.videoWidth
  canvas.height = video.videoHeight
  context.drawImage(video, 0, 0, canvas.width, canvas.height)
  
  // 将照片添加到数组中
  const photoData = canvas.toDataURL('image/jpeg', 0.8)
  photoDialog.photos.push({
    dataUrl: photoData,
    isAvatar: photoDialog.photos.length === 0 // 第一张照片自动设为头像
  })
  
  ElMessage.success('拍摄成功')
}

// 删除照片
function deletePhoto(index) {
  const isAvatar = photoDialog.photos[index].isAvatar
  photoDialog.photos.splice(index, 1)
  
  // 如果删除的是头像，且还有其他照片，则将第一张设为头像
  if (isAvatar && photoDialog.photos.length > 0) {
    photoDialog.photos[0].isAvatar = true
  }
  
  ElMessage.success('照片已删除')
}

// 选择为头像
function selectAsAvatar(index) {
  // 先取消所有照片的头像状态
  photoDialog.photos.forEach(photo => {
    photo.isAvatar = false
  })
  // 设置选中的照片为头像
  photoDialog.photos[index].isAvatar = true
  ElMessage.success('已设为头像')
}

async function handleUpdatePhoto() {
  if (photoDialog.index !== null) {
    if (photoDialog.loading) return // 防止重复提交
    if (photoDialog.photos.length === 0) {
      ElMessage.warning('请至少拍摄一张照片')
      return
    }
    
    try {
      photoDialog.loading = true
      const user = users.value[photoDialog.index]
      
      // 找到头像照片
      const avatarPhoto = photoDialog.photos.find(photo => photo.isAvatar)
      if (!avatarPhoto) {
        ElMessage.error('请选择一张照片作为头像')
        photoDialog.loading = false
        return
      }
      
      // 将头像照片转换为File对象
      const base64Data = avatarPhoto.dataUrl
      const byteCharacters = atob(base64Data.split(',')[1])
      const byteNumbers = new Array(byteCharacters.length)
      for (let i = 0; i < byteCharacters.length; i++) {
        byteNumbers[i] = byteCharacters.charCodeAt(i)
      }
      const byteArray = new Uint8Array(byteNumbers)
      const blob = new Blob([byteArray], { type: 'image/jpeg' })
      const imageFile = new File([blob], 'user_avatar.jpg', { type: 'image/jpeg' })
      
      await updateUserPhoto(user.userId, imageFile)
      ElMessage.success('人脸录入成功')
      photoDialog.visible = false
      await fetchUsers() // 重新获取列表
    } catch (error) {
      ElMessage.error('人脸录入失败')
    } finally {
      photoDialog.loading = false
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
  if (passwordDialog.loading) return // 防止重复提交
  
  passwordFormRef.value.validate(async valid => {
    if (!valid) return
    const user = users.value[passwordDialog.index]
    try {
      passwordDialog.loading = true
      // 前端AES加密
      const encrypted = encryptPassword(passwordDialog.form.newPassword)
      await updateUserPassword({ userId: user.userId, password: encrypted })
      ElMessage.success('密码修改成功')
      passwordDialog.visible = false
      passwordDialog.form.newPassword = ''
      passwordDialog.form.confirmPassword = ''
    } catch (e) {
      ElMessage.error('密码修改失败')
    } finally {
      passwordDialog.loading = false
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

/* 选中的头像样式 */
.selected-avatar {
  border-color: #67C23A !important;
  border-width: 3px !important;
  box-shadow: 0 0 8px rgba(103, 194, 58, 0.3);
}


</style> 
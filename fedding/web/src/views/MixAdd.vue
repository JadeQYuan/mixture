<template>
  <div class="mix-add-container">
    <el-card class="form-card">
      <el-form :model="form" :rules="rules" ref="formRef" label-width="100px">
        <el-form-item label="混合料名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入混合料名称" />
        </el-form-item>
        <el-form-item label="批次" prop="batch">
          <el-input v-model="form.batch" placeholder="请输入批次" />
        </el-form-item>
        <el-form-item label="负责人" prop="person">
          <el-input v-model="form.person" placeholder="请输入负责人" />
        </el-form-item>
      </el-form>
    </el-card>
    <el-card class="table-card">
      <div class="table-header">
        <span>原料明细</span>
        <el-button type="primary" @click="addRow">添加原料</el-button>
      </div>
      <el-table :data="form.materials" style="width: 100%">
        <el-table-column prop="material" label="原料名称">
          <template #default="scope">
            <el-input v-model="scope.row.material" placeholder="请输入原料名称" />
          </template>
        </el-table-column>
        <el-table-column prop="amount" label="数量">
          <template #default="scope">
            <el-input-number v-model="scope.row.amount" :min="0" placeholder="数量" />
          </template>
        </el-table-column>
        <el-table-column prop="unit" label="单位">
          <template #default="scope">
            <el-input v-model="scope.row.unit" placeholder="单位" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="scope">
            <el-button type="danger" size="small" @click="confirmDelete(scope.$index)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
    <div class="submit-btn">
      <el-button type="primary" @click="submit">提交</el-button>
    </div>
    <el-dialog v-model="deleteDialog.visible" title="确认删除" width="300px">
      <span>确定要删除该原料吗？</span>
      <template #footer>
        <el-button @click="deleteDialog.visible = false">取消</el-button>
        <el-button type="danger" @click="deleteRow">删除</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'

const formRef = ref()
const form = reactive({
  name: '',
  batch: '',
  person: '',
  materials: [
    { material: '', amount: 0, unit: '' }
  ]
})
const rules = {
  name: [ { required: true, message: '请输入混合料名称', trigger: 'blur' } ],
  batch: [ { required: true, message: '请输入批次', trigger: 'blur' } ],
  person: [ { required: true, message: '请输入负责人', trigger: 'blur' } ]
}
const deleteDialog = reactive({ visible: false, index: null })

function addRow() {
  form.materials.push({ material: '', amount: 0, unit: '' })
}
function confirmDelete(index) {
  deleteDialog.visible = true
  deleteDialog.index = index
}
function deleteRow() {
  if (deleteDialog.index !== null) {
    form.materials.splice(deleteDialog.index, 1)
    deleteDialog.visible = false
    deleteDialog.index = null
  }
}
function validateMaterials() {
  if (form.materials.length === 0) {
    ElMessage.error('请至少添加一条原料明细')
    return false
  }
  for (const [i, m] of form.materials.entries()) {
    if (!m.material || !m.unit || m.amount === null || m.amount === undefined) {
      ElMessage.error(`第${i+1}行原料信息不完整`)
      return false
    }
  }
  return true
}
function submit() {
  formRef.value.validate((valid) => {
    if (valid && validateMaterials()) {
      ElMessage.success('提交成功！')
      // 这里可以提交数据到后端
    }
  })
}
</script>

<style scoped>
.mix-add-container {
  max-width: 900px;
  margin: 40px auto;
}
.form-card {
  margin-bottom: 24px;
}
.table-card {
  margin-bottom: 24px;
}
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}
.submit-btn {
  text-align: right;
}
</style> 
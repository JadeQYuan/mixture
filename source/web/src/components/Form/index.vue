<template>
  <div class="form-container">
    <slot name="form-top"></slot>
    
    <!-- 表单内容 -->
    <el-form 
      ref="formRef" 
      :model="formData" 
      :rules="rules" 
      :label-width="labelWidth"
      :label-position="labelPosition"
      style="margin-top: 32px;"
    >
      <!-- 动态表单字段 -->
      <template v-for="field in props.fields" :key="field.prop">
        <el-form-item 
          v-if="getFieldVisible(field)"
          :label="field.label" 
          :prop="field.prop"
          :label-width="field.labelWidth || labelWidth"
        >
          <!-- 输入框 -->
          <el-input 
            v-if="field.type === 'input'"
            v-model="formData[field.prop]"
            :placeholder="field.placeholder"
            :disabled="getFieldDisabled(field)"
            :type="field.inputType || 'text'"
            :size="field.size || 'large'"
            :style="field.style"
            :show-password="field.showPassword"
            v-bind="field.props || {}"
          >
            <template v-if="field.suffix" #suffix>
              {{ field.suffix }}
            </template>
          </el-input>
          
          <!-- 数字输入框 -->
          <el-input-number 
            v-else-if="field.type === 'number'"
            v-model="formData[field.prop]"
            :placeholder="field.placeholder"
            :disabled="getFieldDisabled(field)"
            :min="field.min"
            :max="field.max"
            :precision="field.precision"
            :size="field.size || 'large'"
            :style="field.style || { width: '100%' }"
            v-bind="field.props || {}"
          >
            <template v-if="field.suffix" #suffix>
              {{ field.suffix }}
            </template>
          </el-input-number>
          
          <!-- 选择框 -->
          <el-select 
            v-else-if="field.type === 'select'"
            v-model="formData[field.prop]"
            :placeholder="field.placeholder"
            :disabled="getFieldDisabled(field)"
            :size="field.size || 'large'"
            :style="field.style"
            v-bind="field.props || {}"
          >
            <el-option 
              v-for="option in field.options" 
              :key="option.value" 
              :label="option.label" 
              :value="option.value" 
            />
          </el-select>
          
          <!-- 文本域 -->
          <el-input 
            v-else-if="field.type === 'textarea'"
            v-model="formData[field.prop]"
            :placeholder="field.placeholder"
            :disabled="getFieldDisabled(field)"
            :rows="field.rows || 3"
            :size="field.size || 'large'"
            :style="field.style"
            type="textarea"
            v-bind="field.props || {}"
          />
          
          <!-- 日期选择器 -->
          <el-date-picker 
            v-else-if="field.type === 'date'"
            v-model="formData[field.prop]"
            :placeholder="field.placeholder"
            :disabled="getFieldDisabled(field)"
            :size="field.size || 'large'"
            :style="field.style"
            :type="field.dateType || 'date'"
            v-bind="field.props || {}"
          />
          
          <!-- 时间选择器 -->
          <el-time-picker 
            v-else-if="field.type === 'time'"
            v-model="formData[field.prop]"
            :placeholder="field.placeholder"
            :disabled="getFieldDisabled(field)"
            :size="field.size || 'large'"
            :style="field.style"
            v-bind="field.props || {}"
          />
          
          <!-- 开关 -->
          <el-switch 
            v-else-if="field.type === 'switch'"
            v-model="formData[field.prop]"
            :disabled="getFieldDisabled(field)"
            :size="field.size || 'large'"
            v-bind="field.props || {}"
          />
          
          <!-- 单选框组 -->
          <el-radio-group 
            v-else-if="field.type === 'radio'"
            v-model="formData[field.prop]"
            :disabled="getFieldDisabled(field)"
            :size="field.size || 'large'"
            v-bind="field.props || {}"
          >
            <el-radio 
              v-for="option in field.options" 
              :key="option.value" 
              :label="option.value"
            >
              {{ option.label }}
            </el-radio>
          </el-radio-group>
          
          <!-- 复选框组 -->
          <el-checkbox-group 
            v-else-if="field.type === 'checkbox'"
            v-model="formData[field.prop]"
            :disabled="getFieldDisabled(field)"
            :size="field.size || 'large'"
            v-bind="field.props || {}"
          >
            <el-checkbox 
              v-for="option in field.options" 
              :key="option.value" 
              :label="option.value"
            >
              {{ option.label }}
            </el-checkbox>
          </el-checkbox-group>
          
          <!-- 自定义组件 -->
          <component 
            v-else-if="field.component"
            :is="field.component"
            v-model="formData[field.prop]"
            v-bind="field.props || {}"
            v-on="field.events || {}"
          />
          
          <!-- 默认文本显示 -->
          <span v-else>{{ formData[field.prop] }}</span>
        </el-form-item>
      </template>
    </el-form>
    
    <!-- 底部按钮 -->
    <div class="form-footer">
      <!-- 配置化的底部按钮 -->
      <template v-if="footerButtons.length > 0">
        <template v-for="button in footerButtons">
          <el-button
            v-if="getButtonVisible(button)"
            :key="button.key"
            :type="button.type || 'default'"
            :size="button.size || 'large'"
            :disabled="button.disabled"
            :loading="getButtonLoading(button)"
            @click="handleFooterButtonClick(button.validate, button.action)"
          >
            {{ button.text }}
          </el-button>
        </template>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, nextTick } from 'vue'

// Props 定义
const props = defineProps({
  // 表单字段配置
  fields: {
    type: Array,
    default: () => []
  },
  // 表单数据
  formData: {
    type: Object,
    default: () => ({})
  },
  // 验证规则
  rules: {
    type: Object,
    default: () => ({})
  },
  // 标签宽度
  labelWidth: {
    type: String,
    default: '120px'
  },
  // 标签位置
  labelPosition: {
    type: String,
    default: 'right'
  },
  // 底部按钮配置
  footerButtons: {
    type: Array,
    default: () => []
  },
  extends: {
    type: [Object, Number, String]
  }
})

// 响应式数据
const formRef = ref()

// 新增：暴露resetFields方法
function resetFields() {
  formRef.value?.resetFields?.()
}

// 处理底部按钮点击
function handleFooterButtonClick(validate, action) {
  if (validate) {
    // 对于submit动作，先验证表单，然后发出submit事件
    formRef.value?.validate(async (valid) => {
      if (valid) {
        action(props.formData)
      }
    })
  } else {
    action(props.formData)
  }
}

// 获取按钮loading状态
function getButtonLoading(button) {
  // 如果按钮有自定义的loading函数，使用它
  if (typeof button.loading === 'function') {
    return button.loading()
  }
  // 否则使用静态的loading值
  return button.loading || false
}

// 获取按钮显示状态
function getButtonVisible(button) {
  // 如果按钮有自定义的visible函数，使用它
  if (typeof button.visible === 'function') {
    if (props.extends !== null) {
      return button.visible(props.extends, props.formData)
    } else {
      return button.visible(props.formData)
    }
  }
  // 否则使用静态的visible值，默认为true
  return button.visible !== false
}

// 获取字段禁用状态
function getFieldDisabled(field) {
  // 如果字段有自定义的disabled函数，使用它
  if (typeof field.disabled === 'function') {
    return field.disabled(props.formData, props.extends)
  }
  // 否则使用静态的disabled值
  return field.disabled || false
}

// 获取字段显示状态
function getFieldVisible(field) {
  // 如果字段有自定义的visible函数，使用它
  if (typeof field.visible === 'function') {
    return field.visible(props.formData, props.extends)
  }
  // 否则使用静态的visible值，默认为true
  return field.visible !== false
}

// 暴露表单引用
defineExpose({
  formRef,
  resetFields
})
</script>

<style scoped>
.form-container {
  width: 100%;
}

.form-footer {
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 20px 0 0 0;
}

/* 表单样式 */
:deep(.el-form-item__label) {
  font-size: 16px !important;
}

:deep(.el-input__inner),
:deep(.el-textarea__inner),
:deep(.el-select .el-input__inner) {
  font-size: 16px !important;
}

:deep(.el-input-number .el-input__inner) {
  font-size: 16px !important;
}

/* 按钮样式 */
:deep(.el-button) {
  font-size: 16px !important;
  height: 40px !important;
  padding: 0 16px !important;
}

/* 步骤条样式 */
:deep(.el-steps .el-step__title) {
  font-size: 16px !important;
}

:deep(.el-steps .el-step__description) {
  font-size: 14px !important;
}
</style> 
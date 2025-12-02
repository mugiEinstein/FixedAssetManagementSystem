<template>
  <div class="login-container">
    <div class="login-box">
      <h2 class="login-title">固定资产管理系统</h2>

      <el-form ref="formRef" :model="loginForm" :rules="loginRules" size="large">
        <el-form-item prop="username">
          <el-input
              v-model="loginForm.username"
              placeholder="请输入用户ID（如：U202341001）"
              :prefix-icon="User"
          />
        </el-form-item>

        <el-form-item prop="password">
          <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              :prefix-icon="Lock"
              show-password
              @keyup.enter="handleLogin"
          />
        </el-form-item>

        <el-form-item>
          <el-button
              type="primary"
              :loading="loading"
              style="width: 100%"
              @click="handleLogin"
          >
            登 录
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-tips">
        <p>管理员：ADMIN-888 / 123456</p>
        <p>学生示例：U202341001 | 教师示例：T2023001</p>
        <p>默认密码：123456</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive({
  username: 'ADMIN-888',
  password: '123456',
  role: 'system_admin'
})

const loginRules: FormRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

async function handleLogin() {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true
    try {
      await userStore.login(loginForm.username, loginForm.password, loginForm.role)
      ElMessage.success('登录成功')
      router.push('/dashboard')
    } catch (error: any) {
      console.error('登录失败:', error)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('@/assets/login-bg.jpg');
  background-size: cover;
  background-position: top left;
  background-repeat: no-repeat;
}

.login-box {
  width: 420px;
  padding: 40px;
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #333;
  font-size: 24px;
}

.login-tips {
  text-align: center;
  margin-top: 20px;
  color: #999;
  font-size: 12px;
}

.login-tips p {
  margin: 5px 0;
}
</style>
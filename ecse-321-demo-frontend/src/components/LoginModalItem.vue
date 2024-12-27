<template>
  <Dialog
    :visible="visible"
    @update:visible="$emit('update:visible', $event)"
    modal
    :dismissableMask="true"
    :closable="true"
    :style="{ width: '400px' }"
    :header="isSignupMode ? 'Sign Up' : 'Login'"
  >
    <div class="flex flex-column gap-2">
      <IftaLabel>
        <InputText id="username" v-model="credentials.username" fluid />
        <label for="username">Username</label>
      </IftaLabel>

      <IftaLabel>
        <Password
          id="password"
          v-model="credentials.password"
          :feedback="isSignupMode"
          toggleMask
          fluid
        />
        <label for="password">Password</label>
      </IftaLabel>

      <IftaLabel v-if="isSignupMode">
        <Password
          id="confirmPassword"
          v-model="credentials.confirmPassword"
          :feedback="isSignupMode"
          toggleMask
          fluid
        />
        <label for="confirmpassword">Confirm Password</label>
      </IftaLabel>
      <small v-if="errorMessage" class="p-error">{{ errorMessage }}</small>

      <div class="flex flex-column gap-2 mt-4">
        <Button
          :label="isSignupMode ? 'Sign Up' : 'Login'"
          @click="handleSubmit"
          :loading="loading"
        />
        <Button
          :label="isSignupMode ? 'Back to Login' : 'Create Account'"
          class="p-button-text"
          @click="toggleMode"
        />
      </div>
    </div>
  </Dialog>
</template>

<script setup>
import { ref, watch } from 'vue'
import Dialog from 'primevue/dialog'
import InputText from 'primevue/inputtext'
import Password from 'primevue/password'
import Button from 'primevue/button'
import IftaLabel from 'primevue/iftalabel'
import api from '@/services/api'

const props = defineProps({
  visible: {
    type: Boolean,
    required: true,
  },
})

const emit = defineEmits(['update:visible', 'loginSuccess'])

const isSignupMode = ref(false)
const loading = ref(false)
const errorMessage = ref('')
const credentials = ref({
  username: '',
  password: '',
  confirmPassowrd: '',
})

const resetForm = () => {
  credentials.value = {
    username: '',
    password: '',
  }
  errorMessage.value = ''
}

const toggleMode = () => {
  isSignupMode.value = !isSignupMode.value
  resetForm()
}

const handleSubmit = async () => {
  try {
    loading.value = true
    errorMessage.value = ''

    if (isSignupMode.value) {
      await api.post('/users', credentials.value)
      isSignupMode.value = false
      errorMessage.value = 'Account created! Please login.'
    } else {
      const response = await api.post('/users/login', credentials.value)
      localStorage.setItem('userId', response.data.userId)
      localStorage.setItem('username', response.data.username)
      emit('update:visible', false)
      emit('loginSuccess')
      resetForm()
    }
  } catch (error) {
    if (error.response?.status === 409) {
      errorMessage.value = 'Username already exists'
    } else if (error.response?.status === 401) {
      errorMessage.value = 'Invalid username or password'
    } else {
      errorMessage.value = 'An error occurred. Please try again.'
    }
  } finally {
    loading.value = false
  }
}

watch(
  () => props.visible,
  (newVal) => {
    if (!newVal) {
      isSignupMode.value = false
      resetForm()
    }
  },
)
</script>

<style scoped>
:deep(.p-float-label) {
  width: 100%;
}

:deep(.p-float-label input),
:deep(.p-float-label .p-password) {
  width: 100%;
}

:deep(.p-password-input) {
  width: 100%;
}
</style>

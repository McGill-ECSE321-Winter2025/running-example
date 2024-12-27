<template>
  <Toast />
  <Button icon="pi pi-filter" @click="toggle" />

  <Popover ref="op">
    <span class="font-medium block pb-2">Filter Events</span>

    <Form
      v-slot="$form"
      :initialValues="filterValues"
      :resolver="resolver"
      @submit="onFilterSubmit"
      class="filter-container"
    >
      <IftaLabel class="filter-item">
        <Select name="eventType" :options="eventTypes" optionLabel="label" fluid />
        <label for="eventType">Event Type</label>
      </IftaLabel>

      <IftaLabel class="filter-item">
        <DatePicker name="startTime" showTime hourFormat="12" />
        <label for="startTime">Start Time</label>
      </IftaLabel>

      <IftaLabel class="filter-item">
        <DatePicker name="endTime" showTime hourFormat="12" />
        <label for="endTime">End Time</label>
        <Message v-if="$form.endTime?.invalid" severity="error" size="small" variant="simple">
          {{ $form.endTime.error?.message }}
        </Message>
      </IftaLabel>

      <Button type="submit" label="Filter" />
    </Form>
  </Popover>
</template>

<script setup>
import { ref } from 'vue'
import { Form } from '@primevue/forms'
import { useToast } from 'primevue/usetoast'
import Button from 'primevue/button'
import Popover from 'primevue/popover'
import IftaLabel from 'primevue/iftalabel'
import Select from 'primevue/select'
import DatePicker from 'primevue/datepicker'
import Message from 'primevue/message'
import Toast from 'primevue/toast'

const op = ref()

const toggle = (event) => {
  op.value.toggle(event)
}

const toast = useToast()

const eventTypes = ref([
  { label: 'Online Event', value: 'ONLINE' },
  { label: 'In-Person Event', value: 'INPERSON' },
])

const filterValues = ref({
  eventType: '',
  startTime: null,
  endTime: null,
})

const resolver = ({ values }) => {
  const errors = {}

  if (values.startTime && new Date(values.endTime) <= new Date(values.startTime)) {
    errors.endTime = [{ message: 'End time must be after start time' }]
  }
  return { errors }
}

const onFilterSubmit = ({ valid }) => {
  if (valid) {
    toast.add({
      severity: 'warn',
      summary: 'Project would be too easy if I showed you how to do this too !!',
      life: 3000,
    })
  }
}
</script>

<style scoped>
.filter-container {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.filter-item {
  width: 100%;
}
</style>

<template>
  <Toast />
  <Button icon="pi pi-filter" @click="toggle" />

  <Popover ref="op">
    <span class="font-medium block pb-2">Filter Registrations</span>

    <Form
      v-slot="$form"
      :initialValues="filterValues"
      :resolver="resolver"
      @submit="onFilterSubmit"
      class="grid grid-cols-2 gap-2 p-2"
    >
      <IftaLabel>
        <Select name="eventType" :options="eventTypes" optionLabel="label" fluid />
        <label for="eventType">Event Type</label>
      </IftaLabel>

      <IftaLabel>
        <DatePicker name="registeredDateRange" showIcon selectionMode="range" />
        <label for="registeredDateRange">Registration Date</label>
      </IftaLabel>

      <IftaLabel>
        <DatePicker name="startTime" showIcon showTime hourFormat="12" />
        <label for="startTime">Start Time</label>
      </IftaLabel>

      <IftaLabel class="filter-item">
        <DatePicker name="endTime" showIcon showTime hourFormat="12" />
        <label for="endTime">End Time</label>
        <Message v-if="$form.endTime?.invalid" severity="error" size="small" variant="simple">
          {{ $form.endTime.error?.message }}
        </Message>
      </IftaLabel>

      <Button type="submit" label="Filter" class="col-span-2" />
    </Form>
  </Popover>
</template>

<script setup>
import { ref } from 'vue'
import { Form } from '@primevue/forms'
import { useToast } from 'primevue/usetoast'
import Toast from 'primevue/toast'
import Button from 'primevue/button'
import Popover from 'primevue/popover'
import IftaLabel from 'primevue/iftalabel'
import Select from 'primevue/select'
import DatePicker from 'primevue/datepicker'
import Message from 'primevue/message'

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
  registeredDateRange: null,
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
      summary: 'yeah...',
      life: 3000,
    })
  }
}
</script>

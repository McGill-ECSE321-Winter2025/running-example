<template>
  <Toolbar class="border-none">
    <template #start>
      <h1 class="text-3xl font-bold">{{ title }}</h1>
    </template>

    <template #center>
      <IconField>
        <InputIcon>
          <i class="pi pi-search" />
        </InputIcon>
        <InputText disabled placeholder="(disabled...)" />
      </IconField>
    </template>

    <template #end>
      <Button
        icon="pi pi-filter"
        variant="text"
        @click="toggleFilterEvent($event)"
        aria-label="Filter"
      />
      <Popover ref="filterOverlay" :showCloseIcon="true">
        <slot name="filterContent"></slot>
      </Popover>
      <Button
        v-if="showNewButton"
        icon="pi pi-plus"
        class="ml-2"
        variant="text"
        label="New Event"
        @click="handleNew"
        aria-label="New"
      />
    </template>
  </Toolbar>
</template>

<script setup>
import { ref } from 'vue'
import Toolbar from 'primevue/toolbar'
import Button from 'primevue/button'
import InputText from 'primevue/inputtext'
import IconField from 'primevue/iconfield'
import InputIcon from 'primevue/inputicon'
import Popover from 'primevue/popover'

defineProps({
  title: {
    type: String,
    required: true,
  },
  showNewButton: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['search', 'new'])

const filterOverlay = ref(null)

const handleNew = () => {
  emit('new')
}
</script>

<style scoped>
:deep(.p-menubar) {
  border: none;
  background: transparent;
  border-radius: 0;
}
</style>

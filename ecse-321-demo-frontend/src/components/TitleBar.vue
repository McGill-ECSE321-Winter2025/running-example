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
      <div class="flex flex-row gap-2">
        <slot name="filterButton"></slot>
        <slot name="newButton"></slot>
      </div>
    </template>
  </Toolbar>
</template>

<script setup>
import Toolbar from 'primevue/toolbar'
import InputText from 'primevue/inputtext'
import IconField from 'primevue/iconfield'
import InputIcon from 'primevue/inputicon'

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
</script>

<style scoped>
:deep(.p-menubar) {
  border: none;
  background: transparent;
  border-radius: 0;
}
</style>

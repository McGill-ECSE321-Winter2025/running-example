<template>
  <div class="datatable-card-container">
    <div class="table-wrapper">
      <div v-if="loading" class="loading-state">
        <ProgressSpinner />
      </div>

      <div v-else-if="error" class="error-state">
        {{ error }}
      </div>

      <div v-else class="table-container">
        <slot name="datatable"></slot>
      </div>
    </div>
  </div>
</template>

<script setup>
import ProgressSpinner from 'primevue/progressspinner'

defineProps({
  loading: {
    type: Boolean,
    required: true,
  },
  error: {
    type: String,
    default: null,
  },
})
</script>

<style scoped>
.datatable-card-container {
  display: flex;
  flex-direction: column;
  height: 100%;
  overflow-y: hidden;
}

.table-wrapper {
  flex: 1;
  min-height: 0;
  position: relative;
}

.loading-state,
.error-state {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.table-container {
  height: 100%;
}
</style>

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
        <DataTable
          :value="data"
          :paginator="true"
          :rows="rows"
          :resizableColumns="true"
          columnResizeMode="fit"
          scrollable
          scrollHeight="flex"
        >
          <slot></slot>
        </DataTable>
      </div>
    </div>
  </div>
</template>

<script setup>
import DataTable from 'primevue/datatable'
import ProgressSpinner from 'primevue/progressspinner'

defineProps({
  title: {
    type: String,
    required: true,
  },
  showNewButton: {
    type: Boolean,
    default: false,
  },
  data: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    required: true,
  },
  error: {
    type: String,
    default: null,
  },
  rows: {
    type: Number,
    default: 25,
  },
})
</script>

<style scoped>
.datatable-card-container {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.header {
  flex-shrink: 0;
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

:deep(.p-datatable) {
  height: 100%;
}

:deep(.p-datatable-wrapper) {
  height: 100%;
}

:deep(.p-datatable-table) {
  min-width: 100%;
}

:deep(.p-column-header-content) {
  display: flex;
  align-items: center;
}

:deep(.p-paginator) {
  position: sticky;
  bottom: 0;
  background: var(--surface-card);
  z-index: 1;
}
</style>

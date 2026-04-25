<template>
  <div id="appPage">
    <RouterView v-if="isBlankLayout" />
    <BasicLayout v-else>
      <RouterView />
    </BasicLayout>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute, RouterView } from 'vue-router'
import BasicLayout from './layouts/BasicLayout.vue'

const route = useRoute()
const isBlankLayout = computed(() => route.meta?.layout === 'blank')
</script>

<style scoped>
#appPage {
}
</style>

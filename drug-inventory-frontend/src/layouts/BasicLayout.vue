<template>
  <div id="basicLayoutPage">
    <el-container class="layoutContainer">
      <el-aside :width="asideWidth">
        <AppSidebar :collapsed="collapsed" :active-menu="activeMenu" />
      </el-aside>

      <el-container>
        <el-header style="padding: 0">
          <AppHeader :collapsed="collapsed" @toggle-collapse="toggleCollapse" />
        </el-header>

        <el-main class="mainArea">
          <div class="contentCard">
            <slot />
          </div>
        </el-main>

        <el-footer class="footerArea">
          <AppFooter />
        </el-footer>
      </el-container>
    </el-container>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute } from 'vue-router'
import AppSidebar from '@/components/layout/AppSidebar.vue'
import AppHeader from '@/components/layout/AppHeader.vue'
import AppFooter from '@/components/layout/AppFooter.vue'

const route = useRoute()
const activeMenu = computed(() => route.path)

const collapsed = ref(false)
const asideWidth = computed(() => (collapsed.value ? '64px' : '200px'))
const toggleCollapse = () => {
  collapsed.value = !collapsed.value
}
</script>

<style scoped>
#basicLayoutPage {
  min-height: 100vh;
  background: #f3f4f6;
}

.layoutContainer {
  min-height: 100vh;
}

.mainArea {
  padding: 16px;
  background: #f3f4f6;
}

.contentCard {
  min-height: calc(100vh - 56px - 44px - 32px);
  padding: 16px;
  border-radius: 8px;
  background: #ffffff;
  box-shadow: 0 1px 3px rgb(0 0 0 / 8%);
}

.footerArea {
  padding: 0;
}
</style>

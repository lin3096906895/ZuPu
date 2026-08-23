<template>
  <el-container class="layout">
    <el-aside width="200px" class="aside">
      <div class="logo">族谱管理系统</div>
      <el-menu :default-active="$route.path" router background-color="#001529" text-color="#c0c4cc" active-text-color="#fff">
        <el-menu-item index="/dashboard"><el-icon><DataBoard /></el-icon>仪表盘</el-menu-item>
        <el-menu-item index="/families"><el-icon><OfficeBuilding /></el-icon>家族管理</el-menu-item>
        <el-menu-item index="/persons"><el-icon><User /></el-icon>人物管理</el-menu-item>
        <el-menu-item index="/tree"><el-icon><Share /></el-icon>家族树</el-menu-item>
        <el-menu-item index="/ai"><el-icon><ChatDotRound /></el-icon>AI 助手</el-menu-item>
        <el-menu-item index="/stats"><el-icon><PieChart /></el-icon>统计看板</el-menu-item>
        <el-menu-item index="/relationship"><el-icon><Connection /></el-icon>关系查询</el-menu-item>
        <el-menu-item index="/migration"><el-icon><MapLocation /></el-icon>迁徙地图</el-menu-item>
      </el-menu>
    </el-aside>
    <el-container>
      <el-header class="header">
        <div></div>
        <div class="user">
          <span>{{ auth.user?.displayName || auth.user?.username }}</span>
          <el-button link type="danger" @click="handleLogout">退出</el-button>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const auth = useAuthStore()

function handleLogout() {
  auth.logout()
  router.push('/login')
}
</script>

<style scoped>
.layout {
  height: 100%;
}
.aside {
  background-color: #001529;
}
.logo {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  text-align: center;
  padding: 18px 0;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
  background: #fff;
}
.user {
  display: flex;
  align-items: center;
  gap: 8px;
}
</style>

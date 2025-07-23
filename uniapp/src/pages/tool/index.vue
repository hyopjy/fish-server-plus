<route lang="json5" type="home">
{
  style: {
    navigationStyle: 'default',
    navigationBarTitleText: '游戏管理',
    navigationBarTextStyle: 'white',
    navigationBarBackgroundColor: '#3B82F6',
  },
}
</route>

<template>
  <view class="min-h-screen bg-gray-100 p-4">
    <view class="bg-white rounded-lg shadow p-4">
      <view class="text-xl font-bold mb-6 text-center">游戏配置导航</view>
      <view class="space-y-4">
        <button
          v-for="item in navList"
          :key="item.title"
          class="w-full py-3 rounded-lg text-lg"
          :class="item.enabled ? 'bg-blue-500 text-white' : 'bg-gray-200 text-gray-700'"
          :disabled="!item.enabled"
          @click="handleNav(item)"
        >
          {{ item.title }}
        </button>
      </view>
    </view>
  </view>
</template>

<script lang="ts" setup>
const navList = [
  { title: '赛制配置', path: '/pages/gameTool/gameTool', enabled: true },
  { title: '世界Boss配置（待定）', path: '', enabled: false },
  { title: 'Boss奖励配置（待定）', path: '', enabled: false },
  { title: '用户背包管理（待定）', path: '', enabled: false },
  { title: '神秘商人设置（待定）', path: '', enabled: false },
  { title: '休渔期设置（待定）', path: '', enabled: false },
  { title: '渔获设置（待定）', path: '', enabled: false },
]

const handleNav = (item) => {
  if (item.enabled && item.path) {
    uni.navigateTo({ url: item.path })
  }
}
</script>

<style lang="scss" scoped>
.min-h-screen {
  min-height: 100vh;
}
</style>

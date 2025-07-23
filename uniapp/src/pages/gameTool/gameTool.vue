<route lang="json5">
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
    <!-- 群组选择 -->
    <view class="bg-white rounded-lg p-4 mb-4 shadow">
      <text class="block text-gray-700 mb-2">选择游戏群</text>
      <picker mode="selector" :range="groupNames" @change="handleGroupChange">
        <view class="flex items-center justify-between p-2 border rounded">
          <text class="text-gray-600">选择群组：</text>
          <text class="font-medium">{{ selectedGroup?.group?.groupName || '请选择群组' }}</text>
        </view>
      </picker>
    </view>

    <!-- 新增游戏按钮 -->
    <button @click="goToAddGame" class="bg-blue-500 text-white px-4 py-2 rounded-lg text-sm mb-4">
      新增游戏
    </button>

    <!-- 游戏列表 -->
    <view v-if="gamesList.length > 0" class="space-y-4">
      <view v-for="game in gamesList" :key="game.id" class="bg-white rounded-lg p-4 shadow">
        <!-- 游戏基础信息展示（与原来一致） -->
        <view class="space-y-2">
          <view class="flex justify-between">
            <text class="text-gray-600">游戏名称：</text>
            <text class="font-medium">{{ game.venue }}</text>
          </view>
          <view class="flex justify-between">
            <text class="text-gray-600">游戏群：</text>
            <text class="font-medium">
              {{ selectedGroup?.group.groupName }}({{ game.groupId }})
            </text>
          </view>
          <view class="flex justify-between">
            <text class="text-gray-600">日期：</text>
            <text class="font-medium">{{ game.day }}</text>
          </view>
          <view class="flex justify-between">
            <text class="text-gray-600">开始时间：</text>
            <text class="font-medium">{{ game.startTime }}</text>
          </view>
          <view class="flex justify-between">
            <text class="text-gray-600">结束时间：</text>
            <text class="font-medium">{{ game.endTime }}</text>
          </view>
          <view class="flex justify-between">
            <text class="text-gray-600">游戏方式：</text>
            <text class="font-medium">{{ game.playingMethod }}</text>
          </view>
          <view class="flex justify-between">
            <text class="text-gray-600">轮次：</text>
            <text class="font-medium">{{ game.round }}</text>
          </view>
          <view class="flex justify-between">
            <text class="text-gray-600">是否奖励：</text>
            <text class="font-medium">{{ game.giveProp ? '是' : '否' }}</text>
          </view>
          <view class="flex justify-between">
            <text class="text-gray-600">奖励道具：</text>
            <text class="font-medium">{{ game.propName }}</text>
          </view>
          <view class="flex justify-between">
            <text class="text-gray-600">状态：</text>
            <text :class="game.running ? 'text-green-500' : 'text-red-500'" class="font-medium">
              {{ game.running ? '进行中' : '未开始' }}
            </text>
          </view>
        </view>
        <!-- 操作按钮 -->
        <view class="flex space-x-2 mt-4">
          <button
            @click="startGame(game)"
            class="bg-green-500 text-white px-3 py-1 rounded text-sm"
          >
            开始游戏
          </button>
          <button
            @click="restartGame(game)"
            class="bg-yellow-500 text-white px-3 py-1 rounded text-sm"
          >
            重新开始
          </button>
          <button @click="deleteGame(game)" class="bg-red-500 text-white px-3 py-1 rounded text-sm">
            删除游戏
          </button>
          <button
            @click="viewRecords(game)"
            class="bg-blue-500 text-white px-3 py-1 rounded text-sm"
          >
            查看游戏记录
          </button>
        </view>
      </view>
    </view>
    <view v-else class="text-center text-gray-500 py-8">
      暂无游戏信息，请点击新增游戏按钮创建游戏
    </view>
  </view>
</template>

<script lang="ts" setup>
// src/pages/gameTool/gameTool.vue 的 script 部分
import { ref, onMounted, computed } from 'vue'
import { openGame, reGame, stopGame, getAllGroupUser, getGamesByGroupId } from '@/service/game/game'

// 游戏信息数据
const gameInfo = ref(null)

// 添加记录接口定义
interface RodeoRecord {
  id: number
  rodeoId: string
  player: string
  turns: number
  rodeoDesc: string
  winFlag: number
  forbiddenSpeech: number
}

// 修改 gameRecords 的类型声明
const gameRecords = ref<RodeoRecord[]>([])

// 在 script 部分添加接口定义
interface RodeoResponse {
  rodeo: {
    id: string
    playingMethod: string
    groupId: string
    venue: string
    day: string
    startTime: string
    endTime: string
    players: string
    round: number
    running: number
    giveProp: boolean // 新增
    propName: string // 新增
  }
}

// 添加群组用户信息接口
interface GroupInfo {
  groupId: string
  groupName: string
}

interface GroupUser {
  id: string
  groupId: string
  userId: string
  userNick: string
}

interface GroupUserInfo {
  group: GroupInfo
  groupUserList: GroupUser[]
}

// 群组列表
const groupList = ref<GroupUserInfo[]>([])
// 选中的群组
const selectedGroup = ref<GroupUserInfo | null>(null)

// 群聊下的所有游戏
const gamesList = ref<RodeoResponse['rodeo'][]>([])

// 添加计算属性用于显示群组名称
const groupNames = computed(() => {
  return groupList.value.map((item) => item.group.groupName)
})

// 群组选择变化
const handleGroupChange = async (e: any) => {
  const index = e.detail.value
  if (index >= 0 && index < groupList.value.length) {
    selectedGroup.value = groupList.value[index]
    gamesList.value = []
    await fetchGamesList(groupList.value[index].group.groupId)
  }
}

// 获取群组列表
const fetchGroupList = async () => {
  try {
    const res = await getAllGroupUser()
    if (res.code === 200 && res.data) {
      groupList.value = res.data as unknown as GroupUserInfo[]
      // 默认选中第一个群组
      if (groupList.value.length > 0) {
        selectedGroup.value = groupList.value[0]
        await fetchGamesList(groupList.value[0].group.groupId)
      }
    }
  } catch (error) {
    console.error('获取群组列表失败:', error)
    uni.showToast({
      title: '获取群组列表失败',
      icon: 'none',
    })
  }
}

// 获取群聊下所有游戏
const fetchGamesList = async (groupId: string) => {
  try {
    // 这里需要后端接口支持：获取某群下所有游戏
    const res = await getGamesByGroupId(groupId)
    gamesList.value = Array.isArray(res.data) ? res.data : []
    return res // 返回响应对象以便调试
  } catch (error) {
    gamesList.value = []
    uni.showToast({ title: '获取游戏列表失败', icon: 'none' })
    return { code: 500, message: '获取游戏列表失败', data: [] } // 返回一个模拟的错误响应
  }
}

// 跳转到新增游戏页面
const goToAddGame = () => {
  if (!selectedGroup.value?.group.groupId) {
    uni.showToast({
      title: '请先选择群组',
      icon: 'none',
    })
    return
  }
  uni.navigateTo({
    url: `/pages/gameTool/addGame?groupId=${selectedGroup.value.group.groupId}`,
  })
}

// 开始游戏
const startGame = async (game) => {
  try {
    await openGame(game.id)
    game.running = 1
    uni.showToast({
      title: '游戏已开始',
      icon: 'success',
    })
  } catch (error) {
    uni.showToast({
      title: '开始游戏失败',
      icon: 'none',
    })
  }
}

// 重新开始游戏
const restartGame = async (game) => {
  try {
    await reGame(game.id)
    uni.showToast({
      title: '游戏已开始',
      icon: 'success',
    })
    // 重新加载游戏列表
    if (selectedGroup.value?.group.groupId) {
      await fetchGamesList(selectedGroup.value.group.groupId)
    }
  } catch (error) {
    uni.showToast({
      title: '开始游戏失败',
      icon: 'none',
    })
  }
}

// 删除游戏
const deleteGame = async (game) => {
  try {
    const stopRes = await stopGame(game.id)
    console.log('stopGame 返回:', stopRes)
    uni.showToast({
      title: '游戏已删除',
      icon: 'success',
    })
    if (selectedGroup.value?.group.groupId) {
      setTimeout(async () => {
        const fetchRes = await fetchGamesList(selectedGroup.value.group.groupId)
        console.log('fetchGamesList 返回:', fetchRes)
        console.log('gamesList after delete:', gamesList.value)
      }, 300)
    }
  } catch (error) {
    uni.showToast({
      title: '删除游戏失败',
      icon: 'none',
    })
  }
}

// 查看游戏记录
const viewRecords = (game) => {
  uni.navigateTo({ url: `/pages/gameTool/gameRecords?gameId=${game.id}` })
}

// 添加格式化时间的函数
const formatForbiddenTime = (seconds: number | string | null | undefined) => {
  // 确保输入是数字
  const secondsNum = Number(seconds)
  if (isNaN(secondsNum)) {
    return '0秒'
  }
  if (secondsNum < 60) {
    return `${secondsNum}秒`
  }
  const minutes = Math.floor(secondsNum / 60)
  const remainingSeconds = secondsNum % 60
  return `${minutes}分${remainingSeconds}秒`
}

// 获取玩家昵称
const getPlayerNickname = (userId: string) => {
  if (!selectedGroup.value) return userId
  const user = selectedGroup.value.groupUserList.find((u) => u.userId === userId)
  return user ? `${user.userNick}(${user.userId})` : userId
}

// 页面加载时获取数据
onMounted(() => {
  fetchGroupList()
})

// 添加页面显示时的生命周期钩子
onShow(() => {
  if (selectedGroup.value?.group.groupId) {
    fetchGamesList(selectedGroup.value.group.groupId)
  }
})
</script>

<style lang="scss" scoped>
// 可以添加需要的样式
</style>

<route lang="json5">
{
  style: {
    navigationStyle: 'default',
    navigationBarTitleText: '游戏记录',
    navigationBarTextStyle: 'white',
    navigationBarBackgroundColor: '#3B82F6',
  },
}
</route>

<template>
  <view class="min-h-screen bg-gray-100 p-4">
    <view class="flex items-center mb-4">
      <button @click="goBack" class="mr-2 bg-gray-200 px-3 py-1 rounded">返回</button>
      <text class="text-lg font-bold">游戏记录</text>
    </view>
    <view v-if="loading" class="text-center text-gray-500 py-8">加载中...</view>
    <view v-else>
      <view v-if="gameRecords.length > 0" class="space-y-4">
        <view
          v-for="record in gameRecords"
          :key="record.id"
          class="bg-white rounded-lg p-4 shadow mb-2"
        >
          <view class="flex justify-between mb-2">
            <text class="text-gray-600">玩家：</text>
            <text class="font-medium">{{ getPlayerNickname(record.player) }}</text>
          </view>
          <view class="flex justify-between mb-2">
            <text class="text-gray-600">禁言时长：</text>
            <text class="font-medium">{{ formatForbiddenTime(record.forbiddenSpeech) }}</text>
          </view>
          <view class="flex justify-between mb-2">
            <text class="text-gray-600">轮次：</text>
            <text class="font-medium">{{ record.turns }}</text>
          </view>
          <view class="flex justify-between">
            <text class="text-gray-600">状态：</text>
            <text :class="record.winFlag ? 'text-green-500' : 'text-red-500'" class="font-medium">
              {{ record.winFlag ? '胜利' : '失败' }}
            </text>
          </view>
        </view>
      </view>
      <view v-else class="text-center text-gray-500 py-8">暂无游戏记录</view>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { ref, onMounted } from 'vue'
import { onLoad } from '@dcloudio/uni-app'
import { getRecordList, getRodeoInfo, getAllGroupUser } from '@/service/game/game'

interface RodeoRecord {
  id: number
  rodeoId: string
  player: string
  turns: number
  rodeoDesc: string
  winFlag: number
  forbiddenSpeech: number
}

interface GroupUser {
  id: string
  groupId: string
  userId: string
  userNick: string
}

const gameId = ref('')
const gameRecords = ref<RodeoRecord[]>([])
const loading = ref(true)
const groupUserList = ref<GroupUser[]>([])

// 获取玩家昵称
const getPlayerNickname = (userId: string) => {
  const user = groupUserList.value.find((u) => u.userId === userId)
  return user ? `${user.userNick}(${user.userId})` : userId
}

// 格式化禁言时长
const formatForbiddenTime = (seconds: number | string | null | undefined) => {
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

// 返回上一页
const goBack = () => {
  uni.navigateBack()
}

// 获取游戏记录和群成员
const fetchData = async () => {
  loading.value = true
  try {
    // 获取游戏记录
    const res = await getRecordList(gameId.value)
    gameRecords.value = (res.data as RodeoRecord[]) || []
    // 获取游戏信息，拿到 groupId
    const gameRes = await getRodeoInfoById(gameId.value)
    const groupId = (gameRes.data as any)?.rodeo?.groupId
    if (groupId) {
      // 获取群成员
      const groupRes = await getAllGroupUser()
      const group = ((groupRes.data as any) || []).find((g: any) => g.group.groupId === groupId)
      groupUserList.value = group ? group.groupUserList : []
    }
  } catch (error) {
    uni.showToast({ title: '获取数据失败', icon: 'none' })
  } finally {
    loading.value = false
  }
}

// 通过gameId获取游戏详情
const getRodeoInfoById = async (id: string) => {
  // 这里假设 getRodeoInfo 支持传id，否则需后端支持
  // 若只支持 groupId，则需前端传递 groupId 进来
  return await getRodeoInfo(id)
}

onLoad((options) => {
  if (options && options.gameId) {
    gameId.value = options.gameId
    fetchData()
  } else {
    uni.showToast({ title: '缺少gameId参数', icon: 'none' })
    loading.value = false
  }
})
</script>

<style lang="scss" scoped></style>

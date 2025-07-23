<template>
  <view class="min-h-screen bg-gray-100 p-4">
    <view class="bg-white rounded-lg p-4 shadow">
      <form @submit.prevent="handleSubmit">
        <!-- 游戏名称 -->
        <view class="mb-4">
          <text class="block text-gray-700 mb-2">游戏名称</text>
          <input
            v-model="formData.venue"
            class="w-full px-3 py-2 border rounded-lg"
            placeholder="请输入游戏名称"
          />
        </view>

        <!-- 日期选择 -->
        <view class="mb-4">
          <text class="block text-gray-700 mb-2">日期</text>
          <picker
            mode="date"
            :value="formData.day"
            @change="onDateChange"
            class="w-full px-3 py-2 border rounded-lg"
          >
            <view class="flex justify-between items-center">
              <text>{{ formData.day || '请选择日期' }}</text>
              <text class="text-gray-400">▼</text>
            </view>
          </picker>
        </view>

        <!-- 时间选择 -->
        <view class="mb-4 flex space-x-4">
          <view class="flex-1">
            <text class="block text-gray-700 mb-2">开始时间</text>
            <picker
              mode="time"
              :value="formData.startTime"
              @change="onStartTimeChange"
              class="w-full px-3 py-2 border rounded-lg"
            >
              <view class="flex justify-between items-center">
                <text>{{ formData.startTime || '请选择' }}</text>
                <text class="text-gray-400">▼</text>
              </view>
            </picker>
          </view>
          <view class="flex-1">
            <text class="block text-gray-700 mb-2">结束时间</text>
            <picker
              mode="time"
              :value="formData.endTime"
              @change="onEndTimeChange"
              class="w-full px-3 py-2 border rounded-lg"
            >
              <view class="flex justify-between items-center">
                <text>{{ formData.endTime || '请选择' }}</text>
                <text class="text-gray-400">▼</text>
              </view>
            </picker>
          </view>
        </view>

        <!-- 群组选择（仅在允许选择时显示） -->
        <view class="mb-4" v-if="canSelectGroup">
          <picker mode="selector" :range="groupNames" @change="handleGroupChange">
            <view class="flex items-center justify-between p-2 border rounded">
              <text class="text-gray-600">选择群组：</text>
              <text class="font-medium">{{ selectedGroup?.group?.groupName || '请选择群组' }}</text>
            </view>
          </picker>
        </view>

        <!-- 是否奖励道具 giveProp属性 -->
        <view class="mb-4 flex items-center">
          <text class="block text-gray-700 mr-4">是否奖励道具</text>
          <switch
            :checked="formData.giveProp"
            @change="(e) => (formData.giveProp = e.detail.value)"
          />
        </view>

        <!-- 道具选择（仅在 giveProp 为 true 时显示） -->
        <view class="mb-4" v-if="formData.giveProp">
          <text class="block text-gray-700 mb-2">选择奖励道具</text>
          <picker
            :range="propCodeList"
            :range-key="'propName'"
            :value="propIndex"
            @change="onPropChange"
            class="w-full px-3 py-2 border rounded-lg"
          >
            <view class="flex justify-between items-center">
              <text>{{ formData.propName || '请选择道具' }}</text>
              <text class="text-gray-400">▼</text>
            </view>
          </picker>
        </view>

        <!-- 玩家选择 -->
        <view class="mb-4">
          <text class="block text-gray-700 mb-2">选择游戏玩家</text>

          <!-- 搜索框 -->
          <view class="mb-3 relative">
            <input
              v-model="searchKeyword"
              placeholder="搜索玩家昵称..."
              class="w-full px-3 py-2 pr-8 border rounded-lg"
              @input="handleSearch"
            />
            <button
              v-if="searchKeyword"
              @click="clearSearch"
              @tap="clearSearch"
              class="absolute right-2 top-1/2 transform -translate-y-1/2 text-gray-400 text-lg w-6 h-6 flex items-center justify-center bg-white rounded-full border border-gray-300"
              style="z-index: 10"
            >
              ×
            </button>
          </view>

          <!-- 快速选择按钮 -->
          <view class="mb-3 flex gap-2">
            <button
              @click="selectAllPlayers"
              class="px-3 py-1 bg-blue-100 text-blue-600 rounded text-sm"
            >
              全选
            </button>
            <button
              @click="clearAllPlayers"
              class="px-3 py-1 bg-gray-100 text-gray-600 rounded text-sm"
            >
              清空
            </button>
            <button
              @click="selectSearchResults"
              v-if="filteredPlayers.length > 0"
              class="px-3 py-1 bg-green-100 text-green-600 rounded text-sm"
            >
              选择搜索结果({{ filteredPlayers.length }})
            </button>
          </view>

          <view class="border rounded-lg p-2 max-h-60 overflow-y-auto">
            <checkbox-group @change="handlePlayerChange">
              <view class="flex flex-wrap gap-2">
                <view
                  v-for="user in displayPlayers"
                  :key="user.userId"
                  class="flex items-center bg-gray-50 px-2 py-1 rounded"
                >
                  <checkbox :value="user.userId" :checked="selectedPlayers.includes(user.userId)" />
                  <text class="ml-1 text-sm">{{ user.userNick }}({{ user.userId }})</text>
                </view>
              </view>
            </checkbox-group>
          </view>

          <view v-if="selectedPlayers.length > 0" class="mt-2">
            <text class="text-gray-600">已选择 {{ selectedPlayers.length }} 名玩家</text>

            <!-- 已选择的玩家列表 -->
            <view class="mt-2 p-2 bg-blue-50 rounded border">
              <text class="text-sm text-gray-700 mb-1 block">已选择的玩家：</text>
              <view class="flex flex-wrap gap-1">
                <view
                  v-for="userId in selectedPlayers"
                  :key="userId"
                  class="flex items-center bg-blue-100 px-2 py-1 rounded text-xs"
                >
                  <text>{{ getUserNickById(userId) }}({{ userId }})</text>
                  <text @click="removePlayer(userId)" class="ml-1 text-red-500 cursor-pointer">
                    ×
                  </text>
                </view>
              </view>
            </view>
          </view>
        </view>

        <!-- 游戏方式 -->
        <view class="mb-4">
          <text class="block text-gray-700 mb-2">游戏方式</text>
          <picker
            :range="playingMethods"
            :value="playingMethodIndex"
            @change="onPlayingMethodChange"
            class="w-full px-3 py-2 border rounded-lg"
          >
            <view class="flex justify-between items-center">
              <text>{{ formData.playingMethod || '请选择游戏方式' }}</text>
              <text class="text-gray-400">▼</text>
            </view>
          </picker>
        </view>

        <!-- 轮次选择（仅在决斗模式下显示） -->
        <view v-if="formData.playingMethod === '决斗'" class="mb-4">
          <text class="block text-gray-700 mb-2">游戏轮次</text>
          <picker
            :range="roundOptions"
            :value="roundIndex"
            @change="onRoundChange"
            class="w-full px-3 py-2 border rounded-lg"
          >
            <view class="flex justify-between items-center">
              <text>{{ formData.round || '请选择轮次' }}</text>
              <text class="text-gray-400">▼</text>
            </view>
          </picker>
        </view>

        <!-- 提交按钮 -->
        <button @click="handleSubmit" class="w-full bg-blue-500 text-white py-2 rounded-lg">
          创建游戏
        </button>
      </form>
    </view>
  </view>
</template>

<script lang="ts" setup>
import { getAllGroupUser, addRodeo } from '@/service/game/game'
import { ref, onMounted, computed, nextTick } from 'vue'
import { onLoad, onShow } from '@dcloudio/uni-app'

// 在 script 部分添加接口定义
interface GroupUserInfo {
  group: {
    groupId: string
    groupName: string
  }
  groupUserList: Array<{
    id: string
    groupId: string
    userId: string
    userNick: string
  }>
}

// 表单数据
const formData = ref({
  venue: '',
  day: '',
  startTime: '',
  endTime: '',
  groupId: '',
  players: '',
  playingMethod: '',
  round: 3,
  giveProp: false,
  propCode: '',
  propName: '',
})

const propCodeList = ref([{ propCode: 'FISH-99', propName: '吉玉头筹' }])

// 群组列表
const groupList = ref<GroupUserInfo[]>([])
// 选中的群组
const selectedGroup = ref<GroupUserInfo | null>(null)
// 选中的玩家
const selectedPlayers = ref<string[]>([])
// 游戏描述
const gameDesc = ref('')

// 搜索相关
const searchKeyword = ref('')
const filteredPlayers = ref<Array<{ userId: string; userNick: string }>>([])

// 玩家列表
const playerList = ref<Array<{ userId: string; userNick: string }>>([])

// 游戏方式
const playingMethods = ['大乱斗', '决斗', '轮盘']
const playingMethodIndex = ref(0)

// 轮次选项（奇数）
const roundOptions = [1, 3, 5, 7, 9]
const roundIndex = ref(1)

// 新增：是否允许选择群组
const canSelectGroup = ref(true)
// 新增：路由参数 groupId
const routeGroupId = ref<string | undefined>()

// 当前群组的玩家列表
const groupUserList = computed(() => {
  return selectedGroup.value?.groupUserList || []
})

// 显示的玩家列表（支持搜索）
const displayPlayers = computed(() => {
  if (searchKeyword.value.trim() === '') {
    return groupUserList.value
  }
  return filteredPlayers.value
})

// 搜索处理函数
const handleSearch = () => {
  const keyword = searchKeyword.value.trim().toLowerCase()
  if (keyword === '') {
    filteredPlayers.value = []
    return
  }

  // 搜索时保持之前选择的玩家状态，只过滤显示结果
  filteredPlayers.value = groupUserList.value.filter(
    (user) =>
      user.userNick.toLowerCase().includes(keyword) || user.userId.toLowerCase().includes(keyword),
  )
}

// 清空搜索
const clearSearch = () => {
  console.log('clearSearch 被调用，当前 searchKeyword:', searchKeyword.value)

  // 强制清空搜索关键词
  searchKeyword.value = ''

  // 清空搜索结果
  filteredPlayers.value = []

  // 强制触发响应式更新
  nextTick(() => {
    console.log('清空后 searchKeyword:', searchKeyword.value)
    console.log('清空后 filteredPlayers:', filteredPlayers.value)
  })

  // 显示提示
  uni.showToast({
    title: '搜索已清空',
    icon: 'none',
    duration: 1000,
  })
}

// 全选玩家
const selectAllPlayers = () => {
  const allUserIds = displayPlayers.value.map((user) => user.userId)
  // 累积选择，不重复添加
  selectedPlayers.value = Array.from(new Set([...selectedPlayers.value, ...allUserIds]))
}

// 清空选择
const clearAllPlayers = () => {
  selectedPlayers.value = []
}

// 选择搜索结果
const selectSearchResults = () => {
  const searchUserIds = filteredPlayers.value.map((user) => user.userId)
  // 累积选择搜索结果，不重复添加
  selectedPlayers.value = Array.from(new Set([...selectedPlayers.value, ...searchUserIds]))
}

// 根据用户ID获取用户昵称
const getUserNickById = (userId: string) => {
  const user = groupUserList.value.find((u) => u.userId === userId)
  return user ? user.userNick : userId
}

// 移除单个玩家
const removePlayer = (userId: string) => {
  selectedPlayers.value = selectedPlayers.value.filter((id) => id !== userId)
}

// 获取群组列表
const fetchGroupList = async () => {
  try {
    const res = await getAllGroupUser()
    if (res.code === 200 && res.data) {
      groupList.value = res.data as unknown as GroupUserInfo[]
      // 如果有 groupId 参数，自动选中该群组
      if (routeGroupId.value) {
        const target = groupList.value.find((g) => g.group.groupId === routeGroupId.value)
        if (target) {
          selectedGroup.value = target
        } else if (groupList.value.length > 0) {
          selectedGroup.value = groupList.value[0]
        }
      } else if (groupList.value.length > 0) {
        selectedGroup.value = groupList.value[0]
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

// 群组选择变化
const handleGroupChange = (e: any) => {
  const index = e.detail.value
  if (index >= 0 && index < groupList.value.length) {
    selectedGroup.value = groupList.value[index]
    selectedPlayers.value = []
  }
}

// 玩家选择变化
const handlePlayerChange = (e: any) => {
  // 获取当前显示的玩家ID列表
  const currentDisplayUserIds = displayPlayers.value.map((user) => user.userId)

  // 获取当前选中的玩家ID列表（来自checkbox-group）
  const currentSelectedUserIds = e.detail.value

  // 保留不在当前显示列表中的已选择玩家
  const preservedSelectedPlayers = selectedPlayers.value.filter(
    (userId) => !currentDisplayUserIds.includes(userId),
  )

  // 合并保留的玩家和当前选中的玩家
  selectedPlayers.value = [...preservedSelectedPlayers, ...currentSelectedUserIds]

  console.log('当前显示玩家:', currentDisplayUserIds)
  console.log('当前选中玩家:', currentSelectedUserIds)
  console.log('保留的玩家:', preservedSelectedPlayers)
  console.log('最终选择列表:', selectedPlayers.value)
}

// 获取玩家数据
const fetchPlayerData = async (groupId: string) => {
  try {
    if (!groupId) {
      uni.showToast({
        title: '未获取到群组信息',
        icon: 'none',
      })
      return
    }

    formData.value.groupId = groupId
    const res = await getAllGroupUser()
    const data = res.data as GroupUserInfo[]
    const currentGroup = data.find((item) => item.group.groupId === groupId)

    if (currentGroup) {
      playerList.value = currentGroup.groupUserList || []
    }
  } catch (error) {
    uni.showToast({
      title: '获取数据失败',
      icon: 'none',
    })
  }
}

const props = defineProps<{
  groupId?: string
}>()

// 页面加载时获取参数
onMounted(() => {
  if (props.groupId) {
    fetchPlayerData(props.groupId)
  }
})

// 事件处理函数
const onDateChange = (e) => {
  formData.value.day = e.detail.value
}

const onStartTimeChange = (e) => {
  formData.value.startTime = e.detail.value
}

const onEndTimeChange = (e) => {
  formData.value.endTime = e.detail.value
}

const onPlayingMethodChange = (e) => {
  const index = e.detail.value
  formData.value.playingMethod = playingMethods[index]
  playingMethodIndex.value = index
  // 如果不是决斗模式，重置轮次
  if (formData.value.playingMethod !== '决斗') {
    formData.value.round = 3
  }
}

const onRoundChange = (e) => {
  const index = e.detail.value
  formData.value.round = roundOptions[index]
  roundIndex.value = index
}

// 当前选中的道具索引
const propIndex = ref(0)

// 选择道具时同步 propCode/propName
const onPropChange = (e) => {
  const index = e.detail.value
  propIndex.value = index
  formData.value.propCode = propCodeList.value[index].propCode
  formData.value.propName = propCodeList.value[index].propName
}

// 提交表单
const handleSubmit = async () => {
  try {
    // 表单验证
    if (!formData.value.venue) {
      uni.showToast({
        title: '请输入游戏名称',
        icon: 'none',
      })
      return
    }
    if (!formData.value.day || !formData.value.startTime || !formData.value.endTime) {
      uni.showToast({
        title: '请选择日期和时间',
        icon: 'none',
      })
      return
    }
    if (selectedPlayers.value.length === 0) {
      uni.showToast({
        title: '请选择参与玩家',
        icon: 'none',
      })
      return
    }
    if (!formData.value.playingMethod) {
      uni.showToast({
        title: '请选择游戏方式',
        icon: 'none',
      })
      return
    }
    if (formData.value.giveProp && !formData.value.propCode) {
      uni.showToast({
        title: '请选择奖励道具',
        icon: 'none',
      })
      return
    }

    // 将选中的玩家数据同步到formData
    formData.value.players = selectedPlayers.value.join(',')
    formData.value.groupId = selectedGroup.value?.group.groupId || ''

    // 构建请求参数
    const requestData = {
      venue: formData.value.venue,
      day: formData.value.day,
      startTime: formData.value.startTime,
      endTime: formData.value.endTime,
      groupId: formData.value.groupId,
      players: formData.value.players,
      playingMethod: formData.value.playingMethod,
      round: formData.value.round,
      gameDesc: gameDesc.value || '',
      giveProp: formData.value.giveProp,
      propCode: formData.value.giveProp ? formData.value.propCode : '',
      propName: formData.value.giveProp ? formData.value.propName : '',
    }

    console.log('提交的游戏数据:', requestData)

    // 调用创建游戏接口
    const res = await addRodeo(requestData)

    if (res.code === 200) {
      uni.showToast({
        title: '创建成功',
        icon: 'success',
      })
      setTimeout(() => {
        uni.redirectTo({
          url: `/pages/gameTool/gameTool?groupId=${formData.value.groupId}`,
        })
      }, 1500)
    } else {
      uni.showToast({
        title: '创建失败',
        icon: 'none',
      })
    }
  } catch (error) {
    console.error('创建游戏失败:', error)
    uni.showToast({
      title: '创建失败',
      icon: 'none',
    })
  }
}

// 页面加载时获取群组列表
fetchGroupList()

// 新增一个 computed 属性，提取群名数组
const groupNames = computed(() => groupList.value.map((item) => item.group.groupName))

onLoad((options) => {
  if (options && options.groupId) {
    routeGroupId.value = options.groupId
    canSelectGroup.value = false
  } else {
    canSelectGroup.value = true
  }
})

onShow(() => {
  if (selectedGroup.value?.group.groupId) {
    fetchPlayerData(selectedGroup.value.group.groupId)
  }
})
</script>

<style lang="scss" scoped>
.picker {
  background-color: #fff;
}
</style>

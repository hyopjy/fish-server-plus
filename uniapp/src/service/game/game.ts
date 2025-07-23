// src/service/game/game.ts
import { http } from '@/utils/http'

// 获取群组成员
export const getAllGroupUser = () => {
  return http.get('/fish-server/all-group-user')
}

// 获取当前比赛信息
export const getRodeoInfo = (rodeoId: string) => {
  return http.get(`/fish-server/rodeo/query?rodeoId=${rodeoId}`)
}

// 新增游戏
export const addRodeo = (data: any) => {
  return http.post('/fish-server/rodeo/add', data)
}

// 开始游戏
export const openGame = (rodeoId: string) => {
  return http.post(`/fish-server/rodeo/open-game?rodeoId=${rodeoId}`)
}

// 停止游戏
export const stopGame = (rodeoId: string) => {
  return http.post(`/fish-server/rodeo/stop-game?rodeoId=${rodeoId}`)
}

// 重新开始游戏
export const reGame = (rodeoId: string) => {
  return http.post(`/fish-server/rodeo/restart-game?rodeoId=${rodeoId}`)
}

// 获取记录列表
export const getRecordList = (rodeoId: string) => {
  return http.post(`/fish-server/record/list?rodeoId=${rodeoId}`)
}

export const getGamesByGroupId = (groupId: string) => {
  return http.get(`/fish-server/rodeo/list?groupId=${groupId}`)
}

import { defineUniPages } from '@uni-helper/vite-plugin-uni-pages'

export default defineUniPages({
  globalStyle: {
    navigationStyle: 'default',
    navigationBarTitleText: '首页',
    navigationBarBackgroundColor: '#3B82F6',
    navigationBarTextStyle: 'white',
    backgroundColor: '#FFFFFF',
  },
  easycom: {
    autoscan: true,
    custom: {
      '^wd-(.*)': 'wot-design-uni/components/wd-$1/wd-$1.vue',
      '^(?!z-paging-refresh|z-paging-load-more)z-paging(.*)':
        'z-paging/components/z-paging$1/z-paging$1.vue',
    },
  },
  tabBar: {
    color: '#999999',
    selectedColor: '#3B82F6',
    backgroundColor: '#FFFFFF',
    borderStyle: 'white',
    height: '50px',
    fontSize: '10px',
    iconWidth: '24px',
    spacing: '3px',
    list: [
      {
        iconPath: 'static/tabbar/home.png',
        selectedIconPath: 'static/tabbar/homeHL.png',
        pagePath: 'pages/tool/index',
        text: '游戏管理',
      },
      {
        iconPath: 'static/tabbar/home.png',
        selectedIconPath: 'static/tabbar/homeHL.png',
        pagePath: 'pages/index/index',
        text: '去水印',
      },
      // {
      //   iconPath: 'static/tabbar/tools.png',
      //   selectedIconPath: 'static/tabbar/toolsHL.png',
      //   pagePath: 'pages/gameTool/gameTool',
      //   text: '游戏管理',
      // },
      // {
      //   iconPath: 'static/tabbar/personal.png',
      //   selectedIconPath: 'static/tabbar/personalHL.png',
      //   pagePath: 'pages/user/user',
      //   text: '我的',
      // },
    ],
  },
  pages: [
    {
      path: 'pages/gameTool/gameTool',
      style: {
        navigationBarTitleText: '游戏管理',
        navigationBarBackgroundColor: '#3B82F6',
        navigationBarTextStyle: 'white',
      },
    },
    {
      path: 'pages/gameTool/addGame',
      style: {
        navigationBarTitleText: '新增游戏',
        navigationBarBackgroundColor: '#3B82F6',
        navigationBarTextStyle: 'white',
      },
    },
    {
      path: 'pages/index/index',
      style: {
        navigationBarTitleText: '首页',
      },
    },
    {
      path: 'pages/user/user',
      style: {
        navigationBarTitleText: '我的',
      },
    },
  ],
})

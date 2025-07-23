const puppeteer = require('puppeteer')
const axios = require('axios')
const fs = require('fs-extra')
const path = require('path')
const { URL } = require('url')

const TARGET_URL = 'https://flowus.cn/gkcsl/share/03feab04-e594-4d92-a475-9ab7cb841a6b'
const OUTPUT_HTML = 'D:/aaa/flowus_doc.html'
const IMG_DIR = 'D:/aaa/resources/images'
const RESOURCE_DIR = 'D:/aaa/resources'

async function downloadFile(fileUrl, outputPath) {
  try {
    const response = await axios.get(fileUrl, { responseType: 'arraybuffer' })
    await fs.outputFile(outputPath, response.data)
    return true
  } catch (e) {
    console.error('下载失败:', fileUrl, e.message)
    return false
  }
}

function getLocalResourcePath(resourceUrl, type) {
  try {
    const urlObj = new URL(resourceUrl)
    let ext = path.extname(urlObj.pathname)
    if (!ext && type === 'img') ext = '.png'
    let filename = path.basename(urlObj.pathname).split('?')[0]
    if (!filename) filename = Date.now() + ext
    if (type === 'img') {
      return `./resources/images/${filename}`
    } else {
      return `./resources/${filename}`
    }
  } catch {
    return resourceUrl
  }
}

async function main() {
  await fs.ensureDir(IMG_DIR)
  await fs.ensureDir(RESOURCE_DIR)

  const browser = await puppeteer.launch({ headless: 'new' })
  const page = await browser.newPage()
  await page.goto(TARGET_URL, { waitUntil: 'networkidle2', timeout: 30000 })

  // 模拟滚动到底部5次
  for (let i = 0; i < 5; i++) {
    await page.evaluate(() => window.scrollTo(0, document.body.scrollHeight))
    await new Promise((r) => setTimeout(r, 2000))
  }

  // 获取完整HTML
  let html = await page.content()

  // 处理图片
  const imgUrls = Array.from(html.matchAll(/<img[^>]+src="([^"]+)"/g)).map((m) => m[1])
  for (const imgUrl of imgUrls) {
    if (!imgUrl.startsWith('http')) continue
    const localPath = getLocalResourcePath(imgUrl, 'img')
    const absPath = path.resolve(IMG_DIR, path.basename(localPath))
    await downloadFile(imgUrl, absPath)
    html = html.replace(new RegExp(imgUrl.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'g'), localPath)
  }

  // 处理CSS/JS
  const resourceUrls = Array.from(html.matchAll(/<(link|script)[^>]+(?:href|src)="([^"]+)"/g)).map(
    (m) => m[2],
  )
  for (const resUrl of resourceUrls) {
    if (!resUrl.startsWith('http')) continue
    const localPath = getLocalResourcePath(resUrl, 'res')
    const absPath = path.resolve(RESOURCE_DIR, path.basename(localPath))
    await downloadFile(resUrl, absPath)
    html = html.replace(new RegExp(resUrl.replace(/[.*+?^${}()|[\]\\]/g, '\\$&'), 'g'), localPath)
  }

  // 替换FlowUs在线地址为本地路径
  html = html.replace(/https:\/\/flowus\.cn\/gkcsl/g, './resources')

  // 替换所有超链接为本地文件
  html = html.replace(/<a\s+([^>]*?)href="([^"]+)"([^>]*)>/g, (match, pre, href, post) => {
    if (href.startsWith('http')) {
      return `<a ${pre}href="./resources"${post}>`
    }
    return match
  })

  // 保存HTML
  await fs.outputFile(OUTPUT_HTML, html, 'utf8')
  await browser.close()
  console.log('全部完成！HTML和资源已保存到 D:/aaa')
}

main()

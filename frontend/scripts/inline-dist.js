import { readFileSync, writeFileSync } from 'node:fs'
import { join } from 'node:path'

const dist = join(process.cwd(), 'dist')
const indexPath = join(dist, 'index.html')
let html = readFileSync(indexPath, 'utf8')

html = html.replace(
  /<link rel="stylesheet" crossorigin href="([^"]+)">/g,
  (_, href) => {
    const css = readFileSync(join(dist, href.replace(/^\//, '')), 'utf8')
    return `<style>${css}</style>`
  },
)

html = html.replace(
  /<script type="module" crossorigin src="([^"]+)"><\/script>/g,
  (_, src) => {
    const js = readFileSync(join(dist, src.replace(/^\//, '')), 'utf8')
    return `<script type="module">${js}</script>`
  },
)

writeFileSync(indexPath, html)

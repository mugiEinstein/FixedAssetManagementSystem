/**
 * 前端导出 Excel 工具
 * 使用纯前端方式导出表格数据为 Excel 文件
 */

// 将数据导出为 CSV 文件（兼容 Excel）
export function exportToExcel(data: any[], columns: { prop: string; label: string }[], filename: string = '导出数据') {
    if (!data || data.length === 0) {
        console.warn('没有可导出的数据')
        return
    }

    // 构建 CSV 内容
    const header = columns.map(col => col.label).join(',')
    const rows = data.map(row => {
        return columns.map(col => {
            let value = row[col.prop]
            // 处理特殊字符
            if (value === null || value === undefined) {
                value = ''
            }
            // 如果包含逗号、换行符或双引号，需要用双引号包裹
            if (typeof value === 'string' && (value.includes(',') || value.includes('\n') || value.includes('"'))) {
                value = `"${value.replace(/"/g, '""')}"`
            }
            return value
        }).join(',')
    }).join('\n')

    const csvContent = '\uFEFF' + header + '\n' + rows // 添加 BOM 以支持中文

    // 创建 Blob 并下载
    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${filename}.csv`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
}

// 解析导入的 CSV/Excel 文件
export function parseImportFile(file: File): Promise<any[]> {
    return new Promise((resolve, reject) => {
        const reader = new FileReader()
        
        reader.onload = (e) => {
            try {
                const content = e.target?.result as string
                const lines = content.split('\n').filter(line => line.trim())
                
                if (lines.length < 2) {
                    reject(new Error('文件内容为空或格式不正确'))
                    return
                }

                // 解析表头
                const headers = parseCSVLine(lines[0])
                
                // 解析数据行
                const data = []
                for (let i = 1; i < lines.length; i++) {
                    const values = parseCSVLine(lines[i])
                    if (values.length === headers.length) {
                        const row: Record<string, string> = {}
                        headers.forEach((header, index) => {
                            row[header] = values[index]
                        })
                        data.push(row)
                    }
                }

                resolve(data)
            } catch (error) {
                reject(error)
            }
        }

        reader.onerror = () => reject(new Error('文件读取失败'))
        reader.readAsText(file, 'UTF-8')
    })
}

// 解析 CSV 行（处理引号内的逗号）
function parseCSVLine(line: string): string[] {
    const result: string[] = []
    let current = ''
    let inQuotes = false

    for (let i = 0; i < line.length; i++) {
        const char = line[i]
        
        if (char === '"') {
            if (inQuotes && line[i + 1] === '"') {
                current += '"'
                i++
            } else {
                inQuotes = !inQuotes
            }
        } else if (char === ',' && !inQuotes) {
            result.push(current.trim())
            current = ''
        } else {
            current += char
        }
    }
    
    result.push(current.trim())
    return result
}

// 生成导入模板
export function downloadTemplate(columns: { prop: string; label: string }[], filename: string = '导入模板') {
    const header = columns.map(col => col.label).join(',')
    const csvContent = '\uFEFF' + header + '\n'

    const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' })
    const url = window.URL.createObjectURL(blob)
    const link = document.createElement('a')
    link.href = url
    link.download = `${filename}.csv`
    document.body.appendChild(link)
    link.click()
    document.body.removeChild(link)
    window.URL.revokeObjectURL(url)
}

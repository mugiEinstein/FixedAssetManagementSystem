import { defineStore } from 'pinia'
import { ref } from 'vue'

export const useAppStore = defineStore('app', () => {
    // 侧边栏折叠状态
    const sidebarCollapsed = ref(false)

    // 深色模式
    const isDarkMode = ref(localStorage.getItem('darkMode') === 'true')

    // 当前激活的菜单
    const activeMenu = ref('')

    // 面包屑
    const breadcrumbs = ref<{ title: string; path?: string }[]>([])

    // 切换侧边栏
    function toggleSidebar() {
        sidebarCollapsed.value = !sidebarCollapsed.value
    }

    // 设置激活菜单
    function setActiveMenu(menu: string) {
        activeMenu.value = menu
    }

    // 设置面包屑
    function setBreadcrumbs(items: { title: string; path?: string }[]) {
        breadcrumbs.value = items
    }

    // 切换深色模式
    function toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
        localStorage.setItem('darkMode', isDarkMode.value.toString())
        applyDarkMode()
    }

    // 应用深色模式
    function applyDarkMode() {
        if (typeof document !== 'undefined') {
            if (isDarkMode.value) {
                document.documentElement.classList.add('dark')
            } else {
                document.documentElement.classList.remove('dark')
            }
        }
    }

    // 初始化深色模式
    function initDarkMode() {
        applyDarkMode()
    }

    // 初始化时应用主题
    initDarkMode()

    return {
        sidebarCollapsed,
        isDarkMode,
        activeMenu,
        breadcrumbs,
        toggleSidebar,
        toggleDarkMode,
        setActiveMenu,
        setBreadcrumbs
    }
})
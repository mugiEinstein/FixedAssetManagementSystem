// 费用计算工具

/**
 * 器材外借押金计算
 * 押金 = Max(器材原值 × 10%, 1000)
 */
export function calculateEquipmentDeposit(originalValue: number): number {
    return Math.max(originalValue * 0.1, 1000)
}

/**
 * 器材租金计算
 * 租金 = 每日租金 × 租借天数
 */
export function calculateEquipmentRent(dailyRent: number, days: number): number {
    return dailyRent * days
}

/**
 * 校外借用违约金计算
 * 违约金 = 1. 5 × 每日租金 × 违约天数
 */
export function calculateExternalPenalty(dailyRent: number, overdueDays: number): number {
    return 1.5 * dailyRent * overdueDays
}

/**
 * 校内借用违约金计算
 * 违约金 = 1.2 × 每日租金 × 违约天数
 */
export function calculateInternalPenalty(dailyRent: number, overdueDays: number): number {
    return 1.2 * dailyRent * overdueDays
}

/**
 * 办公用房超面积费用计算
 * 超面积费用 = (实际面积 - 基准最大面积) × 50元/㎡/月 × 月数
 */
export function calculateExcessAreaFee(
    actualArea: number,
    baseMaxArea: number,
    months: number = 1
): number {
    const excessArea = Math.max(actualArea - baseMaxArea, 0)
    return excessArea * 50 * months
}

/**
 * 教职工住宿租金计算
 * 租金 = 面积(㎡) × 90元/㎡
 */
export function calculateStaffHousingRent(area: number): number {
    return area * 90
}

/**
 * 校外活动安保费计算
 * 安保费 = 向上取整(观众人数 / 100) × 200
 */
export function calculateSecurityFee(attendees: number): number {
    return Math. ceil(attendees / 100) * 200
}

/**
 * 校内活动费用计算（60%优惠）
 * 费用 = 场地单价 × 时长 × 0.6
 */
export function calculateInternalActivityFee(
    hourlyRate: number,
    hours: number
): number {
    return hourlyRate * hours * 0.6
}

/**
 * 年折旧额计算（年限平均法）
 * 年折旧额 = (原值 - 预计净残值) / 折旧年限
 * 预计净残值率默认为 5%
 */
export function calculateAnnualDepreciation(
    originalValue: number,
    residualRate: number = 0.05,
    depreciationYears: number
): number {
    const residualValue = originalValue * residualRate
    return (originalValue - residualValue) / depreciationYears
}

/**
 * 资产净值计算
 */
export function calculateNetValue(
    originalValue: number,
    annualDepreciation: number,
    usedYears: number
): number {
    const accumulatedDepreciation = annualDepreciation * usedYears
    const residualValue = originalValue * 0.05
    return Math. max(originalValue - accumulatedDepreciation, residualValue)
}

/**
 * 根据职称获取基准面积范围
 */
export function getBaseAreaByLevel(staffLevel: string): { min: number; max: number } {
    const areaMap: Record<string, { min: number; max: number }> = {
        'professor': { min: 15, max: 20 },       // 教授
        'associate_prof': { min: 10, max: 15 },  // 副教授
        'lecturer': { min: 5, max: 10 },         // 讲师
        'school_leader': { min: 25, max: 30 },   // 校级正职
        'school_deputy': { min: 20, max: 25 },   // 校级副职
        'dept_leader': { min: 15, max: 20 },     // 处级正职
        'dept_deputy': { min: 10, max: 15 },     // 处级副职
        'staff': { min: 5, max: 10 },            // 其他人员
    }
    return areaMap[staffLevel] || { min: 5, max: 10 }
}

/**
 * 格式化金额显示
 */
export function formatMoney(amount: number): string {
    return `¥${amount.toFixed(2).replace(/\B(?=(\d{3})+(?!\d))/g, ',')}`
}
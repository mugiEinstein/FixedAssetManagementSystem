// 用户相关类型定义
export interface LoginParams {
    username: string
    password: string
    role?: string
}

export interface UserInfo {
    id: string
    username: string
    realName: string
    role: UserRole
    department: string
    title?: string
    token: string
}

export type UserRole =
    | 'system_admin'      // 系统管理员
    | 'student'           // 学生
    | 'teacher'           // 教师
    | 'equip_manager'     // 器材管理员
    | 'room_manager'      // 房产管理员
    | 'vehicle_manager'   // 车辆管理员
    | 'finance_manager'   // 财务管理员
    | 'maintenance_staff' // 维保人员
    | 'logistics_admin'   // 后勤管理员(车辆管理员)
    | 'security_admin'    // 安保主管

// 职称等级
export type StaffLevel =
    | 'professor'         // 教授 15-20㎡
    | 'associate_prof'    // 副教授 10-15㎡
    | 'lecturer'          // 讲师 5-10㎡
    | 'school_leader'     // 校级正职 25-30㎡
    | 'school_deputy'     // 校级副职 20-25㎡
    | 'dept_leader'       // 处级正职 15-20㎡
    | 'dept_deputy'       // 处级副职 10-15㎡
    | 'staff'             // 其他人员 5-10㎡
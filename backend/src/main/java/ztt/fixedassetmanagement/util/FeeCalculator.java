package ztt.fixedassetmanagement.util;

import java.math. BigDecimal;
import java.math.RoundingMode;
import java. util.HashMap;
import java. util.Map;

public class FeeCalculator {

    // 职称基准面积配置
    private static final Map<String, BigDecimal[]> STAFF_LEVEL_AREA = new HashMap<>();

    static {
        STAFF_LEVEL_AREA.put("professor", new BigDecimal[]{new BigDecimal("15"), new BigDecimal("20")});
        STAFF_LEVEL_AREA.put("associate_prof", new BigDecimal[]{new BigDecimal("10"), new BigDecimal("15")});
        STAFF_LEVEL_AREA.put("lecturer", new BigDecimal[]{new BigDecimal("5"), new BigDecimal("10")});
        STAFF_LEVEL_AREA.put("principal", new BigDecimal[]{new BigDecimal("25"), new BigDecimal("30")});
        STAFF_LEVEL_AREA. put("vice_principal", new BigDecimal[]{new BigDecimal("20"), new BigDecimal("25")});
        STAFF_LEVEL_AREA. put("director", new BigDecimal[]{new BigDecimal("15"), new BigDecimal("20")});
        STAFF_LEVEL_AREA.put("vice_director", new BigDecimal[]{new BigDecimal("10"), new BigDecimal("15")});
        STAFF_LEVEL_AREA. put("staff", new BigDecimal[]{new BigDecimal("5"), new BigDecimal("10")});
    }

    /**
     * 计算器材外借押金：Max(原值×10%, 1000)
     */
    public static BigDecimal calculateEquipmentDeposit(BigDecimal originalValue) {
        BigDecimal tenPercent = originalValue. multiply(new BigDecimal("0.1"));
        return tenPercent.max(new BigDecimal("1000")).setScale(2, RoundingMode. HALF_UP);
    }

    /**
     * 计算器材租金：每日租金 × 天数
     */
    public static BigDecimal calculateEquipmentRent(BigDecimal dailyRent, int days) {
        return dailyRent.multiply(new BigDecimal(days)). setScale(2, RoundingMode. HALF_UP);
    }

    /**
     * 计算校外借用违约金：1. 5 × 每日租金 × 违约天数
     */
    public static BigDecimal calculateExternalPenalty(BigDecimal dailyRent, int overdueDays) {
        return dailyRent.multiply(new BigDecimal("1.5"))
                .multiply(new BigDecimal(overdueDays))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算校内借用违约金：1.2 × 每日租金 × 违约天数
     */
    public static BigDecimal calculateInternalPenalty(BigDecimal dailyRent, int overdueDays) {
        return dailyRent.multiply(new BigDecimal("1.2"))
                .multiply(new BigDecimal(overdueDays))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取基准面积范围
     */
    public static BigDecimal[] getBaseAreaRange(String staffLevel) {
        return STAFF_LEVEL_AREA. getOrDefault(staffLevel,
                new BigDecimal[]{new BigDecimal("5"), new BigDecimal("10")});
    }

    /**
     * 计算办公用房超面积费用：超面积 × 50元/㎡/月 × 月数
     */
    public static BigDecimal calculateOfficeExcessFee(BigDecimal allocatedArea, BigDecimal baseAreaMax, int months) {
        BigDecimal excessArea = allocatedArea.subtract(baseAreaMax). max(BigDecimal. ZERO);
        return excessArea.multiply(new BigDecimal("50"))
                . multiply(new BigDecimal(months))
                .setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 计算教职工住宿月租金：面积 × 90元/㎡
     */
    public static BigDecimal calculateStaffHousingRent(BigDecimal area) {
        return area.multiply(new BigDecimal("90")). setScale(2, RoundingMode. HALF_UP);
    }

    /**
     * 计算校内活动费用（60%优惠）
     */
    public static BigDecimal calculateInternalActivityFee(BigDecimal originalRate, BigDecimal duration) {
        return originalRate.multiply(new BigDecimal("0.6"))
                .multiply(duration)
                .setScale(2, RoundingMode. HALF_UP);
    }

    /**
     * 计算年折旧额：(原值 - 原值×残值率) ÷ 折旧年限
     */
    public static BigDecimal calculateAnnualDepreciation(BigDecimal originalValue,
                                                         BigDecimal residualRate,
                                                         int depreciationYears) {
        BigDecimal residualValue = originalValue.multiply(residualRate);
        return originalValue.subtract(residualValue)
                .divide(new BigDecimal(depreciationYears), 2, RoundingMode.HALF_UP);
    }
}
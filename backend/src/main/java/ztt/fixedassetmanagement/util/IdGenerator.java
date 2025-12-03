package ztt.fixedassetmanagement.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class IdGenerator {

    private static final AtomicInteger counter = new AtomicInteger(0);
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static String generateId(String prefix) {
        String timestamp = LocalDateTime.now().format(formatter);
        int seq = counter.incrementAndGet() % 10000;
        return String.format("%s%s%04d", prefix, timestamp, seq);
    }

    public static String generateBorrowId() {
        return generateId("JY");
    }

    public static String generateMaintenanceId() {
        return generateId("WB");
    }

    public static String generateFeeId() {
        return generateId("FE");
    }

    public static String generateAllocationId() {
        return generateId("AL");
    }

    public static String generateEventId() {
        return generateId("EV");
    }

    public static String generatePlanId() {
        return generateId("PL");
    }

    public static String generateBuildingId() {
        return generateId("B");
    }

    public static String generateRoomId() {
        return generateId("R");
    }

    public static String generateEquipmentId() {
        return generateId("EQ");
    }

    public static String generateVehicleId() {
        return generateId("V");
    }
}
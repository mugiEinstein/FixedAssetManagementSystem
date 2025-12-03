package ztt.fixedassetmanagement;

import org.mybatis.spring.annotation.MapperScan;
import org. springframework.boot.SpringApplication;
import org.springframework.boot. autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("ztt.fixedassetmanagement.mapper")
public class FixedAssetManagementApplication {
    public static void main(String[] args) {
        SpringApplication.run(FixedAssetManagementApplication.class, args);
    }
}
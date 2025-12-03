package ztt.fixedassetmanagement. controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas. annotations.tags.Tag;
import org.springframework.beans.factory.annotation. Value;
import org.springframework.web. bind.annotation.*;
import org.springframework.web. multipart.MultipartFile;
import ztt.fixedassetmanagement.common.BusinessException;
import ztt.fixedassetmanagement.common.Result;

import java.io. File;
import java. io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java. util.HashMap;
import java. util.Map;
import java.util. UUID;

@Tag(name = "文件上传")
@RestController
@RequestMapping("/api")
public class FileController {

    @Value("${file.upload-dir:uploads}")
    private String uploadDir;

    @Operation(summary = "文件上传")
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        try {
            // 创建上传目录
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 生成文件名
            String originalFilename = file. getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename. substring(originalFilename.lastIndexOf(". "))
                    : "";
            String newFilename = UUID.randomUUID().toString() + extension;

            // 保存文件
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file. getInputStream(), filePath);

            // 返回文件URL
            Map<String, String> result = new HashMap<>();
            result.put("url", "/uploads/" + newFilename);
            result. put("filename", newFilename);

            return Result.success(result);

        } catch (IOException e) {
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }
    }
}
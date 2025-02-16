package org.fuchuang.biz.passageservice.controller;

import lombok.RequiredArgsConstructor;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.fuchuang.framework.starter.convention.result.Result;
import org.fuchuang.framework.starter.web.Results;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 测试控制层
 */
@RestController
@RequiredArgsConstructor
public class TestController {

    private final FileStorageService fileStorageService;

    /**
     * 测试上传oss
     * @return 文件路径
     */
    @PostMapping("/test/upload")
    public Result<String> testUpload(MultipartFile file) {
        FileInfo fileInfo = fileStorageService.of(file)
                .setPath("test/")// 相对路径
                .setSaveFilename("test1.jpg")// 保存文件名
                .setObjectId("0")   //关联对象id，为了方便管理，不需要可以不写
                .setObjectType("0") //关联对象类型，为了方便管理，不需要可以不写
                .putAttr("role","admin") //保存一些属性，可以在切面、保存上传记录、自定义存储平台等地方获取使用，不需要可以不写
                .upload();
        return Results.success(fileInfo.getUrl());
    }
}

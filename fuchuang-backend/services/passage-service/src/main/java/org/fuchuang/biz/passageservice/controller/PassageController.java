package org.fuchuang.biz.passageservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.fuchuang.biz.passageservice.dto.req.PassageUploadReqDTO;
import org.fuchuang.biz.passageservice.service.PassageService;
import org.fuchuang.framework.starter.convention.result.Result;
import org.fuchuang.framework.starter.web.Results;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文章处理控制层
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "文章处理控制层")
public class PassageController {

    private final PassageService passageService;

    /**
     * 文章上传
     */
    @PostMapping("/api/passage-service/v1/upload")
    @Operation(summary = "上传文章")
    public Result<Void> uploadPassage(@RequestBody PassageUploadReqDTO requestParam) {
        passageService.uploadPassage(requestParam);
        return Results.success();
    }
}

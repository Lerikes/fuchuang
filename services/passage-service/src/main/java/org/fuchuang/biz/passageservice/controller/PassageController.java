package org.fuchuang.biz.passageservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.fuchuang.biz.passageservice.dto.req.CommentReqDTO;
import org.fuchuang.biz.passageservice.dto.req.DoLikeReqDTO;
import org.fuchuang.biz.passageservice.dto.req.PartitionReqDTO;
import org.fuchuang.biz.passageservice.dto.req.PassageUploadReqDTO;
import org.fuchuang.biz.passageservice.dto.resp.FirstPassageInfoRespDTO;
import org.fuchuang.biz.passageservice.dto.resp.PartitionRespDTO;
import org.fuchuang.biz.passageservice.dto.resp.PassageDetailInfoRespDTO;
import org.fuchuang.biz.passageservice.service.PassageDoLikeService;
import org.fuchuang.biz.passageservice.service.PassageService;
import org.fuchuang.framework.starter.convention.result.Result;
import org.fuchuang.framework.starter.web.Results;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 文章处理控制层
 */
@RestController
@RequestMapping("/api/passage-service/v1")
@RequiredArgsConstructor
@Tag(name = "文章处理控制层")
public class PassageController {

    private final PassageService passageService;

    private final PassageDoLikeService passageDoLikeService;

    /**
     * 文章上传
     */
    @PostMapping("/upload")
    @Operation(summary = "上传文章")
    public Result<Void> uploadPassage(@RequestBody PassageUploadReqDTO requestParam) {
        passageService.uploadPassage(requestParam);
        return Results.success();
    }

    /**
     * 获取文章分区列表
     */
    @PostMapping("/partition-list")
    @Operation(summary = "获取文章分区列表")
    public Result<PartitionRespDTO> getPartitionList() {
        PartitionRespDTO result = passageService.getPartitionInfo();
        return Results.success(result);
    }

    /**
     * 新建文章分区
     */
    @PostMapping("/addPartition")
    @Operation(summary = "新建文章分区")
    public Result<Void> addNewPartition(@RequestBody PartitionReqDTO requestParam) {
        passageService.addNewPartition(requestParam);
        return Results.success();
    }

    /**
     * 文章展示一级页面，按照标签分组，并且只展示文章标题
     */
    @PostMapping("/first-passage-info")
    @Operation(summary = "文章展示一级页面")
    public Result<Map<String, List<FirstPassageInfoRespDTO>>> getFirstPassageInfo() {
        Map<String, List<FirstPassageInfoRespDTO>> result = passageService.getFirstPassageInfo();
        return Results.success(result);
    }

    /**
     * 文章细节展示，用户点击一级页面的文章内容后通过文章id获取文章具体内容
     */
    @PostMapping("/detail/{passageId}")
    @Operation(summary = "文章细节展示")
    public Result<PassageDetailInfoRespDTO> getDetailPassageInfo(@PathVariable String passageId) {
        PassageDetailInfoRespDTO result = passageService.getPassageDetailInfo(passageId);
        return Results.success(result);
    }

    /**
     * 点赞
     * @return 是否成功
     */
    @PostMapping("/doLike")
    public Result<String> doLike(@RequestBody DoLikeReqDTO requestParam){
        passageDoLikeService.doLike(requestParam);
        return Results.success("点赞成功！");
    }

    /**
     * 收藏
     * @return 是否成功
     */
    @PostMapping("/doCollect")
    public Result<String> doCollect(@RequestBody DoLikeReqDTO requestParam){
        passageDoLikeService.doCollect(requestParam);
        return Results.success("收藏成功！");
    }

    /**
     * 评论
     * @return 是否成功
     */
    @PostMapping("/doComment")
    public Result<String> doComment(@RequestBody CommentReqDTO requestParam){
        passageDoLikeService.doComment(requestParam);
        return Results.success("评论成功！");
    }
}

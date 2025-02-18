package org.fuchuang.biz.passageservice.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章细节返回参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章细节返回参数")
public class PassageDetailInfoRespDTO {

    /**
     * 文章id
     */
    @Schema(description = "文章id")
    private String passageId;

    /**
     * 文章标题
     */
    @Schema(description = "文章标题")
    private String title;

    /**
     * 文章分区
     */
    @Schema(description = "文章分区")
    private String partition;

    /**
     * 文章内容
     */
    @Schema(description = "文章内容")
    private String content;

    /**
     * 上传用户id
     */
    @Schema(description = "上传用户id")
    private String authorId;

    /**
     * 上传用户名称
     */
    @Schema(description = "上传用户名称")
    private String username;

    /**
     * 点赞数
     */
    @Schema(description = "点赞数")
    private Long likes;

    /**
     * 收藏数
     */
    @Schema(description = "收藏数")
    private Long collection;

    /**
     * 观看次数
     */
    @Schema(description = "观看次数")
    private Long views;

    /**
     * 文章创建时间
     */
    @Schema(description = "文章创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 文章更新时间
     */
    @Schema(description = "文章更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;

    /**
     * 文章图片
     */
    @Schema(description = "文章图片")
    private String images;
}

package org.fuchuang.biz.passageservice.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 一级页面获取返回参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "一级页面获取返回参数")
public class FirstPassageInfoRespDTO {

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
     * 分区id
     */
    @Schema(description = "分区id")
    private String partitionId;

    /**
     * 分区名称
     */
    @Schema(description = "文章id")
    private String partitionName;

    /**
     * 文章创建时间
     */
    @Schema(description = "文章创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}

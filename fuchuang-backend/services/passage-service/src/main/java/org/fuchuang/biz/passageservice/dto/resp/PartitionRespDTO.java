package org.fuchuang.biz.passageservice.dto.resp;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 文章分区获取返回参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章分区获取返回参数")
public class PartitionRespDTO {

    /**
     * 分区id
     */
    @Schema(description = "分区id")
    private String partitionId;

    /**
     * 分区名称
     */
    @Schema(description = "分区名称")
    private String partitionName;

    /**
     * 分区创建时间
     */
    @Schema(description = "分区创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /**
     * 分区更新时间
     */
    @Schema(description = "分区更新时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}

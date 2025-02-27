package org.fuchuang.biz.passageservice.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分区新增请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分区新增请求参数")
public class PartitionReqDTO {

    /**
     * 文章分区名称
     */
    @Schema(description = "文章分区名称")
    private String partitionName;
}

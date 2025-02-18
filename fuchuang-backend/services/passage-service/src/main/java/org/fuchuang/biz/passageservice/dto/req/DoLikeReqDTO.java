package org.fuchuang.biz.passageservice.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 点赞，收藏请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "点赞，收藏请求参数")
public class DoLikeReqDTO {

    /**
     * 文章id
     */
    @Schema(description = "文章id")
    private String passageId;

    /**
     * 作者id
     */
    @Schema(description = "作者id")
    private String authorId;

    /**
     * 点赞类型 0取消 1点赞
     */
    @Schema(description = "点赞类型")
    private int type;
}

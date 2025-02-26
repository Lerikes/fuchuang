package org.fuchuang.biz.passageservice.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 评论请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "评论请求参数")
public class CommentReqDTO {

    /**
     * 文章id
     */
    @Schema(description = "文章id")
    private String passageId;

    /**
     * 父评论id
     */
    @Schema(description = "父评论id")
    private String parentId;

    /**
     * 评论正文
     */
    @Schema(description = "评论正文")
    private String content;
}

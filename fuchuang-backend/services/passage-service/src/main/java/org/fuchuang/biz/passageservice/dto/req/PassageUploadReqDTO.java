package org.fuchuang.biz.passageservice.dto.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章上传请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章上传请求参数")
public class PassageUploadReqDTO {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章标题
     */
    @TableField("title")
    private String title;

    /**
     * 文章内容
     */
    @TableField("content")
    private String content;

    /**
     * 文章作者用户id
     */
    @TableField("user_id")
    private Long userId;
}

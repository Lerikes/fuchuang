package org.fuchuang.biz.passageservice.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章内容表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tbl_passage_content")
public class PassageContentDO {

    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文章id
     */
    @TableField("passage_id")
    private Long passageId;

    /**
     * 文章内容
     */
    @TableField("content")
    private String content;
}

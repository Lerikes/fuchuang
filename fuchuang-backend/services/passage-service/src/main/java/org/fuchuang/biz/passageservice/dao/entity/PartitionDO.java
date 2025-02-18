package org.fuchuang.biz.passageservice.dao.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fuchuang.framework.starter.database.base.BaseDO;

/**
 * 分区信息表
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("tbl_passage_partition")
public class PartitionDO extends BaseDO {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分区名称
     */
    @TableField("name")
    private String name;
}

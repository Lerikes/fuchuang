package org.fuchuang.biz.passageservice.dao.entity.mongo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * mongodb中的收藏
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "collection")
public class CollectionDO {

    /**
     * id
     */
    @Id
    private String id;

    /**
     * 收藏者id
     */
    private Long collectorId;

    /**
     * 收藏对象的id
     */
    private Long objectId;

    /**
     * 收藏类型 0:文章 1:评论
     */
    private Integer type;

    /**
     * 收藏时间
     */
    private Date createTime;

    /**
     * 修改时间
     */
    private Date updateTime;

    /**
     * 删除标识
     */
    private Integer delFlag;
}

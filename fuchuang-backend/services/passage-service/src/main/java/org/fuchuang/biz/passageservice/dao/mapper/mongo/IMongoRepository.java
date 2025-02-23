package org.fuchuang.biz.passageservice.dao.mapper.mongo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

@NoRepositoryBean
public interface IMongoRepository<T, ID extends Serializable> extends MongoRepository<T,ID> {
    /**
     * 创建
     *
     * @param entity 新增内容
     * @return 已创建的内容
     */
    public T  create(T entity);

    /**
     * 编辑
     *
     * @param id ID
     * @param entity 编辑的内容
     * @return 编辑后的内容
     */
    public T update(ID id, T entity);

    /**
     * 删除
     *
     * @param ids 待删除的ID
     * @return 删除的组件ID集合
     */
    public List<ID> delete(Collection<ID> ids);

    /**
     * 根据ID查询
     *
     * @param id id
     * @return 组件
     */

    public T getOne(ID id);

    /**
     * 分页查询
     *
     * @param entity   查询条件
     * @param pageRequest 分页信息
     * @return 分页列表
     */
    public Page<T> pageList(T entity, PageRequest pageRequest);
}


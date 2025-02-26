package org.fuchuang.biz.passageservice.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.fuchuang.biz.passageservice.dao.entity.PassageDO;
import org.fuchuang.biz.passageservice.dto.resp.FirstPassageInfoRespDTO;
import org.fuchuang.biz.passageservice.dto.resp.PassageDetailInfoRespDTO;

import java.util.List;

/**
 * 文章持久层
 */
public interface PassageMapper extends BaseMapper<PassageDO> {

    /**
     * 获取一级页面
     */
    List<FirstPassageInfoRespDTO> getFirstPassageInfo();

    /**
     * 通过文章id获取细节信息
     */
    PassageDetailInfoRespDTO getPassageDetailInfo(@Param("passageId") Long passageId);
}

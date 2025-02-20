package org.fuchuang.biz.passageservice.service;

import org.fuchuang.biz.passageservice.dto.req.PartitionReqDTO;
import org.fuchuang.biz.passageservice.dto.req.PassageUploadReqDTO;
import org.fuchuang.biz.passageservice.dto.resp.FirstPassageInfoRespDTO;
import org.fuchuang.biz.passageservice.dto.resp.PartitionRespDTO;
import org.fuchuang.biz.passageservice.dto.resp.PassageDetailInfoRespDTO;

import java.util.List;
import java.util.Map;

/**
 * 文章处理接口
 */
public interface PassageService {

    /**
     * 文章上传
     * @param requestParam 文章参数
     */
    void uploadPassage(PassageUploadReqDTO requestParam);

    /**
     * 一级页面展示
     */
    Map<String, List<FirstPassageInfoRespDTO>> getFirstPassageInfo();

    /**
     * 文章细节展示
     */
    PassageDetailInfoRespDTO getPassageDetailInfo(String passageId);

    /**
     * 获取文章分区列表
     */
    PartitionRespDTO getPartitionInfo();

    /**
     * 新增文章分区
     */
    void addNewPartition(PartitionReqDTO requestParam);
}

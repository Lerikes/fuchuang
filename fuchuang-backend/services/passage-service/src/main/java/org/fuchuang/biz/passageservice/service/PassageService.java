package org.fuchuang.biz.passageservice.service;

import org.fuchuang.biz.passageservice.dto.req.PassageUploadReqDTO;

/**
 * 文章处理接口
 */
public interface PassageService {
    void uploadPassage(PassageUploadReqDTO requestParam);
}

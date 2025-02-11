package org.fuchuang.biz.passageservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fuchuang.biz.passageservice.dao.entity.PassageDO;
import org.fuchuang.biz.passageservice.dao.mapper.PassageMapper;
import org.fuchuang.biz.passageservice.service.PassageService;
import org.springframework.stereotype.Service;

/**
 * 文章处理接口实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PassageServiceImpl extends ServiceImpl<PassageMapper, PassageDO> implements PassageService {
}

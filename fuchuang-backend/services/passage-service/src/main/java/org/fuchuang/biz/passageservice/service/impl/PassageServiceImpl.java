package org.fuchuang.biz.passageservice.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.fuchuang.biz.passageservice.dao.entity.PassageContentDO;
import org.fuchuang.biz.passageservice.dao.entity.PassageDO;
import org.fuchuang.biz.passageservice.dao.mapper.PartitionMapper;
import org.fuchuang.biz.passageservice.dao.mapper.PassageContentMapper;
import org.fuchuang.biz.passageservice.dao.mapper.PassageMapper;
import org.fuchuang.biz.passageservice.dto.req.PassageUploadReqDTO;
import org.fuchuang.biz.passageservice.dto.resp.FirstPassageInfoRespDTO;
import org.fuchuang.biz.passageservice.dto.resp.PassageDetailInfoRespDTO;
import org.fuchuang.biz.passageservice.service.PassageService;
import org.fuchuang.framework.starter.convention.exception.ClientException;
import org.fuchuang.frameworks.starter.user.core.UserContext;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;

import java.util.*;

/**
 * 文章处理接口实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PassageServiceImpl extends ServiceImpl<PassageMapper, PassageDO> implements PassageService {

    private final PassageMapper passageMapper;

    private final FileStorageService fileStorageService;

    private final PassageContentMapper passageContentMapper;

    private final PartitionMapper partitionMapper;

    /**
     * 文章上传
     * @param requestParam 文章内容
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadPassage(PassageUploadReqDTO requestParam) {
       // 参数校验
        if (requestParam == null || StringUtils.isEmpty(requestParam.getTitle()) || StringUtils.isEmpty(requestParam.getContent())
                || requestParam.getPartition() == null || requestParam.getPartition() < 0) {
            throw new ClientException("传参有误！");
        }

        // 获取当前登录用户id
        String userId = UserContext.getUserId();
        // 根据id获取用户名称

        // 新建文章实体类
        PassageDO passageDO = new PassageDO();
        // 属性拷贝
        BeanUtils.copyProperties(requestParam, passageDO);
        // 设置用户id
        passageDO.setAuthorId(Long.valueOf(userId));
        // TODO 根据用户id查找并设置上传者名称

        // 将图片列表转换为用逗号分隔的字符串
        String imagesString = String.join(",", requestParam.getImages());
        // 设置文章图片
        passageDO.setImages(imagesString);

        try {
            // 保存文章到数据库
            passageMapper.insert(passageDO);

            // 新建文章内容实体类
            PassageContentDO passageContentDO = new PassageContentDO();
            // 设置文章id
            passageContentDO.setPassageId(passageDO.getId());
            // 设置文章内容
            passageContentDO.setContent(requestParam.getContent());
            // 保存文章内容到数据库
            passageContentMapper.insert(passageContentDO);
        }catch (Exception e) {
            // 发生异常，回滚事务
            log.error("文章上传失败", e);
            throw new ClientException("文章上传失败");
        }
    }

    /**
     * 一级页面获取
     * @return 按标签分区的文章标题
     */
    @Override
    public Map<String, List<FirstPassageInfoRespDTO>> getFirstPassageInfo() {
        // 获取数据
        List<FirstPassageInfoRespDTO> passages = passageMapper.getFirstPassageInfo();

        // 按照标签分类
        Map<String, List<FirstPassageInfoRespDTO>> result = new LinkedHashMap<>();
        for (FirstPassageInfoRespDTO passage : passages) {
            result.computeIfAbsent(passage.getPartitionName(), k -> new ArrayList<>()).add(passage);
        }

        // TODO 修改为redis取热点文章逻辑
        // 将标签中的数据按照时间降序（暂时）排序
        result.forEach((label, passageList) ->
                passageList.sort(Comparator.comparing(FirstPassageInfoRespDTO::getCreateTime).reversed()));

        return result;
    }

    /**
     * 文章细节获取
     * @param passageId 选择文章的id
     * @return 文章内容
     */
    @Override
    public PassageDetailInfoRespDTO getPassageDetailInfo(String passageId) {
        // 参数校验
        if (StringUtils.isEmpty(passageId)) {
            throw new ClientException("传参有误！");
        }

        // 根据文章id连表查询细节
        PassageDetailInfoRespDTO result = passageMapper.getPassageDetailInfo(Long.valueOf(passageId));

        // 设置参数
        Boolean isCheck = result.getIsCheck();
        result.setIsCheck(isCheck);
        result.setFakeRate(isCheck ? result.getFakeRate() : null);
        String imagesStr = (result.getImages() != null ? result.getImages().toString() : "");
        if (StrUtil.isNotBlank(imagesStr)){
            List<String> images = Arrays.asList(imagesStr.split(","));
            result.setImages(images);
        }

        return result;
    }

    /**
     * 图片上传方法
     * @param imageFile 上传的图片
     * @return 图片的url
     */
    private String uploadPassageImages(MultipartFile imageFile) {
        // 1. 获取文件名
        String fileName = imageFile.getOriginalFilename();
        if (fileName == null || fileName.lastIndexOf(".") == -1) {
            throw new ClientException("文件格式异常");
        }
        // 2 获取后缀
        String suffix = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
        // 2.1 图片格式判断
        List<String> allowedSuffixes = Arrays.asList(".jpg", ".png", ".jpeg");
        if (!allowedSuffixes.contains(suffix)) {
            throw new ClientException("仅支持JPG/PNG/JPEG格式");
        }
        // 2.2 拼接文件名
        String name = "passageImages_" + UUID.randomUUID() + suffix;
        // 3. 上传文件
        FileInfo fileInfo = fileStorageService.of(imageFile)
                .setPath("passage-images/")
                .setSaveFilename(name)
                .upload();
        return fileInfo.getUrl();
    }
}

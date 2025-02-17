package org.fuchuang.biz.passageservice.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.x.file.storage.core.FileInfo;
import org.dromara.x.file.storage.core.FileStorageService;
import org.fuchuang.biz.passageservice.common.constant.ParamConstant;
import org.fuchuang.biz.passageservice.dao.entity.PassageDO;
import org.fuchuang.biz.passageservice.dao.mapper.PassageMapper;
import org.fuchuang.biz.passageservice.dto.req.PassageUploadReqDTO;
import org.fuchuang.biz.passageservice.dto.resp.FirstPassageInfoRespDTO;
import org.fuchuang.biz.passageservice.dto.resp.PassageDetailInfoRespDTO;
import org.fuchuang.biz.passageservice.service.PassageService;
import org.fuchuang.framework.starter.convention.exception.ClientException;
import org.fuchuang.frameworks.starter.user.core.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.HtmlUtils;

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

    private final ObjectMapper objectMapper;

    /**
     * 文章上传
     * @param requestParam 文章内容
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadPassage(PassageUploadReqDTO requestParam) {
        // 参数检验
        if (requestParam == null || StringUtils.isBlank(requestParam.getTitle()) || StringUtils.isBlank(requestParam.getContent())) {
            throw new ClientException("上传参数有误！");
        }

        // 构建文章实体，先插入到数据库
        PassageDO passageDO = PassageDO.builder()
                .title(HtmlUtils.htmlEscape(requestParam.getTitle())) // xss防御
                .content(HtmlUtils.htmlEscape(requestParam.getContent()))
                .authorId(Long.valueOf(UserContext.getUserId()))
                .label(HtmlUtils.htmlEscape(requestParam.getLabel()))
                .likes(ParamConstant.UPLOAD_DEFAULT_COUNT)
                .views(ParamConstant.UPLOAD_DEFAULT_COUNT)
                .collection(ParamConstant.UPLOAD_DEFAULT_COUNT)
                .build();
        // 保存到数据库
        try {
            passageMapper.insert(passageDO);
        } catch (Exception e) {
            throw new ClientException("文章上传失败！");
        }

        // todo: 这里不能这样处理，这样肯定会有性能问题，而且可能有并发问题，一定要改！！！！！！
        // 并发上传图片处理
        List<String> imageUrls = Collections.synchronizedList(new ArrayList<>());
        List<MultipartFile> validFiles = Optional.ofNullable(requestParam.getImages())
                .orElse(Collections.emptyList())
                .parallelStream()  // 启用并行流
                .filter(file -> !file.isEmpty())
                .toList();
        try {
            validFiles.parallelStream().forEach(file -> {
                try {
                    String url = uploadPassageImages(file);
                    imageUrls.add(url);
                } catch (Exception e) {
                    throw new ClientException("获取图片失败！");
                }
            });
            // 更新图片信息
            try {
                passageDO.setImages(objectMapper.writeValueAsString(imageUrls));
                passageMapper.updateById(passageDO);
            } catch (Exception e) {
                throw new ClientException("图片序列化失败！");
            }
        } catch (Exception e) {
            // 如果文件上传失败应该进行清理
            imageUrls.parallelStream().forEach(url -> {
                try {
                    fileStorageService.delete(url);
                } catch (Exception e1) {
                    throw new ClientException("文件清理失败！");
                }
            });
        }
    }

    /**
     * 一级页面获取
     * @return 按标签分区的文章标题
     */
    @Override
    public Map<String, List<FirstPassageInfoRespDTO>> getFirstPassageInfo() {
        // 获取数据
        List<PassageDO> passages = passageMapper.getFirstPassageInfo();
        // 按照标签分类
        Map<String, List<FirstPassageInfoRespDTO>> result = new LinkedHashMap<>();
        passages.forEach(passageDO -> {
            String label = passageDO.getLabel();
            result.computeIfAbsent(label, k -> new ArrayList<>())
                    .add(new FirstPassageInfoRespDTO(passageDO.getId().toString(),
                            passageDO.getTitle(),
                            passageDO.getCreateTime(),
                            passageDO.getUpdateTime()));
        });
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

        // 从数据库查询文章信息
        PassageDO passageDO = passageMapper.selectById(passageId);
        PassageDetailInfoRespDTO result = new PassageDetailInfoRespDTO();
        result.setPassageId(passageId);
        result.setTitle(passageDO.getTitle());
        result.setLabel(passageDO.getLabel());
        result.setContent(passageDO.getContent());
        result.setAuthorId(String.valueOf(passageDO.getAuthorId()));
        result.setUsername(passageDO.getUserName());
        result.setLikes(passageDO.getLikes());
        result.setCollection(passageDO.getCollection());
        result.setViews(passageDO.getViews());
        // todo 这里需要对返回的图片url进行处理
        result.setImages(passageDO.getImages());
        log.info("文章细节：{}", result);

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

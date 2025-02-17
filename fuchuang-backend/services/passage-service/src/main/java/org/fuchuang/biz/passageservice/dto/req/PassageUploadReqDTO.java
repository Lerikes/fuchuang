package org.fuchuang.biz.passageservice.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 文章上传请求参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文章上传请求参数")
public class PassageUploadReqDTO {

    /**
     * 文章标题
     */
    @Schema(description = "文章标题")
    private String title;

    /**
     * 文章内容
     */
    @Schema(description = "文章内容")
    private String content;

    /**
     * 文章图片
     */
    @Schema(description = "文章图片")
    private List<MultipartFile> images;
}

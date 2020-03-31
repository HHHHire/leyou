package com.mall.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @author: ddh
 * @data: 2020/3/24 20:37
 * @description
 */
@Service
public class UploadService {

    @Autowired
    private FastFileStorageClient storageClient;

    private static final String URL_PREX = "http://47.100.88.162/";

    private Logger logger = LoggerFactory.getLogger(UploadService.class);

    private static final List<String> CONTENT_TYPE = Arrays.asList("image/jpeg", "image/png", "image/gif");

    public String uploadImage(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        // 检查文件类型
        String contentType = file.getContentType();
        if (!CONTENT_TYPE.contains(contentType)) {
            logger.info("文件类型不符:{}", originalFilename);
            return null;
        }

        try {
            // 校验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if (bufferedImage == null) {
                logger.info("文件内容不合法:{}", originalFilename);
                return null;
            }

            // 保存到服务器
//            file.transferTo(new File(""));
            String ext = StringUtils.substringAfterLast(originalFilename, ".");
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);

            // 返回 url 路径
            return URL_PREX + storePath.getFullPath();
        } catch (IOException e) {
            logger.info("服务器内部故障:{}", originalFilename);
            e.printStackTrace();
        }
        return null;
    }
}

package com.shousi.shousibackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * 文件上传
     * @param file
     * @return
     */
    String upload(MultipartFile file);
}

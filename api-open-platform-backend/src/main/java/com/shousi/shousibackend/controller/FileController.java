package com.shousi.shousibackend.controller;

import cn.hutool.core.io.FileUtil;
import com.shousi.shousicommon.common.BaseResponse;
import com.shousi.shousicommon.common.ErrorCode;
import com.shousi.shousicommon.common.ResultUtils;
import com.shousi.shousicommon.model.vo.ImageVo;
import com.shousi.shousicommon.model.vo.UserVO;
import com.shousi.shousibackend.exception.BusinessException;
import com.shousi.shousibackend.service.FileService;
import com.shousi.shousibackend.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@RestController
@RequestMapping("/file")
@Slf4j
public class FileController {
    private static final long ONE_M = 2 * 1024 * 1024L;
    @Resource
    private UserService userService;

    @Resource
    private FileService fileService;

    /**
     * 上传文件
     *
     * @param multipartFile 多部分文件
     * @param request       请求
     * @return {@link BaseResponse}<{@link ImageVo}>
     */
    @PostMapping("/upload")
    public BaseResponse<ImageVo> uploadFile(@RequestPart("file") MultipartFile multipartFile, HttpServletRequest request) {
        UserVO userVO = userService.getLoginUser(request);
        if (userVO == null) {
            return ResultUtils.error(ErrorCode.NOT_LOGIN_ERROR);
        }
        String upload = fileService.upload(multipartFile);
        ImageVo imageVo = new ImageVo();
        imageVo.setUrl(upload);
        return ResultUtils.success(imageVo);
    }

    /**
     * 有效文件
     * 校验文件
     *
     * @param multipartFile 多部分文件
     */
    private String validFile(MultipartFile multipartFile) {
        // 文件大小
        long fileSize = multipartFile.getSize();
        // 文件后缀
        String fileSuffix = FileUtil.getSuffix(multipartFile.getOriginalFilename());
        if (fileSize > ONE_M) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小不能超过 1M");
        }
        if (!Arrays.asList("jpeg", "jpg", "svg", "png", "webp", "jfif").contains(fileSuffix)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持该类型文件");
        }
        return "success";
    }
}
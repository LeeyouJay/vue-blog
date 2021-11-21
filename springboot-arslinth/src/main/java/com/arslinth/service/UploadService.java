package com.arslinth.service;

import cn.hutool.core.io.FileTypeUtil;
import com.arslinth.utils.ThumbnailsUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

/**
 * @author Arslinth
 * @ClassName UploadService
 * @Description TODO
 * @Date 2021/3/2
 */
@Service
public class UploadService {
    // 文件的真实路径
    @Value("${file.uploadFolder}")
    private String realBasePath;
    @Value("${file.accessPath}")
    private String accessPath;

    @Value("${file.musicPath}")
    private String musicPath;

    public String uploadImg(MultipartFile file, String name) throws IOException {

        InputStream inputStream = file.getInputStream();

        String fileName = name + ".jpg" ;

        String saveToPath = accessPath;
        // 真实路径，实际储存的路径
        String realPath = realBasePath;
        // 储存文件的物理路径，使用本地路径储存
        String filepath = realPath + fileName;

        System.out.println("上传图片名为：" + fileName + "--虚拟文件路径为：" + saveToPath + "--物理文件路径为：" + realPath);

        // 判断有没有对应的文件夹
        File destFile = new File(filepath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        //输出流 输出到文件
        OutputStream outputStream = new FileOutputStream(destFile);
        // 缓冲区
        byte[] bs = new byte[1024];
        int len = -1;
        while ((len = inputStream.read(bs)) != -1) {
            outputStream.write(bs,0,len);
        }
        inputStream.close();
        outputStream.close();
        //复制保存并缩小原图图片
        ThumbnailsUtil.generateThumbnail2Directory(realBasePath + fileName);
        //追加后缀
        String miniName = ThumbnailsUtil.appendSuffix(fileName, ThumbnailsUtil.SUFFIX);
        // 返回虚拟路径，给链接访问
        return saveToPath + miniName;
    }

    public String uploadSong(MultipartFile file) throws IOException {

        // 获取原文件名
        String OriginalFilename = file.getOriginalFilename();

        //String suffixName = OriginalFilename.substring(OriginalFilename.lastIndexOf("."));// 获取文件的后缀名

        String saveToPath = musicPath;
        // 真实路径，实际储存的路径
        String realPath = realBasePath;
        // 储存文件的物理路径，使用本地路径储存
        String filepath = realPath + OriginalFilename;
        System.out.println("上传音乐名：" + OriginalFilename + "--虚拟文件路径为：" + saveToPath + "--物理文件路径为：" + realPath);
        // 判断有没有对应的文件夹
        File destFile = new File(filepath);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }
        file.transferTo(destFile);// 文件写入
        // 返回虚拟路径，给链接访问
        return saveToPath +OriginalFilename;
    }
}

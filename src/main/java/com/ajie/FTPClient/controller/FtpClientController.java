package com.ajie.FTPClient.controller;

import com.ajie.FTPClient.services.FtpClientServices;

/**
 * @ClassName FtpClientController
 * @Description FtpClient的Controller
 * @Author septzhang
 * @Date 2022/2/13 23:20
 * @Version 1.0
 **/
public class FtpClientController {
    FtpClientServices ftpClientServices = new FtpClientServices();

    public void updownFile(String filePath){
        ftpClientServices.uploadFiles(filePath);
    }

    public Boolean downLoadFile(String fileUuid, String desPath, String fileName){
        return ftpClientServices.downloadFiles(fileUuid,desPath,fileName);
    }

    public String getFileInfo(String FileUuid){
        return  ftpClientServices.getFileInfo(FileUuid);
    }

}

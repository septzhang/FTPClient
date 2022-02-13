package com.ajie.FTPClient;

import com.ajie.FTPClient.controller.FtpClientController;
import org.junit.Test;

/**
 * @ClassName SimpleHttpTest
 * @Description
 * @Author septzhang
 * @Date 2022/2/13 17:16
 * @Version 1.0
 **/
public class SimpleHttpTest {
    FtpClientController ftpClientController = new FtpClientController();

    /**
     * 获取文件信息
     */
    @Test
    public void doGetFileInfo(){
        System.out.println(ftpClientController.getFileInfo("53fa657f71f643648c27100aeff4d74e"));
    }


    /**
     * 下载文件测试
     */
    @Test
    public void dodownloadFiles(){
        ftpClientController.downLoadFile("53fa657f71f643648c27100aeff4d74e","E:/imgs/img/image/img1/","cat.png");
    }

    /**
     * 上传文件测试
     */
    @Test
    public void doupwnloadFiles(){
        ftpClientController.updownFile("E:/img/cat.png");
    }


}

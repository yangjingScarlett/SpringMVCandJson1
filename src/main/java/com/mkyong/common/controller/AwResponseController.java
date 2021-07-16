package com.mkyong.common.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

@RestController
@RequestMapping("/awbat")
public class AwResponseController {

  @RequestMapping("/test")
  public String test() {
    return "Hello Awbat!";
  }

  @RequestMapping(value = "/awResponse/{requestId}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public String awBatResponse(@RequestParam("logFile") CommonsMultipartFile file,
      @PathVariable String requestId,
      HttpServletRequest request, HttpServletResponse response) {

    System.out.println(requestId);
    String sweepStatus = request.getParameter("sweepStatus");
    String sweepMessage = request.getParameter(("sweepMessage"));
    if ("S".equals(sweepStatus)) {
      String file_PATH = "/home/jingyang/upload/temp";
      if (System.getProperty("os.name").startsWith("Windows")) {
        file_PATH = "C:/upload/temp/";
      }

      //将文件缓冲到本地
      boolean localFile = createLocalFile(file_PATH, file);
      if (!localFile) {
        System.out.println("Create local file failed!");
        return "Create local file failed!";
      }
      System.out.println("Create local file successfully");
      return "Create local file successfully";
    } else {
      System.out.println("Sweep failed: " + sweepMessage);
      return "sweep failed: " + sweepMessage;
    }
  }

  /**
   * 通过上传的文件名，缓冲到本地，后面才能解压、验证
   *
   * @param filePath 临时缓冲到本地的目录
   * @param file     客户端上传过来的文件
   */
  public boolean createLocalFile(String filePath, MultipartFile file) {
    File localFile = new File(filePath);
    //先创建目录
    boolean createDirFlag = localFile.mkdirs();

    String originalFilename = file.getOriginalFilename();
    String path = filePath + "/" + originalFilename;

    System.out.println("createLocalFile path = " + path);

    localFile = new File(path);
    FileOutputStream fos = null;
    InputStream in = null;
    try {
      if (localFile.exists()) {
        //如果文件存在删除文件
        boolean delete = localFile.delete();
        if (!delete) {
          System.out.println("Delete exist file " + path + " failed!!!" +
              new Exception("Delete exist file \"" + path + "\" failed!!!"));
        }
      }
      //创建文件
      if (!localFile.exists()) {
        //如果文件不存在，则创建新的文件
        boolean createdFileFlag = localFile.createNewFile();
        System.out.println("Create file successfully,the file is " + path);
      }

      //创建文件成功后，写入内容到文件里
      fos = new FileOutputStream(localFile);
      in = file.getInputStream();
      byte[] bytes = new byte[1024];

      int len = -1;

      while ((len = in.read(bytes)) != -1) {
        fos.write(bytes, 0, len);
      }

      fos.flush();
      System.out.println("Reading uploaded file and buffering to local successfully!");
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    } finally {
      try {
        if (fos != null) {
          fos.close();
        }
        if (in != null) {
          in.close();
        }
      } catch (IOException e) {
        System.out.println("InputStream or OutputStream close error :" + e);
      }
    }

    return true;
  }

}

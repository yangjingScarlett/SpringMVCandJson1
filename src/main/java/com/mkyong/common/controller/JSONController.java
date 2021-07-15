package com.mkyong.common.controller;

import com.mkyong.common.model.Shop;
import com.mkyong.common.model.User;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Map;
import java.util.zip.GZIPOutputStream;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
//@RequestMapping("/kfc/brands")
public class JSONController {

  public
  @RequestMapping("/shop")
  @ResponseBody
  Shop getShopInJSON() {

    Shop shop = new Shop();
    shop.setName("yang");
    shop.setStaffName(new String[]{"mkyong1", "mkyong2"});

    return shop;
  }

  public
  @RequestMapping("/user")
  @ResponseBody
  User getUserInJSON() {
    return new User("huahua", "male");
  }

  @RequestMapping(value = "/user", method = RequestMethod.POST)
  @ResponseBody
  public User addNewUser(String name, String sex) {
    return new User(name, sex);
  }

  // This API is used when client request with JSON object string. and the file is parsed as byte[], then encode to string
  @RequestMapping(value = "/awResponseJson/{requestId}", method = RequestMethod.POST, consumes = "application/json")
  @ResponseBody
  public String awbatResponse(@RequestBody Map<String, String> map) {
    if (map.containsKey("sweepStatus") && map.get("sweepStatus").equals("S")) {
      String awFileContents = map.get("awFileContents");
      byte[] bytes = Base64.getDecoder().decode(awFileContents);
      writeBytesToFiles(bytes, "/home/jingyang/gePrograms/glassfish5/rsweep/awbat/awbat.tar.gz");
      return "success";
    }
    System.out.println(map.get("sweepMessage"));
    return "fail";
  }

  /**
   * Write byte array of gzip file to writeFilePath
   *
   * @param bytes         byte array of a gzip file
   * @param writeFilePath file path to write byte array
   */

  public void writeBytesToFiles(byte[] bytes, String writeFilePath) {
    GZIPOutputStream gzipOutputStream = null;
    FileOutputStream fileOutputStream = null;
    ByteArrayOutputStream byteArrayOutputStream = null;
    try {
      byteArrayOutputStream = new ByteArrayOutputStream(bytes.length);
      gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
      gzipOutputStream.write(bytes);
      fileOutputStream = new FileOutputStream(writeFilePath);
      fileOutputStream.write(byteArrayOutputStream.toByteArray());
    } catch (IOException e) {
      System.out.println(e.getMessage());
    } finally {
      try {
        if (gzipOutputStream != null) {
          gzipOutputStream.close();
        }
        if (byteArrayOutputStream != null) {
          byteArrayOutputStream.close();
        }
        if (fileOutputStream != null) {
          fileOutputStream.close();
        }
      } catch (IOException e) {
        System.out.println(e.getMessage());
        ;
      }
    }

  }
}
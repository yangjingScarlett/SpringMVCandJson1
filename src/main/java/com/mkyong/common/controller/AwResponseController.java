package com.mkyong.common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public class AwResponseController {

  @RequestMapping(value = "/awResponse/{requestId}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public String awBatResponse(@RequestParam("awFile") CommonsMultipartFile file,
      @PathVariable String requestId,
      HttpServletRequest request, HttpServletResponse response) {
    System.out.println("got data...");
    String fileContents = file.getOriginalFilename();
    String sweepStatus = request.getParameter("sweepStatus");
    if ("S".equals(sweepStatus)) {
      return "success";
    }
    return "fail";
  }

}

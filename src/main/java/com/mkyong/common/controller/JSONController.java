package com.mkyong.common.controller;

import com.mkyong.common.model.Shop;
import com.mkyong.common.model.User;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
    User user = new User("huahua", "male");
    return user;
  }

  @RequestMapping(value = "/user", method = RequestMethod.POST)
  @ResponseBody
  public User addNewUser(String name, String sex) {
    return new User(name, sex);
  }

  @RequestMapping(value = "/awResponse/{requestId}", method = RequestMethod.POST, consumes = "application/json")
  @ResponseBody
  public String awbatResponse(@RequestBody Map<String, String> map) {
    if (map.get("sweepStatus").equals("F")) {
      return "fail";
    }
    return "success";
  }
}
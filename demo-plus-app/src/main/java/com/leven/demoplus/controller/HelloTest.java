package com.leven.demoplus.controller;

import com.leven.demoplus.cache.LocalCache;
import com.leven.demoplus.enity.User;
import com.leven.demoplus.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping(path = "v1")
public class HelloTest {

  private UserService userService;

  public HelloTest(UserService userService) {
    this.userService = userService;
  }

  @Autowired
  LocalCache localCache;

  @RequestMapping(
      path = "hello",
      method = RequestMethod.POST,
      consumes = "application/json", //指定请求的格式
      produces = MediaType.APPLICATION_JSON_VALUE )//指定返回conent-type的格式
  @ResponseBody //返回到方法体
  public void hello(@RequestBody User user) {
    userService.get(1);
  }

  @RequestMapping(path = "/owners/{ownerId}", method = RequestMethod.GET)
  public String findOwner(@PathVariable String ownerId, Model model) { //@PathVariable 绑定请求路径上的值{}
    // Owner owner = ownerService.findOwner(ownerId);
    // model.addAttribute("owner", owner);
    return "displayOwner";
  }

  @RequestMapping(method = RequestMethod.GET)
  public String setupForm(
      @RequestParam(value = "petId", required = false) int petId, ModelMap model) { //@RequestParam get请求中？后面携带参数绑定
    /*Pet pet = this.clinic.loadPet(petId);
    model.addAttribute("pet", pet);*/
    return "petForm";
  }

  @RequestMapping(path = "/test")
  public void test() throws ExecutionException {
    System.out.println("localDate = " + localCache.cacheList.get("456"));
  }
}

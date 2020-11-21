package com.leven.demoplus.controller;

import com.leven.demoplus.enity.KafkaConsumeData;
import com.leven.demoplus.enity.User;
import com.leven.demoplus.mybatis.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.security.acl.Owner;

@RestController
@RequestMapping(path = "v1")
public class HelloTest {

  private UserService userService;

  public HelloTest(UserService userService) {
    this.userService = userService;
  }

  @RequestMapping(
      path = "hello",
      method = RequestMethod.POST,
      consumes = "application/json",
      produces = MediaType.APPLICATION_JSON_VALUE)
  @ResponseBody //返回到方法体
  public void hello(@RequestBody User user) {
    userService.get(1);
  }

  @RequestMapping(path = "/owners/{ownerId}", method = RequestMethod.GET)
  public String findOwner(@PathVariable String ownerId, Model model) {
    // Owner owner = ownerService.findOwner(ownerId);
    // model.addAttribute("owner", owner);
    return "displayOwner";
  }

  @RequestMapping(method = RequestMethod.GET)
  public String setupForm(
      @RequestParam(value = "petId", required = false) int petId, ModelMap model) {
    /*Pet pet = this.clinic.loadPet(petId);
    model.addAttribute("pet", pet);*/
    return "petForm";
  }
}

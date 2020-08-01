package com.leven.demoplus.enumDemo;

import com.leven.demoplus.enity.Country;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

class Student {
  private Country country;

  public Country getCountry() {
    return country;
  }

  public void setCountry(Country country) {
    this.country = country;
  }
}

@SpringBootTest
public class EnumDemo {

  @Test
  void test01() {
    Student s = new Student();
    s.setCountry(Country.ONE);

    Country coutry = Country.getEnumById(1);

    System.out.println("coutry.getResStr() = " + coutry.getResStr());
  }
}

package com.leven.demoplus.javase.newfeature;

import org.junit.Test;

import java.time.*;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.time.temporal.TemporalAdjusters;

/** java8的一些新特性： 1：lamada表达式 2：Optional 3：新的时间api 4： */
public class TestLocalDataTime {

  @Test
  public void test1() {
    LocalDateTime now = LocalDateTime.now();
    System.out.println("now = " + now);

    /** 修改天数 */
    LocalDateTime ldt1 = now.withDayOfMonth(10);
    System.out.println("ldt1 = " + ldt1);

    /** 下一个周五 */
    LocalDateTime ldt2 = now.with(TemporalAdjusters.next(DayOfWeek.FRIDAY));
    System.out.println("ldt2 = " + ldt2);

    // 自定义
    LocalDateTime ldt3 =
        now.with(
            new TemporalAdjuster() {
              @Override
              public Temporal adjustInto(Temporal temporal) {
                LocalDateTime l = (LocalDateTime) temporal;
                DayOfWeek dayOfWeek = l.getDayOfWeek();
                if (DayOfWeek.FRIDAY.equals(dayOfWeek)) {
                  return l.plusDays(3);
                } else if (DayOfWeek.THURSDAY.equals(dayOfWeek)) {
                  return l.plusDays(4);
                } else if (DayOfWeek.SUNDAY.equals(dayOfWeek)) {
                  return l.plusDays(1);
                }
                return null;
              }
            });
    System.out.println("ldt3 = " + ldt3);
  }

  /** LocaDate LocalTime LocalDateTime 给人读的时间 Instance是给机器时间 */
  @Test
  public void test2() {
    LocalDateTime ldt = LocalDateTime.now();
    System.out.println("ldt = " + ldt);

    LocalDateTime ldt2 = LocalDateTime.of(2015, 10, 1, 10, 01);
    System.out.println("ldt2 = " + ldt2);

    int month = ldt.getMonthValue();

    int dayOfMonth = ldt.getDayOfMonth();
    int dayOfYear1 = ldt.getDayOfYear();
    // dayOfYear返回就是一年中的天数
    System.out.println("dayOfYear1 = " + dayOfYear1);
    System.out.println("dayOfMonth = " + dayOfMonth);

    DayOfWeek dayOfWeek = ldt.getDayOfWeek();
    int dayOfYear = ldt.getYear();
    System.out.println(
        "dayOfYear = " + dayOfYear + "\t" + "month\t" + month + "\tweek\t" + dayOfWeek);
  }

  // Instance
  @Test
  public void test03() throws InterruptedException {
    // 默认是获取UTC时间
    Instant now = Instant.now();
    System.out.println("now = " + now);

    // 运算
    OffsetDateTime offsetDateTime = now.atOffset(ZoneOffset.ofHours(8)); // 移动8个时区
    System.out.println("offsetDateTime = " + offsetDateTime); // 2020-11-29T12:12:12.136+08:00

    // 毫秒显示
    System.out.println("毫秒：\t" + now.toEpochMilli());
    System.out.println("nano\t" + now.getNano());
    System.out.println("加十毫秒\t" + Instant.ofEpochMilli(10)); // 元年加10毫秒

    // Duration 时间间隔
    Instant now1 = Instant.now();

    Thread.sleep(1000);

    Instant now2 = Instant.now();

    Duration between = Duration.between(now1, now2);
    System.out.println("between.toMillis() = " + between.toMillis());

    System.out.println("-----------------------------------------");
  }

  // Period：计算两个日期之间的间隔
  @Test
  public void test04() throws InterruptedException {
    LocalDate now = LocalDate.now();
    Thread.sleep(1000);
    LocalDate now1 = LocalDate.now();
    Period between = Period.between(now, now1);
    System.out.println(between);
  }

  // 毫秒转时间
  @Test
  public void test05() throws InterruptedException {
    long l = Instant.now().toEpochMilli();
    LocalDate localDate = Instant.ofEpochMilli(l).atZone(ZoneId.systemDefault()).toLocalDate();
    System.out.println("localDate = " + localDate);
    LocalDateTime localDateTime =
        Instant.ofEpochMilli(l).atZone(ZoneId.systemDefault()).toLocalDateTime();
    System.out.println("localDateTime = " + localDateTime);

    System.out.println("====================");
    // 时间转毫秒
    LocalDateTime now = LocalDateTime.of(2021, 10, 14, 18, 20);
    System.out.println("now = " + now);
    long l1 = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    System.out.println("l1 = " + l1);
  }
}

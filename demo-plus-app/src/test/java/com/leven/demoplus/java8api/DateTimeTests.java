package com.leven.demoplus.java8api;

import org.junit.jupiter.api.Test;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Set;
import java.util.concurrent.TimeUnit;

class DateTimeTests {

  // LocalDate LocalTime LocalDateTime
  @Test
  void contextLoads() {
    // 获取当前时间
    Instant now = Instant.now(); // 获取UTC时区的时间
    int nano = now.getNano();
    OffsetDateTime offsetDateTime = now.atOffset(ZoneOffset.ofHours(8)); // 偏移UTC的8小时时间
    System.out.println("nano = " + nano);
    System.out.println(now);
    System.out.println("offsetDateTime = " + offsetDateTime);

    // LocalDateTime
    System.out.println("now.toEpochMilli() = " + now.toEpochMilli());
  }

  // Duration 计算两个时间的间隔
  // Period 计算两个日期之间的间隔
  @Test
  void test02() throws InterruptedException {
    Instant now = Instant.now();
    TimeUnit.SECONDS.sleep(3);
    Instant now2 = Instant.now();

    Duration between = Duration.between(now, now2);

    System.out.println("between = " + between.toMillis());
    System.out.println("between = " + between.toNanos());
    System.out.println("between = " + between.toDays());

    System.out.println("========================");
  }

  @Test
  void test03() {
    LocalDate of1 = LocalDate.of(2015, 1, 1);
    LocalDate of2 = LocalDate.of(2011, 5, 1);
    Period between = Period.between(of2, of1);
    System.out.println("between = " + between.toTotalMonths());
    System.out.println("Period.between(of1,of2) = " + Period.between(of2, of1).getDays());
    System.out.println("Period.between(of1,of2) = " + Period.between(of2, of1).getYears());
    System.out.println("Period.between(of1,of2) = " + Period.between(of2, of1).getMonths());
  }

  // 时间矫正器
  @Test
  void test04() {
    LocalDateTime now = LocalDateTime.now();
    // 下一个周五
    System.out.println(now.with(TemporalAdjusters.next(DayOfWeek.FRIDAY)));
    // 自定义
    LocalDateTime with =
        now.with(
            (l) -> {
              LocalDateTime ldt3 = (LocalDateTime) l;
              DayOfWeek dayOfWeek = ldt3.getDayOfWeek();
              if (dayOfWeek.equals(DayOfWeek.FRIDAY)) {
                return ldt3.plusDays(3);
              } else if (DayOfWeek.WEDNESDAY.equals(ldt3)) {
                return ldt3.plusDays(4);
              } else {
                return ldt3.plusDays(5);
              }
            });
    System.out.println("with = " + with);
  }

  // 时间格式化
  @Test
  void test05() {
    LocalDateTime now = LocalDateTime.now();
    DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
    String format = now.format(df);
    System.out.println("df = " + format);

    System.out.println("=================");
    LocalDateTime parseTime = now.parse(format, df);
    System.out.println("parseTime = " + parseTime);
  }

  // ZoneDate ZoneTime ZonedateTime
  // 对时区的支持
  @Test
  void test06() {
    // 查看支持那些时区
    Set<String> zoneIds = ZoneId.getAvailableZoneIds();
    //System.out.println("zoneIds.toString() = " + zoneIds.toString());

    // 获取所在时区的时间
    LocalDateTime now = LocalDateTime.now(ZoneId.of("America/Cuiaba"));
    System.out.println("now = " + now);
  }
}

package com.leven.demoplus.javase.spi;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * java的SPI机制：
 * 1：在ClassPath目录下创建一个/META-INF/services目录，
 * 2：目录下创建一个接口的权限定名称作为文件名的文件
 * 3：接口里面配置实现类的全限定名称
 * 4：通过类加载器就可以加载到目录下的对象
 * 6：JDBC就是通过这种方法适配不同厂商的驱动的，厂商只需要实现接口，在包下的配置实现的类就可以了。
 */
public class DemoAction {
  public static void main(String[] args) {
      ServiceLoader<ILog> load = ServiceLoader.load(ILog.class);
      Iterator<ILog> iterator = load.iterator();
      while (iterator.hasNext()){
          ILog next = iterator.next();
          next.log();
      }
  }
}

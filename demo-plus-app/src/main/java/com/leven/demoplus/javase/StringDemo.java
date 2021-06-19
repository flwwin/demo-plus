package com.leven.demoplus.javase;

import org.junit.Test;

public class StringDemo {

    @Test
    public void test01(){
        String str1 = "58" + "tongcheng";
        System.out.println(str1);
        System.out. println( str1.intern( ) );
        System.out.println(str1 == str1.intern());
        System.out.println();
        String str2 = new StringBuilder("ja").append("va" ).toString();System.out.println(str2);
        System.out.println(str2.intern());
        System.out.println(str2 == str2.intern());
    }
}

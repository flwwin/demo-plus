package com.leven.demoplus.javase.api;

import com.leven.demoplus.enity.Student;

public enum EnumDemo {
    ONE(1, "齐国"), TWO(2, "楚国");

    private int resCode;
    private String resStr;

    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getResStr() {
        return resStr;
    }

    public void setResStr(String resStr) {
        this.resStr = resStr;
    }

    EnumDemo(int resCode, String resStr) {
        this.resCode = resCode;
        this.resStr = resStr;
    }

    public static EnumDemo forEachEnum(int ind) {
        EnumDemo[] arrs = EnumDemo.values();
        for (EnumDemo item : arrs) {
            if (item.getResCode() == ind) {
                return item;
            }
        }
        return null;
    }


    public static void main(String[] args) {
        EnumDemo enumDemo = EnumDemo.valueOf(EnumDemo.class,"ONE");
        System.out.println(enumDemo.resCode);
        System.out.println(enumDemo.resStr);

        Student student = new Student();
        student.setCountry(EnumDemo.ONE);

        String resStr = student.getCountry().getResStr();
    }
}

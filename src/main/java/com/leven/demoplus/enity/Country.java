package com.leven.demoplus.enity;


public enum Country {
    ONE(1, "齐国"),
    TWO(2, "楚国");

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

    Country(int resCode, String resStr) {
        this.resCode = resCode;
        this.resStr = resStr;
    }

    public static Country getEnumById(int ind) {
        Country[] arrs = Country.values();
        for (Country item : arrs) {
            if (item.getResCode() == ind) {
                return item;
            }
        }
        return null;
    }
}

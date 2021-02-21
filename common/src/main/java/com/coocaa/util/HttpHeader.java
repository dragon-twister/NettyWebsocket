package com.coocaa.util;

import java.util.HashMap;

/**
 *
 * 傻逼代码，每次调用都put，有空再改
 */
public class HttpHeader {

    public static  HashMap<String,String> HttpHandle(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Accept-Charset","utf-8");
        hashMap.put("cAppVersion","1000000");
        hashMap.put("cBrand","Skyworth");
        hashMap.put("Accept","application/json,text/*");
        hashMap.put("cSID","ddcf37e3-c493-4e31-8eae-201d7ce37413");
        hashMap.put("cModel","Q40");
        hashMap.put("cPkg","com.coocaa.educatephone");
        hashMap.put("cFMode","Default");
        hashMap.put("cPattern","normal");
        hashMap.put("language","zh");
        hashMap.put("cEmmcCID","11010030313647373000cb62ceaa7500");
        hashMap.put("cSkySecurity","false");
        hashMap.put("MAC","f02b8a1fef22");
        hashMap.put("aSdk","26");
        hashMap.put("headerVersion","8");
        hashMap.put("cUDID","74052520");
        // hashMap.put("userId","6adb58d1568811e9ac64525400f3186f");
        hashMap.put("cTcVersion","700190506");
        hashMap.put("cChip","8S34");
        hashMap.put("supportSource","4k");
        hashMap.put("cSize","50");
        hashMap.put("Resolution","1920x1080");
        hashMap.put("cHomepageVersion","7130034");
        hashMap.put("mobileDevelopType","mobile");

        return hashMap;
    }


    public static HashMap<String,String> HttpHandle1(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("Content-Type","application/x-www-form-urlencoded");
        hashMap.put("Accept-Encoding","application/json,text/*");
        hashMap.put("cUDID","235050");
        hashMap.put("MAC","bcec234e832e");
        hashMap.put("cModel","14K");
        hashMap.put("cChip","8S61");
        hashMap.put("cSize","40");
        hashMap.put("cResolution","1920x1080");
        hashMap.put("cTcVersion","502168194");
        hashMap.put("cFMode","Default");
        hashMap.put("vAppVersion","1000005");
        hashMap.put("cPattern","normal");
        hashMap.put("cThirdAreaId","323302");
        hashMap.put("cCA","825010219545584");
        return hashMap;

    }
}

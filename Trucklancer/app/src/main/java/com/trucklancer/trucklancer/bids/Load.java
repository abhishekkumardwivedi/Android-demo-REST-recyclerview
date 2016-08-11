package com.trucklancer.trucklancer.bids;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * [{"0":"85","id":"85",
  "1":"Satara","fromcity":"Satara",
  "2":"Chennai","tocity":"Chennai",
  "3":"42","material":"42",
  "4":"66","trucktype":"66",
  "5":"9","weight":"9",
  "6":"","sechudle":"",
  "7":"","notrucks":"",
  "8":"","summaryofpost":"",
  "9":"Transport Paper Material",
  "message":"Transport Paper Material",
  "10":"2016-07-28","spostdate":"2016-07-28",
  "11":null,"postdate":null,
  "12":"J-P-I-YBzb1UL69X","postid":"J-P-I-YBzb1UL69X",
  "13":"59","posterid":"59",
  "14":"2016-07-21","postiddate":"2016-07-21",
  "15":null,"hide_profile":null,
  "16":"0","shipping_type":"0"},
 */


public class Load {
    private String fromCity;
    private String toCity;
    private String postDate;
    private String weight;
    private Integer materialId;
    private Integer truckId;
    private String message;
    private String postId;
    private boolean hideProfile;
    private String id;

    private static Map<Integer, String> materialMap = new HashMap<Integer, String>();
    private static Map<Integer, String> truckTypeMap = new HashMap<Integer, String>();
    private static final String weightUnit = "tons";

    private bids mBids;

    public class bids {
    }

    public void addToMaterialMap(Integer mId, String material) {
        materialMap.put(mId, material);
    }

    public void addToTruckMap(Integer tid, String truck) {
        truckTypeMap.put(tid, truck);
    }

    public boolean isMaterialMapEmpty() {
        if(materialMap.size() > 0)
            return false;
        else
            return true;
    }

    public boolean isTruckTypeMapEmpty() {
        if(truckTypeMap.size() > 0)
            return false;
        else
            return true;
    }

    public String getWeightUnit() {
        return weightUnit;
    }

    public Load() {
    }

    public void setId(String uid) {
        id = uid;
    }

    public String getId() {
        return id;
    }
    public void setFromCity(String city) {
        fromCity = city;
    }

    public String getFromCity() {
        return fromCity;
    }

    public void setToCity(String city) {
        toCity = city;
    }

    public String getToCity() {
        return toCity;
    }

    public void setPostDate(String data) {
        postDate = data;
    }

    public String getPostDate() {
        return postDate;
    }

    public void setWeight(String wt) {
        weight = wt;
    }

    public String getWeight() {
        return weight;
    }

    public void setMaterialId(Integer mid) {
        materialId = mid;
    }

    public String getMaterial() {
        return materialMap.get(materialId);
    }

    public void setTruckId(Integer tid) {
        truckId = tid;
    }

    public String getTruckType() {
        return truckTypeMap.get(truckId);
    }

    public void setMessage(String msg) {
        message = msg;
    }

    public String getMessage() {
        return message;
    }

    public void setPostId(String pid) {
        postId = pid;
    }

    public String getPostId() {
        return postId;
    }

    public void setHideProfile(String hide) {
        if(hide.equals("1"))
            hideProfile = true;
        else
            hideProfile = false;
    }

    public boolean isHideProfile() {
        return hideProfile;
    }
}

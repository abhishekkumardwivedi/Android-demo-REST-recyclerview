package com.trucklancer.trucklancer.detailedbid;

/**
 * Created by abhishek on 11/8/16.
 */
public class Quote {

//[{"0":"73","id":"73","1":"98","postid":"98","2":"73","partalacid":"73","3":"59","accepterid":"59","4":"50000","price":"50000","5":"","description":"","6":"16-08-08","acceptdatetime":"16-08-08","7":"2","status":"2"},
//    {"0":"71","id":"71","1":"98","postid":"98","2":"74","partalacid":"74","3":"0","accepterid":"0","4":"30000","price":"30000","5":"","description":"","6":"16-07-27","acceptdatetime":"16-07-27","7":"0","status":"0"}]

    String postId;
    String id;
    String quoteId; //partialacid
    String acceptId;
    String price;
    String acceptDate;
    String status;
    String description;

    public Quote() {}

    public void setPostId(String d ) {
        postId = d;
    }

    public void setId(String d ) {
        id = d;
    }

    public void setQuoteId(String d ) {
        quoteId = d;
    }

    public void setAcceptId(String d ) {
        acceptId = d;
    }

    public void setPrice(String d ) {
        price = d;
    }

    public void setAcceptDate(String d ) {
        acceptDate = d;
    }

    public void setStatus(String d ) {
        status = d;
    }

    public void setDescription(String d ) {
        description = d;
    }

    public String getPostId() {
        return postId;
    }

    public String getId() {
        return id;
    }

    public String getQuoteId() {
        return quoteId;
    }

    public String getAcceptId() {
        return acceptId;
    }

    public String getPrice() {
        return price;
    }

    public String getAcceptDate() {
        return acceptDate;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}

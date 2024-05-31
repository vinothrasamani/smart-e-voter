package com.example.smart_e_voter;

public class dataholder {
    public String getMsg() {
        return msg;
    }

    private String msg, msgkey;

    public String getMsgkey() {
        return msgkey;
    }

    public void setMsgkey(String msgkey) {
        this.msgkey = msgkey;
    }

    public dataholder(String msg) {
        this.msg = msg;
    }

    public dataholder(){}
}

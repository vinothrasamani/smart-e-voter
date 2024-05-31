package com.example.smart_e_voter;

import com.google.firebase.database.IgnoreExtraProperties;
@IgnoreExtraProperties
public class dbhelper {
    private String can_name, party_name, img, cankey;
    private long votecount;

    public dbhelper() {
    }

    public String getCankey() {
        return cankey;
    }

    public void setCankey(String cankey) {
        this.cankey = cankey;
    }

    public dbhelper(String name, String party, String img, long count) {
        this.can_name = name;
        this.party_name = party;
        this.img = img;
        this.votecount = count;
    }

    public long getVotecount() {
        return votecount;
    }

    public String getCan_name() {
        return can_name;
    }

    public String getParty_name() {
        return party_name;
    }

    public String getImg() {
        return img;
    }
}

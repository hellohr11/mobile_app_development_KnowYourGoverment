package com.example.goverment;

import java.io.Serializable;

public class Official implements Serializable{
    private String title;
    private String name;
    private OfficalAddress address;
    private String party="unKnown";
    private String phone;
    private String email;
    private String url;
    private SocialMediaChannel sc;
    private String photourl;


    public Official(String t, String txt){
        title=t;
        name=txt;
    }

    String getParty(){return party;}


    String getTitle() {
        return title;
    }

    String getName() {
        return name;
    }

    OfficalAddress getAddress() {return address;}

    String getPhone(){return phone;}

    String getEmail(){return email;}

    String getPhotourl(){return photourl;}

    public void setPhotourl(String pturl){photourl=pturl;}

    public void setEmail(String e){email=e;}

    String getUrl(){return url;}

    public void setUrl(String url){this.url=url;}

    public void setSc(SocialMediaChannel sc){this.sc=sc;}

    SocialMediaChannel getSc(){return sc;}

    public void setName(String text) {
        this.name = text;
    }

    public void setTitle(String title){this.title=title;}

    public void setAddress(OfficalAddress ad){this.address=ad;}

    public void setParty(String party){this.party=party;}

    public void setPhone(String phone){this.phone=phone;}


}

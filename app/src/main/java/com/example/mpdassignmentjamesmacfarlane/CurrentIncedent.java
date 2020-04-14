package com.example.mpdassignmentjamesmacfarlane;

import android.util.Log;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CurrentIncedent implements ItemType {
    String title;
    String desc;
    String latLong;

    public CurrentIncedent(String title, String desc,String latLong){
        this.title=title;
        this.desc=desc;
        this.latLong=latLong;
    }

    public Date getStDate() {
        Date date=null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
             date = sdf.parse("13 february 2020");
        }catch(Exception e){
            Log.e("Get Start Date error",e.toString());
        }
        return date;
    }

    public Date getEnDate(){
        Date date=null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
            date = sdf.parse("13 february 2020");
        }catch(Exception e){
            Log.e("Get Start Date error",e.toString());
        }
        return date;
    }

    public String getDesc() {
        return desc;
    }

    public String getTitle() {
        return title;
    }

    public String getLatLong() {
        return latLong;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String toString() {
        return "\n\nTitle: "+title+"\nDescription: "+desc+"\nLat/Long: "+latLong;
    }
}

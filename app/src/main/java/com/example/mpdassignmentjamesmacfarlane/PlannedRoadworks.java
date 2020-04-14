package com.example.mpdassignmentjamesmacfarlane;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlannedRoadworks implements ItemType {

    String title;
    String desc;
    String latLong;
    Date startDate;
    Date endDate;
    SimpleDateFormat sdf = new SimpleDateFormat("EEEEEEEEEE dd MMMMMMMMMM yyyy");


    public PlannedRoadworks(String title, String desc,String latLong){
        this.title=title;
        this.desc=desc;
        parsin(desc);
        this.latLong=latLong;
    }


    public void parsin(String desc){
        try {

            int st = desc.indexOf("Start Date: ") + "Start Date: ".length();
            int en = desc.lastIndexOf(" - 00:00&lt;br /&gt;E");
            startDate = sdf.parse(desc.substring(st, en).replace(",",""));

            st = desc.indexOf("End Date: ") + "End Date: ".length();
            en = desc.lastIndexOf(" - 00:00&lt;br /&gt;W");
            endDate = sdf.parse(desc.substring(st, en).replace(",",""));

            st = desc.indexOf("Works") + "Works".length();
            en = desc.length();
            this.desc = desc.substring(st, en);


        }catch(ParseException e){
            Log.e("Parsing Error: ",e.toString());
        }
    }

    public Date getStDate() {
        return startDate;
    }

    public Date getEnDate(){
        return endDate;
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

    public String getSDate(){return startDate.toString();}

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

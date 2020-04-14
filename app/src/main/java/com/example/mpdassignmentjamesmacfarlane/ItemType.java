package com.example.mpdassignmentjamesmacfarlane;

import java.util.Date;

public interface ItemType {
    public String getDesc();
    public String getTitle();
    public void setDesc(String desc);
    public void setLatLong(String latLong);
    public void setTitle(String title);
    public Date getStDate();
    public Date getEnDate();
    public String toString();
    public String getLatLong();
}

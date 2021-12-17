package com.test.tmap4;

public class GPSinfo {
    private double latitude;
    private double longittude;

    public GPSinfo(double latitude, double longittude){
        this.latitude = latitude;
        this.longittude = longittude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongittude() {
        return longittude;
    }

    public void setLongittude(double longittude) {
        this.longittude = longittude;
    }
}

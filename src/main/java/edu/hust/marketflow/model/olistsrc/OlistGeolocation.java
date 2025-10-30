package edu.hust.marketflow.model.olistsrc;

public class OlistGeolocation {
    private String geolocationZipCodePrefix;
    private String geolocationLat;
    private String geolocationLng;
    private String geolocationCity;
    private String geolocationState;

    public static int getFieldCount() {
        return 5;
    }

    public static OlistGeolocation fromArray(String [] data) {
        OlistGeolocation geolocation = new OlistGeolocation();
        geolocation.geolocationZipCodePrefix = data[0];
        geolocation.geolocationLat = data[1];
        geolocation.geolocationLng = data[2];
        geolocation.geolocationCity = data[3];
        geolocation.geolocationState = data[4];
        return geolocation;
    }

    public String getGeolocationLng() {
        return geolocationLng;
    }

    public void setGeolocationLng(String geolocationLng) {
        this.geolocationLng = geolocationLng;
    }

    public String getGeolocationCity() {
        return geolocationCity;
    }

    public void setGeolocationCity(String geolocationCity) {
        this.geolocationCity = geolocationCity;
    }

    public String getGeolocationLat() {
        return geolocationLat;
    }

    public void setGeolocationLat(String geolocationLat) {
        this.geolocationLat = geolocationLat;
    }

    public String getGeolocationState() {
        return geolocationState;
    }

    public void setGeolocationState(String geolocationState) {
        this.geolocationState = geolocationState;
    }

    public String getGeolocationZipCodePrefix() {
        return geolocationZipCodePrefix;
    }

    public void setGeolocationZipCodePrefix(String geolocationZipCodePrefix) {
        this.geolocationZipCodePrefix = geolocationZipCodePrefix;
    }
}

package edu.hust.marketflow.model.olistsrc;

public class OlistGeolocation {
    private String geolocationZipCodePrefix;
    private String geolocationLat;
    private String geolocationLng;
    private String geolocationCity;
    private String geolocationState;

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

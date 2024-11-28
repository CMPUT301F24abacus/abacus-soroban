package com.example.soroban.fragment;

public interface GeolocationListener {
    void setLocation(double latitude, double longitude);
    void returnResult(boolean result);
}

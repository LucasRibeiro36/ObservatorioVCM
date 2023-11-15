package com.ifba.observatoriovcm.utils;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;

import org.osmdroid.bonuspack.location.GeocoderNominatim;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class GeocodeFromAddressTask extends AsyncTask<Void, Void, List<Double>> {
    private WeakReference<Context> contextRef;
    private String address;
    private ProjectHelper.LocationCallback callback;

    GeocodeFromAddressTask(Context context, String address, ProjectHelper.LocationCallback callback) {
        contextRef = new WeakReference<>(context);
        this.address = address;
        this.callback = callback;
    }

    @Override
    protected List<Double> doInBackground(Void... params) {
        Context context = contextRef.get();
        if (context == null) {
            return null; // O contexto foi liberado, não podemos continuar
        }

        try {
            GeocoderNominatim geocoderNominatim = new GeocoderNominatim("ObservatorioVCM");
            List<Address> addresses = geocoderNominatim.getFromLocationName(address, 1);
            if (!addresses.isEmpty()) {
                Address firstAddress = addresses.get(0);
                double latitude = firstAddress.getLatitude();
                double longitude = firstAddress.getLongitude();

                List<Double> location = new ArrayList<>();
                location.add(latitude);
                location.add(longitude);

                return location;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    @Override
    protected void onPostExecute(List<Double> location) {
        if (location != null) {
            callback.onLocationResult(location);
        } else {
            callback.onLocationResult(null);
        }
    }
}
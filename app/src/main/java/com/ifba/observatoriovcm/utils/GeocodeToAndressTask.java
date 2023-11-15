package com.ifba.observatoriovcm.utils;

import android.content.Context;
import android.location.Address;
import android.os.AsyncTask;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.List;

public class GeocodeToAndressTask extends AsyncTask<Void, Void, String> {
    private Context context;
    private IGeoPoint geoPoint;
    private ProjectHelper.AndressCallback callback;

    public GeocodeToAndressTask(Context context, IGeoPoint geoPoint, ProjectHelper.AndressCallback callback) {
        this.context = context;
        this.geoPoint = geoPoint;
        this.callback = callback;
    }


    @Override
    protected String doInBackground(Void... voids) {
        try {
            GeocoderNominatim geocoderNominatim = new GeocoderNominatim("ObservatorioVCM");
            List<Address> addresses = geocoderNominatim.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);

            if (!addresses.isEmpty()) {
                Address firstAddress = addresses.get(0);

                // Obtém o endereço completo linha por linha
                StringBuilder fullAddress = new StringBuilder();
                for (int i = 0; i <= firstAddress.getMaxAddressLineIndex(); i++) {
                    fullAddress.append(firstAddress.getAddressLine(i)).append(", ");
                }

                return fullAddress.toString();
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    @Override
    protected void onPostExecute(String address) {
        if (address != null) {
            callback.onAddressResult(address);
        } else {
            callback.onAddressResult("Endereço não encontrado");
        }
    }
}

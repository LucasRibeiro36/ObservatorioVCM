package com.ifba.observatoriovcm.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class ProjectHelper {
    private Context context;
    public ProjectHelper(Context context) {
        this.context = context;
    }

    static public String getDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        return sdf.format(date);
    }

    public void getLocation(final LocationCallback callback) {
        // Check if location permission is granted
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Location permission is granted, you can proceed to access the location.

            FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            Task<Location> locationTask = locationProviderClient.getCurrentLocation(100, null);

            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location lastLocation) {
                    if (lastLocation != null) {
                        double latitude = lastLocation.getLatitude();
                        double longitude = lastLocation.getLongitude();

                        List<Double> location = new ArrayList<>();
                        location.add(latitude);
                        location.add(longitude);

                        callback.onLocationResult(location);
                    }
                }
            });
        } else {
            // Location permission is not granted. You should request it from the user.
            // You can display a permission request dialog or request it through the Android permission system.
            // Handle the permission request and its result here.
        }
    }

    public interface LocationCallback {
        void onLocationResult(List<Double> location);
    }

    public void getAndress(final AndressCallback callback) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(context);
            Task<Location> locationTask = locationProviderClient.getCurrentLocation(100, null);

            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location lastLocation) {
                    if (lastLocation != null) {
                        double latitude = lastLocation.getLatitude();
                        double longitude = lastLocation.getLongitude();

                        // Agora, você pode chamar um serviço de geocodificação para obter o endereço.
                        // Aqui está um exemplo de como usar a geocodificação do Google.
                        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                            if (!addresses.isEmpty()) {
                                String addressLine = addresses.get(0).getAddressLine(0); // Obtém a primeira linha de endereço
                                callback.onAddressResult(addressLine);
                            } else {
                                callback.onAddressResult("Endereço não encontrado");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            callback.onAddressResult("Erro ao obter o endereço");
                        }
                    }
                }
            });
        } else {
            // Lida com o caso em que a permissão de localização não foi concedida.
            // Você pode solicitar a permissão ou tratar de acordo com sua lógica.
        }
    }

    public interface AndressCallback {
        void onAddressResult(String address);
    }

    public NetworkInfo getNetworkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }

    public void getGeocodeFromAndress(String andress, final LocationCallback callback) throws IOException {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocationName(andress, 1);

            if (!addresses.isEmpty()) {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();

                List<Double> location = new ArrayList<>();
                location.add(latitude);
                location.add(longitude);

                callback.onLocationResult(location);
            } else {
                callback.onLocationResult(null);
            }
        } catch (IOException e) {
            e.printStackTrace();
            callback.onLocationResult(null);
        }

    }

}

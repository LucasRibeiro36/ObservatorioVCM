package com.ifba.observatoriovcm.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;

import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import org.osmdroid.bonuspack.location.GeocoderNominatim;
import org.osmdroid.util.GeoPoint;


public class ProjectHelper {
    private Context context;
    public ProjectHelper(Context context) {
        this.context = context;
    }

    static public String getDate() {
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
        TimeZone timeZone = TimeZone.getTimeZone("America/Sao_Paulo");
        sdf.setTimeZone(timeZone); // Configura o fuso horário para o horário brasileiro
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

                        // Realize a geocodificação reversa para obter o endereço a partir das coordenadas
                        GeoPoint geoPoint = new GeoPoint(latitude, longitude);
                        GeocodeToAndressTask reverseGeocodingTask = new GeocodeToAndressTask(context, geoPoint, callback);
                        reverseGeocodingTask.execute();
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

    public void getGeocodeFromAddress(final String address, final LocationCallback callback) {
        new GeocodeFromAddressTask(context, address, callback).execute();
    }

}

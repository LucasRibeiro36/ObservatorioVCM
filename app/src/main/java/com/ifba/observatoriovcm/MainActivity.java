package com.ifba.observatoriovcm;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ifba.observatoriovcm.dao.DenunciaDao;
import com.ifba.observatoriovcm.model.DenunciaModel;
import com.ifba.observatoriovcm.utils.ProjectHelper;
import com.ifba.observatoriovcm.utils.SpinnerHelper;
import com.ifba.observatoriovcm.view.MapsActivity;
import com.ifba.observatoriovcm.view.NewDenunciaActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button buttonDenunciar, buttonvVerDenuncias, buttonDenunciarOutroLocal;
    private Spinner spinnerTipoDenuncias;
    private String descricao;
    private LocationManager locationManager;
    private ProjectHelper projectUtils;
    private DenunciaDao denunciaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActivityCompat.requestPermissions( MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_NETWORK_STATE}, 1);
        // verificar se tem conexão com a internet
        projectUtils = new ProjectHelper(MainActivity.this);
        NetworkInfo networkInfo = projectUtils.getNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Sem conexão com a internet");
            builder.setMessage("Você precisa estar conectado à internet para usar o aplicativo");
            builder.setPositiveButton("OK", null);
            builder.show();
        }
        buttonDenunciar = (Button) findViewById(R.id.buttonDenunciar);
        buttonvVerDenuncias = (Button) findViewById(R.id.buttonVerDenuncias);
        buttonDenunciarOutroLocal = (Button) findViewById(R.id.buttonDenunciarOutroLocal);
        denunciaDao = new DenunciaDao();

        // Set up the spinner and its selection listener
        spinnerTipoDenuncias = (Spinner) findViewById(R.id.spinnerTipoDenuncia);
        String[] tiposDeDenuncia = {"Assédio", "Agressão", "Outro Tipo de Denúncia"};
        spinnerTipoDenuncias = SpinnerHelper.createAndPopulateSpinner(MainActivity.this, spinnerTipoDenuncias, tiposDeDenuncia);
        spinnerTipoDenuncias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                String itemSelecionado = tiposDeDenuncia[position];
                descricao = itemSelecionado;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        buttonDenunciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Request location and handle the result in a callback
                projectUtils.getLocation(new ProjectHelper.LocationCallback() {
                    @Override
                    public void onLocationResult(List<Double> location) {
                        if (location != null && location.size() == 2) {
                            double latitude = location.get(0);
                            double longitude = location.get(1);
                            List<Double> locations = new ArrayList<>();
                            locations.add(latitude);
                            locations.add(longitude);
                            String time = ProjectHelper.getDate();
                            String situacao = "Em Aberto";
                            projectUtils.getAndress(new ProjectHelper.AndressCallback() {
                                @Override
                                public void onAddressResult(String address) {
                                    if (address != null) {
                                        String endereco = address;
                                        String tipo = "Denuncia por GPS";
                                        System.out.println("endereco: " + endereco + " descricao: " + descricao + " time: " + time + " situacao: " + situacao + " locations: " + locations);
                                        DenunciaModel denunciaModel = new DenunciaModel(tipo, time, locations, situacao, endereco, descricao);
                                        denunciaDao.adicionarDenuncia(denunciaModel);
                                        Toast.makeText(MainActivity.this, "Denúncia enviada com sucesso!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            }
                    }
                });
            }
        });

        buttonvVerDenuncias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                startActivity(intent);
            }
        });

        buttonDenunciarOutroLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, NewDenunciaActivity.class);
                startActivity(intent);
            }
        });
    }
}

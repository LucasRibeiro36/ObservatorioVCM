package com.ifba.observatoriovcm;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.ifba.observatoriovcm.R;
import com.ifba.observatoriovcm.dao.DenunciaDao;
import com.ifba.observatoriovcm.model.DenunciaModel;
import com.ifba.observatoriovcm.utils.ProjectUtils;
import com.ifba.observatoriovcm.utils.SpinnerHelper;
import com.ifba.observatoriovcm.view.MapsActivity;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button buttonDenunciar, buttonvVerDenuncias;
    private Spinner spinnerTipoDenuncias;
    private String descricao;
    private LocationManager locationManager;
    private ProjectUtils projectUtils;
    private DenunciaDao denunciaDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        projectUtils = new ProjectUtils(MainActivity.this);
        buttonDenunciar = (Button) findViewById(R.id.buttonDenunciar);
        buttonvVerDenuncias = (Button) findViewById(R.id.buttonVerDenuncias);
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
                projectUtils.getLocation(new ProjectUtils.LocationCallback() {
                    @Override
                    public void onLocationResult(List<Double> location) {
                        if (location != null && location.size() == 2) {
                            double latitude = location.get(0);
                            double longitude = location.get(1);
                            List<Double> locations = new ArrayList<>();
                            locations.add(latitude);
                            locations.add(longitude);
                            String time = ProjectUtils.getDate();
                            String situacao = "Em Aberto";
                            projectUtils.getAndress(new ProjectUtils.AndressCallback() {
                                @Override
                                public void onAddressResult(String address) {
                                    if (address != null) {
                                        String endereco = address;
                                        DenunciaModel denunciaModel = new DenunciaModel(descricao, time, locations, situacao, endereco);
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
                denunciaDao.listarDenuncias(new DenunciaDao.DenunciasCallback() {
                    @Override
                    public void onDenunciasRetrieved(List<DenunciaModel> denuncias) {
                        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                        intent.putExtra("denuncias", (ArrayList<DenunciaModel>) denuncias);
                        startActivity(intent);
                    }

                    @Override
                    public void onDenunciasRetrievalError(Exception error) {

                    }
                });
                //Intent intent = new Intent(MainActivity.this, MapsActivity.class);
                //startActivity(intent);
            }
        });
    }
}

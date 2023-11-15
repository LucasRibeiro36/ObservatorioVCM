package com.ifba.observatoriovcm.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.ifba.observatoriovcm.R;
import com.ifba.observatoriovcm.dao.DenunciaDao;
import com.ifba.observatoriovcm.model.DenuncianteModel;
import com.ifba.observatoriovcm.utils.ProjectHelper;
import com.ifba.observatoriovcm.utils.SpinnerHelper;

import java.io.IOException;
import java.util.List;
import java.util.Random;

public class NewDenunciaActivity extends AppCompatActivity {
    Button voltarButton, enviarButton;
    EditText editTextTextPostalAddress;
    ProjectHelper projectHelper = new ProjectHelper(this);
    DenunciaDao denunciaDao = new DenunciaDao();
    Spinner spinnerTipoDenuncia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newdenuncia);
        editTextTextPostalAddress = (EditText) findViewById(R.id.editTextTextPostalAddress);
        enviarButton =  (Button) findViewById(R.id.enviarButton);
        voltarButton = (Button) findViewById(R.id.buttonVoltar);
        spinnerTipoDenuncia = (Spinner) findViewById(R.id.spinnerTipoDenuncias);
        String[] tiposDeDenuncia = {"Assédio", "Agressão", "Outro Tipo de Denúncia"};
        spinnerTipoDenuncia = SpinnerHelper.createAndPopulateSpinner(this, spinnerTipoDenuncia, tiposDeDenuncia);
        voltarButton.setOnClickListener(v -> {
            finish();
        });

        enviarButton.setOnClickListener(v -> {
            String endereco = editTextTextPostalAddress.getText().toString();
            if (endereco.isEmpty()) {
                editTextTextPostalAddress.setError("Campo obrigatório");
                return;
            } else {
                projectHelper.getGeocodeFromAddress(endereco, new ProjectHelper.LocationCallback() {
                    @Override
                    public void onLocationResult(List<Double> location) {
                        DenuncianteModel denuncianteModel = new DenuncianteModel();
                        denuncianteModel.setEndereco(endereco);
                        location.set(0, location.get(0) + (new Random().nextDouble() * 2 - 1) * 0.0001);
                        location.set(1, location.get(1) + (new Random().nextDouble() * 2 - 1) * 0.0001);
                        denuncianteModel.setLocation(location);
                        denuncianteModel.setTimestamp(projectHelper.getDate());
                        denuncianteModel.setTipo("Denuncia por Texto");
                        denuncianteModel.setDescricao(spinnerTipoDenuncia.getSelectedItem().toString());
                        denuncianteModel.setSituacao("Em andamento");
                        denunciaDao.adicionarDenuncia(denuncianteModel);
                        finish();
                    }
                });
            }
        });
    }

}
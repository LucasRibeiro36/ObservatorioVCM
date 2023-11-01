package com.ifba.observatoriovcm.model;

import java.io.Serializable;
import java.util.List;

public class DenuncianteModel extends DenunciaModel implements Serializable {

    private List<Double> locationDenunciante;

    public DenuncianteModel(String tipo, String timestamp, List<Double> location, String situacao, String endereco, List<Double> locationDenunciante) {
        super(tipo, timestamp, location, situacao, endereco);
        this.locationDenunciante = locationDenunciante;
    }

    public DenuncianteModel(List<Double> locationDenunciante) {
        this.locationDenunciante = locationDenunciante;
    }

    public DenuncianteModel(){

    }

    public List<Double> getLocationDenunciante() {
        return locationDenunciante;
    }

    public void setLocationDenunciante(List<Double> locationDenunciante) {
        this.locationDenunciante = locationDenunciante;
    }
}

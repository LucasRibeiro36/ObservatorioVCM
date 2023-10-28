package com.ifba.observatoriovcm.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.List;

@IgnoreExtraProperties
public class DenunciaModel implements Serializable {
    private String tipo;
    private String timestamp;
    private List<Double> location;
    private String situacao;
    private String endereco;

    public DenunciaModel(String tipo, String timestamp, List<Double> location, String situacao, String endereco) {
        this.tipo = tipo;
        this.timestamp = timestamp;
        this.location = location;
        this.situacao = situacao;
        this.endereco = endereco;
    }

    public DenunciaModel() {
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}

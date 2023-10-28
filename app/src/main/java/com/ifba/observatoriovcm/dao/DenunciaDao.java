package com.ifba.observatoriovcm.dao;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ifba.observatoriovcm.model.DenunciaModel;

import java.util.ArrayList;
import java.util.List;


public class DenunciaDao {
    private DatabaseReference mDatabase;

    public DenunciaDao() {
        // Inicializa o Firebase Realtime Database
        mDatabase = FirebaseDatabase.getInstance("https://observarioviolenciamulher-default-rtdb.firebaseio.com/").getReference("dados");
    }

    // Adicionar uma denúncia
    public void adicionarDenuncia(DenunciaModel denuncia) {
        String key = mDatabase.push().getKey();
        mDatabase.child(key).setValue(denuncia);
    }

    // Atualizar uma denúncia
    public void atualizarDenuncia(String denunciaKey, DenunciaModel denuncia) {
        mDatabase.child(denunciaKey).setValue(denuncia);
    }

    // Excluir uma denúncia
    public void excluirDenuncia(String denunciaKey) {
        mDatabase.child(denunciaKey).removeValue();
    }

    public void listarDenuncias(final DenunciasCallback callback) {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<DenunciaModel> denuncias = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    DenunciaModel denuncia = childSnapshot.getValue(DenunciaModel.class);
                    denuncias.add(denuncia);
                }
                callback.onDenunciasRetrieved(denuncias);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors here
                callback.onDenunciasRetrievalError(databaseError.toException());
            }
        });
    }

    public interface DenunciasCallback {
        void onDenunciasRetrieved(List<DenunciaModel> denuncias);
        void onDenunciasRetrievalError(Exception error);
    }


}


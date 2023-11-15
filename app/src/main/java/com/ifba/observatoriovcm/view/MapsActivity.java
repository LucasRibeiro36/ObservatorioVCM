package com.ifba.observatoriovcm.view;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;

import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import com.ifba.observatoriovcm.R;
import com.ifba.observatoriovcm.dao.DenunciaDao;
import com.ifba.observatoriovcm.model.DenunciaModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity {

    private MapView map;
    private DenunciaDao denunciaDao = new DenunciaDao();
    private Timer updateTimer;
    private static final long UPDATE_INTERVAL = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Inicialize a configuração do osmdroid (deve ser feito antes de criar o mapa)
        Configuration.getInstance().load(this, getPreferences(MODE_PRIVATE));

        // Obtenha a referência ao MapView a partir do layout
        map = findViewById(R.id.map);
        map.setTileSource(org.osmdroid.tileprovider.tilesource.TileSourceFactory.MAPNIK);

        atualizarMapa();
        // Define a visualização do mapa para uma localização específica e aplica zoom
        GeoPoint irece = new GeoPoint(-11.3042, -41.8558); // Coordenadas de Irecê
        MapController mapController = (MapController) map.getController();
        mapController.setCenter(irece);
        mapController.setZoom(8.0);
        map.setMultiTouchControls(true); // Permite zoom com dois dedos
        map.setBuiltInZoomControls(true); // Permite zoom com botões
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
        updateTimer = new Timer();
        updateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                atualizarMapa();
            }
        }, 0, UPDATE_INTERVAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
        if (updateTimer != null) {
            updateTimer.cancel();
        }
    }

    public void atualizarMapa(){
        denunciaDao.listarDenuncias(new DenunciaDao.DenunciasCallback() {
            @Override
            public void onDenunciasRetrieved(List<DenunciaModel> denuncias) {
                for (DenunciaModel denuncia : denuncias) {
                    if (denuncia.getLocation() != null && denuncia.getTipo() != null) {
                        GeoPoint location = new GeoPoint(denuncia.getLocation().get(0), denuncia.getLocation().get(1));
                        Marker marker = new Marker(map);
                        marker.setPosition(location);
                        marker.setTitle(denuncia.getEndereco());
                        marker.setSubDescription(denuncia.getDescricao() + " - " + denuncia.getTipo() + " - " + denuncia.getSituacao() + " - " + denuncia.getTimestamp());
                        map.getOverlays().add(marker);
                    }
                }
            }

            @Override
            public void onDenunciasRetrievalError(Exception error) {

            }
        });
    }
}

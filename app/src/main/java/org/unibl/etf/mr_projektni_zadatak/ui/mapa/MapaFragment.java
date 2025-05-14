package org.unibl.etf.mr_projektni_zadatak.ui.mapa;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.unibl.etf.mr_projektni_zadatak.MainActivity;
import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.adapters.OsnovnoSredstvoAdapter;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;
import org.unibl.etf.mr_projektni_zadatak.ui.lokacije.LokacijeViewModel;
import org.unibl.etf.mr_projektni_zadatak.ui.osnovna_sredstva.OsnovnaSredstvaViewModel;
import org.unibl.etf.mr_projektni_zadatak.ui.zaposleni.ZaposleniViewModel;

import java.util.ArrayList;

public class MapaFragment extends Fragment {
    public static final String LOKACIJA_ID = "lokacijaId";
    int lokacijaId;
    LokacijeViewModel lokacijeViewModel;
    ZaposleniViewModel zaposleniViewModel;
    OsnovnaSredstvaViewModel osnovnaSredstvaViewModel;
    private GoogleMap mMap;
    private View root;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(LOKACIJA_ID)) {
            lokacijaId = getArguments().getInt(LOKACIJA_ID);
        }

//        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
//                NavController navController = Navigation.findNavController(requireView());
//                navController.navigateUp();
                Navigation.findNavController(requireView()).popBackStack();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        lokacijeViewModel = new ViewModelProvider(this).get(LokacijeViewModel.class);
        zaposleniViewModel = new ViewModelProvider(this).get(ZaposleniViewModel.class);
        osnovnaSredstvaViewModel = new ViewModelProvider(this).get(OsnovnaSredstvaViewModel.class);
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            lokacijeViewModel.getLokacijaById(lokacijaId).observe(getViewLifecycleOwner(), lokacija -> {
                if(lokacija != null) {
                    LatLng lokacijaLatLng = new LatLng(lokacija.getGeografskaSirina(), lokacija.getGeografskaDuzina());
                    mMap.addMarker(new MarkerOptions().position(lokacijaLatLng).title(lokacija.getGrad()));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lokacijaLatLng, 10));
                }
            });

            mMap.setOnMarkerClickListener(marker -> {
                prikaziOsnovnaSredstvaBottomSheet(lokacijaId);
                return true;
            });
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_mapa, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    private void prikaziOsnovnaSredstvaBottomSheet(int locationId) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet_osnovna_sredstva, null);
        bottomSheetDialog.setContentView(bottomSheetView);

        RecyclerView recyclerView = bottomSheetView.findViewById(R.id.recyclerView);
        TextView title = bottomSheetView.findViewById(R.id.title);
        Button btnClose = bottomSheetView.findViewById(R.id.btnClose);

        OsnovnoSredstvoAdapter adapter = new OsnovnoSredstvoAdapter(new ArrayList<>(), requireContext(), new OsnovnoSredstvoAdapter.OnItemClickListener() {
            @Override
            public void onViseClick(OsnovnoSredstvo osnovnoSredstvo) {
                prikaziToastPoruku(osnovnoSredstvo);
            }
        });
        recyclerView.setAdapter(adapter);

        osnovnaSredstvaViewModel.getOsnovnaSredstvaByLokacijaId(locationId)
                .observe(getViewLifecycleOwner(), sredstva -> {
                    if (sredstva != null && !sredstva.isEmpty()) {
                        String os = getString(R.string.osnovna_sredstva);
                        String naslov = os + " (" + sredstva.size() + ")";
                        title.setText(naslov);
                        adapter.setFiltriranaOsnovnaSredstva(sredstva);
                    } else {
                        String naslov = getString(R.string.osnovna_sredstva);
                        title.setText(naslov);
                    }
                });

        btnClose.setOnClickListener(v -> bottomSheetDialog.dismiss());

        bottomSheetDialog.show();

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior.from(bottomSheet).setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        });
    }

    private void prikaziToastPoruku(OsnovnoSredstvo osnovnoSredstvo) {
        String barkod = getString(R.string.barkod);
        String barkodText = barkod + "\n" + osnovnoSredstvo.getBarkod();

        int zaposleniId = osnovnoSredstvo.getZaposleniId();

        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, null);
        TextView text = layout.findViewById(R.id.custom_toast_text);

        zaposleniViewModel.getZaposleniById(zaposleniId).observe(getViewLifecycleOwner(), zaposleni -> {
            if(zaposleni != null) {
                String zaduzenaOsoba = getString(R.string.zaduzena_osoba);
                String zaduzenaOsobaText = zaduzenaOsoba + "\n" + zaposleni.getIme() + " " + zaposleni.getPrezime();
                text.setText(barkodText + "\n\n" + zaduzenaOsobaText);

                Toast toast = new Toast(requireContext());
                toast.setDuration(Toast.LENGTH_SHORT);
                toast.setView(layout);
                toast.show();
            }
        });
    }
}
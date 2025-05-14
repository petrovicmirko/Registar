package org.unibl.etf.mr_projektni_zadatak.ui.stavka;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.unibl.etf.mr_projektni_zadatak.MainActivity;
import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.adapters.PopisnaListaAdapter;
import org.unibl.etf.mr_projektni_zadatak.adapters.StavkaAdapter;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.PopisnaLista;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Stavka;
import org.unibl.etf.mr_projektni_zadatak.ui.osnovna_sredstva.OsnovnaSredstvaViewModel;
import org.unibl.etf.mr_projektni_zadatak.ui.popisne_liste.PopisneListeViewModel;

import java.util.ArrayList;
import java.util.List;

public class StavkeFragment extends Fragment {
    public static final String POPISNA_LISTA_ID = "popisnaListaId";
    public static final String STAVKA_ID = "stavkaId";
    public static final String BARKOD = "barkod";
    private int popisnaListaId;
    private List<Stavka> stavke;
    private StavkeViewModel stavkeViewModel;
    private RecyclerView recyclerView;
    private StavkaAdapter adapter;
    private String barkod;
    private View root;

    private OsnovnaSredstvaViewModel osnovnaSredstvaViewModel;

    public static StavkeFragment newInstance() {
        return new StavkeFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            popisnaListaId = getArguments().getInt(POPISNA_LISTA_ID, -1);
        }

        //((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
//                NavController navController = Navigation.findNavController(requireView());
//                navController.navigateUp();
                Navigation.findNavController(requireView()).popBackStack();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_stavke, container, false);

        stavke = new ArrayList<>();
        osnovnaSredstvaViewModel = new ViewModelProvider(this).get(OsnovnaSredstvaViewModel.class);

        root.findViewById(R.id.fab_dodaj_stavku).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pokreniSkener();
                //Toast.makeText(requireContext(), barkod, Toast.LENGTH_SHORT).show();
            }
        });

        adapter = new StavkaAdapter(stavke, osnovnaSredstvaViewModel, requireContext(),this, new StavkaAdapter.OnItemClickListener() {
            @Override
            public void onViseClick(Stavka stavka) {
                setAlertDialog(stavka);
            }
        });

        recyclerView = root.findViewById(R.id.rv_stavke);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        stavkeViewModel = new ViewModelProvider(this).get(StavkeViewModel.class);
        stavkeViewModel.getStavkeByPopisnaListaId(popisnaListaId).observe(getViewLifecycleOwner(), stavke1 -> {
            stavke.clear();
            stavke.addAll(stavke1);
            adapter.notifyDataSetChanged();
        });

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    public void setAlertDialog(Stavka stavka) {
        String odaberiAkciju = getString(R.string.odaberi_akciju);
        String azurirajAkcija = getString(R.string.azuriraj_akcija);
        String obrisiAkcija = getString(R.string.obrisi_akcija);
        String potvrdaBrisanja = getString(R.string.potvrda_brisanja);
        String potvrdaBrisanjaPitanje = getString(R.string.potvrda_brisanja_pitanje);
        String da = getString(R.string.da);
        String ne = getString(R.string.ne);
        String uspjesnoObrisano = getString(R.string.uspjesno_obrisano);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle(odaberiAkciju);
//        builder.setMessage("Šta želiš učiniti sa stavkom?");

        builder.setPositiveButton(azurirajAkcija, (dialog, which) -> {
            Bundle bundle = new Bundle();
            bundle.putInt(STAVKA_ID, stavka.getId());
            bundle.putInt(POPISNA_LISTA_ID, stavka.getPopisnaListaId());
            Navigation.findNavController(requireView()).navigate(R.id.action_nav_stavke_to_nav_dodaj_stavku, bundle);
        });

        builder.setNeutralButton(obrisiAkcija, (dialog, which) -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle(potvrdaBrisanja)
                    .setMessage(potvrdaBrisanjaPitanje)
                    .setPositiveButton(da, (dialog1, which1) -> {
                        stavkeViewModel.delete(stavka);
                        stavke.remove(stavka);
                        adapter.setFiltriraneStavke(stavke);
                        Toast.makeText(requireContext(), uspjesnoObrisano, Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(ne, null)
                    .show();
        });

        builder.show();
    }

    private void pokreniSkener() {
        String skeniraj = getString(R.string.skeniraj_kod);
        ScanOptions options = new ScanOptions();
        options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES);
        options.setPrompt(skeniraj);
        options.setCameraId(0);
        options.setBeepEnabled(false);
        options.setBarcodeImageEnabled(true);
        barcodeLauncher.launch(options);
    }

    private final ActivityResultLauncher<ScanOptions> barcodeLauncher = registerForActivityResult(
            new ScanContract(),
            result -> {
                if(result.getContents() != null) {
                    barkod = result.getContents();

                    new Thread(() -> {
                        OsnovnoSredstvo osnovnoSredstvo = osnovnaSredstvaViewModel.checkBarkod(barkod);

                        requireActivity().runOnUiThread(() -> {
                            if(osnovnoSredstvo != null && !postojiUStavkama(osnovnoSredstvo.getId())) {
                                Bundle bundle = new Bundle();
                                bundle.putString(BARKOD, barkod);
                                bundle.putInt(POPISNA_LISTA_ID, popisnaListaId);
                                Navigation.findNavController(requireView())
                                        .navigate(R.id.action_nav_stavke_to_nav_dodaj_stavku, bundle);
                            } else {
                                String osnovnoSredstvoPostoji = getString(R.string.osnovno_sredstvo_postoji);
                                String ok = getString(R.string.ok);
                                Snackbar.make(requireView(),
                                                osnovnoSredstvoPostoji,
                                                Snackbar.LENGTH_LONG)
                                        .setAction(ok, v -> {})
                                        .show();
                            }
                        });
                    }).start();
                }
            });

    private boolean postojiUStavkama(int osnovnoSredstvoId) {
        for(Stavka stavka : stavke) {
            if(stavka.getOsnovnoSredstvoId() == osnovnoSredstvoId) {
                return true;
            }
        }
        return false;
    }
}
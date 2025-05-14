package org.unibl.etf.mr_projektni_zadatak.ui.dodavanje.stavka;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Osoba;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Stavka;
import org.unibl.etf.mr_projektni_zadatak.ui.lokacije.LokacijeViewModel;
import org.unibl.etf.mr_projektni_zadatak.ui.osnovna_sredstva.OsnovnaSredstvaViewModel;
import org.unibl.etf.mr_projektni_zadatak.ui.stavka.StavkeViewModel;
import org.unibl.etf.mr_projektni_zadatak.ui.zaposleni.ZaposleniViewModel;

import java.util.ArrayList;
import java.util.List;

public class DodajStavkuFragment extends Fragment {
    public static final String BARKOD = "barkod";
    public static final String STAVKA_ID = "stavkaId";
    public static final String POPISNA_LISTA_ID = "popisnaListaId";
    private TextView tvBarkod, tvTrenutnaOsoba, tvTrenutnaLokacija;
    private Spinner spinnerNovaOsoba, spinnerNovaLokacija;
    private Button btnSacuvaj;
    private int popisnaListaId;
    private OsnovnoSredstvo osnovnoSredstvo;
    private Stavka stavka;
    private List<Integer> lokacijeIds = new ArrayList<>();
    private List<String> gradovi = new ArrayList<>();
    private List<Integer> zaposleniIds = new ArrayList<>();
    private List<String> zaposleni = new ArrayList<>();
    private StavkeViewModel stavkeViewModel;
    private LokacijeViewModel lokacijeViewModel;
    private ZaposleniViewModel zaposleniViewModel;
    OsnovnaSredstvaViewModel osnovnaSredstvaViewModel;

    private View root;

    public static DodajStavkuFragment newInstance() {
        return new DodajStavkuFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dodaj_stavku, container, false);

        tvBarkod = root.findViewById(R.id.tv_barkod_stavka);
        tvTrenutnaOsoba = root.findViewById(R.id.tv_trenutna_osoba);
        tvTrenutnaLokacija = root.findViewById(R.id.tv_trenutna_lokacija);
        spinnerNovaOsoba = root.findViewById(R.id.spinner_nova_zaduzena_osoba);
        spinnerNovaLokacija = root.findViewById(R.id.spinner_nova_lokacija);

        btnSacuvaj = root.findViewById(R.id.btn_sacuvaj_stavku);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        osnovnaSredstvaViewModel = new ViewModelProvider(this).get(OsnovnaSredstvaViewModel.class);
        stavkeViewModel = new ViewModelProvider(this).get(StavkeViewModel.class);

        if (getArguments() != null && getArguments().containsKey(BARKOD)) {
            String barkodString = getArguments().getString(BARKOD);
            popisnaListaId = getArguments().getInt(POPISNA_LISTA_ID);
            long barkod = Long.parseLong(barkodString);

            osnovnaSredstvaViewModel.getOsnovnoSredstvoByBarkod(barkod).observe(getViewLifecycleOwner(), osnovnoSredstvo -> {
                if(osnovnoSredstvo != null) {
                    this.osnovnoSredstvo = osnovnoSredstvo;
                    tvBarkod.setText(barkodString);

                    setViewModels();

                    btnSacuvajSetOnClickListener(osnovnoSredstvo);
                }
            });
        } else if(getArguments() != null && getArguments().containsKey(STAVKA_ID)) {
            int stavkaId = getArguments().getInt(STAVKA_ID);
            popisnaListaId = getArguments().getInt(POPISNA_LISTA_ID);
            stavkeViewModel.getStavkaById(stavkaId).observe(getViewLifecycleOwner(), stavka -> {
                if(stavka != null) {
                    this.stavka = stavka;
                    osnovnaSredstvaViewModel.getOsnovnoSredstvoById(stavka.getOsnovnoSredstvoId()).observe(getViewLifecycleOwner(), osnovnoSredstvo -> {
                        if(osnovnoSredstvo != null) {
                            this.osnovnoSredstvo = osnovnoSredstvo;
                            String barkod = String.valueOf(osnovnoSredstvo.getBarkod());

                            tvBarkod.setText(barkod);

                            setViewModels();

                            btnSacuvajSetOnClickListener(osnovnoSredstvo);
                        }
                    });
                }
            });
        }
    }

    private void btnSacuvajSetOnClickListener(OsnovnoSredstvo osnovnoSredstvo) {
        btnSacuvaj.setOnClickListener(v -> {
            int osnovnoSredstvoId = osnovnoSredstvo.getId();
            int trenutnaOsobaId = osnovnoSredstvo.getZaposleniId();
            int trenutnaLokacijaId = osnovnoSredstvo.getLokacijaId();

            int selectedNovaOsobaPosition = spinnerNovaOsoba.getSelectedItemPosition();
            int selectedNovaLokacijaPosition = spinnerNovaLokacija.getSelectedItemPosition();

            if(selectedNovaOsobaPosition == 0) {
                selectedNovaOsobaPosition = trenutnaOsobaId;
            }

            if(selectedNovaLokacijaPosition == 0) {
                selectedNovaLokacijaPosition = trenutnaLokacijaId;
            }

            if(trenutnaOsobaId != selectedNovaOsobaPosition) {
                osnovnoSredstvo.setZaposleniId(selectedNovaOsobaPosition);
                osnovnaSredstvaViewModel.update(osnovnoSredstvo);
            }

            if(trenutnaLokacijaId != selectedNovaLokacijaPosition) {
                osnovnoSredstvo.setLokacijaId(selectedNovaLokacijaPosition);
                osnovnaSredstvaViewModel.update(osnovnoSredstvo);
            }

            Stavka stavka = new Stavka(osnovnoSredstvoId, trenutnaOsobaId, selectedNovaOsobaPosition,
                                        trenutnaLokacijaId, selectedNovaLokacijaPosition, popisnaListaId);

            sacuvajStavku(stavka);

            Navigation.findNavController(requireView()).popBackStack();
        });
    }

    private void sacuvajStavku(Stavka stavka) {
        if (getArguments() != null && getArguments().containsKey(STAVKA_ID)) {
            int stavkaId = getArguments().getInt(STAVKA_ID);
            stavka.setId(stavkaId);
            stavkeViewModel.update(stavka);
        } else {
            stavkeViewModel.insert(stavka);
        }
    }
    private void setViewModels() {
        lokacijeViewModel = new ViewModelProvider(this).get(LokacijeViewModel.class);
        zaposleniViewModel = new ViewModelProvider(this).get(ZaposleniViewModel.class);

        setLokacijeSpinner();
        setZaposleniSpinner();
    }

    private void setLokacijeSpinner() {
        String odaberiLokaciju = getString(R.string.odaberi_novu_lokaciju);

        lokacijeViewModel.getLokacije().observe(getViewLifecycleOwner(), lokacijeList -> {
            lokacijeIds.clear();
            gradovi.clear();

            gradovi.add(odaberiLokaciju);
            lokacijeIds.add(-1);

            for (Lokacija lokacija : lokacijeList) {
                lokacijeIds.add(lokacija.getId());
                gradovi.add(lokacija.getGrad());

                if(lokacija.getId() == osnovnoSredstvo.getLokacijaId()) {
                    tvTrenutnaLokacija.setText(lokacija.getGrad());
                }
            }

            ArrayAdapter<String> lokacijeAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, gradovi);
            lokacijeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerNovaLokacija.setAdapter(lokacijeAdapter);
        });
    }

    private void setZaposleniSpinner() {
        String odaberiZaposlenog = getString(R.string.odaberi_novog_zaposlenog);

        zaposleniViewModel.getZaposlene().observe(getViewLifecycleOwner(), zaposleniList -> {
            zaposleniIds.clear();
            zaposleni.clear();

            zaposleni.add(odaberiZaposlenog);
            zaposleniIds.add(-1);

            for (Osoba osoba : zaposleniList) {
                zaposleniIds.add(osoba.getId());
                zaposleni.add(osoba.getIme() + " " + osoba.getPrezime());

                if(osoba.getId() == osnovnoSredstvo.getZaposleniId()) {
                    String imeOsobe = osoba.getIme() + " " + osoba.getPrezime();
                    tvTrenutnaOsoba.setText(imeOsobe);
                }
            }

            ArrayAdapter<String> zaposleniAdapter = new ArrayAdapter<>(requireContext(),
                    android.R.layout.simple_spinner_item, zaposleni);
            zaposleniAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerNovaOsoba.setAdapter(zaposleniAdapter);
        });
    }
}
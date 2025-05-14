package org.unibl.etf.mr_projektni_zadatak.ui.dodavanje.zaposleni;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.unibl.etf.mr_projektni_zadatak.MainActivity;
import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Osoba;
import org.unibl.etf.mr_projektni_zadatak.ui.lokacije.LokacijeViewModel;
import org.unibl.etf.mr_projektni_zadatak.ui.zaposleni.ZaposleniViewModel;

public class DodajZaposlenogFragment extends Fragment {
    public static final String ZAPOSLENI_ID = "zaposleniId";
    private EditText etIme, etPrezime, etPozicija;
    private Button btnSacuvaj;

    private ZaposleniViewModel zaposleniViewModel;

    private View root;

    public static DodajZaposlenogFragment newInstance() {
        return new DodajZaposlenogFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Navigation.findNavController(requireView()).popBackStack();
            }
        };

        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_dodaj_zaposlenog, container, false);

        etIme = root.findViewById(R.id.et_ime_zaposlenog);
        etPrezime = root.findViewById(R.id.et_prezime_zaposlenog);
        etPozicija = root.findViewById(R.id.et_pozicija_zaposlenog);
        btnSacuvaj = root.findViewById(R.id.btn_sacuvaj_zaposlenog);

        zaposleniViewModel = new ViewModelProvider(this).get(ZaposleniViewModel.class);

        btnSacuvajSetOnClickListener();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity != null && activity.getSupportActionBar() != null) {
            activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (getArguments() != null && getArguments().containsKey(ZAPOSLENI_ID)) {
            int zaposleniId = getArguments().getInt(ZAPOSLENI_ID);

            zaposleniViewModel.getZaposleniById(zaposleniId).observe(getViewLifecycleOwner(), zaposleni -> {
                if(zaposleni != null) {
                    etIme.setText(zaposleni.getIme());
                    etPrezime.setText(zaposleni.getPrezime());
                    etPozicija.setText(zaposleni.getPozicija());
                }
            });
        }
    }

    private void btnSacuvajSetOnClickListener() {
        btnSacuvaj.setOnClickListener(v -> {
            String ime = etIme.getText().toString();
            String prezime = etPrezime.getText().toString();
            String pozicija = etPozicija.getText().toString();

            if (ime.isEmpty() || prezime.isEmpty() || pozicija.isEmpty()) {
                String popunitePolja = getString(R.string.popunite_polja);
                Toast.makeText(requireContext(), popunitePolja, Toast.LENGTH_SHORT).show();
                return;
            }

            Osoba osoba = new Osoba(ime, prezime, pozicija);

            sacuvajZaposlenog(osoba);

            Navigation.findNavController(root).navigate(R.id.action_nav_dodaj_zaposlenog_to_nav_zaposleni);
        });
    }

    private void sacuvajZaposlenog(Osoba osoba) {
        if (getArguments() != null && getArguments().containsKey(ZAPOSLENI_ID)) {
            int zaposleniId = getArguments().getInt(ZAPOSLENI_ID);
            osoba.setId(zaposleniId);
            zaposleniViewModel.update(osoba);
        } else {
            zaposleniViewModel.insert(osoba);
        }
    }
}
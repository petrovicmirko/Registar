package org.unibl.etf.mr_projektni_zadatak.ui.dodavanje.lokacija;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.unibl.etf.mr_projektni_zadatak.MainActivity;
import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.ui.lokacije.LokacijeViewModel;

import java.io.File;

public class DodajLokacijuFragment extends Fragment {
    public static final String LOKACIJA_ID = "lokacijaId";
    private EditText etNazivGrada, etSirina, etDuzina;
    private Button btnSacuvaj;
    private LokacijeViewModel lokacijeViewModel;

    private View root;

    public static DodajLokacijuFragment newInstance() {
        return new DodajLokacijuFragment();
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
        root = inflater.inflate(R.layout.fragment_dodaj_lokaciju, container, false);

        etNazivGrada = root.findViewById(R.id.et_naziv_grada);
        etSirina = root.findViewById(R.id.et_sirina);
        etDuzina = root.findViewById(R.id.et_duzina);
        btnSacuvaj = root.findViewById(R.id.btn_sacuvaj_lokaciju);

        lokacijeViewModel = new ViewModelProvider(this).get(LokacijeViewModel.class);

        String sirinaUslov = getString(R.string.sirina_uslov);
        String duzinaUslov = getString(R.string.duzina_uslov);
        String nevazeciFormat = getString(R.string.nevazeci_format);

        etSirina.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    try {
                        double value = Double.parseDouble(s.toString());
                        if (value > 90 || value < -90) {
                            etSirina.setError(sirinaUslov);
                        }
                    } catch (NumberFormatException e) {
                        etSirina.setError(nevazeciFormat);
                    }
                }
            }
        });

        etDuzina.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    try {
                        double value = Double.parseDouble(s.toString());
                        if (value > 180 || value < -180) {
                            etDuzina.setError(duzinaUslov);
                        }
                    } catch (NumberFormatException e) {
                        etDuzina.setError(nevazeciFormat);
                    }
                }
            }
        });

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


        if (getArguments() != null && getArguments().containsKey(LOKACIJA_ID)) {
            int lokacijaId = getArguments().getInt(LOKACIJA_ID);

            lokacijeViewModel.getLokacijaById(lokacijaId).observe(getViewLifecycleOwner(), lokacija -> {
                if(lokacija != null) {
                    etNazivGrada.setText(lokacija.getGrad());
                    etSirina.setText(String.valueOf(lokacija.getGeografskaSirina()));
                    etDuzina.setText(String.valueOf(lokacija.getGeografskaDuzina()));
                }
            });
        }
    }

    private void btnSacuvajSetOnClickListener() {
        String popunitePolja = getString(R.string.popunite_polja);
        String sirinaDuzinaUslov = getString(R.string.sirina_duzina_uslov);

        btnSacuvaj.setOnClickListener(v -> {
            String nazivGrada = etNazivGrada.getText().toString();
            String sirinaText = etSirina.getText().toString();
            String duzinaText = etDuzina.getText().toString();

            if (nazivGrada.isEmpty() || sirinaText.isEmpty() || duzinaText.isEmpty()) {
                Toast.makeText(requireContext(), popunitePolja, Toast.LENGTH_SHORT).show();
                return;
            }

            double sirina, duzina;
            try {
                sirina = Double.parseDouble(sirinaText);
                duzina = Double.parseDouble(duzinaText);

                if (sirina >= -90 && sirina <= 90 && duzina >= -180 && duzina <= 180) {
                    Lokacija lokacija = new Lokacija(nazivGrada, sirina, duzina);

                    sacuvajLokaciju(lokacija);

                    Navigation.findNavController(requireView()).popBackStack();
                }
            } catch (NumberFormatException e) {
                Toast.makeText(requireContext(), sirinaDuzinaUslov, Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    private void sacuvajLokaciju(Lokacija lokacija) {
        if (getArguments() != null && getArguments().containsKey(LOKACIJA_ID)) {
            int lokacijaId = getArguments().getInt(LOKACIJA_ID);
            lokacija.setId(lokacijaId);
            lokacijeViewModel.update(lokacija);
        } else {
            lokacijeViewModel.insert(lokacija);
        }
    }
}
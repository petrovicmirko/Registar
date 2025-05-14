package org.unibl.etf.mr_projektni_zadatak.ui.podesavanja;

import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import org.unibl.etf.mr_projektni_zadatak.MainActivity;
import org.unibl.etf.mr_projektni_zadatak.R;

import java.util.Locale;

public class PodesavanjaFragment extends Fragment {
    public static final String SR = "sr";
    public static final String EN = "en";
    private RadioGroup languageRadioGroup;
    private PodesavanjaViewModel podesavanjaViewModel;

    public static PodesavanjaFragment newInstance() {
        return new PodesavanjaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_podesavanja, container, false);

        languageRadioGroup = view.findViewById(R.id.languageRadioGroup);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        String currentLang = preferences.getString(MainActivity.SELECTED_LANGUAGE, EN);
        languageRadioGroup.check(currentLang.equals(EN) ? R.id.radioEnglish : R.id.radioSerbian);

        languageRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String lang = checkedId == R.id.radioEnglish ? EN : SR;

            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).changeLanguage(lang);
            }
        });

        return view;
    }
}
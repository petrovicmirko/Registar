package org.unibl.etf.mr_projektni_zadatak.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Lokacija;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LokacijaAdapter extends RecyclerView.Adapter<LokacijaAdapter.LokacijaViewHolder> {
    private List<Lokacija> lokacije;
    private OnItemClickListener listener;

    public LokacijaAdapter(List<Lokacija> lokacije, OnItemClickListener listener) {
        this.lokacije = lokacije;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LokacijaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.lokacija_item, parent, false);
        return new LokacijaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LokacijaViewHolder holder, int position) {
        Lokacija lokacija = lokacije.get(position);

        holder.tvGrad.setText(lokacija.getGrad());
        holder.tvGeografskaSirina.setText(String.valueOf(lokacija.getGeografskaSirina()));
        holder.tvGeografskaDuzina.setText(String.valueOf(lokacija.getGeografskaDuzina()));

        holder.btnLokacijaVise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViseClick(lokacija);
            }
        });
    }

    @Override
    public int getItemCount() {
        return lokacije.size();
    }

    public static class LokacijaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvGrad;
        private TextView tvGeografskaSirina;
        private TextView tvGeografskaDuzina;
        private Button btnLokacijaVise;

        public LokacijaViewHolder(View view) {
            super(view);
            tvGrad = view.findViewById(R.id.tv_grad);
            tvGeografskaSirina = view.findViewById(R.id.tv_geografska_sirina);
            tvGeografskaDuzina = view.findViewById(R.id.tv_geografska_duzina);
            btnLokacijaVise = view.findViewById(R.id.btn_lokacija_vise);
        }
    }

    public void setFiltriraneLokacije(List<Lokacija> filtriraneLokacije) {
        this.lokacije = filtriraneLokacije;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onViseClick(Lokacija lokacija);
    }
}

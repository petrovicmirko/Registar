package org.unibl.etf.mr_projektni_zadatak.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.PopisnaLista;

import java.util.ArrayList;
import java.util.List;

public class PopisnaListaAdapter extends RecyclerView.Adapter<PopisnaListaAdapter.PopisnaListaViewHolder> {
    List<PopisnaLista> popisneListe = new ArrayList<>();
    private OnItemClickListener listener;

    public PopisnaListaAdapter(List<PopisnaLista> popisneListe, OnItemClickListener listener) {
        this.popisneListe = popisneListe;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PopisnaListaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.popisna_lista_item, parent, false);
        return new PopisnaListaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopisnaListaViewHolder holder, int position) {
        PopisnaLista popisnaLista = popisneListe.get(position);

        holder.tvPopisnaLista.setText(popisnaLista.getNazivListe());
        holder.tvOpis.setText(popisnaLista.getOpis());

        holder.btnPopisnaLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViseClick(popisnaLista);
            }
        });
    }

    @Override
    public int getItemCount() {
        return popisneListe.size();
    }

    public static class PopisnaListaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPopisnaLista;
        private TextView tvOpis;
        private Button btnPopisnaLista;
        public PopisnaListaViewHolder(View view) {
            super(view);

            tvPopisnaLista = view.findViewById(R.id.tv_popisna_lista);
            tvOpis = view.findViewById(R.id.tv_opis_popisne_liste);
            btnPopisnaLista = view.findViewById(R.id.btn_popisna_lista_vise);
        }
    }

    public void setFiltriranePopisneListe(List<PopisnaLista> filtriranePopisneListe) {
        this.popisneListe = filtriranePopisneListe;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onViseClick(PopisnaLista popisnaLista);
    }
}

package org.unibl.etf.mr_projektni_zadatak.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import org.unibl.etf.mr_projektni_zadatak.R;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.OsnovnoSredstvo;
import org.unibl.etf.mr_projektni_zadatak.registar_db.model.Stavka;
import org.unibl.etf.mr_projektni_zadatak.ui.osnovna_sredstva.OsnovnaSredstvaViewModel;

import java.util.List;

public class StavkaAdapter extends RecyclerView.Adapter<StavkaAdapter.StavkaViewHolder> {
    private List<Stavka> stavke;
    private OnItemClickListener listener;
    private OsnovnaSredstvaViewModel osnovnaSredstvaViewModel;
    private LifecycleOwner lifecycleOwner;
    private Context context;

    public StavkaAdapter(List<Stavka> stavke, OsnovnaSredstvaViewModel osnovnaSredstvaViewModel, Context context, LifecycleOwner lifecycleOwner, OnItemClickListener listener) {
        this.stavke = stavke;
        this.listener = listener;
        this.osnovnaSredstvaViewModel = osnovnaSredstvaViewModel;
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
    }

    @NonNull
    @Override
    public StavkaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.stavka_item, parent, false);
        return new StavkaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StavkaViewHolder holder, int position) {
        Stavka stavka = stavke.get(position);

        String nepoznatoOsnovnoSredstvo = context.getString(R.string.nepoznato_os);

        LiveData<OsnovnoSredstvo> liveDataOsnovnoSredstvo =
                osnovnaSredstvaViewModel.getOsnovnoSredstvoById(stavka.getOsnovnoSredstvoId());

        liveDataOsnovnoSredstvo.observe(lifecycleOwner, osnovnoSredstvo -> {
            if (osnovnoSredstvo != null) {
                holder.tvStavkaNaziv.setText(osnovnoSredstvo.getNaziv());
                holder.tvStavkaBarkod.setText(String.valueOf(osnovnoSredstvo.getBarkod()));
            } else {
                holder.tvStavkaNaziv.setText(nepoznatoOsnovnoSredstvo);
                holder.tvStavkaBarkod.setText("");
            }
        });

        holder.btnStavkaVise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onViseClick(stavka);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stavke.size();
    }

    public static class StavkaViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStavkaNaziv;
        private TextView tvStavkaBarkod;
        private Button btnStavkaVise;

        public StavkaViewHolder(View view) {
            super(view);
            tvStavkaNaziv = view.findViewById(R.id.tv_stavka_naziv);
            tvStavkaBarkod = view.findViewById(R.id.tv_stavka_barkod);
            btnStavkaVise = view.findViewById(R.id.btn_stavka_vise);
        }
    }
    public void setFiltriraneStavke(List<Stavka> filtriranaLista) {
        this.stavke = filtriranaLista;
        notifyDataSetChanged();
    }
    public interface OnItemClickListener {
        void onViseClick(Stavka stavka);
    }
}

package com.example.fa_malsha_c0871063_android.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fa_malsha_c0871063_android.R;
import com.example.fa_malsha_c0871063_android.model.Product;

import java.util.List;

public class RecyclerViewAdapter <T> extends RecyclerView.Adapter<RecyclerViewAdapter<T>.ViewHolder> {
    private static final String TAG = "Recycler View Adapter";

    private List<T> tList;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public RecyclerViewAdapter(List<T> tList, Context context, OnItemClickListener onItemClickListener) {
        this.tList = tList;
        this.context = context;
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.product_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        T t = tList.get(position);
        if (t instanceof Product) {
            Product product = (Product) t;
            holder.name.setText(product.getName());
            holder.description.setText(product.getDescription());
            holder.price.setText(String.valueOf(product.getPrice()));
            String log = String.valueOf(product.getLongitude());
            String lat = String.valueOf(product.getLatitude());
            String location = "Latitude : " + lat + ", Longitude : " + log;
            holder.location.setText(location);
            holder.imageView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return tList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView name;
        private TextView description;
        private TextView price;
        private TextView location;
        private ImageView imageView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name_row);
            description = itemView.findViewById(R.id.description_row);
            price = itemView.findViewById(R.id.price_row);
            location = itemView.findViewById(R.id.location_row);
            imageView = itemView.findViewById(R.id.imageView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemClickListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setDataList(List<T> tList) {
        this.tList = tList;
        notifyDataSetChanged();
    }
}

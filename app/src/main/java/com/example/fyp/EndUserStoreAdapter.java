package com.example.fyp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class EndUserStoreAdapter extends RecyclerView.Adapter<EndUserStoreAdapter.StoreViewHolder>{
    private Context context;
    private List<Map<String, Object>> productList;

    public EndUserStoreAdapter(Context context,List<Map<String, Object>> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_card_view, parent, false);
        return new StoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EndUserStoreAdapter.StoreViewHolder holder, int position) {
        Map<String, Object> productData = productList.get(position);
        holder.bind(productData);
    }
    public class StoreViewHolder extends RecyclerView.ViewHolder {

        private TextView authorTextView;
        private TextView nameTextView;
        private TextView priceTextView;
        private ImageView productImageView;

        public StoreViewHolder(@NonNull View itemView) {
            super(itemView);
            authorTextView = itemView.findViewById(R.id.authorTextView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            productImageView = itemView.findViewById(R.id.productImageView);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Map<String, Object> productData = productList.get(position);
                    showProductDialog(productData);
                }
            });
        }

        public void bind(Map<String, Object> productData) {
            authorTextView.setText(productData.get("author").toString());
            nameTextView.setText(productData.get("name").toString());
            priceTextView.setText(productData.get("price").toString());
            if (productData.containsKey("imageUrl")) {
                String productImageUrl = productData.get("imageUrl").toString();
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child(productImageUrl);
                Picasso.get().load(productImageUrl).into(productImageView);
            } else {
                productImageView.setImageResource(R.drawable.food);
            }
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    private void showProductDialog(Map<String, Object> productData) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.product_dialog_content, null);

        TextView textView1 = dialogView.findViewById(R.id.textView);
        TextView textView2 = dialogView.findViewById(R.id.textView2);
        TextView textView3 = dialogView.findViewById(R.id.textView5);
        ImageView imageView = dialogView.findViewById(R.id.imageView4);
        TextView priceText = dialogView.findViewById(R.id.priceText);
        TextView textView4 = dialogView.findViewById(R.id.textView70);

        textView1.setText(productData.get("name").toString());
        textView2.setText(productData.get("author").toString());
        textView3.setText(productData.get("description").toString());
        priceText.setText(productData.get("price").toString());
        textView4.setText(productData.get("nutriVal").toString());
        if (productData.containsKey("imageUrl")) {
            String productImageUrl = productData.get("imageUrl").toString();
            Picasso.get().load(productImageUrl).into(imageView);}
        else {
            imageView.setImageResource(R.drawable.food);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Details", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                EndUserStoreDetailFragment fragment = new EndUserStoreDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("productData", (Serializable) productData);
                fragment.setArguments(bundle);
                transaction.replace(R.id.endUserFragmentContainerView, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
        builder.create().show();
    }

    public void updateData(List<Map<String, Object>> newProductList) {
        this.productList.clear();
        this.productList.addAll(newProductList);
        notifyDataSetChanged();
    }
}

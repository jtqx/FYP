package com.example.fyp;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class BusinessStoreAdapter extends RecyclerView.Adapter<BusinessStoreAdapter.ProductViewHolder>{
    private Context context;
    private List<Map<String, Object>> productList;
    private OnUpdateClickListener onUpdateClickListener;
    public interface OnUpdateClickListener {
        void onUpdateClick(Map<String, Object> productData);
    }

    public BusinessStoreAdapter(Context context, List<Map<String, Object>> productList) {
        this.context = context;
        this.productList = productList;
        this.onUpdateClickListener = onUpdateClickListener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.product_card_view, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Map<String, Object> productData = productList.get(position);
        holder.bind(productData);
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
        builder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        builder.setNeutralButton("Update", (dialog, which) -> showUpdateDialog(productData));
        builder.setNegativeButton("Delete", (dialog, which) -> deleteProduct(productData));
        builder.create().show();
    }
    public void setOnUpdateClickListener(OnUpdateClickListener onUpdateClickListener) {
        this.onUpdateClickListener = onUpdateClickListener;
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ProductViewHolder extends RecyclerView.ViewHolder {

        private TextView authorTextView;
        private TextView nameTextView;
        private TextView priceTextView;
        private ImageView productImageView;

        public ProductViewHolder(@NonNull View itemView) {
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

    private void showUpdateDialog(Map<String, Object> productData) {
        onUpdateClickListener.onUpdateClick(productData);
    }

    private void deleteProduct(Map<String, Object> productData) {
        Product product = new Product();
        String name = productData.get("name") != null ? productData.get("name").toString() : "";
        String author = productData.get("author") != null ? productData.get("author").toString() : "";
        product.getProductDocumentId(name, author, new Product.ProductDocumentIdCallback() {
            @Override
            public void onSuccess(String documentId) {
                product.deleteProduct(documentId, new Product.UserCallback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onFailure(Exception e) {
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
            }
        });
    }

    public void updateData(List<Map<String, Object>> newProductList) {
        this.productList.clear();
        this.productList.addAll(newProductList);
        notifyDataSetChanged();
    }
}

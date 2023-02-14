package com.example.fa_malsha_c0871063_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.fa_malsha_c0871063_android.Adapter.RecyclerViewAdapter;
import com.example.fa_malsha_c0871063_android.databinding.ActivityMainBinding;
import com.example.fa_malsha_c0871063_android.helper.SwipeHelper;
import com.example.fa_malsha_c0871063_android.helper.SwipeUnderlayButtonClickListener;
import com.example.fa_malsha_c0871063_android.model.Product;
import com.example.fa_malsha_c0871063_android.model.ProductViewModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerViewAdapter.OnItemClickListener {

    ActivityMainBinding binding;
    private RecyclerViewAdapter adapter;
    private ProductViewModel viewModel;

    List<Product> productList;

    private Product deletedProduct;

    public static final String PRODUCT_ID = "product_id";

    private int menuItemId;

    private SwipeHelper swipeHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.addBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                buttonClicked();
            }
        });

        viewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(ProductViewModel.class);

        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL,false));
        binding.recyclerView.setAdapter(adapter);
        productList = new ArrayList<>();
        binding.productListSearch.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Override onQueryTextSubmit method which is call when submit query is searched
            @Override
            public boolean onQueryTextSubmit(String query) {
                ArrayList<Product> productArrayList = searchProduct(query);
                if (productArrayList.size() > 0) {

                    adapter.setDataList(
                            productArrayList
                    );

                } else {
                    Toast.makeText(MainActivity.this, "Item not found.", Toast.LENGTH_SHORT).show();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<Product> productArrayList = searchProduct(newText);
                if (productArrayList.size() > 0) {
                    adapter.setDataList(
                            productArrayList
                    );
                }
                return false;
            }
        });

        // using SwipeHelper class
        swipeHelper = new SwipeHelper(this, 300, binding.recyclerView) {
            @Override
            protected void instantiateSwipeButton(RecyclerView.ViewHolder viewHolder, List<SwipeUnderlayButton> buffer) {
                buffer.add(new SwipeUnderlayButton(MainActivity.this,
                        "Delete",
                        R.drawable.ic_delete,
                        30,
                        Color.parseColor("#BA0F30"),
                        SwipeDirection.LEFT,
                        new SwipeUnderlayButtonClickListener() {
                            @Override
                            public void onClick(int position) {
                                deleteProduct(position);
                            }
                        }));
                buffer.add(new SwipeUnderlayButton(MainActivity.this,
                        "Update",
                        R.drawable.ic_update,
                        30,
                        Color.parseColor("#308423"),
                        SwipeDirection.LEFT,
                        new SwipeUnderlayButtonClickListener() {
                            @Override
                            public void onClick(int position) {
                                editProduct(position);
                            }
                        }));
            }
        };
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadProduct();
    }

    private void buttonClicked() {
        navigateToAdd();

    }

    private void navigateToAdd() {
        Intent intent = new Intent(getBaseContext(), AddProduct.class);
        startActivity(intent);
    }

    private void loadProduct() {
        viewModel.getAllProducts().observe(this, productList -> {
            this.productList = productList;
            if(this.productList.size() == 0){
                addData();
            }
            updateUI();
        });

    }

    private void updateUI() {

        binding.tvCount.setText(String.valueOf(productList.size()) + " Products");
        adapter = new RecyclerViewAdapter(productList, this, this);
        binding.recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private ArrayList<Product> searchProduct(String text) {
        ArrayList<Product> matches = new ArrayList<>();
        for(Product product : productList) {
            if (product.getName().toLowerCase().contains(text.toLowerCase())) {
                matches.add(product);
            }else  if (product.getDescription().toLowerCase().contains(text.toLowerCase())) {
                matches.add(product);
            }
        }
        return matches;
    }

    @Override
    public void onItemClick(int position) {
        createSortMenuOptions(position);
    }

    private void createSortMenuOptions(int position) {
        String[] options = {"View","Delete",};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.pick_option)
                .setItems(options, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:
                                editProduct(position);
                                break;
                            case 1:
                                deleteProduct(position);
                                break;
                        }
                        adapter.notifyDataSetChanged();
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //Single Task Selection Option
    private void editProduct(int position){
        Product product = productList.get(position);
        Intent intent = new Intent(MainActivity.this, AddProduct.class);
        intent.putExtra(PRODUCT_ID, product.getId());
        startActivity(intent);
    }

    private void deleteProduct(int position) {
        Product product = productList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Do you want to delete?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            deletedProduct = product;
            viewModel.delete(product);
            adapter.notifyItemRemoved(position);
            Snackbar.make(binding.recyclerView, deletedProduct.getName() + " is deleted!", Snackbar.LENGTH_LONG)
                    .setAction("Undo", v -> viewModel.insert(deletedProduct)).show();
            Toast.makeText(this, product.getName() + " deleted", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("No", (dialog, which) -> adapter.notifyItemChanged(position));
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    private void addData(){
        viewModel.getAllProducts();
        List<Product> array = new ArrayList<>();
        Product product0 = new Product("Product 0","Product Des",350.0,43.651070, -79.347015);
        array.add(product0);
        Product product1 = new Product("Product 1","Product Des",450.0,43.651070, -79.347015);
        array.add(product1);
        Product product2 = new Product("Product 2","Product Des",750.0,43.651070, -79.347015);
        array.add(product2);
        Product product3 = new Product("Product 3","Product Des",150.0,43.651070, -79.347015);
        array.add(product3);
        Product product4 = new Product("Product 4","Product Des",350.0,43.651070, -79.347015);
        array.add(product4);

        Product product5 = new Product("Product 5","Product Des",300.0,43.651070, -79.347015);
        array.add(product5);
        Product product6 = new Product("Product 6","Product Des",450.0,43.651070, -79.347015);
        array.add(product6);
        Product product7 = new Product("Product 7","Product Des",250.0,43.651070, -79.347015);
        array.add(product7);
        Product product8 = new Product("Product 8","Product Des",50.0,43.651070, -79.347015);
        array.add(product8);
        Product product9 = new Product("Product 9","Product Des",350.0,43.651070, -79.347015);
        array.add(product9);

        for (Product p: array
             ) {
            viewModel.insert(p);
        }
    }
}
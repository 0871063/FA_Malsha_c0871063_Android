package com.example.fa_malsha_c0871063_android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fa_malsha_c0871063_android.databinding.ActivityAddProductBinding;
import com.example.fa_malsha_c0871063_android.model.Product;
import com.example.fa_malsha_c0871063_android.model.ProductViewModel;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class AddProduct extends AppCompatActivity {

    private boolean isEditing = false;
    private long productId = 0;
    private Product productTobeUpdated;

    private ProductViewModel productViewModel;

    ActivityAddProductBinding binding;

    public static final String PRODUCT_ID = "product_id";

    private static final int SECOND_ACTIVITY_REQUEST_CODE = 0;

    Double latitude = 0.0;
    Double longitude = 0.0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddProductBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productViewModel = new ViewModelProvider.AndroidViewModelFactory(this.getApplication())
                .create(ProductViewModel.class);


        binding.btnAddProduct.setOnClickListener(v -> {
            addUpdateEmployee();
        });

        binding.btnLocation.setOnClickListener(v -> {
            navigateToMap();
        });


        if (getIntent().hasExtra(MainActivity.PRODUCT_ID)) {
            productId = getIntent().getLongExtra(MainActivity.PRODUCT_ID, 0);
            Log.d("TAG", "onCreate: " + productId);

            productViewModel.getProduct(productId).observe(this, product -> {
                if (product != null) {
                    latitude = product.getLatitude();
                    longitude = product.getLongitude();
                    String location = "Latitude : " + String.valueOf(latitude) + " , Longitude : " + String.valueOf(longitude);
                    binding.etName.setText(product.getName());
                    binding.etDescription.setText(product.getDescription());
                    binding.etPrice.setText(String.valueOf(product.getPrice()));
                    binding.etLocation.setText(location);
                    productTobeUpdated = product;
                }
            });

            TextView label = findViewById(R.id.label);
            isEditing = true;
            label.setText(R.string.update_label);
            binding.btnAddProduct.setText(R.string.update_btn_text);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
    private void addUpdateEmployee() {
        String name = binding.etName.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        Double price = Double.parseDouble(binding.etPrice.getText().toString());
        if (name.isEmpty()) {
            binding.etName.setError("Name field cannot be empty");
            binding.etName.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            binding.etDescription.setError("Description field cannot be empty");
            binding.etDescription.requestFocus();
            return;
        }


        if (isEditing) {
            Product product = new Product();

            long id = productTobeUpdated.getId();
            product.setName(name);
            product.setDescription(description);
            product.setLatitude(latitude);
            product.setLongitude(longitude);
            product.setPrice(price);
            product.setId(id);
            productViewModel.update( product);
        } else {

            Product product = new Product(name,description,price,latitude,longitude);

            productViewModel.insert(product);
            Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    private void navigateToMap() {
        Intent intent = new Intent(AddProduct.this, MapActivity.class);
        if (productTobeUpdated != null) {
            intent.putExtra(PRODUCT_ID, productTobeUpdated.getId());
        }
        startActivityForResult(intent,SECOND_ACTIVITY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (  data != null) {
                    latitude = Double.parseDouble(data.getStringExtra("Latitude"));
                    longitude = Double.parseDouble(data.getStringExtra("Longitude"));
                    String location = "Latitude : " + String.valueOf(latitude) + " , Longitude : " + String.valueOf(longitude);
                    binding.etLocation.setText(location);
                }

            }
        }
    }
}
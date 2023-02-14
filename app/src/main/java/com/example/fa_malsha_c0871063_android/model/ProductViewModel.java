package com.example.fa_malsha_c0871063_android.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.fa_malsha_c0871063_android.data.ProductRepository;

import java.util.List;

public class ProductViewModel extends AndroidViewModel {
    private ProductRepository repository;
    private final LiveData<List<Product>> allProducts;

    public ProductViewModel(@NonNull Application application) {
        super(application);

        repository = new ProductRepository(application);
        allProducts = repository.getAllProducts();
    }

    public LiveData<List<Product>> getAllProducts() {return allProducts;}

    public LiveData<Product> getProduct(long id) {return repository.getProduct(id);}

    public void insert(Product product) {repository.insert(product);}

    public void update(Product product) {repository.update(product);}

    public void delete(Product product) {repository.delete(product);}
}

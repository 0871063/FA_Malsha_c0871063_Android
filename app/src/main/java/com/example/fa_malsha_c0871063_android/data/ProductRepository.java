package com.example.fa_malsha_c0871063_android.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.example.fa_malsha_c0871063_android.model.Product;
import com.example.fa_malsha_c0871063_android.util.ProductRoomDatabase;

import java.util.List;

public class ProductRepository {

    private ProductDao productDao;
    private LiveData<List<Product>> allProducts;

    public ProductRepository(Application application) {
        ProductRoomDatabase db = ProductRoomDatabase.getInstance(application);
        productDao = db.productDao();
        allProducts = productDao.getAllProducts();
    }

    public LiveData<List<Product>> getAllProducts() {
        return allProducts;
    }

    public LiveData<Product> getProduct(long id) {return productDao.getProduct(id);}

    public void insert(Product product) {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> productDao.insert(product));
    }

    public void update(Product product) {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> productDao.update(product));
    }

    public void delete(Product product) {
        ProductRoomDatabase.databaseWriteExecutor.execute(() -> productDao.delete(product));
    }
}

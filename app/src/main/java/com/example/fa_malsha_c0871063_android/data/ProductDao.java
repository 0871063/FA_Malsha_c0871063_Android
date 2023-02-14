package com.example.fa_malsha_c0871063_android.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.fa_malsha_c0871063_android.model.Product;

import java.util.List;
@Dao
public abstract class ProductDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    public abstract void insert(Product product);

    @Query("DELETE FROM product_table")
    public abstract void deleteAll();

    @Query("SELECT * FROM product_table ORDER BY name ASC")
    public abstract LiveData<List<Product>> getAllProducts();

    @Query("SELECT * FROM product_table WHERE id == :id")
    public abstract LiveData<Product> getProduct(long id);

    @Update
    public abstract void update(Product product);

    @Delete
    public abstract void delete(Product product);

}

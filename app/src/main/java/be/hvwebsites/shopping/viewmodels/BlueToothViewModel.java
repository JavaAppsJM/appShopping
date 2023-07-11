package be.hvwebsites.shopping.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.repositories.FlexiRepository;
import be.hvwebsites.shopping.entities.Meal;
import be.hvwebsites.shopping.entities.MealInMeal;
import be.hvwebsites.shopping.entities.Product;
import be.hvwebsites.shopping.entities.ProductInMeal;
import be.hvwebsites.shopping.entities.ProductInShop;
import be.hvwebsites.shopping.entities.Shop;

public class BlueToothViewModel extends AndroidViewModel {
    // Lijst om bluetooth data in te zetten
    private List<Shop> shopListBt = new ArrayList<>();
    private List<Product> productListBt = new ArrayList<>();
    private List<ProductInShop> productInShopListBt = new ArrayList<>();
    private List<Meal> mealListBt = new ArrayList<>();
    private List<ProductInMeal> productInMealListBt = new ArrayList<>();
    private List<MealInMeal> mealInMealListBt = new ArrayList<>();

    public BlueToothViewModel(@NonNull Application application) {
        super(application);
    }

    public List<Shop> getShopListBt() {
        return shopListBt;
    }

    public List<Product> getProductListBt() {
        return productListBt;
    }

    public List<ProductInShop> getProductInShopListBt() {
        return productInShopListBt;
    }

    public List<Meal> getMealListBt() {
        return mealListBt;
    }

    public List<ProductInMeal> getProductInMealListBt() {
        return productInMealListBt;
    }

    public List<MealInMeal> getMealInMealListBt() {
        return mealInMealListBt;
    }


}

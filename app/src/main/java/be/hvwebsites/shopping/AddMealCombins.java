package be.hvwebsites.shopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.adapters.ChckbxListAdapter;
import be.hvwebsites.shopping.adapters.SmartTextItemListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Meal;
import be.hvwebsites.shopping.entities.Product;
import be.hvwebsites.shopping.services.FileBaseService;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class AddMealCombins extends AppCompatActivity {
    // Device
    private final String deviceModel = Build.MODEL;
    private ShopEntitiesViewModel viewModel;
    private String combinationType = "";
    private Meal mealToManage;
    private List<ListItemHelper> mealCombins = new ArrayList<>();
    private List<ListItemHelper> mealNotCombins = new ArrayList<>();
    private SmartTextItemListAdapter recycMealCombinsAdapter =
            new SmartTextItemListAdapter(this);
    private SmartTextItemListAdapter recycMealNotCombinsAdapter =
            new SmartTextItemListAdapter(this);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meal_combins);

        // Creer een filebase service (bevat file base en file base directory) obv device
        // en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel, getPackageName());

        // Alle data ophalen
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(ShopEntitiesViewModel.class);
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(fileBaseService.getFileBaseDir());
        if (viewModelStatus.getReturnCode() == 0) {
            // Files gelezen
            // Baseswitch in viewmodel zetten is echt nodig voor de creatie ve entity
            // bij toevoegen in de fragmenten
            viewModel.setBaseSwitch(fileBaseService.getFileBase());
        } else if (viewModelStatus.getReturnCode() == 100) {
            Toast.makeText(AddMealCombins.this,
                    viewModelStatus.getReturnMessage(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(AddMealCombins.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // Data uit intent halen
        Intent addCombinsIntent = getIntent();
        combinationType = addCombinsIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_TYPE);
        int mealId = addCombinsIntent.getIntExtra(StaticData.EXTRA_INTENT_KEY_ID,
                StaticData.ITEM_NOT_FOUND);
        mealToManage = viewModel.getMealByID(new IDNumber(mealId));

        // Schermviews definieren
        RecyclerView recycMealCombins = findViewById(R.id.recycMealCombins);
        //SmartTextItemListAdapter recycMealCombinsAdapter = new SmartTextItemListAdapter(this);
        recycMealCombins.setAdapter(recycMealCombinsAdapter);
        recycMealCombins.setLayoutManager(new LinearLayoutManager(this));
        recycMealCombinsAdapter.setClient(SpecificData.ACTIVITY_ADDMEALCOMBINS);

        RecyclerView recycNotMealCombins = findViewById(R.id.recycNotCombins);
        //SmartTextItemListAdapter recycMealNotCombinsAdapter = new SmartTextItemListAdapter(this);
        recycNotMealCombins.setAdapter(recycMealNotCombinsAdapter);
        recycNotMealCombins.setLayoutManager(new LinearLayoutManager(this));
        recycMealNotCombinsAdapter.setClient(SpecificData.ACTIVITY_ADDMEALCOMBINS);

        // RecycleList vullen voor eerste keer
        fillReusablelist();

        // Als er geclicked is op een item inde mealcombins recycler list, wordt dat hier gecapteerd
        recycMealCombinsAdapter.setOnItemClickListener(new SmartTextItemListAdapter.ClickListener() {
            @Override
            public void onItemClicked(int position, View v) {
                int indexCombinToDelete;
                // via het itemID vd listitemhelper het juiste item bepalen en de combinatie
                // met mealToManage verbreken
                // Verwerken afhankelijk vn combinatieType
                switch (combinationType){
                    case SpecificData.SC_PRODUCTSMEAL:
                        // Verbinding meal en product verbreken
                        // Product bepalen
                        Product clickedProduct = viewModel.getProductByID(mealCombins.get(position).getItemID());
                        // Index van product, meal combinatie bepalen
                        indexCombinToDelete = viewModel.getIndexByIdsFromCList(
                                viewModel.getProductInMealList(),
                                mealToManage.getEntityId(),
                                clickedProduct.getEntityId());
                        // Product, meal combinatie deleten
                        viewModel.getProductInMealList().remove(indexCombinToDelete);
                        break;
                    case SpecificData.SC_SUBMEAL:
                        // Verbinding meal en submeal verbreken
                        // Submeal bepalen
                        Meal clickedSubMeal = viewModel.getMealByID(mealCombins.get(position).getItemID());
                        // Index van meal, submeal combinatie bepalen
                        indexCombinToDelete = viewModel.getIndexByIdsFromCList(
                                viewModel.getMealInMealList(),
                                mealToManage.getEntityId(),
                                clickedSubMeal.getEntityId());
                        // Meal, submeal combinatie deleten
                        viewModel.getMealInMealList().remove(indexCombinToDelete);
                        break;
                    case SpecificData.SC_PARENTMEAL:
                        // Verbinding meal en parentmeal verbreken
                        // Parentmeal bepalen
                        Meal clickedParentMeal = viewModel.getMealByID(mealCombins.get(position).getItemID());
                        // Index van parentmeal, meal combinatie bepalen
                        indexCombinToDelete = viewModel.getIndexByIdsFromCList(
                                viewModel.getMealInMealList(),
                                clickedParentMeal.getEntityId(),
                                mealToManage.getEntityId());
                        // Meal, submeal combinatie deleten
                        viewModel.getMealInMealList().remove(indexCombinToDelete);
                        break;
                    default:
                }
                fillReusablelist();
            }
        });
        // Als er geclicked is op een item inde mealNotcombins recycler list, wordt dat hier gecapteerd
        recycMealNotCombinsAdapter.setOnItemClickListener(new SmartTextItemListAdapter.ClickListener() {
            @Override
            public void onItemClicked(int position, View v) {
                int indexCombinToDelete;
                // via het itemID vd listitemhelper het juiste item bepalen en de combinatie
                // met mealToManage toevoegen
                // TODO: Verwerken afhankelijk vn combinatieType
                switch (combinationType){
                    case SpecificData.SC_PRODUCTSMEAL:
                        // TODO: Verbinding meal en product toevoegen
                        // Product bepalen
                        Product clickedProduct = viewModel.getProductByID(mealNotCombins.get(position).getItemID());
                        break;
                    case SpecificData.SC_SUBMEAL:
                        // Verbinding meal en submeal verbreken
                        // Submeal bepalen
                        Meal clickedSubMeal = viewModel.getMealByID(mealCombins.get(position).getItemID());
                        // Index van meal, submeal combinatie bepalen
                        indexCombinToDelete = viewModel.getIndexByIdsFromCList(
                                viewModel.getMealInMealList(),
                                mealToManage.getEntityId(),
                                clickedSubMeal.getEntityId());
                        // Meal, submeal combinatie deleten
                        viewModel.getMealInMealList().remove(indexCombinToDelete);
                        break;
                    case SpecificData.SC_PARENTMEAL:
                        // Verbinding meal en parentmeal verbreken
                        // Parentmeal bepalen
                        Meal clickedParentMeal = viewModel.getMealByID(mealCombins.get(position).getItemID());
                        // Index van parentmeal, meal combinatie bepalen
                        indexCombinToDelete = viewModel.getIndexByIdsFromCList(
                                viewModel.getMealInMealList(),
                                clickedParentMeal.getEntityId(),
                                mealToManage.getEntityId());
                        // Meal, submeal combinatie deleten
                        viewModel.getMealInMealList().remove(indexCombinToDelete);
                        break;
                    default:
                }
                fillReusablelist();
            }
        });
    }

    private void fillReusablelist(){
        // Recyclerlists vullen afhankelijk van combinationType
        switch (combinationType){
            case SpecificData.SC_PRODUCTSMEAL:
                // Vullen met productsmeal
                mealCombins.clear();
                mealCombins.addAll(viewModel.getProductNamesByMeal(mealToManage));
                recycMealCombinsAdapter.setReusableList(mealCombins);
                // Vullen met nog niet gekoppelde producten
                mealNotCombins.clear();
                mealNotCombins.addAll(getMissingProducts(mealCombins));
                recycMealNotCombinsAdapter.setReusableList(mealNotCombins);
                break;
            case SpecificData.SC_SUBMEAL:
                // Vullen met submeals
                mealCombins.clear();
                mealCombins.addAll(viewModel.getChildMealNamesByMeal(mealToManage));
                recycMealCombinsAdapter.setReusableList(mealCombins);
                // Vullen met nog niet gekoppelde submeals
                mealNotCombins.clear();
                mealNotCombins.addAll(getMissingMeals(mealCombins, mealToManage));
                recycMealNotCombinsAdapter.setReusableList(mealNotCombins);
                break;
            case SpecificData.SC_PARENTMEAL:
                // Vullen met parentmeals
                mealCombins.clear();
                mealCombins.addAll(viewModel.getParentMealNamesByMeal(mealToManage));
                recycMealCombinsAdapter.setReusableList(mealCombins);
                // Vullen met nog niet gekoppelde parentmeals
                mealNotCombins.clear();
                mealNotCombins.addAll(getMissingMeals(mealCombins, mealToManage));
                recycMealNotCombinsAdapter.setReusableList(mealNotCombins);
                break;
            default:
        }
    }

    private List<ListItemHelper> getMissingProducts(List<ListItemHelper> inList){
        List<ListItemHelper> missingProcucts = new ArrayList<>();

        for (int i = 0; i < viewModel.getProductList().size(); i++) {
            // Elk product dat niet in de inList zit is een missing product
            Product tempProduct = viewModel.getProductList().get(i);
            if (notExistIdInList(tempProduct.getEntityId().getId(), inList)){
                missingProcucts.add(new ListItemHelper(tempProduct.getEntityName(), "",
                        tempProduct.getEntityId()));
            }
        }
        return missingProcucts;
    }

    private List<ListItemHelper> getMissingMeals(List<ListItemHelper> inList, Meal excludeMeal){
        List<ListItemHelper> missingMeals = new ArrayList<>();

        for (int i = 0; i < viewModel.getMealList().size(); i++) {
            // Elk meal dat niet in de inList zit is een missing meal
            Meal tempMeal = viewModel.getMealList().get(i);
            if (notExistIdInList(tempMeal.getEntityId().getId(), inList) &&
                    (tempMeal.getEntityId().getId() != excludeMeal.getEntityId().getId())){
                missingMeals.add(new ListItemHelper(tempMeal.getEntityName(), "",
                        tempMeal.getEntityId()));
            }
        }
        return missingMeals;
    }

    private boolean notExistIdInList(int inId, List<ListItemHelper> inList){
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getItemID().getId() == inId){
                return false;
            }
        }
        return true;
    }
}
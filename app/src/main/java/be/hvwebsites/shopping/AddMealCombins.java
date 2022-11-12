package be.hvwebsites.shopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.repositories.Cookie;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
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
        SmartTextItemListAdapter recycMealCombinsAdapter = new SmartTextItemListAdapter(this);
        recycMealCombins.setAdapter(recycMealCombinsAdapter);
        recycMealCombins.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView recycNotMealCombins = findViewById(R.id.recycNotCombins);
        SmartTextItemListAdapter recycMealNotCombinsAdapter = new SmartTextItemListAdapter(this);
        recycNotMealCombins.setAdapter(recycMealNotCombinsAdapter);
        recycNotMealCombins.setLayoutManager(new LinearLayoutManager(this));

        // Recyclerlists vullen afhankelijk van combinationType
        switch (combinationType){
            case SpecificData.SC_PRODUCTSMEAL:
                // Vullen met productsmeal
                recycMealCombinsAdapter.setReusableList(viewModel.getProductNamesByMeal(mealToManage));
                // TODO: Vullen met nog niet gekoppelde producten
                recycMealNotCombinsAdapter.setReusableList(
                        getMissingProducts(recycMealCombinsAdapter.getReusableList()));
                break;
            case SpecificData.SC_SUBMEAL:
                // Vullen met submeals
                recycMealCombinsAdapter.setReusableList(viewModel.getChildMealNamesByMeal(mealToManage));
                break;
            case SpecificData.SC_PARENTMEAL:
                // Vullen met parentmeals
                recycMealCombinsAdapter.setReusableList(viewModel.getParentMealNamesByMeal(mealToManage));
                break;
            default:
        }
    }

    private List<ListItemHelper> getMissingProducts(List<ListItemHelper> inList){
        List<ListItemHelper> missingProcucts = new ArrayList<>();

        for (int i = 0; i < viewModel.getProductList().size(); i++) {
            // Elk product dat niet in de inList zit is een missing product
            Product tempProduct = viewModel.getProductList().get(i);
            if (!existIdInList(tempProduct.getEntityId().getId(), inList)){
                missingProcucts.add(new ListItemHelper(tempProduct.getEntityName(), "",
                        tempProduct.getEntityId()));
            }

        }
        return missingProcucts;
    }

    private boolean existIdInList(int inId, List<ListItemHelper> inList){
        for (int i = 0; i < inList.size(); i++) {
            if (inList.get(i).getItemID().getId() == inId){
                return true;
            }
        }
        return false;
    }
}
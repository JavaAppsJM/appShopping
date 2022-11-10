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

import be.hvwebsites.libraryandroid4.repositories.Cookie;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.adapters.SmartTextItemListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.services.FileBaseService;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class AddMealCombins extends AppCompatActivity {
    // Device
    private final String deviceModel = Build.MODEL;
    private ShopEntitiesViewModel viewModel;

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

        // Data uit intent halen vr het list type, action en evt selectie & index
        Intent addCombinsIntent = getIntent();
        String action = addCombinsIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_ACTION);
        // Data uit intent halen als die er is
        CookieRepository cookieRepository = new CookieRepository(fileBaseService.getFileBaseDir());
        if (addCombinsIntent.hasExtra(StaticData.EXTRA_INTENT_KEY_TYPE)){
            listType = addCombinsIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_TYPE);
            // listtype in cookie steken
            cookieRepository.addCookie(new Cookie(SpecificData.COOKIE_RETURN_ENTITY_TYPE, listType));
        }else {
            // er is geen intent, listtype ophalen als Cookie
            listType = cookieRepository.getCookieValueFromLabel(SpecificData.LIST_TYPE);
        }

        // Schermviews definieren
        RecyclerView recycMealCombins = findViewById(R.id.recycMealCombins);
        SmartTextItemListAdapter recycMealCombinsAdapter = new SmartTextItemListAdapter(this);
        recycMealCombins.setAdapter(recycMealCombinsAdapter);
        recycMealCombins.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView recycNotMealCombins = findViewById(R.id.recycNotCombins);
        SmartTextItemListAdapter recycMealNotCombinsAdapter = new SmartTextItemListAdapter(this);
        recycNotMealCombins.setAdapter(recycMealNotCombinsAdapter);
        recycNotMealCombins.setLayoutManager(new LinearLayoutManager(this));

        // Recyclerlists vullen
        recycMealCombinsAdapter.setReusableList();
    }
}
package be.hvwebsites.shopping;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.fragments.ProductFragment;
import be.hvwebsites.shopping.fragments.ShopFragment;
import be.hvwebsites.shopping.services.FileBaseService;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class ManageItemActivity extends AppCompatActivity  {
    // Device
    private final String deviceModel = Build.MODEL;
    private ShopEntitiesViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_item);

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
            Toast.makeText(ManageItemActivity.this,
                    viewModelStatus.getReturnMessage(),
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ManageItemActivity.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // Data uit intent halen vr het list type, action en evt selectie & index
        Intent manageItemIntent = getIntent();
        String listType = manageItemIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_TYPE);
        String action = manageItemIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_ACTION);

        // Bundle voorbereiden om mee te geven aan fragment
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putString(StaticData.EXTRA_INTENT_KEY_ACTION, action);
        // TODO: is dit nodig want gans het viewmodel wordt gerecupereerd in het fragment ?
        //fragmentBundle.putString(StaticData.EXTRA_INTENT_KEY_FILE_BASE_DIR, fileBaseService.getFileBaseDir());

        // indien update
        if (action.equals(StaticData.ACTION_UPDATE)){
            int index = manageItemIntent.getIntExtra(StaticData.EXTRA_INTENT_KEY_INDEX, 0);
            fragmentBundle.putInt(StaticData.EXTRA_INTENT_KEY_INDEX, index);
        }

        switch (listType){
            case SpecificData.LIST_TYPE_1:
                if (action.equals(StaticData.ACTION_NEW)){
                    setTitle(SpecificData.TITLE_NEW_ACTIVITY_T1);
                }else {
                    setTitle(SpecificData.TITLE_UPDATE_ACTIVITY_T1);
                }
                // Creeer fragment_shop
                if (savedInstanceState == null){
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragmentShopEntity, ShopFragment.class, fragmentBundle)
                            .commit();
                }
                break;
            case SpecificData.LIST_TYPE_2:
                if (action.equals(StaticData.ACTION_NEW)){
                    setTitle(SpecificData.TITLE_NEW_ACTIVITY_T2);
                }else {
                    setTitle(SpecificData.TITLE_UPDATE_ACTIVITY_T2);
                }
                // Creeer fragment_product
                if (savedInstanceState == null){
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragmentShopEntity, ProductFragment.class, fragmentBundle)
                            .commit();
                }
                break;
            case SpecificData.LIST_TYPE_3:
                if (action.equals(StaticData.ACTION_NEW)){
                    setTitle(SpecificData.TITLE_NEW_ACTIVITY_T3);
                }else {
                    setTitle(SpecificData.TITLE_UPDATE_ACTIVITY_T3);
                }
                // Creeer fragment_meal
                // TODO: ga naar mealfragment als het klaar is
                if (savedInstanceState == null){
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragmentShopEntity, MealFragment.class, fragmentBundle)
                            .commit();
                }
/*
                if (savedInstanceState == null){
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragmentShopEntity, MealFragment.class, fragmentBundle)
                            .commit();
                }
*/
                break;
        }
    }
}
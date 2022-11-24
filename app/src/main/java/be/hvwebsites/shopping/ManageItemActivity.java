package be.hvwebsites.shopping;

import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import be.hvwebsites.libraryandroid4.repositories.Cookie;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.fragments.MealFragment;
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
        } else {
            Toast.makeText(ManageItemActivity.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // Bundle definieren om mee te geven aan fragment
        Bundle fragmentBundle = new Bundle();

        // Data uit intent halen indien ingevuld anders cookies
        Intent manageItemIntent = getIntent();
        CookieRepository cookieRepository = new CookieRepository(fileBaseService.getFileBaseDir());
        String listType = "";
        if (manageItemIntent.hasExtra(StaticData.EXTRA_INTENT_KEY_TYPE)){
            listType = manageItemIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_TYPE);
        }else {
            // er is geen intent, listtype ophalen als Cookie
            listType = cookieRepository.getCookieValueFromLabel(SpecificData.COOKIE_RETURN_ENTITY_TYPE);
        }
        String action = "";
        if (manageItemIntent.hasExtra(StaticData.EXTRA_INTENT_KEY_ACTION)){
            action = manageItemIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_ACTION);
        }else {
            // er is geen intent, action ophalen als Cookie
            action = cookieRepository.getCookieValueFromLabel(SpecificData.COOKIE_RETURN_ACTION);
        }
        // Indien Update, moet index vh uptodaten element gevonden worden
        int index = StaticData.ITEM_NOT_FOUND;
        if (action.equals(StaticData.ACTION_UPDATE)){
            if (manageItemIntent.hasExtra(StaticData.EXTRA_INTENT_KEY_INDEX)){
                index = manageItemIntent.getIntExtra(StaticData.EXTRA_INTENT_KEY_INDEX, 0);
            }else {
                // er is geen intent, index ophalen als Cookie
                index = parseInt(cookieRepository.getCookieValueFromLabel(SpecificData.COOKIE_RETURN_UPDATE_INDEX));
            }
        }
        fragmentBundle.putString(StaticData.EXTRA_INTENT_KEY_ACTION, action);
        fragmentBundle.putInt(StaticData.EXTRA_INTENT_KEY_INDEX, index);
        // Dit is niet nodig want gans het viewmodel wordt gerecupereerd in het fragment ?
        //fragmentBundle.putString(StaticData.EXTRA_INTENT_KEY_FILE_BASE_DIR, fileBaseService.getFileBaseDir());

        // Bewaar terugkeerkruimels als cookies
        cookieRepository.addCookie(new Cookie(SpecificData.COOKIE_RETURN_ENTITY_TYPE, listType));
        cookieRepository.addCookie(new Cookie(SpecificData.COOKIE_RETURN_ACTION, action));
        cookieRepository.addCookie(new Cookie(SpecificData.COOKIE_RETURN_UPDATE_INDEX, String.valueOf(index)));

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
                if (savedInstanceState == null){
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragmentShopEntity, MealFragment.class, fragmentBundle)
                            .commit();
                }
                break;
        }
    }
}
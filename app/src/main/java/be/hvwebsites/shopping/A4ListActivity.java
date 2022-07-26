package be.hvwebsites.shopping;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import be.hvwebsites.libraryandroid4.repositories.Cookie;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.fragments.CheckBoxListFragment;
import be.hvwebsites.shopping.fragments.TextItemListFragment;
import be.hvwebsites.shopping.services.FileBaseService;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class A4ListActivity extends AppCompatActivity {
    // Device
    private final String deviceModel = Build.MODEL;
    private ShopEntitiesViewModel viewModel;
    private String listType;
    private String filebaseDir = "";
    private String baseSwitch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a4_list);

        // Intent definieren
        Intent listIntent = getIntent();

        // File directory bepalen
        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel, getPackageName());
        baseSwitch = fileBaseService.getFileBase();
        filebaseDir = fileBaseService.getFileBaseDir();

        Toast.makeText(A4ListActivity.this,
                "filebase is " + baseSwitch,
                Toast.LENGTH_SHORT).show();

        /** Data ophalen */
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(ShopEntitiesViewModel.class);
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(filebaseDir);
        if (viewModelStatus.getReturnCode() == 0) {
            // Files gelezen
            // Baseswitch in viewmodel zetten is echt nodig voor de creatie ve entity
            // bij toevoegen in de fragmenten
            viewModel.setBaseSwitch(baseSwitch);
        } else {
            Toast.makeText(A4ListActivity.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // ProductinShops clearen uit commentaar zetten indien nodig
        int temp = 0;
        if (temp == 1){
            viewModel.clearAllProductInShop();
        }
        int highestShop = viewModel.determineHighestID(viewModel.getShopList());
        int highestProd = viewModel.determineHighestID(viewModel.getProductList());

        // Data uit intent halen als die er is
        CookieRepository cookieRepository = new CookieRepository(filebaseDir);
        if (listIntent.hasExtra(StaticData.EXTRA_INTENT_KEY_TYPE)){
            listType = listIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_TYPE);
            // listtype in cookie steken
            cookieRepository.addCookie(new Cookie(SpecificData.LIST_TYPE, listType));
        }else {
            // er is geen intent, listtype ophalen als Cookie
            listType = cookieRepository.getCookieValueFromLabel(SpecificData.LIST_TYPE);
        }

        // Bundle voorbereiden om mee te geven aan fragment
        Bundle fragmentBundle = new Bundle();
        fragmentBundle.putString(SpecificData.LIST_TYPE, listType);
        fragmentBundle.putString(SpecificData.CALLING_ACTIVITY, SpecificData.ACTIVITY_A4LIST);

        // Creeer fragment vr gepaste recyclerview
        if (savedInstanceState == null ){
            switch (listType){
                case SpecificData.LIST_TYPE_1:
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragmentRecyclerV, TextItemListFragment.class, fragmentBundle)
                            .commit();
                    break;
                default:
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragmentRecyclerV, CheckBoxListFragment.class, fragmentBundle)
                            .commit();
                    break;
            }
        }

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(A4ListActivity.this,
                        ManageItemActivity.class);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, listType);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_ACTION, StaticData.ACTION_NEW);
//                intent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE, baseSwitch);
                startActivity(intent);
            }
        });

        // data zit nu in ViewModel, tonen op scherm afhankelijk vn type
        switch (listType) {
            case SpecificData.LIST_TYPE_1:
                setTitle(SpecificData.TITLE_LIST_ACTIVITY_T1);
                if (viewModel.getShopList().size() == 0){
                    Toast.makeText(A4ListActivity.this,
                            SpecificData.TOAST_NO_SHOPS,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case SpecificData.LIST_TYPE_2:
                setTitle(SpecificData.TITLE_LIST_ACTIVITY_T2);
                if (viewModel.getProductList().size() == 0) {
                    Toast.makeText(A4ListActivity.this,
                            SpecificData.TOAST_NO_PRODUCTS,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            case SpecificData.LIST_TYPE_3:
                setTitle(SpecificData.TITLE_LIST_ACTIVITY_T3);
                if (viewModel.getMealList().size() == 0) {
                    Toast.makeText(A4ListActivity.this,
                            SpecificData.TOAST_NO_MEALS,
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + listType);
        }
    }
}
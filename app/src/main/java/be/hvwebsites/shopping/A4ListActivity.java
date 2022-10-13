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
import be.hvwebsites.shopping.fragments.ProductListFragment;
import be.hvwebsites.shopping.fragments.ShopListFragment;
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

/*
        baseSwitch = listIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE);
        if (listIntent.hasExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE_DIR)){
            fileBaseDir = listIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE_DIR);
        }else {
            if (baseSwitch == null){
                baseSwitch = SpecificData.BASE_DEFAULT;
            }
            if (baseSwitch.equals(SpecificData.BASE_INTERNAL)){
                fileBaseDir = getBaseContext().getFilesDir().getAbsolutePath();
            }else {
                fileBaseDir = getBaseContext().getExternalFilesDir(null).getAbsolutePath();
            }
        }
*/

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
            viewModel.setBaseSwitch(baseSwitch);
        } else if (viewModelStatus.getReturnCode() == 100) {
            Toast.makeText(A4ListActivity.this,
                    viewModelStatus.getReturnMessage(),
                    Toast.LENGTH_LONG).show();
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
        int highestShop = viewModel.determineHighestShopID();
        int highestProd = viewModel.determineHighestProductID();

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

        // Creeer fragment vr gepaste recyclerview
        if (savedInstanceState == null ){
            getSupportFragmentManager().beginTransaction()
                    .setReorderingAllowed(true)
                    .add(R.id.fragmentRecyclerV, CheckBoxListFragment.class, fragmentBundle)
                    .commit();
            //TODO: Shoplistfragment nog vervangen dr een generieker !
            switch (listType){
                case SpecificData.LIST_TYPE_1:
                    getSupportFragmentManager().beginTransaction()
                            .setReorderingAllowed(true)
                            .add(R.id.fragmentRecyclerV, ShopListFragment.class, fragmentBundle)
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
                break;
            case SpecificData.LIST_TYPE_2:
                setTitle(SpecificData.TITLE_LIST_ACTIVITY_T2);
                break;
            case SpecificData.LIST_TYPE_3:
                setTitle(SpecificData.TITLE_LIST_ACTIVITY_T3);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + listType);
        }
    }
}
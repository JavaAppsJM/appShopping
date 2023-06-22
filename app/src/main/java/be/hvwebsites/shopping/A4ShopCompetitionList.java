package be.hvwebsites.shopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.adapters.NothingSelectedSpinnerAdapter;
import be.hvwebsites.libraryandroid4.helpers.CheckboxHelper;
import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.shopping.adapters.CheckboxListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Product;
import be.hvwebsites.shopping.entities.Shop;
import be.hvwebsites.shopping.services.FileBaseService;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class A4ShopCompetitionList extends AppCompatActivity {
    // Device
    private final String deviceModel = Build.MODEL;
    private ShopEntitiesViewModel viewModel;
    private CookieRepository cookieRepository;
//    private boolean smsOn = false;
//    private List<Product> productsMatchingShopfilter = new ArrayList<>();
//    private List<Product> prodinShopMatchingShopFilter = new ArrayList<>();
    private List<CheckboxHelper> checkboxList = new ArrayList<>();
    private CheckboxListAdapter cbListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a4_shop_competition_list);

        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel, getPackageName());

        /** Data ophalen */
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(ShopEntitiesViewModel.class);
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(fileBaseService.getFileBaseDir());
        if (viewModelStatus.getReturnCode() != 0) {
            Toast.makeText(A4ShopCompetitionList.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }


        // Recyclerview definieren
        RecyclerView recyclerView = findViewById(R.id.recyclShopCompetitionList);
        // ChckbxListAdapter kan vervangen worden door CheckBoxListAdapter !!
        cbListAdapter = new CheckboxListAdapter(this);
        recyclerView.setAdapter(cbListAdapter);
        LinearLayoutManager cbLineairLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(cbLineairLayoutManager);
        // Indien CheckBoxListAdapter gekozen wordt, dan vlgnd statement geactiveerd worden !!
        cbListAdapter.setActivityMaster(SpecificData.ACTIVITY_A4SHOPPINGLIST);

        // Invullen adapter vd recyclerview met checkboxlist
        cbListAdapter.setReference(SpecificData.LIST_TYPE_2);
        cbListAdapter.setCheckboxList(checkboxList);
        setTitle(SpecificData.TITLE_SHOPPING_LIST);


    }
}
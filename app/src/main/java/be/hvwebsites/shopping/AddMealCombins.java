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
        recycMealCombinsAdapter.setClient(SpecificData.ACTIVITY_ADDMEALCOMBINS);

        RecyclerView recycNotMealCombins = findViewById(R.id.recycNotCombins);
        SmartTextItemListAdapter recycMealNotCombinsAdapter = new SmartTextItemListAdapter(this);
        recycNotMealCombins.setAdapter(recycMealNotCombinsAdapter);
        recycNotMealCombins.setLayoutManager(new LinearLayoutManager(this));
        recycMealNotCombinsAdapter.setClient(SpecificData.ACTIVITY_ADDMEALCOMBINS);

        // Recyclerlists vullen afhankelijk van combinationType
        switch (combinationType){
            case SpecificData.SC_PRODUCTSMEAL:
                // Vullen met productsmeal
                recycMealCombinsAdapter.setReusableList(viewModel.getProductNamesByMeal(mealToManage));
                // Vullen met nog niet gekoppelde producten
                recycMealNotCombinsAdapter.setReusableList(
                        getMissingProducts(recycMealCombinsAdapter.getReusableList()));
                break;
            case SpecificData.SC_SUBMEAL:
                // Vullen met submeals
                recycMealCombinsAdapter.setReusableList(viewModel.getChildMealNamesByMeal(mealToManage));
                // Vullen met nog niet gekoppelde submeals
                recycMealNotCombinsAdapter.setReusableList(
                        getMissingMeals(recycMealCombinsAdapter.getReusableList(), mealToManage));
                break;
            case SpecificData.SC_PARENTMEAL:
                // Vullen met parentmeals
                recycMealCombinsAdapter.setReusableList(viewModel.getParentMealNamesByMeal(mealToManage));
                // Vullen met nog niet gekoppelde parentmeals
                recycMealNotCombinsAdapter.setReusableList(
                        getMissingMeals(recycMealCombinsAdapter.getReusableList(), mealToManage));
                break;
            default:
        }
        // Als er geclicked is op een item, wordt dat hier gecapteerd
        recycMealCombinsAdapter.setOnItemClickListener(new SmartTextItemListAdapter.ClickListener() {
            @Override
            public void onItemClicked(int position, View v) {
                // via het itemID vd checkboxhelper het juiste produkt bepalen en de toBuy zetten
                Product clickedProduct = viewModel.getProductByID(checkboxList.get(position).getItemID());
                clickedProduct.setToBuy(checked);
//                viewModel.getProductByID(checkboxList.get(position).getItemID()).setToBuy(checked);
                viewModel.storeProducts();
                composeCheckboxList();
                cbListAdapter.setCheckboxList(checkboxList);

                // Voorbereiden SMS msg indien sms on
                if (smsOn){
                    String smsMsg = clickedProduct.getSMSLine();
                    String smsReceiver = StaticData.SMS_RECEIVER_DEFAULT;
                    //Getting intent and PendingIntent instance
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent,0);
                    //Get the SmsManager instance and call the sendTextMessage method to send message
                    SmsManager sms = SmsManager.getDefault();
                    sms.sendTextMessage(smsReceiver, null, smsMsg, pi,null);

                    Toast.makeText(getApplicationContext(), "Message Sent!!",
                            Toast.LENGTH_LONG).show();
                }
                boolean debug = true;
            }
        });
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
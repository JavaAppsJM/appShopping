package be.hvwebsites.shopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.helpers.ListItemHelper;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.adapters.SmartTextItemListAdapter;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Meal;
import be.hvwebsites.shopping.entities.MealInMeal;
import be.hvwebsites.shopping.entities.Product;
import be.hvwebsites.shopping.entities.ProductInMeal;
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
    private List<Meal> remainingMeals = new ArrayList<>();
    //private SmartTextItemListAdapter recycMealCombinsAdapter = new SmartTextItemListAdapter(this);
    //private SmartTextItemListAdapter recycMealNotCombinsAdapter = new SmartTextItemListAdapter(this);

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
        setTitle("Koppel " + mealToManage.getEntityName() + " aan ...");
        TextView instAddMealCombin = findViewById(R.id.instCombinItems);
        TextView labelAddMealCombin = findViewById(R.id.labelCombinItems);
        TextView labelAddMealNotCombin = findViewById(R.id.labelNotCombinItems);
        switch (combinationType){
            case SpecificData.SC_PRODUCTSMEAL:
                instAddMealCombin.setText(SpecificData.INSTRUCTION_ADDMEALCOMBIN_T1);
                labelAddMealCombin.setText(SpecificData.HEAD_ADDMEALCOMBIN_COMBINS_T1);
                labelAddMealNotCombin.setText(SpecificData.HEAD_ADDMEALCOMBIN_NOTCOMBINS_T1);
                break;
            case SpecificData.SC_SUBMEAL:
                instAddMealCombin.setText(SpecificData.INSTRUCTION_ADDMEALCOMBIN_T2);
                labelAddMealCombin.setText(SpecificData.HEAD_ADDMEALCOMBIN_COMBINS_T2_3);
                labelAddMealNotCombin.setText(SpecificData.HEAD_ADDMEALCOMBIN_NOTCOMBINS_T2_3);
                break;
            case SpecificData.SC_PARENTMEAL:
                instAddMealCombin.setText(SpecificData.INSTRUCTION_ADDMEALCOMBIN_T3);
                labelAddMealCombin.setText(SpecificData.HEAD_ADDMEALCOMBIN_COMBINS_T2_3);
                labelAddMealNotCombin.setText(SpecificData.HEAD_ADDMEALCOMBIN_NOTCOMBINS_T2_3);
                break;
            default:
        }
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

        // RecycleList vullen voor eerste keer
        fillMealCombins();
        recycMealCombinsAdapter.setReusableList(mealCombins);
        recycMealNotCombinsAdapter.setReusableList(mealNotCombins);
        //fillReusablelist();

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
                        viewModel.storeProdsInMeal();
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
                        viewModel.storeMealInMeal();
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
                        if (indexCombinToDelete != StaticData.ITEM_NOT_FOUND){
                            viewModel.getMealInMealList().remove(indexCombinToDelete);
                        }
                        viewModel.storeMealInMeal();
                        break;
                    default:
                }
                fillMealCombins();
                recycMealCombinsAdapter.setReusableList(mealCombins);
                recycMealNotCombinsAdapter.setReusableList(mealNotCombins);
                //fillReusablelist();
            }
        });
        // Als er geclicked is op een item inde mealNotcombins recycler list, wordt dat hier gecapteerd
        recycMealNotCombinsAdapter.setOnItemClickListener(new SmartTextItemListAdapter.ClickListener() {
            @Override
            public void onItemClicked(int position, View v) {
                // via het itemID vd listitemhelper het juiste item bepalen en de combinatie
                // met mealToManage toevoegen
                // Verwerken afhankelijk vn combinatieType
                switch (combinationType){
                    case SpecificData.SC_PRODUCTSMEAL:
                        // Verbinding meal en product toevoegen
                        // Product bepalen
                        Product clickedProduct = viewModel.getProductByID(mealNotCombins.get(position).getItemID());
                        if (viewModel.getIndexByIdsFromCList(viewModel.getProductInMealList(),
                                mealToManage.getEntityId(),
                                clickedProduct.getEntityId()) == StaticData.ITEM_NOT_FOUND){
                            // Combinatie bestaat niet, moet gecreerd worden
                            viewModel.getProductInMealList().add(new ProductInMeal(clickedProduct.getEntityId(),
                                    mealToManage.getEntityId()));
                            viewModel.storeProdsInMeal();
                        }
                        break;
                    case SpecificData.SC_SUBMEAL:
                        // Verbinding meal en submeal toevoegen
                        // Submeal bepalen
                        Meal clickedSubMeal = viewModel.getMealByID(mealNotCombins.get(position).getItemID());
                        if (viewModel.getIndexByIdsFromCList(viewModel.getMealInMealList(),
                                mealToManage.getEntityId(),
                                clickedSubMeal.getEntityId()) == StaticData.ITEM_NOT_FOUND){
                            // Combinatie bestaat niet, moet gecreerd worden
                            viewModel.getMealInMealList().add(new MealInMeal(mealToManage.getEntityId(),
                                    clickedSubMeal.getEntityId()));
                            viewModel.storeMealInMeal();
                        }
                        break;
                    case SpecificData.SC_PARENTMEAL:
                        // Verbinding meal en parentmeal verbreken
                        // Parentmeal bepalen
                        Meal clickedParentMeal = viewModel.getMealByID(mealNotCombins.get(position).getItemID());
                        if (viewModel.getIndexByIdsFromCList(viewModel.getMealInMealList(),
                                clickedParentMeal.getEntityId(),
                                mealToManage.getEntityId()) == StaticData.ITEM_NOT_FOUND){
                            // Combinatie bestaat niet, moet gecreerd worden
                            viewModel.getMealInMealList().add(new MealInMeal(clickedParentMeal.getEntityId(),
                                    mealToManage.getEntityId()));
                            viewModel.storeMealInMeal();
                        }
                        break;
                    default:
                }
                fillMealCombins();
                recycMealCombinsAdapter.setReusableList(mealCombins);
                recycMealNotCombinsAdapter.setReusableList(mealNotCombins);
                //fillReusablelist();
            }
        });
    }

    private void fillMealCombins(){
        // MealCombins vullen afhankelijk van combinationType
        switch (combinationType){
            case SpecificData.SC_PRODUCTSMEAL:
                // Vullen met productsmeal
                mealCombins.clear();
                mealCombins.addAll(viewModel.getProductNamesByMeal(mealToManage));
                //recycMealCombinsAdapter.setReusableList(mealCombins);
                // Vullen met nog niet gekoppelde producten
                mealNotCombins.clear();
                mealNotCombins.addAll(getMissingProducts(mealCombins));
                //recycMealNotCombinsAdapter.setReusableList(mealNotCombins);
                break;
            case SpecificData.SC_SUBMEAL:
                // Vullen met submeals
                mealCombins.clear();
                mealCombins.addAll(viewModel.getChildMealNamesByMeal(mealToManage));
                //recycMealCombinsAdapter.setReusableList(mealCombins);
                // Vullen met nog niet gekoppelde submeals
                mealNotCombins.clear();
                mealNotCombins.addAll(getMissingMeals(mealCombins, mealToManage));
                //recycMealNotCombinsAdapter.setReusableList(mealNotCombins);
                break;
            case SpecificData.SC_PARENTMEAL:
                // Vullen met parentmeals
                mealCombins.clear();
                mealCombins.addAll(viewModel.getParentMealNamesByMeal(mealToManage));
                //recycMealCombinsAdapter.setReusableList(mealCombins);
                // Vullen met nog niet gekoppelde parentmeals
                mealNotCombins.clear();
                mealNotCombins.addAll(getMissingMeals(mealCombins, mealToManage));
                //recycMealNotCombinsAdapter.setReusableList(mealNotCombins);
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
        remainingMeals.clear();

        for (int i = 0; i < viewModel.getMealList().size(); i++) {
            // Elk meal dat niet in de inList zit is een missing meal
            Meal tempMeal = viewModel.getMealList().get(i);
            if (notExistIdInList(tempMeal.getEntityId().getId(), inList) &&
                    (tempMeal.getEntityId().getId() != excludeMeal.getEntityId().getId())){
                remainingMeals.add(tempMeal);
                //missingMeals.add(new ListItemHelper(tempMeal.getEntityName(), "", tempMeal.getEntityId()));
            }
        }
        // Corrigeer remainingmeals voor parents vn parents
        correctRemainingMealsParents(excludeMeal.getEntityId().getId());

        // Corrigeer missingmeals voor children vn children
        correctRemainingMealsChildren(excludeMeal.getEntityId().getId());

        // Zet remainingmeals om nr listitemhelpers
        missingMeals = convertRemMealsInMissing();

        return missingMeals;
    }

    private List<ListItemHelper> convertRemMealsInMissing(){
        List<ListItemHelper> missingRemMeals = new ArrayList<>();

        for (int i = 0; i < remainingMeals.size(); i++) {
            missingRemMeals.add(new ListItemHelper(
                    remainingMeals.get(i).getEntityName(),
                    "",
                    remainingMeals.get(i).getEntityId()));
        }
        return missingRemMeals;
    }

    private void correctRemainingMealsParents(int inMealId){
        List<Integer> parentsId = new ArrayList<>();
        // Bepaal parents vn inMeal
        parentsId.addAll(viewModel.getFirstIdsBySecondId(viewModel.getMealInMealList(), inMealId));

        for (int i = 0; i < parentsId.size(); i++) {
            // Verwijder parent uit remaininglist
            int indexRemList = getIndexInRemListForMeal(parentsId.get(i));
            if (indexRemList != StaticData.ITEM_NOT_FOUND){
                remainingMeals.remove(indexRemList);
            }
            correctRemainingMealsParents(parentsId.get(i));
        }

    }

    private int getIndexInRemListForMeal(int inMealId){
        // Bepaal index vn inMeal in remaininglist
        for (int i = 0; i < remainingMeals.size(); i++) {
            if (remainingMeals.get(i).getEntityId().getId() == inMealId){
                return i;
            }
        }
        return StaticData.ITEM_NOT_FOUND;
    }

    private void correctRemainingMealsChildren(int inMealId){
        List<Integer> childrenId = new ArrayList<>();
        // Bepaal children vn inMeal
        childrenId.addAll(viewModel.getSecondIdsByFirstId(viewModel.getMealInMealList(), inMealId));

        for (int i = 0; i < childrenId.size(); i++) {
            // Verwijder child uit remaininglist
            int indexRemList = getIndexInRemListForMeal(childrenId.get(i));
            if (indexRemList != StaticData.ITEM_NOT_FOUND){
                remainingMeals.remove(indexRemList);
            }
            correctRemainingMealsChildren(childrenId.get(i));
        }
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
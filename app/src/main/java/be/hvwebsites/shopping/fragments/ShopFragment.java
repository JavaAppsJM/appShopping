package be.hvwebsites.shopping.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import java.sql.Time;

import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.A4ListActivity;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Shop;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;
import be.hvwebsites.shopping.R;

public class ShopFragment extends Fragment {
    private ShopEntitiesViewModel viewModel;
    private int indexToUpdate = 0;

    // Toegevoegd vanuit android tutorial
    public ShopFragment(){
        super(R.layout.fragment_shop);
    }

    private FragmentManager getSupportFragmentManager() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_shop, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Definitie openingsuren
        EditText moOpenFromH = view.findViewById(R.id.editMoFromH);
        EditText moOpenFromM = view.findViewById(R.id.editMoFromM);
        EditText moOpentillH = view.findViewById(R.id.editMoTillH);
        EditText moOpentillM = view.findViewById(R.id.editMoTillM);
        EditText diOpenFromH = view.findViewById(R.id.editDiFromH);
        EditText diOpenFromM = view.findViewById(R.id.editDiFromM);
        EditText diOpentillH = view.findViewById(R.id.editDiTillH);
        EditText diOpentillM = view.findViewById(R.id.editDiTillM);
        EditText woOpenFromH = view.findViewById(R.id.editWoFromH);
        EditText woOpenFromM = view.findViewById(R.id.editWoFromM);
        EditText woOpentillH = view.findViewById(R.id.editWoTillH);
        EditText woOpentillM = view.findViewById(R.id.editWoTillM);
        EditText doOpenFromH = view.findViewById(R.id.editDoFromH);
        EditText doOpenFromM = view.findViewById(R.id.editDoFromM);
        EditText doOpentillH = view.findViewById(R.id.editDoTillH);
        EditText doOpentillM = view.findViewById(R.id.editDoTillM);
        EditText vrOpenFromH = view.findViewById(R.id.editFrFromH);
        EditText vrOpenFromM = view.findViewById(R.id.editFrFromM);
        EditText vrOpentillH = view.findViewById(R.id.editFrTillH);
        EditText vrOpentillM = view.findViewById(R.id.editFrTillM);
        EditText saOpenFromH = view.findViewById(R.id.editSaFromH);
        EditText saOpenFromM = view.findViewById(R.id.editSaFromM);
        EditText saOpentillH = view.findViewById(R.id.editSaTillH);
        EditText saOpentillM = view.findViewById(R.id.editSaTillM);
        EditText suOpenFromH = view.findViewById(R.id.editSuFromH);
        EditText suOpenFromM = view.findViewById(R.id.editSuFromM);
        EditText suOpentillH = view.findViewById(R.id.editSuTillH);
        EditText suOpentillM = view.findViewById(R.id.editSuTillM);
        TextView isShopOpenV = view.findViewById(R.id.textShopOpen);

        // Definitie button
        Button saveButton = view.findViewById(R.id.addButtonShop);
        saveButton.setText(SpecificData.BUTTON_TOEVOEGEN);

        // Via het viewmodel uit de activity kan je over de data beschikken !
        viewModel = new ViewModelProvider(requireActivity()).get(ShopEntitiesViewModel.class);

        // Wat zijn de argumenten die werden meegegeven
        String action = requireArguments().getString(StaticData.EXTRA_INTENT_KEY_ACTION);
        if (action.equals(StaticData.ACTION_UPDATE)){
            indexToUpdate = requireArguments().getInt(StaticData.EXTRA_INTENT_KEY_INDEX);
            // Bepaal geselecteerde shop bepalen obv meegegeven index
            Shop shopToUpdate = viewModel.getShopList().get(indexToUpdate);

            // Vul Scherm in met gegevens
            EditText nameView = view.findViewById(R.id.editNameNewShop);
            nameView.setText(shopToUpdate.getEntityName());

            // Invullen openingsuren
            moOpenFromH.setText(shopToUpdate.getMonday().getOpenFromHForm());
            moOpenFromM.setText(shopToUpdate.getMonday().getOpenFromMinForm());
            moOpentillH.setText(shopToUpdate.getMonday().getOpenTillHForm());
            moOpentillM.setText(shopToUpdate.getMonday().getOpenTillMinForm());
            diOpenFromH.setText(shopToUpdate.getTuesday().getOpenFromHForm());
            diOpenFromM.setText(shopToUpdate.getTuesday().getOpenFromMinForm());
            diOpentillH.setText(shopToUpdate.getTuesday().getOpenTillHForm());
            diOpentillM.setText(shopToUpdate.getTuesday().getOpenTillMinForm());
            woOpenFromH.setText(shopToUpdate.getWensday().getOpenFromHForm());
            woOpenFromM.setText(shopToUpdate.getWensday().getOpenFromMinForm());
            woOpentillH.setText(shopToUpdate.getWensday().getOpenTillHForm());
            woOpentillM.setText(shopToUpdate.getWensday().getOpenTillMinForm());
            doOpenFromH.setText(shopToUpdate.getThursday().getOpenFromHForm());
            doOpenFromM.setText(shopToUpdate.getThursday().getOpenFromMinForm());
            doOpentillH.setText(shopToUpdate.getThursday().getOpenTillHForm());
            doOpentillM.setText(shopToUpdate.getThursday().getOpenTillMinForm());
            vrOpenFromH.setText(shopToUpdate.getFriday().getOpenFromHForm());
            vrOpenFromM.setText(shopToUpdate.getFriday().getOpenFromMinForm());
            vrOpentillH.setText(shopToUpdate.getFriday().getOpenTillHForm());
            vrOpentillM.setText(shopToUpdate.getFriday().getOpenTillMinForm());
            saOpenFromH.setText(shopToUpdate.getSatday().getOpenFromHForm());
            saOpenFromM.setText(shopToUpdate.getSatday().getOpenFromMinForm());
            saOpentillH.setText(shopToUpdate.getSatday().getOpenTillHForm());
            saOpentillM.setText(shopToUpdate.getSatday().getOpenTillMinForm());
            suOpenFromH.setText(shopToUpdate.getSunday().getOpenFromHForm());
            suOpenFromM.setText(shopToUpdate.getSunday().getOpenFromMinForm());
            suOpentillH.setText(shopToUpdate.getSunday().getOpenTillHForm());
            suOpentillM.setText(shopToUpdate.getSunday().getOpenTillMinForm());

            // Is de winkel open
            isShopOpenV.setText(shopToUpdate.getTextOpenShop());

            // Invullen text button
            saveButton.setText(SpecificData.BUTTON_AANPASSEN);
        }

        // Als maandag ingevuld wordt, wordt rest vd week overgenomen
        moOpenFromH.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // Welke view has changed
                if (view.getId() == moOpenFromH.getId() && !b){
                    // Invullen andere dagen
                    diOpenFromH.setText(moOpenFromH.getText());
                    woOpenFromH.setText(moOpenFromH.getText());
                    doOpenFromH.setText(moOpenFromH.getText());
                    vrOpenFromH.setText(moOpenFromH.getText());
                    saOpenFromH.setText(moOpenFromH.getText());
                    suOpenFromH.setText(moOpenFromH.getText());
                }
            }
        });
        moOpenFromM.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // Welke view has changed
                if (view.getId() == moOpenFromM.getId() && !b){
                    // Invullen andere dagen
                    diOpenFromM.setText(moOpenFromM.getText());
                    woOpenFromM.setText(moOpenFromM.getText());
                    doOpenFromM.setText(moOpenFromM.getText());
                    vrOpenFromM.setText(moOpenFromM.getText());
                    saOpenFromM.setText(moOpenFromM.getText());
                    suOpenFromM.setText(moOpenFromM.getText());
                }
            }
        });
        moOpentillH.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // Welke view has changed
                if (view.getId() == moOpentillH.getId() && !b){
                    // Invullen andere dagen
                    diOpentillH.setText(moOpentillH.getText());
                    woOpentillH.setText(moOpentillH.getText());
                    doOpentillH.setText(moOpentillH.getText());
                    vrOpentillH.setText(moOpentillH.getText());
                    saOpentillH.setText(moOpentillH.getText());
                    suOpentillH.setText(moOpentillH.getText());
                }
            }
        });
        moOpentillM.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                // Welke view has changed
                if (view.getId() == moOpentillM.getId() && !b){
                    // Invullen andere dagen
                    diOpentillM.setText(moOpentillM.getText());
                    woOpentillM.setText(moOpentillM.getText());
                    doOpentillM.setText(moOpentillM.getText());
                    vrOpentillM.setText(moOpentillM.getText());
                    saOpentillM.setText(moOpentillM.getText());
                    suOpentillM.setText(moOpentillM.getText());
                }
            }
        });


        // Als button ingedrukt wordt...
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Definitie inputvelden
                EditText nameView = view.findViewById(R.id.editNameNewShop);
                if (action.equals(StaticData.ACTION_UPDATE)){
                    viewModel.getShopList().get(indexToUpdate)
                            .setEntityName(String.valueOf(nameView.getText()));

                    // Opnemen openingsuren
                    viewModel.getShopList().get(indexToUpdate).getMonday()
                            .setOpenFromHour(Integer.parseInt(String.valueOf(moOpenFromH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getMonday()
                            .setOpenFromMinutes(Integer.parseInt(String.valueOf(moOpenFromM.getText())));
                    viewModel.getShopList().get(indexToUpdate).getMonday()
                            .setOpenTillHour(Integer.parseInt(String.valueOf(moOpentillH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getMonday()
                            .setOpenTillMinutes(Integer.parseInt(String.valueOf(moOpentillM.getText())));

                    viewModel.getShopList().get(indexToUpdate).getTuesday()
                            .setOpenFromHour(Integer.parseInt(String.valueOf(diOpenFromH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getTuesday()
                            .setOpenFromMinutes(Integer.parseInt(String.valueOf(diOpenFromM.getText())));
                    viewModel.getShopList().get(indexToUpdate).getTuesday()
                            .setOpenTillHour(Integer.parseInt(String.valueOf(diOpentillH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getTuesday()
                            .setOpenTillMinutes(Integer.parseInt(String.valueOf(diOpentillM.getText())));

                    viewModel.getShopList().get(indexToUpdate).getWensday()
                            .setOpenFromHour(Integer.parseInt(String.valueOf(woOpenFromH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getWensday()
                            .setOpenFromMinutes(Integer.parseInt(String.valueOf(woOpenFromM.getText())));
                    viewModel.getShopList().get(indexToUpdate).getWensday()
                            .setOpenTillHour(Integer.parseInt(String.valueOf(woOpentillH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getWensday()
                            .setOpenTillMinutes(Integer.parseInt(String.valueOf(woOpentillM.getText())));

                    viewModel.getShopList().get(indexToUpdate).getThursday()
                            .setOpenFromHour(Integer.parseInt(String.valueOf(doOpenFromH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getThursday()
                            .setOpenFromMinutes(Integer.parseInt(String.valueOf(doOpenFromM.getText())));
                    viewModel.getShopList().get(indexToUpdate).getThursday()
                            .setOpenTillHour(Integer.parseInt(String.valueOf(doOpentillH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getThursday()
                            .setOpenTillMinutes(Integer.parseInt(String.valueOf(doOpentillM.getText())));

                    viewModel.getShopList().get(indexToUpdate).getFriday()
                            .setOpenFromHour(Integer.parseInt(String.valueOf(vrOpenFromH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getFriday()
                            .setOpenFromMinutes(Integer.parseInt(String.valueOf(vrOpenFromM.getText())));
                    viewModel.getShopList().get(indexToUpdate).getFriday()
                            .setOpenTillHour(Integer.parseInt(String.valueOf(vrOpentillH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getFriday()
                            .setOpenTillMinutes(Integer.parseInt(String.valueOf(vrOpentillM.getText())));

                    viewModel.getShopList().get(indexToUpdate).getSatday()
                            .setOpenFromHour(Integer.parseInt(String.valueOf(saOpenFromH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getSatday()
                            .setOpenFromMinutes(Integer.parseInt(String.valueOf(saOpenFromM.getText())));
                    viewModel.getShopList().get(indexToUpdate).getSatday()
                            .setOpenTillHour(Integer.parseInt(String.valueOf(saOpentillH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getSatday()
                            .setOpenTillMinutes(Integer.parseInt(String.valueOf(saOpentillM.getText())));

                    viewModel.getShopList().get(indexToUpdate).getSunday()
                            .setOpenFromHour(Integer.parseInt(String.valueOf(suOpenFromH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getSunday()
                            .setOpenFromMinutes(Integer.parseInt(String.valueOf(suOpenFromM.getText())));
                    viewModel.getShopList().get(indexToUpdate).getSunday()
                            .setOpenTillHour(Integer.parseInt(String.valueOf(suOpentillH.getText())));
                    viewModel.getShopList().get(indexToUpdate).getSunday()
                            .setOpenTillMinutes(Integer.parseInt(String.valueOf(suOpentillM.getText())));
                }else {
                    Shop newShop = new Shop(viewModel.getBasedir(), false);
                    newShop.setEntityName(String.valueOf(nameView.getText()));
                    viewModel.getShopList().add(newShop);
                }
                viewModel.sortShopList();
                viewModel.storeShops();
                Intent replyIntent = new Intent(getContext(), A4ListActivity.class);
                replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, SpecificData.LIST_TYPE_1);
                replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE, viewModel.getBaseSwitch());
                startActivity(replyIntent);
            }
        });
    }
}
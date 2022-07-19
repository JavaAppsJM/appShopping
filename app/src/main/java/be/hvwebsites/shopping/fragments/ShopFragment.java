package be.hvwebsites.shopping.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

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
        // button
        Button saveButton = view.findViewById(R.id.addButtonShop);
        saveButton.setText(SpecificData.BUTTON_TOEVOEGEN);

        // Via het viewmodel uit de activity kan je over de data beschikken !
        viewModel = new ViewModelProvider(requireActivity()).get(ShopEntitiesViewModel.class);

        // Wat zijn de argumenten die werden meegegeven
        String action = requireArguments().getString(StaticData.EXTRA_INTENT_KEY_ACTION);
        if (action.equals(StaticData.ACTION_UPDATE)){
            indexToUpdate = requireArguments().getInt(StaticData.EXTRA_INTENT_KEY_INDEX);
            // Bepaal geselecteerde shop bepalen obv meegegeven index
            Shop shopToUpdate = (Shop) viewModel.getShopList().get(indexToUpdate);
            // Vul Scherm in met gegevens
            EditText nameView = view.findViewById(R.id.editNameNewShop);
            nameView.setText(shopToUpdate.getEntityName());
            saveButton.setText(SpecificData.BUTTON_AANPASSEN);
        }

        // Als button ingedrukt wordt...
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Definitie inputvelden
                EditText nameView = view.findViewById(R.id.editNameNewShop);
                if (action.equals(StaticData.ACTION_UPDATE)){
                    viewModel.getShopList().get(indexToUpdate).setEntityName(String.valueOf(nameView.getText()));
                }else {
                    Shop newShop = new Shop(viewModel.getBasedir(), false);
                    newShop.setEntityName(String.valueOf(nameView.getText()));
                    viewModel.getShopList().add(newShop);
                }
                viewModel.storeShops();
                Intent replyIntent = new Intent(getContext(), A4ListActivity.class);
                replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, SpecificData.LIST_TYPE_1);
                replyIntent.putExtra(StaticData.EXTRA_INTENT_KEY_SELECTION, viewModel.getBaseSwitch());
                startActivity(replyIntent);
            }
        });
    }
}
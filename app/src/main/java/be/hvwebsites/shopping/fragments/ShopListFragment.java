package be.hvwebsites.shopping.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import be.hvwebsites.winkelen.R;
import be.hvwebsites.winkelen.adapters.TextItemListAdapter;
import be.hvwebsites.winkelen.constants.SpecificData;
import be.hvwebsites.winkelen.viewmodels.ShopEntitiesViewModel;

public class ShopListFragment extends Fragment {
    private ShopEntitiesViewModel viewModel;
    private List<String> lineList = new ArrayList<>();

    // TODO:Toegevoegd vanuit android tutorial
    public ShopListFragment(){
        super(R.layout.fragment_text_item_recycler);

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
        return inflater.inflate(R.layout.fragment_text_item_recycler, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Via het viewmodel uit de activity kan je over de data beschikken !
        viewModel = new ViewModelProvider(requireActivity()).get(ShopEntitiesViewModel.class);

        // Recyclerview definieren
        RecyclerView recyclerView = view.findViewById(R.id.recyclerviewShops);
        final TextItemListAdapter adapter = new TextItemListAdapter(this.getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        TextView labelColHead1 = view.findViewById(R.id.listColHeadShops);
        labelColHead1.setText(SpecificData.HEAD_LIST_ACTIVITY_T1);
        lineList.clear();
        lineList.addAll(viewModel.getShopNameList());

        // om te kunnen swipen in de recyclerview ; swippen == deleten
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                    @Override
                    public boolean onMove(@NonNull RecyclerView recyclerView,
                                          @NonNull RecyclerView.ViewHolder viewHolder,
                                          @NonNull RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Toast.makeText(ShopListFragment.super.getContext(),
                                "Deleting item ... ",
                                Toast.LENGTH_LONG).show();
                        viewModel.deleteShop(position);
                        // Refresh recyclerview
                        lineList.clear();
                        lineList.addAll(viewModel.getShopNameList());
                        adapter.setReference(SpecificData.LIST_TYPE_1);
                        adapter.setBaseSwitch(viewModel.getBaseSwitch());
                        adapter.setReusableList(lineList);
                    }
                });
        helper.attachToRecyclerView(recyclerView);
        adapter.setReference(SpecificData.LIST_TYPE_1);
        adapter.setBaseSwitch(viewModel.getBaseSwitch());
        adapter.setReusableList(lineList);
    }
}
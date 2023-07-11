package be.hvwebsites.shopping;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class BackUpToExternal extends AppCompatActivity {
    private ShopEntitiesViewModel viewModel;
    private String listType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_back_up_to_external);

        // Internal basis directory definitie
        String intBaseDir = getBaseContext().getFilesDir().getAbsolutePath();

        // External basis directory definitie
        String extBaseDir = getBaseContext().getExternalFilesDir(null).getAbsolutePath();

/*
        // Internal Data ophalen
        // Get a viewmodel from the viewmodelproviders
        viewModel = new ViewModelProvider(this).get(ShopEntitiesViewModel.class);
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(intBaseDir);
        if (viewModelStatus.getReturnCode() == 0) {
            // Files gelezen
        } else {
            Toast.makeText(BackUpToExternal.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }

        // Internal Data copieren nr external
        Toast.makeText(BackUpToExternal.this,
                "Internal Data opgehaald ! Data wordt nu gecopieerd in external directory ...",
                Toast.LENGTH_LONG).show();
        //viewModel.saveExternal(extBaseDir);
        viewModel.saveInBaseDir(extBaseDir);
        Toast.makeText(BackUpToExternal.this,
                "Back up van internal data is afgelopen !",
                Toast.LENGTH_LONG).show();
*/


    }
}
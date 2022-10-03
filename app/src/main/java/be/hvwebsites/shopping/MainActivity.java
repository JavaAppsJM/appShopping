package be.hvwebsites.shopping;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import be.hvwebsites.libraryandroid4.repositories.CookieRepository;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.services.FileBaseService;

public class MainActivity extends AppCompatActivity {
    // Device
    private final String deviceModel = Build.MODEL;
    // Basis Directory waar de bestanden worden bewaard op het toestel: internal of external
    private String fileBase = StaticData.FILE_BASE_EXTERNAL;
    private String filebaseDir = "";
    private String smsStatus = StaticData.SMS_VALUE_OFF;
    private CookieRepository cookieRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel, getPackageName());

        // Intent definieren voor terugkoppeling gegevens
        Intent newItemIntent = getIntent();

        // Restoring File Base en corrigeer file base en filedirectory
        if (newItemIntent.hasExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE)){
            // From intent
            fileBase = newItemIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE);
            // Corrigeer file directory met file base
            fileBaseService.setFileBase(fileBase);
        }else  if (savedInstanceState != null) {
            // From Instance state
            fileBase = savedInstanceState.getString(StaticData.FILE_BASE);
            // Corrigeer file directory met file base
            fileBaseService.setFileBase(fileBase);
        }
        filebaseDir = fileBaseService.getFileBaseDir();
/*      // TODO: mag weg
        if (newItemIntent.hasExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE)){
            basisSwitch = newItemIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE);
            fileBaseService.setFileBase(basisSwitch);
        }
        basisSwitch = fileBaseService.getFileBase();
        filebaseDir = fileBaseService.getFileBaseDir();
*/
        // Restore SMS status
        if (savedInstanceState != null){
            smsStatus = savedInstanceState.getString(StaticData.SMS_LABEL);
        }

/*
        // TODO: Definieer Cookierepository en gebruik file directory vd fileBaseService ; Is dit nodig ??
        cookieRepository = new CookieRepository(fileBaseService.getFileBaseDir());
        // Bewaar smsStatus in cookie
        cookieRepository.registerCookie(SpecificData.SMS_COOKIE_LABEL, smsStatus);
*/

        // Vul scherm in
        TextView basisSwitchView = findViewById(R.id.basisSwitch);
        basisSwitchView.setText(fileBase);
        // Toestand van sms cookie op main scherm laten zien
        TextView smsStatusV = findViewById(R.id.smsStatus);
        smsStatusV.setText(smsStatus);
        Button buttonShops = findViewById(R.id.shops);
        buttonShops.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, A4ListActivity.class);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, SpecificData.LIST_TYPE_1);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE, fileBase);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE_DIR, filebaseDir);
                startActivity(intent);
            }
        });
        Button buttonProducts = findViewById(R.id.products);
        buttonProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, A4ListActivity.class);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, SpecificData.LIST_TYPE_2);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE, fileBase);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE_DIR, filebaseDir);
                startActivity(intent);

            }
        });
        Button buttonShopping = findViewById(R.id.shopping);
        buttonShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, A4ShoppingListActivity.class);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE, fileBase);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE_DIR, filebaseDir);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        /* Voor GT-I9100, base switch opties invisible zetten geeft nullpointerexception op setVisible !
        if (deviceModel.equals("GT-I9100")){
            MenuItem mBU = findViewById(R.id.menu_bu);
            mBU.setVisible(false);
            MenuItem mEX = findViewById(R.id.menu_set_base_switch_external);
            mEX.setVisible(false);
            MenuItem mIN = findViewById(R.id.menu_set_base_switch_internal);
            mIN.setVisible(false);
        }*/

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // View om toestand basisswitch te laten zien
        TextView basisSwitchView = findViewById(R.id.basisSwitch);
        TextView smsMenuV = findViewById(R.id.smsStatus);

        // Welke menu optie is er gekozen ?
        switch (item.getItemId()) {
            case R.id.menu_bu:
                // Maak een copy vn je internal files nr external files (werkt niet voor oud model !)
                if (!deviceModel.equals("GT-I9100")){
                    Intent mainIntent = new Intent(MainActivity.this, BackUpToExternal.class);
                    startActivity(mainIntent);
                } else {
                    Toast.makeText(MainActivity.this,
                            "Toestel ondersteunt geen external files !",
                            Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_set_base_switch_external:
                // Zet BASE_SWITCH to external
                if (!deviceModel.equals("GT-I9100")) {
                    fileBase = SpecificData.BASE_EXTERNAL;
                    basisSwitchView.setText(fileBase);
                    Toast.makeText(MainActivity.this,
                            "External files geactiveerd !",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Toestel ondersteunt geen external files !",
                            Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_set_base_switch_internal:
                // Zet BASE_SWITCH to internal
                fileBase = SpecificData.BASE_INTERNAL;
                basisSwitchView.setText(fileBase);
                if (!deviceModel.equals("GT-I9100")) {
                    Toast.makeText(MainActivity.this,
                            "Internal files geactiveerd !",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "Toestel werkt altijd met internal files !",
                            Toast.LENGTH_LONG).show();
                }
                return true;
            case R.id.menu_initiate_bt:
                // Initiate bluetooth communication
                Intent mainIntent = new Intent(MainActivity.this, BluetoothCom.class);
                startActivity(mainIntent);
                return true;
            case R.id.menu_sms_on_off:
                // Zet sms on or off
                if (smsStatus.equals(StaticData.SMS_VALUE_OFF)){
                    smsStatus = StaticData.SMS_VALUE_ON;
                    item.setTitle("Set SMS off");
                }else {
                    smsStatus = StaticData.SMS_VALUE_OFF;
                    item.setTitle("Set SMS on");
                }
                smsMenuV.setText(smsStatus);
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Bewaar Instance State (bvb: fileBase, smsStatus, entityType, enz..)
        savedInstanceState.putString(StaticData.FILE_BASE, fileBase);
        savedInstanceState.putString(StaticData.SMS_LABEL, smsStatus);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }
}
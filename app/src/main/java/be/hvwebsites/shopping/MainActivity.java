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
    private String fileBase;
    private String filebaseDir = "";
    private String smsStatus = StaticData.SMS_VALUE_OFF;
    private CookieRepository cookieRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

/*
        We gaan de file base of de file directory niet meer bijhouden als cookie omdat voor de cookies
        de file base moet gekend zijn en niet kan wijzigen !
        File base en file directory gaan we in elke activity bepalen adhv FileBaseService klasse !
*/

        // Creer een filebase service (bevat file base en file base directory) obv device en package name
        FileBaseService fileBaseService = new FileBaseService(deviceModel, getPackageName());
        filebaseDir = fileBaseService.getFileBaseDir();
        fileBase = fileBaseService.getFileBase();

        // Restore SMS status via cookie
        cookieRepository = new CookieRepository(filebaseDir);
        if (cookieRepository.bestaatCookie(StaticData.SMS_LABEL) != CookieRepository.COOKIE_NOT_FOUND){
            smsStatus = cookieRepository.getCookieValueFromLabel(StaticData.SMS_LABEL);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

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
                startActivity(intent);
            }
        });
        Button buttonProducts = findViewById(R.id.products);
        buttonProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, A4ListActivity.class);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, SpecificData.LIST_TYPE_2);
                startActivity(intent);

            }
        });
        Button buttonMeals = findViewById(R.id.meals);
        buttonMeals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, A4ListActivity.class);
                intent.putExtra(StaticData.EXTRA_INTENT_KEY_TYPE, SpecificData.LIST_TYPE_3);
                startActivity(intent);

            }
        });
        Button buttonShopping = findViewById(R.id.shopping);
        buttonShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, A4ShopCompetitionList.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);

        MenuItem smsMenuItem = menu.getItem(4);
        if (smsStatus.equals(StaticData.SMS_VALUE_ON)){
            smsMenuItem.setTitle("Set sms off");
        }else {
            smsMenuItem.setTitle("Set sms on");
        }

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
/*
            case R.id.menu_bu:
                // Maak een copy vn je internal files nr external files (werkt niet voor oud model !)
                if (!deviceModel.equals(FileBaseService.SAMSUNG_GTI9100)
                &&(fileBase.equals(FileBaseService.FILE_BASE_INTERNAL))){
                    Intent mainIntent = new Intent(MainActivity.this, BackUpToExternal.class);
                    startActivity(mainIntent);
                } else {
                    Toast.makeText(MainActivity.this,
                            "Toestel ondersteunt geen external files !",
                            Toast.LENGTH_LONG).show();
                }
                return true;
*/
            case R.id.menu_set_base_switch_external:
                // Zet BASE_SWITCH to external
                if (!deviceModel.equals(FileBaseService.SAMSUNG_GTI9100)) {
                    fileBase = FileBaseService.FILE_BASE_EXTERNAL;
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
                fileBase = FileBaseService.FILE_BASE_INTERNAL;
                basisSwitchView.setText(fileBase);
                if (!deviceModel.equals(FileBaseService.SAMSUNG_GTI9100)) {
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
                mainIntent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE, fileBase);
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
                // Bewaar als cookie
                cookieRepository.registerCookie(StaticData.SMS_LABEL, smsStatus);
                smsMenuV.setText(smsStatus);
                return true;
            case R.id.menu_finish:
                // Stop app
                finish();
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);

        // Bewaar Instance State (bvb: fileBase, smsStatus, entityType, enz..)
        savedInstanceState.putString(StaticData.FILE_BASE, fileBase);
        savedInstanceState.putString(StaticData.SMS_LABEL, smsStatus);
    }
}
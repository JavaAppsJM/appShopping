package be.hvwebsites.shopping;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;

import be.hvwebsites.libraryandroid4.helpers.IDNumber;
import be.hvwebsites.libraryandroid4.returninfo.ReturnInfo;
import be.hvwebsites.libraryandroid4.statics.StaticData;
import be.hvwebsites.shopping.constants.SpecificData;
import be.hvwebsites.shopping.entities.Meal;
import be.hvwebsites.shopping.entities.Product;
import be.hvwebsites.shopping.entities.ProductInShop;
import be.hvwebsites.shopping.entities.Shop;
import be.hvwebsites.shopping.services.BluetoothService;
import be.hvwebsites.shopping.services.BtDeviceListActivity;
import be.hvwebsites.shopping.viewmodels.BlueToothViewModel;
import be.hvwebsites.shopping.viewmodels.ShopEntitiesViewModel;

public class BluetoothCom extends AppCompatActivity {
    private static final String TAG = "Bluetooth";
    private ShopEntitiesViewModel viewModel;
    private BlueToothViewModel blueToothViewModel;
    private String baseDir;
    private String baseSwitch;
    private String sendMsgProcessStatus = SEND_STATUS_NONE;
    private String receiveMsgProcessStatus = REC_STATUS_NONE;
    private boolean btInitiated = false;

    // Send Message Process Statusses
    private static final String SEND_STATUS_NONE = "Zend status none";
    private static final String SEND_STATUS_START = "Zend status start";
    private static final String SEND_STATUS_SENDING = "Zend status sending";
    private static final String SEND_STATUS_END = "Zend status eindstap";
    private static final String SEND_STATUS_FILES = "Zend status stap files";
    private static final String SEND_STATUS_SHOPS = "Zend status stap shoplist";
    private static final String SEND_STATUS_ENDSHOPS = "Zend status stap endshoplist";
    private static final String SEND_STATUS_PRODUCTS = "Zend status stap productlist";
    private static final String SEND_STATUS_ENDPRODUCTS = "Zend status stap endproductlist";
    private static final String SEND_STATUS_PRODSHOPS = "Zend status stap products in shops";
    private static final String SEND_STATUS_ENDPRODSHOPS = "Zend status stap end products in shops";

    // Receive Message Process Statusses
    private static final String REC_STATUS_NONE = "Receive status none";
    private static final String REC_STATUS_START = "Receive status start";
    private static final String REC_STATUS_SENDING = "Receive status sending";
    private static final String REC_STATUS_END = "Receive status eindstap";
    private static final String REC_STATUS_FILES = "Receive status stap files";
    private static final String REC_STATUS_SHOPS = "Receive status stap shoplist";
    private static final String REC_STATUS_PRODUCTS = "Receive status stap productlist";
    private static final String REC_STATUS_PRODSHOPS = "Receive status stap products in shops";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    // Layout Views
    private ListView mConversationView;
    private TextView statusBar;
    private Button scanButton;
    private Button sendButton;
    private Button stopButton;
    private Button acceptButton;

    /* Name of the connected device */
    private String mConnectedDeviceName = null;

    /* Array adapter for the conversation thread */
    private ArrayAdapter<String> mConversationArrayAdapter;

    /* Local Bluetooth adapter */
    private BluetoothAdapter mBluetoothAdapter = null;

    /* Member object for the bluetooth services */
    private BluetoothService mBtService = null;

    /* Launcher for startforactivityresult */
    ActivityResultLauncher<Intent> mStartForResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_com);
        // Schermvelden definieren
        mConversationView = findViewById(R.id.in);
        statusBar = findViewById(R.id.btStatusBar);
        scanButton = findViewById(R.id.buttonScan);
        sendButton = findViewById(R.id.buttonSend);
        stopButton = findViewById(R.id.buttonStop);

        acceptButton = findViewById(R.id.buttonAcceptBtData);
        acceptButton.setVisibility(View.INVISIBLE);

        // Intent definieren
        Intent newItemIntent = getIntent();
        baseSwitch = newItemIntent.getStringExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE);
        if (baseSwitch == null){
            baseSwitch = SpecificData.BASE_DEFAULT;
        }

        // Start bluetooth
        ReturnInfo resultBt = InitiateBluetooth();
        if (resultBt.getReturnCode() != 0) {
            Toast.makeText(BluetoothCom.this,
                    resultBt.getReturnMessage(),
                    Toast.LENGTH_LONG).show();
            // Indien bluetooth initiated mislukt is, ga terug nr mainActivity
            Intent returnintent = new Intent(BluetoothCom.this, MainActivity.class);
            startActivity(returnintent);
        } else {
            Toast.makeText(BluetoothCom.this,
                    "Bluetooth initiated !",
                    Toast.LENGTH_LONG).show();
        }

        // Bestanden ophalen
        // Get a viewmodel from the viewmodelproviders
        statusBar.setText("Data ophalen");
        viewModel = new ViewModelProvider(this).get(ShopEntitiesViewModel.class);
        // Basis directory definitie
        baseDir = "";
        if (baseSwitch.equals(SpecificData.BASE_INTERNAL)){
            baseDir = getBaseContext().getFilesDir().getAbsolutePath();
        }else {
            baseDir = getBaseContext().getExternalFilesDir(null).getAbsolutePath();
        }
        // Initialize viewmodel mt basis directory (data wordt opgehaald in viewmodel)
        ReturnInfo viewModelStatus = viewModel.initializeViewModel(baseDir);
        if (viewModelStatus.getReturnCode() == 0) {
            // Files gelezen
            viewModel.setBaseSwitch(baseSwitch);
            statusBar.setText("Data opgehaald uit " + baseSwitch);
        } else {
            Toast.makeText(BluetoothCom.this,
                    "Ophalen data is mislukt",
                    Toast.LENGTH_LONG).show();
        }
    }

    private ReturnInfo InitiateBluetooth() {
        ReturnInfo returnInfo = new ReturnInfo(0);

        // Create bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device doesn't support bluetooth
            returnInfo.setReturnCode(StaticData.BLUETOOTH_NOT_SUPPORTED);
            returnInfo.setReturnMessage("Device does not support bluetooth !");
            return returnInfo;
        }
        // Check if bluetooth is enabled
        if (!mBluetoothAdapter.isEnabled()) {
            /* docu from Android Studio ==> DEPRECIATED !!
            int REQUEST_ENABLE_BT = 0;
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(enableBtIntent);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            */
            // TODO: Permission checken om BluetoothAdapter te enablen
            // Check permissions
            String[] permissions = {
                    Manifest.permission.BLUETOOTH,
                    Manifest.permission.BLUETOOTH_ADVERTISE
            };
            if (ContextCompat.checkSelfPermission(BluetoothCom.this, Manifest.permission.BLUETOOTH_ADVERTISE)
                    == PackageManager.PERMISSION_DENIED){
                // Permissions not yet ok, request permissions
                ActivityCompat.requestPermissions(this, permissions, 0);
            }else  (mBluetoothAdapter.enable()) {
                // Bluetooth enabled
            } else {
                returnInfo.setReturnCode(StaticData.BLUETOOTH_ENABLE_FAILED);
                returnInfo.setReturnMessage("Bluetooth enabling failed !");
                return returnInfo;
            }
        }
        // Bluetooth enabled
        // Definieer conversation array
        mConversationArrayAdapter = new ArrayAdapter<>(BluetoothCom.this, R.layout.message);
        mConversationView.setAdapter(mConversationArrayAdapter);

        // Initialize the scan button with a listener for click events
        scanButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Launch the DeviceListActivity to do scan and see devices and select a device to connect
                Intent serverIntent = new Intent(BluetoothCom.this, BtDeviceListActivity.class);
                mStartForResult.launch(serverIntent);
            }
        });
        // Vervang startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE); is depreciated !
        mStartForResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            // Handle the Intent with secure=true because REQUEST_CONNECT_DEVICE_SECURE
                            // Uit BtDeviceListActivity krijg je data terug vh te connecteren device
                            // intent bevat MAC-adres van het te connecteren device, hiermee kan je connecteren
                            connectDevice(intent, true);
                        }
                    }
                });

        // Initialize the send button with a listener for click events
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Start het versturen van de bestanden op
                String msg = "<send><files>";
                statusBar.setText("Bestanden worden verstuurd...");
                sendMessage(msg);
            }
        });

        // Initialize the stop button with a listener for click events
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Stop bluetooth activiteiten en ga terug nr MainActivity
                mBtService.stop();
                mBluetoothAdapter.disable();
                Intent returnintent = new Intent(BluetoothCom.this, MainActivity.class);
                returnintent.putExtra(StaticData.EXTRA_INTENT_KEY_FILE_BASE, baseSwitch);
                startActivity(returnintent);
            }
        });

        // Initialize the accept button with a listener
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Accepteer wat er in bluetooth data zit
                // TODO: blueToothViewModel activeren
                //acceptBtData();
                // TODO: viewmodel hier verwijderen als blueToothViewmodel geactiveerd wordt
                viewModel.forceBtData(baseDir);

                statusBar.setText("Bestanden bewaard in " + baseSwitch);
                mConversationArrayAdapter.add("Bluetooth data accepted & saved in " + baseSwitch);
            }
        });

        // Initialize the BluetoothService to perform bluetooth connections
        mBtService = new BluetoothService(BluetoothCom.this, mHandler);

        // To be discoverable
        ensureDiscoverable();
        mConversationArrayAdapter.add("Device is now visible for other bluetooth devices for 5 min");

        // start listening
        mBtService.start();

        return returnInfo;
    }

    public void acceptBtData(){
        // Bluetooth data accepteren en stockeren in de bestanden
        if (blueToothViewModel.getShopListBt().size() > 0){
            // Er zijn shops te accepteren
            viewModel.fillShopListWithOtherShopList(blueToothViewModel.getShopListBt());
        }
        if (blueToothViewModel.getProductListBt().size() > 0){
            // Er zijn producten te accepteren
            viewModel.fillProdListWithOtherProdList(blueToothViewModel.getProductListBt());
        }
        if (blueToothViewModel.getProductInShopListBt().size() > 0){
            // Er zijn prodinshops te accepteren
            viewModel.fillProdInShopListWithOtherList(blueToothViewModel.getProductInShopListBt());
        }
        if (blueToothViewModel.getMealListBt().size() > 0){
            // Er zijn gerechten te accepteren
            viewModel.fillMealListWithOtherMealList(blueToothViewModel.getMealListBt());
        }
        if (blueToothViewModel.getProductInMealListBt().size() > 0){
            // Er zijn prodinmeals te accepteren
            viewModel.fillProdInMealListWithOtherList(blueToothViewModel.getProductInMealListBt());
        }
        if (blueToothViewModel.getMealInMealListBt().size() > 0){
            // Er zijn mealinmeals te accepteren
            viewModel.fillMealInMealListWithOtherList(blueToothViewModel.getMealInMealListBt());
        }
        boolean debug = true;
    }

    /*** Makes this device discoverable for 300 seconds (5 minutes).*/
    private void ensureDiscoverable() {
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /** Sends a message.
     * @param message A string of text to send. */
    private void sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mBtService.getState() != BluetoothService.STATE_CONNECTED) {
            Toast.makeText(BluetoothCom.this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothService to write
            byte[] send = message.getBytes();
            mBtService.write(send);
        }
    }

    /*** TODO: Updates the status on the action bar.
     * @param resId a string resource ID */
    private void setStatus(int resId) {
        if (getActionBar() != null){
            getActionBar().setSubtitle(resId);
        }
    }

    /*** Updates the status on the action bar.
     * @param subTitle status */
    private void setStatus(CharSequence subTitle) {
        getActionBar().setSubtitle(subTitle);
    }

    /** The Handler that gets information back from the BluetoothService */
    private final Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            FragmentActivity activity = BluetoothCom.this;
            switch (msg.what) {
                case StaticData.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            //setStatus(R.string.title_connected_to);
                            statusBar.setText("connected to");
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothService.STATE_CONNECTING:
                            //setStatus(R.string.title_connecting);
                            statusBar.setText("connecting...");
                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            //setStatus(R.string.title_not_connected);
                            statusBar.setText("not connected");
                            break;
                    }
                    break;
                case StaticData.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case StaticData.MESSAGE_READ:
                    // Hier wordt de data ontvangen
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    // Verwerk message
                    processMsgReceived(readMessage);
                    break;
                case StaticData.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(StaticData.DEVICE_NAME);
                    if (null != activity) {
                        Toast.makeText(activity, "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                        mConversationArrayAdapter.add("Connected to " + mConnectedDeviceName);
                    }
                    break;
                case StaticData.MESSAGE_TOAST:
                    if (null != activity) {
                        Toast.makeText(activity, msg.getData().getString(StaticData.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case StaticData.MESSAGE_CONVERSATION_AREA:
                    if (null != activity) {
                        mConversationArrayAdapter.add(msg.getData().getString(StaticData.CONVERSATION_MSG));
                    }
                    break;
            }
        }
    };

    private void processMsgReceived(String message){
        /** Verwerkt de binnengekomen messages
            Patroon verstuur: <send><files>
            Patroon2 verstuur: <send><shoplist><0: index in arraylist><shopID><shopnaam>
            Patroon3 verstuur: <send><endshoplist>
            Patroon4 verstuur: <send><prodlist><0><productID><productnaam><preferredshopID><toBuy><wanted>
            Patroon5 verstuur: <send><endprodlist>
            Patroon6 verstuur: <send><shopprod><0: index in arraylist><shopID><productID>
            Patroon7 verstuur: <send><endshopprod>
            Patroon8 verstuur: <send><end>
            Patroon ontvang: <rec><files>
            Patroon2 ontvang: <rec><shoplist><0: index in arraylist><shopnaam>
            Patroon3 ontvang: <rec><endshoplist>
            Patroon4 ontvang: <rec><prodlist><0: index in arraylist><productnaam>
            Patroon5 ontvang: <rec><endprodlist>
            Patroon6 ontvang: <rec><shopprod><0: index in arraylist>
            Patroon7 ontvang: <rec><endshopprod>
            Patroon8 ontvang: <rec><end>
         */

        boolean sendReceived = false;
        boolean recReceived = false;
        String[] btLineContent = message.split("<");
        for (int i = 0; i < btLineContent.length; i++) {
            /**
             * Verwerken vn send msgs
             * */
            if (btLineContent[i].matches("send.*")){
                sendReceived = true;
                recReceived = false;
                sendMsgProcessStatus = SEND_STATUS_START;
            }
            if (sendReceived && btLineContent[i].matches("files.*")){
                sendMsgProcessStatus = SEND_STATUS_SENDING;
                // bluetooth bestanden clearen
                viewModel.getShopListBt().clear();
                viewModel.getProductListBt().clear();
                viewModel.getProductInShopListBt().clear();
                // Versturen van send files ontvangen
                sendMessage("<rec><files>");
            }
            if (sendReceived && btLineContent[i].matches("shoplist.*")){
                sendMsgProcessStatus = SEND_STATUS_SENDING;
                // Er volgt een shop uit de shoplist
                Shop shopreceived = new Shop();
                shopreceived.setEntityId(new IDNumber(btLineContent[i+2].replace(">", "")));
                shopreceived.setEntityName(btLineContent[i+3].replace(">",""));
                // Toevoegen aan de bluetooth shoplist
                // TODO: blueToothViewModel activeren
                //blueToothViewModel.getShopListBt().add(shopreceived);
                // TODO: viewmodel hier verwijderen als blueToothViewmodel geactiveerd wordt
                viewModel.getShopListBt().add(shopreceived);
                // Versturen van shops ontvangen <rec><shoplist><0: index in arraylist><shopnaam>
                int index = Integer.parseInt(btLineContent[i+1].replace(">", ""));
                String indexString = String.valueOf(index);
                String msg = "<rec><shoplist><";
                msg = msg.concat(indexString);
                msg = msg.concat("><");
                msg = msg.concat(shopreceived.getEntityName());
                msg = msg.concat(">");
                sendMessage(msg);
            }
            if (sendReceived && btLineContent[i].matches("endshoplist.*")){
                sendMsgProcessStatus = SEND_STATUS_SENDING;
                // Versturen van send endshoplist ontvangen
                sendMessage("<rec><endshoplist>");
            }
            if (sendReceived && btLineContent[i].matches("prodlist.*")){
                sendMsgProcessStatus = SEND_STATUS_SENDING;
                // Er volgt een product uit de productlist
                Product productReceived = new Product();

                // nog testen vooraleer te activeren en Gitte dit heeft
                productReceived.setBtContent(btLineContent[i+2], btLineContent[i+3], btLineContent[i+4],
                        btLineContent[i+5], btLineContent[i+6], btLineContent[i+7], btLineContent[i+8]);

                // Toevoegen aan de locale productlist
                // TODO: blueToothViewModel activeren
                //blueToothViewModel.getProductListBt().add(productReceived);
                // TODO: viewmodel hier verwijderen als blueToothViewmodel geactiveerd wordt
                viewModel.getProductListBt().add(productReceived);
                // Versturen van productlist ontvangen
                int index = Integer.parseInt(btLineContent[i+1].replace(">", ""));
                String indexString = String.valueOf(index);
                String msg = "<rec><prodlist><";
                msg = msg.concat(indexString);
                msg = msg.concat("><");
                msg = msg.concat(productReceived.getEntityName());
                msg = msg.concat(">");
                sendMessage(msg);
            }
            if (sendReceived && btLineContent[i].matches("endprodlist.*")){
                sendMsgProcessStatus = SEND_STATUS_SENDING;
                // Versturen van send endprodlist ontvangen
                sendMessage("<rec><endprodlist>");
            }
            if (sendReceived && btLineContent[i].matches("shopprod.*")){
                sendMsgProcessStatus = SEND_STATUS_SENDING;
                // Let op doordat de constructor vn ProductInShop eerst de productID verwacht
                // en dan de ShopID, moet btLineContent3 voor 2 meegegeven worden
                ProductInShop productInShopRec = new ProductInShop(
                        new IDNumber(btLineContent[i+3].replace(">", "")),
                        new IDNumber(btLineContent[i+2].replace(">", ""))
                );
                // Toevoegen aan de locale prodshoplist
                // TODO: blueToothViewModel activeren
                //blueToothViewModel.getProductInShopListBt().add(productInShopRec);
                // TODO: viewmodel hier verwijderen als blueToothViewmodel geactiveerd wordt
                viewModel.getProductInShopListBt().add(productInShopRec);
                // Versturen van prodshops ontvangen
                int index = Integer.parseInt(btLineContent[i+1].replace(">", ""));
                String indexString = String.valueOf(index);
                String msg = "<rec><shopprod><";
                msg = msg.concat(indexString);
                msg = msg.concat(">");
                sendMessage(msg);
            }
            if (sendReceived && btLineContent[i].matches("endshopprod.*")){
                sendMsgProcessStatus = SEND_STATUS_SENDING;
                // Versturen van send endshopprod ontvangen
                sendMessage("<rec><endshopprod>");
            }
            // TODO: ontvangen van meals en prodinmeals en mealinmeals is nog niet voorzien
            if (sendReceived && sendMsgProcessStatus.equals(SEND_STATUS_START) && btLineContent[i].matches("end.*")){
                sendMsgProcessStatus = SEND_STATUS_END;
                sendReceived = false;
                // Versturen van send end ontvangen
                sendMessage("<rec><end>");
                statusBar.setText("Bestanden ontvangen");
                // Als er iets ontvangen is in bluetooth data dan kan dat geaccepteerd worden
                acceptButton.setVisibility(View.VISIBLE);
            }
            /**
             ********************************************************** Verwerken vn rec(eive) msgs
             * */
            if (btLineContent[i].matches("rec.*")){
                sendReceived = false;
                recReceived = true;
                receiveMsgProcessStatus = REC_STATUS_START;
            }
            if (recReceived && btLineContent[i].matches("files.*")){
                receiveMsgProcessStatus = REC_STATUS_SENDING;
                // de eerste uit shoplist mag gestuurd worden
                String msg = "<send><shoplist><0><";
                msg = msg.concat(viewModel.getShopList().get(0).getEntityId().getIdString());
                msg = msg.concat("><");
                msg = msg.concat(viewModel.getShopList().get(0).getEntityName());
                msg = msg.concat(">");
                sendMessage(msg);
            }
            if (recReceived && btLineContent[i].matches("shoplist.*")){
                receiveMsgProcessStatus = REC_STATUS_SENDING;
                // de volgende mag gestuurd worden
                int volgendeIndex = Integer.parseInt(btLineContent[i+1].replace(">", ""))+1;
                // is er nog een volgende ?
                String msg = "<send><";
                if (viewModel.getShopList().size()>volgendeIndex){
                    String vlgndIndex = String.valueOf(volgendeIndex);
                    msg = msg.concat("shoplist><");
                    msg = msg.concat(vlgndIndex);
                    msg = msg.concat("><");
                    msg = msg.concat(viewModel.getShopList().get(volgendeIndex).getEntityId().getIdString());
                    msg = msg.concat("><");
                    msg = msg.concat(viewModel.getShopList().get(volgendeIndex).getEntityName());
                    msg = msg.concat(">");
                }else {
                    //Einde vd shoplist Patroon3 verstuur: <send><endshoplist>
                    msg = msg.concat("endshoplist>");
                }
                sendMessage(msg);
            }
            if (recReceived && btLineContent[i].matches("endshoplist.*")){
                receiveMsgProcessStatus = REC_STATUS_SENDING;
                // de eerste uit prodlist mag gestuurd worden
                // Patroon4 verstuur: <send><prodlist><0><productID><productnaam><preferredshopID><toBuy><wanted><cooled>
                String msg = "<send><prodlist><0><";
                // Message wordt samengesteld door een method vn Product: getProductAttributesForBtMsg
                msg = msg.concat(viewModel.getProductList().get(0).getProductAttributesForBtMsg());
                sendMessage(msg);
            }
            if (recReceived && btLineContent[i].matches("prodlist.*")){
                receiveMsgProcessStatus = REC_STATUS_SENDING;
                // de volgende mag gestuurd worden
                int volgendeIndex = Integer.parseInt(btLineContent[i+1].replace(">", ""))+1;
                // is er nog een volgende ?
                String msg = "<send><";
                if (viewModel.getProductList().size() > volgendeIndex){
                    String vlgndIndex = String.valueOf(volgendeIndex);
                    msg = msg.concat("prodlist><");
                    msg = msg.concat(vlgndIndex);
                    msg = msg.concat("><");
                    // Message wordt samengesteld door een method vn Product: getProductAttributesForBtMsg
                msg = msg.concat(viewModel.getProductList().get(volgendeIndex).getProductAttributesForBtMsg());
                }else {
                    //Einde vd prodlist Patroon5 verstuur: <send><endprodlist>
                    msg = msg.concat("endprodlist>");
                }
                sendMessage(msg);
            }
            if (recReceived && btLineContent[i].matches("endprodlist.*")){
                receiveMsgProcessStatus = REC_STATUS_SENDING;
                // de eerste uit prodshop mag gestuurd worden
                // Patroon6 verstuur: <send><shopprod><0: index in arraylist><shopID><productID>
                String msg = "<send><shopprod><0><";
                msg = msg.concat(viewModel.getProductInShopList().get(0).getFirstID().getIdString());
                msg = msg.concat("><");
                msg = msg.concat(viewModel.getProductInShopList().get(0).getSecondID().getIdString());
                msg = msg.concat(">");
                sendMessage(msg);
            }
            if (recReceived && btLineContent[i].matches("shopprod.*")){
                receiveMsgProcessStatus = REC_STATUS_SENDING;
                // de volgende mag gestuurd worden
                int volgendeIndex = Integer.parseInt(btLineContent[i+1].replace(">", ""))+1;
                // is er nog een volgende ?
                String msg = "<send><";
                if (viewModel.getProductInShopList().size() > volgendeIndex){
                    String vlgndIndex = String.valueOf(volgendeIndex);
                    msg = msg.concat("shopprod><");
                    msg = msg.concat(vlgndIndex);
                    msg = msg.concat("><");
                    msg = msg.concat(viewModel.getProductInShopList().get(volgendeIndex).getFirstID().getIdString());
                    msg = msg.concat("><");
                    msg = msg.concat(viewModel.getProductInShopList().get(volgendeIndex).getSecondID().getIdString());
                    msg = msg.concat(">");
                }else {
                    //Einde vd shopprod list en einde vn send operatie verstuur: <send><end>
                    msg = msg.concat("endshopprod>");
                }
                sendMessage(msg);
            }
            if (recReceived && btLineContent[i].matches("endshopprod.*")){
                receiveMsgProcessStatus = REC_STATUS_SENDING;
                // Patroon8 verstuur: <send><end>
                String msg = "<send><end>";
                sendMessage(msg);
            }
            // TODO: zenden van meals en prodinmeals en mealinmeals is nog niet ok
            if (recReceived && receiveMsgProcessStatus.equals(REC_STATUS_START) &&
                    btLineContent[i].matches("end.*")){
                receiveMsgProcessStatus = REC_STATUS_END;
                recReceived = false;
            }
        }
    }

    /** Establish connection with other device
     * @param data   An {@link Intent} with {@link BtDeviceListActivity#EXTRA_DEVICE_ADDRESS} extra.
     * @param secure Socket Security type - Secure (true) , Insecure (false) */
    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        Bundle extras = data.getExtras();
        if (extras == null) {
            return;
        }
        String address = extras.getString(BtDeviceListActivity.EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mBtService.connect(device, secure);
    }
}
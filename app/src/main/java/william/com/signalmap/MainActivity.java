package william.com.signalmap;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            List<CellInfo> ci = tm.getAllCellInfo();
            System.out.println(ci);
        }catch (SecurityException e) {
            System.err.println("no gps");
        }
        checkWifi();
        checkData();
        checkCell();

        ImageButton refreshSignals = (ImageButton) findViewById(R.id.refreshButton);
        refreshSignals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkWifi();
                checkData();
                checkCell();
            }
        });

        Button openCurrent = (Button) findViewById(R.id.newButton);
        openCurrent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MainActivity.this, MapsActivity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    public void checkWifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        int numberOfLevels = 5;
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int dbm = wifiInfo.getRssi();
        int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);

        String[] wifiStrengths = new String[]{"Very Weak","Weak","Fair","Good","Very Good"};

        TextView wifiRating = (TextView) findViewById(R.id.wifiRating);
        if(level == 0) {
            wifiRating.setText("No Wifi");
        } else {
            wifiRating.setText(wifiStrengths[level - 1] + dbm);
        }
    }

    public void checkData() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        try {
            TelephonyManager tm2 = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            List<CellInfo> cellInfoList = tm2.getAllCellInfo();
            for (CellInfo cellInfo : cellInfoList)
            {
                if (cellInfo instanceof CellInfoLte)
                {
                    // cast to CellInfoLte and call all the CellInfoLte methods you need
                    System.out.println(((CellInfoLte)cellInfo).getCellSignalStrength().getDbm());
                }
            }
            CellInfoLte cellInfoLte = (CellInfoLte) tm.getAllCellInfo().get(1);
            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
            int dbmVal = cellSignalStrengthLte.getDbm();
            System.err.println("hi" + dbmVal);
            String[] dataStrengths = new String[]{"Very Weak","Weak","Fair","Good","Very Good"};
            TextView dataRating = (TextView) findViewById(R.id.dataRating);
            if(dbmVal >= -50) {
                dataRating.setText(dataStrengths[4] + dbmVal);
                System.out.println("Very good?");
            } else if(dbmVal >= -62) {
                dataRating.setText(dataStrengths[3] + dbmVal);
            } else if(dbmVal >= -74) {
                dataRating.setText(dataStrengths[2] + dbmVal);
            } else if(dbmVal >= -86) {
                dataRating.setText(dataStrengths[1] + dbmVal);
            } else {
                System.out.println("i did else");
                System.out.println(dataStrengths[0]);
                dataRating.setText(dataStrengths[0] + dbmVal);
            }
        } catch(SecurityException e) {
            System.err.println("GPS permission not granted");
        }
    }

    public void checkCell() {
        TelephonyManager telephonyManager = (TelephonyManager)this.getSystemService(Context.TELEPHONY_SERVICE);

        if(telephonyManager.getPhoneType() == 16) {
            try{
                CellInfoGsm cellinfogsm = (CellInfoGsm)telephonyManager.getAllCellInfo().get(0);
                CellSignalStrengthGsm cellSignalStrengthGsm = cellinfogsm.getCellSignalStrength();
                int dbmVal = cellSignalStrengthGsm.getDbm();
                String[] cellStrengths = new String[]{"Very Weak","Weak","Fair","Good","Very Good"};
                TextView cellRating = (TextView) findViewById(R.id.cellRating);
                if(dbmVal >= -50) {
                    cellRating.setText(cellStrengths[4] + dbmVal);
                } else if(dbmVal >= -62) {
                    cellRating.setText(cellStrengths[3] + dbmVal);
                } else if(dbmVal >= -74) {
                    cellRating.setText(cellStrengths[2] + dbmVal);
                } else if(dbmVal >= -86) {
                    cellRating.setText(cellStrengths[1] + dbmVal);
                } else {
                    cellRating.setText(cellStrengths[0] + dbmVal);
                }

            } catch(SecurityException e) {
                System.err.println("GPS permission not granted");
            }
        } else if(telephonyManager.getPhoneType() == 4  || telephonyManager.getPhoneType() == 2) {
            try {
                CellInfoCdma cellInfoCdma = (CellInfoCdma) telephonyManager.getAllCellInfo().get(0);
                CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                int dbmVal = cellSignalStrengthCdma.getDbm();
                System.err.println("!!!" + dbmVal);
                String[] cellStrengths = new String[]{"Very Weak","Weak","Fair","Good","Very Good"};
                TextView cellRating = (TextView) findViewById(R.id.cellRating);
                if(dbmVal >= -50) {
                    cellRating.setText(cellStrengths[4] + dbmVal + telephonyManager.getPhoneType());
                } else if(dbmVal >= -62) {
                    cellRating.setText(cellStrengths[3] + dbmVal);
                } else if(dbmVal >= -74) {
                    cellRating.setText(cellStrengths[2] + dbmVal);
                } else if(dbmVal >= -86) {
                    cellRating.setText(cellStrengths[1] + dbmVal);
                } else {
                    cellRating.setText(cellStrengths[0] + dbmVal);
                }
            } catch (SecurityException e) {
                System.err.println("GPS permission not granted");
            }
        } else {
            TextView cellRating = (TextView) findViewById(R.id.cellRating);
            cellRating.setText("Neither" + telephonyManager.getPhoneType());
        }

    }
}

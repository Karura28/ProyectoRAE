package com.example.appv1_1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    PieChart pieChart;
    private final String DEVICE_ADDRESS="98:D3:32:20:CC:0C"; // mac del antiguo proto
    // private final String DEVICE_ADDRESS="54:4A:16:3A:01:1E"; // mac del proto mejorado
    private final UUID PORT_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");//Serial Port Service ID
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    Button startButton , sendButton, clearButton, stopButton;
    TextView textView;
    EditText editText;
    boolean deviceConnected=false;
    Thread thread;
    byte buffer[];
    // int bufferPosition;
    boolean stopThread;
    int contador =0;
    int Dato ;
    Dialog myDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //startButton = findViewById(R.id.buttonStart);
        //sendButton = findViewById(R.id.buttonSend);
        //clearButton = findViewById(R.id.buttonClear);
        //stopButton = findViewById(R.id.buttonStop);
        //editText =   findViewById(R.id.editText);
        //textView =  findViewById(R.id.textView);
        myDialog = new Dialog(this);

        pieChart = findViewById(R.id.PieChart);
        pieChart.setDragDecelerationFrictionCoef(1000f);
        pieChart.getDescription().setPosition(121,88123);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(android.R.color.white);
        pieChart.setTransparentCircleRadius(61f);
        pieChart.setDrawSlicesUnderHole(true);


        int contenedor100 = 100;
        ArrayList<PieEntry> yValues = new ArrayList<>();
        yValues.add(new PieEntry((float) contenedor100));

        pieChart.setCenterText(contenedor100+"%");
        pieChart.setCenterTextColor( Color.parseColor("#ffffff"));
        pieChart.setCenterTextSize(30f);

        pieChart.animateX(7600, Easing.EaseInBounce);
        pieChart.animateY(7500, Easing.EaseInOutBack);

        PieDataSet dataSet = new PieDataSet(yValues, "Restante:"+contenedor100);
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(0f);
        dataSet.setValueLineColor(100);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueTextColor(android.R.color.white);
        data.setValueTextSize(10f);
        pieChart.setData(data);
        MensajeInstrucciones();



    }
    public void MensajeInstrucciones(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage("Instrucciones:\n"+"Configurar la aplicacion"+"\n"+"Conectarse al Bluetooth"+"\n"+"La grafica se actualiza tocandola");
        // builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();
    }
    public void alerta( ){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Importante");
        builder.setMessage("Niveles muy bajos");
      //  builder.setPositiveButton("OK",null);
        builder.create();
        builder.show();

    }

    public void ShowPopup(View v) {
        TextView txtclose;
        myDialog.setContentView(R.layout.popup);
        txtclose =myDialog.findViewById(R.id.txtclose);
        txtclose.setText("X");
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }

    public void confi(View v){
            Intent intent = new Intent();
            intent.setClass(MainActivity.this, UserConfig.class);
            startActivity(intent);


        }

   /*/ public void setUiEnabled(boolean bool)
    {   startButton.setEnabled(!bool);
        // sendButton.setEnabled(bool);
        stopButton.setEnabled(bool);
        textView.setEnabled(bool);

    }/*/

    public boolean BTinit()
    {
        boolean found=false;
        BluetoothAdapter bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Dispositivo no soporta el bluetotooh",Toast.LENGTH_SHORT).show();
        }
        if(!bluetoothAdapter.isEnabled())
        {
            Intent enableAdapter = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableAdapter, 0);

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Encender Bluetooth",Toast.LENGTH_SHORT).show();
            }
        }
        Set<BluetoothDevice> bondedDevices = bluetoothAdapter.getBondedDevices();
        if(bondedDevices.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"conectarse al dispositivo ",Toast.LENGTH_SHORT).show();
        }
        else
        {
            for (BluetoothDevice iterator : bondedDevices)
            {
                if(iterator.getAddress().equals(DEVICE_ADDRESS))
                {
                    device=iterator;
                    found=true;
                    break;
                }
            }
        }
        return found;
    }

    public boolean BTconnect()
    {
        boolean connected=true;
        try {
            socket = device.createRfcommSocketToServiceRecord(PORT_UUID);
            socket.connect();
        } catch (IOException e) {
            e.printStackTrace();
            connected=false;
            Toast.makeText(getApplicationContext(),"Conexion perdida",Toast.LENGTH_SHORT).show();
        }
        if(connected)
        {
            try {
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                inputStream=socket.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        return connected;
    }

    public void onClickStart(View view) {
        if(BTinit())
        {
            if(BTconnect())
            {
                //setUiEnabled(true);
                deviceConnected=true;
                beginListenForData();
                Toast.makeText(getApplicationContext(),"Conexion establecida!",Toast.LENGTH_SHORT).show();

            }

        }
    }

    void beginListenForData()
    {

        final Handler handler = new Handler();
        stopThread = false;
        buffer = new byte[1024];


        Thread thread  = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopThread)
                {
                    try {
                        String packet = "";
                        final int byteCount = inputStream.available();
                        final byte[] rawBytes = new byte[byteCount];
                        if (byteCount != 0) {
                            inputStream.read(rawBytes);
                            final String output =new String(rawBytes,"UTF-8");
                            //   final String output = new String(rawBytes, 0, byteCount);
                          //+  textView.append(output);
                            packet += output;
                            if (packet.startsWith("#") && packet.endsWith("#")) {
                                StringTokenizer splitStr = new StringTokenizer(packet, ","); // split string by comma
                                String numberOne = splitStr.nextToken(); // First split string
                                String numberTwo = splitStr.nextToken(); // Second split string
                                numberOne = numberOne.replaceAll("\\D+", ""); // replace all chars, leave only number
                                numberTwo = numberTwo.replaceAll("\\D+", "");
                                packet = "";
                                // System.out.print(packet + numberOne + numberTwo);
                            }
                            packet.replaceAll("#","");

                            //  packet.replaceAll("#",".");

                            System.out.println(packet);
                           // pieChart.animateY(1500, Easing.EaseInBounce);

                            try {

                                int integerValue = Integer.parseInt(packet);
                                System.out.println(integerValue);
                                int z = integerValue;


                                if (integerValue==7 ) {
                                    alerta();
                                    pieChart.getDescription().setEnabled(false);
                                    pieChart.setDragDecelerationFrictionCoef(6f);
                                    pieChart.setDrawHoleEnabled(true);
                                    pieChart.setHoleColor(android.R.color.white);
                                    pieChart.setTransparentCircleRadius(61f);
                                    pieChart.setDrawSlicesUnderHole(true);

                                    int x = integerValue +60;
                                    int contenedor100 = 100;
                                    int contenedorVacio = contenedor100 - x;

                                    pieChart.setCenterText(contenedorVacio+"%");
                                    pieChart.setCenterTextColor( Color.parseColor("#ffffff"));
                                    pieChart.setCenterTextSize(30f);
                                    ArrayList<PieEntry> yValues = new ArrayList<>();
                                    yValues.add(new PieEntry((float) contenedorVacio, contenedorVacio));
                                    yValues.add(new PieEntry((float) x,  x));


                                    PieDataSet dataSet = new PieDataSet(yValues, "Faltante:"+x);
                                    dataSet.setSliceSpace(3f);
                                    dataSet.setSelectionShift(5f);
                                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                    PieData data = new PieData(dataSet);
                                    data.setValueTextColor(android.R.color.holo_purple);
                                    data.setValueTextSize(10f);
                                    pieChart.setData(data);

                                } else if (z ==8) {
alerta();
                                    pieChart.getDescription().setEnabled(false);

                                    pieChart.setDragDecelerationFrictionCoef(6f);

                                    pieChart.setDrawHoleEnabled(true);
                                    pieChart.setHoleColor(android.R.color.white);
                                    pieChart.setTransparentCircleRadius(61f);
                                    pieChart.setDrawSlicesUnderHole(true);
                                    int x = z + 70;
                                    int contenedor100 = 100;
                                    int contenedorVacio = contenedor100 - x;
                                    pieChart.setCenterText(contenedorVacio+"%");
                                    pieChart.setCenterTextColor( Color.parseColor("#ffffff"));
                                    pieChart.setCenterTextSize(30f);
                                    ArrayList<PieEntry> yValues = new ArrayList<>();
                                    yValues.add(new PieEntry((float) contenedorVacio, contenedorVacio));
                                    yValues.add(new PieEntry((float) x, x));

                                    PieDataSet dataSet = new PieDataSet(yValues, "Faltante:"+x);
                                    dataSet.setSliceSpace(3f);
                                    dataSet.setSelectionShift(5f);
                                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                    PieData data = new PieData(dataSet);
                                    data.setValueTextColor(android.R.color.holo_purple);
                                    data.setValueTextSize(10f);
                                    pieChart.setData(data);


                                }else if (integerValue ==9 ){
                                    alerta();
                                    pieChart.getDescription().setEnabled(false);


                                    pieChart.setDragDecelerationFrictionCoef(6f);

                                    pieChart.setDrawHoleEnabled(true);
                                    pieChart.setHoleColor(android.R.color.white);
                                    pieChart.setTransparentCircleRadius(61f);
                                    pieChart.setDrawSlicesUnderHole(true);
                                    int x  = (z + 80) ;
                                    int contenedor100 = 100;
                                    int contenedorVacio = contenedor100 - x;
                                    pieChart.setCenterText(contenedorVacio+"%");
                                    pieChart.setCenterTextColor( Color.parseColor("#ffffff"));
                                    pieChart.setCenterTextSize(30f);
                                    ArrayList<PieEntry> yValues = new ArrayList<>();
                                    yValues.add(new PieEntry((float) contenedorVacio, contenedorVacio));
                                    yValues.add(new PieEntry((float) x, x));

                                    PieDataSet dataSet = new PieDataSet(yValues, "Faltante:"+x);

                                    dataSet.setSliceSpace(3f);
                                    dataSet.setSelectionShift(5f);
                                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                    PieData data = new PieData(dataSet);
                                    data.setValueTextColor(android.R.color.holo_purple);
                                    data.setValueTextSize(10f);
                                    pieChart.setData(data);



                                }else if (z ==1 ){

                                    pieChart.getDescription().setEnabled(false);
                                    pieChart.setDragDecelerationFrictionCoef(6f);

                                    pieChart.setDrawHoleEnabled(true);
                                    pieChart.setHoleColor(android.R.color.white);
                                    pieChart.setTransparentCircleRadius(61f);
                                    pieChart.setDrawSlicesUnderHole(true);

                                    Intent recibir = getIntent();
                                    int dat = recibir.getIntExtra("datos",0);
                                    System.out.println("recibido"+dat);
                                    int x = z +z;
                                    int contenedor100 = dat;
                                    int contenedorVacio = contenedor100 - x;
                                    pieChart.setCenterText(contenedorVacio+"%");
                                    pieChart.setCenterTextColor( Color.parseColor("#ffffff"));
                                    pieChart.setCenterTextSize(30f);
                                    ArrayList<PieEntry> yValues = new ArrayList<>();
                                    yValues.add(new PieEntry((float) contenedorVacio, contenedorVacio));
                                    yValues.add(new PieEntry((float) x, x));

                                    PieDataSet dataSet = new PieDataSet(yValues, "Faltante:"+x);
                                    dataSet.setSliceSpace(3f);
                                    dataSet.setSelectionShift(5f);
                                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                    PieData data = new PieData(dataSet);
                                    data.setValueTextColor(android.R.color.holo_purple);
                                    data.setValueTextSize(10f);
                                    pieChart.setData(data);

                                }else if (z ==2 ){

                                    pieChart.getDescription().setEnabled(false);


                                    pieChart.setDragDecelerationFrictionCoef(6f);

                                    pieChart.setDrawHoleEnabled(true);
                                    pieChart.setHoleColor(android.R.color.white);
                                    pieChart.setTransparentCircleRadius(61f);
                                    pieChart.setDrawSlicesUnderHole(true);
                                    int x = z + 10;
                                    int contenedor100 = 100;
                                    int contenedorVacio = contenedor100 - x;
                                    pieChart.setCenterText(contenedorVacio+"%");
                                    pieChart.setCenterTextColor( Color.parseColor("#ffffff"));
                                    pieChart.setCenterTextSize(30f);
                                    ArrayList<PieEntry> yValues = new ArrayList<>();
                                    yValues.add(new PieEntry((float) contenedorVacio, contenedorVacio));
                                    yValues.add(new PieEntry((float) x, x));

                                    PieDataSet dataSet = new PieDataSet(yValues, "Faltante:"+x);
                                    dataSet.setSliceSpace(3f);
                                    dataSet.setSelectionShift(5f);
                                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                    PieData data = new PieData(dataSet);
                                    data.setValueTextColor(android.R.color.holo_purple);
                                    data.setValueTextSize(10f);
                                    pieChart.setData(data);


                                }else if (z ==3 ){

                                    pieChart.getDescription().setEnabled(false);


                                    pieChart.setDragDecelerationFrictionCoef(6f);

                                    pieChart.setDrawHoleEnabled(true);
                                    pieChart.setHoleColor(android.R.color.white);
                                    pieChart.setTransparentCircleRadius(61f);
                                    pieChart.setDrawSlicesUnderHole(true);
                                    int x= z + 20+3;
                                    int contenedor100 = 100;
                                    int contenedorVacio = contenedor100 - x;
                                    pieChart.setCenterText(contenedorVacio+"%");
                                    pieChart.setCenterTextColor( Color.parseColor("#ffffff"));
                                    pieChart.setCenterTextSize(30f);

                                    ArrayList<PieEntry> yValues = new ArrayList<>();
                                    yValues.add(new PieEntry((float) contenedorVacio, contenedorVacio));
                                    yValues.add(new PieEntry((float) x, x));

                                    PieDataSet dataSet = new PieDataSet(yValues, "Faltante:"+x);
                                    dataSet.setSelectionShift(5f);
                                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                    PieData data = new PieData(dataSet);
                                    data.setValueTextColor(android.R.color.holo_purple);
                                    data.setValueTextSize(10f);
                                    pieChart.setData(data);

                                }else if (z ==4 ){

                                    pieChart.getDescription().setEnabled(false);


                                    pieChart.setDragDecelerationFrictionCoef(6f);

                                    pieChart.setDrawHoleEnabled(true);
                                    pieChart.setHoleColor(android.R.color.white);
                                    pieChart.setTransparentCircleRadius(61f);
                                    pieChart.setDrawSlicesUnderHole(true);
                                    int x = z + 30;
                                    int contenedor100 = 100;
                                    int contenedorVacio = contenedor100 - x;
                                    pieChart.setCenterText(contenedorVacio+"%");
                                    pieChart.setCenterTextColor( Color.parseColor("#ffffff"));
                                    pieChart.setCenterTextSize(30f);
                                    ArrayList<PieEntry> yValues = new ArrayList<>();
                                    yValues.add(new PieEntry((float) contenedorVacio, contenedorVacio));
                                    yValues.add(new PieEntry((float) x, x));

                                    PieDataSet dataSet = new PieDataSet(yValues, "Faltantee:"+x);
                                     dataSet.setSliceSpace(3f);
                                    dataSet.setSelectionShift(5f);
                                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                    PieData data = new PieData(dataSet);
                                    data.setValueTextColor(android.R.color.holo_purple);
                                    data.setValueTextSize(10f);
                                    pieChart.setData(data);

                                }else if (z ==5 ){

                                    pieChart.getDescription().setEnabled(false);


                                    pieChart.setDragDecelerationFrictionCoef(6f);

                                    pieChart.setDrawHoleEnabled(true);
                                    pieChart.setHoleColor(android.R.color.white);
                                    pieChart.setTransparentCircleRadius(61f);
                                    pieChart.setDrawSlicesUnderHole(true);
                                    int x = z + 40;
                                    int contenedor100 = 100;
                                    int contenedorVacio = contenedor100 - x;

                                    pieChart.setCenterText(contenedorVacio+"%");
                                    pieChart.setCenterTextColor( Color.parseColor("#ffffff"));
                                    pieChart.setCenterTextSize(30f);
                                    ArrayList<PieEntry> yValues = new ArrayList<>();
                                    yValues.add(new PieEntry((float) contenedorVacio, contenedorVacio));
                                    yValues.add(new PieEntry((float) x, x));

                                    PieDataSet dataSet = new PieDataSet(yValues, "Faltante:"+x);
                                    dataSet.setSliceSpace(3f);
                                    dataSet.setSelectionShift(5f);
                                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                    PieData data = new PieData(dataSet);
                                    data.setValueTextColor(android.R.color.holo_purple);
                                    data.setValueTextSize(10f);
                                    pieChart.setData(data);

                                }else if (z ==6 ){

                                    pieChart.getDescription().setEnabled(false);


                                    pieChart.setDragDecelerationFrictionCoef(6f);

                                    pieChart.setDrawHoleEnabled(true);
                                    pieChart.setHoleColor(android.R.color.white);
                                    pieChart.setTransparentCircleRadius(61f);
                                    pieChart.setDrawSlicesUnderHole(true);
                                    int x = z + 50;
                                    int contenedor100 = 100;
                                    int contenedorVacio = contenedor100 - x;
                                    pieChart.setCenterText(contenedorVacio+"%");
                                    pieChart.setCenterTextColor( Color.parseColor("#ffffff"));
                                    pieChart.setCenterTextSize(30f);
                                    ArrayList<PieEntry> yValues = new ArrayList<>();
                                    yValues.add(new PieEntry((float) contenedorVacio, contenedorVacio));
                                    yValues.add(new PieEntry((float) x, x));

                                    PieDataSet dataSet = new PieDataSet(yValues, "Faltante:"+x);
                                    dataSet.setSliceSpace(3f);
                                    dataSet.setSelectionShift(5f);
                                    dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

                                    PieData data = new PieData(dataSet);
                                    data.setValueTextColor(android.R.color.holo_purple);
                                    data.setValueTextSize(10f);
                                    pieChart.setData(data);

                                }


                            }

                            catch(Exception e){
                                System.out.println("Error");

                            }




                            //
                            //System.out.print(packet);
                            // final int x = Integer.parseInt(packet);

                            // int n = 10  ;

                            //   System.out.println(n);
                            //


                            // char[] aCaracteres = readMessage.toCharArray();
                            //for (int x =0; x<aCaracteres.length; x++)
                            //  System.out.println("[" + x + "] " + aCaracteres[x] );


                            //int num = 0  ;
                            //final int  data= Integer.parseInt((readMessage));
                            //  System.out.println(readMessage);


                            handler.post(new Runnable() {
                                public void run() {

                                    //textView.append(readMessage);



                                }

                            });

                        }

                    }
                    catch (IOException ex)
                    {
                        stopThread = true;
                    }
                }
            }
        });

        thread.start();
    }


    public void cancelar() {
        finish();
    }

    public void onClickSend(View view) {
            int Actualizar = 0;

            try {
                outputStream.write(Actualizar);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "No estas conectado", Toast.LENGTH_SHORT).show();
            }


    }


    public void onClickStop(View view) throws IOException {
        onDestroy();
        //ShowPopup();
        stopThread = true;
        outputStream.close();
        inputStream.close();
        socket.close();
        //setUiEnabled(false);
        deviceConnected=false;
        //Toast.makeText(getApplicationContext(),"Desconectado",Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void onClickClear(View view) {
     //  textView.setText("");
    }



}

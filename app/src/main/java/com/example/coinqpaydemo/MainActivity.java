package com.example.coinqpaydemo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coinqpay.CommUtil;
import com.example.coinqpay.OrderPayActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        EditText merchantName= findViewById(R.id.merchantName);
        EditText orderCode= findViewById(R.id.orderCode);
        EditText network= findViewById(R.id.network);
        EditText chainId= findViewById(R.id.chainId);
        EditText symbol= findViewById(R.id.symbol);
        EditText amount= findViewById(R.id.amount);
        EditText decimal= findViewById(R.id.decimal);
        EditText address= findViewById(R.id.address);
        EditText contractAddress= findViewById(R.id.contractAddress);

        //输入测试
        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(merchantName.getText().toString().isEmpty()||orderCode.getText().toString().isEmpty()||network.getText().toString().isEmpty()||symbol.getText().toString().isEmpty()
                        ||amount.getText().toString().isEmpty()||decimal.getText().toString().isEmpty()||address.getText().toString().isEmpty()){

                    CommUtil.Toast(MainActivity.this,"有必传项未输入");
                    return;
                }



                Intent intent = new Intent(MainActivity.this, OrderPayActivity.class);
                intent.putExtra("merchantName", merchantName.getText().toString());
                intent.putExtra("orderCode", orderCode.getText().toString());
                intent.putExtra("amount", amount.getText().toString());

                intent.putExtra("network", network.getText().toString());
                intent.putExtra("chainId", chainId.getText().toString());

                intent.putExtra("symbol", symbol.getText().toString());
                intent.putExtra("decimal", decimal.getText().toString());
                intent.putExtra("address", address.getText().toString());
                intent.putExtra("contractAddress", contractAddress.getText().toString());

                startActivity(intent);
            }


        });


        //免输测试
        TextView noInputButton = findViewById(R.id.noInput);
        noInputButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, OrderPayActivity.class);
                intent.putExtra("merchantName", "商户一号");
                intent.putExtra("orderCode", "123456789");
                intent.putExtra("amount", "0.13");

//                //ETH  -----Sepolia测试网
                intent.putExtra("network", "ethereum");
                intent.putExtra("chainId", "11155111");
//                /*主币*/
                intent.putExtra("symbol", "ETH");
                intent.putExtra("decimal", 18);
                intent.putExtra("address", "0x58806D167911019Cf86a39944830aBd791922883");
                intent.putExtra("contractAddress", "");
//                /*代币*/
//                intent.putExtra("symbol", "USDT");
//                intent.putExtra("decimal", 6);
//                intent.putExtra("address", "0x58806D167911019Cf86a39944830aBd791922883");
//                intent.putExtra("contractAddress", "0x05A55EAa4DeAE11Ce608b10a4De7c965Fff76133");
//
//                //BSC  -----bsc testnet测试网
//                intent.putExtra("network", "BSC Testnet");
//                intent.putExtra("chainId", "97");
//                /*主币*/
//                intent.putExtra("symbol", "BNB");
//                intent.putExtra("decimal", 18);
//                intent.putExtra("address", "0x361DDa3b2Aa96c4F263b1DfeE1eE6836075817E1");
//                intent.putExtra("contractAddress", "");
//                /*代币*/
//                intent.putExtra("symbol", "USDT");
//                intent.putExtra("decimal", 6);
//                intent.putExtra("address", "0x361DDa3b2Aa96c4F263b1DfeE1eE6836075817E1");
//                intent.putExtra("contractAddress", "0xdac17f958d2ee523a2206206994597c13d831ec7");
//                //BSC  -----正式网
//                intent.putExtra("network", "ethereum");
//                intent.putExtra("chainId", "56");
//                /*主币*/
//                intent.putExtra("symbol", "BNB");
//                intent.putExtra("decimal", 18);
//                intent.putExtra("address", "0x5B38Da6a701c568545dCfcB03FcB875f56beddC4");
//                intent.putExtra("contractAddress", "");
//                /*代币*/
//                intent.putExtra("symbol", "USDT");
//                intent.putExtra("decimal", 18);
//                intent.putExtra("address", "0x5B38Da6a701c568545dCfcB03FcB875f56beddC4");
//                intent.putExtra("contractAddress", "0x55d398326f99059ff775485246999027b3197955");
//
//                intent.putExtra("symbol", "ETH");
//                intent.putExtra("decimal", 18);
//                intent.putExtra("address", "0x5B38Da6a701c568545dCfcB03FcB875f56beddC4");
//                intent.putExtra("contractAddress", "0x2170ed0880ac9a755fd29b2688956bd959f933f8");
//
//
//                //Tron
//                intent.putExtra("network", "tron");
//                /*主币*/
//                intent.putExtra("chainId", "");
//                intent.putExtra("symbol", "TRX");
//                intent.putExtra("decimal", 6);
//                intent.putExtra("address", "TAzNdisHqicoQHr3NVBKqqZrwqHunXSkDS");
//                intent.putExtra("contractAddress", "");
                /*代币*/
//                intent.putExtra("chainId", "");
//                intent.putExtra("symbol", "USDT");
//                intent.putExtra("decimal", 6);
//                intent.putExtra("address", "TAzNdisHqicoQHr3NVBKqqZrwqHunXSkDS");
//                intent.putExtra("contractAddress", "TR7NHqjeKQxGTCi8q8ZY4pL8otSzgjLj6t");


                startActivity(intent);
            }


        });
    }
}
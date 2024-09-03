package com.example.coinqpay;

import static java.lang.Math.pow;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.tokenpocket.opensdk.base.TPListener;
import com.tokenpocket.opensdk.base.TPManager;
import com.tokenpocket.opensdk.simple.model.Authorize;
import com.tokenpocket.opensdk.simple.model.Blockchain;
import com.tokenpocket.opensdk.simple.model.Transaction;
import com.tokenpocket.opensdk.simple.model.Transfer;


import org.json.JSONObject;
import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class OrderPayActivity extends AppCompatActivity {

    private static final String TAG = "OrderPayActivityTag";

    public String merchantName;
    public String orderCode;
    public String network;
    public String chainId;
    public String symbol;
    public String amount;
    public int decimal;
    public String address;
    public String contractAddress;

    private String fromAddress;

    CommUtil.TimerClass timerClass;

    String hashID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_order_pay);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Intent intent = getIntent();
        merchantName = intent.getStringExtra("merchantName");
        orderCode = intent.getStringExtra("orderCode");
        network = intent.getStringExtra("network");
        chainId = intent.getStringExtra("chainId");
        symbol = intent.getStringExtra("symbol");
        amount = intent.getStringExtra("amount");
        decimal = intent.getIntExtra("decimal",0);
        address = intent.getStringExtra("address");
        contractAddress = intent.getStringExtra("contractAddress");


        // 设置状态栏颜色为白色
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.pay_status_bar_color));

        initView ();

    }

    /*订单初始状态*/
    public  void initView () {
        TextView tvCancelBtn = findViewById(R.id.back);
        tvCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               finish();
               timerClass.cancelTimer();
            }
        });
        //倒计时
        TextView tvCountdown = findViewById(R.id.tvCountdown);
        timerClass = new CommUtil.TimerClass(tvCountdown,15*60);
        timerClass.startTimer();
        timerClass.setOnFinishListener(new CommUtil.TimerClass.OnFinishCallback() {
            @Override
            public void onResult() {
                //订单超时---倒计时结束
                timeOutStatusView();
            }
        });

        View resultTipView =  findViewById(R.id.resultTip);
        resultTipView.setVisibility(View.GONE);//设置不可见且不保留空间


        //商户名称
        TextView tvMerchantName = findViewById(R.id.tvMerchantName);
        tvMerchantName.setText(merchantName);
        //商户订单号
        TextView tvOrderCode = findViewById(R.id.tvOrderCode);
        tvOrderCode.setText(orderCode);
        TextView tvCopyOrder= findViewById(R.id.copyOrder);
        tvCopyOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              CommUtil.copyTextToClipboard(OrderPayActivity.this,orderCode);

            }
        });

        String chainNameV;
        if (network.toLowerCase().equals("eth")
                || (network.toLowerCase().equals("ethereum") && network.toLowerCase().equals(""))
                || (network.toLowerCase().equals("ethereum") && network.toLowerCase().equals("11155111"))) {
            chainNameV = "eth";
        } else if ((network.toLowerCase().equals("ethereum") && network.toLowerCase().equals("56"))
                || (network.toLowerCase().equals("bsc testnet") && network.toLowerCase().equals("97"))){
            chainNameV = "bsc";
        } else {
            chainNameV = network.toLowerCase();
        }

        //ChainImage
        ImageView ivChain = findViewById(R.id.ivChain);
        ivChain.setImageResource(chainNameV.equals("eth")? R.drawable.chain_eth:chainNameV.equals("bsc")?R.drawable.chain_bsc:R.drawable.chain_tron);

        //Chain
        TextView tvChain = findViewById(R.id.tvChain);
        String capitalizedText = CommUtil.capitalizeFirstLetter(network);
        tvChain.setText(capitalizedText);

        //订单金额
        TextView tvAmount = findViewById(R.id.tvAmount);
        tvAmount.setText(amount);
        TextView tvSymbol = findViewById(R.id.tvSymbol);
        tvSymbol.setText(symbol);

        //收款 不显示
        View sLine4View =  findViewById(R.id.sLine4);
        sLine4View.setVisibility(View.GONE);//设置不可见且不保留空间

        View collectView =  findViewById(R.id.clCollection);
        collectView.setVisibility(View.GONE);//设置不可见且不保留空间


        //Open TP
        View openView = findViewById(R.id.vOpenTP);
        openView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openTP();
            }
        });

        //二维码
        ImageView qrImgV= findViewById(R.id.qrCode);
        Bitmap qrCodeBitmap = CommUtil.generateQRCode(address, 144, 144);
        qrImgV.setImageBitmap(qrCodeBitmap);

        //收款地址
        TextView tvAddress = findViewById(R.id.tvAddress);
        tvAddress.setText(address);
        tvAddress.setMaxLines(2);

        ImageView tvCopyAddress= findViewById(R.id.copyAddress);
        tvCopyAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CommUtil.copyTextToClipboard(OrderPayActivity.this,address);
            }
        });

    }

    /*支付成功状态*/
    public  void paySuccessStatusView(){
        TextView backBtn = findViewById(R.id.back);
        backBtn.setText("完成");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                timerClass.cancelTimer();
            }
        });

        //倒计时隐藏
        TextView tvCountdown = findViewById(R.id.tvCountdown);
        tvCountdown.setVisibility(View.GONE);//设置不可见且不保留空间

        //支付结果Tips
        View resultTipView =  findViewById(R.id.resultTip);
        resultTipView.setVisibility(View.VISIBLE);//设置可见
        ImageView search= findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String searchUrl = "";
                if (network.toLowerCase().equals("eth")
                        || (network.toLowerCase().equals("ethereum") && chainId.equals(""))) {
                    searchUrl = "https://etherscan.io/tx/";
                } else if (network.toLowerCase().equals("ethereum") && chainId.equals("56")){
                    searchUrl = "https://bscscan.com/tx/";
                } else if (network.toLowerCase().equals("tron")){
                    searchUrl = "https://tronscan.io/#/transaction/";
                }else if (network.toLowerCase().equals("ethereum") && chainId.equals("11155111")){
                    searchUrl = "https://sepolia.etherscan.io/tx/";
                }else if (network.toLowerCase().equals("bsc testnet") && chainId.equals("97")){
                    searchUrl = "https://testnet.bscscan.com/tx/";
                }

                String searchNewUrl =  String.format("%s%s",searchUrl,hashID);
                Log.d("searchNewUrl",searchNewUrl);

                //跳转到WebView页面
                Intent intent = new Intent(OrderPayActivity.this, WebViewActivity.class);
                intent.putExtra("url", searchNewUrl);//"https://www.baidu.com"
                startActivity(intent);


            }
        });

        //修改"支付倒计时"的图标和文字
        ImageView imageView = findViewById(R.id.ivStatus);
        imageView.setImageResource(R.drawable.success);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
        params.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
        imageView.setLayoutParams(params);

        TextView tvStatus = findViewById(R.id.tvStatus);
        tvStatus.setText("发送成功");
        tvStatus.setTextColor(Color.BLACK);
        tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        tvStatus.setTypeface(typeface);

        View sLineEndView =  findViewById(R.id.sLineEnd);
        sLineEndView.setVisibility(View.GONE);//设置可见

        View sLine4View =  findViewById(R.id.sLine4);
        sLine4View.setVisibility(View.VISIBLE);//设置可见

        View collectView =  findViewById(R.id.clCollection);
        collectView.setVisibility(View.VISIBLE);//设置可见

        //收款
        TextView tvCollection = findViewById(R.id.tvCollection);
        tvCollection.setText(amount);
        TextView tvSymbol2 = findViewById(R.id.tvSymbol2);
        tvSymbol2.setText(symbol);

        View qrTotalView =  findViewById(R.id.qrTotalView);
        qrTotalView.setVisibility(View.GONE);//设置不可见且不保留空间


    }
    /*订单超时状态*/
    public  void timeOutStatusView(){
        TextView backBtn = findViewById(R.id.back);
        backBtn.setText("返回");

        //倒计时隐藏
        TextView tvCountdown = findViewById(R.id.tvCountdown);
        tvCountdown.setVisibility(View.GONE);//设置不可见且不保留空间

        //修改"支付倒计时"的图标和文字
        ImageView imageView = findViewById(R.id.ivStatus);
        imageView.setImageResource(R.drawable.fail);
        ViewGroup.LayoutParams params = imageView.getLayoutParams();
        params.width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
        params.height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 36, getResources().getDisplayMetrics());
        imageView.setLayoutParams(params);

        TextView tvStatus = findViewById(R.id.tvStatus);
        tvStatus.setText("已超时，支付失败");
        tvStatus.setTextColor(Color.RED);
        tvStatus.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        Typeface typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);
        tvStatus.setTypeface(typeface);



        View sLineEndView =  findViewById(R.id.sLineEnd);
        sLineEndView.setVisibility(View.INVISIBLE);//设置不可见但保留空间

        View sLine4View =  findViewById(R.id.sLine4);
        sLine4View.setVisibility(View.VISIBLE);//设置可见

        View clView =  findViewById(R.id.clCollection);
        clView.setVisibility(View.GONE);//设置不可见且不保留空间


        View qrTotalView = findViewById(R.id.qrTotalView);
        qrTotalView.setVisibility(View.GONE);//设置不可见且不保留空间
    }

    /**
     *  通用 登录
     */

    public  void openTP () {
        if(!CommUtil.isValidAmount(amount,decimal)){
            CommUtil.Toast(OrderPayActivity.this,"amount格式不正确!");
            return;
        }



        Authorize authorize = new Authorize();

        List blockchains = new ArrayList();
        //blockchains指定可以用哪些网络的钱包操作，evm系列，第一个参数是ethereum,第二个参数是网络的id
        blockchains.add(new Blockchain(network, chainId));
        authorize.setBlockchains(blockchains);

        authorize.setDappName("TokenPocket");
        authorize.setDappIcon("https://gz.bcebos.com/v1/tokenpocket/temp/mobile_sdk_demo.png");


        TPManager.getInstance().authorize(this, authorize, new TPListener() {
            @Override
            public void onSuccess(String s) {
//                Toast.makeText(OrderPayActivity.this, s, Toast.LENGTH_LONG).show();

                Map<String, String> map = new Gson().fromJson(s, HashMap.class);
                Log.d(TAG, "这是信息Map"+map);
                fromAddress = map.get("wallet");
                network = map.get("network");
                chainId = map.get("chainId");
                Log.d(TAG, "这是from address==="+fromAddress);
                Log.d(TAG, "这是network=="+network);
                Log.d(TAG, "这是chainId==="+chainId);

                //发起转账
                if(network.toLowerCase().equals("eth")||network.toLowerCase().equals("ethereum")||network.toLowerCase().equals("bsc")||network.toLowerCase().equals("bsc testnet")){
                    pushEVM();
                }else if(network.toLowerCase().equals("tron")){
                    pushTron();
                }


            }

            @Override
            public void onError(String s) {
                Toast.makeText(OrderPayActivity.this, s, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancel(String s) {
                Toast.makeText(OrderPayActivity.this, s, Toast.LENGTH_LONG).show();

            }
        });
    }


    /**
     * EVM 通用交易
     */
    private void pushEVM() {

        Transaction transaction = new Transaction();
        //标识链
        List<Blockchain> blockchains = new ArrayList<>();
        //指定哪个网络的钱包来执行这次操作
        blockchains.add(new Blockchain(network, chainId));
        transaction.setBlockchains(blockchains);

        transaction.setDappName("TokenPocket");
        transaction.setDappIcon("https://gz.bcebos.com/v1/tokenpocket/temp/mobile_sdk_demo.png");
        transaction.setActionId(String.format("android-push-evm-%s", CommUtil.currentTimeStr()));
        transaction.setAction("pushTransaction");


        BigDecimal num1 = new BigDecimal(amount);
        BigDecimal num2 = new BigDecimal(pow(10, decimal));
        BigDecimal product = num1.multiply(num2); // 乘法
        String hexAmount = Long.toHexString(product.longValue());// String.format("%016x", product.longValue())
        Log.d(TAG, "hexAmount==="+hexAmount);

        String dataHexStr =String.format("0x%s%s%s", "a9059cbb",CommUtil.padStringTo64Bits(address.substring(2)),CommUtil.padStringTo64Bits(hexAmount));
        Log.d(TAG, "dataStr==="+dataHexStr);


        Map<String, Object> map = new HashMap<>();

        if(contractAddress.isEmpty()){
            map.put("from", fromAddress);
            map.put("to", address);
            map.put("data", "");
            map.put("value",  String.format("0x%s", hexAmount));

            map.put("gasLimit", "21000");
        }else {
            map.put("from", fromAddress);
            map.put("to", contractAddress);
            map.put("data", dataHexStr);
//            map.put("value", "0x0");
        }

        //   map.put("gas", "");
        //   map.put("gasPrice", "");
        //   map.put("gasLimit", "21000");


        // 将Map转换为JSON字符串
        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        Log.d(TAG, "jsonString==="+jsonString);

        //这里直接填充你生成的交易数据，
        transaction.setTxData(jsonString);

        TPManager.getInstance().pushTransaction(this, transaction, new TPListener() {
            @Override
            public void onSuccess(String s) {
                //成功后，会返回相应的交易hash，注意，这里并不能保证交易一定在链上成功，开发者需要自己通过交易hash,确认最终链上结果
                //  Toast.makeText(OrderPayActivity.this, s, Toast.LENGTH_LONG).show();
                Log.e("pushTransaction onSuccess", s);

                JsonObject jsonObject = JsonParser.parseString(s).getAsJsonObject();
                hashID = jsonObject.get("txID").getAsString();
                System.out.println(hashID);

                //支付成功
                paySuccessStatusView();
            }

            @Override
            public void onError(String s) {
                Toast.makeText(OrderPayActivity.this, s, Toast.LENGTH_LONG).show();
                Log.e("pushTransaction onError", s);
            }

            @Override
            public void onCancel(String s) {
                Toast.makeText(OrderPayActivity.this, s, Toast.LENGTH_LONG).show();
                Log.e("pushTransaction onCancel", s);
            }
        });
    }

    private void pushTron() {
        Transfer transfer = new Transfer();
        //标识链
        List<Blockchain> blockchains = new ArrayList<>();
        //指定哪个网络的钱包来执行这次操作
        blockchains.add(new Blockchain(network, chainId));

        transfer.setBlockchains(blockchains);
        transfer.setProtocol("TokenPocket");
        transfer.setVersion("1.0");
        transfer.setDappName("Token Pocket");
        transfer.setDappIcon("https://gz.bcebos.com/v1/tokenpocket/temp/mobile_sdk_demo.png");
        transfer.setActionId(String.format("android-push-tron-%s", CommUtil.currentTimeStr()));
        transfer.setAction("transfer");
        transfer.setFrom(fromAddress);
        transfer.setTo(address);
        //代币合约，如果是Tron，则为空
        transfer.setContract(contractAddress);
        transfer.setAmount(Double.valueOf(amount));
        //必须设置
        transfer.setDecimal(decimal);
        transfer.setSymbol(symbol);

        TPManager.getInstance().transfer(this, transfer, new TPListener() {
            @Override
            public void onSuccess(String s) {
                //转账成功后，会返回相应的交易hash，注意，钱包只是把交易push出去，并不能保证最后交易结果，开发者需要根据hash自行确定链上结果
//                Toast.makeText(OrderPayActivity.this, s, Toast.LENGTH_LONG).show();
                Log.e("Transfer onSuccess", s);


                JsonObject jsonObject = JsonParser.parseString(s).getAsJsonObject();
                hashID = jsonObject.get("txID").getAsString();
                System.out.println(hashID);


                //支付成功
                paySuccessStatusView();
            }

            @Override
            public void onError(String s) {
                Toast.makeText(OrderPayActivity.this, s, Toast.LENGTH_LONG).show();
                Log.e("Transfer onError", s);
            }

            @Override
            public void onCancel(String s) {
                Toast.makeText(OrderPayActivity.this, s, Toast.LENGTH_LONG).show();
                Log.e("Transfer onCancel", s);
            }
        });
    }




}
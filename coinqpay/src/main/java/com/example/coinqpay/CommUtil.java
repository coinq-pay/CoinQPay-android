package com.example.coinqpay;



import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CommUtil {

    //当前时间 ：yyy-MM-dd HH:mm:ss
    public static String currentTimeStr(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Calendar calendar = Calendar.getInstance();
        String currentDate = sdf.format(calendar.getTime());
        return currentDate;
    }

   //补足64位
    public static String padStringTo64Bits(String str){
        String  padding = "0000000000000000000000000000000000000000000000000000000000000000";
        Integer targetLength = 64;
        Integer lengthToPad = targetLength - str.length();
        if (lengthToPad > 0) {
            str = padding.substring(0,lengthToPad).concat(str);
        }
        return str;
    }

    //间隔 xxx minit 后的时间戳
    public static long getDateWithInterval(int minit){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minit);
        long tenMinutesLaterTimestamp = calendar.getTimeInMillis();
        return tenMinutesLaterTimestamp;
    }


    //Toast
    public static void Toast(Context context, String text){
        Toast toast =  Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER,0,0);
        toast.show();
    }

    //拷贝
    public static void copyTextToClipboard(Context context, String text) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("label", text));

        Toast(context,"拷贝成功！");
    }
    public static void copyTextToClipboard(Context context, String text,String msg) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        clipboard.setPrimaryClip(ClipData.newPlainText("label", text));

        Toast(context,msg);
    }

    //生成二维码
    public static Bitmap generateQRCode(String data, int width, int height) {
        try {
            // 创建QRCodeWriter实例
            QRCodeWriter qrCodeWriter = new QRCodeWriter();

            // 设置编码参数
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8"); // 字符编码
            hints.put(EncodeHintType.MARGIN, 1); // 边距，最小为0

            // 生成BitMatrix（二维码的矩阵表示）
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height, hints);

            // 初始化Bitmap
            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * width + x] = Color.BLACK;
                    } else {
                        pixels[y * width + x] = Color.WHITE;
                    }
                }
            }

            // 根据像素数组生成Bitmap
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            return bitmap;

        } catch (WriterException e) {
            e.printStackTrace();
            return null;
        }
    }
    //首字母大写
    public static String capitalizeFirstLetter(String original) {
        if (original.length() > 0) {
            return Character.toUpperCase(original.charAt(0)) + original.substring(1);
        }
        return original;
    }

    //验证金额格式正确性
    public static boolean isValidAmount(String amount, int decimal) {
        String amontRegex = String.format("([1-9]([0-9]+)?(\\.[0-9]{1,%d})?)|(^(0){1}$)|(^[0-9]\\.[0-9]{1,%d}?$)",decimal,decimal) ;
        // 编译正则表达式
        Pattern pattern = Pattern.compile(amontRegex);
        // 匹配电话号码
        Matcher matcher = pattern.matcher(amount);
        // 返回是否匹配结果
        return matcher.matches();

    }
    //倒计时
    public static class TimerClass {

        public interface OnFinishCallback {
            void onResult( );
        }
        private OnFinishCallback callback;
        public void setOnFinishListener(OnFinishCallback l) {
            this.callback = l;
        }


        private CountDownTimer countDownTimer = null;

        public  TimerClass(TextView textView, long millis) {

            if (countDownTimer != null) {
                return;
            }
            long day = millis * 1000 / (1000 * 24 * 60 * 60); //单位天
            long hour = (millis * 1000 - day * (1000 * 24 * 60 * 60)) / (1000 * 60 * 60); //单位时
            long minute = (millis * 1000 - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60)) / (1000 * 60); //单位分
            long second = (millis * 1000 - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000;//单位秒
            String contentString1 = "";
            if (day > 0) {
                if (day > 1) {
                    contentString1 = day + " Days";
                } else {
                    contentString1 = day + " Day";
                }

            } else {
                String hourStr = hour + "";
                if (hour < 10) {
                    hourStr = "0" + hour;
                }
                String minuteStr = minute + "";
                if (minute < 10) {
                    minuteStr = "0" + minute;
                }
                String secondStr = second + "";
                if (second < 10) {
                    secondStr = "0" + second;
                }

                contentString1 = hourStr + ":" + minuteStr + ":" + secondStr;
            }


            textView.setText(contentString1);
            countDownTimer = new CountDownTimer(millis * 1000, 1000) {
                @Override
                public void onTick(long l) {

                    long day = l / (1000 * 24 * 60 * 60); //单位天
                    long hour = (l - day * (1000 * 24 * 60 * 60)) / (1000 * 60 * 60); //单位时
                    long minute = (l - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60)) / (1000 * 60); //单位分
                    long second = (l - day * (1000 * 24 * 60 * 60) - hour * (1000 * 60 * 60) - minute * (1000 * 60)) / 1000;//单位秒
                    String contentStr = "";
                    if (day > 0) {
                        if (day > 1) {
                            contentStr = day + " Days";
                        } else {
                            contentStr = day + " Day";
                        }

                    } else {
                        String hourStr = hour + "";
                        if (hour < 10) {
                            hourStr = "0" + hour;
                        }
                        String minuteStr = minute + "";
                        if (minute < 10) {
                            minuteStr = "0" + minute;
                        }
                        String secondStr = second + "";
                        if (second < 10) {
                            secondStr = "0" + second;
                        }

                        contentStr =  minuteStr + ":" + secondStr;
                    }
                    //Log.d("read_activity", contentStr);


                    SpannableString spannableString = new SpannableString(contentStr);
                    // 设置指定位置文字的颜色
                    ForegroundColorSpan colorSpan = new ForegroundColorSpan(Color.GREEN);
                    spannableString.setSpan(colorSpan, 2, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                   // 应用处理过的文本到TextView
                    textView.setText(spannableString);


                }

                @Override
                public void onFinish() {
                    cancelTimer();
                //倒计时结束，刷新
                  callback.onResult();

                }
            };


        }

        public void startTimer() {
            countDownTimer.start();
        }

        public void cancelTimer() {
            countDownTimer.cancel();
        }
    }



}

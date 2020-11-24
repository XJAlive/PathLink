package com.xj.pathlink;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import cn.mama.com.LinkPath;

public class SecondActivity extends AppCompatActivity {

    @LinkPath(value = "jump2Second")
    public static void invoke(Context context) {
        Intent intent = new Intent(context, SecondActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        AppPathLinkUtils.init();
    }


}
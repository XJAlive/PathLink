package com.xj.pathlink;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    AppPathLinkUtils appPathLinkUtils = new AppPathLinkUtils();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppPathLinkUtils.init();
    }

    public void print(View view) {
        appPathLinkUtils.handlerUrl(this, "q://print1");
    }

    public void add(View view) {
        appPathLinkUtils.handlerUrl(this, "q://add?i=8");
    }

    public void showToast(View view) {
        appPathLinkUtils.handlerUrl(this, "q://showToast1?value=4");
    }

    public void jump2Second(View view) {
        appPathLinkUtils.handlerUrl(this, "q://jump2Second");
    }
}
package com.example.bottom_main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class FriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend); // 確保這裡的布局文件名稱正確

        Button backButton = findViewById(R.id.friendback); // 獲取返回按鈕的引用

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish(); // 關閉當前活動，返回到上個活動
            }
        });
    }
}
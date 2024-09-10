package com.example.googlecalendarapi;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.api.services.calendar.CalendarScopes;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 1000;
    private GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化 Google Sign-In 选项
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new com.google.android.gms.common.api.Scope(CalendarScopes.CALENDAR))
                .requestIdToken(getString(R.string.server_client_id))  // 您需要在 strings.xml 中定义 server_client_id
                .build();

        // 创建 GoogleSignInClient 对象
        googleSignInClient = GoogleSignIn.getClient(this, gso);

        // 启动登录意图
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // 获取登录结果
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                Log.d("MainActivity", "Sign-in successful, now fetching events");

                // 传递 account 给 CalendarQuickstart
                CalendarQuickstart quickstart = new CalendarQuickstart();

                // 获取事件信息
                String eventDetails = quickstart.fetchEvents(account);

                // 更新 UI
                TextView eventsTextView = findViewById(R.id.eventsTextView);
                eventsTextView.setText(eventDetails);  // 在 TextView 中显示事件信息
            }
        } catch (ApiException e) {
            Log.w("MainActivity", "signInResult:failed code=" + e.getStatusCode());
        }
    }

}

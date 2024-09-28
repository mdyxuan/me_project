package com.example.bottom_main;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccountFragment extends Fragment {

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private TextView usernameTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ImageView account_back = view.findViewById(R.id.account_back);
        usernameTextView = view.findViewById(R.id.username_text_view); // 初始化 TextView

        // 初始化 FirebaseAuth 和 FirebaseDatabase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference("users");

        // 返回主頁面的按鈕點擊事件
        account_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "返回主頁面", Toast.LENGTH_SHORT).show();
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        // 獲取當前登入的使用者
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // 取得使用者的 UID，這裡是使用 UID 來獲取資料
            String uid = currentUser.getUid();

            // 從 Firebase Realtime Database 中獲取使用者名稱
            mDatabase.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // 確保有資料
                    if (dataSnapshot.exists()) {
                        // 假設資料中有 "username" 欄位
                        String username = dataSnapshot.child("username").getValue(String.class);
                        if (username != null) {
                            // 在 TextView 中顯示使用者名稱
                            usernameTextView.setText(username);
                        } else {
                            Toast.makeText(getActivity(), "未找到使用者名稱", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "使用者資料不存在", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // 處理錯誤
                    Toast.makeText(getActivity(), "無法獲取使用者資訊", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // 使用者未登入
            Toast.makeText(getActivity(), "尚未登入", Toast.LENGTH_SHORT).show();
        }

        return view;
    }
}

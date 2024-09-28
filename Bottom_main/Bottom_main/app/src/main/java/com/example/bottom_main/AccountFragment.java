package com.example.bottom_main;

import android.os.Bundle;
import android.util.Log; // 導入 Log
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
            // 使用者名稱在此用 name 來查詢
            String username = currentUser.getDisplayName(); // 假設你有設置顯示名稱
            Log.d("AccountFragment", "Current User Name: " + username);

            // 從 Firebase Realtime Database 中獲取使用者名稱
            mDatabase.child(username).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // 確保有資料
                    if (dataSnapshot.exists()) {
                        String dbUsername = dataSnapshot.child("username").getValue(String.class);
                        if (dbUsername != null) {
                            usernameTextView.setText(dbUsername);
                        } else {
                            Toast.makeText(getActivity(), "未找到使用者名稱", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getActivity(), "使用者資料不存在", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
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

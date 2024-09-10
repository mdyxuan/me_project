package com.example.bottom_main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import android.widget.ImageView;


public class AccountFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        ImageView account_back = view.findViewById(R.id.account_back);

        account_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(getActivity(), "返回主頁面", Toast.LENGTH_SHORT).show();

                // 使用 FragmentTransaction 切换到 HomeFragment
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                // 替换当前的 CallFragment 为 HomeFragment
                fragmentTransaction.replace(R.id.frame_layout, new HomeFragment());

                // 可选：将该事务添加到返回栈中，以便用户可以按返回键返回 CallFragment
                fragmentTransaction.addToBackStack(null);

                // 提交事务
                fragmentTransaction.commit();
            }
        });
        // 返回视图
        return view;
    }

}
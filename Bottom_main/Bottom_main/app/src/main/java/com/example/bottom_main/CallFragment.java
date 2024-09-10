package com.example.bottom_main;

import android.annotation.SuppressLint;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class CallFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_call, container, false);

        // 找到按钮
        Button create = view.findViewById(R.id.create);
        Button call_back = view.findViewById(R.id.call_back);

        // 设置点击监听器
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 显示 Toast 消息
                Toast.makeText(getActivity(), "活動已創建", Toast.LENGTH_SHORT).show();
            }
        });
        call_back.setOnClickListener(new View.OnClickListener() {
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

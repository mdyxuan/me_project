package com.example.bottom_main;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Calendar;

public class CallFragment extends Fragment {

    private static final int PICK_IMAGE_REQUEST = 1; // 常量用於圖片選擇請求
    private TextView detailDate; // 用於顯示選擇的日期
    private Button selectDateBtn; // 用於選擇日期的按鈕
    private ImageView detailImage; // 用於顯示選擇的圖片
    private Button selectImageBtn; // 用於選擇圖片的按鈕
    private Uri imageUri; // 用於存儲圖片的 URI

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_call, container, false);

        // 找到按钮
        Button create = view.findViewById(R.id.create);
        Button call_back = view.findViewById(R.id.call_back);
        selectDateBtn = view.findViewById(R.id.selectDateBtn); // 找到選擇日期的按鈕
        detailDate = view.findViewById(R.id.detailDate); // 找到顯示日期的 TextView
        detailImage = view.findViewById(R.id.detailImage); // 找到顯示圖片的 ImageView
        selectImageBtn = view.findViewById(R.id.selectImageBtn); // 找到選擇圖片的按鈕

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

        // 設置選擇日期的按鈕點擊事件
        selectDateBtn.setOnClickListener(view1 -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                    (datePicker, selectedYear, selectedMonth, selectedDay) -> {
                        // 更新 TextView 顯示選擇的日期
                        String date = String.format("%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay);
                        detailDate.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        // 設置選擇圖片的按鈕點擊事件
        selectImageBtn.setOnClickListener(view12 -> openFileChooser());

        return view;
    }

    // 打開文件選擇器以選擇圖片
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "選擇圖片"), PICK_IMAGE_REQUEST);
    }

    // 處理圖片選擇的結果
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
                detailImage.setImageBitmap(bitmap); // 將選擇的圖片顯示在 ImageView 中
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getActivity(), "選擇圖片失敗", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

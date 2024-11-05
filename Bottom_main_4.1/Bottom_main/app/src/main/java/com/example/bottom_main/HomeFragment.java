package com.example.bottom_main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.bottom_main.Adapter.CategoryAdapter;
import com.example.bottom_main.Adapter.PopularAdapter;
import com.example.bottom_main.Adapter.RecommendedAdapter;
import com.example.bottom_main.Adapter.SliderAdapter;
import com.example.bottom_main.Domain.Category;
import com.example.bottom_main.Domain.ItemDomain;
import com.example.bottom_main.Domain.Location;
import com.example.bottom_main.Domain.SliderItems;
import com.example.bottom_main.R;
import com.example.bottom_main.databinding.FragmentHomeBinding;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference categoryRef, popularRef, recommendedRef, bannerRef, locationRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        // 接收資料
        Bundle bundle = getArguments();
        if (bundle != null) {
            String username = bundle.getString("username");
            String email = bundle.getString("email");
            String name = bundle.getString("name");
            // 使用這些資料
            TextView usernameTextView = view.findViewById(R.id.textView3);
            usernameTextView.setText(name);
        }
        // 初始化 Firebase 資料庫和各個引用
        database = FirebaseDatabase.getInstance();
        categoryRef = database.getReference("Category");
        popularRef = database.getReference("Popular");
        recommendedRef = database.getReference("Item");
        bannerRef = database.getReference("Banner");
        locationRef = database.getReference("Location");

        // 初始化各個功能
        initLocation();
        initBanner();
        initCategory(); // 加入類別初始化功能
        initRecommended();
        initPopular();
        initSearchFunctionality(); // 初始化搜尋功能
        initNotifyFunctionality(); // 初始化通知功能
        initMapFunctionality(); // 初始化定位功能

        return view;
    }

    // 通知功能的初始化
    private void initNotifyFunctionality() {
        // 設定通知按鈕的點擊事件
        ImageView bellIcon = binding.imageView; // 假設你在布局中有一個 ID 為 imageView 的 ImageView
        bellIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), NotificationActivity.class);
                startActivity(intent); // 跳轉至 NotificationActivity
            }
        });
    }
    // 定位功能的初始化
    private void initMapFunctionality() {
        // 設定定位按鈕的點擊事件
        ImageView location = binding.location1; // 確認布局中有 ID 為 location_main 的 ImageView
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MapActivity.class);
                startActivity(intent); // 跳轉至 MapActivity
            }
        });
    }

    // 搜尋功能的初始化
    private void initSearchFunctionality() {
        EditText searchInput = binding.etSearch; // 綁定 EditText
        Button searchButton = binding.btnSearch; // 綁定 Button

        // 設定按鈕點擊事件
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchInput.getText().toString().trim(); // 取得使用者輸入的搜尋文字
                if (!query.isEmpty()) {
                    searchItemsByTag(query); // 如果有輸入，則進行搜尋
                } else {
                    Toast.makeText(getActivity(), "請輸入搜尋關鍵字", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // 搜尋特定 Tag 的項目
    private void searchItemsByTag(String query) {
        ArrayList<ItemDomain> searchResults = new ArrayList<>(); // 儲存搜尋結果
        recommendedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        ItemDomain item = issue.getValue(ItemDomain.class);
                        if (item != null && item.getTags() != null) {
                            for (String tag : item.getTags()) {
                                if (tag.equalsIgnoreCase(query)) {
                                    searchResults.add(item);
                                    break;
                                }
                            }
                        }
                    }
                    if (!searchResults.isEmpty()) {
                        setupRecyclerView(binding.recyclerViewRecommended, new RecommendedAdapter(searchResults), LinearLayoutManager.HORIZONTAL);
                    } else {
                        showError("No items found matching the search tag.");
                    }
                } else {
                    showError("No items available for search.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleError(error);
            }
        });
    }

    // 初始化熱門目的地
    private void initPopular() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);
        ArrayList<ItemDomain> list = new ArrayList<>();

        popularRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(ItemDomain.class));
                    }
                    setupRecyclerView(binding.recyclerViewPopular, new PopularAdapter(list), LinearLayoutManager.HORIZONTAL);
                    binding.progressBarPopular.setVisibility(View.GONE);
                } else {
                    showError("No popular items found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleError(error);
            }
        });
    }

    // 初始化推薦項目
    private void initRecommended() {
        binding.progressBarRecommended.setVisibility(View.VISIBLE);
        ArrayList<ItemDomain> list = new ArrayList<>();

        recommendedRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(ItemDomain.class));
                    }
                    setupRecyclerView(binding.recyclerViewRecommended, new RecommendedAdapter(list), LinearLayoutManager.HORIZONTAL);
                    binding.progressBarRecommended.setVisibility(View.GONE);
                } else {
                    showError("No recommended items found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleError(error);
            }
        });
    }

    // 初始化類別
    private void initCategory() {
        binding.progressBarCategory.setVisibility(View.VISIBLE);
        ArrayList<Category> list = new ArrayList<>();

        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Category.class));
                    }
                    setupRecyclerView(binding.recyclerViewCategory, new CategoryAdapter(list, getActivity()), LinearLayoutManager.HORIZONTAL);
                    binding.progressBarCategory.setVisibility(View.GONE);
                } else {
                    showError("No categories found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleError(error);
            }
        });
    }

    // 初始化橫幅廣告
    private void initBanner() {
        binding.progressBarBanner.setVisibility(View.VISIBLE);
        ArrayList<SliderItems> items = new ArrayList<>();

        bannerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        items.add(issue.getValue(SliderItems.class));
                    }
                    banners(items);
                    binding.progressBarBanner.setVisibility(View.GONE);
                } else {
                    showError("No banners found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleError(error);
            }
        });
    }

    // 初始化地點
    private void initLocation() {
        ArrayList<Location> list = new ArrayList<>();
        // 可以在這裡添加地點的初始化邏輯
    }

    // RecyclerView 設置
    private void setupRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter, int orientation) {
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), orientation, false));
        recyclerView.setAdapter(adapter);
    }

    // 處理橫幅廣告的 UI 邏輯
    private void banners(ArrayList<SliderItems> items) {
        binding.viewPagerSlider.setAdapter(new SliderAdapter(items, binding.viewPagerSlider));
        binding.viewPagerSlider.setClipToPadding(false);
        binding.viewPagerSlider.setClipChildren(false);
        binding.viewPagerSlider.setOffscreenPageLimit(3);
        binding.viewPagerSlider.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);

        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(40));
        binding.viewPagerSlider.setPageTransformer(transformer);
    }

    // 錯誤處理
    private void handleError(DatabaseError error) {
        Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        binding.progressBarBanner.setVisibility(View.GONE);
    }

    // 顯示錯誤訊息
    private void showError(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }





}
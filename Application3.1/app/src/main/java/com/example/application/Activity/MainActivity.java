package com.example.application.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;

import com.example.application.Adapter.CategoryAdapter;
import com.example.application.Adapter.PopularAdapter;
import com.example.application.Adapter.RecommendedAdapter;
import com.example.application.Adapter.SliderAdapter;
import com.example.application.Domain.Category;
import com.example.application.Domain.ItemDomain;
import com.example.application.Domain.Location;
import com.example.application.Domain.SliderItems;
import com.example.application.R;
import com.example.application.databinding.ActivityMainBinding;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {
    ActivityMainBinding binding;
    private FirebaseDatabase database;
    private DatabaseReference categoryRef, popularRef, recommendedRef, bannerRef, locationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

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
        initCategory();// 加入類別初始化功能
        initRecommended();
        initPopular();

        // 初始化搜尋功能
        initSearchFunctionality();
    }

    // 搜尋功能的初始化
    private void initSearchFunctionality() {
        EditText searchInput = binding.etSearch;// 綁定 EditText
        Button searchButton = binding.btnSearch;// 綁定 Button

        // 設定按鈕點擊事件
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = searchInput.getText().toString().trim();// 取得使用者輸入的搜尋文字
                if (!query.isEmpty()) {
                    searchItemsByTag(query);// 如果有輸入，則進行搜尋
                } else {
                    // 提示使用者輸入搜尋關鍵字
                    Toast.makeText(MainActivity.this, "請輸入搜尋關鍵字", Toast.LENGTH_SHORT).show();
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
                            // 如果Tags中包含搜尋字串，加入結果
                            for (String tag : item.getTags()) {
                                if (tag.equalsIgnoreCase(query)) {
                                    searchResults.add(item);
                                    break;
                                }
                            }
                        }
                    }
                    // 顯示搜尋結果
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

    // 優化的熱門目的地初始化
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

    // 優化的推薦項目初始化
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

    // 優化的類別初始化
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
                    setupRecyclerView(binding.recyclerViewCategory, new CategoryAdapter(list, MainActivity.this), LinearLayoutManager.HORIZONTAL);
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

    // 優化的橫幅廣告初始化
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

    // 優化的地點初始化
    private void initLocation() {
        ArrayList<Location> list = new ArrayList<>();

        locationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot issue : snapshot.getChildren()) {
                        list.add(issue.getValue(Location.class));
                    }
                    ArrayAdapter<Location> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.sp_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinner.setAdapter(adapter);
                } else {
                    showError("No locations found.");
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                handleError(error);
            }
        });
    }

    // 公用 RecyclerView 設置
    private void setupRecyclerView(RecyclerView recyclerView, RecyclerView.Adapter<?> adapter, int orientation) {
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, orientation, false));
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
        Toast.makeText(MainActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        binding.progressBarBanner.setVisibility(View.GONE);
    }

    // 顯示錯誤訊息
    private void showError(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}
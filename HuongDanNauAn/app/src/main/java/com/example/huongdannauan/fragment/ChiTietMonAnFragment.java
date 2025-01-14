package com.example.huongdannauan.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.huongdannauan.R;
import com.example.huongdannauan.adapter.CommentAdapter;
import com.example.huongdannauan.model.Comment;
import com.example.huongdannauan.model.Ingredient;
import com.example.huongdannauan.model.Instruction;
import com.example.huongdannauan.model.Recipe;
import com.example.huongdannauan.model.Step;
import com.example.huongdannauan.model.TrangThai;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChiTietMonAnFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChiTietMonAnFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static String ID_MONAN = "";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Recipe recipe;
    private DatabaseReference databaseReference1;
    private DatabaseReference databaseReference2;
    private ImageView imageDish;
    private TextView titleDish, summaryDish, preparationMinutes, cookingMinutes, healthScore, cuisines;
    private LinearLayout ingredientsContainer, loaimonanContainer, huongdanNauContainer;
    private RecyclerView recyclerViewComment;
    private CommentAdapter adapter;
    private EditText edittextComment;
    protected Button btnSend;
    List<Comment> comments;
    ProgressBar progressBar1;
    ProgressBar progressBar2;

    public ChiTietMonAnFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param idmonan Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChiTietMonAnFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChiTietMonAnFragment newInstance(String idmonan, String param2) {
        ChiTietMonAnFragment fragment = new ChiTietMonAnFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, idmonan);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            ID_MONAN = mParam1;
        }
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chi_tiet_mon_an, container, false);
        // Ánh xạ các view
        imageDish = view.findViewById(R.id.imageDish);
        titleDish = view.findViewById(R.id.titleDish);
        summaryDish = view.findViewById(R.id.summaryDish);
        preparationMinutes = view.findViewById(R.id.preparationMinutes);
        cookingMinutes = view.findViewById(R.id.cookingMinutes);
        healthScore = view.findViewById(R.id.healthScore);
        cuisines = view.findViewById(R.id.cuisines);
        ingredientsContainer = view.findViewById(R.id.ingredientsContainer);
        loaimonanContainer = view.findViewById(R.id.LoaiMonAnContainer);
        huongdanNauContainer = view.findViewById(R.id.huongDanNauAnContainer);
        recyclerViewComment = view.findViewById(R.id.commentRecyclerView);
        edittextComment = view.findViewById(R.id.editTextComment);
        btnSend = view.findViewById(R.id.buttonSend);

        progressBar1 = view.findViewById(R.id.progressBar1);
        progressBar1.setVisibility(View.VISIBLE);
        progressBar2 = view.findViewById(R.id.progressBar2);
        progressBar2.setVisibility(View.VISIBLE);

        addEvent();

        // Tham chiếu tới Firebase Database
        databaseReference1 = FirebaseDatabase.getInstance().getReference().child("recipes").child(mParam1);

        // Lấy dữ liệu món ăn từ Firebase
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Chuyển đổi dữ liệu Firebase thành đối tượng Recipe
                recipe = snapshot.getValue(Recipe.class);

                // Kiểm tra và cập nhật giao diện nếu recipe không null
                if (recipe != null) {
                    updateUI(recipe);
                }

                progressBar1.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RecipeDetailActivity", "Lỗi khi lấy dữ liệu", error.toException());
                progressBar1.setVisibility(View.GONE);
            }
        });
        databaseReference2 = FirebaseDatabase.getInstance().getReference().child("comments").child(mParam1);

        // Lấy dữ liệu món ăn từ Firebase
        databaseReference2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments = new ArrayList<>();
                for (DataSnapshot commentSnapshot : snapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    comments.add(comment);
                }
                if(comments.size()>0) updateComment(comments);
                progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("RecipeDetailActivity", "Lỗi khi lấy dữ liệu", error.toException());
                progressBar2.setVisibility(View.GONE);
            }
        });

        return view;
    }
    private void updateUI(Recipe recipe) {
        Glide.with(this).load(recipe.getImage()).into(imageDish);
        titleDish.setText(recipe.getTitle());
        summaryDish.setText(recipe.getSummary());
        preparationMinutes.setText("Chuẩn bị: " + recipe.getPreparationMinutes() + " phút");
        cookingMinutes.setText("Nấu: " + recipe.getCookingMinutes() + " phút");
        healthScore.setText("Điểm sức khỏe: " + recipe.getHealthScore());

        cuisines.setText(TextUtils.join(", ", recipe.getCuisines()));


        // Thêm Loai mon an
        for (String loaimonan : recipe.getDishTypes()) {
            TextView loaiView = new TextView(getContext());
            loaiView.setText("- " + loaimonan);
            loaiView.setTextSize(22);
            loaimonanContainer.addView(loaiView);
        }

        // Thêm nguyên liệu
        for (Ingredient ingredient : recipe.getExtendedIngredients()) {
            TextView ingredientView = new TextView(getContext());
            ingredientView.setText("• " + ingredient.getName());
            ingredientView.setTextSize(22);
            ingredientsContainer.addView(ingredientView);
        }

        // Thêm huong dan nau an
        if(recipe.getAnalyzedInstructions()!=null){
            for (Instruction instruction : recipe.getAnalyzedInstructions()) {
                if(!instruction.getName().isEmpty()){
                    TextView ingredientView = new TextView(getContext());
                    ingredientView.setText("• "+instruction.getName() + ": ");
                    ingredientView.setTextSize(24);
                    ingredientView.setTextColor(Color.rgb(0, 153, 0));
                    huongdanNauContainer.addView(ingredientView);
                }
                if(instruction.getSteps()!=null){
                    for(Step step : instruction.getSteps()){
                        TextView ingredientView = new TextView(getContext());
                        ingredientView.setText("- Bước "+step.getNumber() + ": " + step.getStep());
                        ingredientView.setTextSize(22);
                        huongdanNauContainer.addView(ingredientView);
                    }
                }
            }
        }
    }
    private void updateComment(List<Comment> commentList) {
        if(commentList!= null){
            recyclerViewComment.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new CommentAdapter(commentList, getContext(), requireActivity().getSupportFragmentManager());
            recyclerViewComment.setAdapter(adapter);
        }
    }
    void addEvent(){
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TrangThai.userEmail.isEmpty()) {
                    // Điều hướng từ Fragment Món ăn đến Fragment Đăng nhập
                    Bundle args = new Bundle();
                    args.putString("return_fragment", "ChiTietMonAnFragment");
                    args.putString("idmonan", mParam1);
                    DangNhapFragment loginFragment = new DangNhapFragment();
                    loginFragment.setArguments(args);

                    openAccountFragment(loginFragment);
                }else {
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference commentsRef = database.getReference("comments/"+mParam1).push(); // id mon an

                    // Tạo dữ liệu cho comment mới
                    Map<String, Object> newComment = new HashMap<>();
                    newComment.put("commentId", commentsRef.getKey());
                    newComment.put("content", edittextComment.getText().toString());
                    newComment.put("date", TrangThai.getCurrentDateString());
                    newComment.put("likes", 0);
                    newComment.put("userEmail", TrangThai.userEmail);
                    newComment.put("replies", new ArrayList<>()); // Không có phản hồi ban đầu

                    // Thêm comment mới vào danh sách
                    commentsRef.setValue(newComment)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    edittextComment.setText("");
                                    System.out.println("Thêm comment thành công!");

                                    // Xóa focus khỏi EditText và ẩn bàn phím
                                    edittextComment.clearFocus();
                                    // Lấy InputMethodManager từ Context
                                    InputMethodManager imm = requireContext().getSystemService(InputMethodManager.class);
                                    imm.hideSoftInputFromWindow(edittextComment.getWindowToken(), 0);


                                    loadComments(comments);
                                    updateComment(comments);

                                } else {
                                    System.err.println("Lỗi khi thêm comment: " + task.getException());
                                }
                            });
                }
            }
        });
    }
    public void loadComments(List<Comment> commentList) {
        progressBar2.setVisibility(View.VISIBLE);
        // Đọc lại dữ liệu từ Firebase và cập nhật adapter
        DatabaseReference commentsRef = FirebaseDatabase.getInstance().getReference("comments/"+mParam1);
        commentsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                commentList.clear();
                for (DataSnapshot commentSnapshot : dataSnapshot.getChildren()) {
                    Comment comment = commentSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                progressBar2.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.err.println("Lỗi khi load comments: " + databaseError.getMessage());
                progressBar2.setVisibility(View.GONE);
            }
        });
    }
    private void openAccountFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

        if (getActivity() != null) {
            Log.d("DangNhapFragment", "Opening Account Fragment");
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Log.e("DangNhapFragment", "Activity is null, cannot open fragment");
        }
    }

}
package com.example.huongdannauan.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huongdannauan.R;
import com.example.huongdannauan.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DangKyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DangKyFragment extends Fragment {
    TextView dangNhap;
    EditText edTenDN, edMail, edPas, edResetPass;
    Button dangKy;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DangKyFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DangKyFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DangKyFragment newInstance(String param1, String param2) {
        DangKyFragment fragment = new DangKyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dang_ky, container, false);
        dangNhap = (TextView) view.findViewById(R.id.txtDN); edTenDN = (EditText) view.findViewById(R.id.edtTenDN);
        edMail = (EditText) view.findViewById(R.id.edMail);
        edPas = (EditText) view.findViewById(R.id.edPass);
        edResetPass = (EditText) view.findViewById(R.id.edResetPass);
        dangKy = (Button) view.findViewById(R.id.btnDK);

        openFragmentOfUser(dangNhap, new DangNhapFragment());

        dangKy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSignUp();
            }
        });
        return view;
    }
    void openFragmentOfUser(TextView textView, Fragment fragment){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, fragment); // fragment_container là id của container chứa fragment
                transaction.addToBackStack(null); // Thêm vào back stack để có thể quay lại FirstFragment
                transaction.commit();
            }
        });
    }

    void onClickSignUp() {
        String email = edMail.getText().toString().trim();
        String password = edPas.getText().toString().trim();
        String resetPassword = edResetPass.getText().toString().trim();
        String TenDN = edTenDN.getText().toString().trim();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        if (email.isEmpty() || password.isEmpty() || resetPassword.isEmpty()) {
            Toast.makeText(requireContext(), "Email and password cannot be empty.", Toast.LENGTH_SHORT).show();
            return; // Ngừng thực hiện nếu có trường rỗng
        }

        if (!password.equals(resetPassword)) { // Kiểm tra xem password có khớp với resetPassword không
            Toast.makeText(requireContext(), "Passwords do not match.", Toast.LENGTH_SHORT).show();
            return; // Ngừng nếu mật khẩu không khớp
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            addUser(TenDN,email);
                            // Đăng ký thành công, chuyển hướng đến AccountFragment
                            openAccountFragment(new AccountFragment());
                        } else {
                            // Nếu đăng ký thất bại, hiển thị thông báo cho người dùng
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Đăng ký thất bại.";
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void openAccountFragment(Fragment fragment) {
        FragmentTransaction transaction = requireActivity().getSupportFragmentManager().beginTransaction();

        if (getActivity() != null) {
            Log.d("DangKyFragment", "Opening Account Fragment");
            transaction.replace(R.id.fragment_container, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            Log.e("DangKyFragment", "Activity is null, cannot open fragment");
        }
    }



    void addUser(String name, String email) {
        DatabaseReference database = FirebaseDatabase.getInstance().getReference("user");

        // Tạo đối tượng người dùng mới
        User newUser = new User("avata1.png",email, "", "", name, "","","");

        // Thêm người dùng vào cơ sở dữ liệu
        String userId = database.push().getKey(); // Tạo ID ngẫu nhiên cho người dùng
        if (userId != null) {
            database.child(userId).setValue(newUser).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("Firebase", "Người dùng đã được thêm thành công: " + name);
                } else {
                    Log.e("Firebase", "Thêm người dùng không thành công: " + task.getException().getMessage());
                }
            });
        } else {
            Log.e("Firebase", "ID người dùng không hợp lệ.");

        }
    }

}
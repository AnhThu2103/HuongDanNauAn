package com.example.huongdannauan.fragment;

import android.app.ProgressDialog;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huongdannauan.R;
import com.example.huongdannauan.model.TrangThai;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DangNhapFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DangNhapFragment extends Fragment {
    TextView dangKy,QuenMK;
    EditText edEmail, edPass;
    Button dangNhap;
    private ProgressDialog progressDialog;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DangNhapFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DangNhapFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DangNhapFragment newInstance(String param1, String param2) {
        DangNhapFragment fragment = new DangNhapFragment();
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
        View view = inflater.inflate(R.layout.fragment_dang_nhap, container, false);
        dangKy = (TextView) view.findViewById(R.id.txtDK);
        edEmail = (EditText) view.findViewById(R.id.edEmail);
        edPass = (EditText) view.findViewById(R.id.edPass);
        dangNhap = (Button) view.findViewById(R.id.btnDN);
        QuenMK = (TextView) view.findViewById(R.id.txtQuenMK);
        openFragmentOfUser(dangKy, new DangKyFragment());

        dangNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn();
            }
        });
        QuenMK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgetPassWord();
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

    void SignIn()
    {
        String email = edEmail.getText().toString().trim();
        String password = edPass.getText().toString().trim();

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
//        progressDialog.show();
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(requireContext(), "Email and password cannot be empty.", Toast.LENGTH_SHORT).show();
            return; // Ngừng thực hiện nếu có trường rỗng
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            TrangThai.userEmail = email;
                            Bundle args = getArguments();
                            if (args != null && "ChiTietMonAnFragment".equals(args.getString("return_fragment"))) {
                                // Quay lại Fragment Món ăn
                                openAccountFragment(ChiTietMonAnFragment.newInstance(args.getString(("idmonan")), ""));
                            } else {
                                // Nếu không, mở Fragment tài khoản như mặc định
                                openAccountFragment(new AccountFragment());
                            }
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "Đăng nhập thất bại.";
                            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show();
                        }
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


    void ForgetPassWord()
    {
//        progressDialog.show();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String emailAddress = edEmail.getText().toString().trim();
        if (emailAddress.isEmpty()) {
//            progressDialog.dismiss();
            Toast.makeText(getActivity(), "Vui lòng nhập email!", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
//                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Email đặt lại mật khẩu đã được gửi!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}
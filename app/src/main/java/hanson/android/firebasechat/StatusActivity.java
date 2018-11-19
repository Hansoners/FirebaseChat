package hanson.android.firebasechat;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StatusActivity extends AppCompatActivity {

    @BindView(R.id.status_appbar)
    Toolbar mStatusBar;
    @BindView(R.id.status_save_btn)
    Button mSaveBtn;
    @BindView(R.id.status_description)
    TextInputLayout mStatusDescription;

    private DatabaseReference myRef;
    String current_status;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        ButterKnife.bind(this);
        current_status = getIntent().getStringExtra("status");
        FirebaseUser mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mCurrentUser != null;
        String cur_uid = mCurrentUser.getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(cur_uid);
        setSupportActionBar(mStatusBar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Change Status");
        Objects.requireNonNull(mStatusDescription.getEditText()).setText(current_status);

        mSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog = new ProgressDialog(StatusActivity.this);
                mProgressDialog.setTitle("Saving.");
                mProgressDialog.show();
                String status = Objects.requireNonNull(mStatusDescription.getEditText()).getEditableText().toString().trim();
                myRef.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mProgressDialog.dismiss();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error. Changes not saved.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}

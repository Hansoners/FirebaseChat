package hanson.android.firebasechat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.UCrop.Options;

import java.io.File;
import java.security.SecureRandom;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.profile_name)
    TextView mName;
    @BindView(R.id.profile_description)
    TextView mStatus;
    @BindView(R.id.profile_image)
    CircleImageView mProfileImage;
    @BindView(R.id.profile_status_btn)
    Button mStatusBtn;
    @BindView(R.id.profile_image_btn)
    Button mImageBtn;

    FirebaseUser mCurrentUser;
    private static final int GALLERY_PIC = 1;
    private StorageReference mImageStorage;
    private DatabaseReference myRef;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        mImageStorage = FirebaseStorage.getInstance().getReference();


        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        assert mCurrentUser != null;
        String current_uid = mCurrentUser.getUid();
        myRef = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumbnail = dataSnapshot.child("thumbnail").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                Picasso.get()
                        .load(image)
                        .into(mProfileImage);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String current_status = mStatus.getText().toString();
                Intent status_intent = new Intent(SettingsActivity.this, StatusActivity.class);
                status_intent.putExtra("status", current_status);
                startActivity(status_intent);
            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_intent = new Intent();
                gallery_intent.setType("image/*");
                gallery_intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery_intent, "Select Image"), GALLERY_PIC);

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == GALLERY_PIC) {

            //Progress bar
            mProgressDialog = new ProgressDialog(SettingsActivity.this);
            mProgressDialog.setTitle("Uploading Image");
            mProgressDialog.setCanceledOnTouchOutside(false);
            mProgressDialog.show();

            Uri imageUri = data.getData();

            assert imageUri != null;
            String destinationFileName = "Cropped Image";
            Uri destinationUri = Uri.fromFile(new File(getCacheDir(), destinationFileName));


            UCrop.Options options = new UCrop.Options();
            options.setToolbarColor(this.getResources().getColor(R.color.colorPrimary));
            options.setActiveWidgetColor(ContextCompat.getColor(this, R.color.colorAccent));
            options.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
            options.setRootViewBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryLight));

            UCrop.of(imageUri, destinationUri)
                    .withAspectRatio(1, 1)
                    .withOptions(options)
                    .withMaxResultSize(1000, 1000)
                    .start(this);

            final String cur_id = mCurrentUser.getUid();

            final StorageReference filePath = mImageStorage.child("profile_images").child(cur_id + ".jpg");
            filePath.putFile(destinationUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()) {

                        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                myRef.child("image").setValue(uri.toString());
                                mProgressDialog.dismiss();
                            }
                        });



                    } else {
                        Toast.makeText(SettingsActivity.this, "Error uploading image", Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    }
                }
            });


        } else if (resultCode == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(data);
        }
    }


}

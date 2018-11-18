package hanson.android.firebasechat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    @BindView(R.id.main_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.tab_pager)
    ViewPager mTabPager;
    @BindView(R.id.main_tabs)
    TabLayout mTabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("FlatChat");

        SectionsAdapter mSectionsAdapter = new SectionsAdapter(getSupportFragmentManager());
        mTabPager.setAdapter(mSectionsAdapter);
        mTabLayout.setupWithViewPager(mTabPager);


    }

    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            toStart();
        }
    }

    private void toStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case R.id.main_logout:
                FirebaseAuth.getInstance().signOut();
                toStart();
                break;
        }

        return true;
    }
}



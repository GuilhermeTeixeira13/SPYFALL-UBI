package pt.ubi.di.pmd.spyfall_ubi;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView menuIcon = findViewById(R.id.menu_icon);
        ImageView shareIcon = findViewById(R.id.share_icon);
        TextView title = findViewById(R.id.landing_tootlbar_title);
        ImageView spyImage = findViewById(R.id.spyImage);

        Toolbar sideMenu = findViewById(R.id.side_menu);
        setSupportActionBar(sideMenu);

        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, sideMenu,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }
}
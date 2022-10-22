package pt.ubi.di.pmd.spyfall_ubi;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView menuIcon = findViewById(R.id.menu_icon);
        ImageView shareIcon = findViewById(R.id.share_icon);
        TextView title = findViewById(R.id.landing_tootlbar_title);
        ImageView spyImage = findViewById(R.id.spyImage);

    }
}
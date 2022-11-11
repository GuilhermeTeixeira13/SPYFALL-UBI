package pt.ubi.di.pmd.spyfall_ubi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;

public class NonSpiesWinActivity extends AppCompatActivity {
    ArrayList<Player> players;
    Place place;
    TextView TxtViewLocationName;
    TextView TxtViewSpies;
    ImageView ImgLocation;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nonspieswin);

        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        if(checkFlag.equals("FROM_REVEALSPY")){
            players = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS_COMPLETED");
            place = (Place) getIntent().getSerializableExtra("PLACE");
        }

        TxtViewLocationName = (TextView) findViewById(R.id.locationName);
        TxtViewLocationName.setText(place.getName());

        TxtViewSpies= (TextView) findViewById(R.id.spies);
        String spies = getSpies(players);
        if(spies.contains("/"))
            TxtViewSpies.setText("Spies: "+spies);
        else
            TxtViewSpies.setText("Spy: "+spies);

        ImgLocation = (ImageView) findViewById(R.id.imageViewLocation);
        Uri path = Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID +  "/drawable/"+place.getImagePath());
        ImgLocation.setImageURI(path);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    public String getSpies(ArrayList<Player> players){
        String spies = "";

        for(int i=0; i<players.size(); i++){
            if(players.get(i).getRole() == 1)
                spies += players.get(i).getName()+"/";
        }

        return spies.substring(0, spies.length() - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_rest, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareButton:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Link to Playstore";
                String shareSubject = "Spyfall @ UBI!";
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(Intent.createChooser(sharingIntent, "Share using:"));
                break;
            case R.id.homeButton:
                // Ask if we want to the lobby and lose all the current page settings

                new AlertDialog.Builder(NonSpiesWinActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you going to the main page?")
                        .setMessage("Do you want to lose the current game state and go back to the main page?")
                        .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                goToMainActivity();
                            }
                        })
                        .setNegativeButton("No!", null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToMainActivity () {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }

    public void showLocationInfo (View v) {
        new AlertDialog.Builder(NonSpiesWinActivity.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(place.getName())
                .setMessage(place.getInfo())
                .setPositiveButton("Got it!", null)
                .show();
    }

    public void goToLobby (View v) {
        Intent goToLobbyIntent = new Intent(this, LobbyActivity.class);
        goToLobbyIntent.putExtra("flag","FROM_NONSPYWIN");
        goToLobbyIntent.putExtra("PLAYERS", players);
        startActivity(goToLobbyIntent);
    }
}

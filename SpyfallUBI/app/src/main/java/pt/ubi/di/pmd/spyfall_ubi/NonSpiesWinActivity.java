package pt.ubi.di.pmd.spyfall_ubi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Change toolbar title
        setTitle(getResources().getString(R.string.NONSPIESWinActivity));

        // Getting the flag from the intent that he came from
        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        // Check flag and initialize objects
        if(checkFlag.equals("FROM_REVEALSPY")){
            players = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS_COMPLETED");
            place = (Place) getIntent().getSerializableExtra("PLACE");
        }

        TxtViewLocationName = (TextView) findViewById(R.id.locationName);
        TxtViewLocationName.setText(place.getName());

        TxtViewSpies= (TextView) findViewById(R.id.spies);
        String spies = getSpies(players);
        if(spies.contains("/"))
            TxtViewSpies.setText(getResources().getString(R.string.spies) + spies);
        else
            TxtViewSpies.setText(getResources().getString(R.string.spy) + spies);

        ImgLocation = (ImageView) findViewById(R.id.imageViewLocation);
        Uri path = Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID +  "/drawable/"+place.getImagePath());
        ImgLocation.setImageURI(path);
    }

    // Inflating the toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_rest, menu);
        return true;
    }

    // Toolbar button clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.shareButton:
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getResources().getString(R.string.Share1);
                String shareSubject = "Spyfall @ UBI!";
                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                sharingIntent.putExtra(Intent.EXTRA_SUBJECT, shareSubject);
                startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.Share2)));
                break;
            case R.id.homeButton:
                new AlertDialog.Builder(NonSpiesWinActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getResources().getString(R.string.GoHome1))
                        .setMessage(getResources().getString(R.string.GoHome2))
                        .setPositiveButton(getResources().getString(R.string.YES), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                goToMainActivity();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.NO), null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Go to MainActivity by clicking in a button
    public void goToMainActivity () {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }

    // It returns a string with the name of the spies in the game
    public String getSpies(ArrayList<Player> players){
        String spies = "";

        for(int i=0; i<players.size(); i++){
            if(players.get(i).getRole() == 1)
                spies += players.get(i).getName()+"/";
        }

        return spies.substring(0, spies.length() - 1);
    }

    // Show dialog box with additional info about the place where civilians are
    public void showLocationInfo (View v) {
        new AlertDialog.Builder(NonSpiesWinActivity.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(place.getName())
                .setMessage(place.getInfo())
                .setPositiveButton(getResources().getString(R.string.GotIt), null)
                .show();
    }

    // Go to Lobby by clicking in a button
    public void goToLobby (View v) {
        Intent goToLobbyIntent = new Intent(this, LobbyActivity.class);
        goToLobbyIntent.putExtra("flag","FROM_NONSPYWIN");
        goToLobbyIntent.putExtra("PLAYERS", players);
        startActivity(goToLobbyIntent);
    }
}

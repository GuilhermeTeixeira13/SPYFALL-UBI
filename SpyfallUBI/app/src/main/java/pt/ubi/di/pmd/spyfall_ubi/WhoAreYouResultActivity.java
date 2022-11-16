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

public class WhoAreYouResultActivity extends AppCompatActivity {
    ArrayList<Player> players;
    Place place;
    Integer playerVisualizing = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_whoareyouresult);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Change toolbar title
        setTitle(getResources().getString(R.string.WhoAreYouActivity));

        // Getting the flag from the intent that he came from
        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        // Check flag and initialize objects
        if(checkFlag.equals("FROM_WHOAREYOU")){
            players = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
            place = (Place) getIntent().getSerializableExtra("PLACE");
            playerVisualizing = (Integer) intent.getIntExtra("PLAYER_VISUALIZING", 0);
        }

        TextView TxtViewPlayerName = (TextView) findViewById(R.id.textViewPlayerName);
        TextView TxtViewLocationName = (TextView) findViewById(R.id.locationName);
        TextView TxtViewHelp = (TextView) findViewById(R.id.help);
        TextView TxtViewLocationSpyCard = (TextView) findViewById(R.id.textViewLocationSpyCard);
        ImageView ImageViewCard = (ImageView ) findViewById(R.id.imageViewLocation);
        Button BtnInfo = (Button) findViewById(R.id.btn_know_more);
        View View2 = (View) findViewById(R.id.view2);

        TxtViewPlayerName.setText(players.get(playerVisualizing).getName());

        // If the player that is visualizing is a non-spie
        if(players.get(playerVisualizing).getRole() == 0){
            TxtViewLocationSpyCard.setText(getResources().getString(R.string.WhoAreYouResAct2));
            TxtViewLocationName.setText(place.getName());
            TxtViewHelp.setText(getResources().getString(R.string.WhoAreYouResAct5));

            // Set image of the place
            Uri path = Uri.parse("android.resource://" + BuildConfig.APPLICATION_ID +  "/drawable/"+place.getImagePath());
            ImageViewCard.setImageURI(path);

            if(place.getCategory().equals("UBI")){
                // Add extra space to the view so it can fit the additional info about UBI
                View2.getLayoutParams().height = (int) getResources().getDimension(R.dimen.view2_height);
            }else{
                // If the place is not in UBI/Covilhã, then it will not have the "more info" button
                BtnInfo.setVisibility(View.GONE);
            }
        } else {
            // If the player that is visualizing is a spy

            TxtViewLocationSpyCard.setText(getResources().getString(R.string.WhoAreYouResAct3));
            TxtViewLocationName.setText(getResources().getString(R.string.WhoAreYouResAct4));
            TxtViewLocationName.setTextSize(27);
            TxtViewHelp.setText(getResources().getString(R.string.WhoAreYouResAct6));

            // Remove the "more info" button and set the ImageViewCard with the spy image
            BtnInfo.setVisibility(View.GONE);
            ImageViewCard.setImageResource(R.drawable.spy2);
        }
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
                new AlertDialog.Builder(WhoAreYouResultActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle(getResources().getString(R.string.GoHome1))
                        .setMessage(getResources().getString(R.string.GoHome2))
                        .setPositiveButton(getResources().getString(R.string.YES), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.NO), null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // It only goes to Ready activity when all the playersActive have seen what their roles are
    public void whoAreYou (View v){
        if(playerVisualizing < players.size() - 1){
            // Go to WhoAreYouActivity
            Intent goToWhoAreYouIntent = new Intent(this, WhoAreYouActivity.class);
            goToWhoAreYouIntent.putExtra("flag","FROM_WHOAREYOURESULT");
            goToWhoAreYouIntent.putExtra("PLAYERS", players);
            goToWhoAreYouIntent.putExtra("PLACE", place);
            goToWhoAreYouIntent.putExtra("PLAYER_VISUALIZING", playerVisualizing + 1);
            startActivity(goToWhoAreYouIntent);
        }else{
            // Go to ReadyActivity
            Intent goToReadyIntent = new Intent(this, ReadyActivity.class);
            goToReadyIntent.putExtra("flag","FROM_WHOAREYOURESULT");
            goToReadyIntent.putExtra("PLAYERS", players);
            goToReadyIntent.putExtra("PLACE", place);
            startActivity(goToReadyIntent);
        }
    }

    // Go to MainActivity by clicking in a button
    public void goToMainActivity () {
        Intent goToMainActivityIntent = new Intent(this, MainActivity.class);
        startActivity(goToMainActivityIntent);
    }

    // Show adittional info about UBI places by clicking on a button
    public void showLocationInfo (View v) {
        new AlertDialog.Builder(WhoAreYouResultActivity.this)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setTitle(place.getName())
                .setMessage(place.getInfo())
                .setPositiveButton(getResources().getString(R.string.GotIt), null)
                .show();
    }
}

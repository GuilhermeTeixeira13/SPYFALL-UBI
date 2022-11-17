package pt.ubi.di.pmd.spyfall_ubi;

import static pt.ubi.di.pmd.spyfall_ubi.RevealSpyActivity.getKickedPlayers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class RevealLocationActivity extends AppCompatActivity {
    ArrayList<Player> playersActive;
    ArrayList<Player> playersKicked;
    ArrayList<Player> playersCompleted;
    Place place;
    ArrayList<Place> allPlaces;
    int playerVoting;
    long timer;
    String timerStr;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveallocation);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Change toolbar title
        setTitle(getResources().getString(R.string.RevealLocationActivity));

        // Getting the flag from the intent that he came from
        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        // Check flag and initialize objects
        if(checkFlag.equals("FROM_GAMEON")){
            playersActive = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
            place = (Place) getIntent().getSerializableExtra("PLACE");
            playerVoting = (int) getIntent().getSerializableExtra("PLAYER_VOTING");
            timer = (long) getIntent().getSerializableExtra("TIMER_LONG");
            timerStr = (String) getIntent().getSerializableExtra("TIMER_STRING");
            playersCompleted = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS_COMPLETED");
            playersKicked = getKickedPlayers(playersCompleted, playersActive);
        }

        // Getting all the places available considering the "game mode" -> UBI or OTHER
        try {
            allPlaces = getPlaces(place.getCategory().equals("UBI") ? true : false);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize ListView with all the places
        ListView lv = (ListView) findViewById(R.id.listviewlocations);
        lv.setAdapter(new MyListAdaper(this, R.layout.list_locations, allPlaces));
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
                new AlertDialog.Builder(RevealLocationActivity.this)
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

    // List adapter class
    private class MyListAdaper extends ArrayAdapter<Place> {
        private int layout;
        private ArrayList<Place> mObjects;
        private MyListAdaper(Context context, int resource, ArrayList<Place> objects) {
            super(context, resource, objects);
            mObjects = objects;
            layout = resource;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Initialize List elements

            ViewHolder mainViewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.location = (TextView) convertView.findViewById(R.id.list_location);
                viewHolder.btnVote = (Button) convertView.findViewById(R.id.list_btn_vote);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            ViewHolder finalMainViewholder = mainViewholder;

            // Listener for clicks in some of the List elements
            mainViewholder.btnVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(allPlaces.get(position).equals(place)){
                        // If the item clicked corresponds to the current place -> Spies win
                        playersCompleted.get(playerVoting).setPoints(playersCompleted.get(playerVoting).getPoints() + 4);
                        goToSpiesWin(getWindow().getDecorView());
                    } else {
                        int numpSpies = getNumberOfSpies(playersActive);

                        if(numpSpies > 1) {
                            // Bad prediction -> The spy who vote get kicked and the game proceeds
                            Player SpyToEliminate = playersActive.get(playerVoting);
                            playersActive.remove(SpyToEliminate);
                            goToSpyEliminated(getWindow().getDecorView(), SpyToEliminate);
                        } else {
                            // Bad prediction -> Non-spies win because the last spy alive was eliminated
                            points(playersCompleted, playersKicked, 0, 1);
                            goToNonSpiesWin(getWindow().getDecorView());
                        }
                    }
                }
            });

            mainViewholder.location.setText(allPlaces.get(position).getName());

            return convertView;
        }
    }

    // Each item of the list has 2 attributes: location and a button that allow the spy to vote
    public class ViewHolder {
        TextView location;
        Button btnVote;
    }

    // Set playersActive scores by role, considering the spies that were kicked, so they don't receive points
    public static void points(ArrayList<Player> players_completed, ArrayList<Player> players_kicked, Integer role, Integer points){
        for(int i=0; i<players_completed.size(); i++) {
            if (players_completed.get(i).getRole().equals(role) && !players_kicked.contains(players_completed.get(i)))
                players_completed.get(i).setPoints(players_completed.get(i).getPoints() + points);
        }
    }


    // Return the number of spies in the list of players
    public int getNumberOfSpies(ArrayList<Player> players){
        int spieCount = 0;
        for(int i=0; i< players.size(); i++){
            if(players.get(i).getRole() == 1)
                spieCount++;
        }

        return spieCount;
    }

    // Read places that have a certain category ("UBI" or "OTHER") to an ArrayList of places
    public ArrayList<Place> readPlaces(String filepath, String category) throws IOException {
        BufferedReader reader = null;
        ArrayList<Place> places = new ArrayList<Place>();
        String[] parts;

        try {
            reader = new BufferedReader(new InputStreamReader(getAssets().open(filepath)));
            String line = reader.readLine();

            while(line != null){
                parts = line.split("/");

                String name = parts[0];
                String imagePath = parts[1];
                String info = parts[2];
                String cat = parts[3];

                if (cat.equals(category)) {
                    Place newPlace = new Place(name, imagePath, info, cat);
                    places.add(newPlace);
                }

                line = reader.readLine();
            }
        } catch (IOException e) {
            System.out.println("File not found.");
        }

        return places;
    }

    // If UBIPlaces is true, read UBI places from file and put them in a list
    // If UBIPlaces is false, read OTHER places from file and put them in a list
    public ArrayList<Place> getPlaces(Boolean UBIPlaces) throws IOException {
        ArrayList<Place> places;

        String langPref = "Language";
        SharedPreferences prefs = getSharedPreferences("CommonPrefs", MODE_PRIVATE);
        String language = prefs.getString(langPref, "");


        if(language.equals("pt")) {
            if (UBIPlaces)
                places = readPlaces("placesPT.txt" , "UBI");
            else
                places = readPlaces("placesPT.txt" , "OTHER");
        }
        else {
            if (UBIPlaces)
                places = readPlaces("places.txt" , "UBI");
            else
                places = readPlaces("places.txt" , "OTHER");
        }

        return places;
    }

    // Go to GameONActivity
    public void goToGame(View v){
        Intent goToGameONIntent = new Intent(this, GameONActivity.class);
        goToGameONIntent.putExtra("flag","FROM_REVEALSPY");
        goToGameONIntent.putExtra("PLAYERS", playersActive);
        goToGameONIntent.putExtra("PLACE", place);
        goToGameONIntent.putExtra("PLAYER_PLAYING", getRandomNumber(0, playersActive.size()));
        goToGameONIntent.putExtra("TIMER", timer);
        goToGameONIntent.putExtra("PLAYERS_COMPLETED", playersCompleted);
        startActivity(goToGameONIntent);
    }

    // Go to NonSpiesWinActivity
    public void goToNonSpiesWin(View v){
        Intent goToNonSpiesWinIntent = new Intent(this, NonSpiesWinActivity.class);
        goToNonSpiesWinIntent.putExtra("flag","FROM_REVEALSPY");
        goToNonSpiesWinIntent.putExtra("PLAYERS_COMPLETED", playersCompleted);
        goToNonSpiesWinIntent.putExtra("PLACE", place);
        startActivity(goToNonSpiesWinIntent);
    }

    // Go to SpiesWinActivity
    public void goToSpiesWin(View v){
        Intent goToSpiesWinIntent = new Intent(this, SpiesWinActivity.class);
        goToSpiesWinIntent.putExtra("flag","FROM_REVEALSPY");
        goToSpiesWinIntent.putExtra("PLAYERS_COMPLETED", playersCompleted);
        goToSpiesWinIntent.putExtra("PLACE", place);
        startActivity(goToSpiesWinIntent);
    }

    // Go to SpyEliminatedActivity
    public void goToSpyEliminated(View v, Player playerElimiando){
        Intent goToSpyEliminatedIntent = new Intent(this, SpyEliminatedActivity.class);
        goToSpyEliminatedIntent.putExtra("flag","FROM_REVEALSPY");
        goToSpyEliminatedIntent.putExtra("PLAYERS", playersActive);
        goToSpyEliminatedIntent.putExtra("PLACE", place);
        goToSpyEliminatedIntent.putExtra("PLAYER_PLAYING", getRandomNumber(0, playersActive.size()));
        goToSpyEliminatedIntent.putExtra("TIMER", timer);
        goToSpyEliminatedIntent.putExtra("PLAYER_ELIMINATED", playerElimiando);
        goToSpyEliminatedIntent.putExtra("PLAYERS_COMPLETED", playersCompleted);
        startActivity(goToSpyEliminatedIntent);
    }

    // Go to LobbyActivity
    public void goToLobby (View v) {
        Intent goToLobbyIntent = new Intent(this, LobbyActivity.class);
        goToLobbyIntent.putExtra("flag","FROM_REVEALLOCATION");
        goToLobbyIntent.putExtra("PLAYERS", playersCompleted);
        startActivity(goToLobbyIntent);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}

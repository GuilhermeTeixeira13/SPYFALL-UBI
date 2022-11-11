package pt.ubi.di.pmd.spyfall_ubi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;


public class RevealLocationActivity extends AppCompatActivity {
    ArrayList<Player> players;
    ArrayList<Player> players_kicked;
    ArrayList<Player> players_completed;
    Place place;
    ArrayList<Place> allPlaces;
    int player_voting;
    long timer;
    String timerStr;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reveallocation);

        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        if(checkFlag.equals("FROM_GAMEON")){
            players = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
            place = (Place) getIntent().getSerializableExtra("PLACE");
            player_voting = (int) getIntent().getSerializableExtra("PLAYER_VOTING");
            timer = (long) getIntent().getSerializableExtra("TIMER_LONG");
            timerStr = (String) getIntent().getSerializableExtra("TIMER_STRING");
            players_completed = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS_COMPLETED");
            players_kicked = (ArrayList<Player>) players_completed.clone();
            players_kicked.removeAll(players);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        try {
            allPlaces = getPlaces(place.getCategory().equals("UBI") ? true : false);
            System.out.println(allPlaces);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ListView lv = (ListView) findViewById(R.id.listviewlocations);
        lv.setAdapter(new MyListAdaper(this, R.layout.list_locations, allPlaces));
    }


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
            mainViewholder.btnVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(allPlaces.get(position).equals(place)){
                        // Spies win
                        players_completed.get(player_voting).setPoints(players_completed.get(player_voting).getPoints() + 4);

                        goToSpiesWin(getWindow().getDecorView());
                    } else {
                        int numpSpies = getNumberOfSpies(players);

                        if(numpSpies > 1) {
                            // A spy got kicked and the game proceeds
                            Player SpyToEliminate = players.get(player_voting);
                            players.remove(SpyToEliminate);
                            goToSpyEliminated(getWindow().getDecorView(), SpyToEliminate);
                        } else {
                            // Non-spies win
                            points(players_completed, players_kicked, 0, 1);
                            goToNonSpiesWin(getWindow().getDecorView());
                        }
                    }
                }
            });

            mainViewholder.location.setText(allPlaces.get(position).getName());

            return convertView;
        }
    }

    public static void points(ArrayList<Player> players_completed, ArrayList<Player> players_kicked, Integer role, Integer points){
        for(int i=0; i<players_completed.size(); i++) {
            if (players_completed.get(i).getRole().equals(role) && !players_kicked.contains(players_completed.get(i))) {
                players_completed.get(i).setPoints(players_completed.get(i).getPoints() + points);
            }
        }
    }

    public class ViewHolder {
        TextView location;
        Button btnVote;
    }

    public int getNumberOfSpies(ArrayList<Player> players){
        int spieCount = 0;
        for(int i=0; i< players.size(); i++){
            if(players.get(i).getRole() == 1)
                spieCount++;
        }

        return spieCount;
    }

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

    public ArrayList<Place> getPlaces(Boolean UBIPlaces) throws IOException {
        ArrayList<Place> places;

        if (UBIPlaces) {
            places = readPlaces("places.txt" , "UBI");
        }
        else{
            places = readPlaces("places.txt" , "OTHER");
        }

        return places;
    }

    public void goToGame(View v){
        Intent goToGameONIntent = new Intent(this, GameONActivity.class);
        goToGameONIntent.putExtra("flag","FROM_REVEALSPY");
        goToGameONIntent.putExtra("PLAYERS", players);
        goToGameONIntent.putExtra("PLACE", place);
        goToGameONIntent.putExtra("PLAYER_PLAYING", getRandomNumber(0, players.size()));
        goToGameONIntent.putExtra("TIMER", timer);
        goToGameONIntent.putExtra("PLAYERS_COMPLETED", players_completed);
        startActivity(goToGameONIntent);
    }

    public void goToNonSpiesWin(View v){
        Intent goToNonSpiesWinIntent = new Intent(this, NonSpiesWinActivity.class);
        goToNonSpiesWinIntent.putExtra("flag","FROM_REVEALSPY");
        goToNonSpiesWinIntent.putExtra("PLAYERS_COMPLETED", players_completed);
        goToNonSpiesWinIntent.putExtra("PLACE", place);
        startActivity(goToNonSpiesWinIntent);
    }

    public void goToSpiesWin(View v){
        Intent goToSpiesWinIntent = new Intent(this, SpiesWinActivity.class);
        goToSpiesWinIntent.putExtra("flag","FROM_REVEALSPY");
        System.out.println(players_completed);
        System.out.println(players_kicked);
        goToSpiesWinIntent.putExtra("PLAYERS_COMPLETED", players_completed);
        goToSpiesWinIntent.putExtra("PLACE", place);
        startActivity(goToSpiesWinIntent);
    }

    public void goToSpyEliminated(View v, Player playerElimiando){
        Intent goToSpyEliminatedIntent = new Intent(this, SpyEliminatedActivity.class);
        goToSpyEliminatedIntent.putExtra("flag","FROM_REVEALSPY");
        goToSpyEliminatedIntent.putExtra("PLAYERS", players);
        goToSpyEliminatedIntent.putExtra("PLACE", place);
        goToSpyEliminatedIntent.putExtra("PLAYER_PLAYING", getRandomNumber(0, players.size()));
        goToSpyEliminatedIntent.putExtra("TIMER", timer);
        goToSpyEliminatedIntent.putExtra("PLAYER_ELIMINATED", playerElimiando);
        goToSpyEliminatedIntent.putExtra("PLAYERS_COMPLETED", players_completed);
        startActivity(goToSpyEliminatedIntent);
    }

    public void goToLobby (View v) {
        Intent goToLobbyIntent = new Intent(this, LobbyActivity.class);
        goToLobbyIntent.putExtra("flag","FROM_REVEALLOCATION");
        goToLobbyIntent.putExtra("PLAYERS", players_completed);
        startActivity(goToLobbyIntent);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
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

                new AlertDialog.Builder(RevealLocationActivity.this)
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
}

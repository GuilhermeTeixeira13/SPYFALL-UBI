package pt.ubi.di.pmd.spyfall_ubi;

import static pt.ubi.di.pmd.spyfall_ubi.RevealLocationActivity.points;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


public class RevealSpyActivity extends AppCompatActivity {
    ArrayList<Player> players;
    ArrayList<Player> players_kicked;
    ArrayList<Player> players_completed;
    Place place;
    int player_voting;
    int player_starting;
    TextView txtViewPlayerVoting;
    int numVoto = 0;
    ArrayList<Integer> votos;
    long timer;
    String timerStr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revealspy);

        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        if(checkFlag.equals("FROM_GAMEON")){
            players = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
            place = (Place) getIntent().getSerializableExtra("PLACE");
            player_voting = (int) getIntent().getSerializableExtra("PLAYER_VOTING");
            player_starting = player_voting;
            timer = (long) getIntent().getSerializableExtra("TIMER_LONG");
            timerStr = (String) getIntent().getSerializableExtra("TIMER_STRING");
            players_completed = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS_COMPLETED");
            players_kicked = (ArrayList<Player>) players_completed.clone();
            players_kicked.removeAll(players);
        }

        txtViewPlayerVoting = (TextView ) findViewById(R.id.player_name);
        txtViewPlayerVoting.setText(players.get(player_voting).getName());

        votos = inicializeVotos(0, players.size());

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(new MyListAdaper(this, R.layout.list_players, players));
    }

    public ArrayList<Integer> inicializeVotos(int num, int size){
        ArrayList<Integer> votos = new ArrayList<>();

        for(int i=0; i<size; i++){
            votos.add(i, num);
        }
        return votos;
    }

    private class MyListAdaper extends ArrayAdapter<Player> {
        private int layout;
        private ArrayList<Player> mObjects;
        private MyListAdaper(Context context, int resource, ArrayList<Player> objects) {
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
                viewHolder.nVotes = (TextView) convertView.findViewById(R.id.list_number_votes);
                viewHolder.playerName = (TextView) convertView.findViewById(R.id.list_player_name);
                viewHolder.btnVote = (Button) convertView.findViewById(R.id.list_btn_vote);
                convertView.setTag(viewHolder);
            }
            mainViewholder = (ViewHolder) convertView.getTag();
            ViewHolder finalMainViewholder = mainViewholder;
            mainViewholder.btnVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    votos.set(position, votos.get(position) + 1);

                    player_voting += 1;
                    if(player_voting >= players.size())
                        player_voting = 0;

                    numVoto += 1;

                    if (numVoto < players.size()){
                        // Each player vote
                        txtViewPlayerVoting.setText(players.get(player_voting).getName());
                        finalMainViewholder.nVotes.setText("["+votos.get(position)+"]");
                    } else {
                        // Vote ended
                        int maxVotos = Collections.max(votos);
                        int maxVotosReps = Collections.frequency(votos, maxVotos);
                        boolean draw = maxVotosReps > 1;

                        if (draw) {
                            // If we are in the case where the time ended then reset votes and vote again
                            // Else Keep playing

                            if(timerStr.equals("finish")) {
                                Toast.makeText(RevealSpyActivity.this, "Draw on the votes! Vote again!",
                                        Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(getIntent());
                            } else {
                                Toast.makeText(RevealSpyActivity.this, "Draw on the votes! Keep playing!",
                                        Toast.LENGTH_LONG).show();
                                goToGame(getWindow().getDecorView());
                            }
                        } else {
                            // Check what card corresponds to the player with more votes

                            // If it is a Spy Card
                            //      If there is more then 1 spy, then that spy is eliminated and the game proceed
                            //      Else Non spies win
                            // Else the spies wins

                            Player playerMaisVotado = players.get(votos.indexOf(maxVotos));

                            if(playerMaisVotado.getRole() == 1){
                                int numSpies = getNumberOfSpies(players);
                                if (numSpies > 1) {
                                    // A spy got kicked and the game proceeds
                                    players.remove(playerMaisVotado);
                                    players_completed.get(player_starting).setPoints(players_completed.get(player_starting).getPoints() + 1);

                                    goToSpyEliminated(getWindow().getDecorView(), playerMaisVotado);
                                } else {
                                    // Non spies win
                                    players_completed.get(player_starting).setPoints(players_completed.get(player_starting).getPoints() + 1);
                                    points(players_completed, players_kicked, 0, 1);

                                    goToNonSpiesWin(getWindow().getDecorView());
                                }
                            }else {
                                // Spies wins
                                points(players_completed, players_kicked, 1, 4);
                                goToSpiesWin(getWindow().getDecorView());
                            }
                        }
                    }
                }
            });

            mainViewholder.nVotes.setText("[0]");
            mainViewholder.playerName.setText(players.get(position).getName());

            return convertView;
        }
    }

    public int getNumberOfSpies(ArrayList<Player> players){
        int spieCount = 0;
        for(int i=0; i< players.size(); i++){
            if(players.get(i).getRole() == 1)
                spieCount++;
        }

        return spieCount;
    }

    public class ViewHolder {
        TextView nVotes;
        TextView playerName;
        Button btnVote;
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
        goToSpiesWinIntent.putExtra("PLAYERS_COMPLETED", players_completed);
        goToSpiesWinIntent.putExtra("PLACE", place);
        startActivity(goToSpiesWinIntent);
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

                new AlertDialog.Builder(RevealSpyActivity.this)
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

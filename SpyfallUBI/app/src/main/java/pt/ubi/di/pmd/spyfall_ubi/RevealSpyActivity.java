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
    ArrayList<Player> playersActive;
    ArrayList<Player> playersKicked;
    ArrayList<Player> playersCompleted;
    ArrayList<Integer> votes;
    TextView TxtViewPlayerVoting;
    Place place;
    int playerVoting;
    int playerStarting;
    int numVote = 0;
    long timer;
    String timerStr;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revealspy);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Change toolbar title
        setTitle(getResources().getString(R.string.RevealSPYActivity));

        // Getting the flag from the intent that he came from
        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        // Check flag and initialize objects
        if(checkFlag.equals("FROM_GAMEON")){
            playersActive = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
            place = (Place) getIntent().getSerializableExtra("PLACE");
            playerVoting = (int) getIntent().getSerializableExtra("PLAYER_VOTING");
            playerStarting = playerVoting;
            timer = (long) getIntent().getSerializableExtra("TIMER_LONG");
            timerStr = (String) getIntent().getSerializableExtra("TIMER_STRING");
            playersCompleted = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS_COMPLETED");
            playersKicked = (ArrayList<Player>) playersCompleted.clone();
            playersKicked.removeAll(playersActive);
        }

        TxtViewPlayerVoting = (TextView ) findViewById(R.id.player_name);
        TxtViewPlayerVoting.setText(playersActive.get(playerVoting).getName());

        votes = initializeVotes(0, playersActive.size());

        // Initialize ListView with all the places
        ListView lv = (ListView) findViewById(R.id.listview);
        lv.setAdapter(new MyListAdaper(this, R.layout.list_players, playersActive));
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
                new AlertDialog.Builder(RevealSpyActivity.this)
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

    // Initializes the ArrayList votes with 0
    public ArrayList<Integer> initializeVotes(int num, int size){
        ArrayList<Integer> votes = new ArrayList<>();

        for(int i=0; i<size; i++){
            votes.add(i, num);
        }
        return votes;
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
            // Initialize List elements
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

            // Listener for clicks in some of the List elements
            mainViewholder.btnVote.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // After a vote, a new player is asked to vote until all the players voted
                    votes.set(position, votes.get(position) + 1);

                    playerVoting += 1;
                    if(playerVoting >= playersActive.size())
                        playerVoting = 0;

                    numVote += 1;

                    if (numVote < playersActive.size()){
                        // Players are voting
                        TxtViewPlayerVoting.setText(playersActive.get(playerVoting).getName());
                        finalMainViewholder.nVotes.setText("["+ votes.get(position)+"]");
                    } else {
                        // Vote ended

                        // Check if there is a draw
                        int maxVotos = Collections.max(votes);
                        int maxVotosReps = Collections.frequency(votes, maxVotos);
                        boolean draw = maxVotosReps > 1;

                        if (draw) {
                            if(timerStr.equals("finish")) {
                                // We are in the case where the time ended then reset votes and vote again

                                Toast.makeText(RevealSpyActivity.this, "Draw on the votes! Vote again!",
                                        Toast.LENGTH_LONG).show();
                                finish();
                                startActivity(getIntent());
                            } else {
                                // Keep playing

                                Toast.makeText(RevealSpyActivity.this, "Draw on the votes! Keep playing!",
                                        Toast.LENGTH_LONG).show();
                                goToGame(getWindow().getDecorView());
                            }
                        } else {
                            // Check what card corresponds to the player with more votes

                            // If it is a Spy Card
                            //    If there is more then 1 spy, then that spy is eliminated and the game proceed
                            //    Else Non spies win
                            // Else the spies wins

                            Player playerMaisVotado = playersActive.get(votes.indexOf(maxVotos));

                            if(playerMaisVotado.getRole() == 1){
                                int numSpies = getNumberOfSpies(playersActive);
                                if (numSpies > 1) {
                                    // A spy got kicked and the game proceeds
                                    playersActive.remove(playerMaisVotado);
                                    playersCompleted.get(playerStarting).setPoints(playersCompleted.get(playerStarting).getPoints() + 1);

                                    goToSpyEliminated(getWindow().getDecorView(), playerMaisVotado);
                                } else {
                                    // Non spies win
                                    playersCompleted.get(playerStarting).setPoints(playersCompleted.get(playerStarting).getPoints() + 1);
                                    points(playersCompleted, playersKicked, 0, 1);

                                    goToNonSpiesWin(getWindow().getDecorView());
                                }
                            }else {
                                // Spies wins
                                points(playersCompleted, playersKicked, 1, 4);
                                goToSpiesWin(getWindow().getDecorView());
                            }
                        }
                    }
                }
            });

            mainViewholder.nVotes.setText("[0]");
            mainViewholder.playerName.setText(playersActive.get(position).getName());

            return convertView;
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

    // Each item of the list has 3 attributes: number of votes, player's name and a button that allow the player to vote
    public class ViewHolder {
        TextView nVotes;
        TextView playerName;
        Button btnVote;
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

    // Go to NonSpiesWinActivity
    public void goToNonSpiesWin(View v){
        Intent goToNonSpiesWinIntent = new Intent(this, NonSpiesWinActivity.class);
        goToNonSpiesWinIntent.putExtra("flag","FROM_REVEALSPY");
        goToNonSpiesWinIntent.putExtra("PLAYERS_COMPLETED", playersCompleted);
        goToNonSpiesWinIntent.putExtra("PLACE", place);
        startActivity(goToNonSpiesWinIntent);
    }

    // Go to goToSpiesWin
    public void goToSpiesWin(View v){
        Intent goToSpiesWinIntent = new Intent(this, SpiesWinActivity.class);
        goToSpiesWinIntent.putExtra("flag","FROM_REVEALSPY");
        goToSpiesWinIntent.putExtra("PLAYERS_COMPLETED", playersCompleted);
        goToSpiesWinIntent.putExtra("PLACE", place);
        startActivity(goToSpiesWinIntent);
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}

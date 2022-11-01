package pt.ubi.di.pmd.spyfall_ubi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class LobbyActivity extends AppCompatActivity {
    ListView playersList;
    EditText EdText_player_name;
    CheckBox checkbox_ubi;
    ImageButton Btn_add_player;
    ArrayList<String> arrList_players;
    ArrayAdapter<String> adapter;
    Boolean checkbox_result;
    Integer numberOfSpies;
    TextView textNumberSpies;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Btn_add_player = findViewById(R.id.btn_add_player);
        playersList = findViewById(R.id.players_list);
        EdText_player_name = findViewById(R.id.add_player_name);
        checkbox_ubi = findViewById(R.id.checkBoxUBI);
        textNumberSpies = findViewById(R.id.number_of_spies);

        arrList_players = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrList_players);
        playersList.setAdapter(adapter);

        checkbox_result = true;

        playersList.setOnItemLongClickListener( new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int itemToDelete = i;

                new AlertDialog.Builder(LobbyActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want to delete this item?")
                        .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                arrList_players.remove(itemToDelete);
                                adapter.notifyDataSetChanged();
                                updateNumberOfSpies();
                            }
                        })
                        .setNegativeButton("No!", null)
                        .show();
                return true;
            }
        });
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
                new AlertDialog.Builder(LobbyActivity.this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Are you sure?")
                        .setMessage("Do you want end the current game and go back to the main page?")
                        .setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finish();
                            }
                        })
                        .setNegativeButton("No!", null)
                        .show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void updateNumberOfSpies(){
        if (arrList_players.isEmpty()) {
            numberOfSpies = 0;
        }
        else if (arrList_players.size() < 6) {
            numberOfSpies = 1;
        }
        else {
            numberOfSpies = 2;
        }
        textNumberSpies.setText("Number of spies: "+String.valueOf(numberOfSpies));
    }

    public void addToList (View v){
        arrList_players.add(EdText_player_name.getText().toString());
        adapter.notifyDataSetChanged();
        updateNumberOfSpies();
    }

    public void checkBoxClick (View v){
        if (checkbox_ubi.isChecked()) {
            checkbox_result = true;
            System.out.println(checkbox_result);
        } else {
            checkbox_result = false;
            System.out.println(checkbox_result);
        }
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public ArrayList<Player> setPlayersRoles(ArrayList<String> playersList){
        int i = 0;
        int posFirstSpy  = 0;
        int posSecondSpy = 0;
        ArrayList<Player> players = new ArrayList<Player>();

        posFirstSpy = getRandomNumber(0, playersList.size());
        if (numberOfSpies > 1) {
            do{
                posSecondSpy = getRandomNumber(0, playersList.size());
            }while(posSecondSpy == posFirstSpy );

            for (i=0; i<playersList.size(); i++){
                if (i == posFirstSpy ) {
                    players.add(new Player(playersList.get(i), 1));
                }
                else if (i == posSecondSpy) {
                    players.add(new Player(playersList.get(i), 1));
                }
                else{
                    players.add(new Player(playersList.get(i), 0));
                }
            }
        }
        else {
            for (i=0; i<playersList.size(); i++){
                if (i == posFirstSpy ) {
                    players.add(new Player(playersList.get(i), 1));
                }
                else{
                    players.add(new Player(playersList.get(i), 0));
                }
            }
        }

        return players;
    }

    public ArrayList<Place> getPlaces(Boolean UBIPlaces){
        ArrayList<Place> places = new ArrayList<Place>();

        if (UBIPlaces) {
            places.add(new Place("UBI - Library", "@drawable/", ""));
            places.add(new Place("UBI - Canteen", "@drawable/", ""));
            places.add(new Place("UBI - Amphitheater 6.01", "@drawable/", ""));
            places.add(new Place("UBI - Philosophers Tour", "@drawable/", ""));
            places.add(new Place("UBI - Faculty of Health Sciences", "@drawable/", ""));
            places.add(new Place("UBI - Faculty of Engineering", "@drawable/", ""));
            places.add(new Place("Covilhã - Cova da Beira University Hospital", "@drawable/", ""));
            places.add(new Place("Covilhã - Serra Shopping", "@drawable/", ""));
            places.add(new Place("Covilhã - Torre", "@drawable/", ""));
            places.add(new Place("Covilhã - Wool museum", "@drawable/", ""));
        }
        else{
            places.add(new Place("Air Plane", "@drawable/", ""));
            places.add(new Place("Beach", "@drawable/", ""));
            places.add(new Place("Supermarket", "@drawable/", ""));
            places.add(new Place("Restaurant", "@drawable/", ""));
            places.add(new Place("Hospital", "@drawable/", ""));
            places.add(new Place("School", "@drawable/", ""));
            places.add(new Place("Zoo", "@drawable/", ""));
            places.add(new Place("Bank", "@drawable/", ""));
            places.add(new Place("Night Club", "@drawable/", ""));
            places.add(new Place("Space Station", "@drawable/", ""));
        }

        return places;
    }

    public Place choosePlace(ArrayList<Place> gamePlaces){
        return gamePlaces.get(getRandomNumber(0, gamePlaces.size()));
    }

    public void StartGame (View v){
        if (arrList_players.size() < 4) {
            new AlertDialog.Builder(LobbyActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Can't proceed!")
                    .setMessage("It is necessary at least 4 players to start the game!")
                    .setNeutralButton("Got it!", null)
                    .show();
        }else {
            ArrayList<Player> players = setPlayersRoles(arrList_players);
            Place place = choosePlace(getPlaces(checkbox_result));

            Intent goToWhoAreYouIntent = new Intent(this, WhoAreYouActivity.class);
            goToWhoAreYouIntent.putExtra("flag", "FROM_LOBBY");
            goToWhoAreYouIntent.putExtra("PLAYERS", players);
            goToWhoAreYouIntent.putExtra("PLACE", place);
            startActivity(goToWhoAreYouIntent);
        }
    }
}

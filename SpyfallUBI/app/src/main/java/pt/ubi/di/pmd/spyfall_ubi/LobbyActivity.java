package pt.ubi.di.pmd.spyfall_ubi;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.os.ConfigurationCompat;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

public class LobbyActivity extends AppCompatActivity {
    ListView ListViewPlayers;
    EditText EditTextPlayerName;
    CheckBox CheckBoxUBI;
    ImageButton BtnAddPlayer;
    Boolean CheckBoxResult;
    Integer numberOfSpies;
    TextView TextViewNumberOfSpies;
    ArrayList<Player> players;
    ArrayAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Change toolbar title
        setTitle(getResources().getString(R.string.LobbyActivity));

        // Getting the flag from the intent that he came from
        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        // Check flag and initialize objects
        if(checkFlag == null){
            players = new ArrayList<Player>();
        }
        else if(checkFlag.equals("FROM_NONSPYWIN") || checkFlag.equals("FROM_SPYWIN") || checkFlag.equals("FROM_REVEALLOCATION") || checkFlag.equals("FROM_GAMEON")){
            players = (ArrayList<Player>) getIntent().getSerializableExtra("PLAYERS");
        }
        numberOfSpies = 0;
        CheckBoxResult = true;

        BtnAddPlayer = findViewById(R.id.btn_add_player);
        ListViewPlayers = findViewById(R.id.players_list);
        EditTextPlayerName = findViewById(R.id.add_player_name);
        CheckBoxUBI = findViewById(R.id.checkBoxUBI);
        TextViewNumberOfSpies = findViewById(R.id.number_of_spies);

        // Sort players by their score
        Collections.sort(players, (o1, o2) -> o2.getPoints() - o1.getPoints());

        // Put the players and their scores in the list
        adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_2, android.R.id.text1, players) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text1 = view.findViewById(android.R.id.text1);
                TextView text2 = view.findViewById(android.R.id.text2);

                text1.setText(players.get(position).getName());
                text2.setText(players.get(position).getPoints()+" points");
                return view;
            }
        };
        ListViewPlayers.setAdapter(adapter);

        // Listener for list item click (player deletion)
        ListViewPlayers.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                final int itemToDelete = i;

                new AlertDialog.Builder(LobbyActivity.this)
                        .setIcon(android.R.drawable.ic_delete)
                        .setTitle(getResources().getString(R.string.DeleteItem1))
                        .setMessage(getResources().getString(R.string.DeleteItem2))
                        .setPositiveButton(getResources().getString(R.string.YES), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                players.remove(itemToDelete);
                                adapter.notifyDataSetChanged();
                                updateNumberOfSpies();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.NO), null)
                        .show();
                return true;
            }
        });
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
                new AlertDialog.Builder(LobbyActivity.this)
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

    // Update the number of spies -> If there are more then 5 players then the game will have 2 spies
    public void updateNumberOfSpies(){
        if (players.isEmpty())
            numberOfSpies = 0;
        else if (players.size() < 6)
            numberOfSpies = 1;
        else
            numberOfSpies = 2;

        String spiesString = getResources().getString(R.string.LobbyAct4)+String.valueOf(numberOfSpies);
        TextViewNumberOfSpies.setText(spiesString);
    }

    // Create player and add it to the list
    public void addToList (View v){
        players.add(new Player(EditTextPlayerName.getText().toString(), 0, 0));
        adapter.notifyDataSetChanged();
        updateNumberOfSpies();
    }

    // Updates CheckBoxResult whenever CheckBoxUBI is clicked (changed)
    public void checkBoxClick (View v){
        if (CheckBoxUBI.isChecked())
            CheckBoxResult = true;
        else
            CheckBoxResult = false;
    }

    // Sets players roles by previously generating random positions for the spies
    public void setPlayersRoles(ArrayList<Player> players){
        int i = 0;
        int posFirstSpy  = getRandomNumber(0, players.size());
        int posSecondSpy = 0;

        if (numberOfSpies > 1) {
            do{
                posSecondSpy = getRandomNumber(0, players.size());
            }while(posSecondSpy == posFirstSpy );

            for (i = 0; i < players.size(); i++){
                if (i == posFirstSpy || i == posSecondSpy)
                    players.get(i).setRole(1);
                else
                    players.get(i).setRole(0);
            }
        }
        else {
            for (i = 0; i < players.size(); i++){
                if (i == posFirstSpy )
                    players.get(i).setRole(1);
                else
                    players.get(i).setRole(0);
            }
        }
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

    // Choose a random place between the ones that were previously readed from file
    public Place choosePlace(ArrayList<Place> gamePlaces){
        return gamePlaces.get(getRandomNumber(0, gamePlaces.size()));
    }

    // Start Game by clicking in a button
    public void StartGame (View v) throws IOException {
        // If there arent enought players (4) to start the game, the game wont proceed
        if (players.size() < 4) {
            new AlertDialog.Builder(LobbyActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getResources().getString(R.string.NotEnoughtPlayers1))
                    .setMessage(getResources().getString(R.string.NotEnoughtPlayers2))
                    .setNeutralButton(getResources().getString(R.string.GotIt), null)
                    .show();
        }else {
            setPlayersRoles(players);
            Place place = choosePlace(getPlaces(CheckBoxResult));

            Intent goToWhoAreYouIntent = new Intent(this, WhoAreYouActivity.class);
            goToWhoAreYouIntent.putExtra("flag", "FROM_LOBBY");
            goToWhoAreYouIntent.putExtra("PLAYERS", players);
            goToWhoAreYouIntent.putExtra("PLACE", place);
            startActivity(goToWhoAreYouIntent);
        }
    }

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}

package com.iconcells.simpletodo;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Iterator;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    Integer ItemPos;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Simple Todo");
        setContentView(R.layout.activity_main);

        lvItems = (ListView) findViewById(R.id.lvItems);

        readItems();
        Log.d("items: ", items.toString());

        itemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();

    }

    public void setupListViewListener(){
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
                        items.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        return true;
                    }
                }
        );

        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String item = (String) parent.getItemAtPosition(position);
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                //i.putExtra("position", position);
                ItemPos = position;
                i.putExtra("SelectedItem", item);
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    // callback after Item update
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE){
            String newItem = data.getExtras().getString("newItemText");

            //update the element in arraylist.
            items.set(ItemPos, newItem);
            itemsAdapter.notifyDataSetChanged();
            writeItems();

            int code = data.getExtras().getInt("code", 0);

            //NewItem Update display
            Toast.makeText(this, newItem + " updated", Toast.LENGTH_SHORT).show();
        }
    }

    public void onAddItem(View v){
        TextView etNewItem = (TextView) findViewById(R.id.etNewItem);
        String itemText = etNewItem.getText().toString();
        itemsAdapter.add(itemText);
        etNewItem.setText("");
        writeItems();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void readItems(){
        //readItemsUsingFile();
        dbGetAll();
    }
    private void writeItems(){
        //writeItemsUsingFile();
        dbSetAll();
    }

    //Read Write using SQLite DB
    private static final String SAMPLE_DB_NAME = "todoandroid.db";
    private static final String SAMPLE_TABLE_NAME = "todoitems";
    private static SQLiteDatabase sampleDB;

    public void dbGetAll(){
        items = new ArrayList<String>();

        sampleDB = this.openOrCreateDatabase(SAMPLE_DB_NAME, MODE_PRIVATE, null);
        sampleDB.execSQL("CREATE TABLE IF NOT EXISTS " +
                SAMPLE_TABLE_NAME +
                " (ItemName VARCHAR, Details VARCHAR," +
                " Rank VARCHAR);");

        String sql = "SELECT ItemName FROM " + SAMPLE_TABLE_NAME + ";";

        String[] dbResult = {};
        items = new ArrayList<String>();

        Cursor c = sampleDB.rawQuery(sql, null);
        while(c.moveToNext()) {
            items.add(c.getString(0));
        }
        sampleDB.close();
        Toast.makeText(this, "Items loaded from DB!", Toast.LENGTH_LONG).show();

    }
    public void dbSetAll(){
        sampleDB = this.openOrCreateDatabase(SAMPLE_DB_NAME, MODE_PRIVATE, null);
        sampleDB.execSQL("DELETE FROM " +
                SAMPLE_TABLE_NAME + ";");

        Iterator<String> i = items.iterator();
        while(i.hasNext()) {
            sampleDB.execSQL("INSERT INTO " +
                    SAMPLE_TABLE_NAME +
                    " Values ('" + i.next() + "','','');");
        }
        sampleDB.close();
    }

    // Read Write using File
    private void readItemsUsingFile(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try{
            items = new ArrayList<String>(FileUtils.readLines(todoFile));
        } catch (IOException e){
            items = new ArrayList<String>();
        }
    }

    private void writeItemsUsingFile(){
        File filesDir = getFilesDir();
        File todoFile = new File(filesDir, "todo.txt");
        try{
            FileUtils.writeLines(todoFile, items);
        } catch(IOException e){
            e.printStackTrace();
        }
    }

}

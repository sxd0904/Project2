package com.dhsu0701.project2;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Adapter;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MyActivity extends Activity
{

    ArrayList<String> tagArrayList = new ArrayList<String>();
    ArrayAdapter<String> adapter;

    SharedPreferences sharePreferences;
    SharedPreferences.Editor editor;
    final Context context = this;

    EditText queryInputBox;
    EditText tagInput;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        tagInput = (EditText) findViewById(R.id.utaQueryEdit);
        queryInputBox = (EditText) findViewById(R.id.tagTextEdit);

        final ListView listView = (ListView) findViewById(R.id.listView);

        sharePreferences = getPreferences(MODE_PRIVATE);
        editor = sharePreferences.edit();
        editor.apply();

        tagArrayList = new ArrayList<String>(sharePreferences.getAll().keySet());

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                loadPreferences(tagArrayList.get(position));
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> a, View v, int position, long id)
            {
                final CharSequence[] items = {"Share", "Edit", "Delete"};
                final String tagArray = (tagArrayList.get(position));

                String formatTag = String.format("Share, Edit or Delete the search tagged as \"%s\"", tagArray);

                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                builder.setTitle(formatTag);
                builder.setItems(items, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        String selected = items[which].toString();

                        if(selected.equals("Share"))
                        {
                            Intent shareIntent = new Intent();
                            shareIntent.setAction(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            startActivity(Intent.createChooser(shareIntent, "Share"));
                        }
                        if(selected.equals("Edit"))
                        {
                            queryInputBox.setText(sharePreferences.getString(tagArray, ""));

                            tagInput.setText(tagArray);
                            tagArrayList.remove(tagArray);

                            adapter.notifyDataSetChanged();

                            editor.remove(tagArray);
                            editor.apply();
                        }
                        if(selected.equals("Delete"))
                        {
                            tagArrayList.remove(tagArray);
                            adapter.notifyDataSetChanged();
                            editor.remove(tagArray);
                            editor.apply();
                        }
                    }
                });

                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int id){}
                });

                builder.create();
                builder.show();

                return true;
            }
        });

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, tagArrayList);

        listView.setAdapter(adapter);
    }

    public void onSaveClick(View v)
    {
        String tagSaved;
        String searchQuery;

        searchQuery = queryInputBox.getText().toString();
        tagSaved = tagInput.getText().toString();


        editor.putString(tagSaved, searchQuery);
        editor.commit();


        if(!tagArrayList.contains(tagSaved))
        {
            tagArrayList.add(tagInput.getText().toString());

            queryInputBox.setText("");
            tagInput.setText("");

        }
        else
        {
            queryInputBox.setText("");
            tagInput.setText("");
            queryInputBox.requestFocus();
        }

        //Hides Keyboard
        ((InputMethodManager) getSystemService (
                Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                queryInputBox.getWindowToken(), 0);

        adapter.notifyDataSetChanged();
    }

    private void loadPreferences(String key)
    {
        String utaUrl = "http://www.uta.edu/search/?q=";

        String searchKeyWord;

        SharedPreferences sharePreferences = getPreferences(MODE_PRIVATE);
        String keyWord = sharePreferences.getString(key, "");

        searchKeyWord = String.format("%s%s", utaUrl, keyWord);
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(searchKeyWord));

        startActivity(intent);
    }
}
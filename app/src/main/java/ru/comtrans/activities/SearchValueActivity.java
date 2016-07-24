package ru.comtrans.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.adapters.ListAdapter;
import ru.comtrans.helpers.Const;
import ru.comtrans.items.ListItem;

public class SearchValueActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<ListItem> items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_value);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        listView = (ListView)findViewById(android.R.id.list);
        setSupportActionBar(toolbar);
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(Const.EXTRA_TITLE));

        }
        items = getIntent().getParcelableArrayListExtra(Const.EXTRA_VALUES);
        if(items!=null) {
            final ListAdapter adapter = new ListAdapter(SearchValueActivity.this, items);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent();
                    intent.putExtra(Const.EXTRA_POSITION,getIntent().getIntExtra(Const.EXTRA_POSITION,-1));
                    intent.putExtra(Const.EXTRA_VALUE,adapter.getItem(position));
                    setResult(Const.SEARCH_VALUE_RESULT, intent);
                    finish();
                }
            });
        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_value,menu);
        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        final SearchView searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.onActionViewCollapsed();
                searchView.setQuery("", false);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                ((ListAdapter) listView.getAdapter()).getFilter().filter(s);
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;

        }

        return false;
    }

}

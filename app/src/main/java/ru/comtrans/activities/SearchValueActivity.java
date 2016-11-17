package ru.comtrans.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import ru.comtrans.singlets.InfoBlockHelper;

public class SearchValueActivity extends BaseActivity {

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

        int screenNum = getIntent().getIntExtra(Const.EXTRA_SCREEN_NUM,-1);
        int position = getIntent().getIntExtra(Const.EXTRA_POSITION,-1);
        long mark = getIntent().getLongExtra(Const.EXTRA_MARK,-1);

        InfoBlockHelper helper = InfoBlockHelper.getInstance();

        items = helper.getItems().get(screenNum).get(position).getListValues();


        if(mark!=-1){
            ArrayList<ListItem> values = new ArrayList<>();
            for (ListItem value :
                    items) {
                if(value.getMark()==mark){
                    values.add(value);
                }
            }
            items = values;
        }



        if(items!=null) {
            Log.d("TAG","items "+items.toString());
            final ListAdapter adapter = new ListAdapter(SearchValueActivity.this, items);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent result = new Intent();
                    result.putExtra(Const.EXTRA_POSITION,getIntent().getIntExtra(Const.EXTRA_POSITION,-1));
                    result.putExtra(Const.EXTRA_VALUE,adapter.getItem(position));
                    setResult(Const.SEARCH_VALUE_RESULT, result);
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

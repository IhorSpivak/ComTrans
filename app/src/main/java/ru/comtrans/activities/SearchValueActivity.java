package ru.comtrans.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import ru.comtrans.R;
import ru.comtrans.adapters.ListAdapter;
import ru.comtrans.helpers.Const;
import ru.comtrans.items.IdsRelationHelperItem;
import ru.comtrans.items.ListItem;
import ru.comtrans.singlets.InfoBlockHelper;
import ru.comtrans.singlets.InfoBlocksStorage;

public class SearchValueActivity extends BaseActivity {

    private ListView listView;
    private TextView tvEmpty;
    private ArrayList<ListItem> items;
    private ListAdapter adapter;
    private MenuItem addValueItem;
    private SearchView searchView;
    private InfoBlockHelper helper;
    private int screenNum;
    private int extraPosition;
    private boolean canAdd;
    private long propCode;
    private String infoBlockId;
    private IdsRelationHelperItem idsRelationHelperItem;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_value);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        listView = (ListView) findViewById(android.R.id.list);
        tvEmpty = (TextView) findViewById(android.R.id.empty);
        listView.setEmptyView(tvEmpty);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getIntent().getStringExtra(Const.EXTRA_TITLE));

        }

        screenNum = getIntent().getIntExtra(Const.EXTRA_SCREEN_NUM, -1);
        extraPosition = getIntent().getIntExtra(Const.EXTRA_POSITION, -1);
        infoBlockId = getIntent().getStringExtra(Const.EXTRA_INFO_BLOCK_ID);
        propCode = getIntent().getLongExtra(Const.EXTRA_PROP_CODE, -1);
        idsRelationHelperItem = (IdsRelationHelperItem) getIntent().getSerializableExtra(Const.EXTRA_IDS_HELPER);
        Log.e("TMP_TEST",idsRelationHelperItem.toString());
//        mark = getIntent().getLongExtra(Const.EXTRA_MARK,-1);
//        model = getIntent().getLongExtra(Const.EXTRA_MODEL,-1);
//        engineMark = getIntent().getLongExtra(Const.EXTRA_ENGINE_MARK,-1);

        helper = InfoBlockHelper.getInstance();

        items = helper.getItems().get(screenNum).get(extraPosition).getListValues();

        canAdd = helper.getItems().get(screenNum).get(extraPosition).canAdd();

        Log.e("TMP_TEST","items.size="+items.size());

        if (items != null) {
            adapter = new ListAdapter(SearchValueActivity.this, items, idsRelationHelperItem, canAdd, infoBlockId);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent result = new Intent();
                    helper.getItems().get(screenNum).get(extraPosition).setListValues(adapter.getItems());
                    result.putExtra(Const.EXTRA_POSITION, extraPosition);
                    result.putExtra(Const.EXTRA_VALUE, adapter.getItem(position));
                    setResult(Const.SEARCH_VALUE_RESULT, result);
                    finish();
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_value, menu);
        addValueItem = menu.findItem(R.id.action_add);
        if (!canAdd) {
            if (addValueItem != null)
                addValueItem.setVisible(false);
            invalidateOptionsMenu();
        }
        final MenuItem myActionMenuItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
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
        switch (item.getItemId()) {
            case android.R.id.home:
                helper.getItems().get(screenNum).get(extraPosition).setListValues(adapter.getItems());
                finish();
                return true;
            case R.id.action_add:
                android.support.v7.app.AlertDialog.Builder addPropertyDialog = new android.support.v7.app.AlertDialog.Builder(this);
                addPropertyDialog.setTitle(R.string.dialog_add_property_title);
                View v = View.inflate(this, R.layout.dialog_add_property, null);

                addPropertyDialog.setView(v);
                final EditText etValue = (EditText) v.findViewById(R.id.et_value);

                addPropertyDialog.setPositiveButton(R.string.action_add_property,
                        null);

                addPropertyDialog.setNegativeButton(R.string.dialog_negative_answer,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                final AlertDialog dialog = addPropertyDialog.create();
                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialogInterface) {
                        Button b = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (etValue.getText().toString().trim().equals("")) {
                                    Toast.makeText(SearchValueActivity.this, R.string.enter_value_toast, Toast.LENGTH_SHORT).show();
                                } else if (adapter.containsValue(etValue.getText().toString())) {
                                    Toast.makeText(SearchValueActivity.this, R.string.value_exist_toast, Toast.LENGTH_SHORT).show();
                                } else {
                                    ListItem listItem = new ListItem(-2, etValue.getText().toString().trim());
                                    if (idsRelationHelperItem.getMark() != -1) {
                                        listItem.setMark((int) idsRelationHelperItem.getMark());
                                    }
                                    if (idsRelationHelperItem.getEngineMark() != -1) {
                                        listItem.setEngineMark((int) idsRelationHelperItem.getEngineMark());
                                    }
                                    if (idsRelationHelperItem.getModel() != -1) {
                                        ArrayList<Integer> model = new ArrayList<>();
                                        model.add((int) idsRelationHelperItem.getModel());
                                        listItem.setModel(model);
                                    }
                                    adapter.addItem(listItem);
                                    searchView.onActionViewCollapsed();
                                    searchView.setQuery("", false);
                                    dialog.dismiss();
                                }
                            }
                        });
                    }
                });
                dialog.show();
                return true;

        }

        return false;
    }

}

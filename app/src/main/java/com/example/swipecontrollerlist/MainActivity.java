package com.example.swipecontrollerlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements  SwipeControllerCallback.SwipeButtonActionsListener {


    private SwipeControllerCallback mSwipeControllerCallback;
    private CustomRecyclerViewAdapter mCustomRecyclerViewAdapter;

    private RelativeLayout mainLayout;
    private RecyclerView recyclerView;

    private List<String> mItemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mainLayout = findViewById(R.id.rl_main);

        mSwipeControllerCallback = new SwipeControllerCallback(this);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(mSwipeControllerCallback);

        recyclerView = (RecyclerView)findViewById(R.id.recycler_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemTouchHelper.attachToRecyclerView(recyclerView);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                mSwipeControllerCallback.onDraw(c);
            }
        });
        populateData();
    }

    private void populateData() {

        mItemList= new ArrayList<String>();
        mItemList.add("Item 1");
        mItemList.add("Item 2");
        mItemList.add("Item 3");
        mItemList.add("Item 4");
        mItemList.add("Item 5");
        mItemList.add("Item 6");
        mItemList.add("Item 7");
        mItemList.add("Item 8");
        mItemList.add("Item 9");
        mItemList.add("Item 10");

        mCustomRecyclerViewAdapter = new CustomRecyclerViewAdapter(mItemList);
        recyclerView.setAdapter(mCustomRecyclerViewAdapter);

    }

    @Override
    public void onButtonClicked(int itemID, int btnId) {
        switch (btnId){
            case 0:
                 deleteItem(itemID);
                break;
            case 1:
                Toast.makeText(this,"edit event",Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(this,"invalid event",Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteItem(final int id) {

        final String data = mItemList.get(id);
        mCustomRecyclerViewAdapter.deleteItem(id);
        Snackbar snackbar = Snackbar
                .make(mainLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mCustomRecyclerViewAdapter.restoreItem(id,data);
                recyclerView.scrollToPosition(id);
            }
        });

        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();


    }
}

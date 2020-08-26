package com.example.swipecontrollerlist;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE;
import static androidx.recyclerview.widget.ItemTouchHelper.LEFT;


enum ButtonState{
    BUTTON_GONE ,
    BUTTON_VISIBLE

}

public class SwipeControllerCallback extends ItemTouchHelper.Callback {


    private static final String TAG = SwipeControllerCallback.class.getSimpleName();
    private boolean swipeBack = false;
    private ButtonState mButtonVisibilityState = ButtonState.BUTTON_GONE;
    private SwipeButtonActionsListener swipeButtonActionsListener ;
    private static final float buttonWidth = 150;
    private RecyclerView.ViewHolder mCurrentItemViewHolder = null;
    private RectF[] buttonInstanceArray= new RectF[2];


    SwipeControllerCallback(SwipeButtonActionsListener listener){

        this.swipeButtonActionsListener =listener;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT);
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        if (swipeBack) {
            swipeBack = mButtonVisibilityState != ButtonState.BUTTON_GONE;
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }
    @Override
    public void onChildDraw(Canvas c,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        if (actionState == ACTION_STATE_SWIPE) {
            if (mButtonVisibilityState != ButtonState.BUTTON_GONE) {
                dX = Math.min(dX, -(buttonWidth*2));
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            } else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }

        if (mButtonVisibilityState == ButtonState.BUTTON_GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        mCurrentItemViewHolder = viewHolder;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchListener(final Canvas c,
                                  final RecyclerView recyclerView,
                                  final RecyclerView.ViewHolder viewHolder,
                                  final float dX, final float dY,
                                  final int actionState, final boolean isCurrentlyActive) {

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                if(swipeBack){
                    if(dX < - (buttonWidth*2)) {
                        mButtonVisibilityState = ButtonState.BUTTON_VISIBLE;
                    }

                    if(mButtonVisibilityState != ButtonState.BUTTON_GONE){
                        setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                        setItemsClickable(recyclerView, false);
                    }
                }
                return false;


            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setTouchDownListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                      final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                }
                return false;
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setTouchUpListener(final Canvas c, final RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    SwipeControllerCallback.super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                    recyclerView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return false;
                        }
                    });
                    setItemsClickable(recyclerView, true);
                    swipeBack = false;

                    if (swipeButtonActionsListener != null) {


                        float deleteButtonX = viewHolder.itemView.getRight();
                        float editButtonX = (deleteButtonX- buttonWidth);
                        float eventX = event.getX();
                        float eventY = event.getY();
                        if(buttonInstanceArray[0] != null && buttonInstanceArray[0].contains(eventX,eventY) &&
                                (eventX<=deleteButtonX && eventX> editButtonX)){
                            swipeButtonActionsListener.onButtonClicked(viewHolder.getAdapterPosition(), 0);

                        }else if(buttonInstanceArray[1] != null && buttonInstanceArray[1].contains(event.getX(),event.getY()) &&
                                (eventX<=editButtonX && eventX>= (editButtonX-buttonWidth))){
                            swipeButtonActionsListener.onButtonClicked(viewHolder.getAdapterPosition(),1);

                        }

                    }
                    mButtonVisibilityState = ButtonState.BUTTON_GONE;
                    mCurrentItemViewHolder = null;
                }
                return false;
            }
        });
    }

    private void setItemsClickable(RecyclerView recyclerView, boolean b) {
        for (int i = 0; i < recyclerView.getChildCount(); ++i) {
            recyclerView.getChildAt(i).setClickable(b);
        }
    }


    private void drawButtons(Canvas c, RecyclerView.ViewHolder viewHolder) {

        float buttonWidthWithoutPadding = buttonWidth - 20;
        float corners = 16;

        View itemView = viewHolder.itemView;
        Paint p = new Paint();


        int right =itemView.getRight();
        Log.i(TAG,"Right First values  "+ (itemView.getRight() - buttonWidthWithoutPadding)+" "+ itemView.getTop()+" "+itemView.getRight()+ " " + itemView.getBottom());
        RectF deleteButton = new RectF(itemView.getRight() - buttonWidthWithoutPadding, itemView.getTop(), itemView.getRight(), itemView.getBottom());
        p.setColor(Color.RED);
        c.drawRoundRect(deleteButton, corners, corners, p);
        drawText("DELETE", c, deleteButton, p);

        buttonInstanceArray[0] = deleteButton;


        Log.i(TAG,"Right Second values  "+ ((itemView.getRight() - buttonWidthWithoutPadding)-buttonWidth)+" "+ itemView.getTop()+" "+(itemView.getRight()-buttonWidth)+ " " + itemView.getBottom());
        RectF editButton = new RectF((itemView.getRight() - buttonWidthWithoutPadding)-buttonWidth, itemView.getTop(), itemView.getRight()-buttonWidth, itemView.getBottom());
        p.setColor(Color.BLUE);
        c.drawRoundRect(editButton, corners, corners, p);
        drawText("EDIT", c, editButton, p);
        buttonInstanceArray[1] = editButton;

        mButtonVisibilityState = null;

    }

    private void drawText(String text, Canvas c, RectF button, Paint p) {
        float textSize = 30;
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);

        float textWidth = p.measureText(text);
        c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
    }

    public void onDraw(Canvas c) {
        if (mCurrentItemViewHolder != null) {
            drawButtons(c, mCurrentItemViewHolder);
        }
    }


    public interface SwipeButtonActionsListener{
        void onButtonClicked(int itemID, int btnId);
    }
}

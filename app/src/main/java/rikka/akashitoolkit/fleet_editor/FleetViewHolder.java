package rikka.akashitoolkit.fleet_editor;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.TextView;

import rikka.akashitoolkit.R;
import rikka.akashitoolkit.ui.widget.ItemTouchHelperCallback;
import rikka.akashitoolkit.viewholder.ItemTouchHelperViewHolder;

/**
 * Created by Rikka on 2016/7/29.
 */
public class FleetViewHolder extends RecyclerView.ViewHolder implements ItemTouchHelperViewHolder {

    public TextView mTitle;
    public TextView mSummary;
    public TextView mContent;
    public RecyclerView mRecyclerView;
    public FleetEquipAdapter mAdapter;
    public View mButton;

    public boolean drag;
    public boolean swipe;

    public FleetViewHolder(View itemView) {
        super(itemView);

        mTitle = (TextView) itemView.findViewById(android.R.id.title);
        mSummary = (TextView) itemView.findViewById(android.R.id.summary);
        mContent = (TextView) itemView.findViewById(android.R.id.content);
        mRecyclerView = (RecyclerView) itemView.findViewById(R.id.content_container);
        mButton = itemView.findViewById(android.R.id.icon);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false));
        mRecyclerView.getLayoutManager().setAutoMeasureEnabled(true);
        mRecyclerView.setNestedScrollingEnabled(false);

        mAdapter = new FleetEquipAdapter();
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(mAdapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(mRecyclerView);

        drag = true;
        swipe = false;
    }


    @Override
    public int getDragFlags() {
        return drag ? dragFlags : 0;
    }

    @Override
    public int getSwipeFlags() {
        return swipe ? swipeFlags : 0;
    }
}

package rikka.akashitoolkit.viewholder;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import rikka.akashitoolkit.R;
import rikka.akashitoolkit.adapter.GalleryAdapter;
import rikka.akashitoolkit.utils.Utils;

/**
 * Created by Rikka on 2016/7/29.
 */
public class FleetListViewHolder extends RecyclerView.ViewHolder {
    private static final int SPAN_COUNT = 3;

    public TextView mTitle;
    public TextView mSummary;
    public RecyclerView mRecyclerView;

    public int mItemSize;

    public int mItemHeight;
    public int mItemWidth;

    public void setImageSize() {
        mRecyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                mItemSize = (v.getWidth() - (Utils.dpToPx(4) * (SPAN_COUNT - 1))) / SPAN_COUNT;
                mItemHeight = (int) (mItemSize * 0.25);
                mItemWidth = mItemSize;
                mRecyclerView.getAdapter().notifyDataSetChanged();

                v.removeOnLayoutChangeListener(this);
            }
        });

        mRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerView.getAdapter().notifyDataSetChanged();
            }
        }, 300);
    }

    public FleetListViewHolder(View itemView) {
        super(itemView);

        mTitle = (TextView) itemView.findViewById(android.R.id.title);
        mSummary = (TextView) itemView.findViewById(android.R.id.summary);
        mRecyclerView = (RecyclerView) itemView.findViewById(android.R.id.content);

        mRecyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(), SPAN_COUNT));
        mRecyclerView.getLayoutManager().setAutoMeasureEnabled(true);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(new GalleryAdapter(R.layout.item_ship_banner) {
            @Override
            public void onBindViewHolder(ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                holder.mImageView.setLayoutParams(new FrameLayout.LayoutParams(mItemWidth, mItemHeight));
                holder.mImageView.setOnClickListener(null);
            }
        });
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                int position = parent.getChildAdapterPosition(view) + 1;

                outRect.set(0, Utils.dpToPx(4), Utils.dpToPx(4), 0);

                if (position % SPAN_COUNT == 0) {
                    outRect.right = 0;
                }

                if (position <= SPAN_COUNT) {
                    outRect.top = 0;
                }
            }
        });
    }
}

package com.epsilonpros.ipsolution.views;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.annotation.DimenRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import com.epsilonpros.ipsolution.R;

public class RecyclerViewBK extends RecyclerView {
    private View mEmptyView;

    public RecyclerViewBK(Context context) {
        super(context);
    }

    public RecyclerViewBK(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initAttrib(context, attrs,-1);
    }

    public RecyclerViewBK(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrib(context,attrs,defStyle);
    }
    private void initAttrib(Context context, AttributeSet attrs, int defStyle){
        TypedArray attr = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewBK, defStyle, 0);
        int emptyRes = attr.getInt(attr.getIndex(R.styleable.RecyclerViewBK_recycler_emptyview),1);
        if (context instanceof Activity && emptyRes != defStyle){
            mEmptyView = ((Activity)context).findViewById(emptyRes);
        }
    }

    private void initEmptyView() {
        post(()->{
            if (mEmptyView != null) {
                mEmptyView.setVisibility(
                        getAdapter() == null || getAdapter().getItemCount() == 0 ? VISIBLE : GONE);
                RecyclerViewBK.this.setVisibility(
                        getAdapter() == null || getAdapter().getItemCount() == 0 ? GONE : VISIBLE);
            }
        });

    }

    final AdapterDataObserver observer = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            initEmptyView();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            initEmptyView();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            initEmptyView();
        }
    };

    @Override
    public void setAdapter(Adapter adapter) {
        Adapter oldAdapter = getAdapter();
        super.setAdapter(adapter);

        if (oldAdapter != null) {
            oldAdapter.unregisterAdapterDataObserver(observer);
        }

        if (adapter != null) {
            adapter.registerAdapterDataObserver(observer);
        }
    }

    public void setEmptyView(View view) {
        this.mEmptyView = view;
        initEmptyView();
    }
    public static class ItemOffsetDecoration extends ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(@NonNull Context context, @DimenRes int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
    }
}

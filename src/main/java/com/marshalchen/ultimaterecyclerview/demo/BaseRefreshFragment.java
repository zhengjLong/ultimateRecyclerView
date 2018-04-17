package com.marshalchen.ultimaterecyclerview.demo;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.marshalchen.ultimaterecyclerview.CustomUltimateRecyclerview;
import com.marshalchen.ultimaterecyclerview.ObservableScrollState;
import com.marshalchen.ultimaterecyclerview.ObservableScrollViewCallbacks;
import com.marshalchen.ultimaterecyclerview.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.layoutmanagers.ClassicSpanGridLayoutManager;
import com.marshalchen.ultimaterecyclerview.layoutmanagers.ScrollSmoothLineaerLayoutManager;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;
import com.marshalchen.ultimaterecyclerview.ui.DividerItemDecoration;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import in.srain.cube.views.ptr.header.MaterialHeader;
import in.srain.cube.views.ptr.header.StoreHouseHeader;

/**
 * 刷新fragment基类
 * @author : jerome
 */
public abstract class BaseRefreshFragment extends Fragment {

    protected Toolbar toolbar;
    protected boolean status_progress = false;


    /**
     * 添加recycleView的头部
     *
     * @param layout
     */
    protected void enableParallaxHeader(int layout) {

        if (null == setUltimateRecyclerView()) return;

        setUltimateRecyclerView().setParallaxHeader(getLayoutInflater().inflate(layout, setUltimateRecyclerView().mRecyclerView, false));
        setUltimateRecyclerView().setOnParallaxScroll(new UltimateRecyclerView.OnParallaxScroll() {
            @Override
            public void onParallaxScroll(float percentage, float offset, View parallax) {
                if (null != toolbar) {
                    Drawable c = toolbar.getBackground();
                    c.setAlpha(Math.round(127 + percentage * 128));
                    toolbar.setBackgroundDrawable(c);
                }
            }
        });
    }

    protected void setToolbar(Toolbar toolbar){
        this.toolbar = toolbar;
    }


    /**
     * 实现加载更多
     */
    protected void enableLoadMore() {

        if (null == setUltimateRecyclerView()) return;

        setUltimateRecyclerView().setLoadMoreView(R.layout.custom_bottom_progressbar);

        setUltimateRecyclerView().setOnLoadMoreListener(new UltimateRecyclerView.OnLoadMoreListener() {
            @Override
            public void loadMore(int itemsCount, final int maxLastVisiblePosition) {
                status_progress = true;
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        onLoadMore();
                        status_progress = false;
                    }
                }, 500);
            }
        });

    }


    /**
     * 实现自定义下拉刷新
     */
    protected void enableRefreshString() {
        if (null == setUltimateRecyclerView())return;
        setUltimateRecyclerView().setCustomSwipeToRefresh();
        StoreHouseHeader storeHouseHeader = new StoreHouseHeader(getContext());
        storeHouseHeader.initWithString("UMEETING");
        setUltimateRecyclerView().mPtrFrameLayout.removePtrUIHandler(storeHouseHeader);
        setUltimateRecyclerView().mPtrFrameLayout.setHeaderView(storeHouseHeader);
        setUltimateRecyclerView().mPtrFrameLayout.addPtrUIHandler(storeHouseHeader);
        setUltimateRecyclerView().mPtrFrameLayout.autoRefresh(false);
        setUltimateRecyclerView().mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout ptrFrameLayout, View view, View view2) {
                return PtrDefaultHandler.checkContentCanBePulledDown(ptrFrameLayout, view, view2);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout ptrFrameLayout) {
                ptrFrameLayout.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onFireRefresh();
                    }
                }, 1800);
            }
        });

    }




    /**
     * 实现原生下拉刷新
     */
    protected void enableRefreshMaterial() {

        if (null == setUltimateRecyclerView())return;
        setUltimateRecyclerView().setCustomSwipeToRefresh();

        MaterialHeader materialHeader = new MaterialHeader(getContext());
        int[] colors = new int[]{R.color.colorPrimary};
        materialHeader.setColorSchemeColors(colors);
        materialHeader.setLayoutParams(new PtrFrameLayout.LayoutParams(-1, -2));
        materialHeader.setPadding(0, 15, 0, 10);
        materialHeader.setPtrFrameLayout(setUltimateRecyclerView().mPtrFrameLayout);
        setUltimateRecyclerView().mPtrFrameLayout.autoRefresh(false);
        setUltimateRecyclerView().mPtrFrameLayout.removePtrUIHandler(materialHeader);
        setUltimateRecyclerView().mPtrFrameLayout.setHeaderView(materialHeader);
        setUltimateRecyclerView().mPtrFrameLayout.addPtrUIHandler(materialHeader);

        setUltimateRecyclerView().mPtrFrameLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return true;
            }

            @Override
            public void onRefreshBegin(final PtrFrameLayout frame) {
                frame.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onFireRefresh();
                    }
                }, 1800);
            }
        });
    }

    /**
     * 瀑布流布局管理
     * @param rv
     * @param ad
     */
    protected final void configStaggerLayoutManager(UltimateRecyclerView rv, easyRegularAdapter ad) {
        StaggeredGridLayoutManager gaggeredGridLayoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rv.setLayoutManager(gaggeredGridLayoutManager);
    }

    /**
     * 网格布局拓管理
     * @param rv
     * @param ad
     */
    protected final void configGridLayoutManager(UltimateRecyclerView rv, easyRegularAdapter ad) {
        if (null != getContext()){
            final ClassicSpanGridLayoutManager mgm = new ClassicSpanGridLayoutManager(getContext(), 2, ad);
            rv.setLayoutManager(mgm);
        }
    }

    /**
     * 线性布局管理
     * @param rv
     */
    protected final void configLinearLayoutManager(UltimateRecyclerView rv) {
        if (null != getContext()){
            final ScrollSmoothLineaerLayoutManager mgm = new ScrollSmoothLineaerLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false, 300);
            rv.setLayoutManager(mgm);
            rv.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL_LIST));
        }
    }


    /**
     * 配合FloatingActionMenu使用
     */
    protected final void enableScrollControl() {
        if (null == setUltimateRecyclerView()) return;
        setUltimateRecyclerView().setScrollViewCallbacks(new ObservableScrollViewCallbacks() {
            @Override
            public void onScrollChanged(int scrollY, boolean firstScroll, boolean dragging) {
            }

            @Override
            public void onDownMotionEvent() {

            }

            @Override
            public void onUpOrCancelMotionEvent(ObservableScrollState observableScrollState) {
                if (null != setUltimateRecyclerView() && null != toolbar) {
                    if (observableScrollState == ObservableScrollState.UP) {
                        setUltimateRecyclerView().hideToolbar(toolbar, setUltimateRecyclerView(), getScreenHeight());
                        setUltimateRecyclerView().hideFloatingActionMenu();
                    } else if (observableScrollState == ObservableScrollState.DOWN) {
                        setUltimateRecyclerView().showToolbar(toolbar, setUltimateRecyclerView(), getScreenHeight());
                        setUltimateRecyclerView().showFloatingActionMenu();
                    }
                }

            }
        });

        setUltimateRecyclerView().showFloatingButtonView();
    }


    /**
     * 显示空视图
     */
    protected void enableEmptyViewPolicy() {

        if (null == setUltimateRecyclerView()) return;
        setUltimateRecyclerView().setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_CLEAR_ALL);

//        setUltimateRecyclerView().setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_KEEP_HEADER_AND_LOARMORE);
//            setUltimateRecyclerView().setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_KEEP_HEADER);
        //  ultimateRecyclerView.setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_SHOW_LOADMORE_ONLY);
    }


    /**
     * 子类实现加载更多
     */
    protected abstract void onLoadMore();

    /**
     * 结束刷新
     */
    protected abstract void onFireRefresh();


    /**
     * 显示的界面高度
     *
     * @return
     */
    public int getScreenHeight() {
        if (null != getActivity())
        return getActivity().findViewById(android.R.id.content).getHeight();
        return 0;
    }

    /**
     * 外部实现recycleView
     *
     * @return
     */
    public abstract CustomUltimateRecyclerview setUltimateRecyclerView();

}

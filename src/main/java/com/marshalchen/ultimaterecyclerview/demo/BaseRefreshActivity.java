package com.marshalchen.ultimaterecyclerview.demo;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.marshalchen.ultimaterecyclerview.ObservableScrollState;
import com.marshalchen.ultimaterecyclerview.ObservableScrollViewCallbacks;
import com.marshalchen.ultimaterecyclerview.R;
import com.marshalchen.ultimaterecyclerview.UltimateRecyclerView;
import com.marshalchen.ultimaterecyclerview.layoutmanagers.ClassicSpanGridLayoutManager;
import com.marshalchen.ultimaterecyclerview.layoutmanagers.ScrollSmoothLineaerLayoutManager;
import com.marshalchen.ultimaterecyclerview.quickAdapter.easyRegularAdapter;

/**
 * 刷新基类
 * @author jerome
 */
public abstract class BaseRefreshActivity extends Activity {

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
     * 实现原生下拉刷新
     */
    protected void enableRefresh() {

        if (null == setUltimateRecyclerView()) return;

        setUltimateRecyclerView().setDefaultOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onFireRefresh();
                    }
                }, 1000);
            }
        });
        setUltimateRecyclerView().setDefaultSwipeToRefreshColorScheme(getResources().getColor(android.R.color.holo_blue_bright));

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
        final ClassicSpanGridLayoutManager mgm = new ClassicSpanGridLayoutManager(this, 2, ad);
        rv.setLayoutManager(mgm);
    }

    /**
     * 线性布局管理
     * @param rv
     */
    protected final void configLinearLayoutManager(UltimateRecyclerView rv) {
        final ScrollSmoothLineaerLayoutManager mgm = new ScrollSmoothLineaerLayoutManager(this, LinearLayoutManager.VERTICAL, false, 300);
        rv.setLayoutManager(mgm);
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
        //  ultimateRecyclerView.setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_KEEP_HEADER_AND_LOARMORE);
        //    ultimateRecyclerView.setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_KEEP_HEADER);
        //  ultimateRecyclerView.setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_SHOW_LOADMORE_ONLY);
        if (null == setUltimateRecyclerView()) return;
        setUltimateRecyclerView().setEmptyView(R.layout.empty_view, UltimateRecyclerView.EMPTY_CLEAR_ALL);
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
        return findViewById(android.R.id.content).getHeight();
    }

    /**
     * 外部实现recycleView
     *
     * @return
     */
    public abstract UltimateRecyclerView setUltimateRecyclerView();

}

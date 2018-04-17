package com.marshalchen.ultimaterecyclerview.demo;

/**
 * View层基类
 * @author : jerome
 */
public interface IBaseView {

    void showLoading();

    void dismissLoading();

    void showToast(String msg);

    void requestError(int code, String message);
}

package example.com.erp.callback;
/*
Create by user on 12-04-2019 at 04:54 PM for ERP
*/

public interface CartItemCallBack {

    void onItemClick(Object object, int position);

    void onItemEdit(Object object, int position);

    void onItemDelete(Object object, int position);
}

package example.com.erp.contracts;
/*
Create by user on 13-02-2019 at 07:03 PM for ERP
*/

public interface CommonClicks {

    void onItemClick(Object obj);

    void onChartClick(Object obj);

    interface CartItemClicks extends CommonClicks {
        void onViewProductDetails(Object obj);
    }
}

package example.com.erp.database;
/*
Create by user on 11-04-2019 at 06:15 PM for ERP
*/

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "cart")
public class CartItem {

    @DatabaseField(columnName = "id", generatedId = true)
    private int id;

    @DatabaseField(columnName = "user_id")
    private String user_id;

    @DatabaseField(columnName = "item_id")
    private String item_id;

    @DatabaseField(columnName = "gs_item_name")
    private String gs_item_name;

    @DatabaseField(columnName = "gs_groups_master_table_id")
    private String gs_groups_master_table_id;

    @DatabaseField(columnName = "base_rate")
    private String base_rate;

    @DatabaseField(columnName = "gs_item_rate")
    private String gs_item_rate;

    @DatabaseField(columnName = "gross_rate")
    private String gross_rate;

    @DatabaseField(columnName = "loading_charges")
    private String loading_charges;

    @DatabaseField(columnName = "insurance_charges")
    private String insurance_charges;

    @DatabaseField(columnName = "net_rate")
    private String net_rate;

    @DatabaseField(columnName = "gst_percentage")
    private String gst_percentage;

    @DatabaseField(columnName = "tax_paid_rate")
    private String tax_paid_rate;

    @DatabaseField(columnName = "order")
    private String order;

    @DatabaseField(columnName = "parent_gs_group_id")
    private String parent_gs_group_id;

    @DatabaseField(columnName = "code")
    private String code;

    @DatabaseField(columnName = "qty")
    private String qty;

    @DatabaseField(columnName = "parent_grp_name")
    private String parent_grp_name;

    @DatabaseField(columnName = "grp_name")
    private String grp_name;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getGs_item_name() {
        return gs_item_name;
    }

    public void setGs_item_name(String gs_item_name) {
        this.gs_item_name = gs_item_name;
    }

    public String getGs_groups_master_table_id() {
        return gs_groups_master_table_id;
    }

    public void setGs_groups_master_table_id(String gs_groups_master_table_id) {
        this.gs_groups_master_table_id = gs_groups_master_table_id;
    }

    public String getBase_rate() {
        return base_rate;
    }

    public void setBase_rate(String base_rate) {
        this.base_rate = base_rate;
    }

    public String getGs_item_rate() {
        return gs_item_rate;
    }

    public void setGs_item_rate(String gs_item_rate) {
        this.gs_item_rate = gs_item_rate;
    }

    public String getGross_rate() {
        return gross_rate;
    }

    public void setGross_rate(String gross_rate) {
        this.gross_rate = gross_rate;
    }

    public String getLoading_charges() {
        return loading_charges;
    }

    public void setLoading_charges(String loading_charges) {
        this.loading_charges = loading_charges;
    }

    public String getInsurance_charges() {
        return insurance_charges;
    }

    public void setInsurance_charges(String insurance_charges) {
        this.insurance_charges = insurance_charges;
    }

    public String getNet_rate() {
        return net_rate;
    }

    public void setNet_rate(String net_rate) {
        this.net_rate = net_rate;
    }

    public String getGst_percentage() {
        return gst_percentage;
    }

    public void setGst_percentage(String gst_percentage) {
        this.gst_percentage = gst_percentage;
    }

    public String getTax_paid_rate() {
        return tax_paid_rate;
    }

    public void setTax_paid_rate(String tax_paid_rate) {
        this.tax_paid_rate = tax_paid_rate;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getParent_gs_group_id() {
        return parent_gs_group_id;
    }

    public void setParent_gs_group_id(String parent_gs_group_id) {
        this.parent_gs_group_id = parent_gs_group_id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getParent_grp_name() {
        return parent_grp_name;
    }

    public void setParent_grp_name(String parent_grp_name) {
        this.parent_grp_name = parent_grp_name;
    }

    public String getGrp_name() {
        return grp_name;
    }

    public void setGrp_name(String grp_name) {
        this.grp_name = grp_name;
    }
}

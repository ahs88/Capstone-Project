package shopon.com.shopon.datamodel.shop_product_categories;

/**
 * Created by akshath on 11/24/2015.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class Category {

    @SerializedName("key")
    @Expose
    private List<String> key = new ArrayList<String>();
    @SerializedName("string")
    @Expose
    private List<String> string = new ArrayList<String>();
    @SerializedName("array")
    @Expose
    private SubCategoryList array;

    /**
     * @return The key
     */
    public List<String> getKey() {
        return key;
    }

    /**
     * @param key The key
     */
    public void setKey(List<String> key) {
        this.key = key;
    }

    /**
     * @return The string
     */
    public List<String> getString() {
        return string;
    }

    /**
     * @param string The string
     */
    public void setString(List<String> string) {
        this.string = string;
    }

    /**
     * @return The array
     */
    public SubCategoryList getArray() {
        return array;
    }

    /**
     * @param array The array
     */
    public void setArray(SubCategoryList array) {
        this.array = array;
    }

}
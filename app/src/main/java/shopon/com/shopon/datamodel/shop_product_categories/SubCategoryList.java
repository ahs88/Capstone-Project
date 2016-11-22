package shopon.com.shopon.datamodel.shop_product_categories;

/**
 * Created by akshath on 11/24/2015.
 */


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class SubCategoryList {

    @SerializedName("string")
    @Expose
    private List<String> string = new ArrayList<String>();

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

}

package shopon.com.shopon.datamodel.merchant;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import shopon.com.shopon.datamodel.offer.OfferRealm;


public class MerchantsRealm extends RealmObject {

    @SerializedName("email")
    @Expose
    public String email;
    @SerializedName("user_id")
    @Expose
    public Integer userId;
    @SerializedName("merchentCategory")
    @Expose
    public String merchentCategory;
    @SerializedName("mobile")
    @Expose
    public String mobile;
    @SerializedName("name")
    @Expose
    public String name;
    /*@SerializedName("offers")
    @Expose
    public OfferRealm offers;*/
    @SerializedName("shopName")
    @Expose
    public String shopName;

    /**
     * @return The email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return The userId
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * @param userId The user_id
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * @return The merchentCategory
     */
    public String getMerchentCategory() {
        return merchentCategory;
    }

    /**
     * @param merchentCategory The merchentCategory
     */
    public void setMerchentCategory(String merchentCategory) {
        this.merchentCategory = merchentCategory;
    }

    /**
     * @return The mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * @param mobile The mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }



    /**
     * @return The shopName
     */
    public String getShopName() {
        return shopName;
    }

    /**
     * @param shopName The shopName
     */
    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

}

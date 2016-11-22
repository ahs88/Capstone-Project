
package shopon.com.shopon.datamodel.merchant;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import shopon.com.shopon.utils.Utils;


public class MerchantData {

    @SerializedName("merchants")
    @Expose
    public Merchants merchants;

    /**
     * 
     * @return
     *     The merchants
     */
    public Merchants getMerchants() {
        return merchants;
    }

    /**
     * 
     * @param merchants
     *     The merchants
     */
    public void setMerchants(Merchants merchants) {
        this.merchants = merchants;
    }

    public void setRealmMerchant(MerchantsRealm merchants){
        this.merchants = new Merchants();
        this.merchants.setUserId(merchants.getUserId());
        this.merchants.setEmail(merchants.getEmail());
        this.merchants.setMobile(merchants.getMobile());
        //this.merchants.setOffers((merchants.getOffers()!=null)?Utils.converOfferRealmToOffer(merchants.getOffers()):null);
        this.merchants.setName(merchants.getName());
        this.merchants.setMerchentCategory(merchants.getMerchentCategory());
        this.merchants.setShopName(merchants.getShopName());
    }

}

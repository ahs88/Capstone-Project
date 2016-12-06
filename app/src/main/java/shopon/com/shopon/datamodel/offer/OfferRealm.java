
package shopon.com.shopon.datamodel.offer;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class OfferRealm extends RealmObject {
    @PrimaryKey
    @SerializedName("offer_id")
    @Expose
    private int offerId;
    @SerializedName("deliverMessageOn")
    @Expose
    private String deliverMessageOn;
    @SerializedName("offerText")
    @Expose
    private String offerText;

    @SerializedName("offer_numbers")
    @Expose
    private String numbers;

    @SerializedName("offer_status")
    @Expose
    private boolean offerStatus;

    /**
     * @return The offerId
     */
    public int getOfferId() {
        return offerId;
    }

    /**
     * @param offerId The offer_id
     */
    public void setOfferId(int offerId) {
        this.offerId = offerId;
    }

    /**
     * @return The deliverMessageOn
     */
    public String getDeliverMessageOn() {
        return deliverMessageOn;
    }

    /**
     * @param deliverMessageOn The deliverMessageOn
     */
    public void setDeliverMessageOn(String deliverMessageOn) {
        this.deliverMessageOn = deliverMessageOn;
    }

    /**
     * @return The offerText
     */
    public String getOfferText() {
        return offerText;
    }

    /**
     * @param offerText The offerText
     */
    public void setOfferText(String offerText) {
        this.offerText = offerText;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }


    public boolean getOfferStatus() {
        return offerStatus;
    }

    public void setOfferStatus(boolean offerStatus) {
        this.offerStatus = offerStatus;
    }
}

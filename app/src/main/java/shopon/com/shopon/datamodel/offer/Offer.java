package shopon.com.shopon.datamodel.offer;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Akshath on 28-07-2016.
 */


public class Offer {

    @SerializedName("deliverMessageOn")
    @Expose
    public String deliverMessageOn;
    @SerializedName("offerText")
    @Expose
    public String offerText;
    @SerializedName("offer_id")
    @Expose
    public int offer_id;
    @SerializedName("offer_numbers")
    @Expose
    public String numbers;

    @SerializedName("offer_status")
    @Expose
    public boolean offerStatus;

    @SerializedName("createdAt")
    @Expose
    public String createdAt;

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

    /**
     * @return The id
     */
    public int getOfferId() {
        return offer_id;
    }

    /**
     * @param id The id
     */
    public void setOfferId(int id) {
        this.offer_id = id;
    }

    /**
     * @return The customerId
     */
    public String getNumbers() {
        return numbers;
    }

    /**
     * @param customerIds The customer_id
     */
    public void setNumbers(String customerIds) {
        this.numbers = customerIds;
    }

    public boolean getOfferStatus() {
        return offerStatus;
    }

    public void setOfferStatus(boolean offerStatus) {
        this.offerStatus = offerStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}
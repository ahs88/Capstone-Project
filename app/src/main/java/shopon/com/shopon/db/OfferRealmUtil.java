package shopon.com.shopon.db;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import shopon.com.shopon.datamodel.offer.Offer;
import shopon.com.shopon.datamodel.offer.OfferRealm;

/**
 * Created by Akshath on 14-11-2016.
 */

public class OfferRealmUtil {
    public static OfferRealm convertRemoteOfferToRealmCustomer(OfferRealm offer_realm, Offer offer_remote_data) {
        if (offer_realm.getOfferId() == 0 || TextUtils.isEmpty(String.valueOf(offer_realm.getOfferId()))) {
            offer_realm.setOfferId(offer_remote_data.getOfferId());
        }

        offer_realm.setOfferText(offer_remote_data.getOfferText());
        offer_realm.setDeliverMessageOn(offer_remote_data.getDeliverMessageOn());
        offer_realm.setNumbers(offer_remote_data.getNumbers());
        offer_realm.setOfferStatus(offer_remote_data.getOfferStatus());
        return offer_realm;
    }

    public static Offer converOfferRealmToOffer(OfferRealm offer_realm) {
        Offer offer = new Offer();
        offer.setOfferId(offer_realm.getOfferId());
        offer.setOfferText(offer_realm.getOfferText());
        offer.setDeliverMessageOn(offer_realm.getDeliverMessageOn());
        offer.setNumbers(offer_realm.getNumbers());
        offer.setOfferStatus(offer_realm.getOfferStatus());
        return offer;
    }

    public static List<Offer> convertRealmOfferListToOfferList(List<OfferRealm> offersRealm) {
        List<Offer> offerList = new ArrayList<>();
        for (int i = 0; i < offersRealm.size(); i++) {
            offerList.add(converOfferRealmToOffer(offersRealm.get(i)));
        }
        return offerList;
    }


}

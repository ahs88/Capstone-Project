
package shopon.com.shopon.datamodel.customer;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class CustomerData {

    @SerializedName("customers")
    @Expose
    public Customers customers;

    /**
     * 
     * @return
     *     The customers
     */
    public Customers getCustomers() {
        return customers;
    }

    /**
     * 
     * @param customers
     *     The customers
     */
    public void setCustomers(Customers customers) {
        this.customers = customers;
    }

    public void setRealmCustomer(CustomersRealm customersRealm){
        customers = new Customers();
        customers.setEmail(customersRealm.getEmail());
        customers.setId(customersRealm.getId());
        customers.setMobile(customersRealm.getMobile());
        customers.setName(customersRealm.getName());
        customers.setIntrestedIn(customersRealm.getIntrestedIn());

    }



}

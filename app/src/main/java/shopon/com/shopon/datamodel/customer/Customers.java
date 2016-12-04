
package shopon.com.shopon.datamodel.customer;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;


public class Customers {

    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("intrestedIn")
    @Expose
    private String intrestedIn;
    @SerializedName("merchentId")
    @Expose
    private Integer merchentId;
    @SerializedName("mobile")
    @Expose
    private String mobile;
    @SerializedName("name")
    @Expose
    private String name;

    @SerializedName("createdAt")
    @Expose
    private String createdAt;

    /**
     *
     * @return
     *     The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     *     The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     *     The id
     */
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     *     The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return
     *     The intrestedIn
     */
    public String getIntrestedIn() {
        return intrestedIn;
    }

    /**
     *
     * @param intrestedIn
     *     The intrestedIn
     */
    public void setIntrestedIn(String intrestedIn) {
        this.intrestedIn = intrestedIn;
    }

    /**
     *
     * @return
     *     The merchentId
     */
    public Integer getMerchentId() {
        return merchentId;
    }

    /**
     *
     * @param merchentId
     *     The merchentId
     */
    public void setMerchentId(Integer merchentId) {
        this.merchentId = merchentId;
    }

    /**
     *
     * @return
     *     The mobile
     */
    public String getMobile() {
        return mobile;
    }

    /**
     *
     * @param mobile
     *     The mobile
     */
    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    /**
     *
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }
}

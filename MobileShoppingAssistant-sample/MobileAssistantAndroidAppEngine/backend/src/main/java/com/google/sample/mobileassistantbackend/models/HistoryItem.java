package com.google.sample.mobileassistantbackend.models;

/**
 * Created by hooman on 01/05/16.
 */


import com.google.appengine.api.datastore.GeoPt;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;



/**
 * Place entity.
 */
@Entity
public class HistoryItem {


    /**
     * * Unique identifier of this Entity in the database.
     */
    @Id
    private Long id;

    /**
     * The user of the item.
     */
    private String userEmail;

    /**
     * The product name of the item.
     */
    private String productName;

    private Long productid;

    /**
     * The purchase date of the item.
     */
    private Date purchaseDate;

    /**
     * The purchase price of the item.
     */
    private long purchasePrice;

    /**
     * The Place of purchase of the item .
     */
    private String placeName;


    /**
     * Returns the unique identifier of this item.
     * @return the unique identifier associated to this item in the database.
     */
    public final Long getHisotryItemId() {
        return id;
    }

    /**
     * Sets the unique identifier of this item.
     * @param pHisotryItemId the identifier to set for this item.
     */
    public final void setHisotryItemId(final Long pHisotryItemId) {
        this.id = pHisotryItemId;
    }

    /**
     * Returns the user of the item.
     * @return the user of the item.
     */
    public final String getUserEmail() {
        return userEmail;
    }

    /**
     * Sets the user of the item.
     * @param pUserEmail the user to set for this item.
     */
    public final void setUserEmail(final String pUserEmail) {
        this.userEmail = pUserEmail;
    }

    /**
     * Returns the name of the item.
     * @return the name of the item.
     */
    public final String getProductName() {
        return productName;
    }

    /**
     * Sets the product of the item.
     * @param pProductName the product to set for this item.
     */
    public final void setProductName(final String pProductName) {
        this.productName = pProductName;
    }

    /**
     * Returns the name of the item.
     * @return the name of the item.
     */
    public final Long getProductid() {
        return productid;
    }

    /**
     * Sets the product of the item.
     * @param pProductid the product to set for this item.
     */
    public final void setProductid(final Long pProductid) {
        this.productid = pProductid;
    }



    /**
     * Returns the purchase date of the item.
     * @return the purchase date of the item.
     */
    public final Date getPurchaseDate() {
        return purchaseDate;
    }

    /**
     * Sets the purchase date of the item.
     * @param pPurchaseDate the purchase date to set for this item.
     */
    public final void setPurchaseDate(final Date pPurchaseDate) {
        this.purchaseDate = pPurchaseDate;
    }

    /**
     * Returns the purchase price of the item.
     * @return the purchase price of the item.
     */
    public final long getPurchasePrice() {
        return purchasePrice;
    }

    /**
     * Sets the purchase price of the item.
     * @param pPurchasePrice the purchase price to set for this item.
     */
    public final void setPurchasePrice(final long pPurchasePrice) {
        this.purchasePrice = pPurchasePrice;
    }

    /**
     * Returns the Place of the item.
     * @return the Place price of the item.
     */
    public final String getPlaceName() {
        return placeName;
    }

    /**
     * Sets the Place of the item.
     * @param pPlaceName the Place to set for this item.
     */
    public final void setPlaceName(final String pPlaceName) {
        this.placeName = pPlaceName;
    }


}

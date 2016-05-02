/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.sample.mobileassistantbackend.apis;

import com.google.api.server.spi.ServiceException;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiClass;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.config.Named;
import com.google.appengine.api.users.User;
import com.google.sample.mobileassistantbackend.Constants;
import com.google.sample.mobileassistantbackend.models.HistoryItem;
import com.google.sample.mobileassistantbackend.utils.EndpointUtil;


import java.util.List;
import java.util.logging.Logger;

import static com.google.sample.mobileassistantbackend.OfyService.ofy;

/**
 * Exposes REST API over HistoryItem resources.
 */
@Api(name = "shoppingAssistant", version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = Constants.API_OWNER,
                ownerName = Constants.API_OWNER,
                packagePath = Constants.API_PACKAGE_PATH
        )
)
@ApiClass(resource = "historyItems",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.IOS_CLIENT_ID,
                Constants.WEB_CLIENT_ID},
        audiences = {Constants.AUDIENCE_ID}
)
public class HistoryItemEndpoint {

    /**
     * Log output.
     */
    private static final Logger LOG = Logger
            .getLogger(HistoryItemEndpoint.class.getName());

    /**
     * Maximum number of historyItems to return.
     */
    private static final int MAXIMUM_NUMBER_HISTORY_ITEM = 10000;



    /**
     * Gets the entity having primary key id.
     * @param id the primary key of the java bean.
     * @param user the user that requested the entities.
     * @return The entity with primary key id.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(path="get_historyitem", httpMethod = "GET")
    public final HistoryItem getHistoryItem(@Named("id") final Long id, final User user) {
         //   throws ServiceException {
        //EndpointUtil.throwIfNotAdmin(user);

        return findHistoryItem(id);
    }

    @ApiMethod(path="get_user_history", httpMethod = "GET")
    public final List<HistoryItem> getUserHistory(final User user) {
         //   throws ServiceException {
       // EndpointUtil.throwIfNotAdmin(user);

        return searchUserHistory(user);
    }


    /**
     * Inserts the entity into App Engine datastore. It uses HTTP POST method.
     * @param historyItem the entity to be inserted.
     * @param user the user that requested to insert the entity.
     * @return The inserted entity.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(path="inset_historyitem", httpMethod = "POST")
    public final HistoryItem insertHistoryItem(final HistoryItem historyItem, final User user) {
            //throws
           // ServiceException {
        //EndpointUtil.throwIfNotAdmin(user);

        ofy().save().entity(historyItem).now();

        return historyItem;
    }

    @ApiMethod(path="upload_receipt", httpMethod = "POST")
    public final void uploadReceipt(@Named("text") final String text, final User user) {
        // throws
        //    ServiceException {
        //EndpointUtil.throwIfNotAdmin(user);
        // associate the text with the product database

        //ofy().save().entity().now();

    }





    /**
     * Updates an entity. It uses HTTP PUT method.
     * @param historyItem the entity to be updated.
     * @param user the user that requested the update to the entity.
     * @return The updated entity.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "PUT")
    public final HistoryItem updateHistoryItem(final HistoryItem historyItem, final User user) throws
            ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        ofy().save().entity(historyItem).now();

        return historyItem;
    }

    /**
     * Removes the entity with primary key id. It uses HTTP DELETE method.
     * @param id the primary key of the entity to be deleted.
     * @param user the user that requested the deletion of the entity.
     * @throws com.google.api.server.spi.ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "DELETE")
    public final void removeHistoryItem(@Named("id") final Long id, final User user)
            throws ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        HistoryItem historyItem = findHistoryItem(id);
        if (historyItem == null) {
            LOG.info(
                    "HistoryItem " + id + " not found, skipping deletion.");
            return;
        }
        ofy().delete().entity(historyItem).now();
    }

    /**
     * Searches an entity by ID.
     * @param id the historyItem ID to search
     * @return the HistoryItem associated to id
     */
    private HistoryItem findHistoryItem(final Long id) {
        return ofy().load().type(HistoryItem.class).id(id).now();
    }

    private List<HistoryItem> searchUserHistory(final User user) {
        return ofy().load().type(HistoryItem.class).filter("userEmail ==", user.getEmail()).order("-purchaseDate").list();
    }

}

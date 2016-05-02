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
import com.google.sample.mobileassistantbackend.models.History;
import com.google.sample.mobileassistantbackend.models.HistoryItem;
import com.google.sample.mobileassistantbackend.utils.EndpointUtil;

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
@ApiClass(resource = "history",
        clientIds = {
                Constants.ANDROID_CLIENT_ID,
                Constants.IOS_CLIENT_ID,
                Constants.WEB_CLIENT_ID},
        audiences = {Constants.AUDIENCE_ID}
)
public class HistoryEndpoint {

    /**
     * Log output.
     */
    private static final Logger LOG = Logger
            .getLogger(HistoryEndpoint.class.getName());

    /**
     * Maximum number of history to return.
     */
    private static final int MAXIMUM_NUMBER_HISTORY_ITEM = 10000;



    /**
     * Gets the entity having primary key id.
     * @param id the primary key of the java bean.
     * @param user the user that requested the entities.
     * @return The entity with primary key id.
     * @throws ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "GET")
    public final History getHistory(@Named("id") final Long id, final User user)
            throws ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        return findHistory(id);
    }

    /**
     * Inserts the entity into App Engine datastore. It uses HTTP POST method.
     * @param history the entity to be inserted.
     * @param user the user that requested to insert the entity.
     * @return The inserted entity.
     * @throws ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "POST")
    public final History insertHistory(final History history, final User user) throws
            ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        ofy().save().entity(history).now();

        return history;
    }

    /**
     * Updates an entity. It uses HTTP PUT method.
     * @param history the entity to be updated.
     * @param user the user that requested the update to the entity.
     * @return The updated entity.
     * @throws ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "PUT")
    public final History updateHistory(final History history, final User user) throws
            ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        ofy().save().entity(history).now();

        return history;
    }

    /**
     * Removes the entity with primary key id. It uses HTTP DELETE method.
     * @param id the primary key of the entity to be deleted.
     * @param user the user that requested the deletion of the entity.
     * @throws ServiceException if user is not
     * authorized
     */
    @ApiMethod(httpMethod = "DELETE")
    public final void removeHistory(@Named("id") final Long id, final User user)
            throws ServiceException {
        EndpointUtil.throwIfNotAdmin(user);

        History history = findHistory(id);
        if (history == null) {
            LOG.info(
                    "History " + id + " not found, skipping deletion.");
            return;
        }
        ofy().delete().entity(history).now();
    }

    /**
     * Searches an entity by ID.
     * @param id the history ID to search
     * @return the History associated to id
     */
    private History findHistory(final Long id) {
        return ofy().load().type(History.class).id(id).now();
    }
}

/*
 * Copyright 2009 Prime Technology.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.primefaces.examples.view;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import org.primefaces.examples.service.TwitterAPIService;
import org.primefaces.examples.service.TwitterService;
import org.primefaces.json.JSONArray;
import org.primefaces.json.JSONObject;
import org.primefaces.push.PushContext;
import org.primefaces.push.PushContextFactory;
import twitter4j.Status;

public class TwitterSearchView {

    private boolean active;
    private TwitterService twitterService = new TwitterAPIService();

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void start() {
        if(!active) {

            PushContext context = PushContextFactory.getDefault().getPushContext();
            
            context.schedule("/twitter", new Callable<String>() {
                
                private String results;

                public String call() throws Exception {
                    List<Status> tweets = twitterService.asyncSearch("I");
                    if (tweets != null) {
                        JSONObject jSONObject = new JSONObject();
                        JSONArray jSONArray = new JSONArray();
                        for (Status t : tweets) {
                            JSONObject j = new JSONObject();
                            j.put("from_user", t.getUser().getScreenName());
                            j.put("text", t.getText());
                            j.put("image", t.getUser().getMiniProfileImageURL());
                            jSONArray.put(j);
                        }
                        jSONObject.put("results", jSONArray);
                        String s = jSONObject.toString();

                        StringBuilder jsonBuilder = new StringBuilder();
                        jsonBuilder.append("{\"data\":").append(s).append("}");

                        results = jsonBuilder.toString();
                    }
                    return results;
                }
            }, 10, TimeUnit.SECONDS);

            active = true;
        }
    }
}

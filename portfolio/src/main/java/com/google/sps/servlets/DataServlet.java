// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that returns some example content. */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

  final int SC_NO_CONTENT = 204;

  @Override
  public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    for (Entity entity : results.asIterable()) {
      datastore.delete(entity.getKey());
    }
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    Query query = new Query("Comment");

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);

    List<String> comments = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      String comment = (String) entity.getProperty("comment");

      String language = request.getParameter("languages");

      if (language != null && !language.equals("original")) {
        Translate translate = TranslateOptions.getDefaultInstance().getService();
        Translation translation = translate.translate(comment, Translate.TranslateOption.targetLanguage(language));
        comment = translation.getTranslatedText();
      }

      comments.add(comment);
    }
    
    response.setContentType("application/json");
    response.getWriter().println(convertCommentsToJson(comments));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    /* Get the input from the form. */
    String comment = request.getParameter("text-input");
    if (comment != null) {
      Entity commentEntity = new Entity("Comment");
      commentEntity.setProperty("comment", comment);

      DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
      datastore.put(commentEntity);
    }
    
    response.sendRedirect("/index.html");
  }

  /* Convert comments ArrayList into Json. */
  private String convertCommentsToJson(List<String> comments) {
    
    int length = comments.size();

    String json = "[";
    for (int i = 0; i < length; i++) {
      json += "{\"comment\":\"";
      json += comments.get(i);
      json += "\"}";
      if (i < length - 1) {
        json += ",";
      }
    }
    json += "]";
    return json;
  }
}

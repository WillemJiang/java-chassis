/*
 * Copyright 2017 Huawei Technologies Co., Ltd
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.servicecomb.demo.jaxrs.tests.endpoints;

import static javax.ws.rs.core.MediaType.TEXT_PLAIN;

import io.servicecomb.provider.rest.common.RestSchema;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

@RestSchema(schemaId = "schemaFirst")
@Path("/schemaFirstJaxrs")
public class SchemaFirstJaxrsImpl extends SomeAbstractJaxrsRestEndpoint {

  @Path("/reduce")
  @GET
  public int reduce(@Context HttpServletRequest request) {
    int a = Integer.parseInt(request.getParameter("a"));
    int b = Integer.parseInt(request.getParameter("b"));
    return a - b;
  }

  @Path("/addstring")
  @DELETE
  @Produces(TEXT_PLAIN)
  public String addString(@QueryParam("s") String[] s) {
    String result = "";
    for (String x : s) {
      result += x;
    }
    return result;
  }
}

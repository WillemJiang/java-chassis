/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.servicecomb.core.invocation;

import java.util.Map;

import org.apache.servicecomb.core.Const;
import org.apache.servicecomb.core.Endpoint;
import org.apache.servicecomb.core.Invocation;
import org.apache.servicecomb.core.SCBEngine;
import org.apache.servicecomb.core.definition.InvocationRuntimeType;
import org.apache.servicecomb.core.definition.OperationMeta;
import org.apache.servicecomb.core.provider.consumer.ReferenceConfig;
import org.apache.servicecomb.serviceregistry.RegistrationManager;

public final class InvocationFactory {
  private InvocationFactory() {
  }

  private static String getMicroserviceName() {
    return RegistrationManager.INSTANCE.getMicroservice().getServiceName();
  }

  public static Invocation forConsumer(ReferenceConfig referenceConfig, OperationMeta operationMeta,
      InvocationRuntimeType invocationRuntimeType, Map<String, Object> swaggerArguments) {
    Invocation invocation = new Invocation(referenceConfig,
        operationMeta,
        invocationRuntimeType,
        swaggerArguments);
    invocation.addContext(Const.SRC_MICROSERVICE, getMicroserviceName());
    return invocation;
  }

  /*
   * transport server收到请求时，创建invocation
   */
  public static Invocation forProvider(Endpoint endpoint,
      OperationMeta operationMeta,
      Map<String, Object> swaggerArguments) {
    SCBEngine.getInstance().ensureStatusUp();
    return new Invocation(endpoint,
        operationMeta,
        swaggerArguments);
  }
}

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

package io.servicecomb.common.rest.codec.param;

import java.lang.reflect.Type;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import io.servicecomb.common.rest.codec.QueryProcessorFactory;
import io.servicecomb.common.rest.codec.QueryTypeEnum;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.QueryParameter;

public class QueryProcessorCreator implements ParamValueProcessorCreator {
  public static final String PARAMTYPE = "query";


  public QueryProcessorCreator() {
    ParamValueProcessorCreatorManager.INSTANCE.register(PARAMTYPE, this);
  }

  @Override
  public ParamValueProcessor create(Parameter parameter, Type genericParamType) {
    JavaType targetType = TypeFactory.defaultInstance().constructType(genericParamType);

    QueryProcessorFactory queryProcessorFactory = new QueryProcessorFactory(parameter.getName(), targetType);

    String collectionFormat = ((QueryParameter) parameter).getCollectionFormat();
    if (collectionFormat == null) {
      return queryProcessorFactory.creator(QueryTypeEnum.multi.value());
    }
    return queryProcessorFactory.creator(QueryTypeEnum.valueOf(collectionFormat).value());
 }
}
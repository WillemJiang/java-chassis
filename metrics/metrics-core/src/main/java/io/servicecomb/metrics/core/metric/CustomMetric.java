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

package io.servicecomb.metrics.core.metric;

import io.servicecomb.foundation.common.exceptions.ServiceCombException;
import rx.functions.Func0;

public class CustomMetric extends AbstractMetric {

  private final Func0<Number> getCallback;

  public CustomMetric(String name, Func0<Number> getCallback) {
    super(name);
    this.getCallback = getCallback;
  }

  @Override
  public void update(Number num) {
    throw new ServiceCombException("unable update custom metric");
  }

  @Override
  public Number get(String tag) {
    return getCallback.call();
  }
}

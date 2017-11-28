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
package io.servicecomb.authentication.provider;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.servicecomb.authentication.RSAAuthenticationToken;
import io.servicecomb.foundation.common.utils.RSAUtils;
import io.servicecomb.serviceregistry.api.Const;
import io.servicecomb.serviceregistry.api.registry.MicroserviceInstance;
import io.servicecomb.serviceregistry.cache.MicroserviceInstanceCache;

public class RSAProviderTokenManager  {

  private final static Logger LOGGER = LoggerFactory.getLogger(RSAProviderTokenManager.class);

  private Set<RSAAuthenticationToken> validatedToken = ConcurrentHashMap.newKeySet(1000);

  public boolean valid(String token) {
    try {
      RSAAuthenticationToken rsaToken = RSAAuthenticationToken.fromStr(token);
      if (null == rsaToken) {
        LOGGER.error("token format is error, perhaps you need to set auth handler at consumer");
        return false;
      }
      if (tokenExprired(rsaToken)) {
        LOGGER.error("token is expired");
        return false;
      }
      if (validatedToken.contains(rsaToken)) {
        LOGGER.info("found vaildate token in vaildate pool");
        return true;
      }
      
      String sign = rsaToken.getSign();
      String content = rsaToken.plainToken();
      String publicKey = getPublicKey(rsaToken.getInstanceId(), rsaToken.getServiceId());
      boolean verify = RSAUtils.verify(publicKey, sign, content);
      if (verify && !tokenExprired(rsaToken)) {
        validatedToken.add(rsaToken);
        return true;
      }
      
      LOGGER.error("token verify error");
      return false;

    } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException | SignatureException e) {
      LOGGER.error("verfiy error", e);
      return false;
    }
  }

  private boolean tokenExprired(RSAAuthenticationToken rsaToken) {
    long generateTime = rsaToken.getGenerateTime();
    long expired = generateTime + RSAAuthenticationToken.TOKEN_ACTIVE_TIME + 15 * 60 * 1000;
    long now = System.currentTimeMillis();
    return now > expired;
  }

  private String getPublicKey(String instanceId, String serviceId) {
    Optional<MicroserviceInstance> instances = Optional
        .ofNullable(MicroserviceInstanceCache.getOrCreate(serviceId, instanceId));
    if (instances.isPresent()) {
      return instances.map(MicroserviceInstance::getProperties)
          .map(properties -> properties.get(Const.INSTANCE_PUBKEY_PRO))
          .get();
    } else {
      LOGGER.error("not instance found {}-{}, maybe attack", instanceId, serviceId);
      return "";
    }
  }

}

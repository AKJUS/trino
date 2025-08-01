/*
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
package io.trino.plugin.iceberg.containers;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.airlift.log.Logger;
import io.trino.testing.containers.BaseTestContainer;
import org.testcontainers.containers.Network;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class NessieContainer
        extends BaseTestContainer
{
    private static final Logger log = Logger.get(NessieContainer.class);

    public static final String DEFAULT_IMAGE = "ghcr.io/projectnessie/nessie:0.104.3";
    public static final String DEFAULT_HOST_NAME = "nessie";
    public static final String VERSION_STORE_TYPE = "IN_MEMORY";

    public static final int PORT = 19121;

    public static final Map<String, String> DEFAULT_ENV_VARS = ImmutableMap.of(
            "QUARKUS_HTTP_PORT", String.valueOf(PORT),
            "NESSIE_VERSION_STORE_TYPE", VERSION_STORE_TYPE);

    public static Builder builder()
    {
        return new Builder();
    }

    private NessieContainer(
            String image,
            String hostName,
            Set<Integer> exposePorts,
            Map<String, String> filesToMount,
            Map<String, String> envVars,
            Optional<Network> network,
            int retryLimit)
    {
        super(image, hostName, exposePorts, filesToMount, envVars, network, retryLimit);
    }

    @Override
    public void start()
    {
        super.start();
        log.info("Nessie server container started with address for REST API: %s", getRestApiUri());
    }

    public String getRestApiUri()
    {
        return "http://" + getMappedHostAndPortForExposedPort(PORT) + "/api/v2";
    }

    public static class Builder
            extends BaseTestContainer.Builder<NessieContainer.Builder, NessieContainer>
    {
        private Builder()
        {
            this.image = DEFAULT_IMAGE;
            this.hostName = DEFAULT_HOST_NAME;
            this.exposePorts = ImmutableSet.of(PORT);
            this.envVars = DEFAULT_ENV_VARS;
        }

        @Override
        public NessieContainer build()
        {
            return new NessieContainer(image, hostName, exposePorts, filesToMount, envVars, network, startupRetryLimit);
        }
    }
}

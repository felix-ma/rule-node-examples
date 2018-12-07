/**
 * Copyright © 2018 The Thingsboard Authors
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
package org.thingsboard.rule.engine.node.enrichment;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.thingsboard.rule.engine.api.*;
import org.thingsboard.rule.engine.api.util.TbNodeUtils;
import org.thingsboard.server.common.data.plugin.ComponentType;
import org.thingsboard.server.common.msg.TbMsg;

import java.util.concurrent.ExecutionException;

import static org.thingsboard.rule.engine.api.TbRelationTypes.SUCCESS;

/**
 * Created by mshvayka on 10.08.18.
 */
@RuleNode(
        type = ComponentType.ENRICHMENT,
        name = "get EntityId",
        configClazz = TbGetEntityIdConfiguration.class,
        nodeDescription = "获取当前消息的EntityId，如果是设备的话就是DeviceId",
        nodeDetails = "如果outputkey设置的是EntityId，那么此节点会将数据存储到metadata.EntityId中，通过script节点可以改变数据到msg下",
        uiResources = {"static/rulenode/custom-nodes-config.js"},
        configDirective = "tbEnrichmentNodeGetEntityIdConfig")
public class TbGetEntityId implements TbNode {

    private TbGetEntityIdConfiguration config;
    private String outputKey;

    @Override
    public void init(TbContext ctx, TbNodeConfiguration configuration) throws TbNodeException {
        this.config = TbNodeUtils.convert(configuration, TbGetEntityIdConfiguration.class);
        outputKey = config.getOutputKey();
    }


    @Override
    public void onMsg(TbContext ctx, TbMsg msg) throws ExecutionException, InterruptedException, TbNodeException {
        try {
            msg.getMetaData().putValue(outputKey, msg.getOriginator().getId().toString());
            msg = ctx.newMsg(msg.getType(), msg.getOriginator(), msg.getMetaData(), msg.getData());
            ctx.tellNext(msg, SUCCESS);
        } catch (Exception e) {
            ctx.tellFailure(msg, e);
        }
    }

    @Override
    public void destroy() {

    }
}

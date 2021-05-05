/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.druid.timeline.partition;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

public class BloomFilterStreamFanOutHashBasedNumberedShardSpecFactory implements ShardSpecFactory
{
  private static final BloomFilterStreamFanOutHashBasedNumberedShardSpecFactory
      INSTANCE = new BloomFilterStreamFanOutHashBasedNumberedShardSpecFactory();
  @JsonIgnore
  private final List<String> partitionDimensions;
  @JsonIgnore
  private final Set<Integer> streamPartitionIds;
  @JsonIgnore
  private final Integer streamPartitions;
  @JsonIgnore
  private final Integer fanOutSize;

  public static BloomFilterStreamFanOutHashBasedNumberedShardSpecFactory instance()
  {
    return INSTANCE;
  }

  private BloomFilterStreamFanOutHashBasedNumberedShardSpecFactory()
  {
    this.partitionDimensions = null;
    this.streamPartitionIds = null;
    this.streamPartitions = null;
    this.fanOutSize = null;
  }

  public BloomFilterStreamFanOutHashBasedNumberedShardSpecFactory(
      @JsonProperty("partitionDimensions") @Nullable List<String> partitionDimensions,
      @JsonProperty("streamPartitionIds") @Nullable Set<Integer> streamPartitionIds,
      @JsonProperty("streamPartitions") @Nullable Integer streamPartitions,
      @JsonProperty("fanOutSize") @Nullable Integer fanOutSize
  )
  {
    this.partitionDimensions = partitionDimensions;
    this.streamPartitionIds = streamPartitionIds;
    this.streamPartitions = streamPartitions;
    this.fanOutSize = fanOutSize;
  }

  @JsonProperty("partitionDimensions")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public List<String> getPartitionDimensions()
  {
    return partitionDimensions;
  }

  @JsonProperty("streamPartitionIds")
  @JsonInclude(JsonInclude.Include.NON_EMPTY)
  public Set<Integer> getStreamPartitionIds()
  {
    return streamPartitionIds;
  }

  @JsonProperty("streamPartitions")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Integer getStreamPartitions()
  {
    return streamPartitions;
  }

  @JsonProperty("fanOutSize")
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public Integer getFanOutSize()
  {
    return fanOutSize;
  }

  @Override
  public ShardSpec create(ObjectMapper objectMapper, @Nullable ShardSpec specOfPreviousMaxPartitionId)
  {
    if (specOfPreviousMaxPartitionId == null) {
      return new BloomFilterStreamFanOutHashBasedNumberedShardSpec(
          0,
          0,
          partitionDimensions,
          streamPartitionIds,
          streamPartitions,
          fanOutSize,
          objectMapper
      );
    } else {
      final NumberedShardSpec prevSpec = (NumberedShardSpec) specOfPreviousMaxPartitionId;
      return new BloomFilterStreamFanOutHashBasedNumberedShardSpec(
          prevSpec.getPartitionNum() + 1,
          prevSpec.getPartitions(),
          partitionDimensions,
          streamPartitionIds,
          streamPartitions,
          fanOutSize,
          objectMapper
      );
    }
  }

  @Override
  public ShardSpec create(ObjectMapper objectMapper, int partitionId)
  {
    return new BloomFilterStreamFanOutHashBasedNumberedShardSpec(
        partitionId,
        0,
        partitionDimensions,
        streamPartitionIds,
        streamPartitions,
        fanOutSize,
        objectMapper
    );
  }

  @Override
  public Class<? extends ShardSpec> getShardSpecClass()
  {
    return BloomFilterStreamFanOutHashBasedNumberedShardSpec.class;
  }
}

/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.search.aggregations.metrics.cardinality;

import org.elasticsearch.common.ParseField;
import org.elasticsearch.common.ParseFieldMatcher;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentParser.Token;
import org.elasticsearch.search.aggregations.AggregatorFactory;
import org.elasticsearch.search.aggregations.support.AbstractValuesSourceParser.AnyValuesSourceParser;
import org.elasticsearch.search.aggregations.support.ValueType;
import org.elasticsearch.search.aggregations.support.ValuesSourceType;

import java.io.IOException;
import java.util.Map;


public class CardinalityParser extends AnyValuesSourceParser {

    private static final ParseField REHASH = new ParseField("rehash").withAllDeprecated("no replacement - values will always be rehashed");

    public CardinalityParser() {
        super(true, false);
    }

    @Override
    public String type() {
        return InternalCardinality.TYPE.name();
    }

    @Override
    protected CardinalityAggregatorFactory createFactory(String aggregationName, ValuesSourceType valuesSourceType,
            ValueType targetValueType, Map<ParseField, Object> otherOptions) {
        CardinalityAggregatorFactory factory = new CardinalityAggregatorFactory(aggregationName, targetValueType);
        Long precisionThreshold = (Long) otherOptions.get(CardinalityAggregatorFactory.PRECISION_THRESHOLD_FIELD);
        if (precisionThreshold != null) {
            factory.precisionThreshold(precisionThreshold);
        }
        return factory;
    }

    @Override
    protected boolean token(String aggregationName, String currentFieldName, Token token, XContentParser parser,
            ParseFieldMatcher parseFieldMatcher, Map<ParseField, Object> otherOptions) throws IOException {
        if (token.isValue()) {
            if (parseFieldMatcher.match(currentFieldName, CardinalityAggregatorFactory.PRECISION_THRESHOLD_FIELD)) {
                otherOptions.put(CardinalityAggregatorFactory.PRECISION_THRESHOLD_FIELD, parser.longValue());
                return true;
            } else if (parseFieldMatcher.match(currentFieldName, REHASH)) {
                // ignore
                return true;
            }
        }
        return false;
    }

    @Override
    public AggregatorFactory[] getFactoryPrototypes() {
        return new AggregatorFactory[] { new CardinalityAggregatorFactory(null, null) };
    }
}

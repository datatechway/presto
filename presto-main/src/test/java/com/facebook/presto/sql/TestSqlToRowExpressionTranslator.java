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
package com.facebook.presto.sql;

import com.facebook.presto.block.BlockEncodingManager;
import com.facebook.presto.metadata.FunctionRegistry;
import com.facebook.presto.spi.type.Type;
import com.facebook.presto.spi.type.TypeManager;
import com.facebook.presto.sql.analyzer.FeaturesConfig;
import com.facebook.presto.sql.relational.SqlToRowExpressionTranslator;
import com.facebook.presto.sql.tree.CoalesceExpression;
import com.facebook.presto.sql.tree.Expression;
import com.facebook.presto.sql.tree.LongLiteral;
import com.facebook.presto.type.TypeRegistry;
import com.facebook.presto.util.maps.IdentityLinkedHashMap;
import org.testng.annotations.Test;

import static com.facebook.presto.SessionTestUtils.TEST_SESSION;
import static com.facebook.presto.metadata.FunctionKind.SCALAR;
import static com.facebook.presto.spi.type.BigintType.BIGINT;

public class TestSqlToRowExpressionTranslator
{
    @Test(timeOut = 10_000)
    public void testPossibleExponentialOptimizationTime()
    {
        TypeManager typeManager = new TypeRegistry();
        FunctionRegistry functionRegistry = new FunctionRegistry(typeManager, new BlockEncodingManager(typeManager), new FeaturesConfig());

        Expression expression = new LongLiteral("1");
        IdentityLinkedHashMap<Expression, Type> types = new IdentityLinkedHashMap<>();
        types.put(expression, BIGINT);
        for (int i = 0; i < 100; i++) {
            expression = new CoalesceExpression(expression);
            types.put(expression, BIGINT);
        }
        SqlToRowExpressionTranslator.translate(expression, SCALAR, types, functionRegistry, typeManager, TEST_SESSION, true);
    }
}

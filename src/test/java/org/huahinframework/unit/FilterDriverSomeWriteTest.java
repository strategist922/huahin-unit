/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.huahinframework.unit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.huahinframework.core.Filter;
import org.huahinframework.core.Writer;
import org.huahinframework.core.io.Record;
import org.huahinframework.core.util.StringUtil;
import org.huahinframework.unit.FilterDriver;
import org.junit.Test;

/**
 *
 */
public class FilterDriverSomeWriteTest extends FilterDriver {
    private static final String LABEL_COLUMN = "COLUMN";
    private static final String LABEL_VALUE = "VALUE";

    private static final String COLUMN_A = "A";
    private static final String COLUMN_B = "B";

    private static final String[] LABELS = new String[] { LABEL_COLUMN, LABEL_VALUE };

    private static class TestFilter extends Filter {
        @Override
        public void filter(Record record, Writer writer)
                throws IOException, InterruptedException {
            String column = record.getValueString(LABEL_COLUMN);
            if (!column.equals(COLUMN_A)) {
                return;
            }

            for (int i = 0; i < 3; i++) {
                Record emitRecord = new Record();
                emitRecord.addGrouping(LABEL_COLUMN, column);
                emitRecord.addValue(LABEL_VALUE,
                                    Integer.valueOf(record.getValueString(LABEL_VALUE)) + i);
                writer.write(emitRecord);
            }
        }

        @Override
        public void filterSetup() {
        }

        @Override
        public void init() {
        }
    }

    @Test
    public void testFirstHit() {
        String input = COLUMN_A + StringUtil.TAB + 1;

        List<Record> output = new ArrayList<Record>();
        for (int i = 0; i < 3; i++) {
            Record r = new Record();
            r.addGrouping(LABEL_COLUMN, COLUMN_A);
            r.addValue(LABEL_VALUE, 1 + i);
            output.add(r);
        }

        run(LABELS, StringUtil.TAB, false, input, output);
    }

    @Test
    public void testFirstNotHit() {
        String input = COLUMN_B + StringUtil.TAB + 1;
        run(LABELS, StringUtil.TAB, false, input, null);
    }

    @Test
    public void testSecondHit() {
        Record input = new Record();
        input.addValue(LABEL_COLUMN, COLUMN_A);
        input.addValue(LABEL_VALUE, "1");

        List<Record> output = new ArrayList<Record>();
        for (int i = 0; i < 3; i++) {
            Record r = new Record();
            r.addGrouping(LABEL_COLUMN, COLUMN_A);
            r.addValue(LABEL_VALUE, 1 + i);
            output.add(r);
        }

        run(input, output);
    }

    @Test
    public void testSecondNotHit() {
        Record input = new Record();
        input.addValue(LABEL_COLUMN, COLUMN_B);
        input.addValue(LABEL_VALUE, "1");

        run(input, null);
    }

    @Override
    public Filter getFilter() {
        return new TestFilter();
    }
}
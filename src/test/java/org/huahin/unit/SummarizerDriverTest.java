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
package org.huahin.unit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.huahin.core.Summarizer;
import org.huahin.core.Writer;
import org.huahin.core.io.Record;
import org.junit.Test;

/**
 *
 */
public class SummarizerDriverTest extends SummarizerDriver {
    private static final String LABEL_COLUMN = "COLUMN";
    private static final String LABEL_VALUE = "VALUE";

    private static final String COLUMN_A = "A";

    private static class TestEndSummarizer extends Summarizer {
        private int count;

        @Override
        public void init() {
            count = 0;
        }

        @Override
        public boolean summarizer(Record record, Writer writer)
                throws IOException, InterruptedException {
            count += record.getValueInteger(LABEL_VALUE);
            return false;
        }

        @Override
        public void end(Record record, Writer writer)
                throws IOException, InterruptedException {
            Record emitRecord = new Record();
            emitRecord.addValue(LABEL_VALUE, count);
            writer.write(emitRecord);
        }

        @Override
        public void summarizerSetup() {
        }
    }

    @Test
    public void test() {
        List<Record> input = new ArrayList<Record>();
        for (int i = 1; i <= 5; i++) {
            Record r = new Record();
            r.addGrouping(LABEL_COLUMN, COLUMN_A);
            r.addValue(LABEL_VALUE, i * (i + 1));
            input.add(r);
        }

        Record output = new Record();
        output.addGrouping(LABEL_COLUMN, COLUMN_A);
        output.addValue(LABEL_VALUE, 70);

        run(input, Arrays.asList(output));
    }

    @Override
    public Summarizer getSummarizer() {
        return new TestEndSummarizer();
    }
}
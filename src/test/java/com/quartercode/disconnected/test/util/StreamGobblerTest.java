/*
 * This file is part of Disconnected.
 * Copyright (c) 2013 QuarterCode <http://www.quartercode.com/>
 *
 * Disconnected is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Disconnected is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Disconnected. If not, see <http://www.gnu.org/licenses/>.
 */

package com.quartercode.disconnected.test.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import com.quartercode.disconnected.util.StreamGobbler;

@RunWith (Parameterized.class)
public class StreamGobblerTest {

    @Parameters
    public static Collection<Object[]> getData() {

        List<Object[]> data = new ArrayList<Object[]>();
        data.add(new Object[] { "SomeInput", null });
        data.add(new Object[] { "-:_.,+#'2", null });
        data.add(new Object[] { "Input with prefix", "> " });
        return data;
    }

    private final String input;
    private final String prefix;

    public StreamGobblerTest(String input, String prefix) {

        this.input = input;
        this.prefix = prefix;
    }

    @Test
    public void test() throws InterruptedException {

        InputStream inputStream = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        StreamGobbler gobbler = new StreamGobbler(prefix, inputStream, new PrintStream(outputStream));
        gobbler.start();
        gobbler.join();
        Assert.assertEquals("Input equals output", (prefix == null ? "" : prefix) + input + System.getProperty("line.separator"), outputStream.toString());
    }

}

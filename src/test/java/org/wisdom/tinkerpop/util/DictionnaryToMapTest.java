package org.wisdom.tinkerpop.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Map;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.collections.Sets;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class DictionnaryToMapTest
{
    private Dictionary<Integer, Integer> input;

    private DictionnaryToMap<Integer, Integer> cut;

    @Before
    public void setUp()
    {
        input = new Hashtable<>();
        IntStream.range(0,5).forEach(i -> input.put(i, i+1));
        cut = new DictionnaryToMap<>(input);
    }

    @Test
    public void testSize() throws Exception
    {
        assertEquals(input.size(), cut.size());
    }

    @Test
    public void testIsEmpty() throws Exception
    {
        assertEquals(input.isEmpty(), cut.isEmpty());
    }

    @Test
    public void testContainsKey() throws Exception
    {
        Enumeration<Integer> keys = input.keys();
        while (keys.hasMoreElements())
        {
            assertTrue(cut.containsKey(keys.nextElement()));
        }
    }

    @Test
    public void testContainsValue() throws Exception
    {
        Enumeration<Integer> values = input.elements();
        while (values.hasMoreElements())
        {
            assertTrue(cut.containsValue(values.nextElement()));
        }
    }

    @Test
    public void testGet() throws Exception
    {
        Enumeration<Integer> keys = input.keys();
        while (keys.hasMoreElements())
        {
            Integer key = keys.nextElement();
            assertEquals(input.get(key), cut.get(key));
        }
    }

    @Test
    public void testPut() throws Exception
    {
        int size = cut.size();
        Integer put = 18;
        assertEquals(null, cut.put(put, put));
        assertEquals(size+1, cut.size());

        assertEquals(put, cut.put(put, put));
        assertEquals(size+1, cut.size());
    }

    @Test
    public void testRemove() throws Exception
    {
        int size = cut.size();
        Integer removed = 18;
        assertEquals(false, cut.remove(removed, removed));
        assertEquals(size, cut.size());

        cut.put(removed, removed);
        assertEquals(true, cut.remove(removed, removed));
        assertEquals(size, cut.size());
    }

    @Test
    public void testPutAll() throws Exception
    {
        int size = cut.size();
        Map<Integer, Integer> put = Maps.asMap(Sets.newSet(7, 8, 9), new Function<Integer, Integer>()
        {
            @Nullable
            @Override
            public Integer apply(@Nullable final Integer input)
            {
                return input;
            }
        });
        cut.putAll(put);
        assertEquals(size+put.size(), cut.size());
    }

    @Test
    public void testClear() throws Exception
    {
        cut.clear();
        assertTrue(cut.size() == 0);
    }

    @Test
    public void testKeySet() throws Exception
    {
        assertThat(cut.keySet()).containsExactlyElementsOf(Collections.list(input.keys()));
    }

    @Test
    public void testValues() throws Exception
    {
        assertThat(cut.values()).containsExactlyElementsOf(Collections.list(input.elements()));
    }

    @Test
    public void testEntrySet() throws Exception
    {
        cut.entrySet().stream().forEach(e -> assertEquals(input.get(e.getKey()), e.getValue()));
    }
}
package org.wisdom.tinkerpop.util;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

/**
 * Created by gdelafosse on 23/12/15.
 */
public class DictionnaryToMap<K, V> implements Map<K, V>
{
    private Dictionary<K, V> dictionary;

    public DictionnaryToMap(Dictionary<K, V> dictionary)
    {
        this.dictionary = dictionary;
    }

    @Override
    public int size()
    {
        return dictionary.size();
    }

    @Override
    public boolean isEmpty()
    {
        return dictionary.isEmpty();
    }

    @Override
    public boolean containsKey(final Object key)
    {
        return dictionary.get(key) != null;
    }

    @Override
    public boolean containsValue(final Object value)
    {
       return Collections.list(dictionary.elements()).stream().anyMatch(v -> v.equals(value));
    }

    @Override
    public V get(final Object key)
    {
        return dictionary.get(key);
    }

    @Override
    public V put(final K key, final V value)
    {
        return dictionary.put(key, value);
    }

    @Override
    public V remove(final Object key)
    {
        return dictionary.remove(key);
    }

    @Override
    public void putAll(final Map<? extends K, ? extends V> m)
    {
        m.keySet().stream().forEach(k -> dictionary.put(k, m.get(k)));
    }

    @Override
    public void clear()
    {
        Collections.list(dictionary.keys()).stream().forEach(k -> dictionary.remove(k));
    }

    @Override
    public Set<K> keySet()
    {
        return ImmutableSet.copyOf(Collections.list(dictionary.keys()));
    }

    @Override
    public Collection<V> values()
    {
        return ImmutableSet.copyOf(Collections.list(dictionary.elements()));
    }

    @Override
    public Set<Entry<K, V>> entrySet()
    {
        return Collections.list(dictionary.keys()).stream()
                .map(k -> new Entry<K,V>()
                {
                    @Override
                    public K getKey()
                    {
                        return k;
                    }

                    @Override
                    public V getValue()
                    {
                        return dictionary.get(k);
                    }

                    @Override
                    public V setValue(final V value)
                    {
                        return dictionary.put(k, value);
                    }
                })
                .collect(Collectors.toSet());
    }
}

/*
 **********************************************************************
 * Copyright (c) 2017, 2020 Contributors to the Eclipse Foundation
 *               2010, 2013 Coda Hale, Yammer.com
 *
 * See the NOTICES file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 **********************************************************************/
package org.eclipse.microprofile.metrics;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * The registry that stores metrics and their metadata.
 * The MetricRegistry provides methods to register, create and retrieve metrics and their respective metadata.
 *
 *
 * @see MetricFilter
 */
public interface MetricRegistry {

    /**
     * An enumeration representing the scopes of the MetricRegistry
     */
    enum Type {
        /**
         * The Application (default) scoped MetricRegistry.
         * Any metric registered/accessed via CDI will use this MetricRegistry.
         */
        APPLICATION("application"),

        /**
         * The Base scoped MetricRegistry.
         * This MetricRegistry will contain required metrics specified in the MicroProfile Metrics specification.
         */
        BASE("base"),

        /**
         * The Vendor scoped MetricRegistry.
         * This MetricRegistry will contain vendor provided metrics which may vary between different vendors.
         */
        VENDOR("vendor");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        /**
         * Returns the name of the MetricRegistry scope.
         *
         * @return the scope
         */
        public String getName() {
            return name;
        }
    }

    /**
     * Concatenates elements to form a dotted name, eliding any null values or empty strings.
     *
     * @param name the first element of the name
     * @param names the remaining elements of the name
     * @return {@code name} and {@code names} concatenated by periods
     */
    static String name(String name, String... names) {
        List<String> ns = new ArrayList<>();
        ns.add(name);
        ns.addAll(asList(names));
        return ns.stream().filter(part -> part != null && !part.isEmpty()).collect(joining("."));
    }

    /**
     * Concatenates a class name and elements to form a dotted name, eliding any null values or
     * empty strings.
     *
     * @param klass the first element of the name
     * @param names the remaining elements of the name
     * @return {@code klass} and {@code names} concatenated by periods
     */
    static String name(Class<?> klass, String... names) {
        return name(klass.getCanonicalName(), names);
    }

    /**
     * Given a {@link Metric}, registers it under a {@link MetricID} with the given name and with no tags.
     * A {@link Metadata} object will be registered with the name and type.
     * However, if a {@link Metadata} object is already registered with this metric name and is not equal
     * to the created {@link Metadata} object then an exception will be thrown.
     *
     * @param name the name of the metric
     * @param metric the metric
     * @param <T> the type of the metric
     * @return {@code metric}
     * @throws IllegalArgumentException if the name is already registered or if Metadata with different
     *             values has already been registered with the name
     */
    <T extends Metric> T register(String name, T metric) throws IllegalArgumentException;

    /**
     * Given a {@link Metric} and {@link Metadata}, registers the metric with a {@link MetricID} with the
     * name provided by the {@link Metadata} and with no tags.
     * <p>
     * Note: If a {@link Metadata} object is already registered under this metric name and is not equal
     * to the provided {@link Metadata} object then an exception will be thrown.
     * </p>
     *
     * @param metadata the metadata
     * @param metric the metric
     * @param <T> the type of the metric
     * @return {@code metric}
     * @throws IllegalArgumentException if the name is already registered or if Metadata with different
     *             values has already been registered with the name
     *
     * @since 1.1
     */
    <T extends Metric> T register(Metadata metadata, T metric) throws IllegalArgumentException;

    /**
     * Given a {@link Metric} and {@link Metadata}, registers both under a {@link MetricID} with the
     * name provided by the {@link Metadata} and with the provided {@link Tag}s.
     * <p>
     * Note: If a {@link Metadata} object is already registered under this metric name and is not equal
     * to the provided {@link Metadata} object then an exception will be thrown.
     * </p>
     *
     * @param metadata the metadata
     * @param metric the metric
     * @param <T> the type of the metric
     * @param tags the tags of the metric
     * @return {@code metric}
     * @throws IllegalArgumentException if the name is already registered or if Metadata with different
     *             values has already been registered with the name
     *
     * @since 2.0
     */
    <T extends Metric> T register(Metadata metadata, T metric, Tag... tags) throws IllegalArgumentException;

    /**
     * Return the {@link Counter} registered under the {@link MetricID} with this name and with no tags;
     * or create and register a new {@link Counter} if none is registered.
     *
     * If a {@link Counter} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Counter}
     */
    default Counter counter(String name) {
        return counter(new MetricID(name));
    }

    /**
     * Return the {@link Counter} registered under the {@link MetricID} with this name and with the provided
     * {@link Tag}s; or create and register a new {@link Counter} if none is registered.
     *
     * If a {@link Counter} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param name the name of the metric
     * @param tags the tags of the metric
     * @return a new or pre-existing {@link Counter}
     *
     * @since 2.0
     */
    default Counter counter(String name, Tag... tags) {
        return counter(new MetricID(name, tags));
    }

    /**
     * Return the {@link Counter} registered under the {@link MetricID};
     * or create and register a new {@link Counter} if none is registered.
     *
     * If a {@link Counter} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param metricID the ID of the metric
     * @return a new or pre-existing {@link Counter}
     *
     * @since 3.0
     */
    Counter counter(MetricID metricID);

    /**
     * Return the {@link Counter} registered under the {@link MetricID} with the {@link Metadata}'s name and
     * with no tags; or create and register a new {@link Counter} if none is registered. If a {@link Counter}
     * was created, the provided {@link Metadata} object will be registered.
     * <p>
     * Note: During retrieval or creation, if a {@link Metadata} object is already registered under this
     * metric name and is not equal to the provided {@link Metadata} object then an exception will be thrown.
     * </p>
     *
     * @param metadata the name of the metric
     * @return a new or pre-existing {@link Counter}
     */
    Counter counter(Metadata metadata);

    /**
     * Return the {@link Counter} registered under the {@link MetricID} with the {@link Metadata}'s name and
     * with the provided {@link Tag}s; or create and register a new {@link Counter} if none is registered.
     * If a {@link Counter} was created, the provided {@link Metadata} object will be registered.
     * <p>
     * Note: During retrieval or creation, if a {@link Metadata} object is already registered under this
     * metric name and is not equal to the provided {@link Metadata} object then an exception will be thrown.
     * </p>
     *
     * @param metadata the name of the metric
     * @param tags the tags of the metric
     * @return a new or pre-existing {@link Counter}
     *
     * @since 2.0
     */
    Counter counter(Metadata metadata, Tag... tags);

    /**
     * Return the {@link ConcurrentGauge} registered under the {@link MetricID} with this name; or create and register
     * a new {@link ConcurrentGauge} if none is registered.
     * If a {@link ConcurrentGauge} was created, a {@link Metadata} object will be registered with the name and type.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link ConcurrentGauge}
     */
    default ConcurrentGauge concurrentGauge(String name) {
        return concurrentGauge(new MetricID(name));
    }

    /**
     * Return the {@link ConcurrentGauge} registered under the {@link MetricID} with this name and
     * with the provided {@link Tag}s; or create and register a new {@link ConcurrentGauge} if none is registered.
     * If a {@link ConcurrentGauge} was created, a {@link Metadata} object will be registered with the name and type.
     *
     * @param name the name of the metric
     * @param tags the tags of the metric
     * @return a new or pre-existing {@link ConcurrentGauge}
     */
    default ConcurrentGauge concurrentGauge(String name, Tag... tags) {
        return concurrentGauge(new MetricID(name, tags));
    }

    /**
     * Return the {@link ConcurrentGauge} registered under the {@link MetricID}; or create and register
     * a new {@link ConcurrentGauge} if none is registered.
     * If a {@link ConcurrentGauge} was created, a {@link Metadata} object will be registered with the name and type.
     *
     * @param metricID the ID of the metric
     * @return a new or pre-existing {@link ConcurrentGauge}
     *
     * @since 3.0
     */
    ConcurrentGauge concurrentGauge(MetricID metricID);

    /**
     * Return the {@link ConcurrentGauge} registered under the {@link MetricID} with the {@link Metadata}'s name;
     * or create and register a new {@link ConcurrentGauge} if none is registered.
     * If a {@link ConcurrentGauge} was created, the provided {@link Metadata} object will be registered.
     * <p>
     * Note: During retrieval or creation, if a {@link Metadata} object is already registered under
     * this metric name and is not equal to the provided {@link Metadata} object then an exception
     * will be thrown.
     * </p>
     *
     * @param metadata the name of the metric
     * @return a new or pre-existing {@link ConcurrentGauge}
     */
    ConcurrentGauge concurrentGauge(Metadata metadata);

    /**
     * Return the {@link ConcurrentGauge} registered under the {@link MetricID} with the {@link Metadata}'s name and
     * with the provided {@link Tag}s; or create and register a new {@link ConcurrentGauge} if none is registered.
     * If a {@link ConcurrentGauge} was created, the provided {@link Metadata} object will be registered.
     * <p>
     * Note: During retrieval or creation, if a {@link Metadata} object is already registered under
     * this metric name and is not equal to the provided {@link Metadata} object then an exception
     * will be thrown.
     * </p>
     *
     * @param metadata the name of the metric
     * @param tags the tags of the metric
     * @return a new or pre-existing {@link ConcurrentGauge}
     */
    ConcurrentGauge concurrentGauge(Metadata metadata, Tag... tags);

    /**
     * Return the {@link Gauge} registered under the {@link MetricID} with this name and with no tags;
     * or create and register this gauge if none is registered.
     *
     * If a {@link Gauge} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param name the name of the metric
     * @param gauge the {@link Gauge} to use if none is registered already
     * @return the pre-existing or provided {@link Gauge}
     *
     * @since 3.0
     */
    default Gauge<?> gauge(String name, Gauge<?> gauge) {
        return gauge(new MetricID(name), gauge);
    }

    /**
     * Return the {@link Gauge} registered under the {@link MetricID} with this name and with the
     * provided {@link Tag}s; or create and register this gauge if none is registered.
     *
     * If a {@link Gauge} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param name the name of the metric
     * @param gauge the {@link Gauge} to use if none is registered already
     * @return the pre-existing or provided {@link Gauge}
     *
     * @since 3.0
     */
    default Gauge<?> gauge(String name, Gauge<?> gauge, Tag...tags) {
        return gauge(new MetricID(name, tags), gauge);
    }

    /**
     * Return the {@link Gauge} registered under the {@link MetricID}; or create and register this
     * gauge if none is registered.
     *
     * If a {@link Gauge} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param metricID the ID of the metric
     * @param gauge the {@link Gauge} to use if none is registered already
     * @return the pre-existing or provided {@link Gauge}
     *
     * @since 3.0
     */
    Gauge<?> gauge(MetricID metricID, Gauge<?> gauge);

    /**
     * Return the {@link Histogram} registered under the {@link MetricID} with this name and with no tags;
     * or create and register a new {@link Histogram} if none is registered.
     *
     * If a {@link Histogram} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Histogram}
     */
    default Histogram histogram(String name) {
        return histogram(new MetricID(name));
    }

    /**
     * Return the {@link Histogram} registered under the {@link MetricID} with this name and with the
     * provided {@link Tag}s; or create and register a new {@link Histogram} if none is registered.
     *
     * If a {@link Histogram} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param name the name of the metric
     * @param tags the tags of the metric
     * @return a new or pre-existing {@link Histogram}
     *
     * @since 2.0
     */
    default Histogram histogram(String name, Tag... tags) {
        return histogram(new MetricID(name, tags));
    }

    /**
     * Return the {@link Histogram} registered under the {@link MetricID};
     * or create and register a new {@link Histogram} if none is registered.
     *
     * If a {@link Histogram} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param metricID the ID of the metric
     * @return a new or pre-existing {@link Histogram}
     *
     * @since 3.0
     */
    Histogram histogram(MetricID metricID);

    /**
     * Return the {@link Histogram} registered under the {@link MetricID} with the {@link Metadata}'s
     * name and with no tags; or create and register a new {@link Histogram} if none is registered.
     * If a {@link Histogram} was created, the provided {@link Metadata} object will be registered.
     * <p>
     * Note: During retrieval or creation, if a {@link Metadata} object is already registered under
     * this metric name and is not equal to the provided {@link Metadata} object then an exception
     * will be thrown.
     * </p>
     *
     * @param metadata the name of the metric
     * @return a new or pre-existing {@link Histogram}
     */
    Histogram histogram(Metadata metadata);

    /**
     * Return the {@link Histogram} registered under the {@link MetricID} with the {@link Metadata}'s
     * name and with the provided {@link Tag}s; or create and register a new {@link Histogram} if none is registered.
     * If a {@link Histogram} was created, the provided {@link Metadata} object will be registered.
     * <p>
     * Note: During retrieval or creation, if a {@link Metadata} object is already registered under
     * this metric name and is not equal to the provided {@link Metadata} object then an exception will be thrown.
     * </p>
     *
     * @param metadata the name of the metric
     * @param tags the tags of the metric
     * @return a new or pre-existing {@link Histogram}
     *
     * @since 2.0
     */
    Histogram histogram(Metadata metadata, Tag... tags);

    /**
     * Return the {@link Meter} registered under the {@link MetricID} with this name and with no tags; or
     * create and register a new {@link Meter} if none is registered.
     *
     * If a {@link Meter} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Meter}
     */
    default Meter meter(String name) {
        return meter(new MetricID(name));
    }

    /**
     * Return the {@link Meter} registered under the {@link MetricID} with this name and with the provided {@link Tag}s;
     * or create and register a new {@link Meter} if none is registered.
     *
     * If a {@link Meter} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param name the name of the metric
     * @param tags the tags of the metric
     * @return a new or pre-existing {@link Meter}
     *
     * @since 2.0
     */
    default Meter meter(String name, Tag... tags) {
        return meter(new MetricID(name, tags));
    }

    /**
     * Return the {@link Meter} registered under the {@link MetricID};
     * or create and register a new {@link Meter} if none is registered.
     *
     * If a {@link Meter} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param metricID the ID of the metric
     * @return a new or pre-existing {@link Meter}
     *
     * @since 3.0
     */
    Meter meter(MetricID metricID);

    /**
     * Return the {@link Meter} registered under the {@link MetricID} with the {@link Metadata}'s name and with
     * no tags; or create and register a new {@link Meter} if none is registered. If a {@link Meter} was created,
     * the provided {@link Metadata} object will be registered.
     * <p>
     * Note: During retrieval or creation, if a {@link Metadata} object is already registered under this metric
     * name and is not equal to the provided {@link Metadata} object then an exception will be thrown.
     * </p>
     *
     * @param metadata the name of the metric
     * @return a new or pre-existing {@link Meter}
     */
    Meter meter(Metadata metadata);

    /**
     * Return the {@link Meter} registered under the {@link MetricID} with the {@link Metadata}'s name and with
     * the provided {@link Tag}s; or create and register a new {@link Meter} if none is registered. If a {@link Meter} was
     * created, the provided {@link Metadata} object will be registered.
     * <p>
     * Note: During retrieval or creation, if a {@link Metadata} object is already registered under this metric name
     * and is not equal to the provided {@link Metadata} object then an exception will be thrown.
     * </p>
     *
     * @param metadata the name of the metric
     * @param tags the tags of the metric
     * @return a new or pre-existing {@link Meter}
     *
     * @since 2.0
     */
    Meter meter(Metadata metadata, Tag... tags);

    /**
     * Return the {@link Timer} registered under the {@link MetricID} with this name and with no tags; or create
     * and register a new {@link Timer} if none is registered.
     *
     * If a {@link Timer} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link Timer}
     */
    default Timer timer(String name) {
        return timer(new MetricID(name));
    }

    /**
     * Return the {@link Timer} registered under the {@link MetricID} with this name and with the provided {@link Tag}s;
     * or create and register a new {@link Timer} if none is registered.
     *
     * If a {@link Timer} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     *
     * @param name the name of the metric
     * @param tags the tags of the metric
     * @return a new or pre-existing {@link Timer}
     *
     * @since 2.0
     */
    default Timer timer(String name, Tag... tags) {
        return timer(new MetricID(name, tags));
    }

    /**
     * Return the {@link Timer} registered under the {@link MetricID};
     * or create and register a new {@link Timer} if none is registered.
     *
     * If a {@link Timer} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param metricID the ID of the metric
     * @return a new or pre-existing {@link Timer}
     *
     * @since 3.0
     */
    Timer timer(MetricID metricID);

    /**
     * Return the {@link Timer} registered under the the {@link MetricID} with the {@link Metadata}'s name and
     * with no tags; or create and register a new {@link Timer} if none is registered. If a {@link Timer} was
     * created, the provided {@link Metadata} object will be registered.
     * <p>
     * Note: During retrieval or creation, if a {@link Metadata} object is already registered under this metric
     * name and is not equal to the provided {@link Metadata} object then an exception will be thrown.
     * </p>
     *
     * @param metadata the name of the metric
     * @return a new or pre-existing {@link Timer}
     */
    Timer timer(Metadata metadata);

    /**
     * Return the {@link Timer} registered under the the {@link MetricID} with the {@link Metadata}'s name and
     * with the provided {@link Tag}s; or create and register a new {@link Timer} if none is registered.
     * If a {@link Timer} was created, the provided {@link Metadata} object will be registered.
     * <p>
     * Note: During retrieval or creation, if a {@link Metadata} object is already registered under this metric
     * name and is not equal to the provided {@link Metadata} object then an exception will be thrown.
     * </p>
     *
     * @param metadata the name of the metric
     * @param tags the tags of the metric
     * @return a new or pre-existing {@link Timer}
     *
     * @since 2.0
     */
    Timer timer(Metadata metadata, Tag... tags);

    /**
     * Return the {@link SimpleTimer} registered under the {@link MetricID} with this name and with no tags; or create
     * and register a new {@link SimpleTimer} if none is registered.
     *
     * If a {@link SimpleTimer} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param name the name of the metric
     * @return a new or pre-existing {@link SimpleTimer}
     *
     * @since 2.3
     */
    default SimpleTimer simpleTimer(String name) {
        return simpleTimer(new MetricID(name));
    }

    /**
     * Return the {@link SimpleTimer} registered under the {@link MetricID} with this name and with the provided {@link Tag}s;
     * or create and register a new {@link SimpleTimer} if none is registered.
     *
     * If a {@link SimpleTimer} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     *
     * @param name the name of the metric
     * @param tags the tags of the metric
     * @return a new or pre-existing {@link SimpleTimer}
     *
     * @since 2.3
     */
    default SimpleTimer simpleTimer(String name, Tag... tags) {
        return simpleTimer(new MetricID(name, tags));
    }

    /**
     * Return the {@link SimpleTimer} registered under the {@link MetricID};
     * or create and register a new {@link SimpleTimer} if none is registered.
     *
     * If a {@link SimpleTimer} was created, a {@link Metadata} object will be registered with the name
     * and type. If a {@link Metadata} object is already registered with this metric name then that
     * {@link Metadata} will be used.
     *
     * @param metricID the ID of the metric
     * @return a new or pre-existing {@link SimpleTimer}
     *
     * @since 3.0
     */
    SimpleTimer simpleTimer(MetricID metricID);

    /**
     * Return the {@link SimpleTimer} registered under the the {@link MetricID} with the {@link Metadata}'s name and
     * with no tags; or create and register a new {@link SimpleTimer} if none is registered. If a {@link SimpleTimer} was
     * created, the provided {@link Metadata} object will be registered.
     * <p>
     * Note: During retrieval or creation, if a {@link Metadata} object is already registered under this metric
     * name and is not equal to the provided {@link Metadata} object then an exception will be thrown.
     * </p>
     *
     * @param metadata the name of the metric
     * @return a new or pre-existing {@link SimpleTimer}
     *
     * @since 2.3
     */
    SimpleTimer simpleTimer(Metadata metadata);

    /**
     * Return the {@link SimpleTimer} registered under the the {@link MetricID} with the {@link Metadata}'s name and
     * with the provided {@link Tag}s; or create and register a new {@link SimpleTimer} if none is registered.
     * If a {@link SimpleTimer} was created, the provided {@link Metadata} object will be registered.
     * <p>
     * Note: During retrieval or creation, if a {@link Metadata} object is already registered under this metric
     * name and is not equal to the provided {@link Metadata} object then an exception will be thrown.
     * </p>
     *
     * @param metadata the name of the metric
     * @param tags the tags of the metric
     * @return a new or pre-existing {@link SimpleTimer}
     *
     * @since 2.3
     */
    SimpleTimer simpleTimer(Metadata metadata, Tag... tags);

    /**
     * Return the {@link Metric} registered for a provided {@link MetricID}.
     *
     * @param metricID lookup key, not {@code null}
     * @return the {@link Metric} registered for the provided {@link MetricID}
     *         or {@code null} if none has been registered so far
     *
     * @since 3.0
     */
    Metric getMetric(MetricID metricID);

    /**
     * Return the {@link Metric} registered for the provided {@link MetricID} as the provided type.
     *
     * @param metricID lookup key, not {@code null}
     * @param asType the return type which is expected to be compatible with the actual type
     *        of the registered metric
     * @return the {@link Metric} registered for the provided {@link MetricID}
     *         or {@code null} if none has been registered so far
     * @throws IllegalArgumentException If the registered metric was not assignable to the provided type
     *
     * @since 3.0
     */
    default <T extends Metric> T getMetric(MetricID metricID, Class<T> asType) {
        try {
            return asType.cast(getMetric(metricID));
        }
        catch (ClassCastException e) {
            throw new IllegalArgumentException(metricID + " was not of expected type " + asType, e);
        }
    }

    /**
     * Return the {@link Counter} registered for the provided {@link MetricID}.
     *
     * @param metricID lookup key, not {@code null}
     * @return the {@link Counter} registered for the key or {@code null} if none has been registered so far
     * @throws IllegalArgumentException If the registered metric was not assignable to {@link Counter}
     *
     * @since 3.0
     */
    default Counter getCounter(MetricID metricID) {
        return getMetric(metricID, Counter.class);
    }

    /**
     * Return the {@link ConcurrentGauge} registered for the provided {@link MetricID}.
     *
     * @param metricID lookup key, not {@code null}
     * @return the {@link ConcurrentGauge} registered for the key or {@code null} if none has been registered so far
     * @throws IllegalArgumentException If the registered metric was not assignable to {@link ConcurrentGauge}
     *
     * @since 3.0
     */
    default ConcurrentGauge getConcurrentGauge(MetricID metricID) {
        return getMetric(metricID, ConcurrentGauge.class);
    }

    /**
     * Return the {@link  Gauge} registered for the provided {@link MetricID}.
     *
     * @param metricID lookup key, not {@code null}
     * @return the {@link  Gauge} registered for the key or {@code null} if none has been registered so far
     * @throws IllegalArgumentException If the registered metric was not assignable to {@link  Gauge}
     *
     * @since 3.0
     */
    default Gauge<?> getGauge(MetricID metricID) {
        return getMetric(metricID, Gauge.class);
    }

    /**
     * Return the {@link Histogram} registered for the provided {@link MetricID}.
     *
     * @param metricID lookup key, not {@code null}
     * @return the {@link Histogram} registered for the key or {@code null} if none has been registered so far
     * @throws IllegalArgumentException If the registered metric was not assignable to {@link Histogram}
     *
     * @since 3.0
     */
    default Histogram getHistogram(MetricID metricID) {
        return getMetric(metricID, Histogram.class);
    }

    /**
     * Return the {@link  Meter} registered for the provided {@link MetricID}.
     *
     * @param metricID lookup key, not {@code null}
     * @return the {@link  Meter} registered for the key or {@code null} if none has been registered so far
     * @throws IllegalArgumentException If the registered metric was not assignable to {@link  Meter}
     *
     * @since 3.0
     */
    default Meter getMeter(MetricID metricID) {
        return getMetric(metricID, Meter.class);
    }

    /**
     * Return the {@link  Timer} registered for the provided {@link MetricID}.
     *
     * @param metricID lookup key, not {@code null}
     * @return the {@link  Timer} registered for the key or {@code null} if none has been registered so far
     * @throws IllegalArgumentException If the registered metric was not assignable to {@link  Timer}
     *
     * @since 3.0
     */
    default Timer getTimer(MetricID metricID) {
        return getMetric(metricID, Timer.class);
    }

    /**
     * Return the {@link  SimpleTimer} registered for the provided {@link MetricID}.
     *
     * @param metricID lookup key, not {@code null}
     * @return the {@link  SimpleTimer} registered for the key or {@code null} if none has been registered so far
     * @throws IllegalArgumentException If the registered metric was not assignable to {@link  SimpleTimer}
     *
     * @since 3.0
     */
    default SimpleTimer getSimpleTimer(MetricID metricID) {
        return getMetric(metricID, SimpleTimer.class);
    }

    /**
     * Return the {@link Metadata} for the provided name.
     *
     * @param name the name of the metric
     * @return the {@link Metadata} for the provided name of {@code null} if none has been registered for that name
     *
     * @since 3.0
     */
    Metadata getMetadata(String name);

    /**
     * Removes all metrics with the given name.
     *
     * @param name the name of the metric
     * @return whether or not the metric was removed
     */
    boolean remove(String name);

    /**
     * Removes the metric with the given MetricID
     *
     * @param metricID the MetricID of the metric
     * @return whether or not the metric was removed
     *
     * @since 2.0
     */
    boolean remove(MetricID metricID);

    /**
     * Removes all metrics which match the given filter.
     *
     * @param filter a filter
     */
    void removeMatching(MetricFilter filter);

    /**
     * Returns a set of the names of all the metrics in the registry.
     *
     * @return the names of all the metrics
     */
    SortedSet<String> getNames();

    /**
     * Returns a set of the {@link MetricID}s of all the metrics in the registry.
     *
     * @return the MetricIDs of all the metrics
     */
    SortedSet<MetricID> getMetricIDs();

    /**
     * Returns a map of all the gauges in the registry and their {@link MetricID}s.
     *
     * @return all the gauges in the registry
     */
    SortedMap<MetricID, Gauge> getGauges();

    /**
     * Returns a map of all the gauges in the registry and their {@link MetricID}s which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the gauges in the registry
     */
    default SortedMap<MetricID, Gauge> getGauges(MetricFilter filter) {
        return getMetrics(Gauge.class, filter);
    }

    /**
     * Returns a map of all the counters in the registry and their {@link MetricID}s.
     *
     * @return all the counters in the registry
     */
    SortedMap<MetricID, Counter> getCounters();

    /**
     * Returns a map of all the counters in the registry and their {@link MetricID}s which match the given
     * filter.
     *
     * @param filter the metric filter to match
     * @return all the counters in the registry
     */
    default SortedMap<MetricID, Counter> getCounters(MetricFilter filter) {
        return getMetrics(Counter.class, filter);
    }

    /**
     * Returns a map of all the concurrent gauges in the registry and their {@link MetricID}s.
     *
     * @return all the concurrent gauges in the registry
     */
    SortedMap<MetricID, ConcurrentGauge> getConcurrentGauges();

    /**
     * Returns a map of all the concurrent gauges in the registry and their {@link MetricID}s which match
     * the given filter.
     *
     * @param filter    the metric filter to match
     * @return all the concurrent gauges in the registry
     */
    default SortedMap<MetricID, ConcurrentGauge> getConcurrentGauges(MetricFilter filter) {
        return getMetrics(ConcurrentGauge.class, filter);
    }

    /**
     * Returns a map of all the histograms in the registry and their {@link MetricID}s.
     *
     * @return all the histograms in the registry
     */
    SortedMap<MetricID, Histogram> getHistograms();

    /**
     * Returns a map of all the histograms in the registry and their {@link MetricID}s which match the given
     * filter.
     *
     * @param filter the metric filter to match
     * @return all the histograms in the registry
     */
    default SortedMap<MetricID, Histogram> getHistograms(MetricFilter filter) {
        return getMetrics(Histogram.class, filter);
    }

    /**
     * Returns a map of all the meters in the registry and their {@link MetricID}s.
     *
     * @return all the meters in the registry
     */
    SortedMap<MetricID, Meter> getMeters();

    /**
     * Returns a map of all the meters in the registry and their {@link MetricID}s which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the meters in the registry
     */
    default SortedMap<MetricID, Meter> getMeters(MetricFilter filter) {
        return getMetrics(Meter.class, filter);
    }

    /**
     * Returns a map of all the timers in the registry and their {@link MetricID}s.
     *
     * @return all the timers in the registry
     */
    SortedMap<MetricID, Timer> getTimers();

    /**
     * Returns a map of all the timers in the registry and their {@link MetricID}s which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the timers in the registry
     */
    default SortedMap<MetricID, Timer> getTimers(MetricFilter filter) {
        return getMetrics(Timer.class, filter);
    }

    /**
     * Returns a map of all the simple timers in the registry and their {@link MetricID}s.
     *
     * @return all the timers in the registry
     */
    SortedMap<MetricID, SimpleTimer> getSimpleTimers();

    /**
     * Returns a map of all the simple timers in the registry and their {@link MetricID}s which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the timers in the registry
     */
    default SortedMap<MetricID, SimpleTimer> getSimpleTimers(MetricFilter filter) {
        return getMetrics(SimpleTimer.class, filter);
    }

    /**
     * Returns a map of all the metrics in the registry and their {@link MetricID}s which match the given filter.
     *
     * @param filter the metric filter to match
     * @return all the metrics in the registry
     *
     * @since 3.0
     */
    SortedMap<MetricID, Metric> getMetrics(MetricFilter filter);

    /**
     * Returns a map of all the metrics in the registry and their {@link MetricID}s which match the given filter
     * and which are assignable to the provided type.
     * @param ofType the type to which all returned metrics should be assignable
     * @param filter the metric filter to match
     *
     * @return all the metrics in the registry
     *
     * @since 3.0
     */
    @SuppressWarnings("unchecked")
    default <T extends Metric> SortedMap<MetricID, T> getMetrics(Class<T> ofType, MetricFilter filter) {
        return (SortedMap<MetricID, T>) getMetrics(
                (metricID, metric) -> filter.matches(metricID, metric)
                    && ofType.isAssignableFrom(metric.getClass()));
    }

    /**
     * Returns a map of all the metrics in the registry and their {@link MetricID}s.
     *
     * @return all the metrics in the registry
     * @deprecated Prior to 2.4 this method was the standard way to access {@link Metric}s by their {@link MetricID};
     *             Doing so might cause unnecessary creation of an intermediate map that might be computed by the
     *             implementation. Prefer {@link #getMetric(MetricID)} for key based lookup of single {@link Metric};
     *             Use {@link #getMetricIDs()} to iterate over all metrics.
     *             Use {@link #getMetrics(MetricFilter)} with {@link MetricFilter#ALL} if a (most likely) computed
     *             {@link Map} is preferred.
     */
    @Deprecated
    Map<MetricID, Metric> getMetrics();

    /**
     * Returns a map of all the metadata in the registry and their names.
     *
     * @return all the metadata in the registry
     * @deprecated Prior to 2.4 this method was the standard way to access {@link Metadata} by their name; Doing so
     *             might cause unnecessary creation of an intermediate map that might be computed by the implementation.
     *             Prefer {@link #getMetadata(String)} for key based lookup of a single {@link Metadata};
     *             Use {@link #getNames()} to iterate over all names.
     */
    @Deprecated
    Map<String, Metadata> getMetadata();

    /**
     * Returns the type of this metric registry.
     *
     * @return Type of this registry (VENDOR, BASE, APPLICATION)
     */
    Type getType();

}

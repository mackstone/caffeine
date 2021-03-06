/*
 * Copyright 2014 Ben Manes. All Rights Reserved.
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
package com.github.benmanes.caffeine.cache.testing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import com.github.benmanes.caffeine.cache.RemovalCause;
import com.github.benmanes.caffeine.cache.RemovalListener;

/**
 * Some common removal listener implementations for tests.
 *
 * @author ben.manes@gmail.com (Ben Manes)
 */
public final class RemovalListeners {

  private RemovalListeners() {}

  /** A removal listener that stores the notifications for inspection. */
  public static <K, V> RemovalListener<K, V> consuming() {
    return new ConsumingRemovalListener<>();
  }

  /** A removal listener that throws an exception if a notification arrives. */
  public static <K, V> RemovalListener<K, V> rejecting() {
    return new RejectingRemovalListener<>();
  }

  public static final class RejectingRemovalListener<K, V>
      implements RemovalListener<K, V>, Serializable {
    private static final long serialVersionUID = 1L;

    public boolean reject = true;
    public int rejected;

    @Override
    public void onRemoval(K key, V value, RemovalCause cause) {
      if (reject) {
        rejected++;
        throw new RejectedExecutionException("Rejected eviction of " +
            new RemovalNotification<>(key, value, cause));
      }
    }
  }

  public static final class ConsumingRemovalListener<K, V>
      implements RemovalListener<K, V>, Serializable {
    private static final long serialVersionUID = 1L;

    private final List<RemovalNotification<K, V>> evicted;

    public ConsumingRemovalListener() {
      this.evicted = new ArrayList<>();
    }

    @Override
    public synchronized void onRemoval(K key, V value, RemovalCause cause) {
      evicted.add(new RemovalNotification<>(key, value, cause));
    }

    public List<RemovalNotification<K, V>> evicted() {
      return evicted;
    }
  }
}

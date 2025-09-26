/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.common.threadpool.concurrent;

import org.apache.dubbo.common.logger.ErrorTypeAwareLogger;
import org.apache.dubbo.common.logger.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class ScheduledCompletableFuture {
    protected static final ErrorTypeAwareLogger logger =
            LoggerFactory.getErrorTypeAwareLogger(ScheduledCompletableFuture.class);

    public static <T> CompletableFuture<T> schedule(
            ScheduledExecutorService executor, Supplier<T> task, long delay, TimeUnit unit) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        executor.schedule(
                () -> {
                    try {
                        return completableFuture.complete(task.get());
                    } catch (Throwable t) {
                        return completableFuture.completeExceptionally(t);
                    }
                },
                delay,
                unit);
        return completableFuture;
    }

    public static <T> CompletableFuture<T> submit(ScheduledExecutorService executor, Supplier<T> task) {
        CompletableFuture<T> completableFuture = new CompletableFuture<>();
        logger.info("自定义日志---提交任务（同步阻塞）：Callable");
        executor.submit(() -> {
            try {
                return completableFuture.complete(task.get());
            } catch (Throwable t) {
                return completableFuture.completeExceptionally(t);
            }
        });
        return completableFuture;
    }
}

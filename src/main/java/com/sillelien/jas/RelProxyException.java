/*
 *    Copyright (c) 2014-2017 Neil Ellis
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.sillelien.jas;

import org.jetbrains.annotations.NotNull;

/**
 * Internal checked exceptions thrown by RelProxy and library specific errors are wrapped into this exception class.
 *
 * @author Jose Maria Arranz Santamaria
 */
public class RelProxyException extends RuntimeException {
    /**
     * Constructs a new exception with the specified message and cause.
     * <p>Parameters are passed to the super constructor.</p>
     *
     * @param message the detail message
     * @param cause   the cause
     */
    public RelProxyException(@NotNull String message, @NotNull Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified message.
     * <p>Parameter is passed to the super constructor.</p>
     *
     * @param message the detail message
     */
    public RelProxyException(@NotNull String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified cause.
     * <p>Parameter is passed to the super constructor.</p>
     *
     * @param cause the cause
     */
    public RelProxyException(@NotNull Throwable cause) {
        super(cause);
    }
}

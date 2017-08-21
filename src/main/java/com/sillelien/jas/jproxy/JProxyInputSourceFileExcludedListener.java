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

package com.sillelien.jas.jproxy;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * This interface is provided to developers to implement excluding rules to filter source files not to be part of the hot reloading system in spite of included in input paths
 *
 * @author Jose Maria Arranz Santamaria
 * @see JProxyConfig#setJProxyInputSourceFileExcludedListener(JProxyInputSourceFileExcludedListener)
 */
public interface JProxyInputSourceFileExcludedListener {
    /**
     * This method is called per file when going to be managed by the hot reloading system.
     *
     * @param file                the file to be managed.
     * @param rootFolderOfSources the folder root of sources where this file is located.
     * @return true whether the file must be ignored.
     */
    boolean isExcluded(@NotNull File file, @NotNull File rootFolderOfSources);
}

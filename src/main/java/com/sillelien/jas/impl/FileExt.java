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

package com.sillelien.jas.impl;

import com.sillelien.jas.RelProxyException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 * @author jmarranz
 */
public class FileExt {
    @NotNull
    protected final File file;
    @NotNull
    protected final String cannonicalPath; // El obtener el cannonicalPath exige acceder al sistema de archivos, por eso nos inventamos esta clase, para evitar sucesivas llamadas a File.getCanonicalPath()

    public FileExt(@NotNull File file) {
        this.file = file;
        try {
            cannonicalPath = file.getCanonicalPath();
        } catch (IOException ex) {
            throw new RelProxyException(ex);
        }
    }

    @NotNull
    public File getFile() {
        return file;
    }

    @NotNull
    public String getCanonicalPath() {
        return cannonicalPath;
    }
}

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

package com.sillelien.jas.impl.jproxy.core.clsmgr;

import com.sillelien.jas.RelProxyException;
import com.sillelien.jas.impl.FileExt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * @author jmarranz
 */
public class FolderSourceList {
    @Nullable
    protected FileExt[] sourceList;

    public FolderSourceList(@Nullable final String[] sourcePathList, boolean expectedDirectory) {
        if (sourcePathList != null) // En el caso de shell interactivo es null
        {
            // El convertir siempre a File los paths es para normalizar paths
            sourceList = new FileExt[sourcePathList.length];
            for (int i = 0; i < sourcePathList.length; i++) {
                String pathname = sourcePathList[i];
                assert pathname != null;
                File folder = new File(pathname);
                if (!folder.exists())
                    throw new RelProxyException("Source folder does not exist: " + folder.getAbsolutePath());
                boolean isDirectory = folder.isDirectory();
                if (expectedDirectory) {
                    if (!isDirectory)
                        throw new RelProxyException("Source folder is not a directory: " + folder.getAbsolutePath());
                } else {
                    if (isDirectory)
                        throw new RelProxyException("Expected a file not a directory: " + folder.getAbsolutePath());
                }
                sourceList[i] = new FileExt(folder);
            }
        }
    }

    @Nullable
    public FileExt[] getArray() {
        return sourceList;
    }

    @Nullable
    public String buildClassNameFromFile(@NotNull FileExt sourceFile) {
        assert sourceList != null;
        for (FileExt rootFolderOfSources : sourceList) {
            assert rootFolderOfSources != null;
            String className = buildClassNameFromFile(sourceFile, rootFolderOfSources);
            if (className != null)
                return className;
        }
        throw new RelProxyException("File not found in source folders: " + sourceFile.getFile().getAbsolutePath());
    }

    @Nullable
    public static String buildClassNameFromFile(@NotNull FileExt sourceFile, @NotNull FileExt rootFolderOfSources) {
        String path = sourceFile.getCanonicalPath();

        String rootFolderOfSourcesAbsPath = rootFolderOfSources.getCanonicalPath();
        int pos = path.indexOf(rootFolderOfSourcesAbsPath);
        if (pos == 0) // Está en este source folder
        {
            path = path.substring(rootFolderOfSourcesAbsPath.length() + 1); // Sumamos +1 para quitar también el / separador del pathInput y el path relativo de la clase
            // Puede no tener extensión (script) o bien ser .java o bien ser una inventada (ej .jsh), la quitamos si existe
            pos = path.lastIndexOf('.');
            if (pos != -1)
                path = path.substring(0, pos);
            path = path.replace(File.separatorChar, '.');  // getAbsolutePath() normaliza con el caracter de la plataforma
            return path;
        }
        return null;
    }


}

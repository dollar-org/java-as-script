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

package com.sillelien.jas.impl.jproxy.core.clsmgr.comp;

import com.sillelien.jas.RelProxyException;
import com.sillelien.jas.jproxy.JProxyDiagnosticsListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;
import java.util.List;

/**
 * @author jmarranz
 */
public class JProxyCompilerContext {
    @NotNull
    protected StandardJavaFileManager standardFileManager;

    @Nullable
    protected DiagnosticCollector<JavaFileObject> diagnostics;

    @Nullable
    protected JProxyDiagnosticsListener diagnosticsListener;

    @NotNull
    private static final Logger log = LoggerFactory.getLogger("JProxyCompilerContext");

    public JProxyCompilerContext(@NotNull StandardJavaFileManager standardFileManager,
                                 @NotNull DiagnosticCollector<JavaFileObject> diagnostics,
                                 @Nullable JProxyDiagnosticsListener diagnosticsListener) {
        this.standardFileManager = standardFileManager;
        this.diagnostics = diagnostics;
        this.diagnosticsListener = diagnosticsListener;
    }

    public @NotNull StandardJavaFileManager getStandardFileManager() {
        return standardFileManager;
    }

    @Nullable
    public DiagnosticCollector<JavaFileObject> getDiagnosticCollector() {
        return diagnostics;
    }

    public void close() {
        try {
            standardFileManager.close();
        } catch (IOException ex) {
            throw new RelProxyException(ex);
        }

        List<Diagnostic<? extends JavaFileObject>> diagList = null;
        if (diagnostics != null) {
            diagList = diagnostics.getDiagnostics();
            if (!diagList.isEmpty()) {
                if (diagnosticsListener != null) {
                    diagnosticsListener.onDiagnostics(diagnostics);
                } else {
                    int i = 1;
                    for (Diagnostic diagnostic : diagList) {
                        log.debug("Diagnostic {}", i);
                        log.debug("  code: {}", diagnostic.getCode());
                        log.debug("  kind: {}", diagnostic.getKind());
                        log.debug("  line number: {}", diagnostic.getLineNumber());
                        log.debug("  column number: {}", diagnostic.getColumnNumber());
                        log.debug("  start position: {}", diagnostic.getStartPosition());
                        log.debug("  position: {}", diagnostic.getPosition());
                        log.debug("  end position: {}", diagnostic.getEndPosition());
                        log.debug("  source: {}", diagnostic.getSource());
                        log.debug("  message: {}", diagnostic.getMessage(null));
                        i++;
                    }
                }
            }
        }
    }
}

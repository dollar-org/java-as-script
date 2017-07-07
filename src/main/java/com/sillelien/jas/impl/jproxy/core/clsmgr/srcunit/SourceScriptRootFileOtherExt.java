package com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit;

import com.sillelien.jas.impl.FileExt;
import com.sillelien.jas.impl.jproxy.JProxyUtil;
import com.sillelien.jas.impl.jproxy.core.clsmgr.FolderSourceList;
import org.jetbrains.annotations.NotNull;

/**
 * @author jmarranz
 */
public class SourceScriptRootFileOtherExt extends SourceScriptRootFile {
    public SourceScriptRootFileOtherExt(@NotNull FileExt sourceFile, @NotNull FolderSourceList folderSourceList) {
        super(sourceFile, folderSourceList);
    }

    @NotNull
    @Override
    public String getScriptCode(@NotNull String encoding, boolean[] hasHashBang) {
        String codeBody = JProxyUtil.readTextFile(sourceFile.getFile(), encoding);
        // Eliminamos la primera línea #!  (debe estar en la primera línea y sin espacios antes)
        if (codeBody.startsWith("#!")) {
            hasHashBang[0] = true;
            int pos = codeBody.indexOf('\n');
            if (pos != -1) // Rarísimo que sólo esté el hash bang (script vacío)
            {
                codeBody = codeBody.substring(pos + 1);
            }
        } else hasHashBang[0] = false;
        return codeBody;
    }
}

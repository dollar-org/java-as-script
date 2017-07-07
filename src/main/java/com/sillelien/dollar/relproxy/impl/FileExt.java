package com.sillelien.dollar.relproxy.impl;

import com.sillelien.dollar.relproxy.RelProxyException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author jmarranz
 */
public class FileExt
{
    @NotNull
    protected final File file;
    @NotNull
    protected final String cannonicalPath; // El obtener el cannonicalPath exige acceder al sistema de archivos, por eso nos inventamos esta clase, para evitar sucesivas llamadas a File.getCanonicalPath()
            
    public FileExt(@NotNull File file)
    {
        this.file = file;
        try { this.cannonicalPath = file.getCanonicalPath(); }
        catch (IOException ex) { throw new RelProxyException(ex); }        
    }
    
    @NotNull
    public File getFile()
    {
        return file;
    }
    
    @NotNull
    public String getCanonicalPath()
    {
        return cannonicalPath;
    }
}

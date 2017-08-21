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

package com.sillelien.jas.impl.jproxy.core.clsmgr.cldesc;

import com.sillelien.jas.RelProxyException;
import com.sillelien.jas.impl.jproxy.core.JProxyImpl;
import com.sillelien.jas.impl.jproxy.core.clsmgr.JProxyEngine;
import com.sillelien.jas.impl.jproxy.core.clsmgr.srcunit.SourceScriptRoot;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

/**
 * @author jmarranz
 */
public class ClassDescriptorSourceScript extends ClassDescriptorSourceUnit {
    @NotNull
    protected String source;

    public ClassDescriptorSourceScript(@NotNull JProxyEngine engine, @NotNull String className, @NotNull SourceScriptRoot sourceFile, long timestamp) {
        super(engine, className, sourceFile, timestamp);

        generateSourceCode();
    }


    @NotNull
    public SourceScriptRoot getSourceScript() {
        return (SourceScriptRoot) sourceUnit;
    }

    private void generateSourceCode() {
        boolean[] hasHashBang = new boolean[1];

        String scriptCode = getSourceScript().getScriptCode(getEncoding(), hasHashBang);

        boolean completeClass = isCompleteClass(scriptCode);

        StringBuilder finalCode = new StringBuilder();
        if (completeClass) {
            if (hasHashBang[0])
                finalCode.append("\n");   // Como hemos quitado la línea #! añadimos una nueva para que los números de línea en caso de error coincidan con el original
            finalCode.append(scriptCode);
        } else {
            JProxyImpl jproxy = engine.getJProxy();
            String mainParamsDec = null;
            String mainReturnType = null;

            Class mainParamClass = jproxy.getMainParamClass();
            if (String[].class.equals(mainParamClass)) {
                mainParamsDec = "String[] args";
                mainReturnType = "void";
            } else if (ScriptContext.class.equals(mainParamClass)) {
                mainParamsDec = ScriptEngine.class.getName() + " engine," + ScriptContext.class.getName() + " context";
                mainReturnType = "Object";

                if ("".equals(scriptCode)) scriptCode = "return null;";
            }

             List<String> imports= jproxy.getImports();
            for (String anImport : imports) {
                finalCode.append("import ").append(anImport).append(";\n");
            }
             List<String> staticImports= jproxy.getStaticImports();
            for (String anImport : staticImports) {
                finalCode.append("import static ").append(anImport).append(";\n");
            }


            finalCode.append("public class ").append(className).append(" { public static ").append(mainReturnType).append(
                    " main(").append(mainParamsDec).append(
                    ") {\n"); // Lo ponemos todo en una línea para que en caso de error la línea de error coincida con el script original pues hemos quitado la primera línea #!
            finalCode.append(scriptCode);
            finalCode.append("  }\n");
            finalCode.append("}\n");
        }
        source = finalCode.toString();
    }

    private boolean isCompleteClass(@NotNull String code) {
        // Buscamos si hay un " class ..." o un "import..." al comienzo para soportar la definición de una clase completa como script       
        int pos = code.indexOf("class");
        if (pos == -1) return false;
        // Hay al menos un "class", ojo que puede ser parte de una variable o dentro de un comentario, pero si no existiera desde luego que no es clase completa

        pos = getFirstPosIgnoringCommentsAndSeparators(code);
        if (pos == -1) return false;

        // Lo primero que nos tenemos encontrar es un import o una declaración de class
        int pos2 = code.indexOf("import", pos);
        if (pos2 == pos)
            return true; // Si hay un import hay declaración de clase

        // Vemos si es un "public class..." o similar
        int posClass = code.indexOf("class", pos);
        String visibility = code.substring(pos, posClass);
        visibility = visibility.trim(); // No consideramos \n hay que ser retorcido poner un \n entre el public y el class por ejemplo
        if (visibility.isEmpty()) return true; // No hay visibilidad, que no compile no es cosa nuestra
        return ("private".equals(visibility) || "public".equals(visibility) || "protected".equals(visibility));
    }

    private int getFirstPosIgnoringCommentsAndSeparators(@NotNull String code) {
        int i = -1;
        for (i = 0; i < code.length(); i++) {
            char c = code.charAt(i);
            if ((c == ' ') || (c == '\n') || (c == '\t')) continue;
            else if ((c == '/') && ((i + 1) < code.length())) {
                char c2 = code.charAt(i + 1);
                if (c2 == '/') {
                    i = getFirstPosIgnoringOneLineComment(code, i);
                    if (i == -1) return -1; // Comentario mal formado
                } else if (c2 == '*') {
                    i = getFirstPosIgnoringMultiLineComment(code, i);
                    if (i == -1) return -1; // Comentario mal formado                    
                }
            } else break;
        }
        return i;
    }

    private int getFirstPosIgnoringOneLineComment(@NotNull String code, int start) {
        return code.indexOf('\n', start);
    }

    private int getFirstPosIgnoringMultiLineComment(@NotNull String code, int start) {
        return code.indexOf("*/", start);
    }

    @Override
    public void updateTimestamp(long timestamp) {
        long oldTimestamp = this.timestamp;
        if (oldTimestamp != timestamp) {
            generateSourceCode();
        }
        super.updateTimestamp(timestamp);
    }

    @NotNull
    public String getSourceCode() {
        return source;
    }

    public void callMainMethod(@NotNull LinkedList<String> argsToScript) throws Throwable {
        try {
            Class scriptClass = getLastLoadedClass();
            assert scriptClass != null;
            Method method = scriptClass.getDeclaredMethod("main", String[].class);
            String[] argsToScriptArr = !argsToScript.isEmpty() ? argsToScript.toArray(
                    new String[argsToScript.size()]) : new String[0];
            if (method != null) {
                method.invoke(null, new Object[]{argsToScriptArr});
            } else {
                throw new IllegalAccessException("No main method could be found");
            }
        } catch (@NotNull IllegalAccessException | IllegalArgumentException | SecurityException ex) {
            throw new RelProxyException(ex);
        } catch (@NotNull InvocationTargetException ex) {
            //noinspection ConstantConditions
            throw ex.getCause();
        } // Los errores de ejecución se envuelven en un InvocationTargetException
    }

    @Nullable
    public Object callMainMethod(@NotNull ScriptEngine engine, @NotNull ScriptContext context) throws Throwable {
        Class scriptClass = getLastLoadedClass();
        assert scriptClass != null;
        return callMainMethod(scriptClass, engine, context);
    }

    @Nullable
    public static Object callMainMethod(@NotNull Class scriptClass, @NotNull ScriptEngine engine, @NotNull  ScriptContext context) throws Throwable {
        try {
            Method method = scriptClass.getDeclaredMethod("main", ScriptEngine.class, ScriptContext.class);
            if (method != null) {
                return method.invoke(null, engine, context);
            } else {
                throw new IllegalAccessException("No main method could be found");
            }
        } catch (@NotNull IllegalAccessException | NoSuchMethodException | SecurityException | IllegalArgumentException ex) {
            throw new RelProxyException(ex);
        } catch (@NotNull InvocationTargetException ex) {
            //noinspection ConstantConditions
            throw ex.getCause();
        } // Los errores de ejecución se envuelven en un InvocationTargetException
    }
}

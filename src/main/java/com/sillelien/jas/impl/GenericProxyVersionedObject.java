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
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * @author jmarranz
 */
public abstract class GenericProxyVersionedObject {
    @NotNull
    protected Object obj;
    protected GenericProxyInvocationHandler parent;

    public GenericProxyVersionedObject(@NotNull Object obj, GenericProxyInvocationHandler parent) {
        this.obj = obj;
        this.parent = parent;
    }

    protected static void getTreeFields(@NotNull Class clasz,  @NotNull Object obj, @NotNull ArrayList<Field> fieldList,  @Nullable ArrayList<Object> valueList) throws IllegalAccessException {
        getFields(clasz, obj, fieldList, valueList);
        Class superClass = clasz.getSuperclass();
        if (superClass != null)
            getTreeFields(superClass, obj, fieldList, valueList);
    }

    protected static void getFields(@NotNull Class clasz, Object obj, @NotNull ArrayList<Field> fieldList, @Nullable ArrayList<Object> valueList) throws IllegalAccessException {
        Field[] fieldListClass = clasz.getDeclaredFields();
        assert fieldListClass != null;
        for (int i = 0; i < fieldListClass.length; i++) {
            Field field = fieldListClass[i];
            fieldList.add(field);
            if (valueList != null) {
                field.setAccessible(true);
                Object value = field.get(obj);
                valueList.add(value);
            }
        }
    }

    @NotNull
    public Object getCurrent() {
        return obj;
    }

    @NotNull
    public Object getNewVersion() throws Throwable {
        Class<?> newClass = reloadClass();
        if (newClass == null)
            return obj;

        Class oldClass = obj.getClass();
        if (newClass != oldClass) {
            obj = copy(oldClass, obj, newClass);
        }

        return obj;
    }

    @NotNull
    private Object copy(@NotNull Class oldClass, @NotNull Object oldObj, @NotNull Class newClass) throws IllegalAccessException, InstantiationException, IllegalArgumentException, InvocationTargetException {
        Object newObj;

        ArrayList<Field> fieldListOld = new ArrayList<Field>();
        ArrayList<Object> valueListOld = new ArrayList<Object>();

        getTreeFields(oldClass, oldObj, fieldListOld, valueListOld);

        Class<?> enclosingClassNew = newClass.getEnclosingClass();
        if (enclosingClassNew == null) {
            Constructor construc;
            try {
                construc = newClass.getConstructor();
            } catch (NoSuchMethodException ex) {
                throw new RelProxyException("Cannot reload " + newClass.getName() + " a default empty of params constructor is required", ex);
            }
            assert construc != null;
            newObj = construc.newInstance();
        } else {
            // En el caso de inner class o anonymous inner class el constructor por defecto se obtiene de forma diferente, útil para los EventListener de ItsNat
            Constructor construc;
            try {
                construc = newClass.getDeclaredConstructor(enclosingClassNew);
            } catch (NoSuchMethodException ex) // Yo creo que nunca ocurre al menos no en anonymous inner classes pero por si acaso
            {
                throw new RelProxyException("Cannot reload " + newClass.getName() + " a default empty of params constructor is required", ex);
            }
            assert construc != null;
            construc.setAccessible(true);  // Necesario

            // http://stackoverflow.com/questions/1816458/getting-hold-of-the-outer-class-object-from-the-inner-class-object    


            Field enclosingFieldOld;
            try {
                enclosingFieldOld = oldClass.getDeclaredField("this$0");
            } catch (NoSuchFieldException ex) {
                throw new RelProxyException(ex);
            }
            assert enclosingFieldOld != null;
            enclosingFieldOld.setAccessible(true);
            Object enclosingObjectOld = enclosingFieldOld.get(oldObj);
            assert enclosingObjectOld != null;
            Object enclosingObjectNew = copy(enclosingObjectOld.getClass(), enclosingObjectOld, enclosingClassNew);

            newObj = construc.newInstance(enclosingObjectNew);
        }


        ArrayList<Field> fieldListNew = new ArrayList<Field>();

        getTreeFields(newClass, newObj, fieldListNew, null);

        if (fieldListOld.size() != fieldListNew.size())
            throw new RelProxyException("Cannot reload " + newClass.getName() + " number of fields have changed, redeploy");

        for (int i = 0; i < fieldListOld.size(); i++) {
            Field fieldOld = fieldListOld.get(i);
            Field fieldNew = fieldListNew.get(i);
            assert fieldOld != null;
            assert fieldNew != null;

            if (enclosingClassNew != null && "this$0".equals(fieldOld.getName()) && "this$0".equals(fieldNew.getName()))
                continue; // Ya están correctamente definidos


            Class<?> type = fieldOld.getType();
            assert type != null;

            Class<?> fieldNewType = fieldNew.getType();

            if ((!ignoreField(fieldOld) && !fieldOld.getName().equals(fieldNew.getName())) ||
                    !type.equals(fieldNewType))
                throw new RelProxyException("Cannot reload " + newClass.getName() + " fields have changed, redeploy");

            Object fieldObj = valueListOld.get(i);
            fieldNew.setAccessible(true);
            int modifiersNew = fieldNew.getModifiers();
            boolean isStaticFinal = Modifier.isStatic(modifiersNew) && Modifier.isFinal(modifiersNew);
            Field modifiersField = null;
            if (isStaticFinal) {
                // http://stackoverflow.com/questions/3301635/change-private-static-final-field-using-java-reflection
                try {
                    modifiersField = Field.class.getDeclaredField("modifiers");
                } catch (NoSuchFieldException ex) {
                    throw new RelProxyException(ex);
                }
                assert modifiersField != null;
                modifiersField.setAccessible(true);
                modifiersField.setInt(fieldNew, fieldNew.getModifiers() & ~Modifier.FINAL);  // Quitamos el modifier final
            }

            fieldNew.set(newObj, fieldObj);

            if (modifiersField != null) {
                modifiersField.setInt(fieldNew, fieldNew.getModifiers() & ~Modifier.FINAL);  // Restauramos el modifier final
            }
        }
        return newObj;
    }

    @Nullable
    protected abstract <T> Class<T> reloadClass();

    protected abstract boolean ignoreField(Field field);
}

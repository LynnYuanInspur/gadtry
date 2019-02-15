/*
 * Copyright (C) 2018 The Harbby Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.harbby.gadtry.jvm;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collections;
import java.util.Map;

public class JVMLauncherTest
{
    @Test
    public void test1()
            throws IOException, ClassNotFoundException, JVMException
    {
        System.out.println("--- vm test ---");
        JVMLauncher<Integer> launcher = JVMLaunchers.<Integer>newJvm()
                .setCallable(() -> {
                    //TimeUnit.SECONDS.sleep(1000000);
                    System.out.println("************ job start ***************");
                    return 1;
                })
                .addUserjars(Collections.emptyList())
                .setXms("16m")
                .setXmx("16m")
                .setConsole((msg) -> System.out.println(msg))
                .build();

        VmFuture<Integer> out = launcher.startAndGet();
        Assert.assertEquals(out.get().get().intValue(), 1);
    }

    @Test
    public void testForkJvmEnv()
            throws JVMException
    {
        String envValue = "value_007";
        JVMLauncher<String> launcher = JVMLaunchers.<String>newJvm()
                .setCallable(() -> {
                    //TimeUnit.SECONDS.sleep(1000000);
                    System.out.println("************ job start ***************");
                    String env = System.getenv("TestEnv");
                    System.out.println("get env TestEnv = " + env);
                    Assert.assertEquals(env, envValue);
                    return env;
                })
                .setEnvironment("TestEnv", envValue)
                .addUserjars(Collections.emptyList())
                .setXms("16m")
                .setXmx("16m")
                .setConsole((msg) -> System.out.println(msg))
                .build();

        VmFuture<String> out = launcher.startAndGet();
        Assert.assertEquals(out.get().get(), envValue);
    }

    @Test
    public void test2()
            throws IllegalAccessException
    {
        Class<?> class1 = java.io.ObjectInputStream.class;
        try {
            Field field = class1.getDeclaredField("primClasses");
            field.setAccessible(true);
            Map map = (Map) field.get(class1);
            System.out.println(field.get(map));

            System.out.println(field.getName());
            System.out.println(field.getType());
        }
        catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void test()
    {
        Field[] fields = JVMLauncherTest.class.getDeclaredFields();
        try {
            for (Field field : fields) {
                field.setAccessible(true);
                if (field.getType().toString().endsWith("java.lang.String") && Modifier.isStatic(field.getModifiers())) {
                    System.out.println(field.getName() + " , " + field.get(JVMLauncherTest.class));
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void methodTest()
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException
    {
        Class<?> class1 = java.io.ObjectInputStream.class;
        Method method = class1.getDeclaredMethod("latestUserDefinedLoader");
        method.setAccessible(true);  //必须要加这个才能
        Object a1 = method.invoke(null);
        Assert.assertTrue(a1 instanceof ClassLoader);
    }
}

/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.dynamic.codegen.impl ;

import org.glassfish.pfl.basic.reflection.Bridge;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;

/** Class used to get a class directly from code generated by
 * a runtime code generator.
 * The code generator extends this base class, and must implement
 * the getClassData method.
 * Most of this is independent of BCEL, but finalizeMethod is
 * specific to the BCEL framework.
 */
public class CodeGeneratorUtil {
	private static final Bridge BRIDGE_REF = AccessController.doPrivileged(
         new PrivilegedAction<Bridge>() {
             @Override
             public Bridge run() {
                 return Bridge.get();
             }
         }
 );
    private CodeGeneratorUtil() { }

    // Name that Java uses for constructor methods
    public static final String CONSTRUCTOR_METHOD_NAME = "<init>" ;

	public static Class<?> makeClass( String name, byte[] def, ProtectionDomain pd, ClassLoader loader ) {
    	return BRIDGE_REF.defineClass(name, def, loader, pd);
    }
}
/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Distribution License v. 1.0, which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: BSD-3-Clause
 */

package org.glassfish.pfl.basic.proxy ;


import java.util.Map ;
import java.util.LinkedHashMap ;
  
import java.lang.reflect.Method ;
import java.lang.reflect.InvocationHandler ;
import org.glassfish.pfl.basic.algorithm.ClassAnalyzer;
import org.glassfish.pfl.basic.func.UnaryPredicateBase;

public class CompositeInvocationHandlerImpl implements 
    CompositeInvocationHandler
{
    private static final long serialVersionUID = -5206426596331848271L;
    private Map<Class<?>,InvocationHandler> classToInvocationHandler =
        new LinkedHashMap<Class<?>,InvocationHandler>() ;
    private InvocationHandler defaultHandler = null ;

    @Override
    public void addInvocationHandler( final Class<?> interf,
	final InvocationHandler handler )
    {
        final ClassAnalyzer ca = ClassAnalyzer.getClassAnalyzer( interf ) ;
        ca.findClasses(
            new UnaryPredicateBase<Class<?>>("AddClassToMap") {
                @Override
                public boolean eval(Class<?> cls) {
                    classToInvocationHandler.put( cls, handler ) ;
                    return true ;
                }
            }
        ) ;
    }

    @Override
    public void setDefaultHandler( InvocationHandler handler ) 
    {
	defaultHandler = handler ;
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args )
	throws Throwable
    {
	// Note that the declaring class in method is the interface
	// in which the method was defined, not the proxy class.
	Class<?> cls = method.getDeclaringClass() ;

        // Handle Object methods here.  This allows overridding of
        // toString, equals, and hashCode in a class that extends
        // CompositeInvocationHandlerImpl.
        if (cls.equals( Object.class )) {
            try {
                return method.invoke( this, args ) ;
            } catch (Exception exc) {
                throw new RuntimeException( "Invocation error on Object method",
                    exc ) ;
            }
        }

	InvocationHandler handler = classToInvocationHandler.get(cls) ;

	if (handler == null) {
	    if (defaultHandler != null) {
                handler = defaultHandler;
            } else {
		throw new RuntimeException( "No invocation handler for method " 
		    + "\"" + method.toString() + "\"" ) ;
	    }
	}

	// handler should never be null here.

	return handler.invoke( proxy, method, args ) ;
    }
}

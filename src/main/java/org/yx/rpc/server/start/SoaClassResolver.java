package org.yx.rpc.server.start;

import org.yx.annotation.rpc.SoaClass;

public interface SoaClassResolver {

	Class<?> AUTO = Void.class;

	String solvePrefix(Class<?> targetClass, Class<?> refer);

	Class<?> getRefer(Class<?> targetClass, SoaClass sc);

}
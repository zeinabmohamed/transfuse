/**
 * Copyright 2013 John Ericksen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.androidtransfuse.scope;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author John Ericksen
 */
public class ScopeKey<T> {

    public static final String GET_METHOD = "get";
    private static final ConcurrentMap<String, ScopeKey<?>> SCOPE_CACHE = new ConcurrentHashMap<String, ScopeKey<?>>();
    private final String signature;

    private ScopeKey(String signature){
        if(signature == null){
            throw new NullPointerException("ScopeKey signature cannot be null");
        }
        this.signature = signature;
    }

    public static <S> ScopeKey<S> get(Class<S> clazz, String signature){
        ScopeKey result = SCOPE_CACHE.get(signature);
        if (result == null) {
            ScopeKey value = new ScopeKey(signature);
            result = SCOPE_CACHE.putIfAbsent(signature, value);
            if (result == null) {
                result = value;
            }
        }

        return result;
    }

    public static <S> ScopeKey<S> of(Class<S> clazz){
        return new ScopeKey<S>(clazz.getName());
    }

    public ScopeKey<T> annotatedBy(String annotation){
        return new ScopeKey<T>(this.signature + annotation);
    }

    @Override
    public String toString() {
        return signature;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        }
        if (!(o instanceof ScopeKey)){
            return false;
        }

        ScopeKey scopeKey = (ScopeKey) o;

        return signature.equals(scopeKey.signature);
    }

    @Override
    public int hashCode() {
        return signature.hashCode();
    }
}

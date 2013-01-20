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

/**
 * Utility class for referencing a static singleton scope.
 *
 * @author John Ericksen
 */
public final class SingletonScope {

    public static final String GET_INSTANCE = "getInstance";

    private static final Scope INSTANCE = new ConcurrentDoubleLockingScope();

    private SingletonScope() {
        //private singleton constructor
    }

    /**
     * Returns the singleton scope instance.
     *
     * @return singleton scope
     */
    public static Scope getInstance() {
        return INSTANCE;
    }
}

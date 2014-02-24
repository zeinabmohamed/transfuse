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
package org.androidtransfuse.analysis;

import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.util.matcher.Matcher;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * @author John Ericksen
 */
public class AnnotatedTypeMatcher implements Matcher<AnnotatedType>{

    private ASTType type;
    private ASTType annotation;

    public AnnotatedTypeMatcher(ASTType type, ASTType annotation) {
        this.type = type;
        this.annotation = annotation;
    }

    @Override
    public boolean matches(AnnotatedType input) {
        boolean match = true;

        if(type != null){
            match = input.getType().inheritsFrom(type);
        }
        if(annotation != null){
            match &= input.getAnnotation().equals(annotation);
        }


        return match;
    }

    @Override
    public boolean equals(Object that) {
        return EqualsBuilder.reflectionEquals(this, that);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}

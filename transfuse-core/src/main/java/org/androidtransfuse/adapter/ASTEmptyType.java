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
package org.androidtransfuse.adapter;

import com.google.common.collect.ImmutableSet;
import org.androidtransfuse.model.PackageClass;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.lang.annotation.Annotation;

/**
 * @author John Ericksen
 */
public class ASTEmptyType implements ASTType {

    private final String name;

    public ASTEmptyType(String name) {
        this.name = name;
    }

    @Override
    public ImmutableSet<ASTMethod> getMethods() {
        return ImmutableSet.of();
    }

    @Override
    public ImmutableSet<ASTField> getFields() {
        return ImmutableSet.of();
    }

    @Override
    public ImmutableSet<ASTConstructor> getConstructors() {
        return ImmutableSet.of();
    }

    @Override
    public boolean isConcreteClass() {
        return false;
    }

    @Override
    public ASTType getSuperClass() {
        return null;
    }

    @Override
    public ImmutableSet<ASTType> getInterfaces() {
        return ImmutableSet.of();
    }

    @Override
    public boolean isArray() {
        return false;
    }

    @Override
    public ImmutableSet<ASTType> getGenericParameters() {
        return ImmutableSet.of();
    }

    @Override
    public boolean inheritsFrom(ASTType type) {
        return false;
    }

    @Override
    public boolean extendsFrom(ASTType type) {
        return false;
    }

    @Override
    public boolean implementsFrom(ASTType type) {
        return false;
    }

    @Override
    public boolean isAnnotated(Class<? extends Annotation> annotation) {
        return false;
    }

    @Override
    public ImmutableSet<ASTAnnotation> getAnnotations() {
        return null;
    }

    @Override
    public <A extends Annotation> A getAnnotation(Class<A> annotation) {
        return null;
    }

    @Override
    public ASTAnnotation getASTAnnotation(Class annotation) {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public PackageClass getPackageClass() {
        return new PackageClass(null, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ASTEmptyType)) {
            return false;
        }

        ASTEmptyType that = (ASTEmptyType) o;

        return new EqualsBuilder().append(name, that.name).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(name).hashCode();
    }
}

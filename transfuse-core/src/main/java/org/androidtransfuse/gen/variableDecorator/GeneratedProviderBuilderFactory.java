/**
 * Copyright 2011-2015 John Ericksen
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
package org.androidtransfuse.gen.variableDecorator;

import org.androidtransfuse.gen.ProviderGenerator;
import org.androidtransfuse.model.InjectionNode;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * @author John Ericksen
 */
public class GeneratedProviderBuilderFactory {

    private final Provider<ProviderGenerator> providerGeneratorProvider;
    private final Provider<TypedExpressionFactory> typedExpressionFactoryProvider;

    @Inject
    public GeneratedProviderBuilderFactory(Provider<ProviderGenerator> providerGeneratorProvider,
                                           Provider<TypedExpressionFactory> typedExpressionFactoryProvider) {
        this.providerGeneratorProvider = providerGeneratorProvider;
        this.typedExpressionFactoryProvider = typedExpressionFactoryProvider;
    }

    public GeneratedProviderVariableBuilder buildProviderVariableBuilder(InjectionNode providerTypeInjectionNode){

        return new GeneratedProviderVariableBuilder(providerTypeInjectionNode, providerGeneratorProvider.get(), typedExpressionFactoryProvider.get());
    }
}

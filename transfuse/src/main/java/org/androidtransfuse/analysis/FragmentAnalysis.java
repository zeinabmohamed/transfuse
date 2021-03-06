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
package org.androidtransfuse.analysis;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import org.androidtransfuse.adapter.ASTMethod;
import org.androidtransfuse.adapter.ASTType;
import org.androidtransfuse.adapter.PackageClass;
import org.androidtransfuse.adapter.classes.ASTClassFactory;
import org.androidtransfuse.adapter.element.ASTElementFactory;
import org.androidtransfuse.adapter.element.ASTTypeBuilderVisitor;
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepository;
import org.androidtransfuse.analysis.repository.InjectionNodeBuilderRepositoryFactory;
import org.androidtransfuse.annotations.*;
import org.androidtransfuse.experiment.ComponentDescriptor;
import org.androidtransfuse.experiment.ScopesGeneration;
import org.androidtransfuse.experiment.generators.FragmentFactoryGenerator;
import org.androidtransfuse.experiment.generators.FragmentLayoutGenerator;
import org.androidtransfuse.experiment.generators.ObservesExpressionGenerator;
import org.androidtransfuse.experiment.generators.OnCreateInjectionGenerator;
import org.androidtransfuse.gen.componentBuilder.ComponentBuilderFactory;
import org.androidtransfuse.gen.componentBuilder.ListenerRegistrationGenerator;
import org.androidtransfuse.gen.variableBuilder.*;
import org.androidtransfuse.util.AndroidLiterals;

import javax.inject.Inject;
import javax.lang.model.type.TypeMirror;

import static org.androidtransfuse.util.TypeMirrorUtil.getTypeMirror;

/**
 * @author John Ericksen
 */
public class FragmentAnalysis implements Analysis<ComponentDescriptor> {

    private final ASTClassFactory astClassFactory;
    private final ASTElementFactory astElementFactory;
    private final AnalysisContextFactory analysisContextFactory;
    private final InjectionBindingBuilder injectionBindingBuilder;
    private final ASTTypeBuilderVisitor astTypeBuilderVisitor;
    private final InjectionNodeBuilderRepositoryFactory injectionNodeBuilderRepositoryFactory;
    private final ComponentBuilderFactory componentBuilderFactory;
    private final ListenerRegistrationGenerator.ListerRegistrationGeneratorFactory listenerRegistrationGeneratorFactory;
    private final ObservesExpressionGenerator.ObservesExpressionGeneratorFactory observesExpressionGeneratorFactory;
    private final ExtraInjectionNodeBuilder.ExtraInjectionNodeBuilderFactory extraInjectionNodeBuilderFactory;
    private final SystemServiceBindingInjectionNodeBuilder systemServiceBindingInjectionNodeBuilder;
    private final ResourceInjectionNodeBuilder resourceInjectionNodeBuilder;
    private final PreferenceInjectionNodeBuilder preferenceInjectionNodeBuilder;
    private final FragmentViewInjectionNodeBuilder fragmentViewInjectionNodeBuilder;
    private final OnCreateInjectionGenerator.InjectionGeneratorFactory injectionGeneratorFactory;
    private final FragmentLayoutGenerator fragmentLayoutGenerator;
    private final ScopesGeneration.ScopesGenerationFactory scopesGenerationFactory;
    private final ComponentAnalysis componentAnalysis;
    private final FragmentFactoryGenerator fragmentFactoryGenerator;

    @Inject
    public FragmentAnalysis(ASTClassFactory astClassFactory,
                            ASTElementFactory astElementFactory,
                            AnalysisContextFactory analysisContextFactory,
                            InjectionBindingBuilder injectionBindingBuilder,
                            ASTTypeBuilderVisitor astTypeBuilderVisitor,
                            InjectionNodeBuilderRepositoryFactory injectionNodeBuilderRepositoryFactory,
                            ComponentBuilderFactory componentBuilderFactory,
                            ListenerRegistrationGenerator.ListerRegistrationGeneratorFactory listenerRegistrationGeneratorFactory,
                            ObservesExpressionGenerator.ObservesExpressionGeneratorFactory observesExpressionGeneratorFactory,
                            ExtraInjectionNodeBuilder.ExtraInjectionNodeBuilderFactory extraInjectionNodeBuilderFactory,
                            SystemServiceBindingInjectionNodeBuilder systemServiceBindingInjectionNodeBuilder,
                            ResourceInjectionNodeBuilder resourceInjectionNodeBuilder,
                            PreferenceInjectionNodeBuilder preferenceInjectionNodeBuilder,
                            FragmentViewInjectionNodeBuilder fragmentViewInjectionNodeBuilder,
                            OnCreateInjectionGenerator.InjectionGeneratorFactory injectionGeneratorFactory,
                            FragmentLayoutGenerator fragmentLayoutGenerator,
                            ScopesGeneration.ScopesGenerationFactory scopesGenerationFactory,
                            ComponentAnalysis componentAnalysis,
                            FragmentFactoryGenerator fragmentFactoryGenerator) {
        this.astClassFactory = astClassFactory;
        this.astElementFactory = astElementFactory;
        this.analysisContextFactory = analysisContextFactory;
        this.injectionBindingBuilder = injectionBindingBuilder;
        this.astTypeBuilderVisitor = astTypeBuilderVisitor;
        this.injectionNodeBuilderRepositoryFactory = injectionNodeBuilderRepositoryFactory;
        this.componentBuilderFactory = componentBuilderFactory;
        this.extraInjectionNodeBuilderFactory = extraInjectionNodeBuilderFactory;
        this.systemServiceBindingInjectionNodeBuilder = systemServiceBindingInjectionNodeBuilder;
        this.resourceInjectionNodeBuilder = resourceInjectionNodeBuilder;
        this.preferenceInjectionNodeBuilder = preferenceInjectionNodeBuilder;
        this.fragmentViewInjectionNodeBuilder = fragmentViewInjectionNodeBuilder;
        this.listenerRegistrationGeneratorFactory = listenerRegistrationGeneratorFactory;
        this.observesExpressionGeneratorFactory = observesExpressionGeneratorFactory;
        this.injectionGeneratorFactory = injectionGeneratorFactory;
        this.fragmentLayoutGenerator = fragmentLayoutGenerator;
        this.scopesGenerationFactory = scopesGenerationFactory;
        this.componentAnalysis = componentAnalysis;
        this.fragmentFactoryGenerator = fragmentFactoryGenerator;
    }

    @Override
    public ComponentDescriptor analyze(ASTType astType) {
        Fragment fragmentAnnotation = astType.getAnnotation(Fragment.class);
        PackageClass fragmentClassName;
        ComponentDescriptor fragmentDescriptor = null;

        if (!astType.inherits(AndroidLiterals.FRAGMENT)) {
            //generated Android fragment
            fragmentClassName = componentAnalysis.buildComponentPackageClass(astType, fragmentAnnotation.name(), "Fragment");

            TypeMirror type = getTypeMirror(fragmentAnnotation, "type");

            ASTType fragmentType = type == null  || type.toString().equals("java.lang.Object") ? AndroidLiterals.FRAGMENT : type.accept(astTypeBuilderVisitor, null);

            AnalysisContext context = analysisContextFactory.buildAnalysisContext(buildVariableBuilderMap(fragmentType));

            fragmentDescriptor = new ComponentDescriptor(astType, fragmentType, fragmentClassName, context);

            componentAnalysis.setupGenerators(fragmentDescriptor, fragmentType, Fragment.class);

            //application generation profile
            setupFragmentProfile(fragmentDescriptor, astType);
        }

        return fragmentDescriptor;
    }

    private void setupFragmentProfile(ComponentDescriptor fragmentDescriptor, ASTType astType) {
        ASTMethod onCreateViewMethod = getASTMethod("onCreateView", AndroidLiterals.LAYOUT_INFLATER, AndroidLiterals.VIEW_GROUP, AndroidLiterals.BUNDLE);

        fragmentDescriptor.getGenerators().add(injectionGeneratorFactory.build(onCreateViewMethod, astType));
        fragmentDescriptor.getGenerators().add(scopesGenerationFactory.build(onCreateViewMethod));

        fragmentDescriptor.getGenerators().add(fragmentLayoutGenerator);

        fragmentDescriptor.getGenerators().add(fragmentFactoryGenerator);

        // onSaveInstanceState
        ASTMethod onSaveInstanceStateMethod = getASTMethod("onSaveInstanceState", AndroidLiterals.BUNDLE);
        fragmentDescriptor.getGenerators().add(componentBuilderFactory.buildFragmentOnSaveInstanceStateMethodCallbackGenerator(astClassFactory.getType(OnSaveInstanceState.class), onSaveInstanceStateMethod, getASTMethod("onCreateView", AndroidLiterals.LAYOUT_INFLATER, AndroidLiterals.VIEW_GROUP, AndroidLiterals.BUNDLE)));

        fragmentDescriptor.getGenerators().add(listenerRegistrationGeneratorFactory.build(getASTMethod("onCreateView", AndroidLiterals.LAYOUT_INFLATER, AndroidLiterals.VIEW_GROUP, AndroidLiterals.BUNDLE)));

        fragmentDescriptor.getGenerators().add(observesExpressionGeneratorFactory.build(
                getASTMethod("onCreateView", AndroidLiterals.LAYOUT_INFLATER, AndroidLiterals.VIEW_GROUP, AndroidLiterals.BUNDLE),
                getASTMethod("onDestroy"),
                getASTMethod("onResume"),
                getASTMethod("onPause")
        ));

    }

    private ASTMethod getASTMethod(String methodName, ASTType... args) {
        return getASTMethod(AndroidLiterals.FRAGMENT, methodName, args);
    }

    private ASTMethod getASTMethod(ASTType type, String methodName, ASTType... args) {
        return astElementFactory.findMethod(type, methodName, args);
    }

    private InjectionNodeBuilderRepository buildVariableBuilderMap(ASTType fragmentType) {

        InjectionNodeBuilderRepository injectionNodeBuilderRepository = componentAnalysis.setupInjectionNodeBuilderRepository(fragmentType, Fragment.class);

        injectionNodeBuilderRepository.putType(AndroidLiterals.FRAGMENT, injectionBindingBuilder.buildThis(AndroidLiterals.FRAGMENT));
        injectionNodeBuilderRepository.putType(AndroidLiterals.ACTIVITY, injectionBindingBuilder.dependency(AndroidLiterals.FRAGMENT).invoke(AndroidLiterals.ACTIVITY, "getActivity").build());
        injectionNodeBuilderRepository.putType(AndroidLiterals.CONTEXT, injectionBindingBuilder.dependency(AndroidLiterals.FRAGMENT).invoke(AndroidLiterals.CONTEXT, "getActivity").build());
        injectionNodeBuilderRepository.putType(AndroidLiterals.FRAGMENT_MANAGER, injectionBindingBuilder.dependency(AndroidLiterals.FRAGMENT).invoke(AndroidLiterals.FRAGMENT_MANAGER, "getFragmentManager").build());


        while(!fragmentType.equals(AndroidLiterals.FRAGMENT) && fragmentType.inherits(AndroidLiterals.FRAGMENT)){
            injectionNodeBuilderRepository.putType(fragmentType, injectionBindingBuilder.buildThis(fragmentType));
            fragmentType = fragmentType.getSuperClass();
        }

        injectionNodeBuilderRepository.putAnnotation(Extra.class, extraInjectionNodeBuilderFactory.build(new ExtraVariableBuilder.GetExtraExpressionBuilder() {
            @Override
            public JExpression buildGetExtraBundle(JExpression expression) {
                return JExpr._this().invoke("getArguments");
            }
        }));
        injectionNodeBuilderRepository.putAnnotation(Resource.class, resourceInjectionNodeBuilder);
        injectionNodeBuilderRepository.putAnnotation(SystemService.class, systemServiceBindingInjectionNodeBuilder);
        injectionNodeBuilderRepository.putAnnotation(Preference.class, preferenceInjectionNodeBuilder);
        injectionNodeBuilderRepository.putAnnotation(org.androidtransfuse.annotations.View.class, fragmentViewInjectionNodeBuilder);

        injectionNodeBuilderRepository.addRepository(injectionNodeBuilderRepositoryFactory.buildModuleConfiguration());

        return injectionNodeBuilderRepository;
    }
}

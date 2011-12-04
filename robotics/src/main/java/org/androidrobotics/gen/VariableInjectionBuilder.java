package org.androidrobotics.gen;

import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JExpression;
import org.androidrobotics.analysis.AnalysisDependencyProcessingCallback;
import org.androidrobotics.analysis.adapter.ASTType;
import org.androidrobotics.model.InjectionNode;

import javax.inject.Inject;

/**
 * @author John Ericksen
 */
public class VariableInjectionBuilder extends VariableInjectionBuilderBase {

    @Inject
    public VariableInjectionBuilder(JCodeModel codeModel, UniqueVariableNamer variableNamer, InjectionInvocationBuilder injectionInvocationBuilder) {
        super(codeModel, variableNamer, injectionInvocationBuilder);
    }

    @Override
    public JExpression buildVariable(InjectionBuilderContext injectionBuilderContext) {
        return innerBuildVariable(injectionBuilderContext.getInjectionNode().getClassName(), injectionBuilderContext);
    }

    @Override
    public InjectionNode processInjectionNode(ASTType astType, AnalysisDependencyProcessingCallback callback) {
        return callback.processInjectionNode(astType);
    }
}

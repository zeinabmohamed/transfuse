package org.androidrobotics.gen;

import com.sun.codemodel.JExpr;
import com.sun.codemodel.JExpression;
import org.androidrobotics.analysis.AnalysisDependencyProcessingCallback;
import org.androidrobotics.analysis.adapter.ASTType;
import org.androidrobotics.model.InjectionNode;

/**
 * @author John Ericksen
 */
public class ContextVariableBuilder implements VariableBuilder {
    @Override
    public JExpression buildVariable(InjectionBuilderContext injectionBuilderContext) {
        return JExpr._this();
    }

    @Override
    public InjectionNode processInjectionNode(ASTType astType, AnalysisDependencyProcessingCallback callback) {
        return new InjectionNode(astType);
    }
}

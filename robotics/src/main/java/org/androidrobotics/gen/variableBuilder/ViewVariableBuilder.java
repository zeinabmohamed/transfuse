package org.androidrobotics.gen.variableBuilder;

import com.sun.codemodel.*;
import org.androidrobotics.analysis.adapter.ASTType;
import org.androidrobotics.gen.InjectionBuilderContext;
import org.androidrobotics.model.InjectionNode;
import org.androidrobotics.model.r.ResourceIdentifier;

public class ViewVariableBuilder implements VariableBuilder {

    private static final String FIND_VIEW = "findViewById";

    private JType viewType;
    private int viewId;
    private InjectionNode activityInjectionNode;
    private JCodeModel codeModel;

    public ViewVariableBuilder(int viewId, InjectionNode activityInjectionNode, JType viewType, JCodeModel codeModel) {
        this.viewId = viewId;
        this.activityInjectionNode = activityInjectionNode;
        this.viewType = viewType;
        this.codeModel = codeModel;
    }

    @Override
    public JExpression buildVariable(InjectionBuilderContext injectionBuilderContext, InjectionNode injectionNode) {
        JExpression contextVar = injectionBuilderContext.buildVariable(activityInjectionNode);

        ResourceIdentifier viewResourceIdentifier = injectionBuilderContext.getRResource().getResourceIdentifier(viewId);

        ASTType rInnerType = viewResourceIdentifier.getRInnerType();

        JClass rInnerRef = codeModel.ref(rInnerType.getName());

        JFieldRef viewIdRef = rInnerRef.staticRef(viewResourceIdentifier.getName());

        return JExpr.cast(viewType, contextVar.invoke(FIND_VIEW).arg(viewIdRef));
    }
}
package org.androidrobotics.gen.variableBuilder;

import android.app.Activity;
import com.sun.codemodel.JCodeModel;
import org.androidrobotics.analysis.AnalysisContext;
import org.androidrobotics.analysis.InjectionPointFactory;
import org.androidrobotics.analysis.RoboticsAnalysisException;
import org.androidrobotics.analysis.adapter.ASTAnnotation;
import org.androidrobotics.analysis.adapter.ASTClassFactory;
import org.androidrobotics.analysis.adapter.ASTType;
import org.androidrobotics.model.FieldInjectionPoint;
import org.androidrobotics.model.InjectionNode;

import javax.inject.Inject;
import javax.lang.model.element.AnnotationValue;

/**
 * @author John Ericksen
 */
public class ViewInjectionNodeBuilder implements InjectionNodeBuilder {

    private JCodeModel codeModel;
    private ASTClassFactory astClassFactory;
    private InjectionPointFactory injectionPointFactory;

    @Inject
    public ViewInjectionNodeBuilder(JCodeModel codeModel, ASTClassFactory astClassFactory, InjectionPointFactory injectionPointFactory) {
        this.codeModel = codeModel;
        this.astClassFactory = astClassFactory;
        this.injectionPointFactory = injectionPointFactory;
    }

    @Override
    public InjectionNode buildInjectionNode(ASTType astType, AnalysisContext context, ASTAnnotation annotation) {
        InjectionNode injectionNode = new InjectionNode(astType);

        ASTType activityType = astClassFactory.buildASTClassType(Activity.class);
        FieldInjectionPoint activityInjectionNode = injectionPointFactory.buildInjectionPoint(activityType, context);

        Integer viewId = (Integer) ((AnnotationValue) annotation.getProperty("value")).getValue();

        try {
            injectionNode.addAspect(VariableBuilder.class, new InnerViewVariableBuilder(viewId, activityInjectionNode.getInjectionNode(), codeModel.parseType(astType.getName())));
        } catch (ClassNotFoundException e) {
            throw new RoboticsAnalysisException("Unable to parse type " + astType.getName(), e);
        }

        return injectionNode;
    }
}
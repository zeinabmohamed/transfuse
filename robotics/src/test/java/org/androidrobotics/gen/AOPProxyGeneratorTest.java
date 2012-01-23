package org.androidrobotics.gen;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;
import com.sun.codemodel.JClassAlreadyExistsException;
import org.androidrobotics.analysis.Analyzer;
import org.androidrobotics.analysis.InjectionPointFactory;
import org.androidrobotics.analysis.SimpleAnalysisContextFactory;
import org.androidrobotics.analysis.adapter.ASTClassFactory;
import org.androidrobotics.analysis.adapter.ASTMethod;
import org.androidrobotics.analysis.adapter.ASTType;
import org.androidrobotics.analysis.astAnalyzer.AOPProxyAspect;
import org.androidrobotics.config.RoboticsGenerationGuiceModule;
import org.androidrobotics.gen.proxy.AOPProxyGenerator;
import org.androidrobotics.gen.proxy.MockDelegate;
import org.androidrobotics.model.InjectionNode;
import org.androidrobotics.model.PackageClass;
import org.androidrobotics.util.JavaUtilLogger;
import org.junit.Before;
import org.junit.Test;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import static junit.framework.Assert.*;

/**
 * @author John Ericksen
 */
public class AOPProxyGeneratorTest {

    public static final String TEST_VALUE = "test";
    private static final String INPUT_VALUE = "input";
    private static final String EXECUTE_METHOD = "execute";
    private static final String SETVALUE_METHOD = "setValue";
    private static final String PASSTHROUGH_METHOD = "passThroughValue";
    private static final String TEST_PACKLAGE = "org.androidrobotics.gen";
    private static final String TEST_NAME = "MockDelegate_AOPProxy";
    private static final String PRIMITIVE_METHOD = "primitiveCall";
    private static final PackageClass TEST_PACKAGE_FILENAME = new PackageClass(TEST_PACKLAGE, TEST_NAME);

    private InjectionNode delegateInjectionNode;
    @Inject
    private AOPProxyGenerator aopProxyGenerator;
    @Inject
    private ASTClassFactory astClassFactory;
    @Inject
    private CodeGenerationUtil codeGenerationUtil;
    @Inject
    private Analyzer analyzer;
    @Inject
    private SimpleAnalysisContextFactory contextFactory;
    private ASTType delegateAST;
    @Inject
    private MockMethodInterceptor mockMethodInterceptor;
    @Inject
    private InjectionFragmentGeneratorHarness fragmentGeneratorHarness;
    @Inject
    private InjectionPointFactory injectionPointFactory;
    @Inject
    private SimpleAnalysisContextFactory simpleAnalysisContextFactory;
    private ASTType mockMethdInterceptorAST;
    @Inject
    private AOPProxyAspect aopProxyAspect;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(Stage.DEVELOPMENT, new RoboticsGenerationGuiceModule(new JavaUtilLogger(this)));
        injector.injectMembers(this);

        delegateAST = astClassFactory.buildASTClassType(MockDelegate.class);
        delegateInjectionNode = analyzer.analyze(delegateAST, delegateAST, contextFactory.buildContext());

        mockMethdInterceptorAST = astClassFactory.buildASTClassType(MockMethodInterceptor.class);
    }

    private InjectionNode buildMethodInterceptorInjectionNode() {
        return injectionPointFactory.buildInjectionNode(mockMethdInterceptorAST, simpleAnalysisContextFactory.buildContext());
    }

    @Test
    public void testAOPProxy() throws JClassAlreadyExistsException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        aopProxyAspect.addInterceptor(buildASTClassMethod(delegateAST, EXECUTE_METHOD), buildMethodInterceptorInjectionNode());
        delegateInjectionNode.addAspect(AOPProxyAspect.class, aopProxyAspect);

        buildAndTest(delegateInjectionNode);
    }

    @Test
    public void testAOPProxyMethodWithParameters() throws JClassAlreadyExistsException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        aopProxyAspect.addInterceptor(buildASTClassMethod(delegateAST, SETVALUE_METHOD), buildMethodInterceptorInjectionNode());
        delegateInjectionNode.addAspect(AOPProxyAspect.class, aopProxyAspect);

        buildAndTest(delegateInjectionNode);


    }

    @Test
    public void testAOPProxyMethodWithReturn() throws JClassAlreadyExistsException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        aopProxyAspect.addInterceptor(buildASTClassMethod(delegateAST, PASSTHROUGH_METHOD), buildMethodInterceptorInjectionNode());
        delegateInjectionNode.addAspect(AOPProxyAspect.class, aopProxyAspect);

        buildAndTest(delegateInjectionNode);
    }

    @Test
    public void testAOPProxyMethodWithPrimitiveReturn() throws JClassAlreadyExistsException, IOException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        aopProxyAspect.addInterceptor(buildASTClassMethod(delegateAST, PRIMITIVE_METHOD), buildMethodInterceptorInjectionNode());
        delegateInjectionNode.addAspect(AOPProxyAspect.class, aopProxyAspect);

        buildAndTest(delegateInjectionNode);
    }

    @Test
    public void testMultiAOPProxyMethod() throws ClassNotFoundException, JClassAlreadyExistsException, IOException, IllegalAccessException, InstantiationException {
        aopProxyAspect.addInterceptor(buildASTClassMethod(delegateAST, PASSTHROUGH_METHOD), buildMethodInterceptorInjectionNode());
        aopProxyAspect.addInterceptor(buildASTClassMethod(delegateAST, PASSTHROUGH_METHOD), buildMethodInterceptorInjectionNode());
        delegateInjectionNode.addAspect(AOPProxyAspect.class, aopProxyAspect);

        buildAndTest(delegateInjectionNode);
    }

    private void buildAndTest(InjectionNode delegateInjectionNode) throws ClassNotFoundException, JClassAlreadyExistsException, IOException, IllegalAccessException, InstantiationException {
        fragmentGeneratorHarness.buildProvider(delegateInjectionNode, TEST_PACKAGE_FILENAME);

        ClassLoader classLoader = codeGenerationUtil.build(false);
        Class<Provider<MockDelegate>> generatedFactoryClass = (Class<Provider<MockDelegate>>) classLoader.loadClass(TEST_PACKAGE_FILENAME.getFullyQualifiedName());

        assertNotNull(generatedFactoryClass);
        Provider<MockDelegate> provider = generatedFactoryClass.newInstance();

        runMockDelegateTests(provider.get());
    }

    private ASTMethod buildASTClassMethod(ASTType delegateAST, String methodName) {
        for (ASTMethod method : delegateAST.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        assert false : "method not found";
        return null;
    }

    private void runMockDelegateTests(MockDelegate proxy) {
        proxy.execute();
        assertEquals(TEST_VALUE, proxy.getValue());
        proxy.setValue(INPUT_VALUE);
        assertEquals(TEST_VALUE, proxy.passThroughValue(INPUT_VALUE));
        assertTrue(proxy.primitiveCall());

        assertTrue(proxy.validate(INPUT_VALUE, INPUT_VALUE));
    }

}
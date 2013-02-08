/*
 * generated by Xtext
 */
package org.eclipse.xtend.core;

import org.eclipse.xtend.core.compiler.XtendCompiler;
import org.eclipse.xtend.core.compiler.XtendOutputConfigurationProvider;
import org.eclipse.xtend.core.conversion.XtendValueConverterService;
import org.eclipse.xtend.core.formatting.XtendFormatter;
import org.eclipse.xtend.core.imports.XtendImportsConfiguration;
import org.eclipse.xtend.core.jvmmodel.IXtendJvmAssociations;
import org.eclipse.xtend.core.jvmmodel.XtendJvmModelInferrer2;
import org.eclipse.xtend.core.linking.XtendLinkingDiagnosticMessageProvider;
import org.eclipse.xtend.core.naming.XtendQualifiedNameProvider;
import org.eclipse.xtend.core.resource.XtendLocationInFileProvider;
import org.eclipse.xtend.core.resource.XtendResourceDescriptionStrategy;
import org.eclipse.xtend.core.scoping.XtendImportedNamespaceScopeProvider;
import org.eclipse.xtend.core.scoping.XtendScopeProvider;
import org.eclipse.xtend.core.typesystem.DispatchAndExtensionAwareReentrantTypeResolver;
import org.eclipse.xtend.core.typesystem.ExtensionAwareScopeProvider;
import org.eclipse.xtend.core.typesystem.TypeDeclarationAwareBatchTypeResolver;
import org.eclipse.xtend.core.typesystem.XtendTypeComputer;
import org.eclipse.xtend.core.typing.XtendExpressionHelper;
import org.eclipse.xtend.core.validation.XtendConfigurableIssueCodes;
import org.eclipse.xtend.core.validation.XtendEarlyExitValidator;
import org.eclipse.xtend.core.validation.XtendJavaValidator;
import org.eclipse.xtend.core.validation.XtendJavaValidator2;
import org.eclipse.xtend.core.xtend.XtendFactory;
import org.eclipse.xtext.common.types.util.FeatureOverridesService;
import org.eclipse.xtext.common.types.util.TypeArgumentContextProvider;
import org.eclipse.xtext.common.types.util.TypeConformanceComputer;
import org.eclipse.xtext.common.types.util.VisibilityService;
import org.eclipse.xtext.conversion.IValueConverterService;
import org.eclipse.xtext.generator.IFilePostProcessor;
import org.eclipse.xtext.generator.IOutputConfigurationProvider;
import org.eclipse.xtext.linking.ILinkingDiagnosticMessageProvider;
import org.eclipse.xtext.linking.LinkingScopeProviderBinding;
import org.eclipse.xtext.naming.IQualifiedNameProvider;
import org.eclipse.xtext.resource.IDefaultResourceDescriptionStrategy;
import org.eclipse.xtext.resource.ILocationInFileProvider;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.scoping.impl.AbstractDeclarativeScopeProvider;
import org.eclipse.xtext.serializer.tokens.SerializerScopeProviderBinding;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.validation.ConfigurableIssueCodesProvider;
import org.eclipse.xtext.xbase.XbaseFactory;
import org.eclipse.xtext.xbase.annotations.typesystem.XbaseWithAnnotationsBatchScopeProvider;
import org.eclipse.xtext.xbase.compiler.TypeReferenceSerializer;
import org.eclipse.xtext.xbase.compiler.TypeReferenceSerializer2;
import org.eclipse.xtext.xbase.compiler.XbaseCompiler;
import org.eclipse.xtext.xbase.compiler.output.TraceAwarePostProcessor;
import org.eclipse.xtext.xbase.formatting.IBasicFormatter;
import org.eclipse.xtext.xbase.imports.IImportsConfiguration;
import org.eclipse.xtext.xbase.interpreter.IExpressionInterpreter;
import org.eclipse.xtext.xbase.interpreter.impl.XbaseInterpreter2;
import org.eclipse.xtext.xbase.jvmmodel.IJvmModelInferrer;
import org.eclipse.xtext.xbase.jvmmodel.JvmModelAssociator;
import org.eclipse.xtext.xbase.resource.BatchLinkableResource;
import org.eclipse.xtext.xbase.scoping.batch.IBatchScopeProvider;
import org.eclipse.xtext.xbase.scoping.batch.XbaseBatchScopeProvider;
import org.eclipse.xtext.xbase.serializer.SerializerScopeProvider;
import org.eclipse.xtext.xbase.typesystem.computation.ITypeComputer;
import org.eclipse.xtext.xbase.typesystem.internal.DefaultBatchTypeResolver;
import org.eclipse.xtext.xbase.typesystem.internal.DefaultReentrantTypeResolver;
import org.eclipse.xtext.xbase.typesystem.legacy.LegacyFeatureOverridesService;
import org.eclipse.xtext.xbase.typesystem.legacy.LegacyTypeArgumentContextProvider;
import org.eclipse.xtext.xbase.typesystem.legacy.LegacyTypeConformanceComputer;
import org.eclipse.xtext.xbase.typesystem.legacy.LegacyVisibilityService;
import org.eclipse.xtext.xbase.typesystem.legacy.XbaseBatchTypeProvider;
import org.eclipse.xtext.xbase.typing.ITypeProvider;
import org.eclipse.xtext.xbase.util.XExpressionHelper;
import org.eclipse.xtext.xbase.validation.EarlyExitValidator;
import org.eclipse.xtext.xbase.validation.FeatureNameValidator;
import org.eclipse.xtext.xbase.validation.LogicalContainerAwareFeatureNameValidator;

import com.google.inject.Binder;
import com.google.inject.name.Names;

/**
 * Use this class to register components to be used at runtime / without the Equinox extension registry.
 */
@SuppressWarnings("deprecation")
public class XtendRuntimeModule extends org.eclipse.xtend.core.AbstractXtendRuntimeModule {
	
	public XbaseFactory bindXbaseFactory() {
		return XbaseFactory.eINSTANCE;
	}
	
	public Class<? extends XExpressionHelper> bindXExpressionHelper() {
		return XtendExpressionHelper.class;
	}
	
	@Override
	public Class<? extends IValueConverterService> bindIValueConverterService() {
		return XtendValueConverterService.class;
	}
	
	@Override
	public void configureIScopeProviderDelegate(Binder binder) {
		binder.bind(IScopeProvider.class).annotatedWith(Names.named(AbstractDeclarativeScopeProvider.NAMED_DELEGATE))
		.to(XtendImportedNamespaceScopeProvider.class);
	}

	@Override
	public Class<? extends IQualifiedNameProvider> bindIQualifiedNameProvider() {
		return XtendQualifiedNameProvider.class;
	}
	
	@Override
	public Class <? extends IDefaultResourceDescriptionStrategy> bindIDefaultResourceDescriptionStrategy() {
		return XtendResourceDescriptionStrategy.class;
	}

	public Class<? extends JvmModelAssociator> bindJvmModelAssociator() {
		return IXtendJvmAssociations.Impl.class;
	}

	public Class<? extends EarlyExitValidator> bindEarlyExitValidator() {
		return XtendEarlyExitValidator.class;
	}
	
	public Class<? extends IOutputConfigurationProvider> bindIOutputConfigurationProvider() {
		return XtendOutputConfigurationProvider.class;
	}
	
	@Override
	public java.lang.Class<? extends IScopeProvider> bindIScopeProvider() {
		return XtendScopeProvider.class;
	}

	public Class<? extends IFilePostProcessor> bindPostProcessor() {
		return TraceAwarePostProcessor.class;
	}
	
	@Override
	public Class<? extends ILocationInFileProvider> bindILocationInFileProvider() {
		return XtendLocationInFileProvider.class;
	}

	public Class<? extends ILinkingDiagnosticMessageProvider> bindILinkingDiagnosticMessageProvider() {
		return XtendLinkingDiagnosticMessageProvider.class;
	}
	
	public Class<? extends IBasicFormatter> bindIBasicFormatter() {
		return XtendFormatter.class;
	}

	public Class<? extends IImportsConfiguration> bindIImportsConfiguration() {
		return XtendImportsConfiguration.class;
	}

	@Override
	public Class<? extends ConfigurableIssueCodesProvider> bindConfigurableIssueCodesProvider() {
		return XtendConfigurableIssueCodes.class;
	}
	
	public XtendFactory bindXtendFactory() {
		return XtendFactory.eINSTANCE;
	}

	@Override
	public void configureLinkingIScopeProvider(Binder binder) {
		binder.bind(IScopeProvider.class).annotatedWith(LinkingScopeProviderBinding.class)
				.to(ExtensionAwareScopeProvider.class);
	}
	
	@Override
	public void configureSerializerIScopeProvider(Binder binder) {
		binder.bind(IScopeProvider.class).annotatedWith(SerializerScopeProviderBinding.class)
			.to(SerializerScopeProvider.class);
	}

	public Class<? extends IBatchScopeProvider> bindIBatchScopeProvider() {
		return ExtensionAwareScopeProvider.class;
	}

	@Override
	public Class<? extends XtextResource> bindXtextResource() {
		return BatchLinkableResource.class;
	}

	@Override
	public Class<? extends ITypeProvider> bindITypeProvider() {
		return XbaseBatchTypeProvider.class;
	}

	public Class<? extends DefaultBatchTypeResolver> bindDefaultBatchTypeResolver() {
		return TypeDeclarationAwareBatchTypeResolver.class;
	}

	public Class<? extends DefaultReentrantTypeResolver> bindReentrantTypeResolver() {
		return DispatchAndExtensionAwareReentrantTypeResolver.class;
	}

	public Class<? extends ITypeComputer> bindTypeComputer() {
		return XtendTypeComputer.class;
	}

	public Class<? extends XbaseBatchScopeProvider> bindBatchScopeProvider() {
		return XbaseWithAnnotationsBatchScopeProvider.class;
	}

	public Class<? extends XbaseCompiler> bindXbaseCompiler() {
		return XtendCompiler.class;
	}

	@Override
	public Class<? extends IExpressionInterpreter> bindIExpressionInterpreter() {
		return XbaseInterpreter2.class;
	}

	public Class<? extends IJvmModelInferrer> bindIJvmModelInferrer() {
		return XtendJvmModelInferrer2.class;
	}

	@Override
	public Class<? extends XtendJavaValidator> bindXtendJavaValidator() {
		return null;
	}

	@SingletonBinding(eager = true)
	public Class<? extends XtendJavaValidator2> bindXtendJavaValidator2() {
		return XtendJavaValidator2.class;
	}

	public Class<? extends VisibilityService> bindVisibilityService() {
		return LegacyVisibilityService.class;
	}

	public Class<? extends FeatureOverridesService> bindFeatureOverridesService() {
		return LegacyFeatureOverridesService.class;
	}

	@Override
	public Class<? extends TypeArgumentContextProvider> bindTypeArgumentContextProvider() {
		return LegacyTypeArgumentContextProvider.class;
	}

	public Class<? extends TypeReferenceSerializer> bindTypeReferenceSerializer() {
		return TypeReferenceSerializer2.class;
	}

	public Class<? extends FeatureNameValidator> bindFeatureNameValidator() {
		return LogicalContainerAwareFeatureNameValidator.class;
	}
	
	@Override
	public Class<? extends TypeConformanceComputer> bindTypeConformanceComputer() {
		return LegacyTypeConformanceComputer.class;
	}
}

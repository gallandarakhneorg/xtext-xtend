/*******************************************************************************
 * Copyright (c) 2011 itemis AG (http://www.itemis.eu) and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.xtend.maven;

import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Lists.newArrayList;
import static org.eclipse.xtext.util.Strings.concat;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map.Entry;
import java.util.Arrays;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.toolchain.Toolchain;
import org.apache.maven.toolchain.ToolchainManager;
import org.apache.maven.toolchain.java.DefaultJavaToolChain;
import org.codehaus.plexus.util.DirectoryScanner;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.xtend.core.compiler.batch.XtendBatchCompiler;
import org.eclipse.xtend.lib.macro.file.Path;
import org.eclipse.xtext.xbase.file.ProjectConfig;
import org.eclipse.xtext.xbase.file.RuntimeWorkspaceConfigProvider;
import org.eclipse.xtext.xbase.file.WorkspaceConfig;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure1;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;

/**
 * @author Michael Clay - Initial contribution and API
 */
public abstract class AbstractXtendCompilerMojo extends AbstractXtendMojo {
	protected static final Predicate<String> FILE_EXISTS = new Predicate<String>() {

		public boolean apply(String filePath) {
			return new File(filePath).exists();
		}
	};
	
	/**
	 * JDK compliance level.
	 *
	 * @parameter expression="${maven.compiler.target}" default-value="1.6"
	 */
	private String complianceLevel;

	/**
	 * @component
	 */
	private ToolchainManager toolchainManager;

	/**
	 * The current build session instance. This is used for toolchain manager API calls.
	 *
	 * @parameter expression="${session}"
	 * @required
	 * @readonly
	 */
	private MavenSession session;

	@Inject
	protected Provider<XtendBatchCompiler> xtendBatchCompilerProvider;

	/**
	 * Xtend-File encoding argument for the compiler.
	 * 
	 * @parameter expression="${encoding}" default-value="${project.build.sourceEncoding}"
	 */
	protected String encoding;

	/**
	 * Set this to false to suppress the creation of *._trace files.
	 * 
	 * @parameter default-value="true" expression="${writeTraceFiles}"
	 */
	protected boolean writeTraceFiles;

	/**
	 * Location of the Xtend settings file.
	 * 
	 * @parameter default-value="${basedir}/.settings/org.eclipse.xtend.core.Xtend.prefs"
	 * @readonly
	 */
	private String propertiesFileLocation;

	@Inject
	private RuntimeWorkspaceConfigProvider workspaceConfigProvider;

	@Deprecated
	protected XtendBatchCompiler createXtendBatchCompiler() {
		Injector injector = new XtendMavenStandaloneSetup().createInjectorAndDoEMFRegistration();
		XtendBatchCompiler instance = injector.getInstance(XtendBatchCompiler.class);
		instance.setComplianceLevel(complianceLevel);
		instance.setBasePath(project.getBasedir().getAbsolutePath());
		return instance;
	}
	
	/**
	 * @since 2.8
	 */
	protected XtendBatchCompiler getConfiguredBatchCompiler() {
		XtendBatchCompiler xtend2BatchCompiler = xtendBatchCompilerProvider.get();
		xtend2BatchCompiler.setComplianceLevel(complianceLevel);
		xtend2BatchCompiler.setBasePath(project.getBasedir().getAbsolutePath());
		return xtend2BatchCompiler;
	}

	protected void compile(XtendBatchCompiler xtend2BatchCompiler, String classPath, List<String> sourceDirectories,
			String outputPath) throws MojoExecutionException {
		configureWorkspace(sourceDirectories, outputPath);
		xtend2BatchCompiler.setResourceSetProvider(new MavenProjectResourceSetProvider(project));
		Iterable<String> filtered = filter(sourceDirectories, FILE_EXISTS);
		if (Iterables.isEmpty(filtered)) {
			getLog().info(
					"skip compiling sources because the configured directory '" + Iterables.toString(sourceDirectories)
							+ "' does not exists.");
			return;
		}
		getLog().debug("Set temp directory: " + getTempDirectory());
		xtend2BatchCompiler.setTempDirectory(getTempDirectory());
		getLog().debug("Set DeleteTempDirectory: " + false);
		xtend2BatchCompiler.setDeleteTempDirectory(false);
		getLog().debug("Set classpath: " + classPath);
		xtend2BatchCompiler.setClassPath(classPath);
		String bootClassPath = getBootClassPath();
		getLog().debug("Set bootClasspath: " + bootClassPath);
		xtend2BatchCompiler.setBootClassPath(bootClassPath);
		getLog().debug("Set source path: " + concat(File.pathSeparator, newArrayList(filtered)));
		xtend2BatchCompiler.setSourcePath(concat(File.pathSeparator, newArrayList(filtered)));
		getLog().debug("Set output path: " + outputPath);
		xtend2BatchCompiler.setOutputPath(outputPath);
		getLog().debug("Set encoding: " + encoding);
		xtend2BatchCompiler.setFileEncoding(encoding);
		getLog().debug("Set writeTraceFiles: " + writeTraceFiles);
		xtend2BatchCompiler.setWriteTraceFiles(writeTraceFiles);
		if (!xtend2BatchCompiler.compile()) {
			throw new MojoExecutionException("Error compiling xtend sources in '"
					+ concat(File.pathSeparator, newArrayList(filtered)) + "'.");
		}
	}

	private String getBootClassPath() {
		Toolchain toolchain = toolchainManager.getToolchainFromBuildContext("jdk", session);
		if (toolchain instanceof DefaultJavaToolChain) {
			DefaultJavaToolChain javaToolChain = (DefaultJavaToolChain) toolchain;
			getLog().info("Using toolchain " + javaToolChain);

			String[] includes = {"jre/lib/*", "jre/lib/ext/*", "jre/lib/endorsed/*"};
			String[] excludes = new String[0];
			Xpp3Dom config = (Xpp3Dom) javaToolChain.getModel().getConfiguration();
			if (config != null) {
				Xpp3Dom bootClassPath = config.getChild("bootClassPath");
				if (bootClassPath != null) {
					Xpp3Dom includeParent = bootClassPath.getChild("includes");
					if (includeParent != null) {
						includes = getValues(includeParent.getChildren("include"));
					}
					Xpp3Dom excludeParent = bootClassPath.getChild("excludes");
					if (excludeParent != null) {
						excludes = getValues(excludeParent.getChildren("exclude"));
					}
				}
			}

			return scanBootclasspath(javaToolChain.getJavaHome(), includes, excludes);
		}
		return "";
	}

	private String scanBootclasspath(String javaHome, String[] includes, String[] excludes) {
		getLog().debug("Scanning bootClassPath:\n"
				+"\tjavaHome = " + javaHome + "\n"
				+"\tincludes = "+ Arrays.toString(includes) + "\n"
				+"\texcludes = "+ Arrays.toString(excludes)
		);
		DirectoryScanner scanner = new DirectoryScanner();
		scanner.setBasedir(new File(javaHome));
		scanner.setIncludes(includes);
		scanner.setExcludes(excludes);
		scanner.scan();

		StringBuilder bootClassPath = new StringBuilder();
		String[] includedFiles = scanner.getIncludedFiles();
		for (int i = 0; i < includedFiles.length; i++) {
			if (i > 0) {
				bootClassPath.append(File.pathSeparator);
			}
			bootClassPath.append(new File(javaHome, includedFiles[i]).getAbsolutePath());
		}
		return bootClassPath.toString();
	}

	private String[] getValues(Xpp3Dom[] children) {
		String[] values = new String[children.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = children[i].getValue();
		}
		return values;
	}

	private void configureWorkspace(List<String> sourceDirectories, String outputPath) throws MojoExecutionException {
		WorkspaceConfig workspaceConfig = new WorkspaceConfig(project.getBasedir().getParentFile().getAbsolutePath());
		ProjectConfig projectConfig = new ProjectConfig(project.getBasedir().getName());
		URI absoluteRootPath = project.getBasedir().getAbsoluteFile().toURI();
		URI relativizedTarget = absoluteRootPath.relativize(new File(outputPath).toURI());
		if (relativizedTarget.isAbsolute()) {
			throw new MojoExecutionException("Output path '" + outputPath + "' must be a child of the project folder '"
					+ absoluteRootPath + "'");
		}
		for (String source : sourceDirectories) {
			URI relativizedSrc = absoluteRootPath.relativize(new File(source).toURI());
			if (relativizedSrc.isAbsolute()) {
				throw new MojoExecutionException("Source folder " + source + " must be a child of the project folder "
						+ absoluteRootPath);
			}
			projectConfig.addSourceFolderMapping(relativizedSrc.getPath(), relativizedTarget.getPath());
		}
		workspaceConfig.addProjectConfig(projectConfig);
		workspaceConfigProvider.setWorkspaceConfig(workspaceConfig);
		if (getLog().isDebugEnabled()) {
			getLog().debug("WS config root: " + workspaceConfig.getAbsoluteFileSystemPath());
			getLog().debug("Project name: " + projectConfig.getName());
			getLog().debug("Project root path: " + projectConfig.getRootPath());
			for (Entry<Path, Path> entry : projectConfig.getSourceFolderMappings().entrySet()) {
				getLog().debug("Source path: " + entry.getKey() + " -> " + entry.getValue());
			}
		}
	}

	protected abstract String getTempDirectory();

	protected void addDependencies(Set<String> classPath, List<Artifact> dependencies) {
		for (Artifact artifact : dependencies) {
			classPath.add(artifact.getFile().getAbsolutePath());
		}
	}

	protected void readXtendEclipseSetting(String sourceDirectory, Procedure1<String> fieldSetter) {
		if (propertiesFileLocation != null) {
			File f = new File(propertiesFileLocation);
			if (f.canRead()) {
				Properties xtendSettings = new Properties();
				try {
					xtendSettings.load(new FileInputStream(f));
					// TODO read Xtend setup to compute the properties file loc and property name
					String xtendOutputDirProp = xtendSettings.getProperty("outlet.DEFAULT_OUTPUT.directory", null);
					if (xtendOutputDirProp != null) {
						File srcDir = new File(sourceDirectory);
						getLog().debug("Source dir : " + srcDir.getPath() + " exists " + srcDir.exists());
						if (srcDir.exists() && srcDir.getParent() != null) {
							String path = new File(srcDir.getParent(), xtendOutputDirProp).getPath();
							getLog().debug("Applying Xtend property: " + xtendOutputDirProp);
							fieldSetter.apply(path);
						}
					}
				} catch (FileNotFoundException e) {
					getLog().warn(e);
				} catch (IOException e) {
					getLog().warn(e);
				}
			} else {
				getLog().info(
						"Can't find Xtend properties under " + propertiesFileLocation + ", maven defaults are used.");
			}
		}
	}

	protected String resolveToBaseDir(final String directory) throws MojoExecutionException {
		File outDir = new File(directory);
		if (!outDir.isAbsolute()) {
			outDir = new File(project.getBasedir(), directory);
		}
		return outDir.getAbsolutePath();
	}
}

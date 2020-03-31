package org.gradle.bot.plugin;

import com.sun.codemodel.JPackage;
import com.sun.codemodel.JType;
import org.gradle.api.DefaultTask;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.tasks.TaskAction;
import org.gradle.api.tasks.TaskProvider;
import org.gradle.api.tasks.compile.AbstractCompile;
import org.jsonschema2pojo.DefaultGenerationConfig;
import org.jsonschema2pojo.GenerationConfig;
import org.jsonschema2pojo.Jsonschema2Pojo;
import org.jsonschema2pojo.SourceType;
import org.jsonschema2pojo.rules.Rule;
import org.jsonschema2pojo.rules.RuleFactory;
import org.jsonschema2pojo.util.ParcelableHelper;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;

// This is a workaround for https://github.com/joelittlejohn/jsonschema2pojo/pull/1008
public class Json2JavaPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {
        TaskProvider<Json2JavaTask> task = project.getTasks()
                .register("json2Java", Json2JavaTask.class);
        project.getTasks().withType(AbstractCompile.class)
                .all(it -> it.dependsOn(task));
    }

    public static class MyRuleFactory extends RuleFactory {
        public Rule<JPackage, JType> getObjectRule() {
            return new MyObjectRule(this, new ParcelableHelper(), getReflectionHelper());
        }
    }

    public static class Json2JavaTask extends DefaultTask {
        @TaskAction
        public void run() {
            File sourceDir = (File) getProject().getExtensions().getExtraProperties().get("json2JavaSourceDir");
            File targetDir = (File) getProject().getExtensions().getExtraProperties().get("json2JavaTargetDir");
            String targetPackage = (String) getProject().getExtensions().getExtraProperties().get("json2JavaTargetPackage");

            Stream.of(sourceDir.listFiles())
                    .filter(it -> it.getName().endsWith(".json"))
                    .forEach(it -> generate(it, targetDir, targetPackage));
        }

        private void generate(File srcJson, File targetDir, String targetPackage) {
            GenerationConfig config = new DefaultGenerationConfig() {
                @Override
                public Class<? extends RuleFactory> getCustomRuleFactory() {
                    return MyRuleFactory.class;
                }

                @Override
                public File getTargetDirectory() {
                    return targetDir;
                }

                @Override
                public String getTargetPackage() {
                    return targetPackage;
                }

                @Override
                public SourceType getSourceType() {
                    return SourceType.JSON;
                }

                @Override
                public boolean isIncludeConstructors() {
                    return true;
                }

                @Override
                public boolean isUseLongIntegers() {
                    return true;
                }

                @Override
                public Iterator<URL> getSource() {
                    try {
                        return Arrays.asList(srcJson.toURI().toURL()).iterator();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                }
            };

            try {
                Jsonschema2Pojo.generate(config, null);
                makeAllEventsImplementGitHubEventInterface(srcJson, targetDir, targetPackage);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        private void makeAllEventsImplementGitHubEventInterface(File srcJson, File targetDir, String targetPackage) throws IOException {
            // Abc.json -> Abc
            String baseName = srcJson.getName().substring(0, srcJson.getName().length() - 5);
            if (baseName.endsWith("GitHubEvent")) {
                File generatedJava = new File(targetDir, targetPackage.replace('.', '/') + "/" + baseName + ".java");
                String generatedJavaContent = new String(Files.readAllBytes(generatedJava.toPath()));
                generatedJavaContent = generatedJavaContent.replace("class " + baseName, "class " + baseName + " implements GitHubEvent");
                Files.write(generatedJava.toPath(), generatedJavaContent.getBytes());
            }
        }
    }
}
